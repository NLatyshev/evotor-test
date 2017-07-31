package com.github.nlatyshev.evotor.net

import com.github.nlatyshev.evotor.net.model.Request
import spock.lang.Specification


class RequestParserTest extends Specification {
    def parser = new RequestParser()

    def "Parse request"() {
        given:
            def r = '<?xml version="1.0" encoding="utf-8"?>' +
                    '<request>' +
                            '<request-type>GET-BALANCE</request-type>' +
                            '<extra name="login">login</extra>' +
                            '<extra name="password">password</extra>' +
                        '</request>'
        expect:
            parser.parse(new ByteArrayInputStream(r.getBytes())) == new Request('GET-BALANCE', [login : 'login', password : 'password'])
    }

    def "Throw exception if request has more than one 'request-type' element"() {
        given:
            def r = '<?xml version="1.0" encoding="utf-8"?>' +
                    '<request>' +
                        '<request-type>GET-BALANCE</request-type>' +
                        '<request-type>GET-BALANCE</request-type>' +
                    '</request>'
        when:
            parser.parse(new ByteArrayInputStream(r.bytes))
        then:
            thrown(IllegalArgumentException)
    }

    def "Throw exception if request has unknown element"() {
        given:
            def r = '<?xml version="1.0" encoding="utf-8"?>' +
                    '<request>' +
                        '<unknown>GET-BALANCE</unknown>' +
                    '</request>'
        when:
            parser.parse(new ByteArrayInputStream(r.bytes))
        then:
            thrown(IllegalArgumentException)
    }

    def "Throw exception if request first child has child too"() {
        given:
            def r = '<?xml version="1.0" encoding="utf-8"?>' +
                    '<request>' +
                    '<request-type>' +
                        '<request-type>GET-BALANCE</request-type>' +
                    '</request-type>' +
                    '</request>'
        when:
            parser.parse(new ByteArrayInputStream(r.bytes))
        then:
            thrown(IllegalArgumentException)
    }

    def "Throw exception if extra element has no name"() {
        given:
            def r = '<?xml version="1.0" encoding="utf-8"?>' +
                    '<request>' +
                        '<request-type>GET-BALANCE</request-type>' +
                        '<extra name="login">login</extra>' +
                        '<extra>password</extra>' +
                    '</request>'
        when:
            parser.parse(new ByteArrayInputStream(r.bytes))
        then:
            thrown(IllegalArgumentException)
    }
}
