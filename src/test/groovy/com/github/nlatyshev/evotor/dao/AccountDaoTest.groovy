package com.github.nlatyshev.evotor.dao

import com.github.nlatyshev.evotor.exception.AccountAlreadyExists
import com.github.nlatyshev.evotor.model.Account
import com.github.nlatyshev.evotor.model.AccountCredentials
import groovy.sql.Sql
import org.h2.jdbcx.JdbcDataSource
import spock.lang.Shared
import spock.lang.Specification


class AccountDaoTest extends Specification {
    @Shared Sql sql
    @Shared AccountDao dao

    def setupSpec() {
        def ds = new JdbcDataSource()
        ds.setUrl('jdbc:h2:mem:dataSource;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false');
        ds.setUser('sa');
        ds.setPassword('');

        sql = new Sql(ds)
        dao = new AccountDao(ds, new H2Dialect())

        sql.execute(getClass().getResource('/h2-schema.sql').text)
    }


    def "Persist account"() {
        when:
            dao.persist(new Account(new AccountCredentials('login', 'password'), new BigDecimal('100.1')))
        then:
            def acc = sql.rows('select * from account')
            acc.size() == 1
            acc[0].login == 'login'
            acc[0].password == 'password'
            acc[0].balance == 100.1
    }

    def "Throw exception if account already exists"() {
        setup:
            sql.execute("insert into account values (account_sequence.nextval, 'login1', 'password', 0)")
        when:
            dao.persist(new Account(new AccountCredentials('login1', 'password'), new BigDecimal('100.1')))
        then:
            thrown(AccountAlreadyExists)
    }

    def "Find account by login"() {
        setup:
            sql.execute("insert into account values (account_sequence.nextval, 'login2', 'password2', 2)")
            sql.execute("insert into account values (account_sequence.nextval, 'login3', 'password3', 3)")
        expect:
            dao.findAccountByLogin('login2') == new Account(new AccountCredentials('login2', 'password2'), new BigDecimal(2))
    }

    def 'Return null if account not found' () {
        setup:
            sql.execute("insert into account values (account_sequence.nextval, 'login4', 'password2', 2)")
        expect:
            dao.findAccountByLogin('login5') == null
    }
}
