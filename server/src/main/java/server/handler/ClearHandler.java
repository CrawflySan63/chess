package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class ClearHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        try {
            ClearService service = new ClearService(new MemoryDataAccess());
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
