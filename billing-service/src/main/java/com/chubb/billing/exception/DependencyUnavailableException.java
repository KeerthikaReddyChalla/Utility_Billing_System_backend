package com.chubb.billing.exception;

public class DependencyUnavailableException extends RuntimeException {

    public DependencyUnavailableException(String message) {
        super(message);
    }
}
