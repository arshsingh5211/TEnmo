package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDAO {
    Account getAccount(int id);
    Account getAccount(String username); //left both getAccounts as not sure which we want to use yet
    BigDecimal getBalance(int id);
    void addToBalance(Account account, BigDecimal amountToAdd);
    void subtractFromBalance(Account account, BigDecimal amountToSubtract);
    void deleteAccount(int id); // eventually change all IDs to long
}
