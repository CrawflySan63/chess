package server.handler;

import com.google.gson.Gson;
import server.util.HandlerUtils;
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
        } catch (Exception e) {
            return HandlerUtils.handleException(e, res);
        }
    }
}
