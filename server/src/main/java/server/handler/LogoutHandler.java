package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.LogoutService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            //get the auth token from the Authorization header
            String authToken = req.headers("authorization");

            //call the logout service
            logoutService.logout(authToken);

            //success
            res.status(200);
            return "{}";

        } catch (DataAccessException e) {
            //known errors (e.g., token not found)
            res.status(401);
            return new Gson().toJson(new ErrorMessage(e.getMessage()));

        } catch (Exception e) {
            //unexpected errors
            res.status(500);
            return new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private record ErrorMessage(String message) {}
}
