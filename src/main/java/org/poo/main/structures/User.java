package org.poo.main.structures;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.CommandProcessor;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Represents a user in the system. A user can have multiple accounts, cards, and transactions.
 */
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private List<Account> accounts;
    private List<Card> cards;
    private List<Transaction> transactions;

    private Map<String, String> aliasToIban = new HashMap<>();

    /**
     * Constructs a User with the specified details.
     *
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param email the email of the user
     */
    public User(final String firstName,
                final String lastName,
                final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    // Getters and Setters

    /**
     * Gets the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName the last name to set
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email of the user.
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the email to set
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets the list of accounts associated with the user.
     *
     * @return the list of accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Adds an account to the user's list of accounts.
     *
     * @param account the account to add
     */
    public void addAccount(final Account account) {
        this.accounts.add(account);
    }

    /**
     * Gets the list of cards associated with the user.
     *
     * @return the list of cards
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Adds a card to the user's list of cards.
     *
     * @param card the card to add
     */
    public void addCard(final Card card) {
        this.cards.add(card);
    }

    /**
     * Gets the list of transactions associated with the user.
     *
     * @return the list of transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Adds a transaction to the user's list of transactions.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
    }

    /**
     * Sets an alias for a specific IBAN.
     *
     * @param alias the alias to set
     * @param iban the IBAN associated with the alias
     */
    public void setAlias(final String alias,
                         final String iban) {
        aliasToIban.put(alias, iban);
    }

    /**
     * Gets the IBAN associated with a specific alias.
     *
     * @param alias the alias to look up
     * @return the IBAN associated with the alias, or null if not found
     */
    public String getIbanByAlias(final String alias) {
        return aliasToIban.get(alias);
    }


    /**
     * Adds a new account for the user based on the provided command.
     *
     * @param command the command containing account details
     */
    public void addAccount(final CommandInput command) {
        String iban = Utils.generateIBAN();
        String accountType = command.getAccountType();
        double interestRate = 0.0;

        if ("savings".equals(accountType)) {
            interestRate = command.getInterestRate();
        }
        Account account = new Account(iban, command.getCurrency(), accountType, interestRate);
        addAccount(account);
        Transaction transaction = new Transaction("addAccount",
                command.getTimestamp(), "New account created");
        addTransaction(transaction);
    }

    /**
     * Creates a new card for a specific account.
     *
     * @param accountNumber the account number
     * @param timestamp the timestamp of the card creation
     */
    public void createCard(final String accountNumber,
                           final int timestamp) {
        Account targetAccount = null;
        for (Account account : getAccounts()) {
            if (account.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                targetAccount = account;
                break;
            }
        }

        if (targetAccount == null) {
            return;
        }

        String cardNumber = Utils.generateCardNumber();
        Card card = new Card(cardNumber, targetAccount.getAccountNumber(), false);
        targetAccount.addCard(card);
        Transaction transaction = new Transaction("addCard", timestamp,
                "New card created", cardNumber, getEmail(), targetAccount.getAccountNumber());
        addTransaction(transaction);
    }

    /**
     * Creates a one-time use card for a specific account.
     *
     * @param command the command containing account details
     */
    public void createOneTimeCard(final CommandInput command) {
        String accountNbr = null;
        String cardNumber = null;

        for (Account account : getAccounts()) {
            if (account.getAccountNumber().equals(command.getAccount())) {
                cardNumber = Utils.generateCardNumber();
                accountNbr = account.getAccountNumber();
                Card card = new Card(cardNumber, account.getAccountNumber(), true);
                account.addCard(card);
                break;
            }
        }

        Transaction transaction = new Transaction("addCard", command.getTimestamp(),
                "New card created", cardNumber, getEmail(), accountNbr);
        addTransaction(transaction);
    }

    /**
     * Deletes an account based on the command details.
     *
     * @param command the command containing account details
     * @return true if the account was deleted, false otherwise
     */
    public boolean deleteAccount(final CommandInput command) {
        boolean deleted = getAccounts().removeIf(account -> {
            if (!account.getAccountNumber().equals(command.getAccount())) {
                return false;
            }
            if (account.getBalance() != 0) {
                return false;
            }
            account.getCards().clear();
            return true;
        });

        return deleted;
    }

    /**
     * Deletes a card based on the card number.
     *
     * @param cardNumber the card number
     * @param timestamp the timestamp of the deletion
     * @return the transaction created for the deletion
     */
    public Transaction deleteCard(final String cardNumber,
                                  final int timestamp) {
        Iterator<Card> cardIterator = cards.iterator();
        while (cardIterator.hasNext()) {
            Card card = cardIterator.next();
            if (card.getCardNumber().equals(cardNumber)) {
                cardIterator.remove();
                return createDeleteCardTransaction(cardNumber, null, timestamp);
            }
        }

        for (Account account : accounts) {
            Iterator<Card> accountCardIterator = account.getCards().iterator();
            while (accountCardIterator.hasNext()) {
                Card card = accountCardIterator.next();
                if (card.getCardNumber().equals(cardNumber)) {
                    accountCardIterator.remove();
                    return createDeleteCardTransaction(cardNumber,
                            account.getAccountNumber(), timestamp);
                }
            }
        }

        return null;
    }
    /**
     * Creates a transaction for deleting a card.
     *
     * @param cardNumber the number of the card to be deleted
     * @param accountNumber the account number associated with the card, or null if not applicable
     * @param timestamp the timestamp of the deletion
     * @return the transaction created for deleting the card
     */
    private Transaction createDeleteCardTransaction(final String cardNumber,
                                                    final String accountNumber,
                                                    final int timestamp) {
        Transaction deleteCardTransaction = new Transaction(
                "deleteCard",
                timestamp,
                "The card has been destroyed"
        );
        deleteCardTransaction.setCardNumber(cardNumber);
        deleteCardTransaction.setEmail(this.email);
        if (accountNumber != null) {
            deleteCardTransaction.setAccountNumber(accountNumber);
        }
        return deleteCardTransaction;
    }

    /**
     * Deletes an account by its account number.
     *
     * @param accountNumber the account number to delete
     * @return true if the account was deleted, false otherwise
     */
    public boolean deleteAccount(final String accountNumber) {
        return accounts.removeIf(account -> {
            if (!account.getAccountNumber().equals(accountNumber)) {
                return false;
            }
            if (account.getBalance() != 0) {
                return false;
            }
            account.clearCards();
            return true;
        });
    }

    /**
     * Sets the minimum balance for a specific account.
     *
     * @param accountNumber the account number
     * @param amount the minimum balance to set
     * @return true if the operation succeeded, false otherwise
     */
    public boolean setAccountMinBalance(final String accountNumber,
                                        final double amount) {
        for (Account account : this.accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                account.setMinBalance(amount);
                return true;
            }
        }
        return false;
    }

    /**
     * Processes a transaction for the user.
     *
     * @param command the transaction command input
     * @param responseNode the response node to record transaction details
     * @param commandProcessor the processor handling the transaction
     * @return true if the transaction was processed successfully, false otherwise
     */
    public boolean processTransaction(final CommandInput command,
                                      final ObjectNode responseNode,
                                      final CommandProcessor commandProcessor) {
        for (Account account : this.accounts) {
            if (account.processCardTransaction(command, responseNode, this, commandProcessor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds an account by its IBAN.
     *
     * @param iban the IBAN of the account
     * @return the account if found, null otherwise
     */
    public Account findAccountByIBAN(final String iban) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Sets an alias for a specific account.
     *
     * @param alias the alias to set
     * @param accountNumber the account number to associate with the alias
     * @return true if the alias was set successfully, false otherwise
     */
    public boolean setAliasForAccount(final String alias,
                                      final String accountNumber) {
        Account targetAccount = findAccountByIBAN(accountNumber);
        if (targetAccount == null) {
            return false;
        }
        setAlias(alias, accountNumber);
        return true;
    }

    /**
     * Finds a user by their card number.
     *
     * @param cardNumber the card number
     * @return the user if found, null otherwise
     */
    public User findUserByCard(final String cardNumber) {
        for (Account account : accounts) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return this;
                }
            }
        }
        return null;
    }

    /**
     * Finds a card by its number.
     *
     * @param cardNumber the card number
     * @return the card if found, null otherwise
     */
    public Card findCardByNumber(final String cardNumber) {
        for (Account account : accounts) {
            Card card = account.findCardByNumber(cardNumber);
            if (card != null) {
                return card;
            }
        }
        return null;
    }

    /**
     * Finds the account associated with a specific card.
     *
     * @param card the card to search for
     * @return the account associated with the card, or null if not found
     */
    public Account findAccountByCard(final Card card) {
        for (Account account : accounts) {
            if (account.getCards().contains(card)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Changes the interest rate for a specific account.
     *
     * @param accountNumber the account number
     * @param newInterestRate the new interest rate to set
     * @param timestamp the timestamp of the change
     * @param output the output node to record the change
     * @param objectMapper the object mapper for JSON processing
     * @return true if the interest rate was changed successfully, false otherwise
     */
    public boolean changeAccountInterestRate(final String accountNumber,
                                             final double newInterestRate,
                                             final int timestamp,
                                             final ArrayNode output,
                                             final ObjectMapper objectMapper) {
        Account account = findAccountByIBAN(accountNumber);

        if (account == null || !account.getAccountType().equals("savings")) {
            return false;
        }

        return account.changeInterestRate(newInterestRate, timestamp, this, output, objectMapper);
    }

    /**
     * Adds a split payment transaction.
     *
     * @param description the description of the transaction
     * @param timestamp the timestamp of the transaction
     * @param amount the amount involved in the transaction
     * @param currency the currency of the transaction
     * @param involvedIBANs the list of IBANs involved in the transaction
     */
    public void addSplitPaymentTransaction(final String description,
                                           final int timestamp,
                                           final double amount,
                                           final String currency,
                                           final List<String> involvedIBANs) {
        Transaction splitPaymentTransaction = new Transaction(
                "splitPayment",
                timestamp,
                description
        );
        splitPaymentTransaction.setCurrency(currency);
        splitPaymentTransaction.setAmount(amount);
        splitPaymentTransaction.setInvolvedIBANs(involvedIBANs);
        this.transactions.add(splitPaymentTransaction);
    }

    /**
     * Filters transactions for a report based on account and timestamps.
     *
     * @param account the account to filter transactions for
     * @param startTimestamp the start timestamp of the filter range
     * @param endTimestamp the end timestamp of the filter range
     * @return an array node containing the filtered transactions
     */
    public ArrayNode filterTransactionsForReport(final Account account,
                                                 final int startTimestamp,
                                                 final int endTimestamp) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode transactionsArray = objectMapper.createArrayNode();

        for (Transaction transaction : this.getTransactions()) {
            if (transaction.isWithinTimestamp(startTimestamp, endTimestamp)
                    && transaction.isRelevantToAccountType(account)) {
                transactionsArray.add(transaction.toReportNode(objectMapper));
            }
        }
        return transactionsArray;
    }

    /**
     * Filters spending transactions based on account and timestamps.
     *
     * @param account the account to filter transactions for
     * @param startTimestamp the start timestamp of the filter range
     * @param endTimestamp the end timestamp of the filter range
     * @return an array node containing the filtered spending transactions
     */
    public ArrayNode filterSpendingsTransactions(final Account account,
                                                 final int startTimestamp,
                                                 final int endTimestamp) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode transactionsArray = objectMapper.createArrayNode();

        for (Transaction transaction : this.getTransactions()) {
            if (transaction.isWithinTimestamp(startTimestamp, endTimestamp)
                    && transaction.isSpendingRelevant(account)) {
                transactionsArray.add(transaction.toSpendingsReportNode(objectMapper));
            }
        }
        return transactionsArray;
    }

    /**
     * Returns a string representation of the Transaction object.
     *
     * @return a string containing the account number, amount,
     * description, and timestamp of the transaction
     */
    @Override
    public String toString() {
        return "User{"
                + "firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", email='" + email + '\''
                + ", accounts=" + accounts
                + ", cards=" + cards
                + ", transactions=" + transactions
                + '}';
    }
}
