package server.util;

import com.google.gson.Gson;
import spark.Response;
import dataaccess.DataAccessException;

public class HandlerUtils {

    private static final Gson GSON = new Gson();

    public static String handleException(Exception e, Response res) {
        if (e instanceof DataAccessException dae) {
            String msg = dae.getMessage();
            String lowerMsg = msg.toLowerCase(); // normalize for matching

            if (lowerMsg.contains("bad request")) {
                res.status(400);
            } else if (lowerMsg.contains("unauthorized")) {
                res.status(401);
            } else if (lowerMsg.contains("already taken")) {
                res.status(403);
            } else if (lowerMsg.contains("internal server error")) {
                res.status(500);
            } else {
                res.status(500);
            }

            return GSON.toJson(new ErrorMessage(msg));
        } else {
            res.status(500);
            return GSON.toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private record ErrorMessage(String message) {}
}