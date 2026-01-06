package com.chubb.meter.exception;

public class DuplicateReadingException extends RuntimeException {
    public DuplicateReadingException(String msg) {
        super(msg);
    }
}
