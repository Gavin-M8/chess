package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        var da = new MemoryDataAccess();
        var gson = new Gson();

        // Register your endpoints and exception handlers here.

        // clear /db DELETE
        javalin.delete("/db", ctx -> {
            da.clear();
            ResponseObject.ClearResponse response = new ResponseObject.ClearResponse(null);
            ctx.status(200).contentType("application/json").result(gson.toJson(response));
        });

        // register /user POST
        javalin.post("/user", ctx -> {
            RequestObject.RegisterRequest req;
            try {
                req = gson.fromJson(ctx.body(), RequestObject.RegisterRequest.class);
            }
            catch (JsonSyntaxException exception) {
                throw new BadRequestResponse("Error: bad request");
            }
            da.register(req.username(), req.password(), req.email());
            String token = da.getToken(req.username()).getAuthtoken();
            ResponseObject.RegisterResponse response = new ResponseObject.RegisterResponse(req.username(), token, null);

            ctx.status(200).contentType("application/json").result(gson.toJson(response));
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
