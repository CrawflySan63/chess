import server.Server;
import dataaccess.DatabaseManager;
import dataaccess.DataAccessException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080);
    }
}