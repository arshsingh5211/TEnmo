package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
    Transfer getTransfer(long transferId);
    Transfer createTransfer(Transfer transfer);
    List<User> getUserList();
    List<Transfer> getTransferList();
    String getTransferStatus();
    String getTransferDetails();
    List<Transfer> listAllTransfersSent();
    List<Transfer> searchAllTransfersById(); // current user plus whatever id we pass in (interactions)
    List<Transfer> listAllTransfersReceived();
}
