package com.castlabs.mp4Analyzer.model;


public class ErrorResponse {

    private String errorMessage;

    public ErrorResponse(String message) {
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
