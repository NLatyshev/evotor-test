package com.github.nlatyshev.evotor;

import com.github.nlatyshev.evotor.annotation.EvotorMethod;
import com.github.nlatyshev.evotor.annotation.EvotorParameter;
import com.github.nlatyshev.evotor.dao.AccountDao;
import com.github.nlatyshev.evotor.exception.AccountAlreadyExists;
import com.github.nlatyshev.evotor.exception.AccountNotFoundException;
import com.github.nlatyshev.evotor.exception.IncorrectPasswordException;
import com.github.nlatyshev.evotor.model.Account;
import com.github.nlatyshev.evotor.model.AccountCredentials;

import java.math.BigDecimal;

public class AccountController {
    private final AccountDao dao;

    public AccountController(AccountDao dao) {
        this.dao = dao;
    }

    @EvotorMethod("CREATE-AGT")
    public void createAccount(@EvotorParameter("login") String login,
                              @EvotorParameter("password") String password) throws AccountAlreadyExists {
        if (isSuitable(login) && isSuitable(password)) {
            dao.persist(new Account(new AccountCredentials(login, password)));
        } else {
            throw new IllegalArgumentException("login or/and password is not suitable");
        }
    }

    @EvotorMethod(value = "GET-BALANCE", out = "balance")
    public BigDecimal getBalance(@EvotorParameter("login") String login,
                                 @EvotorParameter("password") String password) throws IncorrectPasswordException, AccountNotFoundException {
        Account account = dao.findAccountByLogin(login);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + login);
        }

        if (account.getCredentials().getPassword().equals(password)) {
            return account.getBalance();
        }
        throw new IncorrectPasswordException("Incorrect password for " + login);
    }

    /**
     * Just check value is not null and not empty.
     * Real implementation should check length and others separately for password and login
     *
     * @return true if it is ok
     */
    private boolean isSuitable(String value) {
        return value != null && !value.isEmpty();
    }

}
