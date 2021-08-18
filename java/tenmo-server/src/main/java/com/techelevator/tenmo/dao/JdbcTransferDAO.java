package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.math.BigDecimal;

public class JdbcTransferDAO implements TransferDAO {
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDAO(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public BigDecimal getTransferAmount(int transferId) {
        BigDecimal amount = null;
        String query = "SELECT amount FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, transferId);
        while (results.next()) {
            amount = results.getBigDecimal("amount");
        }
        return amount;
    }

    @Override
    public Account getAccountFrom() {
        return null;
    }

    @Override
    public Account getAccountTo() {
        return null;
    }
}
