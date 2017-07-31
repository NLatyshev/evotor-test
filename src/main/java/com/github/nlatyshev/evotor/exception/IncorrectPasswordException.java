package com.github.nlatyshev.evotor.exception;

public class IncorrectPasswordException extends EvotorException {
    public IncorrectPasswordException(String message) {
        super(message, "4");
    }
}
