package com.github.nlatyshev.evotor.net;

import com.github.nlatyshev.evotor.net.model.Response;

public interface ExceptionHandler<T extends Exception> {

    Class<T> getExceptionClass();

    Response handle(T exception);

}
