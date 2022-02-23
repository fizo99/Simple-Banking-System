package banking;

import org.sqlite.SQLiteDataSource;

// CR: raczej unikamy takich importów - pelne sciezki sa lepsze
// w intellij mozna ustawic opcje zeby IDE samo nie scalało nam wielu sciezej w jedną w formie import _._.*
import java.sql.*;

public class Database {
    private final String url;

    // CR: usun komentarze
    // constructor
    public Database(String url) {
        this.url = url;
    }

    // method for create table in database
    // CR: lepsza nazwa bylaby np. initializeDatabase()
    // CR: moze ta metoda powinna zostac uzyta w konstruktorze skoro jest wywolywana tylko raz i jej zadaniem jest przygotowanie bazy danych?
    public void create() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(this.url);
        try (Connection con = dataSource.getConnection()) {
            // CR: usun komentarze
            // Statement creation
            // CR: nie musimy zagniezczac try catchow, chyba ze zalezy nam na roznym obslugiwaniu bledow przy tworzeniu statementu i przy nawiazywaniu polaczenia
            try (Statement statement = con.createStatement()) {
                // Statement execution
                String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                        + "     id INTEGER PRIMARY KEY,\n"
                        + "     number TEXT,\n"
                        + "     pin TEXT,\n"
                        + "     balance INTEGER DEFAULT 0"
                        + ");";
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CR: usun komentarze
    // method for connect with database
    private Connection connect() {
        // CR: niepotrzebne przypisanie pola klasy do zmiennej w metodzie
        String url = this.url;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // CR: metoda moze zwrocic null - w przypadku jego odczytania dostaniemy NPE czego nalezy unikac jak ognia
        // lepisza opcja byloby stworzenie wlasnego exceptiona lub sprawienie zeby exception poszedł metodę wyżej
        return conn;
    }

    // CR: usun komentarze
    // method for insert data to database
    // CR: same metody wykonujace jakies query na bazie zrobilbym w innej klasie, do ktorej przekazal bym obiekt Database
    public void insert(String cardNumber, String pin) {
        String sql = "INSERT INTO card(number,pin) VALUES(?,?)";
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cardNumber);
            statement.setString(2, pin);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // CR: usun komentarze
    // method for checking your balance
    public double getBalance(String cardNumber) {
        String sql = "SELECT balance FROM card WHERE number LIKE ?";
        // CR: staraj sie zeby zmienne mialy jak najmnieszy scope
        // nie inicjalizuj zmiennej balance w tym miejscu
        // zamiast tego w miejscu balance = rs.getDouble(1); zrob po prostu return rs.getDouble(1);
        double balance = 0;
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cardNumber);

            //CR: nie potrzebny jest zagniezdzony try catch
            // ResultSet rs = statement.executeQuery();
            // balance = rs.getDouble(1);
            try (ResultSet rs = statement.executeQuery()) {
                // Only expecting a single result
                balance = rs.getDouble(1);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // CR: jesli wyskocza SQLExceptiony to metoda zwroci wartosc 0 co niekoniecznie musi byc prawda
        return balance;
    }

    public boolean getCardNumber(String cardNumber) {
        // CR: dla calej tej metody podobnie jak wyzej
        String sql = "SELECT number FROM card WHERE number LIKE ?";
        boolean found = false;
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cardNumber);

            try (ResultSet rs = statement.executeQuery()) {
                // CR: usun komentarze
                // Only expecting a single result
                found = rs.getBoolean(1);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }

    // CR: usun kometarz
    // CR: poprzednia metoda jest prawie taka sama - moze uda się wydzielic jedna wspolna metode?
    // method for check if card number and pin match, and also check if in database
    public boolean login(String cardNumber, String pin) {
        String sql = "SELECT COUNT(*) FROM card WHERE number LIKE ? AND pin LIKE ?";
        boolean found = false;
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cardNumber);
            statement.setString(2, pin);

            try (ResultSet rs = statement.executeQuery()) {
                // Only expecting a single result
                found = rs.getBoolean(1);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }

    // method for deposit money for your account
    // CR: mieszanie querowania bazy i wyswietlania danych w konsoli
    public void addIncome(String cardNumber, double value) {
        String sql = "UPDATE card SET balance = balance + ?" +
                "WHERE number LIKE ?";

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            // set the corresponding param
            statement.setDouble(1, value);
            statement.setString(2, cardNumber);

            // update
            statement.executeUpdate();
            System.out.println("Income was added!\n");

        } catch (SQLException e) {
            // CR: czy uzytkownik powinien widziec takie wiadomosci?
            // w takich sytuacjach pomocne moze byc tworzenie wlasnych exceptionow
            System.out.println(e.getMessage());
        }
    }

    // CR: nazwa isPossible nic nie mowi o dzialaniu metody
    // CR: czy ta metoda w ogole pasuje do klasy Database?
    public boolean isPossible(String cardNumber, String cardNumberToTransfer) {
        if (!Card.checkCardNumber(cardNumberToTransfer)) { //if card number do not pass luhn algo
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return false;
        }
        if (cardNumber.equals(cardNumberToTransfer)) {
            System.out.println("You can't transfer money to the same account!");
            return false;
        }
        if (!getCardNumber(cardNumberToTransfer)) { //if card number does not exist in db
            System.out.println("Such a card does not exist.");
            return false;
        }
        return true;
    }

    // CR: usun komentarz
    // method for transfer money to other account
    // CR nazwy zmiennych tutaj sa malo wymowne - moze lepiej senderCardNumber i receiverCardNumber ?
    public String doTransfer(String cardNumber, String cardNumberToTransfer, double value) {

        if (getBalance(cardNumber) < value) {
            // CR: w metodach wyzej komunikaty sa printowane - tutaj zwracane. Trzebaby bylo to zunifikowac
            return "Not enough money!";
        } else {
            String sql = "UPDATE card SET balance = balance + ?" +
                    "WHERE number LIKE ?";

            try (Connection conn = this.connect();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                // set the corresponding param
                statement.setDouble(1, value);
                statement.setString(2, cardNumberToTransfer);

                // update
                statement.executeUpdate();
                fundTransfer(cardNumber,value);
                return "Success!\n";

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        // CR: ten return lepiej jakby byl w catchu, bo tutaj nie wiadomo jaka sekwencja doprowadzi nas do tej linii.
        return "Something wrong";
    }

    // CR: fundTransfer jest mylace kiedy istnieje metoda doTransfer. Moze reduceSenderBalance?
    public void fundTransfer(String cardNumber, double value) {

        String sql = "UPDATE card SET balance = balance - ?" +
                "WHERE number LIKE ?";

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            // CR: usun komentarze
            // set the corresponding param
            statement.setDouble(1, value);
            statement.setString(2, cardNumber);

            // CR: usun komentarze
            // update
            statement.executeUpdate();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // CR: usun komentarze
    // method for closing account. Delete data from database
    public void closeAccount(String cardNumber) {
        String sql = "DELETE FROM card WHERE number LIKE ?";
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, cardNumber);
            statement.executeUpdate();

            System.out.println("The account has been closed");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
