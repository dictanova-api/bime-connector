package io.dictanova.connector.api.request;

import java.util.List;

public class AggregationRequest {

    private String field;
    private String type;
    private List<Dimension> dimensions;

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

    public List<Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

}
