package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;
import request.LoginRequest;
import result.LoginResult;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

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

        // Step 3 create and store new user (with hashed password)
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        UserData newUser = new UserData(request.username(), hashedPassword, request.email());
        dataAccess.insertUser(newUser);

        // Step 4 generate and store auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        dataAccess.insertAuth(authData);

        // Step 5 return success
        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        // Step 1 validate input
        if (request.username() == null || request.password() == null ||
                request.username().isBlank() || request.password().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        // Step 2 get user and check password
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        // Step 3 generate and store auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        dataAccess.insertAuth(authData);

        // Step 4 return success
        return new LoginResult(request.username(), authToken);
    }
}
