package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;

public interface AccountDAO {
    Balance getBalance(String user);
}
