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
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        List<GameSummary> summaries = new ArrayList<>();
        List<GameData> gameList = new ArrayList<>(dataAccess.listGames());

        for (GameData game : gameList) {
            summaries.add(new GameSummary(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
        }

        return new ListGamesResult(summaries);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (request.gameName() == null || request.gameName().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        int gameID = Math.abs(UUID.randomUUID().hashCode());
        GameData gameData = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        dataAccess.insertGame(gameData);

        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        if (authToken == null || authToken.isBlank() || request == null) {
            throw new DataAccessException("Error: bad request");
        }

        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        String username = authData.username();
        GameData game = dataAccess.getGame(request.gameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        ChessGame.TeamColor playerColor = request.playerColor();

        //if null, observer joins, so no update to game is required
        if (playerColor == null) {
            return;
        }

        // Check if color is already taken
        if ((playerColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null)
                || (playerColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null)) {
            throw new DataAccessException("Error: already taken");
        }

        if (playerColor == ChessGame.TeamColor.WHITE) {
            dataAccess.setWhiteUsername(game.gameID(), username);
        } else {
            dataAccess.setBlackUsername(game.gameID(), username);
        }
    }
}
