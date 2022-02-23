package banking;

public class Main {

    public static void main(String[] args) {
        //String url = "jdbc:sqlite:" + args[1];
        String url = "jdbc:sqlite:card.s3db";
        // CR: obiekt bazy danych jest tutaj tworzony ale nie jest nigdzie przekazywany
        // zamiasto pola statycznego w klasie Program powinien byc przekazany do Program w konstruktorze
        Database db = new Database(url);
        db.create();

        Program.menu();
    }
}
