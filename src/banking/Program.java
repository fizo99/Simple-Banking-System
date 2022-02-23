package banking;

import java.util.Scanner;

public class Program {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Database db = new Database("jdbc:sqlite:card.s3db");


    public static void menu() {
        boolean run = true;
        while (run) {
            welcomeMenu();
            switch (choice()) {
                case "1":
                    Account acc = Bank.createAccount();
                    db.insert(acc.getCardNumber(), acc.getPin());
                    break;
                case "2":
                    String cardNumber = Bank.login(db);
                    if (!cardNumber.isEmpty()) {
                        boolean isLogged = true;
                        while (isLogged) {
                            logInMenu();
                            switch (choice()) {
                                case "0":
                                    run = false;
                                    isLogged = false;
                                    break;
                                case "1":
                                    System.out.println(db.getBalance(cardNumber));
                                    break;
                                case "2":
                                    db.addIncome(cardNumber, howMuchIncome());
                                    break;
                                case "3":
                                    String cardNumberToTransfer = cardNumberToTransfer();
                                    if (db.isPossible(cardNumber, cardNumberToTransfer)) {
                                        System.out.println(db.doTransfer(cardNumber, cardNumberToTransfer, howMuchTransfer()));
                                    }
                                    break;
                                case "4":
                                    db.closeAccount(cardNumber);
                                    break;
                                case "5":
                                    isLogged = false;
                                    break;
                                default:
                                    System.out.println("Wrong number");
                                    break;
                            }
                        }
                    }
                    break;
                case "0":
                    run = false;
                    break;
                default:
                    System.out.println("Wrong number!");
                    break;
            }
        }
    }

    private static String cardNumberToTransfer() {
        System.out.println("Transfer\n" +
                "Enter card number:");
        return scanner.nextLine();
    }

    // method for printing welcome menu
    private static void welcomeMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    // method for printing menu after login into account
    private static void logInMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    // method that return user input
    private static String choice() {
        return scanner.nextLine();
    }


    private static double howMuchIncome() {
        System.out.println("Enter income:");
        return scanner.nextDouble();
    }

    private static double howMuchTransfer() {
        System.out.println("Enter how much money you want to transfer:");
        return scanner.nextDouble();
    }
}
