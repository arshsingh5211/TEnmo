package com.techelevator.tenmo.services;

public class BalanceServiceException extends Exception {
    private static final long serialVersionUID = 1L; // not sure what this does

    public BalanceServiceException(String message) {
        super(message);
    }
}
