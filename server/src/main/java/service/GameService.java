package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import result.ListGamesResult;

import java.util.Collection;

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
}
