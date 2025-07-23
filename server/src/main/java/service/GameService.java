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
        AuthData auth;
        try {
            auth = dataAccess.getAuth(authToken);
        } catch (Exception e) {
            //database failure
            throw new DataAccessException("Error: internal server error", e);
        }

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        //if above passes, then get gameID, usernames, and gameName
        //from DA method listGames
        List<GameSummary> summaries = new ArrayList<>();
        List<GameData> gameList;

        try {
            gameList = new ArrayList<>(dataAccess.listGames());
        } catch (Exception e) {
            throw new DataAccessException("Error: internal server error", e);
        }

        for (GameData game : gameList) {
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
        AuthData auth;
        try {
            auth = dataAccess.getAuth(authToken);
        } catch (Exception e) {
            //database failure
            throw new DataAccessException("Error: internal server error", e);
        }

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
        try {
            dataAccess.insertGame(gameData);
        } catch (Exception e) {
            throw new DataAccessException("Error: internal server error", e);
        }

        //return success
        return new CreateGameResult(gameID);

    }

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        // Validate input
        if (authToken == null || authToken.isBlank() || request == null) {
            throw new DataAccessException("Error: bad request");
        }

        // Validate playerColor
        ChessGame.TeamColor playerColor = request.playerColor();
        if ((playerColor != ChessGame.TeamColor.WHITE && playerColor != ChessGame.TeamColor.BLACK)) {
            throw new DataAccessException("Error: bad request");
        }

        // Validate authToken
        AuthData authData;
        try {
            authData = dataAccess.getAuth(authToken);
        } catch (Exception e) {
            //database failure
            throw new DataAccessException("Error: internal server error", e);
        }
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        String username = authData.username();
        //System.out.println("Saving username as: " + username);

        // Get the game from database
        GameData game;
        try {
            game = dataAccess.getGame(request.gameID());
        } catch (Exception e) {
            throw new DataAccessException("Error: internal server error", e);
        }
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        // Check if color is already taken
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
        } else {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
        }

        try {
            if (playerColor == ChessGame.TeamColor.WHITE) {
                dataAccess.setWhiteUsername(game.gameID(), username);
            } else {
                dataAccess.setBlackUsername(game.gameID(), username);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: internal server error", e);
        }
    }
}
