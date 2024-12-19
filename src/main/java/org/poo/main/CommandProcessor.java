package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.main.structures.*;
import org.poo.fileio.*;
import org.poo.utils.Utils;
import java.util.*;

public class CommandProcessor {

    private static CommandProcessor instance;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ArrayNode output;
    private final Map<String, User> usersMap = new LinkedHashMap<>();
    private final List<ExchangeRate> exchangeRates = new ArrayList<>();

    private CommandProcessor(ArrayNode output) {
        this.output = output;
    }

    public static CommandProcessor getInstance(ArrayNode output) {
        if (instance == null) {
            instance = new CommandProcessor(output);
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public void initializeUsersAndExchangeRates(List<UserInput> users, List<ExchangeInput> rates) {
        for (UserInput user : users) {
            usersMap.put(user.getEmail(), new User(user.getFirstName(), user.getLastName(), user.getEmail()));
        }

        for (ExchangeInput rate : rates) {
            exchangeRates.add(new ExchangeRate(rate.getFrom(), rate.getTo(), rate.getRate()));
            exchangeRates.add(new ExchangeRate(rate.getTo(), rate.getFrom(), 1 / rate.getRate()));
        }
    }

    public void processCommands(ObjectInput inputData) {
        initializeUsersAndExchangeRates(Arrays.asList(inputData.getUsers()), Arrays.asList(inputData.getExchangeRates()));

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
    private void handlePrintUsers(int timestamp) {
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


    private void handlePrintTansactions(CommandInput command) {
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


                    String amountWithCurrency = transaction.getAmount() + " " + transaction.getCurrency();
                    transactionNode.put("amount", amountWithCurrency);

                    String transferType = user.getEmail().equals(transaction.getEmail()) ? "sent" : "received";
                    transactionNode.put("transferType", transferType);
                    break;

                case "paySucessful":
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());
                {
                    double amount = transaction.getAmount();
                    transactionNode.put("amount", amount);
                }
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

    private void handleAddAccount(CommandInput command) {
        User user = usersMap.get(command.getEmail());
        if (user != null) {
            String iban = Utils.generateIBAN();
            String accountType = command.getAccountType();
            double interestRate = 0.0;

            if ("savings".equals(accountType)) {
                interestRate = command.getInterestRate();
            }

            Account account = new Account(iban, command.getCurrency(), accountType, interestRate);
            user.addAccount(account);

            Transaction transaction = new Transaction("addAccount", command.getTimestamp(), "New account created");
            user.addTransaction(transaction);
        }
    }

    private void handleCreateCard(CommandInput command) {
        String email = command.getEmail();
        String accountNumber = command.getAccount();
        int timestamp = command.getTimestamp();

        User user = usersMap.get(email);

        if (user == null) {
            return;
        }

        Account targetAccount = null;
        for (Account account : user.getAccounts()) {
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
                "New card created", cardNumber, user.getEmail(), targetAccount.getAccountNumber());
        user.addTransaction(transaction);
    }


    private void handleCreateOneTimeCard(CommandInput command) {
        User user = usersMap.get(command.getEmail());
        String accountNbr = null;
        String cardNumber = null;

        if (user != null) {
            for (Account account : user.getAccounts()) {
                if (account.getAccountNumber().equals(command.getAccount())) {
                    cardNumber = Utils.generateCardNumber();
                    accountNbr = account.getAccountNumber();
                    Card card = new Card(cardNumber, account.getAccountNumber(), true);
                    account.addCard(card);
                    break;
                }
            }

            Transaction transaction = new Transaction("addCard", command.getTimestamp(),
                    "New card created", cardNumber, user.getEmail(), accountNbr);
            user.addTransaction(transaction);
        }
    }

    private void handleAddFunds(CommandInput command) {
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

    private void handleDeleteAccount(CommandInput command) {
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("command", "deleteAccount");

        if (command.getEmail() == null || !usersMap.containsKey(command.getEmail())) {
            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("error", "User not found");
            outputNode.put("timestamp", command.getTimestamp());

            responseNode.set("output", outputNode);
            output.add(responseNode);
            return;
        }

        User user = usersMap.get(command.getEmail());
        boolean deleted = user.getAccounts().removeIf(account -> {
            if (!account.getAccountNumber().equals(command.getAccount())) {
                return false;
            }
            if (account.getBalance() != 0) {
                return false;
            }
            account.getCards().clear();
            return true;
        });

        ObjectNode outputNode = objectMapper.createObjectNode();
        if (deleted) {
            outputNode.put("success", "Account deleted");
        } else {
            outputNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
        }
        outputNode.put("timestamp", command.getTimestamp());

        responseNode.set("output", outputNode);
        responseNode.put("timestamp", command.getTimestamp());
        output.add(responseNode);

    }

    private void handleDeleteCard(CommandInput command) {
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("command", "deleteCard");
        responseNode.put("timestamp", command.getTimestamp());


        if (command.getEmail() == null || !usersMap.containsKey(command.getEmail())) {
            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("error", "User not found");
            responseNode.set("output", outputNode);
            output.add(responseNode);
            return;
        }
        if (command.getCardNumber() == null || command.getCardNumber().isEmpty()) {
            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("error", "Card number is missing");
            responseNode.set("output", outputNode);
            output.add(responseNode);
            return;
        }

        User user = usersMap.get(command.getEmail());
        boolean cardDeleted = false;


        Iterator<Card> cardIterator = user.getCards().iterator();
        while (cardIterator.hasNext()) {
            Card card = cardIterator.next();
            if (card.getCardNumber().equals(command.getCardNumber())) {
                cardIterator.remove();
                cardDeleted = true;
                break;
            }
        }

        String deletedCardNumber = null;
        String associatedAccount = null;
        for (Account account : user.getAccounts()) {
            Iterator<Card> accountCardIterator = account.getCards().iterator();
            while (accountCardIterator.hasNext()) {
                Card card = accountCardIterator.next();
                if (card.getCardNumber().equals(command.getCardNumber())) {
                    deletedCardNumber = card.getCardNumber();
                    associatedAccount = card.getAccountNumber();
                    accountCardIterator.remove();
                    cardDeleted = true;
                    break;
                }
            }
        }

        if (cardDeleted) {
            Transaction deleteCardTransaction = new Transaction(
                    "deleteCard",
                    command.getTimestamp(),
                    "The card has been destroyed"

            );
            deleteCardTransaction.setCardNumber(deletedCardNumber);
            deleteCardTransaction.setEmail(user.getEmail());
            if (associatedAccount != null) {
                deleteCardTransaction.setAccountNumber(associatedAccount);
            }
            user.addTransaction(deleteCardTransaction);
        }
    }

    private void handleSetMinBalance(CommandInput command) {
        if (command.getAccount() == null || command.getAccount().isEmpty()) {
            return;
        }
        if (command.getAmount() <= 0) {
            return;
        }

        for (User user : usersMap.values()) {
            for (Account account : user.getAccounts()) {
                if (account.getAccountNumber().equals(command.getAccount())) {
                    account.setMinBalance(command.getAmount());
                    return;
                }
            }
        }
    }

    private void handlePayOnline(CommandInput command) {
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

        boolean cardFound = processTransaction(user, command, responseNode);
        if (!cardFound) {
            addCardNotFoundOutput(responseNode, command.getTimestamp());
        }
    }
    private boolean processTransaction(User user, CommandInput command, ObjectNode responseNode) {
        for (Account account : user.getAccounts()) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(command.getCardNumber())) {
                    if (isCardFrozen(card)) {
                        Transaction payFrosen = new Transaction(
                                "payFrosen",
                                command.getTimestamp(),
                                "The card is frozen"
                        );
                        payFrosen.setAccountNumber(account.getAccountNumber());
                        user.addTransaction(payFrosen);

                        return true;
                    }

                    double transactionAmount = calculateTransactionAmount(command, account);
                    if (transactionAmount < 0) {
                        return true;
                    }

                    if (!isBalanceSufficient(account, transactionAmount)) {
                        Transaction payNoFundsTransaction = new Transaction(
                                "payNoFunds",
                                command.getTimestamp(),
                                "Insufficient funds"
                        );
                        payNoFundsTransaction.setAccountNumber(account.getAccountNumber());
                        user.addTransaction(payNoFundsTransaction);
                        return true;
                    }
                    updateAccountBalance(account, transactionAmount);

                    Transaction paySuccessfulTransaction = new Transaction(
                            "paySucessful",
                            command.getTimestamp(),
                            "Card payment"
                    );
                    paySuccessfulTransaction.setAmount(transactionAmount);
                    paySuccessfulTransaction.setCommerciant(command.getCommerciant());
                    paySuccessfulTransaction.setAccountNumber(account.getAccountNumber());
                    user.addTransaction(paySuccessfulTransaction);

                    if (card.isOneTime()) {
                        handleOneTimeCard(account, card, user, command.getTimestamp());
                    }

                    responseNode.put("timestamp", command.getTimestamp());
                    return true;
                }
            }
        }
        return false;
    }


    private void handleOneTimeCard(Account account, Card card, User user, int timestamp) {
        card.setStatus("frozen");
        String newCardNumber = Utils.generateCardNumber();

        card.setCardNumber(newCardNumber);
        card.setStatus("active");
        Transaction updateCardNumberTransaction = new Transaction(
                "updateOneTimeCard",
                timestamp,
                "One-time card number regenerated"
        );
        updateCardNumberTransaction.setCardNumber(newCardNumber);
        updateCardNumberTransaction.setEmail(user.getEmail());
        updateCardNumberTransaction.setAccountNumber(account.getAccountNumber());
        user.addTransaction(updateCardNumberTransaction);
    }

    private boolean isInvalidCommand(CommandInput command) {
        return command.getCardNumber() == null || command.getCardNumber().isEmpty()
                || command.getAmount() <= 0 || command.getCurrency() == null;
    }

    private void addCardNotFoundOutput(ObjectNode responseNode, int timestamp) {
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("timestamp", timestamp);
        outputNode.put("description", "Card not found");
        responseNode.set("output", outputNode);
        responseNode.put("timestamp", timestamp);
        output.add(responseNode);
    }

    private boolean isCardFrozen(Card card) {
        return "frozen".equalsIgnoreCase(card.getStatus());
    }

    private double calculateTransactionAmount(CommandInput command, Account account) {
        if (command.getCurrency().equalsIgnoreCase(account.getCurrency())) {
            return command.getAmount();
        }

        double conversionRate = getExchangeRateFromTo(command.getCurrency(), account.getCurrency());
        if (conversionRate == 0) {
            return -1;
        }

        double amount = command.getAmount() * conversionRate;
        return amount;
    }

    private boolean isBalanceSufficient(Account account, double transactionAmount) {
        return account.getBalance() >= transactionAmount;
    }

    private void updateAccountBalance(Account account, double transactionAmount) {
        double newBalance = account.getBalance() - transactionAmount;
        account.setBalance(newBalance);
    }

    public double getExchangeRateFromTo(String from, String to) {
        return findExchangeRate(from, to, new HashSet<>());
    }

    private double findExchangeRate(String from, String to, Set<String> visited) {
        if (from.equalsIgnoreCase(to)) {
            return 1.0;
        }

        visited.add(from.toUpperCase());
        for (ExchangeRate exchangeRate : exchangeRates) {
            if (exchangeRate.getFromCurrency().equalsIgnoreCase(from) && exchangeRate.getToCurrency().equalsIgnoreCase(to)) {
                return exchangeRate.getRate();
            }
        }

        for (ExchangeRate exchangeRate : exchangeRates) {
            if (exchangeRate.getFromCurrency().equalsIgnoreCase(from) && !visited.contains(exchangeRate.getToCurrency().toUpperCase())) {
                double intermediateRate = findExchangeRate(exchangeRate.getToCurrency(), to, visited);
                if (intermediateRate > 0) {
                    return exchangeRate.getRate() * intermediateRate;
                }
            }
        }
        return 0;
    }


    private void handleSendMoney(CommandInput command) {
        User senderUser = usersMap.get(command.getEmail());
        if (senderUser == null) {
            return;
        }

        Account senderAccount = findAccountByIBAN(senderUser, command.getAccount());
        if (senderAccount == null) {
            return;
        }

        Account receiverAccount = findAccountByIBANGlobally(command.getReceiver());
        if (receiverAccount == null) {
            return;
        }
        double amount = command.getAmount();

        if (senderAccount.getBalance() < amount) {
            Transaction insufficientFundsTransaction = new Transaction(
                    "payNoFunds",
                    command.getTimestamp(),
                    "Insufficient funds"
            );
            senderUser.addTransaction(insufficientFundsTransaction);
            return;
        }

        double finalAmount = amount;
        String senderCurrency = senderAccount.getCurrency();
        String receiverCurrency = receiverAccount.getCurrency();

        if (!senderCurrency.equals(receiverCurrency)) {
            double rate = getExchangeRateFromTo(senderCurrency, receiverCurrency);
            if (rate == 0) {
                return;
            }
            finalAmount = amount * rate;
            finalAmount = Math.round(finalAmount * 1e9) / 1e9;
        }

        double newSenderBalance = senderAccount.getBalance() - amount;
        newSenderBalance = Math.round(newSenderBalance * 1e9) / 1e9;
        senderAccount.setBalance(newSenderBalance);

        double newReceiverBalance = receiverAccount.getBalance() + finalAmount;
        newReceiverBalance = Math.round(newReceiverBalance * 1e9) / 1e9;
        receiverAccount.setBalance(newReceiverBalance);

        Transaction senderTransaction = new Transaction(
                "sendMoney",
                command.getTimestamp(),
                command.getDescription(),
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber(),
                amount,
                senderCurrency,
                command.getEmail()
        );
        senderUser.addTransaction(senderTransaction);

        Transaction receiverTransaction = new Transaction(
                "sendMoney",
                command.getTimestamp(),
                command.getDescription(),
                senderAccount.getAccountNumber(),
                receiverAccount.getAccountNumber(),
                amount,
                senderCurrency,
                command.getEmail()
        );
        User receiverUser = findUserByAccount(receiverAccount.getAccountNumber());
        if (receiverUser != null) {
            receiverUser.addTransaction(receiverTransaction);
        }
    }


    private Account findAccountByIBAN(User user, String iban) {
        for (Account account : user.getAccounts()) {
            if (account.getAccountNumber().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    private Account findAccountByIBANGlobally(String iban) {
        for (User u : usersMap.values()) {
            for (Account account : u.getAccounts()) {
                if (account.getAccountNumber().equals(iban)) {
                    return account;
                }
            }
        }
        return null;
    }

    private User findUserByAccount(String iban) {
        for (User u : usersMap.values()) {
            for (Account account : u.getAccounts()) {
                if (account.getAccountNumber().equals(iban)) {
                    return u;
                }
            }
        }
        return null;
    }

    private void handleSetAlias(CommandInput command) {
        User user = usersMap.get(command.getEmail());
        if (user == null) {
            return;
        }

        Account targetAccount = null;
        for (Account account : user.getAccounts()) {
            if (account.getAccountNumber().equals(command.getAccount())) {
                targetAccount = account;
                break;
            }
        }

        if (targetAccount == null) {
            return;
        }

        user.setAlias(command.getAlias(), command.getAccount());

    }

    private void handleCheckCardStatus(CommandInput command) {
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("command", "checkCardStatus");

        if (command.getCardNumber() == null || command.getCardNumber().isEmpty()) {
            addCheckCardStatusOutput(responseNode, command.getTimestamp(), "Card not found");
            return;
        }

        Card foundCard = null;
        Account foundAccount = null;
        User foundUser = null;
        for (User u : usersMap.values()) {
            for (Account acc : u.getAccounts()) {
                for (Card c : acc.getCards()) {
                    if (c.getCardNumber().equals(command.getCardNumber())) {
                        foundCard = c;
                        foundAccount = acc;
                        foundUser = u;
                        break;
                    }
                }
                if (foundCard != null) break;
            }
            if (foundCard != null) break;
        }

        if (foundCard == null) {
            addCheckCardStatusOutput(responseNode, command.getTimestamp(), "Card not found");
            return;
        }


        if ("frozen".equals(foundCard.getStatus())) {
            return;
        }

        double balance = foundAccount.getBalance();
        double minBalance = foundAccount.getMinBalance();

        if (balance <= minBalance) {
            foundCard.setStatus("frozen");
            Transaction freezeTransaction = new Transaction(
                    "checkCardStatus",
                    command.getTimestamp(),
                    "You have reached the minimum amount of funds, the card will be frozen"
            );
            foundUser.addTransaction(freezeTransaction);
            return;
        }
    }

    private void addCheckCardStatusOutput(ObjectNode responseNode, int timestamp, String description) {
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("timestamp", timestamp);
        outputNode.put("description", description);
        responseNode.set("output", outputNode);
        responseNode.put("timestamp", timestamp);
        output.add(responseNode);
    }

    private void handleChangeInterestRate(CommandInput command) {
        String iban = command.getAccount();
        double newInterestRate = command.getInterestRate();
        User user = usersMap.get(command.getEmail());
        if (user == null) {
            return;
        }

        Account account = null;
        for (Account acc : user.getAccounts()) {
            if (acc.getAccountNumber().equals(iban)) {
                account = acc;
                break;
            }
        }

        if (account == null) {
            return;
        }

        if (!"savings".equalsIgnoreCase(account.getAccountType())) {
            ObjectNode responseNode = objectMapper.createObjectNode();
            responseNode.put("command", "changeInterestRate");

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", "This is not a savings account");

            responseNode.set("output", outputNode);
            responseNode.put("timestamp", command.getTimestamp());
            output.add(responseNode);

            return;
        }

        account.setInterestRate(newInterestRate);

        String description = "Interest rate changed to " + newInterestRate + "%";
        Transaction changeInterestRateTransaction = new Transaction(
                "changeInterestRate",
                command.getTimestamp(),
                description
        );
        user.addTransaction(changeInterestRateTransaction);
    }

    private void handleSplitPayment(CommandInput command) {
        List<String> accountsForSplit = command.getAccounts();
        double totalAmount = command.getAmount();
        String currency = command.getCurrency();
        int timestamp = command.getTimestamp();

        int numberOfAccounts = accountsForSplit.size();

        if (numberOfAccounts == 0) {
            return;
        }

        double share = totalAmount / numberOfAccounts;
        List<Account> accounts = new ArrayList<>();
        List<User> involvedUsers = new ArrayList<>();
        boolean canProceed = true;

        for (String iban : accountsForSplit) {
            Account account = findAccountByIBANGlobally(iban);
            if (account == null) {
                canProceed = false;
                break;
            }
            accounts.add(account);
            User user = findUserByAccount(iban);
            if (user == null) {
                canProceed = false;
                break;
            }
            involvedUsers.add(user);
        }

        if (canProceed) {
            for (Account account : accounts) {
                double convertedShare = share;
                if (!currency.equalsIgnoreCase(account.getCurrency())) {
                    double rate = getExchangeRateFromTo(currency, account.getCurrency());
                    if (rate == 0) {
                        canProceed = false;
                        break;
                    }
                    convertedShare = share * rate;
                }
                if (account.getBalance() < convertedShare) {
                    canProceed = false;
                    break;
                }
            }
        }

        if (canProceed) {
            for (Account account : accounts) {
                double convertedShare = share;
                if (!currency.equalsIgnoreCase(account.getCurrency())) {
                    double rate = getExchangeRateFromTo(currency, account.getCurrency());
                    convertedShare = share * rate;
                }
                account.setBalance(account.getBalance() - convertedShare);
            }

            List<String> involvedIBANs = new ArrayList<>(accountsForSplit);

            for (Account account : accounts) {
                User user = findUserByAccount(account.getAccountNumber());
                if (user != null) {
                    String description = String.format("Split payment of %.2f %s", totalAmount, currency);
                    if (!currency.equalsIgnoreCase(account.getCurrency())) {
                        double rate = getExchangeRateFromTo(currency, account.getCurrency());
                        double convertedShare = share * rate;
                    }

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
            }
        } else {
            for (String iban : accountsForSplit) {
                Account account = findAccountByIBANGlobally(iban);
                if (account == null) {
                    continue;
                }
                User user = findUserByAccount(account.getAccountNumber());
                if (user != null) {
                    Transaction failedSplitPayment = new Transaction(
                            "payNoFunds",
                            timestamp,
                            "Split payment failed due to insufficient funds"
                    );
                    user.addTransaction(failedSplitPayment);
                }
            }
        }
    }


    private void handleReport(CommandInput command) {
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

        ObjectNode reportOutput = objectMapper.createObjectNode();
        reportOutput.put("IBAN", account.getAccountNumber());
        reportOutput.put("balance", account.getBalance());
        reportOutput.put("currency", account.getCurrency());

        ArrayNode transactionsArray = objectMapper.createArrayNode();

        for (Transaction transaction : user.getTransactions()) {
            if (transaction.getTimestamp() >= startTimestamp && transaction.getTimestamp() <= endTimestamp) {
                if ("savings".equalsIgnoreCase(account.getAccountType())) {
                    if (!"interestIncome".equalsIgnoreCase(transaction.getType()) &&
                            !"changeInterestRate".equalsIgnoreCase(transaction.getType())) {
                        continue;
                    }
                }

                ObjectNode transactionNode = objectMapper.createObjectNode();
                transactionNode.put("timestamp", transaction.getTimestamp());
                transactionNode.put("description", transaction.getDescription());

                switch (transaction.getType()) {
                    case "addAccount":
                    case "addCard":
                    case "deleteCard":
                    case "checkCardStatus":
                    case "changeInterestRate":
                        if (transaction.getCardNumber() != null) {
                            transactionNode.put("card", transaction.getCardNumber());
                        }
                        if (transaction.getEmail() != null) {
                            transactionNode.put("cardHolder", transaction.getEmail());
                        }
                        if (transaction.getAccountNumber() != null) {
                            transactionNode.put("account", transaction.getAccountNumber());
                        }
                        break;

                    case "paySucessful":
                    case "payNoFunds":
                    case "PayOnetime":
                    case "payFrosen":
                    case "splitPayment":
                    case "splitPaymentFailed":
                        if (transaction.getAmount() > 0) {
                            transactionNode.put("amount", transaction.getAmount());
                        }
                        if (transaction.getCommerciant() != null) {
                            transactionNode.put("commerciant", transaction.getCommerciant());
                        }
                        break;

                    case "sendMoney":
                        if (transaction.getSenderIBAN() != null) {
                            transactionNode.put("senderIBAN", transaction.getSenderIBAN());
                        }
                        if (transaction.getReceiverIBAN() != null) {
                            transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
                        }
                        if (transaction.getAmount() > 0) {
                            String amountWithCurrencySend = String.format("%.2f", transaction.getAmount()) + " " + transaction.getCurrency();
                            transactionNode.put("amount", amountWithCurrencySend);
                        }
                        String transferType = user.getEmail().equals(transaction.getEmail()) ? "sent" : "received";

                        if (transferType != null) {
                            transactionNode.put("transferType", transferType);
                        }
                        break;

                    default:
                        break;
                }
                transactionsArray.add(transactionNode);
            }
        }
        reportOutput.set("transactions", transactionsArray);
        ObjectNode finalReport = objectMapper.createObjectNode();
        finalReport.put("command", reportType);
        finalReport.set("output", reportOutput);
        finalReport.put("timestamp", timestamp);
        output.add(finalReport);
    }

    private void handleSpendingsReport(CommandInput command) {
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

        ObjectNode reportOutput = objectMapper.createObjectNode();
        reportOutput.put("IBAN", account.getAccountNumber());
        reportOutput.put("balance", account.getBalance());
        reportOutput.put("currency", account.getCurrency());

        ArrayNode transactionsArray = objectMapper.createArrayNode();

        Map<String, Double> commerciantTotals = new HashMap<>();

        for (Transaction transaction : user.getTransactions()) {
            if (transaction.getTimestamp() >= startTimestamp && transaction.getTimestamp() <= endTimestamp) {
                if ("paySucessful".equalsIgnoreCase(transaction.getType()) && transaction.getCommerciant() != null) {
                    if (iban.equalsIgnoreCase(transaction.getAccountNumber())) {
                        ObjectNode transactionNode = objectMapper.createObjectNode();
                        transactionNode.put("timestamp", transaction.getTimestamp());
                        transactionNode.put("description", transaction.getDescription());
                        transactionNode.put("amount", transaction.getAmount());
                        transactionNode.put("commerciant", transaction.getCommerciant());
                        transactionsArray.add(transactionNode);
                        commerciantTotals.put(transaction.getCommerciant(),
                                commerciantTotals.getOrDefault(transaction.getCommerciant(), 0.0) + transaction.getAmount());
                    }
                }
            }
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

        reportOutput.set("transactions", transactionsArray);
        reportOutput.set("commerciants", commerciantsArray);

        ObjectNode finalReport = objectMapper.createObjectNode();
        finalReport.put("command", commandType);
        finalReport.set("output", reportOutput);
        finalReport.put("timestamp", timestamp);

        output.add(finalReport);
    }

    private void handleUnknownCommand(CommandInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", command.getCommand());
        objectNode.put("status", "Unknown command");
        output.add(objectNode);
    }
}