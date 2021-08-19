package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDAO {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public BigDecimal getBalance (int id) {
        BigDecimal balance = null;
        String query = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, id);
        if ((results.next())) {
            balance = results.getBigDecimal("balance");
        }
        return balance;
    }

    @Override
    public void addToBalance(Account account, BigDecimal amountToAdd) {
        String query = "UPDATE accounts SET account_id = ?, balance = ? WHERE account_id = ?";
        jdbcTemplate.update(query, account.getAccountId(), account.getBalance().add(amountToAdd), account.getUserId());
    }

    @Override
    public void subtractFromBalance(Account account, BigDecimal amountToSubtract) {
        String query = "UPDATE accounts SET account_id = ?, balance = ? WHERE account_id = ?";
        jdbcTemplate.update(query, account.getAccountId(), account.getBalance().subtract(amountToSubtract), account.getUserId());
    }
    // ********should we combine these into one updateBalance()?*************


    @Override
    public void deleteAccount(int id) {
        String query = "DELETE FROM accounts WHERE account_id = ?";
        jdbcTemplate.update(query, id);
    }

   /* @Override
    public Account getAccount(String user) { // or we can use userId too (probably better?)
        Account account = new Account();
        String query = "SELECT * FROM accounts WHERE (SELECT user_id FROM accounts " +
                "JOIN users USING (user_id) WHERE userName = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, user);
        while ((results.next())) {
            account = mapRowToAccount(results);
        }
        return account;
    }*/

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getLong("account_id"));
        account.setUserId(rowSet.getLong("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}
