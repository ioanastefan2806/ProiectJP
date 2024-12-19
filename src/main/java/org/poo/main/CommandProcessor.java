
package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.main.structures.*;
import org.poo.fileio.*;
import org.poo.utils.Utils;
import java.util.*;

/**
 * The CommandProcessor class is responsible for processing various commands related to users,
 * accounts, transactions, and other operations in the system.
 */
public final class CommandProcessor {

    private static CommandProcessor instance;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ArrayNode output;
    private final Map<String, User> usersMap = new LinkedHashMap<>();
    private final List<ExchangeRate> exchangeRates = new ArrayList<>();

    /**
     * Private constructor to enforce singleton pattern.
     *
     * @param output The output ArrayNode for storing results.
     */
    private CommandProcessor(final ArrayNode output) {
        this.output = output;
    }

    /**
     * Retrieves the singleton instance of CommandProcessor.
     *
     * @param output The output ArrayNode for storing results.
     * @return The singleton instance of CommandProcessor.
     */
    public static CommandProcessor getInstance(final ArrayNode output) {
        if (instance == null) {
            instance = new CommandProcessor(output);
        }
        return instance;
    }

    /**
     * Resets the singleton instance of CommandProcessor.
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Initializes the users and exchange rates based on the provided input.
     *
     * @param users A list of user input data.
     * @param rates A list of exchange rate input data.
     */
    public void initializeUsersAndExchangeRates(final List<UserInput> users,
                                                final List<ExchangeInput> rates) {
        for (UserInput user : users) {
            usersMap.put(user.getEmail(), new User(user.getFirstName(),
                    user.getLastName(), user.getEmail()));
        }

        for (ExchangeInput rate : rates) {
            exchangeRates.add(new ExchangeRate(rate.getFrom(), rate.getTo(), rate.getRate()));
            exchangeRates.add(new ExchangeRate(rate.getTo(), rate.getFrom(), 1 / rate.getRate()));
        }
    }

    /**
     * Processes a list of commands and performs corresponding actions.
     *
     * @param inputData The input data containing users, exchange rates, and commands.
     */
    public void processCommands(final ObjectInput inputData) {
        initializeUsersAndExchangeRates(Arrays.asList(inputData.getUsers()),
                Arrays.asList(inputData.getExchangeRates()));

        for (CommandInput command : inputData.getCommands()) {
            switch (command.getCommand()) {
                case "printUsers":
                    handlePrintUsers(command.getTimestamp());
                    break;
                case "addAccount":
                    handleAddAccount(command);
                    break;
                case "createCard":
                    handleCreateCard(command);
                    break;
                case "createOneTimeCard":
                    handleCreateOneTimeCard(command);
                    break;
                case "addFunds":
                    handleAddFunds(command);
                    break;
                case "deleteAccount":
                    handleDeleteAccount(command);
                    break;
                case "deleteCard":
                    handleDeleteCard(command);
                    break;
                case "setMinimumBalance":
                    handleSetMinBalance(command);
                    break;
                case "payOnline":
                    handlePayOnline(command);
                    break;
                case "sendMoney":
                    handleSendMoney(command);
                    break;
                case "printTransactions":
                    handlePrintTansactions(command);
                    break;
                case "setAlias":
                    handleSetAlias(command);
                    break;
                case "checkCardStatus":
                    handleCheckCardStatus(command);
                    break;
                case "changeInterestRate":
                    handleChangeInterestRate(command);
                    break;
                case "splitPayment":
                    handleSplitPayment(command);
                    break;
                case "report":
                    handleReport(command);
                    break;
                case "spendingsReport":
                    handleSpendingsReport(command);
                    break;
                default:
                    handleUnknownCommand(command);
            }
        }
    }

