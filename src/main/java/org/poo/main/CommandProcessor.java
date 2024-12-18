package org.poo.main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.main.structures.*;
import org.poo.fileio.*;
import org.poo.utils.Utils;
import java.util.*;

public class CommandProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ArrayNode output;
    private final Map<String, User> usersMap = new HashMap<>();

    public CommandProcessor(ArrayNode output) {
        this.output = output;
    }

    public void initializeUsers(List<UserInput> users) {
        for (UserInput user : users) {
            usersMap.put(user.getEmail(), new User(user.getFirstName(), user.getLastName(), user.getEmail()));
        }
    }

    public void processCommands(ObjectInput inputData) {
        initializeUsers(Arrays.asList(inputData.getUsers()));

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

        objectNode.putPOJO("output", usersArray); // Adăugăm output prima dată
        objectNode.put("timestamp", timestamp);  // Mutăm timestamp după output
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
            usersMap.put(command.getEmail(), user);
        }
    }

    private void handleCreateCard(CommandInput command) {
        User user = usersMap.get(command.getEmail());
        if (user != null) {
            for (Account account : user.getAccounts()) {
                if (account.getAccountNumber().equals(command.getAccount())) {
                    String cardNumber = Utils.generateCardNumber();
                    Card card = new Card(cardNumber, account.getAccountNumber(), false);
                    account.addCard(card);
                    break;
                }
            }
        }
    }

    private void handleCreateOneTimeCard(CommandInput command) {
        User user = usersMap.get(command.getEmail());
        if (user != null) {
            for (Account account : user.getAccounts()) {
                if (account.getAccountNumber().equals(command.getAccount())) {
                    String cardNumber = Utils.generateCardNumber();
                    Card card = new Card(cardNumber, account.getAccountNumber(), true);
                    account.addCard(card);
                    break;
                }
            }
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
        responseNode.put("timestamp", command.getTimestamp());

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
            outputNode.put("error", "Account cannot be deleted");
        }
        outputNode.put("timestamp", command.getTimestamp());

        responseNode.set("output", outputNode);
        output.add(responseNode);
    }

    private void handleDeleteCard(CommandInput command) {
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("command", "deleteCard");
        responseNode.put("timestamp", command.getTimestamp());

        // Validate email and cardNumber presence
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


        for (Account account : user.getAccounts()) {
            Iterator<Card> accountCardIterator = account.getCards().iterator();
            while (accountCardIterator.hasNext()) {
                Card card = accountCardIterator.next();
                if (card.getCardNumber().equals(command.getCardNumber())) {
                    accountCardIterator.remove();
                    cardDeleted = true;
                    break;
                }
            }
        }
    }


    private void handleUnknownCommand(CommandInput command) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", command.getCommand());
        objectNode.put("status", "Unknown command");
        output.add(objectNode);
    }
}
