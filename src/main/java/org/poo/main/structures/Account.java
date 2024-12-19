package org.poo.main.structures;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.main.CommandProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Represents a bank account with attributes such as account number, currency, type,
 * balance, and interest rate. Provides methods for managing cards, processing
 * transactions, and generating reports.
 */
public class Account {
    private String accountNumber;
    private String currency;
    private String accountType;
    private double balance;
    private double interestRate;
    private double minBalance;
    private List<Card> cards;

    /**
     * Constructs an Account instance.
     *
     * @param accountNumber the account number.
     * @param currency      the currency of the account.
     * @param accountType   the type of account (e.g., savings, checking).
     * @param interestRate  the interest rate for the account.
     */
    public Account(final String accountNumber,
                   final String currency,
                   final String accountType,
                   final double interestRate) {
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.accountType = accountType;
        this.balance = 0.0;
        this.interestRate = interestRate;
        this.minBalance = 0.0;
        this.cards = new ArrayList<>();
    }

    /**
     * Gets the account number.
     *
     * @return the account number.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Gets the account currency.
     *
     * @return the account currency.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Gets the account type.
     *
     * @return the account type.
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * Gets the account balance.
     *
     * @return the account balance.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the account balance.
     *
     * @param balance the new balance.
     */
    public void setBalance(final double balance) {
        this.balance = balance;
    }

    /**
     * Gets the interest rate of the account.
     *
     * @return the interest rate.
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Sets the interest rate of the account.
     *
     * @param interestRate the new interest rate.
     */
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * Gets the minimum balance requirement.
     *
     * @return the minimum balance.
     */
    public double getMinBalance() {
        return minBalance;
    }

