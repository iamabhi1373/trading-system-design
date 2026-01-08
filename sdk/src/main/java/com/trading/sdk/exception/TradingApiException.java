package com.trading.sdk.exception;

public class TradingApiException extends Exception {
    private final int statusCode;
    private final String errorMessage;

    public TradingApiException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
