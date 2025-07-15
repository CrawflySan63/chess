package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;
import request.RegisterRequest;
import result.RegisterResult;
import dataaccess.DataAccessException;

public class RegisterHandler implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            //step 1 parse rquest JSON into RegisterRequest object
            RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);

            //step 2 call the service
            RegisterResult result = userService.register(request);

            //step 3 set success status and return result
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            //step 4 handle known errors
            if (e.getMessage().contains("bad request")) {
                res.status(400);
            } else if (e.getMessage().contains("already taken")) {
                res.status(403);
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
