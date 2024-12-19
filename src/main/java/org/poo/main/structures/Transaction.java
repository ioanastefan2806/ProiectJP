package org.poo.main.structures;

import java.util.List;

public class Transaction {
    private String senderIBAN;
    private String receiverIBAN;

    private String cardNumber;
    private String accountNumber;
    private String commerciant;

    private List<String> involvedIBANs;

    public List<String> getInvolvedIBANs() {
        return involvedIBANs;
    }

    public void setInvolvedIBANs(List<String> involvedIBANs) {
        this.involvedIBANs = involvedIBANs;
    }

    public String getCommerciant() {
        return commerciant;
    }

    public void setCommerciant(String commerciant) {
        this.commerciant = commerciant;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    private String currency;
    private String email;

    private double amount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String description;
    private int timestamp;

    private String type;

    public String getSenderIBAN() {
        return senderIBAN;
    }

    public void setSenderIBAN(String senderIBAN) {
        this.senderIBAN = senderIBAN;
    }

    public String getReceiverIBAN() {
        return receiverIBAN;
    }

    public void setReceiverIBAN(String receiverIBAN) {
        this.receiverIBAN = receiverIBAN;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Transaction(String type, int timestamp, String description, String accountNumber, String number, double amount, String senderCurrency, String email) {
        this.timestamp = timestamp;
        this.description = description;
        this.senderIBAN = accountNumber;
        this.receiverIBAN = number;
        this.amount = amount;
        this.currency = senderCurrency;
        this.email = email;
        this.type = type;
    }

    public Transaction(String type, int timestamp, String description, String cardNumber, String email, String accountNumber) {
        this.timestamp = timestamp;
        this.description = description;
        this.cardNumber = cardNumber;
        this.email= email;
        this.accountNumber = accountNumber;
        this.type = type;
    }

    public Transaction(List<String> involvedIBANs, String currency, double amount, String description, int timestamp, String type) {
        this.involvedIBANs = involvedIBANs;
        this.currency = currency;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Transaction(String type, String accountNumber, double amount, String description, int timestamp) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Transaction(String type, int timestamp, String description) {
        this.description = description;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
