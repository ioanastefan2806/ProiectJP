package org.poo.main.structures;

import org.poo.utils.Utils;

/**
 * Represents a Card associated with a user's account.
 * A Card can be active, frozen, or a one-time-use card.
 */
public class Card {
    private String cardNumber;
    private String accountNumber;
    private String status;
    private boolean isOneTime;

    /**
     * Constructs a Card with the specified card number, account number, and one-time-use flag.
     *
     * @param cardNumber the card number
     * @param accountNumber the account number associated with the card
     * @param isOneTime true if the card is a one-time-use card, false otherwise
     */
    public Card(final String cardNumber,
                final String accountNumber,
                final boolean isOneTime) {
        this.cardNumber = cardNumber;
        this.accountNumber = accountNumber;
        this.status = "active";
        this.isOneTime = isOneTime;
    }

    // Getters and Setters

    /**
     * Retrieves the card number.
     *
     * @return the card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Updates the card number.
     *
     * @param cardNumber the new card number
     */
    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Retrieves the account number associated with the card.
     *
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Retrieves the current status of the card.
     *
     * @return the card status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Updates the status of the card.
     *
     * @param status the new status of the card
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * Checks if the card is a one-time-use card.
     *
     * @return true if the card is a one-time-use card, false otherwise
     */
    public boolean isOneTime() {
        return isOneTime;
    }

    // Methods

    /**
     * Checks if the card is currently frozen.
     *
     * @return true if the card is frozen, false otherwise
     */
    public boolean isFrozen() {
        return "frozen".equalsIgnoreCase(this.status);
    }

    /**
     * Regenerates the card number for one-time-use cards and updates its status to active.
     *
     * @param user the user associated with the card
     * @param timestamp the timestamp of the regeneration
     * @param accountNumber the account number linked to the card
     */
    public void regenerateCardNumber(final User user,
                                     final int timestamp,
                                     final String accountNumber) {
        this.status = "frozen";
        String newCardNumber = Utils.generateCardNumber();
        this.cardNumber = newCardNumber;
        this.status = "active";

        Transaction updateCardTransaction =
                Transaction.createOneTimeCardRegenerationTransaction(timestamp,
                        newCardNumber, user.getEmail(), accountNumber);
        user.addTransaction(updateCardTransaction);
    }

    /**
     * Freezes the card, setting its status to "frozen".
     */
    public void freeze() {
        this.status = "frozen";
    }

    /**
     * Returns a string representation of the Card object.
     *
     * @return a string containing the card number, account number, status, and one-time-use flag
     */
    @Override
    public String toString() {
        return "Card{"
                + "cardNumber='" + cardNumber + '\''
                + ", accountNumber='" + accountNumber + '\''
                + ", status='" + status + '\''
                + ", isOneTime=" + isOneTime
                + '}';
    }
}
