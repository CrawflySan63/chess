package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



public class ClearServiceTest {

    private DataAccess dataAccess;
    private ClearService clearService;

    @BeforeEach
    public void setUp() {
        dataAccess = new MemoryDataAccess();
        clearService = new ClearService(dataAccess);
    }

    @Test
    public void testClearPositive() throws Exception {
        // Arrange – insert fake data
        dataAccess.insertUser(new UserData("testUser", "password", "email@test.com"));
        dataAccess.insertAuth(new AuthData("token123", "testUser"));
        dataAccess.insertGame(new GameData(1, "testUser", null, "Cool Game", null));

        // Confirm data exists before clearing
        assertNotNull(dataAccess.getUser("testUser"));
        assertNotNull(dataAccess.getAuth("token123"));
        assertNotNull(dataAccess.getGame(1));

        // Act – clear all data
        clearService.clear();

        // Assert – data is gone
        assertNull(dataAccess.getUser("testUser"));
        assertNull(dataAccess.getAuth("token123"));
        assertNull(dataAccess.getGame(1));
    }
}
