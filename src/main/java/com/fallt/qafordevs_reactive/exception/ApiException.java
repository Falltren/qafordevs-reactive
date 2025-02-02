package com.fallt.qafordevs_reactive.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    protected final String errorCode;

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
