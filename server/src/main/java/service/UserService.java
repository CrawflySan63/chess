package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        // Step 1 validate input
        if (request.username() == null || request.password() == null || request.email() == null ||
                request.username().isBlank() || request.password().isBlank() || request.email().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        // Step 2 check if username already exists
        UserData existingUser = dataAccess.getUser(request.username());
        if (existingUser != null) {
            throw new DataAccessException("Error: already taken");
        }

        // Step 3 create and store new user
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        dataAccess.insertUser(newUser);

        // Step 4 generate and store auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        dataAccess.insertAuth(authData);

        // Step 5 return success
        return new RegisterResult(request.username(), authToken);
    }
}
