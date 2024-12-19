package org.poo.main.structures;

public class Card {
    private String cardNumber;
    private String accountNumber;
    private String status;
    private boolean isOneTime;

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Card(String cardNumber, String accountNumber, boolean isOneTime) {
        this.cardNumber = cardNumber;
        this.accountNumber = accountNumber;
        this.status = "active";
        this.isOneTime = isOneTime;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOneTime() {
        return isOneTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", status='" + status + '\'' +
                ", isOneTime=" + isOneTime +
                '}';
    }
}
