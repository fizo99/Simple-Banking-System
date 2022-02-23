package banking;

// Account generator
public class Account {
    private final String cardNumber;
    private final String pin;

    public Account (String cardNumber, String pin) {
        this.cardNumber = cardNumber;
        this.pin = pin;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public String getPin() {
        return this.pin;
    }
}
