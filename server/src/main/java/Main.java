import server.Server;
import dataaccess.DatabaseManager;
import dataaccess.DataAccessException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.configureTables();

            Server server = new Server();
            int port = server.run(8080);
        } catch (DataAccessException e) {
            System.err.println("Database setup failed: " + e.getMessage());
        }

    }
}