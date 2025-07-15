package dataaccess;

import model.*;

public interface DataAccess {
    void clear() throws DataAccessException;

    //deleteAuthToken used in LogoutService
    void deleteAuth(String token) throws DataAccessException;

    // Insert methods
    void insertUser(UserData user) throws DataAccessException;
    void insertAuth(AuthData auth) throws DataAccessException;
    void insertGame(GameData game) throws DataAccessException;

    // Getter methods
    UserData getUser(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
}
