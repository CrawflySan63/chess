package server.util;

import com.google.gson.Gson;
import spark.Response;
import dataaccess.DataAccessException;

public class HandlerUtils {

    private static final Gson G_Son = new Gson();

    public static String handleException(Exception e, Response res) {
        if (e instanceof DataAccessException dae) {
            String msg = dae.getMessage();
            if (msg.contains("bad request")) {
                res.status(400);
            } else if (msg.contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return G_Son.toJson(new ErrorMessage(msg));
        } else {
            res.status(500);
            return G_Son.toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private record ErrorMessage(String message) {}
}