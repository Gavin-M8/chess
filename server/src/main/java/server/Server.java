package server;

import dataaccess.AuthData;
import dataaccess.MemoryDataAccess;
import dataaccess.UserData;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        var da = new MemoryDataAccess();

        // Register your endpoints and exception handlers here.

        // clear /db DELETE
        javalin.delete("/db", ctx -> {
                    da.clear();
                    ctx.result("{}");
                });

        // register /user POST
        javalin.post("/user", ctx -> {
                    UserData user = ctx.bodyAsClass(UserData.class);
                    String username = user.getUsername();
                    da.register(user);
                    AuthData token = da.getToken(username);
                    ctx.result(String.format("username: %s, authToken: %s", username, token.getAuthtoken()));
                });


        // login /session POST
        javalin.post("/session", ctx -> ctx.result("{}"));

        // logout /session DELETE
        javalin.delete("/session", ctx -> ctx.result("{}"));

        // list games /game GET
        javalin.get("/game", ctx -> ctx.result("{}"));

        // create game /game POST
        javalin.post("/game", ctx -> ctx.result("{}"));

        // join game /game PUT
        javalin.put("/game", ctx -> ctx.result("{}"));


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
