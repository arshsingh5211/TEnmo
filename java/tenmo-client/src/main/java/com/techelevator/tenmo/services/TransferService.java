package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import com.techelevator.view.ConsoleService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Random;

public class TransferService {
    private String BASE_URL;
    private AuthenticatedUser currentUser;
    private RestTemplate restTemplate = new RestTemplate();
    private AccountService accountService;
    private final ConsoleService console = new ConsoleService(System.in, System.out);

    public TransferService(String BASE_URL, AuthenticatedUser currentUser) {
        this.BASE_URL = BASE_URL;
        this.currentUser = currentUser;
    }

    public Transfer addTransfer(String newTransfer) {
        // call helper method to make a new transfer
        Transfer transfer = makeTransferObject(newTransfer);
        if (transfer == null) return null;
        // call the helper method to encapsulate the body (reservation) and headers together
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        // postForObject (url, entity (headers and body encapsulated), class literal for what object is sent back
        try {
            transfer = restTemplate.postForObject(BASE_URL + "accounts/" + transfer.getTransferId() +
                    "/transfers", entity, Transfer.class);
        } catch (RestClientResponseException e){
            console.printError(e.getRawStatusCode() + " : " + e.getMessage());
        } catch (ResourceAccessException ex){
            console.printError(ex.getMessage());
        }
        return transfer;
    }

    public Transfer sendTransfer(String newTransfer) {

        Transfer transfer = makeTransferObject(newTransfer); //makeTransfer(csv) method needed?
        if (transfer == null) {
            return null;
        }
        // need try/catch?
        restTemplate.postForObject(BASE_URL + "transfer_types", makeTransferTypeObject(transfer), TransferType.class);
        restTemplate.postForObject(BASE_URL + "transfer_statuses", makeTransferStatusObject(transfer), TransferStatus.class);
        return restTemplate.postForObject(BASE_URL + "account/" + transfer.getAccountFrom() + "/transfers" + transfer.getAccountTo(),
                makeTransferEntity(transfer),  Transfer.class);
    }

    public void sendTransfer() {
        User[] users = null;
        Transfer transfer = new Transfer();
        users = restTemplate.exchange(BASE_URL + "list_users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
        System.out.println("--------------------------------------------------------");
        System.out.println("Users\r\n + UD\t\tName\r\n");
        System.out.println("--------------------------------------------------------");
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

    private TransferStatus makeTransferStatusObject(Transfer transfer) {
        TransferStatus transferStatus = new TransferStatus();
        transferStatus.setTransferStatusId(transfer.getTransferStatusId());
        transferStatus.setTransferStatus("Approved");

        return transferStatus;
    }

    private TransferType makeTransferTypeObject(Transfer transfer) {
        TransferType transferType = new TransferType();
        transferType.setTransferTypeId(transfer.getTransferStatusId());
        transferType.setTransferType("Type");

        return transferType;
    }

    private Transfer makeTransferObject(String csv) {
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

        BigDecimal amount = new BigDecimal(parsed[2]);
        long accountFrom = currentUser.getUser().getId();
        long accountTo = accountService.getUserByUsername(parsed[1]).getId();
        long transferId = new Random().nextInt(1000);
        long transferStatusId = new Random().nextInt(1000);
        long transferTypeId = new Random().nextInt(1000);

        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setTransferId(transferId);
        transfer.setAccountFrom(accountFrom);
        transfer.setAccountTo(accountTo);
        transfer.setTransferStatusId(transferStatusId);
        transfer.setTransferTypeId(transferTypeId);

        return transfer;
    }

    public void createTransferStatus(long id, String status) {
        //restTemplate.(BASE_URL + "/transfer_statuses", )
    }
}
