package com.github.nlatyshev.evotor.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    final private AccountCredentials credentials;
    final private BigDecimal balance;

    public Account(AccountCredentials credentials, BigDecimal balance) {
        this.credentials = credentials;
        this.balance = balance;
    }

    public Account(AccountCredentials credentials) {
        this(credentials, new BigDecimal(0));
    }

    public AccountCredentials getCredentials() {
        return credentials;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(credentials, account.credentials) &&
                Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(credentials, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "credentials=" + credentials +
                ", balance=" + balance +
                '}';
    }
}
