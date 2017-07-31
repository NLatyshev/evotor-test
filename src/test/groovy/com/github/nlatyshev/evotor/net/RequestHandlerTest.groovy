package com.github.nlatyshev.evotor.net

import com.github.nlatyshev.evotor.annotation.EvotorMethod
import com.github.nlatyshev.evotor.annotation.EvotorParameter
import com.github.nlatyshev.evotor.net.mapper.TypeMapper
import spock.lang.Specification

import java.util.function.Function


class RequestHandlerTest extends Specification {
    Function<Object, Double> backend = Mock(Function)
    def stringMapper = Mock(TypeMapper)
    def longMapper = Mock(TypeMapper)
    def doubleMapper = Mock(TypeMapper)
    def voidHandler = new RequestHandler(new Controller(), Controller.declaredMethods.find {it.name == 'testVoid'},
            [p1 : stringMapper, p2: longMapper], null)

    def returnHandler = new RequestHandler(new Controller(), Controller.declaredMethods.find {it.name == 'testReturn'},
            [p1 : stringMapper, p2: longMapper], doubleMapper)

    def 'Parse parameters and run handling' () {
        setup:
            stringMapper.parse('p1') >> 'p1'
            longMapper.parse('2') >> 2L
        when:
            def res = voidHandler.handle([p1 : 'p1', p2: '2'])
        then:
            res == [:]
            1* backend.apply(['p1', 2L])
    }

    def 'Parse parameters and run handling and serialize return value' () {
        setup:
            stringMapper.parse('p1') >> 'p1'
            longMapper.parse('2') >> 2L
            backend.apply(['p1', 2L]) >> 3.3
            doubleMapper.toStringRepresentation(3.3) >> '3.3'
        when:
            def res = returnHandler.handle([p1 : 'p1', p2: '2'])
        then:
            res == [out : '3.3']
    }

    def 'Throw exception if at least one parameter is absent' () {
        when:
            voidHandler.handle([p1 : 'p1'])
        then:
            thrown(IllegalArgumentException)
    }

    def class Controller {
        @EvotorMethod('void')
        void testVoid(@EvotorParameter('p1') String p1,@EvotorParameter('p2') long p2) {
            backend.apply([p1, p2])
        }

        @EvotorMethod(value = 'return', out = 'out')
        double testReturn(@EvotorParameter('p1') String p1,@EvotorParameter('p2') long p2) {
            return backend.apply([p1, p2])
        }
    }

}
