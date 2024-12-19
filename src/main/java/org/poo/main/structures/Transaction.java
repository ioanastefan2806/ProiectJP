package org.poo.main.structures;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import java.util.List;

/**
 * Represents a financial transaction involving accounts, cards, and IBANs.
 */
public class Transaction {

    private String senderIBAN;
    private String receiverIBAN;
    private String cardNumber;
    private String accountNumber;
    private String commerciant;
    private List<String> involvedIBANs;
    private String currency;
    private String email;
    private double amount;
    private String description;
    private int timestamp;
    private String type;

    // Getters and Setters

    /**
     * Gets the list of IBANs involved in the transaction.
     *
     * @return the list of involved IBANs
     */
    public List<String> getInvolvedIBANs() {
        return involvedIBANs;
    }

    /**
     * Sets the list of IBANs involved in the transaction.
     *
     * @param involvedIBANs the list of involved IBANs to set
     */
    public void setInvolvedIBANs(final List<String> involvedIBANs) {
        this.involvedIBANs = involvedIBANs;
    }

    /**
     * Gets the name of the merchant associated with the transaction.
     *
     * @return the merchant name
     */
    public String getCommerciant() {
        return commerciant;
    }

    /**
     * Sets the name of the merchant associated with the transaction.
     *
     * @param commerciant the merchant name to set
     */
    public void setCommerciant(final String commerciant) {
        this.commerciant = commerciant;
    }

    /**
     * Gets the card number associated with the transaction.
     *
     * @return the card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the card number associated with the transaction.
     *
     * @param cardNumber the card number to set
     */
    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Gets the sender's IBAN for the transaction.
     *
     * @return the sender's IBAN
     */
    public String getSenderIBAN() {
        return senderIBAN;
    }

    /**
     * Sets the sender's IBAN for the transaction.
     *
     * @param senderIBAN the sender's IBAN to set
     */
    public void setSenderIBAN(final String senderIBAN) {
        this.senderIBAN = senderIBAN;
    }

    /**
     * Gets the receiver's IBAN for the transaction.
     *
     * @return the receiver's IBAN
     */
    public String getReceiverIBAN() {
        return receiverIBAN;
    }

    /**
     * Sets the receiver's IBAN for the transaction.
     *
     * @param receiverIBAN the receiver's IBAN to set
     */
    public void setReceiverIBAN(final String receiverIBAN) {
        this.receiverIBAN = receiverIBAN;
    }

    /**
     * Gets the currency used in the transaction.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency used in the transaction.
     *
     * @param currency the currency to set
     */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * Gets the email associated with the transaction.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email associated with the transaction.
     *
     * @param email the email to set
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets the account number associated with the transaction.
     *
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the account number associated with the transaction.
     *
     * @param accountNumber the account number to set
     */
    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the amount involved in the transaction.
     *
     * @return the transaction amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount involved in the transaction.
     *
     * @param amount the transaction amount to set
     */
    public void setAmount(final double amount) {
        this.amount = amount;
    }

    /**
     * Gets the description of the transaction.
     *
     * @return the transaction description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the transaction.
     *
     * @param description the transaction description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the timestamp of the transaction.
     *
     * @return the transaction timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the transaction.
     *
     * @param timestamp the transaction timestamp to set
     */
    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the type of the transaction.
     *
     * @return the transaction type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the transaction.
     *
     * @param type the transaction type to set
     */
    public void setType(final String type) {
        this.type = type;
    }


    // Constructors

    /**
     * Constructs a Transaction for sending money.
     *
     * @param type the type of transaction
     * @param timestamp the timestamp of the transaction
     * @param description the description of the transaction
     * @param accountNumber the sender's account number
     * @param receiverIBAN the receiver's IBAN
     * @param amount the amount transferred
     * @param currency the currency used
     * @param email the sender's email
     */
    public Transaction(final String type,
                       final int timestamp,
                       final String description,
                       final String accountNumber,
                       final String receiverIBAN,
                       final double amount,
                       final String currency,
                       final String email) {
        this.type = type;
        this.timestamp = timestamp;
        this.description = description;
        this.senderIBAN = accountNumber;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.currency = currency;
        this.email = email;
    }

