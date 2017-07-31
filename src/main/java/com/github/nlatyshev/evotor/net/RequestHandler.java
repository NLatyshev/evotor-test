package com.github.nlatyshev.evotor.net;

import com.github.nlatyshev.evotor.annotation.EvotorMethod;
import com.github.nlatyshev.evotor.annotation.EvotorParameter;
import com.github.nlatyshev.evotor.net.mapper.TypeMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class RequestHandler {
    private Method method;
    private Map<String, TypeMapper<?>> paramMappers;
    private TypeMapper<Object> outMapper;
    private Object controller;

    public RequestHandler(Object controller, Method method, Map<String, TypeMapper<?>> paramMappers, TypeMapper<Object> outMapper) {
        this.method = method;
        this.paramMappers = paramMappers;
        this.outMapper = outMapper;
        this.controller = controller;
    }

    public Map<String, String> handle(Map<String, String> params) throws Exception {
        try {
            Object res = method.invoke(controller, parseParams(params));
            return outMapper != null ? toResult(res) : Collections.emptyMap();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Throwable root = e.getCause();
            if (root instanceof Exception) {
                throw (Exception) root;
            }
            throw new RuntimeException(e);
        }
    }

    private Object[] parseParams(Map<String, String> params) {
        Map<String, Object> paramValues = new HashMap<>();
        paramMappers.forEach((name, mapper) -> {
            String value = params.get(name);
            if (value != null) {
                paramValues.put(name, mapper.parse(value));
            } else {
                throw new IllegalArgumentException("Required param is absent: " + name);
            }
        });

        return Stream.of(method.getParameters())
                .map(p -> p.getAnnotation(EvotorParameter.class))
                .map(p -> paramValues.get(p.value())).toArray();
    }

    private Map<String, String> toResult(Object res) {
        Map<String, String> result = new HashMap<>();
        result.put(method.getDeclaredAnnotation(EvotorMethod.class).out(), outMapper.toStringRepresentation(res));
        return result;
    }

}
