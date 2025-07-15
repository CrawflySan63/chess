package service;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {
    private DataAccess dataAccess;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    @Test
    public void testRegisterPositive() throws DataAccessException {
        //valid user input
        RegisterRequest request = new RegisterRequest("user1", "pass123", "user1@example.com");

        //registering
        RegisterResult result = userService.register(request);

        //should return the username and a valid token
        assertEquals("user1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void testRegisterNegative_UsernameTaken() throws DataAccessException {
        //register the user successfully
        RegisterRequest firstRequest = new RegisterRequest("user1", "pass123", "user1@example.com");
        userService.register(firstRequest);

        //attempt to register the same username again
        RegisterRequest duplicateRequest = new RegisterRequest("user1", "newpass", "user1@example.com");

        //should throw a DataAccessException
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(duplicateRequest);
        });

        assertTrue(exception.getMessage().contains("already taken"));
    }
}
