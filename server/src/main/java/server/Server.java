package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
            req = gson.fromJson(ctx.body(), RequestObject.RegisterRequest.class);

            ArrayList<Object> results = da.register(req.username(), req.password(), req.email());
            boolean canRegister = (boolean) results.getFirst();
            String message = (String) results.getLast();
            if (canRegister) {
                ResponseObject.RegisterResponse response = new ResponseObject.RegisterResponse(req.username(), message, null);
                ctx.status(200).contentType("application/json").result(gson.toJson(response));
            }
            else if (message.equals("Error: User already registered")){
                ResponseObject.RegisterResponse response = new ResponseObject.RegisterResponse(null, null, message);
                ctx.status(403).contentType("application/json").result(gson.toJson(response));
            }
            else {
                ResponseObject.RegisterResponse response = new ResponseObject.RegisterResponse(null, null, message);
                ctx.status(400).contentType("application/json").result(gson.toJson(response));
            }

        });


        // login /session POST
        javalin.post("/session", ctx -> {
            RequestObject.LoginRequest req;
            req = gson.fromJson(ctx.body(), RequestObject.LoginRequest.class);

            ArrayList<Object> result = da.login(req.username(), req.password());
            boolean canLogin = (boolean) result.getFirst();
            String message = (String) result.get(1);

            if (!canLogin && message.equals("Error: Bad request")) {
                ResponseObject.LoginResponse response = new ResponseObject.LoginResponse(null, null, message);
                ctx.status(400).contentType("application/json").result(gson.toJson(response));
            }
            else if (!canLogin && message.equals("Error: User not registered")) {
                ResponseObject.LoginResponse response = new ResponseObject.LoginResponse(null, null, message);
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else if (!canLogin && message.equals("Error: Password incorrect")) {
                ResponseObject.LoginResponse response = new ResponseObject.LoginResponse(null, null, message);
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                ResponseObject.LoginResponse response = new ResponseObject.LoginResponse(req.username(), message, null);
                ctx.status(200).contentType("application/json").result(gson.toJson(response));
            }
        });

        // logout /session DELETE
        javalin.delete("/session", ctx -> {
            String auth = ctx.header("authorization");
            if (auth == null) {
                ResponseObject.LogoutResponse response = new ResponseObject.LogoutResponse("Error: Unauthorized");
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                boolean isAuthorized = da.logout(auth);
                if (!isAuthorized) {
                    ResponseObject.LogoutResponse response = new ResponseObject.LogoutResponse("Error: Unauthorized");
                    ctx.status(401).contentType("application/json").result(gson.toJson(response));
                }
                else {
                    ResponseObject.LogoutResponse response = new ResponseObject.LogoutResponse(null);
                    ctx.status(200).contentType("application/json").result(gson.toJson(response));
                }
            }

        });

        // list games /game GET
        javalin.get("/game", ctx -> {
            String auth = ctx.header("authorization");
            if (auth == null) {
                ResponseObject.ListGamesResponse response = new ResponseObject.ListGamesResponse(null, "Error: Unauthorized");
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                ArrayList<Object> results = da.listGames(auth);
                boolean canListGames = (boolean) results.getFirst();
                if (!canListGames) {
                    ResponseObject.ListGamesResponse response = new ResponseObject.ListGamesResponse(null,"Error: Unauthorized");
                    ctx.status(401).contentType("application/json").result(gson.toJson(response));
                }
                else {
                    ArrayList<Map<String, Object>> gamesList = (ArrayList<Map<String, Object>>) results.getLast();
                    ResponseObject.ListGamesResponse response = new ResponseObject.ListGamesResponse(gamesList, null);
                    ctx.status(200).contentType("application/json").result(gson.toJson(response));
                }
            }
        });

        // create game /game POST
        javalin.post("/game", ctx -> {
            String auth = ctx.header("authorization");
            if (auth == null) {
                ResponseObject.CreateGameResponse response = new ResponseObject.CreateGameResponse(null, "Error: Unauthorized");
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                RequestObject.CreateGameRequest req;
                req = gson.fromJson(ctx.body(), RequestObject.CreateGameRequest.class);

                ArrayList<Object> result = da.createGame(auth, req.gameName());
                boolean isAuthorized = (boolean) result.getFirst();
                Integer gameID = (Integer) result.getLast();

                if (isAuthorized && gameID != null) {
                    ResponseObject.CreateGameResponse response = new ResponseObject.CreateGameResponse(gameID, null);
                    ctx.status(200).contentType("application/json").result(gson.toJson(response));
                } else if (isAuthorized) {
                    ResponseObject.CreateGameResponse response = new ResponseObject.CreateGameResponse(null, "Error: No game name");
                    ctx.status(400).contentType("application/json").result(gson.toJson(response));
                } else {
                    ResponseObject.CreateGameResponse response = new ResponseObject.CreateGameResponse(null,"Error: Unauthorized");
                    ctx.status(401).contentType("application/json").result(gson.toJson(response));
                }
            }
        });

        // join game /game PUT
        javalin.put("/game", ctx -> {
            String auth = ctx.header("authorization");
            if (auth == null) {
                ResponseObject.JoinGameResponse response = new ResponseObject.JoinGameResponse("Error: Unauthorized");
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                RequestObject.JoinGameRequest req;
                req = gson.fromJson(ctx.body(), RequestObject.JoinGameRequest.class);

                ArrayList<Object> result = da.joinGame(req.playerColor(), req.gameID(), auth);
                boolean canJoinGame = (boolean) result.getFirst();
                String message = (String) result.getLast();

                if (!canJoinGame && message.equals("Error: Bad request")) {
                    ResponseObject.JoinGameResponse response = new ResponseObject.JoinGameResponse(message);
                    ctx.status(400).contentType("application/json").result(gson.toJson(response));
                }
                else if (!canJoinGame && message.equals("Error: Unauthorized")) {
                    ResponseObject.JoinGameResponse response = new ResponseObject.JoinGameResponse(message);
                    ctx.status(401).contentType("application/json").result(gson.toJson(response));
                }
                else if (!canJoinGame && message.equals("Error: Already taken")) {
                    ResponseObject.JoinGameResponse response = new ResponseObject.JoinGameResponse(message);
                    ctx.status(403).contentType("application/json").result(gson.toJson(response));
                }
                else {
                    ResponseObject.JoinGameResponse response = new ResponseObject.JoinGameResponse(message);
                    ctx.status(200).contentType("application/json").result(gson.toJson(response));
                }
            }

        });


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
