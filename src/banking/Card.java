package banking;

import java.util.Random;

// Card generator
public class Card {
    private static final Random random = new Random();

    // generate random card number
    public static String cardNumber() {
        StringBuilder number = new StringBuilder("400000");
        for (int i = 0; i < 9; i++) {
            number.append(random.nextInt(10));
        }
        String checkSum = String.valueOf(validCheckSum(number.toString()));
        number.append(checkSum);
        return number.toString();
    }

    // generate random pin
    public static String pin() {
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }

    // check sum 400000 500990212 3
    public static int validCheckSum(String cardNumber) {
        int sum = 0;
        for (int i = 0; i < cardNumber.length(); i++) {
            int digit = Integer.parseInt(cardNumber.substring(i, (i + 1)));
            if ((i % 2) == 0) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }
        int mod = sum % 10;
        return ((mod == 0) ? 0 : 10 - mod);
    }

    public static boolean checkCardNumber(String cardNumber) {
        String cardNumberToCheck = cardNumber.substring(0,15);
        return validCheckSum(cardNumberToCheck) == Integer.parseInt(String.valueOf(cardNumber.charAt(15)));
    }
}
