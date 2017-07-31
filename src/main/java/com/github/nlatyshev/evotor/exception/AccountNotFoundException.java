package com.github.nlatyshev.evotor.exception;

public class AccountNotFoundException extends EvotorException {
    public AccountNotFoundException(String message) {
        super(message, "3");
    }
}
