package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfers {
    private long transferId;
    private long transferTypeId;
    private long transferStatusId;
    private long accountFrom;
    private long accountTo;
    private BigDecimal amount;
    private String transferType;
    private String transferStatus;
    private String userFrom;
    private String userTo;

    public long getTransferId() {
        return transferId;
    }
    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }
    public long getTransferTypeId() {
        return transferTypeId;
    }
    public void setTransferTypeId(long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }
    public long getTransferStatusId() {
        return transferStatusId;
    }
    public void setTransferStatusId(long transferStatusId) {
        this.transferStatusId = transferStatusId;
    }
    public long getAccountFrom() {
        return accountFrom;
    }
    public void setAccountFrom(long accountFrom) {
        this.accountFrom = accountFrom;
    }
    public long getAccountTo() {
        return accountTo;
    }
    public void setAccountTo(long accountTo) {
        this.accountTo = accountTo;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getTransferType() {
        return transferType;
    }
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
    public String getTransferStatus() {
        return transferStatus;
    }
    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }
    public String getUserFrom() {
        return userFrom;
    }
    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }
    public String getUserTo() {
        return userTo;
    }
    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }
}
