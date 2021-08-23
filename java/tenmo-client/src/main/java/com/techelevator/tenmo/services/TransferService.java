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

    public void sendBucks() {
        accountService = new AccountService(BASE_URL, currentUser);
        User[] users = null;
        Transfers transfers = new Transfers();
        try {
            Scanner in = new Scanner(System.in);
            users = restTemplate.exchange(BASE_URL + "users", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
            console.printUsers(users);

            System.out.println("-----------------------------------------------------\n");
            long input = 1L;
            boolean run = true;
            while (run) {
                try {
                    System.out.print("Enter ID of user you are sending to (0 to cancel): ");
                    input = Long.parseLong(in.nextLine());
                    Account account = accountService.getAccountByUserId(input);
                    long toAccountId = account.getAccountId(); //Long.parseLong(in.nextLine());
                    transfers.setAccountTo(toAccountId);
                    transfers.setAccountFrom(accountService.getAccountByUserId(currentUser.getUser().getId()).getAccountId());
                    System.out.print("Enter amount: ");
                    try {
                        transfers.setAmount(BigDecimal.valueOf(Double.parseDouble(in.next())));
                    } catch (NumberFormatException e) {
                        System.out.println("Sorry, that is not a valid amount!");
                    }
                    String message = restTemplate.exchange(BASE_URL + "transfer", HttpMethod.POST, makeTransferEntity(transfers), String.class).getBody();
                    System.out.println(message);
                    run = false;
                } catch (NullPointerException NPException) {
                    if (input == 0) {
                        break;
                    }
                    System.out.println("Invalid User Id. Please choose from the options above");
                } catch (NumberFormatException NFException) {
                    System.out.println("Invalid User Id. Please choose from the options above.");
                }
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
            console.printTransferHeaders();
            String otherUserName = "";
            for (Transfers transfer : transfersList) {
                String userFrom = restTemplate.exchange(BASE_URL + "transfers/" + transfer.getTransferId() + "/from"
                        + transfer.getAccountFrom(), HttpMethod.GET, makeAuthEntity(), String.class).getBody();
                String userTo = restTemplate.exchange(BASE_URL + "transfers/" + transfer.getTransferId() + "/to"
                        + transfer.getAccountTo(), HttpMethod.GET, makeAuthEntity(), String.class).getBody();
                if (currentUser.getUser().getUsername().equals(userFrom)) {
                    otherUserName = "To: " + userTo;
                } else {
                    otherUserName = "From: " + userFrom;
                }
                System.out.println(transfer.getTransferId() + "\t\t\t\t" + otherUserName + "\t\t\t\t\t" +
                        NumberFormat.getCurrencyInstance().format(transfer.getAmount()));
            }
            System.out.println("-------");
            // get transfer details here
            boolean details = false;
            while (!details) {
                long input = 1L;
                try {
                    input = console.getUserInputReturnLong("Please enter transfer ID to view details (0 to cancel): ");
                    details = getTransferDetails(input);
                } catch (NullPointerException NPException) {
                    if (input == 0) {
                        break;
                    }
                    System.out.println("Invalid Transfer Id. Please choose from the options above");
                } catch (NumberFormatException NFException) {
                    System.out.println("Invalid Transfer Id. Please choose from the options above.");
                }
            }
        } catch (RestClientResponseException ex) {
            // handles exceptions thrown by rest template and contains status codes
            console.printError(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException ex) {
            // i/o error, ex: the server isn't running
            console.printError(ex.getMessage());
        }
    }

    private boolean getTransferDetails(long transferId) {
        try {
            List<?> transferDetails = restTemplate.exchange(BASE_URL + "transfer/" + transferId + "/details",
                    HttpMethod.GET, makeAuthEntity(), List.class).getBody();
            if (transferId != 0) {
                System.out.println("Transfer ID:\t\t" + transferDetails.get(0));
                System.out.println("User From:\t\t\t" + transferDetails.get(1));
                System.out.println("User To:\t\t\t" + transferDetails.get(2));
                System.out.println("Transfer Type:\t\t" + transferDetails.get(3));
                System.out.println("Transfer Status:\t" + transferDetails.get(4));
                System.out.println("Transfer Amount:\t" + NumberFormat.getCurrencyInstance().format
                        (new BigDecimal(transferDetails.get(5).toString())));
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
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
}
