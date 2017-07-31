package com.github.nlatyshev.evotor.net;

import com.github.nlatyshev.evotor.net.model.Request;
import com.github.nlatyshev.evotor.net.model.Response;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class EvotorHttpContext implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(EvotorHttpContext.class);
    private EvotorDispatcher dispatcher;
    private RequestParser requestParser;
    private ResponseSerializer responseSerializer;
    private List<ExceptionHandler<?>> exceptionHandlers;

    private ExceptionHandler<Exception> defaultExceptionHandler =
            new ExceptionHandlerSupport<>(Exception.class, e -> "2");

    public EvotorHttpContext(EvotorDispatcher dispatcher, RequestParser requestParser,
                             ResponseSerializer responseSerializer, List<ExceptionHandler<? extends Exception>> exceptionHandlers) {
        this.dispatcher = dispatcher;
        this.requestParser = requestParser;
        this.responseSerializer = responseSerializer;
        this.exceptionHandlers = exceptionHandlers;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        Request request = null;
        try {
            request = requestParser.parse(httpExchange.getRequestBody());
            Response response = dispatcher.handle(request);
            writeResponse(response, httpExchange);
        } catch (Exception e) {
            log.error("Cannot handle request " + request, e);
            Response errorResponse = getExceptionHandler(e).handle(e);
            writeResponse(errorResponse, httpExchange);
        } finally {
            closeQuietly(httpExchange.getResponseBody());
        }
    }

    private void writeResponse(Response response, HttpExchange httpExchange){
        try {
            byte[] bytes = responseSerializer.serialize(response).getBytes();
            httpExchange.sendResponseHeaders(200,bytes.length );
            httpExchange.getResponseBody().write(bytes);
        } catch (IOException e) {
            log.error("Cannot write response");
        }
    }

    private void closeQuietly(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            log.warn("Cannot close output stream");
        }
    }

    @SuppressWarnings("unchecked")
    private ExceptionHandler<Exception> getExceptionHandler(Exception e) {
        for (ExceptionHandler<?> handler : exceptionHandlers) {
            if (handler.getExceptionClass().isInstance(e)) {
                return (ExceptionHandler<Exception>) handler;
            }
        }
        return defaultExceptionHandler;
    }

}
