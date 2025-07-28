package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import request.*;
import result.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username, password, email);
        return makePostRequest("/user", request, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);
        return makePostRequest("/session", request, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        makeDeleteRequest("/session", authToken);
    }

    public GameData createGame(String gameName, String authToken) throws Exception {
        CreateGameRequest request = new CreateGameRequest(gameName);
        return makePostRequest("/game", request, GameData.class, authToken);
    }

    public List<GameSummary> listGames(String authToken) throws Exception {
        ListGamesResult response = makeGetRequest("/game", ListGamesResult.class, authToken);
        return response.games();
    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String authToken) throws Exception {
        JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
        makePutRequest("/game", request, authToken);
    }

    public void observeGame(int gameID, String authToken) throws Exception {
        JoinGameRequest request = new JoinGameRequest(null, gameID);
        makePutRequest("/game", request, authToken);
    }

    // Helper methods

    private <T> T makePostRequest(String path, Object request, Class<T> responseClass) throws Exception {
        return makePostRequest(path, request, responseClass, null);
    }

    private <T> T makePostRequest(String path, Object request, Class<T> responseClass, String authToken) throws Exception {
        URL url = new URL(serverUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        if (authToken != null) conn.setRequestProperty("Authorization", authToken);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(request).getBytes());
        }

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Error: " + conn.getResponseCode());
        }

        try (InputStream is = conn.getInputStream()) {
            return gson.fromJson(new InputStreamReader(is), responseClass);
        }
    }

    private void makePutRequest(String path, Object request, String authToken) throws Exception {
        URL url = new URL(serverUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", authToken);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(request).getBytes());
        }

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Error: " + conn.getResponseCode());
        }
    }

    private void makeDeleteRequest(String path, String authToken) throws Exception {
        URL url = new URL(serverUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Authorization", authToken);

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Error: " + conn.getResponseCode());
        }
    }

    private <T> T makeGetRequest(String path, Class<T> responseClass, String authToken) throws Exception {
        URL url = new URL(serverUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", authToken);

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Error: " + conn.getResponseCode());
        }

        try (InputStream is = conn.getInputStream()) {
            return gson.fromJson(new InputStreamReader(is), responseClass);
        }
    }
}
