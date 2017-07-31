package com.github.nlatyshev.evotor.net;

import com.github.nlatyshev.evotor.annotation.EvotorMethod;
import com.github.nlatyshev.evotor.annotation.EvotorParameter;
import com.github.nlatyshev.evotor.net.mapper.TypeMapper;
import com.github.nlatyshev.evotor.net.model.Request;
import com.github.nlatyshev.evotor.net.model.Response;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class EvotorDispatcher {
    private Map<String, RequestHandler> handlers = new HashMap<>();

    /**
     * it is not good to do initialization inside constructor
     */
    public EvotorDispatcher(List<TypeMapper<?>> typeMappers, List<Object> controllers) {
        init(typeMappers, controllers);
    }

    public Response handle(Request request) throws Exception {
        RequestHandler handler = handlers.get(request.getType());
        if (handler != null) {
            return new Response("0", handler.handle(request.getParams()));
        }
        throw new IllegalArgumentException("There is no handler for " + request.getType());
    }

    private void init(List<TypeMapper<?>> typeMappers, List<Object> controllers) {
        Map<Class<?>, TypeMapper<?>> mappers = new HashMap<>();
        typeMappers.forEach(mapper -> mappers.put(mapper.getType(), mapper));

        controllers.forEach(controller -> Stream.of(controller.getClass().getDeclaredMethods())
                .forEach(method -> {
                    EvotorMethod evotorMethod = method.getDeclaredAnnotation(EvotorMethod.class);
                    if (evotorMethod != null) {
                        Map<String, TypeMapper<?>> paramMappers = new HashMap<>();
                        Stream.of(method.getParameters()).forEach(parameter ->
                                paramMappers.put(getParameterName(parameter), getParameterMapper(mappers, parameter)));

                        handlers.put(evotorMethod.value(),
                                new RequestHandler(controller, method, paramMappers,  getOutTypeMapper(mappers, method, evotorMethod)));
                    }
                }));
    }

    @SuppressWarnings("unchecked")
    private TypeMapper<Object> getOutTypeMapper(Map<Class<?>, TypeMapper<?>> mappers, Method method, EvotorMethod evotorMethod) {
        TypeMapper responseMapper = null;
        if (!evotorMethod.out().equals("none")) {
            responseMapper = mappers.get(method.getReturnType());
            if (responseMapper == null) {
                throw new RuntimeException("There is no mapper for return type of method" + method);
            }
        }
        return responseMapper;
    }

    private String getParameterName(Parameter parameter) {
        EvotorParameter evotorParameter = parameter.getAnnotation(EvotorParameter.class);
        if (evotorParameter != null) {
            return evotorParameter.value();
        }
        throw new RuntimeException("Missed parameter annotation @EvotorParameter: " + parameter);
    }

    private TypeMapper<?> getParameterMapper(Map<Class<?>, TypeMapper<?>> mappers, Parameter parameter) {
        TypeMapper mapper = mappers.get(parameter.getType());
        if (mapper != null) {
            return mapper;
        } else {
            throw new RuntimeException("There is no mapper for " + parameter);
        }
    }

}
