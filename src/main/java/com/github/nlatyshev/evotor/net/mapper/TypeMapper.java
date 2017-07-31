package com.github.nlatyshev.evotor.net.mapper;

/**
 * Simple type mapper (int, long, string)
 */
public interface TypeMapper<T> {
    Class<T> getType();

    T parse(String params);

    String toStringRepresentation(T value);
}
