package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferNotFoundException;
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
        String query = "SELECT t.*, u.username AS userFrom, v.username AS userTo, ts.transfer_status_desc, tt.transfer_type_desc FROM transfers t " +
                "JOIN accounts a ON t.account_from = a.account_id " +
                "JOIN accounts b ON t.account_to = b.account_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "JOIN users v ON b.user_id = v.user_id " +
                "JOIN transfer_statuses ts ON t.transfer_status_id = ts.transfer_status_id " +
                "JOIN transfer_types tt ON t.transfer_type_id = tt.transfer_type_id " +
                "WHERE t.transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, transferId);
        if (results.next()) transfers = mapRowToTransfer(results);
        else throw new TransferNotFoundException();
        return transfers;
    }

    @Override
    public String sendTransfer(long userFrom, long userTo, BigDecimal amount) {
        if (userFrom == userTo) return "You cannot send a transfer to yourself!";
        if (/*accountDAO.getBalance(userFrom).compareTo(amount) == 1 &&*/ amount.compareTo(new BigDecimal("0.00")) == 1) {
            String query = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                                "VALUES (?, ?, ?, ?, ?) ";
            jdbcTemplate.update(query, 2, 2, userFrom, userTo, amount);
            accountDAO.addToBalance(userTo, amount);
            accountDAO.subtractFromBalance(userFrom, amount);
           return "Transfer successful!"; // show current user's new balance
       }
        else return "Invalid transfer! Please add more TEbucks to your account or change transfer amount.";
    }

    @Override
    public List<Transfers> getTransferList(long userId) {
        List<Transfers> transfersList = new ArrayList<>();
        String query = "SELECT t.*, u.username AS sender, v.username AS recipient FROM transfers t " +
                "JOIN accounts a ON t.account_from = a.account_id " +
                "JOIN accounts b ON t.account_to = b.account_id " +
                "JOIN users u ON a.user_id = u.user_id " +
                "JOIN users v ON b.user_id = v.user_id " +
                "WHERE a.user_id = ? OR b.user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, userId, userId);
        while (results.next()) {
            transfersList.add(mapRowToTransfer(results));
        }
        return transfersList;
    }

    @Override
    public String getUserFrom(long accountFrom) {
        String userFrom = "";
        String query = "SELECT username FROM users " +
                "JOIN accounts ON account_id = ? " +
                "WHERE accounts.user_id = users.user_id";
        SqlRowSet result = jdbcTemplate.queryForRowSet(query, accountFrom);
        if (result.next()) {
            userFrom = result.getString("username");
        }
        return userFrom;
    }
    @Override
    public String getUserTo(long accountTo) {
        String userTo = "";
        String query = "SELECT username FROM users " +
                "JOIN accounts ON account_id = ? " +
                "WHERE accounts.user_id = users.user_id";
        SqlRowSet result = jdbcTemplate.queryForRowSet(query, accountTo);
        if (result.next()) {
            userTo = result.getString("username");
        }
        return userTo;
    }

    @Override
    public List<String> getTransferDetails(long transferId) {
        List<String> details = new ArrayList<>();
        String query = "SELECT transfer_id, u.username AS from_user, v.username AS to_user, transfer_type_desc, transfer_status_desc, amount " +
        "FROM transfer_types JOIN transfers USING (transfer_type_id) JOIN transfer_statuses USING (transfer_status_id) " +
        "JOIN accounts a ON transfers.account_from = a.account_id " +
        "JOIN accounts b ON transfers.account_to = b.account_id " +
        "JOIN users u ON a.user_id = u.user_id " +
        "JOIN users v ON b.user_id = v.user_id " +
        "WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, transferId);
        while (results.next()) {
            details.add(results.getString("transfer_id"));
            details.add(results.getString("from_user"));
            details.add(results.getString("to_user"));
            details.add(results.getString("transfer_type_desc"));
            details.add(results.getString("transfer_status_desc"));
            details.add(results.getString("amount"));
        }
        return details;
    }

    @Override
    public String updateTransferRequest(Transfers transfer, long statusId) {
        if (statusId == 3) {
            String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?;";
            jdbcTemplate.update(sql, statusId, transfer.getTransferId());
            return "Request updated!";
        }
        if (accountDAO.getBalance(transfer.getAccountFrom()).compareTo(transfer.getAmount()) != -1) {
            String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?;";
            jdbcTemplate.update(sql, statusId, transfer.getTransferId());
            accountDAO.addToBalance(transfer.getAccountTo(), transfer.getAmount());
            accountDAO.subtractFromBalance(transfer.getAccountFrom(), transfer.getAmount());
            return "Update successful!";
        }
        else return "Sorry, transfer unable to be completed at this time!";
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
