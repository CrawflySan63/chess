package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.handler.RegisterHandler;
import spark.*;
import server.handler.ClearHandler;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //memory-based data store created
        DataAccess dataAccess = new MemoryDataAccess();

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", new ClearHandler());
        Spark.post("/user", new RegisterHandler(dataAccess));

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
