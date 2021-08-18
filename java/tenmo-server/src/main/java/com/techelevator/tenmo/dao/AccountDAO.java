package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDAO {
    Account getAccount(String user);
    BigDecimal getBalance(String user);
}
