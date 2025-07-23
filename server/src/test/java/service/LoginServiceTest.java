package service;

import model.UserData;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import result.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

import org.mindrot.jbcrypt.BCrypt;

public class LoginServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);

        //register a user for login testing
        UserData user = new UserData("testuser", BCrypt.hashpw("password123", BCrypt.gensalt()), "email@example.com");
        dataAccess.insertUser(user);
    }

    @Test
    void loginSuccess() throws DataAccessException {
        LoginRequest request = new LoginRequest("testuser", "password123");
        LoginResult result = userService.login(request);

        assertEquals("testuser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginWrongPasswordFails() {
        LoginRequest badRequest = new LoginRequest("testuser", "wrongpass");

        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> userService.login(badRequest));

        assertEquals("Error: unauthorized", exception.getMessage());
    }
}
