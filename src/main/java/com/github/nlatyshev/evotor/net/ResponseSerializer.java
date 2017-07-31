package com.github.nlatyshev.evotor.net;

import com.github.nlatyshev.evotor.net.model.Response;

public class ResponseSerializer {
    private final static String RESPONSE_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<response>" +
                    "<response-code>%s</response-code>%s" +
            "</response>";
    private final static String EXTRA_TEMPLATE =
            "<extra name=\"%s\">%s</extra>";

    public String serialize(Response response) {
        StringBuilder params = new StringBuilder();
        response.getResult().forEach((name, value) -> params.append(String.format(EXTRA_TEMPLATE, name, value)));
        return String.format(RESPONSE_TEMPLATE, response.getCode(), params);
    }
}
