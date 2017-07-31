package com.github.nlatyshev.evotor.net.mapper;

import java.math.BigDecimal;

public class BigDecimalMapper implements TypeMapper<BigDecimal> {
    @Override
    public Class<BigDecimal> getType() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal parse(String params) {
        return new BigDecimal(params);
    }

    @Override
    public String toStringRepresentation(BigDecimal value) {
        return value.toString();
    }
}
