package com.github.nlatyshev.evotor.exception;

public abstract class EvotorException extends Exception {
    private String code;

    public EvotorException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
