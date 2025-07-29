package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.LoginRequest;
import result.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import server.util.HandlerUtils;

public class LoginHandler implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            //step 1 parse request JSON into RegisterRequest object
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

            //step 2 call the service
            LoginResult result = userService.login(loginRequest);

            //step 3 set success status and return result
            res.status(200);
            return gson.toJson(result);

        } catch (Exception e) {
            return HandlerUtils.handleException(e, res);
        }
    }
}
