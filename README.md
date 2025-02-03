#### ©️ CopyRight Ioana Stefan 332 CA 2024 🏛️

# 🌟 Project Overview 🎉

This project manages a financial system that includes various entities like accounts, transactions, users, and more. Below is an explanation of each class and the overall structure used in the project.

---

## 🏗️ Project Structure 🛠️

The project is organized into the following components:

### **1. 🏦 `Account`** 📊
Represents a bank account with attributes such as account number, currency, type, balance, and interest rate.

- **Responsibilities:**
    - 🏷️ Manage account details including account type (savings or checking), currency, balance, and interest rate.
    - 💳 Handle associated cards and their operations.
    - 📈 Process transactions, including currency conversion and balance checks.
    - 📄 Generate transaction and spending reports.

- **Key Methods:**
    - ➕ `addCard(Card card)` - Adds a card to the account.
    - ❌ `deleteCardByNumber(String cardNumber)` - Deletes a card by its number.
    - 💸 `processCardTransaction(...)` - Processes a transaction using a card linked to the account.
    - 📉 `changeInterestRate(...)` - Changes the interest rate if the account type is "savings."
    - 📊 `generateAccountReport(...)` - Generates a detailed report for the account's transactions.
    - 🛍️ `generateSpendingsReport(...)` - Creates a spending report, including details of spending per merchant.

- **Additional Features:**
    - 💰 Supports balance updates and checks for sufficient funds before transactions.
    - 🌎 Manages currency conversions for multi-currency transactions using a `ConversionRateProvider`.
    - 🔒 Maintains a minimum balance requirement.

---

### **2. 💳 `Card`** 🔐
Represents a Card associated with a user's account. A Card can be active, frozen, or a one-time-use card.

- **Responsibilities:**
    - 💾 Store card information such as card number, associated account number, and status.
    - 🔄 Distinguish between regular cards and one-time-use cards.

- **Key Methods:**
    - 🆔 `getCardNumber()` - Retrieves the card number.
    - ✏️ `setCardNumber(String cardNumber)` - Updates the card number.
    - 🧊 `isFrozen()` - Checks if the card is frozen.
    - 🔄 `regenerateCardNumber(User user, int timestamp, String accountNumber)` - Regenerates the card number for one-time cards and logs the update.
    - ❄️ `freeze()` - Freezes the card by setting its status to "frozen."

- **Additional Features:**
    - 🕵️ Maintains a status attribute to track whether a card is active or frozen.
    - 🔁 Supports automatic regeneration for one-time-use cards after usage.
    - 📝 Logs card regeneration events as transactions.

---

### **3. 🌍 `ConversionRateProvider`** 💱
Functional interface for providing conversion rates between currencies.

- **Responsibilities:**
    - 💹 Provide exchange rates between specified currencies.

- **Key Methods:**
    - 🔢 `getRate(String fromCurrency, String toCurrency)` - Retrieves the conversion rate between two specified currencies (e.g., USD to EUR).

- **Additional Features:**
    - 🤝 Supports easy integration with various currency conversion implementations.

---

### **4. 💱 `ExchangeRate`** 📉
Represents an exchange rate between two currencies.

- **Responsibilities:**
    - 📋 Serve as a data structure to store the source currency, target currency, and exchange rate.

- **Key Attributes:**
    - 🌍 `fromCurrency` - The source currency code (e.g., "USD").
    - 🏳️ `toCurrency` - The target currency code (e.g., "EUR").
    - 💵 `rate` - The exchange rate from the source currency to the target currency.

- **Key Methods:**
    - 🛠️ `getFromCurrency()` - Retrieves the source currency code.
    - ✏️ `setFromCurrency(String fromCurrency)` - Updates the source currency code.
    - 🏳️ `getToCurrency()` - Retrieves the target currency code.
    - ✏️ `setToCurrency(String toCurrency)` - Updates the target currency code.
    - 🔢 `getRate()` - Retrieves the exchange rate.
    - ✏️ `setRate(double rate)` - Updates the exchange rate.

- **Additional Features:**
    - 📄 Provides a `toString()` method to display exchange rate details in a readable format.

---

### **5. 🛒 `Merchant`** 🏬
Represents a merchant with a name and description.

