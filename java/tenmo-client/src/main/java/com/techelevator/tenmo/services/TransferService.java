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
import java.text.NumberFormat;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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

/*    public Transfers addTransfer(String newTransfer) {
        // call helper method to make a new transfer
        User user = accountService.getUserByUsername(console.promptForUser());
        Transfers transfer = makeTransferObject(newTransfer, user);
        if (transfer == null) return null;
        // call the helper method to encapsulate the body (reservation) and headers together
        HttpEntity<Transfers> entity = makeTransferEntity(transfer);
        // postForObject (url, entity (headers and body encapsulated), class literal for what object is sent back
        try {
            transfer = restTemplate.postForObject(BASE_URL + "accounts/" + transfer.getTransferId() +
                    "/transfers", entity, Transfers.class);
        } catch (RestClientResponseException e){
            console.printError(e.getRawStatusCode() + " : " + e.getMessage());
        } catch (ResourceAccessException ex){
            console.printError(ex.getMessage());
        }
        return transfer;
    }*/

/*    public Transfers sendTransfer(String newTransfer) {
        User user = accountService.getUserByUsername(console.promptForUser());
        Transfers transfer = makeTransferObject(newTransfer, user); //makeTransfer(csv) method needed?
        if (transfer == null) {
            return null;
        }
        // need try/catch?
        restTemplate.postForObject(BASE_URL + "transfer_types", makeTransferTypeObject(transfer), TransferTypes.class);
        restTemplate.postForObject(BASE_URL + "transfer_statuses", makeTransferStatusObject(transfer), TransferStatuses.class);
        return restTemplate.postForObject(BASE_URL + "account/" + transfer.getAccountFrom() + "/transfers" + transfer.getAccountTo(),
                makeTransferEntity(transfer),  Transfers.class);
    }*/

    public void sendBucks() {
        accountService = new AccountService(BASE_URL, currentUser);
        User[] users = null;//accountService.getUsers();
        Transfers transfers = new Transfers();
        try {
            Scanner in = new Scanner(System.in);
            users = restTemplate.exchange(BASE_URL + "users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
            console.printUsers(users);
            //String userString = console.promptForUser();
            /*for (User user : users) {
                if (user.getId() != currentUser.getUser().getId()) {
                    System.out.println(user.getId() + "\t\t\t" + user.getUsername());
                }
            }*/
            System.out.println("-----------------------------------------------------\n");
            System.out.print("Enter ID of user you are sending to (0 to cancel): ");
            Account account = accountService.getAccountByUserId(Long.parseLong(in.nextLine()));
            System.out.println(account);
            long toAccountId = account.getAccountId(); //Long.parseLong(in.nextLine());
            transfers.setAccountTo(toAccountId);
            System.out.println(accountService.getAccountByUserId(currentUser.getUser().getId()).getAccountId());
            System.out.println("--");
            transfers.setAccountFrom(accountService.getAccountByUserId(currentUser.getUser().getId()).getAccountId());
            if (toAccountId != 0) {
                System.out.print("Enter amount: ");
                try {
                    transfers.setAmount(BigDecimal.valueOf(Double.parseDouble(in.next())));
                } catch (NumberFormatException e) {
                    System.out.println("Sorry, that is not a valid amount!");
                }
                String message = restTemplate.exchange(BASE_URL + "transfer", HttpMethod.POST, makeTransferEntity(transfers), String.class).getBody();
                /*System.out.println(message);*/
                //String message = restTemplate.postForObject(BASE_URL + "transfer", makeTransferEntity(transfers), String.class);
                System.out.println(message);
            }
        } catch (RestClientResponseException ex) {
            // handles exceptions thrown by rest template and contains status codes
            console.printError(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException ex) {
            // i/o error, ex: the server isn't running
            console.printError(ex.getMessage());
        }
    }

    public void viewPastTransfers() {
        Transfers[] transfersList = null;
        try {
            transfersList = restTemplate.exchange(BASE_URL + "transfers/" + currentUser.getUser().getId() + "/all",
                    HttpMethod.GET, makeAuthEntity(), Transfers[].class).getBody();
            System.out.println("--------------------------------------------");
            System.out.println("ID\t\t\t\tFrom/To\t\t\t\tAmount");
            System.out.println("--------------------------------------------");
            String otherUserName = "";
            for (Transfers transfer : transfersList) {
                String userFrom = restTemplate.exchange(BASE_URL + "transfers/" + transfer.getTransferId() + "/from"
                        + transfer.getAccountFrom(), HttpMethod.GET, makeAuthEntity(), String.class).getBody();
                String userTo = restTemplate.exchange(BASE_URL + "transfers/" + transfer.getTransferId() + "/to"
                        + transfer.getAccountTo(), HttpMethod.GET, makeAuthEntity(), String.class).getBody();
                if (currentUser.getUser().getUsername().equals(userFrom)) {
                    otherUserName = "To " + userTo;
                } else {
                    otherUserName = "From " + userFrom;
                }
                System.out.println(transfer.getTransferId() + "\t\t\t" + otherUserName + "\t\t\t\t" +
                        NumberFormat.getCurrencyInstance().format(transfer.getAmount()));
            }
            System.out.println("-------");
            //System.out.println("Please enter transfer ID to view details (0 to cancel): ");
            // get transfer details here
            getTransferDetails(console.getUserInputReturnLong("Please enter transfer ID to view details (0 to cancel): "));
        } catch (RestClientResponseException ex) {
            // handles exceptions thrown by rest template and contains status codes
            console.printError(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException ex) {
            // i/o error, ex: the server isn't running
            console.printError(ex.getMessage());
        }
    }

    private void getTransferDetails(long transferId) {
        List transferDetails = restTemplate.exchange(BASE_URL + "transfer/" + transferId + "/details",
                HttpMethod.GET, makeAuthEntity(), List.class).getBody();

        System.out.println("Transfer ID:\t\t" + transferDetails.get(0));
        System.out.println("User From:\t\t\t" + transferDetails.get(1));
        System.out.println("User To:\t\t\t" + transferDetails.get(2));
        System.out.println("Transfer Type:\t\t" + transferDetails.get(3));
        System.out.println("Transfer Status:\t" + transferDetails.get(4));
        System.out.println("Transfer Amount:\t" + transferDetails.get(5));
    }

    private HttpEntity<Transfers> makeTransferEntity(Transfers transfers) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // idk what this does?
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfers> entity = new HttpEntity<>(transfers, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

    private Transfers makeTransferObject(String csv, User userTo) {
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
        int userIdFrom = accountService.getUserByUsername(currentUser.getUser().getUsername()).getId();
        int userIdTo = accountService.getUserByUsername(userTo.getUsername()).getId();
        long accountFrom = accountService.getAccountByUserId(userIdFrom).getAccountId();
        long accountTo = accountService.getAccountByUserId(userIdTo).getAccountId();
        long transferId = new Random().nextInt(1000);
        long transferStatusId = 2; //new Random().nextInt(1000);
        long transferTypeId = 2; //new Random().nextInt(1000);

        Transfers transfers = new Transfers();
        transfers.setAmount(amount);
        transfers.setTransferId(transferId);
        transfers.setAccountFrom(accountFrom);
        transfers.setAccountTo(accountTo);
        transfers.setTransferStatusId(transferStatusId);
        transfers.setTransferTypeId(transferTypeId);

        return transfers;
    }

    public void createTransferStatus(long id, String status) {
        //restTemplate.(BASE_URL + "/transfer_statuses", )
    }
}
