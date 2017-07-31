package com.github.nlatyshev.evotor.exception;

public class AccountAlreadyExists extends EvotorException {
    public AccountAlreadyExists(String message) {
        super(message, "1");
    }
}
