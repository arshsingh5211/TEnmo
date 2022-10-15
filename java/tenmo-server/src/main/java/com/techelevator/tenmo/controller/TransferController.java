package com.techelevator.tenmo.controller;

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

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "transfers/{id}/all", method = RequestMethod.GET)
    public List<Transfers> getAllTransfers(@PathVariable long id) {
        return transferDAO.getTransferList(id);
    }

    // TODO: 10/15/22 need to fix so you cant be logged in as one user and pay from a different user to yourself (or others)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public String sendTransfer(@Valid @RequestBody Transfers transfers) {
        return transferDAO.sendTransfer(transfers.getAccountFrom(), transfers.getAccountTo(), transfers.getAmount());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "transfer/{transferId}", method = RequestMethod.GET)
    public Transfers getTransferByTransferId (@PathVariable long transferId) {
        return transferDAO.getTransferById(transferId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "transfers/{transferId}/from{accountFrom}", method = RequestMethod.GET)
    public String getAccountFromUsername (@PathVariable long transferId, @PathVariable long accountFrom) {
        return transferDAO.getUserFrom(accountFrom);
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "transfers/{transferId}/to{accountTo}", method = RequestMethod.GET)
    public String getAccountToUsername (@PathVariable long transferId, @PathVariable long accountTo) {
        return transferDAO.getUserTo(accountTo);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "transfer/{transferId}/details", method = RequestMethod.GET)
    public List<String> getTransferDetails (@PathVariable long transferId) {
        return transferDAO.getTransferDetails(transferId);
    }

}
