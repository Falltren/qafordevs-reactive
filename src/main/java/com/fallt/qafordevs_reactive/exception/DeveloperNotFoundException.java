package com.fallt.qafordevs_reactive.exception;

public class DeveloperNotFoundException extends ApiException {

    public DeveloperNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
