package com.github.ehsan1222.ca.exceptions;

public class AlertNotFoundException extends RuntimeException {

    public AlertNotFoundException() {
    }

    public AlertNotFoundException(String message) {
        super(message);
    }
}