- **Responsibilities:**
    - 🏷️ Store details about the merchant, such as its name and a brief description.

- **Key Attributes:**
    - 🛍️ `name` - The name of the merchant.
    - ✍️ `description` - A brief description of the merchant.

- **Key Methods:**
    - 🆔 `getName()` - Retrieves the name of the merchant.
    - ✏️ `setName(String name)` - Updates the merchant's name.
    - 📝 `getDescription()` - Retrieves the merchant's description.
    - ✏️ `setDescription(String description)` - Updates the merchant's description.

- **Additional Features:**
    - 📄 Provides a `toString()` method to display the merchant's details in a readable format.

---

### **6. 💵 `Transaction`** 💰
Represents a financial transaction involving accounts, cards, and IBANs.

- **Responsibilities:**
    - 🗃️ Store detailed information about a transaction, including sender, receiver, amount, type, and related accounts or cards.
    - 📝 Generate JSON representations for transaction reports.
    - 🔄 Facilitate different types of transactions, including money transfers, card payments, and split payments.

- **Key Attributes:**
    - ✉️ `senderIBAN` - The IBAN of the sender's account.
    - 📬 `receiverIBAN` - The IBAN of the receiver's account.
    - 💳 `cardNumber` - The card number involved in the transaction.
    - 🏦 `accountNumber` - The account number related to the transaction.
    - 🛒 `commerciant` - The merchant involved in the transaction.
    - 💵 `currency` - The currency used in the transaction.
    - 🔢 `amount` - The amount involved in the transaction.
    - ⏰ `timestamp` - The timestamp of the transaction.

- **Key Methods:**
    - ❄️ `createFrozenTransaction(int timestamp, String accountNumber)` - Creates a transaction to freeze an account.
    - ✅ `createSuccessfulTransaction(CommandInput command, String accountNumber, double amount)` - Creates a successful transaction record.
    - 🤝 `createSplitPaymentTransaction(...)` - Logs a split payment transaction.
    - 📄 `toReportNode(ObjectMapper objectMapper)` - Converts the transaction into a JSON node for reporting purposes.
    - 📅 `isWithinTimestamp(int startTimestamp, int endTimestamp)` - Checks if the transaction falls within a specific time range.

- **Additional Features:**
    - 🏗️ Supports factory methods for creating specific transaction types, such as insufficient funds or one-time card regenerations.
    - 🔍 Includes utility methods to determine transaction relevance for reporting and spending analysis.
    - 📄 Provides a `toString()` method to display transaction details in a readable format.

---

### **7. 👤 `User`** 🧑‍💻
Represents a user in the system. A user can have multiple accounts, cards, and transactions.

- **Responsibilities:**
    - 🗂️ Store user-specific details like first name, last name, email, and associated accounts, cards, and transactions.
    - ⚙️ Manage interactions with accounts, including adding and deleting accounts.
    - 💳 Handle operations related to cards, such as creation and deletion.
    - 🔄 Process transactions, including split payments and interest rate changes.

- **Key Attributes:**
    - 🧑‍💼 `firstName` - The user's first name.
    - 🧑‍💼 `lastName` - The user's last name.
    - 📧 `email` - The user's email address.
    - 🏦 `accounts` - A list of accounts associated with the user.
    - 💳 `cards` - A list of cards associated with the user.
    - 📝 `transactions` - A log of transactions involving the user.

- **Key Methods:**
    - ➕ `addAccount(Account account)` - Adds a new account for the user.
    - ➕ `createCard(String accountNumber, int timestamp)` - Creates a new card for a specific account.
    - 🔄 `createOneTimeCard(CommandInput command)` - Creates a one-time-use card.
    - ❌ `deleteAccount(CommandInput command)` - Deletes an account based on the provided command.
    - ❌ `deleteCard(String cardNumber, int timestamp)` - Deletes a card by its number and logs the transaction.
    - 🏷️ `setAliasForAccount(String alias, String accountNumber)` - Sets an alias for a specific account.
    - 🔍 `findAccountByIBAN(String iban)` - Locates an account by its IBAN.
    - 🔍 `findCardByNumber(String cardNumber)` - Finds a card by its number.
    - 📉 `changeAccountInterestRate(...)` - Changes the interest rate for a specific account.
    - 🤝 `addSplitPaymentTransaction(...)` - Logs a split payment transaction.

