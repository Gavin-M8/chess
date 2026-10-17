package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        // clear /db DELETE
        javalin.delete("/db", ctx -> ctx.result("{}"));

        // register /user POST
        javalin.post("/user", ctx -> ctx.result("{}"));

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
