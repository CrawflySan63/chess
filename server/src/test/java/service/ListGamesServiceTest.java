package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.ListGamesResult;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {
    private GameService gameService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);

        // Add a user and auth
        UserData user = new UserData("bob", "password", "email@example.com");
        dataAccess.insertUser(user);

        AuthData auth = new AuthData("valid-token", "bob");
        dataAccess.insertAuth(auth);

        // Add a few games
        GameData game1 = new GameData(1, "bob", null, "Game A", new ChessGame());
        GameData game2 = new GameData(2, null, "bob", "Game B", new ChessGame());
        dataAccess.insertGame(game1);
        dataAccess.insertGame(game2);
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        ListGamesResult result = gameService.listGames("valid-token");

        assertNotNull(result.games());
        assertEquals(2, result.games().size());
        assertTrue(result.games().stream().anyMatch(game -> game.gameName().equals("Game A")));
        assertTrue(result.games().stream().anyMatch(game -> game.gameName().equals("Game B")));
    }

    @Test
    public void listGamesInvalidAuth() {
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameService.listGames("invalid-token"));
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void listGamesMissingAuth() {
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameService.listGames(null));
        assertEquals("Error: unauthorized", exception.getMessage());
    }
}
