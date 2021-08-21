package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JDBCAccountDAO implements AccountDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCAccountDAO(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public BigDecimal getBalance (long id) {
        BigDecimal balance = null;
        String query = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, id);
        if ((results.next())) {
            balance = results.getBigDecimal("balance");
        }
        return balance;
    }

    @Override
    public Account getAccount (long id) {
        Account account = null;
        String query = "SELECT * FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, id);
        if ((results.next())) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public BigDecimal addToBalance(long accountId, BigDecimal amountToAdd) {
        Account account = getAccount(accountId);
        BigDecimal newBalance = account.getBalance().add(amountToAdd);
        String query = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(query, newBalance, accountId);
        return account.getBalance();
    }

    @Override
    public BigDecimal subtractFromBalance(long accountId, BigDecimal amountToSubtract) {
        Account account = getAccount(accountId);
        BigDecimal newBalance = account.getBalance().subtract(amountToSubtract);
        String query = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(query, newBalance, accountId);
        return account.getBalance();
    }
    // ********should we combine these into one updateBalance()?*************


    @Override
    public void deleteAccount(long id) {
        String query = "DELETE FROM accounts WHERE account_id = ?";
        jdbcTemplate.update(query, id);
    }

   @Override
    public Account getAccount(String username) { // or we can use userId too (probably better?)
        Account account = new Account();
        String query = "SELECT * FROM accounts WHERE (SELECT user_id FROM accounts " +
                "JOIN users USING (user_id) WHERE userName = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, username);
        while ((results.next())) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        String query = "SELECT * FROM accounts";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query);
        while (results.next()) {
            accountList.add(mapRowToAccount(results));
        }
        return accountList;
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getLong("account_id"));
        account.setUserId(rowSet.getLong("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}
