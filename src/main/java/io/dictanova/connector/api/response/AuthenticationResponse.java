package io.dictanova.connector.api.response;

import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private Double expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Double getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Double expiresIn) {
        this.expiresIn = expiresIn;
    }
}
