package dataaccess;

import model.*;
import java.util.Collection;


public interface DataAccess {
    void clear() throws DataAccessException;

    //deleteAuthToken used in LogoutService
    void deleteAuth(String token) throws DataAccessException;

    //replaceGame used in GameService.joinGame() method
    //void replaceGame(GameData game) throws DataAccessException;
    void setWhiteUsername(int gameID, String username) throws DataAccessException;
    void setBlackUsername(int gameID, String username) throws DataAccessException;


    //listGames used in GameService.java for the list games endpoint
    Collection<GameData> listGames() throws DataAccessException;

    // Insert methods
    void insertUser(UserData user) throws DataAccessException;
    void insertAuth(AuthData auth) throws DataAccessException;
    void insertGame(GameData game) throws DataAccessException;

    // Getter methods
    UserData getUser(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
}
