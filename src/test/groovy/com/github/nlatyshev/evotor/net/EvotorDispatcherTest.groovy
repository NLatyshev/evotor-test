package com.github.nlatyshev.evotor.net

import com.github.nlatyshev.evotor.AccountController
import com.github.nlatyshev.evotor.dao.AccountDao
import com.github.nlatyshev.evotor.model.Account
import com.github.nlatyshev.evotor.model.AccountCredentials
import com.github.nlatyshev.evotor.net.mapper.TypeMapper
import com.github.nlatyshev.evotor.net.model.Request
import com.github.nlatyshev.evotor.net.model.Response
import spock.lang.Specification


class EvotorDispatcherTest extends Specification {
    def stringMapper = Mock(TypeMapper)
    def bigDecimalMapper = Mock(TypeMapper)
    def dao = Mock(AccountDao)
    EvotorDispatcher dispatcher

    def setup() {
        stringMapper.type >> String
        bigDecimalMapper.type >> BigDecimal
        stringMapper.parse('login') >> 'login'
        stringMapper.parse('password') >> 'password'
        dispatcher = new EvotorDispatcher([stringMapper, bigDecimalMapper], [new AccountController(dao)])
    }

    def "Throw excretion if has no mapper for type"() {
        when:
            new EvotorDispatcher([stringMapper], [new AccountController(dao)])
        then:
            thrown(RuntimeException)
    }

    def "Throw excretion if has no handler for request"() {
        when:
            dispatcher.handle(new Request('unknown', [:]))
        then:
            thrown(IllegalArgumentException)
    }

    def "Dispatch to handler"() {
        when:
            dispatcher.handle(new Request('CREATE-AGT', [login : 'login', password: 'password']))
        then:
            1 * dao.persist(new Account(new AccountCredentials('login', 'password')))
        when:
            dao.findAccountByLogin('login') >> new Account(new AccountCredentials('login', 'password'), new BigDecimal(100))
            bigDecimalMapper.toStringRepresentation(new BigDecimal(100)) >> '100'

            def resp = dispatcher.handle(new Request('GET-BALANCE', [login : 'login', password: 'password']))
        then:
            resp == new Response("0", [balance : '100'])

    }
}
