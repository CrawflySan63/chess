package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import java.util.Collection;
import java.util.UUID;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        //validate token (like in LogoutService.logout())
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        //if above passes, then get all games
        Collection<GameData> games = dataAccess.listGames();

        //return the result by passing in collection of GameData objects made above
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        //validate token
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        //validate gameName from the request
        if (request.gameName() == null || request.gameName().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        //if both authToken and gameName  are valid, then generate gameID
        int gameID = Math.abs(UUID.randomUUID().hashCode());

        //create and insert GameData
        GameData gameData = new GameData(gameID, null,  null, request.gameName(), new ChessGame());
        dataAccess.insertGame(gameData);

        //return success
        return new CreateGameResult(gameID);

    }
}
