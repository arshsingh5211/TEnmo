package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {
    Account getAccountByUserId (long id);
    Account getAccountByAccountId(long id);
//    Account getAccount(String username); //left both getAccounts as not sure which we want to use yet
    List<Account> getAllAccounts();
    BigDecimal getBalance(long id);
    BigDecimal addToBalance(long accountId, BigDecimal amountToAdd);
    BigDecimal subtractFromBalance(long accountId, BigDecimal amountToSubtract);
    void deleteAccount(long id);
}
