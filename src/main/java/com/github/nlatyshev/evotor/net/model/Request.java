package com.github.nlatyshev.evotor.net.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Request {
    private final String type;
    private final Map<String, String> params;

    public Request(String type, Map<String, String> params) {
        this.type = type;
        this.params = params != null ? new HashMap<>(params) : Collections.emptyMap();
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getParams() {
        return new HashMap<>(params);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(type, request.type) &&
                Objects.equals(params, request.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, params);
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                ", params=" + params +
                '}';
    }
}
