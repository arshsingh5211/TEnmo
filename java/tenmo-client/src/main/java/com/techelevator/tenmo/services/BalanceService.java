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

public class BalanceService {
    public static String AUTH_TOKEN = "";
    public static final String BASE_URL = "http://localhost:8080";
    private AuthenticatedUser currentUser;
    private AuthenticationService authenticationService;
    private RestTemplate restTemplate = new RestTemplate();

    /*
    - Tried to make this work instead of putting it in the app class, but kept getting NPE and custom exception
    - Also changed Balance to Account in jdbc but didn't change it here to make sure that's what we want first
    - so far the only thing that works is what andy did in class
    - so feel free to change anything you want
     */

    public void viewCurrentBalance() throws Exception {
        try {
            restTemplate.exchange(BASE_URL + "/balance", HttpMethod.GET, makeAuthEntity(), Balance.class).getBody();
        } catch (RestClientResponseException ex) {
            throw new BalanceServiceException("Exception here"); // fix message later
        }
    }

    private HttpEntity<Balance> makeBalanceEntity(Balance balance) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // idk what this does?
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Balance> entity = new HttpEntity<>(balance, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
