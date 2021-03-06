package com.airbus_cyber_security.graylog.config.rest;

public class GLPIAuthResponse {

    private int statusCode;

    private String message;

    public GLPIAuthResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
