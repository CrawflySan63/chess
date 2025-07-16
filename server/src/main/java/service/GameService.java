package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.GameSummary;
import result.ListGamesResult;

import java.util.ArrayList;
import java.util.List;
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

        //if above passes, then get gameID, usernames, and gameName
        //from DA method listGames
        List<GameSummary> summaries = new ArrayList<>();
        for (GameData game : dataAccess.listGames()) {
            summaries.add(new GameSummary(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
        }

        //return the result by passing in a list of *GameSummary* (not GameData) objects made above
        //using GameSummary record allows us to not include ChessGame game in the HTML response
        return new ListGamesResult(summaries);
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

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        //validate input (make sure authToken, request and playerColor aren't empty or null
        if (authToken == null || authToken.isBlank() ||
                request == null || request.playerColor() == null ||
                (!request.playerColor().equalsIgnoreCase("WHITE") && !request.playerColor().equalsIgnoreCase("BLACK"))) {
            throw new DataAccessException("Error: bad request");
        }

        //validate authToken (make sure it's in the database)
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        //validate player color
        String colorStr = request.playerColor();
        boolean isWhite = colorStr.equalsIgnoreCase("WHITE");
        boolean isBlack = colorStr.equalsIgnoreCase("BLACK");

        if (!isWhite && !isBlack) {
            throw new DataAccessException("Error: bad request");
        }

        //get game
        GameData oldGame = dataAccess.getGame(request.gameID());
        if (oldGame == null) {
            throw new DataAccessException("Error: bad request");
        }

        //check if the requested color is already taken
        if (isWhite) {
            if (oldGame.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
        } else {
            if (oldGame.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
        }

        //create new GameData with updated player
        GameData newGame = new GameData(
                oldGame.gameID(),
                isWhite ? authData.username() : oldGame.whiteUsername(),
                isBlack ? authData.username() : oldGame.blackUsername(),
                oldGame.gameName(),
                oldGame.game()
        );

        //replace game in DAO
        dataAccess.replaceGame(newGame);
    }
}
