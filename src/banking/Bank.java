package banking;

import java.util.HashMap;
import java.util.Scanner;

public class Bank {
    // CR: z tego co wiedze nowy scanner jest tworzony w klasie Bank i w klasie Program - wystarczy tylko jeden
    private static final Scanner scanner = new Scanner(System.in);
    // CR: dlaczego to pole jest publiczne?
    // po co to pole istnieje skoro dane tam sa jedynie wkladane i nic poza tym sie z nimi nie dzieje?
    public static final HashMap<String, String> accounts = new HashMap<>();

    // CR: usun komentarze
    // method for creating new account
    public static Account createAccount() {
        // CR: w javie > 10 mozna pisac var - kompilator wydedukuje typ
        // przyk. var cardNumber = Card.cardNumber()
        // CR: lepszym sposobem byloby zrobienie klasy Card z konstruktorem w ktorym cardNumber i pin by sie tworzyly, a dostep do nich odbywal by sie przez gettery
        // var card = new Card();
        String cardNumber = Card.cardNumber();
        String pin = Card.pin();
        accounts.put(cardNumber, pin);
        // CR: mamy tu zmieszaną logikę aplikacji i wyswietlanie komunikatow na interfejsie (dodawanie konta i robienie println)
        // powinno to byc rozdzielone
        System.out.println("Your card has been created");
        // CR: propozycja - moze lepszy bylby zapis osobnego System.out.println dla kazdego tekstu wyswietlanego w nowej linii?
        // System.out.println("Your card number:")
        // System.out.println(cardNumber)
        // ...
        // wiecej linii, ale kod wydaje mi sie czytelniejszy.

        System.out.println("Your card number:\n" + cardNumber + "\n" +
                "Your card PIN:\n" + pin);
        return new Account(cardNumber, pin);
    }

    // CR: usun komentarze
    // method for login into bank account. returning card number
    public static String login(Database db) {
        // CR: baza danych nie powinna tutaj byc przekazywana w argumencie
        // metoda nie powinna byc statyczna
        // to samo tyczy sie metody wyzej
        // lepiej byłoby zrobic prywatne pole Database db i konstruktor w ktorym przekazemy bazę.
        // bardziej naturalne wydaje sie tworzenie obiektu i operacje na jego metodach
        // var bank = new Bank(db);
        // bank.login() ... itp.
        System.out.println("Enter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();

        if (db.login(cardNumber, pin)) {
            System.out.println("You have successfully logged in!");
            return cardNumber;
        }
        // CR: malo mowiaca informacja o tym co poszlo nie tak.
        System.out.println("Wrong");
        // CR: w jakim celu zwracanie pustego stringa?
        return "";
    }
}
