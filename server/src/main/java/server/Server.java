package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;

import java.util.ArrayList;

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
        javalin.post("/session", ctx -> {
            RequestObject.LoginRequest req;
            try {
                req = gson.fromJson(ctx.body(), RequestObject.LoginRequest.class);
            }
            catch (JsonSyntaxException exception) {
                throw new BadRequestResponse("Error: bad request");
            }
            ArrayList<Object> result = da.login(req.username(), req.password());
            boolean canLogin = (boolean) result.getFirst();
            String message = (String) result.get(1);
            if (!canLogin && message.equals("User not registered")) {
                ResponseObject.LoginResponse response = new ResponseObject.LoginResponse(req.username(), null, message);
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else if (!canLogin && message.equals("Password incorrect")) {
                ResponseObject.LoginResponse response = new ResponseObject.LoginResponse(req.username(), null, message);
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                ResponseObject.LoginResponse response = new ResponseObject.LoginResponse(req.username(), message, null);
                ctx.status(200).contentType("application/json").result(gson.toJson(response));
            }
        });

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
