package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

public class LogoutService {
    private final DataAccess dataAccess;

    public LogoutService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth;
        try {
            auth = dataAccess.getAuth(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error: internal server error", e);
        }

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        try {
            dataAccess.deleteAuth(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error: internal server error", e);
        }
    }
}
