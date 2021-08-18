package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;
import org.springframework.jdbc.core.JdbcTemplate;
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
    public Balance getBalance(String user) { // or we can use userId too (probably better?)
        Balance balance = new Balance();

        //queryforrowset and mapToRowSet stuff
        balance.setBalance(new BigDecimal("200")); // just hardcoding an example here
        return balance;
    }
}
