package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO {
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDAO(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Transfer getTransfer(long transferId) {
        Transfer transfer = new Transfer();
        String query = "SELECT * FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, transferId);
        while (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        String query = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?)";
        Long newTransferId = jdbcTemplate.queryForObject(query, Long.class, transfer.getTransferTypeId(),
                transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return getTransfer(newTransferId);

    }

    @Override
    public void createTransferStatus(long id, String status) {
        String query = "INSERT INTO transfer_statuses(transfer_statuses_id, transfer_statuses_desc)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(query, id, status);
    }

    @Override
    public List<User> getUserList() {
        return null;
    }

    @Override
    public List<Transfer> getTransferList() {
        return null;
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
    public List<Transfer> listAllTransfersSent() {
        return null;
    }

    @Override
    public List<Transfer> searchAllTransfersById() {
        return null;
    }

    @Override
    public List<Transfer> listAllTransfersReceived() {
        return null;
    }



/*    @Override
    public BigDecimal getTransferAmount(int transferId) {
        BigDecimal amount = null;
        String query = "SELECT amount FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, transferId);
        while (results.next()) {
            amount = results.getBigDecimal("amount");
        }
        return amount;
    }*/

    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getLong("transfer_id"));
        transfer.setTransferTypeId(rowSet.getLong("transfer_type_id"));
        transfer.setTransferStatusId(rowSet.getLong("transfer_status_id"));
        transfer.setAccountFrom(rowSet.getLong("account_from"));
        transfer.setAccountTo(rowSet.getLong("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));

        return transfer;
    }
}
