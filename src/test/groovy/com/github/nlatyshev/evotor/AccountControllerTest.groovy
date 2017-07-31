package com.github.nlatyshev.evotor

import com.github.nlatyshev.evotor.dao.AccountDao
import com.github.nlatyshev.evotor.exception.AccountAlreadyExists
import com.github.nlatyshev.evotor.exception.AccountNotFoundException
import com.github.nlatyshev.evotor.exception.IncorrectPasswordException
import com.github.nlatyshev.evotor.model.Account
import com.github.nlatyshev.evotor.model.AccountCredentials
import spock.lang.Specification


class AccountControllerTest extends Specification {
    def dao = Mock(AccountDao)
    def controller = new AccountController(dao)

    def 'Throw exception if has null login or password'() {
        when:
            controller.createAccount(null, 'password')
        then:
            thrown(IllegalArgumentException)
        when:
            controller.createAccount('login', null)
        then:
            thrown(IllegalArgumentException)
    }

    def "Don't catch AccountAlreadyExists exception"() {
        when:
            dao.persist(new Account(new AccountCredentials('login', 'password'))) >> {throw new AccountAlreadyExists('')}
            controller.createAccount('login', 'password')
        then:
            thrown(AccountAlreadyExists)
    }

    def 'Persist account' () {
        when:
            controller.createAccount('login', 'password')
        then:
            1 * dao.persist(new Account(new AccountCredentials('login', 'password')))
    }

    def 'Get account balance' () {
        setup:
            dao.findAccountByLogin('login') >> new Account(new AccountCredentials('login', 'password'), new BigDecimal(100))
        expect:
            controller.getBalance('login', 'password') == new BigDecimal(100)
    }

    def 'Throw exception if account not found' () {
        setup:
            dao.findAccountByLogin('login') >> null
        when:
            controller.getBalance('login', 'password')
        then:
            thrown(AccountNotFoundException)
    }
    def 'Throw exception if password is incorrect' () {
        setup:
            dao.findAccountByLogin('login') >> new Account(new AccountCredentials('login', 'password1'), new BigDecimal(100))
        when:
            controller.getBalance('login', 'password')
        then:
            thrown(IncorrectPasswordException)
    }


}
