package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Random;

public class TransferService {
    private String BASE_URL;
    private AuthenticatedUser currentUser;
    private RestTemplate restTemplate = new RestTemplate();
    private AccountService accountService;

    public TransferService(String BASE_URL, AuthenticatedUser currentUser) {
        this.BASE_URL = BASE_URL;
        this.currentUser = currentUser;
    }

    public Transfer sendTransfer(String newTransfer) {

        Transfer transfer = makeTransferObject(newTransfer); //makeTransfer(csv) method needed?
        if (transfer == null) {
            return null;
        }
        // need try/catch?
        return restTemplate.postForObject(BASE_URL + "transfer/", makeTransferEntity(transfer), Transfer.class);
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // idk what this does?
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

    private Transfer makeTransferObject(String csv) {
        long transferId;
        long transferTypeId;
        long transferStatusId;
        long accountFrom;
        long accountTo;
        BigDecimal amount;
        // {"recipient", 100.00}
        String[] parsed = csv.split(",".toLowerCase());

        // verify csv input has correct number of input
        if (parsed.length < 2 || parsed.length > 3) {
            return null;
        }

        if (parsed.length == 2) {
            String[] withId = new String[3];
            String[] idArray = new String[]{new Random().nextInt(1000) + ""};
            System.arraycopy(idArray, 0, withId, 0, 1);
            System.arraycopy(parsed, 0, withId, 1, 2);
            parsed = withId;
        }

        // verify sufficient balance
        accountService = new AccountService(BASE_URL, currentUser);
        BigDecimal currentBalance = accountService.getBalance();

        amount = new BigDecimal(parsed[2]);
        accountFrom = currentUser.getUser().getId();
        accountTo = accountService.getUserByUsername(parsed[1]).getId();
        transferId = new Random().nextInt(1000);
        transferStatusId = new Random().nextInt(1000);
        return new Transfer(

        );
    }

    public void createTransferStatus(long id, String status) {
        restTemplate.(BASE_URL + "/transfer_statuses", )
    }
}
