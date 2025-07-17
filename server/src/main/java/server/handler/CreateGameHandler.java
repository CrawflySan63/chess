package server.handler;

import com.google.gson.Gson;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.GameService;
import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import spark.Route;
import server.util.HandlerUtils;

public class CreateGameHandler implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            //extract auth token
            String authToken = req.headers("authorization");

            //deserialize request body to get gameRequest
            CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);

            //call service method
            CreateGameResult result = gameService.createGame(request, authToken);

            //success response
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            return HandlerUtils.handleException(e, res);
        }
    }
}