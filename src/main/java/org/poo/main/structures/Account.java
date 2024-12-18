package org.poo.main.structures;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountNumber;
    private String currency;
    private String accountType;
    private double balance;
    private double interestRate;
    private List<Card> cards;

    public Account(String accountNumber, String currency, String accountType, double interestRate) {
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.accountType = accountType;
        this.balance = 0.0; // Balanța inițiala
        this.interestRate = interestRate;
        this.cards = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", currency='" + currency + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", interestRate=" + interestRate +
                ", cards=" + cards +
                '}';
    }
}
