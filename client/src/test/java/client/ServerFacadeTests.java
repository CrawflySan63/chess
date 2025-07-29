package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import result.GameSummary;
import server.Server;

import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDB() throws Exception {
        facade.clear();
    }

    @Test
    public void clearTest() throws Exception {
        facade.register("user", "pw", "email");
        facade.clear();
        Assertions.assertThrows(RuntimeException.class, () -> facade.login("user", "pw"));
    }


    @Test
    public void registerSuccess() throws Exception {
        AuthData auth = facade.register("user1", "pw1", "email1");
        Assertions.assertNotNull(auth);
        Assertions.assertNotNull(auth.authToken());
        Assertions.assertEquals("user1", auth.username());
    }

    @Test
    public void registerFailDuplicateUsername() throws Exception {
        facade.register("user2", "pass2", "email2");

        Assertions.assertThrows(RuntimeException.class, () -> facade.register("user2", "diffpass2", "email_2"));
    }

    @Test
    public void loginSuccess() throws Exception {
        facade.register("user3", "pw3", "email3");
        AuthData auth = facade.login("user3", "pw3");
        Assertions.assertEquals("user3", auth.username());
    }

    @Test
    public void loginFailWrongPassword() throws Exception {
        facade.register("user4", "pw4", "email3");

        Assertions.assertThrows(RuntimeException.class, () -> facade.login("user3", "wrongpass"));
    }

    @Test
    public void loginFailUserDoesNotExist() throws Exception {
        Assertions.assertThrows(RuntimeException.class, () -> facade.login("no_user", "pw"));
    }

    @Test
    public void logoutSuccess() throws Exception {
        facade.register("user5", "pw5", "email5");
        AuthData auth = facade.login("user5", "pw5");
        facade.logout(auth.authToken());

        Assertions.assertThrows(RuntimeException.class, () -> facade.listGames(auth.authToken()));
    }

    @Test
    public void logoutFailNullAuth() throws Exception {
        Assertions.assertThrows(RuntimeException.class, () -> facade.logout(null));
    }

    @Test
    public void createGameSuccess() throws Exception {
        AuthData auth = facade.register("user6", "pw6", "email6");
        GameData game = facade.createGame("Test Game", auth.authToken());
        Assertions.assertNotNull(game);
        Assertions.assertTrue(game.gameID() > 0);
    }

    @Test
    public void createGameFailNullAuth() throws Exception {
        Assertions.assertThrows(RuntimeException.class, () -> facade.createGame("Test Game", null));
    }

    @Test
    public void listGamesShowsCreatedGame() throws Exception {
        AuthData auth = facade.register("user7", "pw7", "email7");
        facade.createGame("Game A", auth.authToken());
        facade.createGame("Game B", auth.authToken());

        List<GameSummary> games = facade.listGames(auth.authToken());
        Assertions.assertTrue(games.stream().anyMatch(g -> g.gameName().equals("Game A")));
        Assertions.assertTrue(games.stream().anyMatch(g -> g.gameName().equals("Game B")));
    }

    @Test
    public void listGamesFailNullAuth() throws Exception {
        Assertions.assertThrows(RuntimeException.class, () -> facade.listGames(null));
    }

    @Test
    public void joinGameAsWhite() throws Exception {
        AuthData auth = facade.register("user8", "pw8", "email8");
        GameData game = facade.createGame("Join Me", auth.authToken());
        Assertions.assertDoesNotThrow(() -> facade.joinGame(game.gameID(), ChessGame.TeamColor.WHITE, auth.authToken()));
    }

    @Test
    public void joinGameAsBlack() throws Exception {
        AuthData auth = facade.register("user9", "pw9", "email9");
        GameData game = facade.createGame("Join", auth.authToken());
        Assertions.assertDoesNotThrow(() -> facade.joinGame(game.gameID(), ChessGame.TeamColor.BLACK, auth.authToken()));
    }

    @Test
    public void joinGameFailWrongGame() throws Exception {
        AuthData auth = facade.register("user10", "pw10", "email10");
        Assertions.assertThrows(RuntimeException.class, () -> facade.joinGame(1, ChessGame.TeamColor.WHITE, auth.authToken()));
    }

//    @Test
//    public void observeGameSuccess() throws Exception {
//        AuthData auth = facade.register("observer", "pw", "em");
//        GameData game = facade.createGame("Observe Me", auth.authToken());
//        Assertions.assertDoesNotThrow(() -> facade.observeGame(game.gameID(), auth.authToken()));
//    }
//
//    @Test
//    public void observeGameFailWrongGame() throws Exception {
//        AuthData auth = facade.register("user11", "pw11", "email11");
//
//        Assertions.assertThrows(RuntimeException.class, () -> facade.observeGame(1, auth.authToken()));
//    }
}
