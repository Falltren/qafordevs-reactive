package com.fallt.qafordevs_reactive.exception;

public class DeveloperWithEmailAlreadyExistsException extends ApiException {

    public DeveloperWithEmailAlreadyExistsException(String message, String errorCode) {
        super(message, errorCode);
    }
}
