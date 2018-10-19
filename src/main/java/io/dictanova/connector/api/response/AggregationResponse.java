package io.dictanova.connector.api.response;

import java.util.List;

public class AggregationResponse {

    private String field;
    private String type;
    private List<PeriodResponse> periods;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PeriodResponse> getPeriods() {
        return periods;
    }

    public void setPeriods(List<PeriodResponse> periods) {
        this.periods = periods;
    }
}