    /**
     * Sets the minimum balance requirement.
     *
     * @param minBalance the new minimum balance.
     */
    public void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
    }

    /**
     * Gets the list of cards associated with the account.
     *
     * @return the list of cards.
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Adds a card to the account.
     *
     * @param card the card to add.
     */
    public void addCard(final Card card) {
        this.cards.add(card);
    }

    /**
     * Deletes a card from the account by its card number.
     *
     * @param cardNumber the card number to delete.
     * @return true if the card was successfully deleted; false otherwise.
     */
    public boolean deleteCardByNumber(final String cardNumber) {
        return cards.removeIf(card -> card.getCardNumber().equals(cardNumber));
    }

    /**
     * Clears all cards associated with the account.
     */
    public void clearCards() {
        this.cards.clear();
    }

    /**
     * Processes a transaction using a card associated with the account.
     *
     * @param command         the transaction command input.
     * @param responseNode    the response node for transaction details.
     * @param user            the user initiating the transaction.
     * @param commandProcessor the command processor for currency conversion rates.
     * @return true if the transaction was processed successfully; false otherwise.
     */
    public boolean processCardTransaction(final CommandInput command,
                                          final ObjectNode responseNode,
                                          final User user,
                                          final CommandProcessor commandProcessor) {
        for (Card card : this.cards) {
            if (card.getCardNumber().equals(command.getCardNumber())) {
                if (card.isFrozen()) {
                    Transaction frozenTransaction =
                            Transaction.createFrozenTransaction(command.getTimestamp(),
                                    this.accountNumber);
                    user.addTransaction(frozenTransaction);
                    return true;
                }

                double transactionAmount = calculateTransactionAmount(command, commandProcessor);
                if (transactionAmount < 0) {
                    return true;
                }

                if (!isBalanceSufficient(transactionAmount)) {
                    Transaction insufficientFundsTransaction =
                            Transaction.createInsufficientFundsTransaction(command.getTimestamp(),
                                    this.accountNumber);
                    user.addTransaction(insufficientFundsTransaction);
                    return true;
                }

                this.updateBalance(transactionAmount);
                Transaction successfulTransaction = Transaction.createSuccessfulTransaction(command,
                        this.accountNumber, transactionAmount);
                user.addTransaction(successfulTransaction);

                if (card.isOneTime()) {
                    card.regenerateCardNumber(user, command.getTimestamp(), this.accountNumber);
                }

                responseNode.put("timestamp", command.getTimestamp());
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the transaction amount, converting currencies if needed.
     *
     * @param command         the transaction command.
     * @param commandProcessor the processor to fetch exchange rates.
     * @return the calculated transaction amount, or -1 if conversion fails.
     */
    private double calculateTransactionAmount(final CommandInput command,
                                              final CommandProcessor commandProcessor) {
        if (command.getCurrency().equalsIgnoreCase(this.currency)) {
            return command.getAmount();
        }

        double conversionRate = commandProcessor.getExchangeRateFromTo(command.getCurrency(),
                this.currency);
        if (conversionRate == 0) {
            return -1;
        }

        return command.getAmount() * conversionRate;
    }

    /**
     * Checks if the account balance is sufficient for a transaction.
     *
     * @param transactionAmount the transaction amount.
     * @return true if the balance is sufficient; false otherwise.
     */
    private boolean isBalanceSufficient(final double transactionAmount) {
        return this.balance >= transactionAmount;
    }

    /**
     * Updates the account balance by subtracting the transaction amount.
     *
     * @param transactionAmount the transaction amount to subtract.
     */
    private void updateBalance(final double transactionAmount) {
        this.balance -= transactionAmount;
    }

    /**
     * Checks if the account can send the specified amount.
     *
     * @param amount the amount to send.
     * @return true if the account has sufficient balance; false otherwise.
     */
    public boolean canSendFunds(final double amount) {
        return this.balance >= amount;
    }

    /**
     * Decreases the account balance by the specified amount.
     *
     * @param amount the amount to decrease.
     */
    public void decreaseBalance(final double amount) {
        this.balance -= amount;
    }

    /**
     * Increases the account balance by the specified amount.
     *
     * @param amount the amount to increase.
     */
    public void increaseBalance(final double amount) {
        this.balance += amount;
    }

    /**
     * Converts the specified amount to the account's currency, if necessary.
     *
     * @param amount          the amount to convert.
     * @param targetCurrency  the target currency.
     * @param rateProvider    the conversion rate provider.
     * @return the converted amount, or -1 if conversion fails.
     */
    public double convertAmountIfNecessary(final double amount,
                                           final String targetCurrency,
                                           final ConversionRateProvider rateProvider) {
        if (this.currency.equalsIgnoreCase(targetCurrency)) {
            return amount;
        }

        double conversionRate = rateProvider.getRate(this.currency, targetCurrency);
        if (conversionRate == 0) {
            return -1;
        }

        double convertedAmount = amount * conversionRate;
        return convertedAmount;
    }

    /**
     * Finds a card by its card number.
     *
     * @param cardNumber the card number to find.
     * @return the card if found, or null otherwise.
     */
    public Card findCardByNumber(final String cardNumber) {
        for (Card card : cards) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        return null;
    }

    /**
     * Checks if the account balance is below the minimum balance.
     *
     * @return true if the balance is below the minimum; false otherwise.
     */
    public boolean isBelowMinimumBalance() {
        return balance <= minBalance;
    }

    /**
     * Changes the interest rate for the account if it is a savings account.
     *
     * @param newInterestRate the new interest rate.
     * @param timestamp       the timestamp of the change.
     * @param user            the user making the change.
     * @param output          the output node to log changes.
     * @param objectMapper    the object mapper for JSON operations.
     * @return true if the interest rate was changed; false otherwise.
     */
    public boolean changeInterestRate(final double newInterestRate,
                                      final int timestamp,
                                      final User user,
                                      final ArrayNode output,
                                      final ObjectMapper objectMapper) {
        if (!"savings".equalsIgnoreCase(this.accountType)) {
            return false;
        }

        this.interestRate = newInterestRate;

        String description = "Interest rate changed to " + newInterestRate + "%";
        Transaction changeInterestRateTransaction = new Transaction("changeInterestRate",
                timestamp, description);
        user.addTransaction(changeInterestRateTransaction);
        return true;
    }

    /**
     * Converts an amount to another currency using a conversion rate provider.
     *
     * @param amount         the amount to convert.
     * @param targetCurrency the target currency.
     * @param rateProvider   the conversion rate provider.
     * @return the converted amount, or -1 if conversion fails.
     */
    public double convertCurrency(final double amount,
                                  final String targetCurrency,
                                  final ConversionRateProvider rateProvider) {
        if (this.currency.equalsIgnoreCase(targetCurrency)) {
            return amount;
        }
        double rate = rateProvider.getRate(this.currency, targetCurrency);
        return rate > 0 ? amount * rate : -1;
    }

    /**
     * Debits a converted amount from the account balance.
     *
     * @param amount       the amount to debit.
     * @param currency     the currency of the amount.
     * @param rateProvider the conversion rate provider.
     */
    public void debitConvertedAmount(final double amount,
                                     final String currency,
                                     final ConversionRateProvider rateProvider) {
        double convertedAmount = this.convertCurrency(amount, currency, rateProvider);

        if (convertedAmount > 0 && this.hasSufficientBalance(convertedAmount)) {
            this.debit(convertedAmount);
        }
    }

    /**
     * Checks if the account has sufficient balance.
     *
     * @param amount the amount to check.
     * @return true if the balance is sufficient; false otherwise.
     */
    public boolean hasSufficientBalance(final double amount) {
        return balance >= amount;
    }

    /**
     * Debits a specific amount from the account balance.
     *
     * @param amount the amount to debit.
     */
    public void debit(final double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
        }
    }

    /**
     * Generates a report for the account transactions.
     *
     * @param user          the user owning the account.
     * @param startTimestamp the start timestamp for the report.
     * @param endTimestamp   the end timestamp for the report.
     * @return a JSON node representing the account report.
     */
    public ObjectNode generateAccountReport(final User user,
                                            final int startTimestamp,
                                            final int endTimestamp) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode reportOutput = objectMapper.createObjectNode();
        reportOutput.put("IBAN", this.getAccountNumber());
        reportOutput.put("balance", this.getBalance());
        reportOutput.put("currency", this.getCurrency());

        ArrayNode transactionsArray = user.filterTransactionsForReport(this,
                startTimestamp, endTimestamp);
        reportOutput.set("transactions", transactionsArray);

        return reportOutput;
    }

    /**
     * Generates a spending report for the account.
     *
     * @param user          the user owning the account.
     * @param startTimestamp the start timestamp for the report.
     * @param endTimestamp   the end timestamp for the report.
     * @return a JSON node representing the spending report.
     */
    public ObjectNode generateSpendingsReport(final User user,
                                              final int startTimestamp,
                                              final int endTimestamp) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode reportOutput = objectMapper.createObjectNode();
        reportOutput.put("IBAN", this.getAccountNumber());
        reportOutput.put("balance", this.getBalance());
        reportOutput.put("currency", this.getCurrency());

        ArrayNode transactionsArray = user.filterSpendingsTransactions(this,
                startTimestamp, endTimestamp);
        reportOutput.set("transactions", transactionsArray);

        ArrayNode commerciantsArray = this.calculateCommerciantTotals(transactionsArray);
        reportOutput.set("commerciants", commerciantsArray);

        return reportOutput;
    }

    /**
     * Calculates the total spendings per commerciant from transactions.
     *
     * @param transactionsArray the array of transactions.
     * @return a JSON array node of commerciant totals.
     */
    private ArrayNode calculateCommerciantTotals(final ArrayNode transactionsArray) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Double> commerciantTotals = new HashMap<>();

        for (int i = 0; i < transactionsArray.size(); i++) {
            ObjectNode transactionNode = (ObjectNode) transactionsArray.get(i);
            String commerciant = transactionNode.get("commerciant").asText();
            double amount = transactionNode.get("amount").asDouble();

            commerciantTotals.put(commerciant,
                    commerciantTotals.getOrDefault(commerciant, 0.0) + amount);
        }

        ArrayNode commerciantsArray = objectMapper.createArrayNode();
        List<String> sortedCommerciants = new ArrayList<>(commerciantTotals.keySet());
        Collections.sort(sortedCommerciants);

        for (String commerciant : sortedCommerciants) {
            ObjectNode commerciantNode = objectMapper.createObjectNode();
            commerciantNode.put("commerciant", commerciant);
            commerciantNode.put("total", commerciantTotals.get(commerciant));
            commerciantsArray.add(commerciantNode);
        }

        return commerciantsArray;
    }


    /**
     * Returns a string representation of the Account object.
     *
     * @return a string containing the account number, currency, account type, balance,
     * min balance, and cards
     */
    @Override
    public String toString() {
        return "Account{"
                + "accountNumber='" + accountNumber + '\''
                + ", currency='" + currency + '\''
                + ", accountType='" + accountType + '\''
                + ", balance=" + balance
                + ", interestRate=" + interestRate
                + ", minBalance=" + minBalance
                + ", cards=" + cards
                + '}';
    }
}
