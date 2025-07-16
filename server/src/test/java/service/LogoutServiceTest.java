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

public class LogoutServiceTest {
    private DataAccess dataAccess;
    private UserService userService;
    private LogoutService logoutService;

    @BeforeEach
    public void setUp() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        logoutService = new LogoutService(dataAccess);
    }

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        //register new user
        var registerRequest = new request.RegisterRequest("testUser", "password", "email@example.com");
        userService.register(registerRequest);

        //login that new user
        var loginRequest = new LoginRequest("testUser", "password");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();

        //logout the user
        logoutService.logout(authToken);

        //checking that the authToken is no longer stored
        assertNull(dataAccess.getAuth(authToken), "Auth token should be deleted after logout");
    }

    @Test
    public void testLogoutInvalidToken() {
        var invalidToken = "not-a-real-token";

        var exception = assertThrows(DataAccessException.class,
                () -> logoutService.logout(invalidToken));

        assertTrue(exception.getMessage().contains("unauthorized"), "Expected unauthorized error");
    }
}
