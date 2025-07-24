package server;

import dataaccess.*;
import server.handler.*;
import service.GameService;
import service.LogoutService;
import spark.*;
import service.UserService;

public class Server {

    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.configureTables();
        } catch (DataAccessException e) {
            System.err.println("Database setup failed: " + e.getMessage());
            e.printStackTrace();
            return -1; // signal error
        }

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //memory-based data store created
        //DataAccess dataAccess = new MemoryDataAccess();
        DataAccess dataAccess = new MySqlDataAccess();
        UserService userService = new UserService(dataAccess);
        LogoutService logoutService = new LogoutService(dataAccess);
        GameService gameService = new GameService(dataAccess);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", new ClearHandler(dataAccess));
        Spark.post("/user", new RegisterHandler(userService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(logoutService));
        Spark.get("/game", new ListGamesHandler(gameService));
        Spark.post("/game", new CreateGameHandler(gameService));
        Spark.put("/game", new JoinGameHandler(gameService));

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
