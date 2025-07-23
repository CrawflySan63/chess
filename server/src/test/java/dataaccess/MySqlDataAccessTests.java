package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlDataAccessTests {
    private DataAccess db;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new MySqlDataAccess();
        db.clear();
    }

    //insertUser() Positive and Negative Test
    @Test
    public void  testInsertUserSuccess() throws DataAccessException {
        var user = new UserData("billy", "pw", "billy@example.com");
        assertDoesNotThrow(() -> db.insertUser(user));
    }

    @Test
    public void testInsertUserDuplicateFailure() throws DataAccessException {
        var user = new UserData("bob", "pw", "bob@example.com");
        db.insertUser(user);
        assertThrows(DataAccessException.class, () -> db.insertUser(user));
    }
}
