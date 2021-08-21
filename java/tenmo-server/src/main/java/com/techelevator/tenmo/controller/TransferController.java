package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {
    @Autowired
    TransferDAO transferDAO;
    @Autowired
    UserDao userDao;

    public TransferController(TransferDAO transferDAO, UserDao userDao) {
        this.transferDAO = transferDAO;
        this.userDao = userDao;
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(path = "all_transfers", method = RequestMethod.GET)
    public List<Transfers> getAllTransfers() {
        return transferDAO.getTransferList();
    }

    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "account/{accountFrom}/transfers/{accountTo}", method = RequestMethod.POST)
    public void sendTransfer(@Valid @PathVariable int accountFrom, @PathVariable int accountTo, @RequestBody Transfers transfers) {
        transferDAO.sendTransfer(accountFrom, accountTo, transfers.getAmount(), transfers, transfers.getTransferStatusId(), transfers.getTransferTypeId());
    }


}