    /**
     * Constructs a Transaction for card-related operations.
     *
     * @param type the type of transaction
     * @param timestamp the timestamp of the transaction
     * @param description the description of the transaction
     * @param cardNumber the card number involved
     * @param email the email associated with the card
     * @param accountNumber the account number linked to the card
     */
    public Transaction(final String type,
                       final int timestamp,
                       final String description,
                       final String cardNumber,
                       final String email,
                       final String accountNumber) {
        this.type = type;
        this.timestamp = timestamp;
        this.description = description;
        this.cardNumber = cardNumber;
        this.email = email;
        this.accountNumber = accountNumber;
    }

    /**
     * Constructs a split payment Transaction.
     *
     * @param involvedIBANs the IBANs involved in the split payment
     * @param currency the currency used in the transaction
     * @param amount the amount per split
     * @param description the description of the transaction
     * @param timestamp the timestamp of the transaction
     * @param type the type of transaction
     */
    public Transaction(final List<String> involvedIBANs,
                       final String currency,
                       final double amount,
                       final String description,
                       final int timestamp,
                       final String type) {
        this.involvedIBANs = involvedIBANs;
        this.currency = currency;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.type = type;
    }

    /**
     * Constructs a simple account-related Transaction.
     *
     * @param type the type of transaction
     * @param accountNumber the account number involved
     * @param amount the amount involved
     * @param description the description of the transaction
     * @param timestamp the timestamp of the transaction
     */
    public Transaction(final String type,
                       final String accountNumber,
                       final double amount,
                       final String description,
                       final int timestamp) {
        this.type = type;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    /**
     * Constructs a basic Transaction.
     *
     * @param type the type of transaction
     * @param timestamp the timestamp of the transaction
     * @param description the description of the transaction
     */
    public Transaction(final String type,
                       final int timestamp,
                       final String description) {
        this.type = type;
        this.timestamp = timestamp;
        this.description = description;
    }

    // Factory Methods and Static Helpers

    /**
     * Creates a frozen transaction for an account.
     *
     * @param timestamp the timestamp of the transaction
     * @param accountNumber the account number to be frozen
     * @return a Transaction object
     */
    public static Transaction createFrozenTransaction(final int timestamp,
                                                      final String accountNumber) {
        Transaction transaction = new Transaction("payFrosen", timestamp, "The card is frozen");
        transaction.setAccountNumber(accountNumber);
        return transaction;
    }

    /**
     * Creates a successful transaction for card payment.
     *
     * @param command the input command
     * @param accountNumber the account number used
     * @param amount the transaction amount
     * @return a Transaction object
     */
    public static Transaction createSuccessfulTransaction(final CommandInput command,
                                                          final String accountNumber,
                                                          final double amount) {
        Transaction transaction = new Transaction("paySucessful",
                command.getTimestamp(), "Card payment");
        transaction.setAmount(amount);
        transaction.setCommerciant(command.getCommerciant());
        transaction.setAccountNumber(accountNumber);
        return transaction;
    }

    /**
     * Creates a one-time card regeneration transaction.
     *
     * @param timestamp the timestamp of the transaction
     * @param cardNumber the card number being regenerated
     * @param email the email associated with the transaction
     * @param accountNumber the account number linked to the card
     * @return a Transaction object
     */
    public static Transaction createOneTimeCardRegenerationTransaction(final int timestamp,
                                                                       final String cardNumber,
                                                                       final String email,
                                                                       final String accountNumber) {
        Transaction transaction = new Transaction("updateOneTimeCard",
                timestamp, "One-time card number regenerated");
        transaction.setCardNumber(cardNumber);
        transaction.setEmail(email);
        transaction.setAccountNumber(accountNumber);
        return transaction;
    }

    /**
     * Creates a transaction for insufficient funds.
     *
     * @param timestamp the timestamp of the transaction
     * @param accountNumber the account number with insufficient funds
     * @return a Transaction object
     */
    public static Transaction createInsufficientFundsTransaction(final int timestamp,
                                                                 final String accountNumber) {
        Transaction transaction = new Transaction("payNoFunds", timestamp, "Insufficient funds");
        transaction.setAccountNumber(accountNumber);
        return transaction;
    }

    /**
     * Creates a transaction for sending money.
     *
     * @param command the input command
     * @param senderAccount the sender's account
     * @param receiverAccount the receiver's account
     * @return a Transaction object
     */
    public static Transaction createSendMoneyTransaction(final CommandInput command,
                                                         final Account senderAccount,
                                                         final Account receiverAccount) {
        return new Transaction(
                "sendMoney",
                command.getTimestamp(),
                command.getDescription(),
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber(),
                command.getAmount(),
                senderAccount.getCurrency(),
                command.getEmail()
        );
    }

    /**
     * Creates a transaction for receiving money.
     *
     * @param command the input command
     * @param senderAccount the sender's account
     * @param receiverAccount the receiver's account
     * @return a Transaction object
     */
    public static Transaction createReceiveMoneyTransaction(final CommandInput command,
                                                            final Account senderAccount,
                                                            final Account receiverAccount) {
        return new Transaction(
                "sendMoney",
                command.getTimestamp(),
                command.getDescription(),
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber(),
                command.getAmount(),
                senderAccount.getCurrency(),
                command.getEmail()
        );
    }

    /**
     * Creates a freeze transaction for an account and card.
     *
     * @param timestamp the timestamp of the transaction
     * @param account the account being frozen
     * @param card the card being frozen
     * @return a Transaction object
     */
    public static Transaction createFreezeTransaction(final int timestamp,
                                                      final Account account,
                                                      final Card card) {
        Transaction transaction = new Transaction(
                "checkCardStatus",
                timestamp,
                "You have reached the minimum amount of funds, the card will be frozen"
        );
        transaction.setAccountNumber(account.getAccountNumber());
        transaction.setCardNumber(card.getCardNumber());
        return transaction;
    }

    /**
     * Creates a split payment transaction.
     *
     * @param user the user initiating the transaction
     * @param account the account being used
     * @param timestamp the timestamp of the transaction
     * @param share the amount shared in the split
     * @param currency the currency of the transaction
     * @param involvedIBANs the list of IBANs involved in the split
     * @param totalAmount the total amount of the split payment
     */
    public static void createSplitPaymentTransaction(final User user,
                                                     final Account account,
                                                     final int timestamp,
                                                     final double share,
                                                     final String currency,
                                                     final List<String> involvedIBANs,
                                                     final double totalAmount) {
        String description = String.format("Split payment of %.2f %s", totalAmount, currency);

        Transaction splitPaymentTransaction = new Transaction(
                "splitPayment",
                timestamp,
                description
        );
        splitPaymentTransaction.setCurrency(currency);
        splitPaymentTransaction.setAmount(share);
        splitPaymentTransaction.setInvolvedIBANs(involvedIBANs);

        user.addTransaction(splitPaymentTransaction);
    }

    /**
     * Creates a failed split payment transaction.
     *
     * @param user the user involved in the failed transaction
     * @param timestamp the timestamp of the transaction
     */
    public static void createFailedSplitPaymentTransaction(final User user,
                                                           final int timestamp) {
        Transaction failedSplitPaymentTransaction = new Transaction(
                "payNoFunds",
                timestamp,
                "Split payment failed due to insufficient funds"
        );

        user.addTransaction(failedSplitPaymentTransaction);
    }

    // Utility Methods

    /**
     * Determines if the transaction is within a specific timestamp range.
     *
     * @param startTimestamp the start of the range
     * @param endTimestamp the end of the range
     * @return true if within range, false otherwise
     */
    public boolean isWithinTimestamp(final int startTimestamp,
                                     final int endTimestamp) {
        return this.getTimestamp() >= startTimestamp && this.getTimestamp() <= endTimestamp;
    }

    /**
     * Checks if the transaction is relevant to a specific account type.
     *
     * @param account the account to check relevance against
     * @return true if relevant, false otherwise
     */
    public boolean isRelevantToAccountType(final Account account) {
        if (!"savings".equalsIgnoreCase(account.getAccountType())) {
            return true;
        }
        return "interestIncome".equalsIgnoreCase(this.getType())
                || "changeInterestRate".equalsIgnoreCase(this.getType());
    }

    /**
     * Converts the transaction into a report node for JSON representation.
     *
     * @param objectMapper the object mapper for JSON creation
     * @return an ObjectNode containing transaction details
     */
    public ObjectNode toReportNode(final ObjectMapper objectMapper) {
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("timestamp", this.getTimestamp());
        transactionNode.put("description", this.getDescription());

        switch (this.getType()) {
            case "addAccount":
            case "addCard":
            case "deleteCard":
            case "checkCardStatus":
            case "changeInterestRate":
                if (this.getCardNumber() != null) {
                    transactionNode.put("card", this.getCardNumber());
                }
                if (this.getEmail() != null) {
                    transactionNode.put("cardHolder", this.getEmail());
                }
                if (this.getAccountNumber() != null) {
                    transactionNode.put("account", this.getAccountNumber());
                }
                break;

            case "paySucessful":
            case "payNoFunds":
            case "PayOnetime":
            case "payFrosen":
            case "splitPayment":
            case "splitPaymentFailed":
                if (this.getAmount() > 0) {
                    transactionNode.put("amount", this.getAmount());
                }
                if (this.getCommerciant() != null) {
                    transactionNode.put("commerciant", this.getCommerciant());
                }
                break;

            case "sendMoney":
                if (this.getSenderIBAN() != null) {
                    transactionNode.put("senderIBAN", this.getSenderIBAN());
                }
                if (this.getReceiverIBAN() != null) {
                    transactionNode.put("receiverIBAN", this.getReceiverIBAN());
                }
                if (this.getAmount() > 0) {
                    String amountWithCurrencySend = String.format("%.1f", this.getAmount())
                            + " " + this.getCurrency();
                    transactionNode.put("amount", amountWithCurrencySend);
                }
                String transferType = this.determineTransferType();

                if (transferType != null) {
                    transactionNode.put("transferType", transferType);
                }
                break;

            default:
                break;
        }
        return transactionNode;
    }

    /**
     * Determines the transfer type of a transaction.
     *
     * @return "received" if the email matches sender IBAN, otherwise "sent"
     */
    public String determineTransferType() {
        if (this.getEmail() == null || this.getSenderIBAN() == null
                || this.getReceiverIBAN() == null) {
            return null;
        }
        return this.getEmail().equals(this.getSenderIBAN()) ? "received" : "sent";
    }

    /**
     * Checks if the transaction is spending-relevant for a specific account.
     *
     * @param account the account to check relevance for
     * @return true if spending-relevant, false otherwise
     */
    public boolean isSpendingRelevant(final Account account) {
        return "paySucessful".equalsIgnoreCase(this.getType())
                && this.getCommerciant() != null
                && this.getAccountNumber().equalsIgnoreCase(account.getAccountNumber());
    }

    /**
     * Converts the transaction into a spending report node for JSON representation.
     *
     * @param objectMapper the object mapper for JSON creation
     * @return an ObjectNode containing spending transaction details
     */
    public ObjectNode toSpendingsReportNode(final ObjectMapper objectMapper) {
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("timestamp", this.getTimestamp());
        transactionNode.put("description", this.getDescription());
        transactionNode.put("amount", this.getAmount());
        transactionNode.put("commerciant", this.getCommerciant());
        return transactionNode;
    }
    /**
     * Returns a string representation of the Transaction object.
     *
     * @return a string containing the account number, amount,
     * description, and timestamp of the transaction
     */
    @Override
    public String toString() {
        return "Transaction{"
                + "accountNumber='" + accountNumber + '\''
                + ", amount=" + amount
                + ", description='" + description + '\''
                + ", timestamp=" + timestamp
                + '}';
    }
}
