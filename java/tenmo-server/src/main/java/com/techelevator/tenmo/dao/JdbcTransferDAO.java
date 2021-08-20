package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private TransferDAO transferDAO;

    /*public JdbcTransferDAO(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }*/

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
    public Transfers createTransfer(Transfers transfers, int typeId) {
        String query = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
        Long newTransferId = jdbcTemplate.queryForObject(query, Long.class, typeId, 3,
                transfers.getAccountFrom(), transfers.getAccountTo(), new BigDecimal("999.99"));
        return getTransferById(newTransferId);
    }

    @Override
    public String sendTransfer(int accountFrom, int accountTo, BigDecimal amount, Transfers transfers) {
        BigDecimal fromAmount = accountDAO.getBalance(accountFrom);
        BigDecimal toAmount = accountDAO.getBalance(accountTo);
        if (accountFrom == accountTo) return "You cannot send a transfer to yourself!";
        if (fromAmount.compareTo(amount) == 1 && amount.compareTo(new BigDecimal("0.00")) == -1) {
            return "Balance insufficient! Please add more TEbucks to your account or change transfer amount.";
        }
        else {
            String query = "BEGIN TRANSACTION; " +
                                "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to) " +
                                "VALUES (DEFAULT, ?, ?, ?, ?); " +
                                "UPDATE accounts SET balance = balance - ? WHERE user_id = ? " +
                                "UPDATE accounts SET balance = balance + ? WHERE user_id = ?; " +
                            "COMMIT";
            jdbcTemplate.update(query, accountFrom, accountTo, amount);
            accountDAO.addToBalance(accountDAO.getAccount(accountTo), amount);
            accountDAO.subtractFromBalance(accountDAO.getAccount(accountFrom), amount);
            return "Transfer successful!"; // show current user's new balance
        }
    }

    @Override
    public void createTransferStatus(long id, String status) {
        String query = "INSERT INTO transfer_statuses(transfer_statuses_id, transfer_statuses_desc)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(query, id, status);
    }

    @Override
    public List<Transfers> getTransferList() {
        return null;
    }

    @Override
    public String getTransferStatus() {
        return null;
    }

/*    @Override
    public String getTransferStatus(String statusString) {
        return null;
    }*/

    @Override
    public String getTransferDetails() {
        return null;
    }

    @Override
    public List<Transfers> listAllMyTransfers() {
        /*List<Transfers> transferList = new ArrayList<>();
        String query = "SELECT */
        return null;
    }

    @Override
    public List<Transfers> searchAllTransfersById() {
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
