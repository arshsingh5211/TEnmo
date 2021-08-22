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

    //@PreAuthorize("hasRole('ROLE_USER')")
    @PreAuthorize("permitAll()")
    @RequestMapping(path = "transfers/{id}/all", method = RequestMethod.GET)
    public List<Transfers> getAllTransfers(@PathVariable long id) {
        return transferDAO.getTransferList(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("hasRole('ROLE_USER')")
    @PreAuthorize("permitAll()")
    @RequestMapping(path = "transfer", method = RequestMethod.POST)
    public String sendTransfer(@Valid @RequestBody Transfers transfers) {
        return transferDAO.sendTransfer(transfers.getAccountFrom(), transfers.getAccountTo(), transfers.getAmount());
    }

    //@PreAuthorize("permitAll()")
    @PreAuthorize("hasRole('ROLE_USER')")
    //@PreAuthorize("permitAll()")
    @RequestMapping(path = "transfer/{transferId}", method = RequestMethod.GET)
    public Transfers getTransferByTransferId (@PathVariable long transferId) {
        return transferDAO.getTransferById(transferId);
    }

    @PreAuthorize("permitAll()")
    //@PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(path = "transfer/{transferId}/details", method = RequestMethod.GET)
    public String getTransferDetails (@PathVariable long transferId) {
        return transferDAO.getTransferDetails(transferId);
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(path = "transfer/status/{statusId}", method = RequestMethod.PUT)
    public String updateRequest(@RequestBody Transfers transfer, @PathVariable long statusId) {
        return transferDAO.updateTransferRequest(transfer, statusId);
    }
}
