package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class Database {
    private final String url;

    // constructor
    public Database(String url) {
        this.url = url;
    }

    // method for create table in database
    public void create() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(this.url);
        try (Connection con = dataSource.getConnection()) {
            // Statement creation
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

    // method for connect with database
    private Connection connect() {
        String url = this.url;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // method for insert data to database
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

    // method for checking your balance
    public double getBalance(String cardNumber) {
        String sql = "SELECT balance FROM card WHERE number LIKE ?";
        double balance = 0;
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cardNumber);

            try (ResultSet rs = statement.executeQuery()) {
                // Only expecting a single result
                balance = rs.getDouble(1);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public boolean getCardNumber(String cardNumber) {
        String sql = "SELECT number FROM card WHERE number LIKE ?";
        boolean found = false;
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cardNumber);

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
            System.out.println(e.getMessage());
        }
    }

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

    // method for transfer money to other account
    public String doTransfer(String cardNumber, String cardNumberToTransfer, double value) {

        if (getBalance(cardNumber) < value) {
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
        return "Something wrong";
    }

    public void fundTransfer(String cardNumber, double value) {

        String sql = "UPDATE card SET balance = balance - ?" +
                "WHERE number LIKE ?";

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            // set the corresponding param
            statement.setDouble(1, value);
            statement.setString(2, cardNumber);

            // update
            statement.executeUpdate();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

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
