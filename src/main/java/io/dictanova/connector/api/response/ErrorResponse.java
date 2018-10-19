package io.dictanova.connector.api.response;

import java.util.List;

public class ErrorResponse {

    private Boolean success;
    private List<Error> errors;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }


}
