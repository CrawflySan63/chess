package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.LoginRequest;
import result.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            //step 1 parse rquest JSON into RegisterRequest object
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

            //step 2 call the service
            LoginResult result = userService.login(loginRequest);

            //step 3 set success status and return result
            res.status(200);
            return gson.toJson(result);

        } catch (DataAccessException e) {
            //step 4 handle known errors
            if (e.getMessage().contains("bad request")) {
                res.status(400);
            } else if (e.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return gson.toJson(new ErrorMessage(e.getMessage()));

        } catch (Exception e) {
            //step 5 catch unexpected errors
            res.status(500);
            return gson.toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private record ErrorMessage(String message) {}
}