    /**
     * Handles the "printUsers" command, printing the list of users and their accounts.
     *
     * @param timestamp The timestamp of the command.
     */
    private void handlePrintUsers(final int timestamp) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "printUsers");

        ArrayNode usersArray = objectMapper.createArrayNode();
        for (User user : usersMap.values()) {
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("firstName", user.getFirstName());
            userNode.put("lastName", user.getLastName());
            userNode.put("email", user.getEmail());

            ArrayNode accountsArray = objectMapper.createArrayNode();
            for (Account account : user.getAccounts()) {
                ObjectNode accountNode = objectMapper.createObjectNode();
                accountNode.put("IBAN", account.getAccountNumber());
                accountNode.put("balance", account.getBalance());
                accountNode.put("currency", account.getCurrency());
                accountNode.put("type", account.getAccountType());

                ArrayNode cardsArray = objectMapper.createArrayNode();
                for (Card card : account.getCards()) {
                    ObjectNode cardNode = objectMapper.createObjectNode();
                    cardNode.put("cardNumber", card.getCardNumber());
                    cardNode.put("status", card.getStatus());
                    cardsArray.add(cardNode);
                }
                accountNode.putPOJO("cards", cardsArray);
                accountsArray.add(accountNode);
            }

            userNode.putPOJO("accounts", accountsArray);
            usersArray.add(userNode);
        }

        objectNode.putPOJO("output", usersArray);
        objectNode.put("timestamp", timestamp);
        output.add(objectNode);
    }

    /**
     * Handles the "printTransactions" command, printing the list of transactions for a user.
     *
     * @param command The command containing the user's email and timestamp.
     */
    private void handlePrintTansactions(final CommandInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "printTransactions");

        User user = usersMap.get(command.getEmail());
        if (user == null) {
            ArrayNode emptyArray = objectMapper.createArrayNode();
            objectNode.set("output", emptyArray);
            objectNode.put("timestamp", command.getTimestamp());
            output.add(objectNode);
            return;
        }

        ArrayNode transactionsArray = objectMapper.createArrayNode();
        for (Transaction transaction : user.getTransactions()) {
            ObjectNode transactionNode = objectMapper.createObjectNode();
            String desc = transaction.getType();

            switch (desc) {
                case "addAccount":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    break;

                case "addCard":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    transactionNode.put("card", transaction.getCardNumber());
                    transactionNode.put("cardHolder", transaction.getEmail());
                    transactionNode.put("account", transaction.getAccountNumber());
                    break;

                case "sendMoney":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    transactionNode.put("senderIBAN", transaction.getSenderIBAN());
                    transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());

                    String amountWithCurrency = transaction.getAmount()
                            + " " + transaction.getCurrency();
                    transactionNode.put("amount", amountWithCurrency);

                    String transferType = user.getEmail().equals(transaction.getEmail())
                            ? "sent" : "received";
                    transactionNode.put("transferType", transferType);
                    break;

                case "paySucessful":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    double amount = transaction.getAmount();
                    transactionNode.put("amount", amount);
                    transactionNode.put("commerciant", transaction.getCommerciant());
                    break;

                case "payNoFunds":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    break;

                case "PayOnetime":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    transactionNode.put("card", transaction.getCardNumber());
                    transactionNode.put("cardHolder", transaction.getEmail());
                    transactionNode.put("account", transaction.getAccountNumber());
                    break;

                case "deleteCard":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    transactionNode.put("card", transaction.getCardNumber());
                    transactionNode.put("cardHolder", transaction.getEmail());
                    transactionNode.put("account", transaction.getAccountNumber());
                    break;

                case "checkCardStatus":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    break;
                case "payFrosen":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    break;
                case "changeInterestRate":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    break;

                case "splitPayment":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                    transactionNode.put("currency", transaction.getCurrency());
                    transactionNode.put("amount", transaction.getAmount());

                    ArrayNode involvedAccountsArray = objectMapper.createArrayNode();
                    for (String iban : transaction.getInvolvedIBANs()) {
                        involvedAccountsArray.add(iban);
                    }
                    transactionNode.set("involvedAccounts", involvedAccountsArray);
                    break;

                default:
                    break;
            }
            transactionsArray.add(transactionNode);
        }

        objectNode.set("output", transactionsArray);
        objectNode.put("timestamp", command.getTimestamp());
        output.add(objectNode);
    }

    /**
     * Handles the addition of a new account for a user.
     *
     * @param command The command containing the user's email and account details.
     */
    private void handleAddAccount(final CommandInput command) {
        User user = usersMap.get(command.getEmail());
        if (user != null) {
            user.addAccount(command);
        }
    }

    /**
     * Handles the creation of a card for a user.
     *
     * @param command The command containing the user's email, account number, and timestamp.
     */
    private void handleCreateCard(final CommandInput command) {
        String email = command.getEmail();
        String accountNumber = command.getAccount();
        int timestamp = command.getTimestamp();

        User user = usersMap.get(email);

        if (user == null) {
            return;
        }
        user.createCard(accountNumber, timestamp);
    }

    /**
     * Handles the creation of a one-time card for a user.
     *
     * @param command The command containing the user's email and card details.
     */
    private void handleCreateOneTimeCard(final CommandInput command) {
        User user = usersMap.get(command.getEmail());
        if (user != null) {
            user.createOneTimeCard(command);
        }
    }

    /**
     * Adds funds to an account.
     *
     * @param command The command containing the account number and amount to be added.
     */
    private void handleAddFunds(final CommandInput command) {
        for (User user : usersMap.values()) {
            for (Account account : user.getAccounts()) {
                if (account.getAccountNumber().equals(command.getAccount())) {
                    double newBalance = account.getBalance() + command.getAmount();
                    account.setBalance(newBalance);
                    break;
                }
            }
        }
    }

    /**
     * Handles the deletion of an account for a user.
     *
     * @param command The command containing the user's email and account details.
     */
    private void handleDeleteAccount(final CommandInput command) {
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("command", "deleteAccount");

        if (command.getEmail() == null || !usersMap.containsKey(command.getEmail())) {
            addErrorToResponse(responseNode, "User not found", command.getTimestamp());
            return;
        }

        User user = usersMap.get(command.getEmail());

        boolean deleted = user.deleteAccount(command.getAccount());

        ObjectNode outputNode = objectMapper.createObjectNode();
        if (deleted) {
            outputNode.put("success", "Account deleted");
        } else {
            outputNode.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");
        }
        outputNode.put("timestamp", command.getTimestamp());

        responseNode.set("output", outputNode);
        responseNode.put("timestamp", command.getTimestamp());
        output.add(responseNode);
    }

    /**
     * Adds an error message to the response node.
     *
     * @param responseNode The response node to which the error will be added.
     * @param errorMessage The error message to add.
     * @param timestamp    The timestamp of the command.
     */
    private void addErrorToResponse(final ObjectNode responseNode,
                                    final String errorMessage,
                                    final int timestamp) {
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("error", errorMessage);
        outputNode.put("timestamp", timestamp);

        responseNode.set("output", outputNode);
        output.add(responseNode);
    }

    /**
     * Handles the deletion of a card for a user.
     *
     * @param command The command containing the user's email, card number, and timestamp.
     */
    private void handleDeleteCard(final CommandInput command) {
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("command", "deleteCard");
        responseNode.put("timestamp", command.getTimestamp());

        if (command.getEmail() == null || !usersMap.containsKey(command.getEmail())) {
            addErrorToResponse(responseNode, "User not found", command.getTimestamp());
            return;
        }

        if (command.getCardNumber() == null || command.getCardNumber().isEmpty()) {
            addErrorToResponse(responseNode, "Card number is missing", command.getTimestamp());
            return;
        }

        User user = usersMap.get(command.getEmail());

        Transaction deleteCardTransaction =
                user.deleteCard(
                        command.getCardNumber(), command.getTimestamp());

        if (deleteCardTransaction != null) {
            user.addTransaction(deleteCardTransaction);
        } else {
            addErrorToResponse(responseNode, "Card not found", command.getTimestamp());
        }
    }

    /**
     * Handles setting the minimum balance for a specific account.
     *
     * @param command The command containing the account details and the minimum balance amount.
     */
    private void handleSetMinBalance(final CommandInput command) {
        if (command.getAccount() == null || command.getAccount().isEmpty()) {
            return;
        }
        if (command.getAmount() <= 0) {
            return;
        }

        for (User user : usersMap.values()) {
            if (user.setAccountMinBalance(command.getAccount(), command.getAmount())) {
                return;
            }
        }
    }

    /**
     * Handles processing an online payment command.
     *
     * @param command The command containing details of the payment,
     *                such as card number, amount, and currency.
     */
    private void handlePayOnline(final CommandInput command) {
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("command", "payOnline");

        if (isInvalidCommand(command)) {
            addCardNotFoundOutput(responseNode, command.getTimestamp());
            return;
        }

        User user = usersMap.get(command.getEmail());
        if (user == null) {
            addCardNotFoundOutput(responseNode, command.getTimestamp());
            return;
        }

        boolean cardFound = user.processTransaction(command, responseNode, this);
        if (!cardFound) {
            addCardNotFoundOutput(responseNode, command.getTimestamp());
        }
    }

    /**
     * Checks if the provided command is invalid.
     *
     * @param command The command to validate.
     * @return True if the command is invalid, otherwise false.
     */
    private boolean isInvalidCommand(final CommandInput command) {
        return command.getCardNumber() == null || command.getCardNumber().isEmpty()
                || command.getAmount() <= 0 || command.getCurrency() == null;
    }

    /**
     * Adds a "Card not found" error message to the response node.
     *
     * @param responseNode The response node to which the error will be added.
     * @param timestamp    The timestamp of the command.
     */
    private void addCardNotFoundOutput(final ObjectNode responseNode,
                                       final int timestamp) {
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("timestamp", timestamp);
        outputNode.put("description", "Card not found");
        responseNode.set("output", outputNode);
        responseNode.put("timestamp", timestamp);
        output.add(responseNode);
    }

    /**
     * Retrieves the exchange rate between two currencies.
     *
     * @param from The currency to convert from.
     * @param to   The currency to convert to.
     * @return The exchange rate, or 0 if no rate is found.
     */
    public double getExchangeRateFromTo(final String from,
                                        final String to) {
        return findExchangeRate(from, to, new HashSet<>());
    }

    /**
     * Recursively finds the exchange rate between two currencies, considering
     * intermediate conversions.
     *
     * @param from    The starting currency.
     * @param to      The target currency.
     * @param visited A set of visited currencies to prevent cyclic searches.
     * @return The calculated exchange rate, or 0 if no valid conversion is found.
     */
    private double findExchangeRate(final String from,
                                    final String to,
                                    final Set<String> visited) {
        if (from.equalsIgnoreCase(to)) {
            return 1.0;
        }

        visited.add(from.toUpperCase());
        for (ExchangeRate exchangeRate : exchangeRates) {
            if (exchangeRate.getFromCurrency().equalsIgnoreCase(from)
                    && exchangeRate.getToCurrency().equalsIgnoreCase(to)) {
                return exchangeRate.getRate();
            }
        }

        for (ExchangeRate exchangeRate : exchangeRates) {
            if (exchangeRate.getFromCurrency().equalsIgnoreCase(from)
                    && !visited.contains(exchangeRate.getToCurrency().toUpperCase())) {
                double intermediateRate =
                        findExchangeRate(exchangeRate.getToCurrency(), to, visited);
                if (intermediateRate > 0) {
                    return exchangeRate.getRate() * intermediateRate;
                }
            }
        }
        return 0;
    }

    /**
     * Handles the process of sending money between accounts.
     *
     * @param command The command containing sender and receiver details, as well
     *                as the amount to transfer.
     */
    private void handleSendMoney(final CommandInput command) {
        User senderUser = usersMap.get(command.getEmail());
        if (senderUser == null) {
            return;
        }

        Account senderAccount = senderUser.findAccountByIBAN(command.getAccount());
        if (senderAccount == null) {
            return;
        }

        Account receiverAccount = findAccountByIBANGlobally(command.getReceiver());
        if (receiverAccount == null) {
            return;
        }

        if (!senderAccount.canSendFunds(command.getAmount())) {
            senderUser.addTransaction(
                    Transaction.createInsufficientFundsTransaction(
                            command.getTimestamp(), senderAccount.getAccountNumber()));
            return;
        }

        double convertedAmount =
                senderAccount.convertAmountIfNecessary(
                        command.getAmount(), receiverAccount.getCurrency(),
                        this::getExchangeRateFromTo);
        if (convertedAmount < 0) {
            return;
        }

        senderAccount.decreaseBalance(command.getAmount());
        receiverAccount.increaseBalance(convertedAmount);

        senderUser.addTransaction(
                Transaction.createSendMoneyTransaction(
                        command, senderAccount, receiverAccount));
        User receiverUser = findUserByAccount(receiverAccount.getAccountNumber());
        if (receiverUser != null) {
            receiverUser.addTransaction(
                    Transaction.createReceiveMoneyTransaction(
                            command, senderAccount, receiverAccount));
        }
    }

    /**
     * Finds an account globally by its IBAN.
     *
     * @param iban The IBAN of the account to find.
     * @return The account if found, otherwise null.
     */
    public Account findAccountByIBANGlobally(final String iban) {
        for (User user : usersMap.values()) {
            Account account = user.findAccountByIBAN(iban);
            if (account != null) {
                return account;
            }
        }
        return null;
    }

    /**
     * Finds a user by an account's IBAN.
     *
     * @param iban The IBAN of the account to find the associated user.
     * @return The user if found, otherwise null.
     */
    public User findUserByAccount(final String iban) {
        for (User user : usersMap.values()) {
            if (user.findAccountByIBAN(iban) != null) {
                return user;
            }
        }
        return null;
    }

    /**
     * Handles setting an alias for an account.
     *
     * @param command The command containing the alias and account details.
     */
    private void handleSetAlias(final CommandInput command) {
        User user = usersMap.get(command.getEmail());
        if (user == null) {
            return;
        }

        if (!user.setAliasForAccount(command.getAlias(), command.getAccount())) {
            return;
        }
    }

    /**
     * Handles checking the status of a card.
     *
     * @param command The command containing card details and a timestamp.
     */
    private void handleCheckCardStatus(final CommandInput command) {
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("command", "checkCardStatus");

        if (command.getCardNumber() == null || command.getCardNumber().isEmpty()) {
            addCheckCardStatusOutput(responseNode, command.getTimestamp(), "Card not found");
            return;
        }

        User user = findUserByCard(command.getCardNumber());
        if (user == null) {
            addCheckCardStatusOutput(responseNode, command.getTimestamp(), "Card not found");
            return;
        }

        Card card = user.findCardByNumber(command.getCardNumber());
        if (card == null || "frozen".equals(card.getStatus())) {
            return;
        }

        Account account = user.findAccountByCard(card);
        if (account == null) {
            return;
        }

        if (account.isBelowMinimumBalance()) {
            card.freeze();
            user.addTransaction(
                    Transaction.createFreezeTransaction(command.getTimestamp(),
                            account, card));
        }
    }

    /**
     * Adds a "Card not found" error message to the response.
     *
     * @param responseNode The response node to which the error will be added.
     * @param timestamp    The timestamp of the command.
     * @param description  The description of the error.
     */
    private void addCheckCardStatusOutput(final ObjectNode responseNode,
                                          final int timestamp,
                                          final String description) {
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("timestamp", timestamp);
        outputNode.put("description", description);
        responseNode.set("output", outputNode);
        responseNode.put("timestamp", timestamp);
        output.add(responseNode);
    }

    /**
     * Finds a user by a card number.
     *
     * @param cardNumber The card number to find the associated user.
     * @return The user if found, otherwise null.
     */
    private User findUserByCard(final String cardNumber) {
        for (User user : usersMap.values()) {
            if (user.findCardByNumber(cardNumber) != null) {
                return user;
            }
        }
        return null;
    }

    /**
     * Handles changing the interest rate for an account.
     *
     * @param command The command containing account details, interest rate, and timestamp.
     */
    private void handleChangeInterestRate(final CommandInput command) {
        User user = usersMap.get(command.getEmail());
        if (user == null) {
            return;
        }


        boolean success = user.changeAccountInterestRate(
                command.getAccount(),
                command.getInterestRate(),
                command.getTimestamp(),
                output,
                objectMapper
        );

        if (!success) {
            ObjectNode responseNode = objectMapper.createObjectNode();
            responseNode.put("command", "changeInterestRate");

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", "This is not a savings account");

            responseNode.set("output", outputNode);
            responseNode.put("timestamp", command.getTimestamp());
            output.add(responseNode);
        }
    }

    /**
     * Handles splitting a payment among multiple accounts.
     *
     * @param command The command containing the accounts, total amount, currency, and timestamp.
     */
    private void handleSplitPayment(final CommandInput command) {
        List<String> accountsForSplit = command.getAccounts();
        double totalAmount = command.getAmount();
        String currency = command.getCurrency();
        int timestamp = command.getTimestamp();

        if (accountsForSplit.isEmpty()) {
            return;
        }

        double share = totalAmount / accountsForSplit.size();
        List<Account> accounts = new ArrayList<>();
        List<User> involvedUsers = new ArrayList<>();

        if (!prepareAccountsAndUsers(accountsForSplit, accounts, involvedUsers)) {
            handleSplitPaymentFailure(accountsForSplit, timestamp);
            return;
        }

        if (!validateBalancesAndCurrency(accounts, share, currency)) {
            handleSplitPaymentFailure(accountsForSplit, timestamp);
            return;
        }

        executeSplitPayment(accounts, involvedUsers, share,
                currency, timestamp, accountsForSplit, totalAmount);
    }

    /**
     * Prepares the accounts and users involved in the split payment.
     *
     * @param accountsForSplit The list of account IBANs for the split payment.
     * @param accounts         The list to store valid accounts.
     * @param involvedUsers    The list to store corresponding users of the accounts.
     * @return True if all accounts and users are valid, otherwise false.
     */
    private boolean prepareAccountsAndUsers(final List<String> accountsForSplit,
                                            final List<Account> accounts,
                                            final List<User> involvedUsers) {
        for (String iban : accountsForSplit) {
            Account account = findAccountByIBANGlobally(iban);
            if (account == null) {
                return false;
            }
            accounts.add(account);

            User user = findUserByAccount(iban);
            if (user == null) {
                return false;
            }
            involvedUsers.add(user);
        }
        return true;
    }

    /**
     * Validates that all accounts have sufficient balances and use compatible currencies.
     *
     * @param accounts The list of accounts to validate.
     * @param share    The share amount to be deducted from each account.
     * @param currency The currency of the payment.
     * @return True if all validations pass, otherwise false.
     */
    private boolean validateBalancesAndCurrency(final List<Account> accounts,
                                                final double share,
                                                final String currency) {
        for (Account account : accounts) {
            double convertedShare = calculateConvertedShare(account, share, currency);
            if (convertedShare < 0 || account.getBalance() < convertedShare) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the converted share amount based on the account's currency.
     *
     * @param account  The account for which the share is calculated.
     * @param share    The share amount in the original currency.
     * @param currency The original currency.
     * @return The converted share amount, or -1 if the conversion fails.
     */
    private double calculateConvertedShare(final Account account,
                                           final double share,
                                           final String currency) {
        if (currency.equalsIgnoreCase(account.getCurrency())) {
            return share;
        }

        double rate = getExchangeRateFromTo(currency, account.getCurrency());
        return rate > 0 ? share * rate : -1;
    }

    /**
     * Executes the split payment by deducting shares from accounts and creating transactions.
     *
     * @param accounts     The list of accounts involved in the payment.
     * @param users        The list of users owning the accounts.
     * @param share        The share amount for each account.
     * @param currency     The currency of the payment.
     * @param timestamp    The timestamp of the command.
     * @param involvedIBANs The list of IBANs involved in the payment.
     * @param totalAmount  The total amount of the split payment.
     */
    private void executeSplitPayment(final List<Account> accounts,
                                     final List<User> users,
                                     final double share,
                                     final String currency,
                                     final int timestamp,
                                     final List<String> involvedIBANs,
                                     final double totalAmount) {
        for (Account account : accounts) {
            double convertedShare = calculateConvertedShare(account, share, currency);
            account.setBalance(account.getBalance() - convertedShare);
        }

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            User user = users.get(i);

            String description = String.format("Split payment of %.2f %s", totalAmount, currency);
            Transaction splitPaymentTransaction =
                    new Transaction("splitPayment", timestamp, description);
            splitPaymentTransaction.setCurrency(currency);
            splitPaymentTransaction.setAmount(share);
            splitPaymentTransaction.setInvolvedIBANs(new ArrayList<>(involvedIBANs));
            user.addTransaction(splitPaymentTransaction);
        }
    }

    /**
     * Handles a failure during a split payment by creating failed
     * transactions for affected accounts.
     *
     * @param accountsForSplit The list of account IBANs involved in the failed payment.
     * @param timestamp        The timestamp of the failure.
     */
    private void handleSplitPaymentFailure(final List<String> accountsForSplit,
                                           final int timestamp) {
        for (String iban : accountsForSplit) {
            Account account = findAccountByIBANGlobally(iban);
            if (account == null) {
                continue;
            }
            User user = findUserByAccount(account.getAccountNumber());
            if (user != null) {
                Transaction failedTransaction =
                        new Transaction("payNoFunds", timestamp,
                                "Split payment failed due to insufficient funds");
                user.addTransaction(failedTransaction);
            }
        }
    }

    /**
     * Handles generating a report for a specific account.
     *
     * @param command The command containing account details and report parameters.
     */
    private void handleReport(final CommandInput command) {
        String iban = command.getAccount();
        int startTimestamp = command.getStartTimestamp();
        int endTimestamp = command.getEndTimestamp();
        String reportType = command.getCommand();
        int timestamp = command.getTimestamp();

        Account account = findAccountByIBANGlobally(iban);
        if (account == null) {
            return;
        }

        User user = findUserByAccount(iban);
        if (user == null) {
            return;
        }

        ObjectNode reportOutput = account.generateAccountReport(user, startTimestamp, endTimestamp);

        ObjectNode finalReport = objectMapper.createObjectNode();
        finalReport.put("command", reportType);
        finalReport.set("output", reportOutput);
        finalReport.put("timestamp", timestamp);
        output.add(finalReport);
    }

    /**
     * Handles generating a spendings report for a specific account.
     *
     * @param command The command containing account details and report parameters.
     */
    private void handleSpendingsReport(final CommandInput command) {
        String iban = command.getAccount();
        int startTimestamp = command.getStartTimestamp();
        int endTimestamp = command.getEndTimestamp();
        int timestamp = command.getTimestamp();
        String commandType = command.getCommand();

        Account account = findAccountByIBANGlobally(iban);
        if (account == null) {
            return;
        }

        User user = findUserByAccount(iban);
        if (user == null) {
            return;
        }

        ObjectNode reportOutput = account.generateSpendingsReport(user,
                startTimestamp, endTimestamp);

        ObjectNode finalReport = objectMapper.createObjectNode();
        finalReport.put("command", commandType);
        finalReport.set("output", reportOutput);
        finalReport.put("timestamp", timestamp);

        output.add(finalReport);
    }

    /**
     * Handles an unknown command by adding it to the output with a status message.
     *
     * @param command The unknown command received.
     */
    private void handleUnknownCommand(final CommandInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", command.getCommand());
        objectNode.put("status", "Unknown command");
        output.add(objectNode);
    }
}