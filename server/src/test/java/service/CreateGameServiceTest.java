package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import result.CreateGameResult;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {

    private GameService gameService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);

        // Register a user
        UserData user = new UserData("testUser", "password", "email@test.com");
        dataAccess.insertUser(user);

        //insert valid authToken
        AuthData auth = new AuthData("valid-token", "testUser");
        dataAccess.insertAuth(auth);
    }

    @Test
    public void createGame_Success() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("Cool Chess Game");
        CreateGameResult result = gameService.createGame(request, "valid-token");

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    public void createGame_InvalidToken() {
        CreateGameRequest request = new CreateGameRequest("Another Game");

        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameService.createGame(request, "invalid-token"));
        assertTrue(exception.getMessage().contains("unauthorized"));
    }

    @Test
    public void createGame_BadRequest() {
        CreateGameRequest request = new CreateGameRequest(null); // Invalid name

        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameService.createGame(request, "valid-token"));
        assertTrue(exception.getMessage().contains("bad request"));
    }
}