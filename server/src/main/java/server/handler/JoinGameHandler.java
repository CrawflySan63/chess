package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.JoinGameRequest;
import server.util.HandlerUtils;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class JoinGameHandler implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            String authToken = req.headers("authorization");
            JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);

            gameService.joinGame(authToken, request);

            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            return HandlerUtils.handleException(e, res);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}