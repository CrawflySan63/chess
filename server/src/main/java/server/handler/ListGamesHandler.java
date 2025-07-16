package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import result.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            //get authToken from request header
            String authToken = req.headers("authorization");

            //call the service to get the list of games
            ListGamesResult result = gameService.listGames(authToken);

            //return success
            res.status(200);
            return gson.toJson(result);

        } catch (DataAccessException e) {
            //handle known errors
            res.status(e.getMessage().contains("unauthorized") ? 401 : 500);
            return gson.toJson(new ErrorMessage("Error: " + e.getMessage()));
        } catch (Exception e) {
            //catch unexpected exceptions
            res.status(500);
            return gson.toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private record ErrorMessage(String message) {}
}
