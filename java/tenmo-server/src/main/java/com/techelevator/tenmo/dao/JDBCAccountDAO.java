package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JDBCAccountDAO implements AccountDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCAccountDAO(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public BigDecimal getBalance (String user) {
        Account account = new Account();
        String query = "SELECT balance FROM accounts WHERE (SELECT user_id FROM accounts " +
                "JOIN users USING (user_id) WHERE userName = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, user);
        while ((results.next())) {
            account = mapRowToAccount(results);
        }
        return account.getBalance();
    }

    @Override
    public Account getAccount(String user) { // or we can use userId too (probably better?)
        Account account = new Account();
        String query = "SELECT * FROM accounts WHERE (SELECT user_id FROM accounts " +
                "JOIN users USING (user_id) WHERE userName = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, user);
        while ((results.next())) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getLong("account_id"));
        account.setUserId(rowSet.getLong("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}
