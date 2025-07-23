package dataaccess;

import model.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        if (!auths.containsKey(token)) {
            throw new DataAccessException("Auth token does not exist");
        }
        auths.remove(token);
    }

    @Override
    public void setWhiteUsername(int gameID, String username) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        GameData updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        games.put(gameID, updated);
    }

    @Override
    public void setBlackUsername(int gameID, String username) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        GameData updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        games.put(gameID, updated);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Game already exists");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public AuthData getAuth(String token) {
        return auths.get(token);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }
}
