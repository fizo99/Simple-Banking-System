package banking;

import java.util.HashMap;
import java.util.Scanner;

public class Bank {

    private static final Scanner scanner = new Scanner(System.in);
    public static final HashMap<String, String> accounts = new HashMap<>();

    // method for creating new account
    public static Account createAccount() {
        String cardNumber = Card.cardNumber();
        String pin = Card.pin();
        accounts.put(cardNumber, pin);
        System.out.println("Your card has been created");
        System.out.println("Your card number:\n" + cardNumber + "\n" +
                "Your card PIN:\n" + pin);
        return new Account(cardNumber, pin);
    }

    // method for login into bank account. returning card number
    public static String login(Database db) {
        System.out.println("Enter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();

        if (db.login(cardNumber, pin)) {
            System.out.println("You have successfully logged in!");
            return cardNumber;
        }
        System.out.println("Wrong");
        return "";
    }
}
