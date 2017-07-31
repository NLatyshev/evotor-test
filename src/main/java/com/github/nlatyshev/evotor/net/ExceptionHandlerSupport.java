package com.github.nlatyshev.evotor.net;

import com.github.nlatyshev.evotor.net.model.Response;

import java.util.Collections;
import java.util.function.Function;

public class ExceptionHandlerSupport<T extends Exception> implements ExceptionHandler<T> {
    private Class<T> clazz;
    private Function<T, String> toErrorCode;

    public ExceptionHandlerSupport(Class<T> clazz, Function<T, String> toErrorCode) {
        this.clazz = clazz;
        this.toErrorCode = toErrorCode;
    }

    @Override
    public Class<T> getExceptionClass() {
        return clazz;
    }

    @Override
    public Response handle(T exception) {
        return new Response(toErrorCode.apply(exception), Collections.emptyMap());
    }
}
