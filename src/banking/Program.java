package banking;

import java.util.Scanner;

// CR: malo znaczaca nazwa klasy
// CR: nie musimy wszystkich metod robic statycznych - lepsza opcja bedzie stworzenie obiektu i wywolywanie metod nalezacych do obiektu
public class Program {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Database db = new Database("jdbc:sqlite:card.s3db");


    public static void menu() {
        // CR: lepiej kontrole czy program ma sie skonczyc czy nie kontrolowal bys przez rzucanie exceptionow
        // to rozwiazanie jest zbyt skomplikowane
        // jak cos to jeden switch - na pewno nie dwa zagniezdzone
        // dobrym sposobem byloby zrobienie osobnej metody dla handlowania kazdego z case
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

    // CR: mieszanie wyswietlania i pobierania danych robi mały mętlik
    // wedlug mnie lepszym wyjsciem byloby rozdzielenie pobierania danych od wyswietlania
    // do samego pobierania danych mozna zrobic osobna klase, np. InputReader
    private static String cardNumberToTransfer() {
        System.out.println("Transfer\n" +
                "Enter card number:");
        return scanner.nextLine();
    }

    // CR: usun komentarze - nazwy metod, pól, zmiennych, stalych powinny same z siebie jasno opisywac kod
    // method for printing welcome menu
    private static void welcomeMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    // CR: usun komentarze
    // method for printing menu after login into account
    private static void logInMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    // CR: usun komentarze
    // CR: malo mowiaca nazwa metody - moze lepsze byloby readUserChoice() ?
    // method that return user input
    private static String choice() {
        return scanner.nextLine();
    }

    // CR: podobnie jak z metoda cardNumberToTransfer()
    // kod bylby jasniejszy gdyby w tej sytuacji zrobic dwie metody np.
    // printEnterIncomeMessage()
    // readIncome()
    // lub rodzielic to jeszcze w inny sposob - np w inne klasy i tam stworzyc te metody
    private static double howMuchIncome() {
        System.out.println("Enter income:");
        return scanner.nextDouble();
    }

    // CR: podobnie jak z metoda cardNumberToTransfer()
    private static double howMuchTransfer() {
        System.out.println("Enter how much money you want to transfer:");
        return scanner.nextDouble();
    }
}
