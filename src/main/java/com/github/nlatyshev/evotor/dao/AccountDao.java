package com.github.nlatyshev.evotor.dao;

import com.github.nlatyshev.evotor.exception.AccountAlreadyExists;
import com.github.nlatyshev.evotor.model.Account;
import com.github.nlatyshev.evotor.model.AccountCredentials;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * It is better to use spring jdbc (at least it has good exception hierarchy).
 * But I tried to follow the task, yes spring jdbc is not orm, I know it, but...
 */
public class AccountDao {
    private static final String INSERT_ACCOUNT = "insert into account (id, login, password, balance) values (account_sequence.nextval,?,?,?)";
    private static final String FIND_ACCOUNT_BY_LOGIN = "select login, password, balance from account where login = ?";
    private final DataSource ds;
    private final SqlDialect dialect;

    public AccountDao(DataSource ds, SqlDialect dialect) {
        this.ds = ds;
        this.dialect = dialect;
    }

    public void persist(Account account) throws AccountAlreadyExists {
        try(Connection con = ds.getConnection()) {
            PreparedStatement statement = con.prepareStatement(INSERT_ACCOUNT);
            statement.setString(1, account.getCredentials().getLogin());
            statement.setString(2, account.getCredentials().getPassword());
            statement.setBigDecimal(3, account.getBalance());
            statement.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == dialect.duplicateKeyErrorCode()) {
                throw new AccountAlreadyExists("Account already exists: " + account.getCredentials().getLogin());
            }
            throw new RuntimeException(e);
        }
    }

    public Account findAccountByLogin(String login) {
        try(Connection con = ds.getConnection()) {
            PreparedStatement statement = con.prepareStatement(FIND_ACCOUNT_BY_LOGIN);
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while (rs.next()) {
                accounts.add(new Account(new AccountCredentials(rs.getString("login"), rs.getString("password")),
                        rs.getBigDecimal("balance")));
            }
            if (accounts.size() > 1) {
                throw new IllegalStateException("Found more then one account with login " + login);
            }
            return accounts.size() == 1 ? accounts.get(0) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Cannot retrive account from DB", e);
        }
    }

}
