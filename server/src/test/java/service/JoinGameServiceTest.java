package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.JoinGameRequest;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {

    private DataAccess dataAccess;
    private GameService gameService;
    private String authToken;
    private int gameID;

    @BeforeEach
    void setup() throws DataAccessException {
        dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);

        // Insert a user and auth token
        UserData user = new UserData("testUser", "password", "email@example.com");
        dataAccess.insertUser(user);

        authToken = "validToken";
        AuthData authData = new AuthData(authToken, user.username());
        dataAccess.insertAuth(authData);

        // Insert a game
        GameData game = new GameData(1, null, null, "Test Game", null);
        dataAccess.insertGame(game);

        gameID = 1;
    }

    @Test
    void joinGamePositiveWhite() throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        assertDoesNotThrow(() -> gameService.joinGame(authToken, request));

        GameData updatedGame = dataAccess.getGame(gameID);
        assertEquals("testUser", updatedGame.whiteUsername());
    }

    @Test
    void joinGamePositiveBlack() throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK, gameID);
        assertDoesNotThrow(() -> gameService.joinGame(authToken, request));

        GameData updatedGame = dataAccess.getGame(gameID);
        assertEquals("testUser", updatedGame.blackUsername());
    }

    @Test
    void joinGameNegativeGameNotFound() {
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, 999);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameService.joinGame(authToken, request));
        assertTrue(exception.getMessage().contains("bad request"));
    }

    @Test
    void joinGameNegativeColorAlreadyTaken() throws DataAccessException {
        // First user joins as white
        JoinGameRequest request1 = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        gameService.joinGame(authToken, request1);

        // Second user
        String secondToken = "anotherToken";
        UserData secondUser = new UserData("otherUser", "pass", "other@email.com");
        dataAccess.insertUser(secondUser);
        dataAccess.insertAuth(new AuthData(secondToken, secondUser.username()));

        // Attempt to join same game as white again
        JoinGameRequest request2 = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameService.joinGame(secondToken, request2));
        assertTrue(exception.getMessage().contains("already taken"));
    }

    @Test
    void joinGameNegativeUnauthorized() {
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameService.joinGame("invalidToken", request));
        assertTrue(exception.getMessage().contains("unauthorized"));
    }

    @Test
    void joinGameNegativeInvalidColor() {
        JoinGameRequest request = new JoinGameRequest(null, gameID);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameService.joinGame(authToken, request));
        assertTrue(exception.getMessage().contains("bad request"));
    }
}