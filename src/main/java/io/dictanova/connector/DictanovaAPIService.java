package io.dictanova.connector;

import io.dictanova.connector.api.request.AggregationRequest;
import io.dictanova.connector.api.request.AuthenticationRequest;
import io.dictanova.connector.api.response.AggregationResponse;
import io.dictanova.connector.api.response.AuthenticationResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface DictanovaAPIService {

    @Headers("Content-Type: application/json")
    @POST("token")
    Call<AuthenticationResponse> authenticate(@Body AuthenticationRequest request);

    @Headers("Content-Type: application/json")
    @POST("aggregation/datasets/{datasetId}/documents")
    Call<AggregationResponse> aggregate(@HeaderMap Map<String, String> headers, @Path("datasetId") String datasetId, @Body AggregationRequest request);

}
