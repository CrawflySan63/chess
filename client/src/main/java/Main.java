import client.TerminalClient;
import client.ServerFacade;

public class Main {
    public static void main(String[] args) {
        var facade = new ServerFacade(8080);
        var client = new TerminalClient(facade);
        client.run();
    }
}