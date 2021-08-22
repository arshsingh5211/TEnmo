package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
    Transfers getTransferById(long transferId);
    String sendTransfer(long accountFrom, long accountTo, BigDecimal amount);
    List<Transfers> getTransferList(long userId);
    String getTransferDetails(long transferId);
    String updateTransferRequest(Transfers transfer, long statusId);
    List<Transfers> searchAllTransfersById(); // current user plus whatever id we pass in (interactions)
}