- **Additional Features:**
    - 🔗 Maintains a mapping of aliases to IBANs for quick lookups.
    - 🔍 Provides utility methods for filtering transactions by timestamp and account type.
    - 📄 Supports generating detailed transaction and spending reports.
    - 🛡️ Includes safeguards for operations like account deletion and interest rate changes, ensuring conditions are met (e.g., account balance is zero before deletion).

---

### **8. 🛠️ `CommandProcessor`** 🤖
Handles incoming commands and executes the appropriate operations.

- **Responsibilities:**
    - 🔄 Parse and execute commands provided in the input.
    - 🤝 Manage interaction between users, accounts, transactions, and external components like `ConversionRateProvider`.

- **Key Methods:**
    - 🛠️ `processCommands(ObjectInput inputData)` - Processes a list of commands provided in the input.
    - 👥 `handlePrintUsers(int timestamp)` - Prints all users and their accounts.
    - ➕ `handleAddAccount(CommandInput command)` - Adds a new account to a user.
    - ➕ `handleCreateCard(CommandInput command)` - Creates a card for a user.
    - 💸 `handlePayOnline(CommandInput command)` - Handles online payments and logs them.
    - 💰 `handleSendMoney(CommandInput command)` - Processes sending money between accounts.
    - 📉 `handleChangeInterestRate(CommandInput command)` - Manages interest rate changes for accounts.
    - 🤝 `handleSplitPayment(CommandInput command)` - Splits payments across multiple accounts.
    - 📄 `handleReport(CommandInput command)` - Generates a report for a specific account.
    - 🛍️ `handleSpendingsReport(CommandInput command)` - Creates a spending report for a specific account.
    - 💱 `getExchangeRateFromTo(String from, String to)` - Retrieves the exchange rate between two currencies.
    - 🔍 `findAccountByIBANGlobally(String iban)` - Searches for an account by IBAN across all users.

- **Additional Features:**
    - 🧩 Implements a singleton design pattern to manage global command processing.
    - 🛡️ Validates user input and ensures error messages are added to the output for invalid commands.

---

## 🔄 System Flow 🔁
1. **👥 User Management:** Users are created and stored in a central registry. Each user manages multiple accounts and cards.
2. **🏦 Account Operations:** Users can add, update, and interact with accounts. Account types include "classic" and "savings."
3. **💳 Transaction Management:** Transactions are logged and processed based on user actions like payments, fund transfers, and split payments.
4. **💱 Currency Conversion:** Transactions involving different currencies are handled by `ConversionRateProvider`, which ensures accurate conversions.
5. **🔄 Command Processing:** Commands like "add account" or "change interest rate" are interpreted and executed by the `CommandProcessor`.

---

## 💡 Example Use Cases ✨
- **➕ Add an Account:** A user can add a new account specifying the type and currency. Savings accounts can have an interest rate applied.
- **💸 Process a Transaction:** A user initiates a transaction, which is validated and processed. Split payments distribute amounts among multiple accounts.
- **📉 Change Interest Rate:** Interest rates can be updated for savings accounts through specific commands.

---

## 🚀 Possible Improvements ✨

- **🔗 Integration with External APIs:** Add support for fetching live currency exchange rates from external providers.
- **📊 Enhanced Reporting:** Provide more detailed financial analytics, such as monthly spending trends or category-based expenses.
- **🔒 Improved Security:** Implement additional security features, like two-factor authentication for card operations.
- **📈 Scalability Enhancements:** Optimize the system to handle a larger number of users and transactions.
- **🎨 Better User Interface:** Develop a graphical user interface (GUI) or web interface for easier interaction.

---

## **🛠️ Design Pattern: Singleton**

The `CommandProcessor` class follows the **Singleton Design Pattern** to ensure that only one instance of the command processor exists throughout the application. This pattern is particularly useful for managing global state and coordinating command execution efficiently.

---

This modular structure allows for flexibility and scalability while maintaining clear boundaries between components. Each class is responsible for a specific aspect of the system, promoting maintainability and ease of testing.

