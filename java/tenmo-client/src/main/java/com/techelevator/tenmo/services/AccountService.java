package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Balance;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class AccountService {
    private String BASE_URL;
    private AuthenticatedUser currentUser;
    private RestTemplate restTemplate = new RestTemplate();

    public AccountService(AuthenticatedUser currentUser, String url) {
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    /*
    - Tried to make this work instead of putting it in the app class, but kept getting NPE and custom exception
    - Also changed Balance to Account in jdbc but didn't change it here to make sure that's what we want first
    - so far the only thing that works is what andy did in class
    - so feel free to change anything you want
     */

    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal("0");
        try {
            balance = restTemplate.exchange(BASE_URL + "balance/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
            System.out.println("Your current balance is " + NumberFormat.getCurrencyInstance().format(balance));
        } catch (RestClientResponseException ex) {
            ex.getRawStatusCode();
            // fix the message later
        }
        return balance;
    }

    private HttpEntity<Balance> makeBalanceEntity(Balance balance) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // idk what this does?
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Balance> entity = new HttpEntity<>(balance, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
