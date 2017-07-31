package com.github.nlatyshev.evotor.net

import com.github.nlatyshev.evotor.net.model.Response
import spock.lang.Specification


class ResponseSerializerTest extends Specification {
    def serializer = new ResponseSerializer()
    def "serialize response"() {
        expect:
            serializer.serialize(new Response("0", [balance : "100.00"])) == "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                                                                            "<response>" +
                                                                                "<response-code>0</response-code>" +
                                                                                "<extra name=\"balance\">100.00</extra>" +
                                                                            "</response>";
    }
}
