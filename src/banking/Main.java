package banking;

public class Main {

    public static void main(String[] args) {
        //String url = "jdbc:sqlite:" + args[1];
        String url = "jdbc:sqlite:card.s3db";
        Database db = new Database(url);
        db.create();

        Program.menu();
    }
}
