package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.DataAccess;

import java.util.HashMap;
import java.util.Map;

public class ClearHandler implements Route {
    private final ClearService service;

    public ClearHandler(DataAccess dataAccess) {
        this.service = new ClearService(dataAccess);
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            service.clear();

            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(500);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error: " + e.getMessage());
            return new Gson().toJson(error);
        }
    }
}
