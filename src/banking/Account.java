package banking;

// CR: usun komentarze
// CR: do poczytania: czesto zamiast tworzenia getterow uzywa sie Lomboka - zmniejsza to ilosc boilerplate kodu
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

