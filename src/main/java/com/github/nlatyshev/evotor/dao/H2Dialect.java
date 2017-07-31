package com.github.nlatyshev.evotor.dao;

public class H2Dialect implements SqlDialect {
    @Override
    public int duplicateKeyErrorCode() {
        return 23505;
    }
}
