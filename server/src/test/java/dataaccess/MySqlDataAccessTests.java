
package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlDataAccessTests {
    private DataAccess db;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new MySqlDataAccess();
        db.clear();
    }

    @Test
    public void testInsertUserSuccess() throws DataAccessException {
        var user = new UserData("billy", "pw", "billy@example.com");
        assertDoesNotThrow(() -> db.insertUser(user));
    }

    @Test
    public void testInsertUserDuplicateFailure() throws DataAccessException {
        var user = new UserData("bob", "pw", "bob@example.com");
        db.insertUser(user);
        assertThrows(DataAccessException.class, () -> db.insertUser(user));
    }

    @Test
    public void testGetUserSuccess() throws DataAccessException {
        var user = new UserData("sara", "pass123", "sara@email.com");
        db.insertUser(user);
        var retrieved = db.getUser("sara");
        assertEquals(user, retrieved);
    }

    @Test
    public void testGetUserNotFound() throws DataAccessException {
        assertNull(db.getUser("ghost"));
    }

    @Test
    public void testInsertAuthSuccess() throws DataAccessException {
        var user = new UserData("billy", "pw", "billy@example.com");
        db.insertUser(user); // insert user first so foreign key is valid

        var auth = new AuthData("abc123", "billy");
        assertDoesNotThrow(() -> db.insertAuth(auth));
    }

    @Test
    public void testInsertAuthDuplicateFailure() throws DataAccessException {
        var user = new UserData("billy", "pw", "billy@example.com");
        db.insertUser(user);

        var auth = new AuthData("abc123", "billy");
        db.insertAuth(auth);
        assertThrows(DataAccessException.class, () -> db.insertAuth(auth));
    }

    @Test
    public void testGetAuthSuccess() throws DataAccessException {
        var user = new UserData("alice", "pass123", "alice@example.com");
        db.insertUser(user); // must insert user first

        var auth = new AuthData("tokenXYZ", "alice");
        db.insertAuth(auth);

        var fetched = db.getAuth("tokenXYZ");
        assertNotNull(fetched);
        assertEquals(auth.authToken(), fetched.authToken());
        assertEquals(auth.username(), fetched.username());
    }

    @Test
    public void testGetAuthNotFound() throws DataAccessException {
        assertNull(db.getAuth("no-token"));
    }

    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {
        var user = new UserData("charlie", "secure", "charlie@example.com");
        db.insertUser(user); // must insert user first

        var auth = new AuthData("deleteMe", "charlie");
        db.insertAuth(auth);

        db.deleteAuth("deleteMe");

        var result = db.getAuth("deleteMe");
        assertNull(result); // confirm it was deleted
    }

    @Test
    public void testDeleteAuthNotFound() throws DataAccessException {
        assertDoesNotThrow(() -> db.deleteAuth("nothing"));
    }

    @Test
    public void testClearRemovesAllData() throws DataAccessException {
        db.insertUser(new UserData("user", "pw", "email"));
        db.insertAuth(new AuthData("auth", "user"));
        db.insertGame(new GameData(123, null, null, "game", new ChessGame()));
        db.clear();
        assertNull(db.getUser("user"));
        assertNull(db.getAuth("auth"));
        assertNull(db.getGame(123));
        assertTrue(db.listGames().isEmpty());
    }

    @Test
    public void testInsertGameSuccess() throws DataAccessException {
        var game = new GameData(999, null, null, "Test Game", new ChessGame());
        assertDoesNotThrow(() -> db.insertGame(game));
    }

    @Test
    public void testInsertGameDuplicateFailure() throws DataAccessException {
        var game = new GameData(999, null, null, "Test Game", new ChessGame());
        db.insertGame(game);
        assertThrows(DataAccessException.class, () -> db.insertGame(game));
    }

    @Test
    public void testGetGameSuccess() throws DataAccessException {
        var game = new GameData(111, null, null, "Another Game", new ChessGame());
        db.insertGame(game);
        assertEquals(game, db.getGame(111));
    }

    @Test
    public void testGetGameNotFound() throws DataAccessException {
        assertNull(db.getGame(999999));
    }

    @Test
    public void testListGamesReturnsAll() throws DataAccessException {
        db.insertGame(new GameData(1, null, null, "Game One", new ChessGame()));
        db.insertGame(new GameData(2, null, null, "Game Two", new ChessGame()));
        Collection<GameData> games = db.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesEmptyList() throws DataAccessException {
        Collection<GameData> games = db.listGames();
        assertTrue(games.isEmpty());
    }

    @Test
    public void testSetWhiteUsernameSuccess() throws DataAccessException {
        var game = new GameData(333, null, null, "White Set", new ChessGame());
        db.insertGame(game);
        db.setWhiteUsername(333, "whiteplayer");
        assertEquals("whiteplayer", db.getGame(333).whiteUsername());
    }

    @Test
    public void testSetWhiteUsernameInvalidGame() {
        assertThrows(DataAccessException.class, () -> db.setWhiteUsername(404, "noone"));
    }

    @Test
    public void testSetBlackUsernameSuccess() throws DataAccessException {
        var game = new GameData(444, null, null, "Black Set", new ChessGame());
        db.insertGame(game);
        db.setBlackUsername(444, "blackplayer");
        assertEquals("blackplayer", db.getGame(444).blackUsername());
    }

    @Test
    public void testSetBlackUsernameInvalidGame() {
        assertThrows(DataAccessException.class, () -> db.setBlackUsername(404, "ghost"));
    }
}
