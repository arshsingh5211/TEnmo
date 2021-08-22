package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
    String getUserFrom(long accountFrom);
    String getUserTo(long accountTo);
    Transfers getTransferById(long transferId);
    String sendTransfer(long accountFrom, long accountTo, BigDecimal amount);
    List<Transfers> getTransferList(long userId);
    List<String> getTransferDetails(long transferId);
    String updateTransferRequest(Transfers transfer, long statusId);
}
