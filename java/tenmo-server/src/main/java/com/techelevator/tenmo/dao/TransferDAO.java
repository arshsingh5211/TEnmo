package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;

public interface TransferDAO {
    BigDecimal getTransferAmount(int transferId);
    Account getAccountFrom();
    Account getAccountTo();
}
