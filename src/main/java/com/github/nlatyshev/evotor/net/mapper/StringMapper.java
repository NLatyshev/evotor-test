package com.github.nlatyshev.evotor.net.mapper;

public class StringMapper implements TypeMapper<String> {

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String parse(String params) {
        return params;
    }

    @Override
    public String toStringRepresentation(String value) {
        return value;
    }
}
