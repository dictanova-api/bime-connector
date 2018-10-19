package io.dictanova.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.iki.elonen.NanoHTTPD;
import io.dictanova.connector.api.request.AggregationRequest;
import io.dictanova.connector.api.request.AuthenticationRequest;
import io.dictanova.connector.api.request.Dimension;
import io.dictanova.connector.api.response.AggregationResponse;
import io.dictanova.connector.api.response.AuthenticationResponse;
import io.dictanova.connector.api.response.ErrorResponse;
import io.dictanova.connector.bime.Converter;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class BimeConnector extends NanoHTTPD {

    private DictanovaAPIService client;
    private Properties props;
    private Gson gson;

    public BimeConnector(Properties props) throws IOException {

        super(Integer.valueOf(props.getProperty("connector.port")));
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dictanova.io/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        this.client = retrofit.create(DictanovaAPIService.class);
        this.props = props;
        this.gson = builder.create();

        System.out.println("-- BIM Connector is started on port " + props.getProperty("connector.port") + " ! --");

    }

    @Override
    public NanoHTTPD.Response serve(IHTTPSession session) {

        String authorization = session.getHeaders().get("authorization");
        if (authorization == null || !authorization.equals("Bearer " + props.getProperty("connector.bearer"))) {
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, "application/json", "{ \"error\" : true }");
        }

        Optional<String> optionalToken = authenticate(client, props.getProperty("client.id"), props.getProperty("client.secret"));

        if (optionalToken.isPresent()) {

            try {

                String token = optionalToken.get();

                Map<String, String> tokenHeaders = new HashMap<>();
                tokenHeaders.put("Authorization", "Bearer " + token);

                AggregationRequest request = new AggregationRequest();
                request.setField(props.getProperty("aggregation.field"));
                request.setType(props.getProperty("aggregation.type"));

                List<String> dimensionFields = Arrays.asList(props.getProperty("aggregation.dimensions.fields").split(","));
                List<String> dimensionGroups = Arrays.asList(props.getProperty("aggregation.dimensions.groups").split(","));

                List<Dimension> dimensions = IntStream.range(0, dimensionFields.size())
                        .mapToObj(i -> {
                            Dimension d = new Dimension();
                            d.setField(dimensionFields.get(i));
                            d.setGroup(dimensionGroups.get(i));
                            return d;
                        }).collect(toList());


                request.setDimensions(dimensions);

                Call<AggregationResponse> agg = client.aggregate(tokenHeaders, props.getProperty("dataset.id"), request);

                retrofit2.Response<AggregationResponse> execute = agg.execute();

                if (execute.isSuccessful()) {

                    AggregationResponse response = execute.body();

                    List<Map<String, Object>> bimeResponse = Converter.toBime(request, response);

                    String toJson = gson.toJson(bimeResponse);

                    return newFixedLengthResponse(Response.Status.OK, "application/json", toJson);


                } else {

                    ErrorResponse errorsResponse = gson.fromJson(execute.errorBody().string(), ErrorResponse.class);
                    System.err.println("Request to Dictanova could not be proceed: " + execute.code());
                    errorsResponse.getErrors().forEach(err -> {
                        System.err.println(err.getCode() + " - " + err.getDescription());
                    });

                    return newFixedLengthResponse(Response.Status.UNAUTHORIZED, "application/json", "{ \"error\" : true }");

                }


            } catch (Exception e) {
                System.err.print("Error while executing connector : " + e.getMessage());
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{ \"error\" : true }");
            }

        } else {
            System.err.println("Unable to authenticate with clientId and clientSecret");
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{ \"error\" : true }");
        }

    }


    public static void main(String[] args) {

        System.out.println("-- Starting Dictanova BIME Connector --\n");

        if (args.length != 1) {
            System.err.println("Two arguments required : conf file path and output file path");
            System.exit(-1);
        }

        Properties props = BimeConnector.parseProperties(args[0]);

        try {
            new BimeConnector(props);
        } catch (Exception e) {
            System.err.println("-- Could not start BIME Connector -- ");
        }

    }

    private Optional<String> authenticate(DictanovaAPIService client, String clientId, String clientSecret) {

        try {

            AuthenticationRequest request = new AuthenticationRequest();
            request.setClientId(clientId);
            request.setClientSecret(clientSecret);

            Call<AuthenticationResponse> authenticate = client.authenticate(request);

            retrofit2.Response<AuthenticationResponse> execute = authenticate.execute();

            if (execute.isSuccessful()) {
                return Optional.of(execute.body().getAccessToken());
            } else {
                System.err.println("Authentication error : please check client id and client secret");
            }

        } catch (IOException e) {
            System.err.println("Unexpected error while authenticating " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();

    }

    private static Properties parseProperties(String propertiesFilePath) {

        try {
            Properties prop = new Properties();
            FileInputStream input = new FileInputStream(propertiesFilePath);
            prop.load(input);
            return prop;
        } catch (Exception e) {
            System.err.println("Unable to parse configuration file");
            System.exit(2);
            return null;
        }

    }

}

