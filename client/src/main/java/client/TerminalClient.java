package client;

import chess.ChessGame;
import model.AuthData;
import java.util.*; //using .Scanner a lot to read user input from terminal

public class TerminalClient {
    private final ServerFacade facade;
    private final Scanner scanner = new Scanner(System.in);
    private AuthData currentUser = null;

    public TerminalClient(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        System.out.println("♕ Welcome to 240 chess. Type Help to get started. ♕");

        while (true) {
            printPrompt();

            String line = scanner.nextLine().trim();
            String[] tokens = line.split("\\s+");

            if (tokens.length == 0 || tokens[0].isEmpty()) {
                continue;
            }

            String command = tokens[0].toLowerCase();

            try {
                if (currentUser == null) {
                    handleLoggedOut(command, tokens);
                } else {
                    handleLoggedIn(command, tokens);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printPrompt() {
        System.out.print(currentUser == null ? "[LOGGED_OUT] >>> " : "[LOGGED_IN] >>> ");
    }

    private void handleLoggedOut(String command, String[] tokens) throws Exception {
        switch (command) {
            case "help" -> showLoggedOutHelp();
            case "register" -> {
                if (tokens.length != 4) {
                    System.out.println("Usage: register <username> <password> <email>");
                } else {
                    currentUser = facade.register(tokens[1], tokens[2], tokens[3]);
                    System.out.println("Logged in as " + currentUser.username());
                }
            }
            case "login" -> {
                if (tokens.length != 3) {
                    System.out.println("Usage: login <username> <password>");
                } else {
                    currentUser = facade.login(tokens[1], tokens[2]);
                    System.out.println("Logged in as " + currentUser.username());
                }
            }
            case "quit" -> quit();
            default -> {
                System.out.println("Unknown command. Type 'help' for options.");
            }
        }
    }

    private void handleLoggedIn(String command, String[] tokens) throws Exception {
        switch (command) {
            case "help" -> showLoggedInHelp();
            case "create" -> {
                if (tokens.length != 2) {
                    System.out.println("Usage: create <name>");
                } else {
                    facade.createGame(tokens[1], currentUser.authToken());
                    System.out.println("Game created.");
                }
            }
            case "list" -> {
                var games = facade.listGames(currentUser.authToken());
                for (var g : games) {
                    System.out.printf("ID: %d | Name: %s | White: %s | Black: %s%n",
                            g.gameID(), g.gameName(), g.whiteUsername(), g.blackUsername());
                }
            }
            case "join" -> {
                if (tokens.length != 3) {
                    System.out.println("Usage: join <ID> [WHITE|BLACK]");
                } else {
                    int id = Integer.parseInt(tokens[1]);
                    ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(tokens[2].toUpperCase());
                    facade.joinGame(id, color, currentUser.authToken());
                    System.out.println("Joined game as " + color);
                }
            }
            case "observe" -> {
                if (tokens.length != 2) {
                    System.out.println("Usage: observe <ID>");
                } else {
                    int id = Integer.parseInt(tokens[1]);
                    facade.observeGame(id, currentUser.authToken());
                    System.out.println("Observing game " + id);
                }
            }
            case "logout" -> {
                facade.logout(currentUser.authToken());
                currentUser = null;
                System.out.println("Logged out.");
            }
            case "quit" -> quit();
            default -> System.out.println("Unknown command. Type 'help' for options.");
        }
    }

    private void showLoggedOutHelp() {
        System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("  login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("  quit - playing chess");
        System.out.println("  help - with possible commands");
    }

    private void showLoggedInHelp() {
        System.out.println("  create <NAME> - a game");
        System.out.println("  list - games");
        System.out.println("  join <ID> [WHITE|BLACK] - a game");
        System.out.println("  observe <ID> - a game");
        System.out.println("  logout - when you are done");
        System.out.println("  quit - playing chess");
        System.out.println("  help - with possible commands");
    }

    private void quit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }
}