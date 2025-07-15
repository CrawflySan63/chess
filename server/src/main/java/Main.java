import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080);
    }
}