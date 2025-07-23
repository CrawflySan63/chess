package dataaccess;

import chess.ChessGame;
import model.*;
import java.sql.*;
import java.util.*;
import com.google.gson.Gson;

public class MySqlDataAccess implements DataAccess {
    private final Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM AuthTokens");
            stmt.executeUpdate("DELETE FROM Games");
            stmt.executeUpdate("DELETE FROM Users");
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear database", e);
        }
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        var sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, user.password()); // should already be hashed
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT username, password, email FROM Users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve user", e);
        }
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        var sql = "DELETE FROM AuthTokens WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth token", e);
        }
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        var sql = "INSERT INTO AuthTokens (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert auth token", e);
        }
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        var sql = "SELECT authToken, username FROM AuthTokens WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Internal server error while fetching auth", e);
        }
    }

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        var sql = "INSERT INTO Games (id, gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game.gameID());
            stmt.setString(2, game.gameName());
            stmt.setString(3, game.whiteUsername());
            stmt.setString(4, game.blackUsername());
            String gameJson = gson.toJson(game.game());
            stmt.setString(5, gameJson);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert game", e);
        }
    }

    @Override
    public void setWhiteUsername(int gameID, String username) throws DataAccessException {
        var sql = "UPDATE Games SET whiteUsername = ? WHERE id = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: GameID not found for white username");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error updating whiteUsername", ex);
        }
    }

    @Override
    public void setBlackUsername(int gameID, String username) throws DataAccessException {
        var sql = "UPDATE Games SET blackUsername = ? WHERE id = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: Game ID not found for black username");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error updating blackUsername", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var sql = "SELECT * FROM Games WHERE id = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String gameJson = rs.getString("game");
                    ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                    return new GameData(
                            rs.getInt("id"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            chessGame
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game", e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        var sql = "SELECT * FROM Games";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {
            while (rs.next()) {
                String gameJson = rs.getString("game");
                ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                games.add(new GameData(
                        rs.getInt("id"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        chessGame
                ));
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games", e);
        }
    }
}
