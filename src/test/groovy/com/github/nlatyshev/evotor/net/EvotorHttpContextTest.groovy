package com.github.nlatyshev.evotor.net

import com.github.nlatyshev.evotor.exception.AccountNotFoundException
import com.github.nlatyshev.evotor.exception.EvotorException
import com.github.nlatyshev.evotor.net.model.Request
import com.github.nlatyshev.evotor.net.model.Response
import com.sun.net.httpserver.HttpExchange
import spock.lang.Specification


class EvotorHttpContextTest extends Specification {
    def dispatcher = Mock(EvotorDispatcher)
    def parser = Mock(RequestParser)
    def serializer = Mock(ResponseSerializer)
    ExceptionHandler<EvotorException> evotorExceptionHandler = Mock(ExceptionHandler)
    ExceptionHandler<AccountNotFoundException> accountNotFoundExceptionHandler = Mock(ExceptionHandler)
    def ctx = new EvotorHttpContext(dispatcher, parser, serializer, [evotorExceptionHandler, accountNotFoundExceptionHandler])

    def setup() {
        evotorExceptionHandler.exceptionClass >> EvotorException
        evotorExceptionHandler.handle(*_) >> new Response('1', [:])
        accountNotFoundExceptionHandler.exceptionClass >> AccountNotFoundException
        accountNotFoundExceptionHandler.handle(*_) >> new Response('3', [:])
    }

    def "Handle request, provide response"() {
        setup:
            def exchange = Mock(HttpExchange)
            def is = new ByteArrayInputStream('request'.bytes)
            exchange.getRequestBody() >> is
            def out = new ByteArrayOutputStream()
            exchange.getResponseBody() >> out

            parser.parse(is) >> new Request('t1', [:])
            dispatcher.handle(new Request('t1', [:])) >> new Response('0', [:])
            serializer.serialize(new Response('0', [:])) >> 'result'
        when:
            ctx.handle(exchange)
        then:
            out.toString() == 'result'
    }

    def "Use default exception handler if cannot find suitable one"() {
        setup:
            def exchange = Mock(HttpExchange)
            def is = new ByteArrayInputStream('request'.bytes)
            exchange.getRequestBody() >> is
            def out = new ByteArrayOutputStream()
            exchange.getResponseBody() >> out

            parser.parse(is) >> {throw new IllegalArgumentException()}
            serializer.serialize(new Response('2', [:])) >> '2'
        when:
            ctx.handle(exchange)
        then:
            out.toString() == '2'
    }

    def "Use most suitable exception handler"() {
        setup:
            def exchange = Mock(HttpExchange)
            def is = new ByteArrayInputStream('request'.bytes)
            exchange.getRequestBody() >> is
            def out = new ByteArrayOutputStream()
            exchange.getResponseBody() >> out

            parser.parse(is) >> new Request('t1', [:])
            dispatcher.handle(new Request('t1', [:])) >> {throw new AccountNotFoundException('')}
            serializer.serialize(new Response('1', [:])) >> '1'
        when:
            ctx.handle(exchange)
        then:
            out.toString() == '1'
    }
}
