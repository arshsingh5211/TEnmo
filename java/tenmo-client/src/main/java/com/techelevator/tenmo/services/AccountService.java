package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
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
import java.util.ArrayList;
import java.util.List;

public class AccountService {
    private String BASE_URL;
    private AuthenticatedUser currentUser;
    private RestTemplate restTemplate = new RestTemplate();
    private final ConsoleService console = new ConsoleService(System.in, System.out);

    public AccountService(String url, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = null;
        try {
            balance = restTemplate.exchange(BASE_URL + "balance/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
            System.out.println("Your current account balance is " + NumberFormat.getCurrencyInstance().format(balance)); // it's TE bucks not USD, get rid of this?
        } catch (RestClientResponseException ex) {
            // handles exceptions thrown by rest template and contains status codes
            console.printError(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException ex) {
            // i/o error, ex: the server isn't running
            console.printError(ex.getMessage());
        }
        return balance;
    }

    public User[] getUsers() {
        User[] userList = null;
        try {
            /*userList = restTemplate.exchange(BASE_URL + "users", HttpMethod.GET, makeAuthEntity(),
                    User[].class).getBody();*/
            userList = restTemplate.getForObject(BASE_URL + "users", User[].class);
        } catch (RestClientResponseException ex) {
            // handles exceptions thrown by rest template and contains status codes
            console.printError(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException ex) {
            // i/o error, ex: the server isn't running
            console.printError(ex.getMessage());
        }
        return userList;
    }

    public User getUserByUsername(String username) {
        User user = null;
        try {
            user = restTemplate.getForObject(BASE_URL + "users/" + username, User.class);
        } catch (RestClientResponseException ex) {
            // handles exceptions thrown by rest template and contains status codes
            console.printError(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException ex) {
            // i/o error, ex: the server isn't running
            console.printError(ex.getMessage());
        }
        return user;
    }

    public Account getAccountByUserId(long id) {
        Account account = null;
        try {
            account = restTemplate.exchange(BASE_URL + "account/user" + id, HttpMethod.GET, makeAuthEntity(), Account.class).getBody();
        } catch (RestClientResponseException ex) {
            // handles exceptions thrown by rest template and contains status codes
            console.printError(ex.getRawStatusCode() + " : " + ex.getStatusText());
        } catch (ResourceAccessException ex) {
            // i/o error, ex: the server isn't running
            console.printError(ex.getMessage());
        }
        return account;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
