package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.handler.LoginHandler;
import server.handler.LogoutHandler;
import server.handler.RegisterHandler;
import service.LogoutService;
import spark.*;
import server.handler.ClearHandler;
import service.UserService;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //memory-based data store created
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        LogoutService logoutService = new LogoutService(dataAccess);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", new ClearHandler(dataAccess));
        Spark.post("/user", new RegisterHandler(userService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(logoutService));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
