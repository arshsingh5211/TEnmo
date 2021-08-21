package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    UserDao userDao;

    public AccountController(AccountDAO accountDAO, UserDao userDao) {
        this.accountDAO = accountDAO;
        this.userDao = userDao;
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(path = "all_accounts", method = RequestMethod.GET)
    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    @PreAuthorize("permitAll()")  // just bypassed authorization stuff for now ***FIX LATER
    @RequestMapping(path = "balance/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance (@PathVariable int id) {
        //System.out.println(principal.getName());
        return accountDAO.getBalance(id);
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(path = "users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return userDao.findAll();
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(path = "users/{username}", method = RequestMethod.GET)
    public User getUserByUsername(@PathVariable String username){
        return userDao.findByUsername(username);
    }
}
