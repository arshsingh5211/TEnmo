package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    @Autowired
    AccountDAO accountDAO;
    @Autowired
    UserDao userDao;
    @Autowired
    TransferDAO transferDAO;

    public TenmoController(AccountDAO accountDAO, UserDao userDao, TransferDAO transferDAO) {
        this.accountDAO = accountDAO;
        this.userDao = userDao;
        this.transferDAO = transferDAO;
    }

    @PreAuthorize("permitAll()")  // just bypassed authorization stuff for now ***FIX LATER
    @RequestMapping(path = "balance/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance (@PathVariable int id) {
        //System.out.println(principal.getName());
        return accountDAO.getBalance(id);
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(path = "account/transfer/{id}", method = RequestMethod.GET)
    public Transfers getSpecificTransfer (@PathVariable long id) {
        return transferDAO.getTransferById(id);
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(path = "account/{id}/transfers", method = RequestMethod.GET)
    public List<Transfers> getAllTransfers (@PathVariable int id) {
        return transferDAO.listAllMyTransfers();
    }

    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "transfers", method = RequestMethod.POST)
    public Transfers sendTransfer(@Valid @RequestBody Transfers transfers, @PathVariable long typeId) {
        return transferDAO.createTransfer(transfers, 2); // sent request pending
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

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "transfer_statuses", method = RequestMethod.POST)
    public void createTransferStatus(@RequestBody long id, String status) {
        transferDAO.createTransferStatus(id, status);
    }

    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "account/{accountFrom}/transfers/{accountTo}", method = RequestMethod.POST)
    public void sendTransfer(@Valid @PathVariable int accountFrom, @PathVariable int accountTo, @RequestBody Transfers transfers) {
        transferDAO.sendTransfer(accountFrom, accountTo, new BigDecimal("50.00"), transfers);
    }
}
