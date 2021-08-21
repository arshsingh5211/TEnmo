package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private TransferDAO transferDAO;


    @Override
    public Transfers getTransferById(long transferId) {
        Transfers transfers = new Transfers();
        String query = "SELECT * FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, transferId);
        while (results.next()) {
            transfers = mapRowToTransfer(results);
        }
        return transfers;
    }

    @Override
    public String sendTransfer(long accountFrom, long accountTo, BigDecimal amount) { // <--- null pointer exception here
        BigDecimal fromAmount = accountDAO.getBalance(accountFrom);
        if (accountFrom == accountTo) return "You cannot send a transfer to yourself!";
        if (fromAmount.compareTo(amount) == 1 && amount.compareTo(new BigDecimal("0.00")) == 1) {
            String query = "BEGIN TRANSACTION; " +
                                "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                                "VALUES (2, 2, ?, ?, ?); " +
                            "COMMIT";
            jdbcTemplate.update(query, accountFrom, accountTo, amount);
            accountDAO.addToBalance(accountTo, amount);
            accountDAO.subtractFromBalance(accountFrom, amount);
           return "Transfer successful!"; // show current user's new balance
       }
        else return "Invalid transfer! Please add more TEbucks to your account or change transfer amount.";
    }

    @Override
    public List<Transfers> getTransferList() {
        List<Transfers> transfersList = new ArrayList<>();
        String query = "SELECT * FROM transfers";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query);
        while (results.next()) {
            transfersList.add(mapRowToTransfer(results));
        }
        return transfersList;
    }

    @Override
    public String getTransferStatus() {
        return null;
    }

    @Override
    public String getTransferDetails() {
        return null;
    }

    @Override
    public List<Transfers> searchAllTransfersById() {
        return null;
    }

    private Transfers mapRowToTransfer(SqlRowSet rowSet) {
        Transfers transfers = new Transfers();
        transfers.setTransferId(rowSet.getLong("transfer_id"));
        transfers.setTransferTypeId(rowSet.getLong("transfer_type_id"));
        transfers.setTransferStatusId(rowSet.getLong("transfer_status_id"));
        transfers.setAccountFrom(rowSet.getLong("account_from"));
        transfers.setAccountTo(rowSet.getLong("account_to"));
        transfers.setAmount(rowSet.getBigDecimal("amount"));

        return transfers;
    }
}
