package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDAO {
    //Account getAccount(int id);
    BigDecimal getBalance(int id);
}
