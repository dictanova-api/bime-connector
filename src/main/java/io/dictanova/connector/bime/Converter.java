package io.dictanova.connector.bime;

import io.dictanova.connector.api.request.AggregationRequest;
import io.dictanova.connector.api.response.AggregationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter {


    public static List<Map<String, Object>> toBime(AggregationRequest request, AggregationResponse response) {


        List<Map<String, Object>> bimeResponse = new ArrayList<>();

        response.getPeriods().forEach(period -> {

            period.getValues().forEach(value -> {

                Map<String, Object> item = new HashMap<>();

                for (int i = 0; i < request.getDimensions().size(); i++) {
                    item.put(request.getDimensions().get(i).getField(), value.getDimensions().get(i));
                }
                item.put("from", period.getPeriod().getFrom());
                item.put("to", period.getPeriod().getTo());
                item.put("value", value.getValue());
                item.put("volume", value.getVolume());

                bimeResponse.add(item);

            });

        });


        return bimeResponse;

    }

}


