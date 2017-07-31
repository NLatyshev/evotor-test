package com.github.nlatyshev.evotor.net.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Response {
    private final String code;
    private final Map<String, String> result;

    public Response(String code, Map<String, String> result) {
        this.code = code;
        this.result = result != null ? new HashMap<>(result) : Collections.emptyMap();
    }

    public String getCode() {
        return code;
    }

    public Map<String, String> getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return Objects.equals(code, response.code) &&
                Objects.equals(result, response.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, result);
    }

    @Override
    public String toString() {
        return "Response{" +
                "code='" + code + '\'' +
                ", result=" + result +
                '}';
    }
}
