package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.sql.MySQLDataAccess;
import io.javalin.*;
import java.util.ArrayList;
import java.util.Map;
import service.AuthService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private boolean databaseAvailable = true;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        var gson = new Gson();

        MySQLDataAccess dao = new MySQLDataAccess();
        try {
            dao.configureDatabase();
        } catch (DataAccessException e) {
            databaseAvailable = false;
        }

        UserService us = new UserService(dao);
        AuthService as = new AuthService(dao);
        GameService gs = new GameService(dao);

        // Register your endpoints and exception handlers here.

        // clear /db DELETE
        javalin.delete("/db", ctx -> {
            if (!databaseAvailable) {
                ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));
            }
            boolean didClear = us.clear();
            if (didClear) {
                ResponseObject.ClearResponse response = new ResponseObject.ClearResponse(null);
                ctx.status(200).contentType("application/json").result(gson.toJson(response));
            } else {
                ResponseObject.ClearResponse response = new ResponseObject.ClearResponse("Error: Server error");
                ctx.status(500).contentType("application/json").result(gson.toJson(response));
            }

        });

        // register /user POST
        javalin.post("/user", ctx -> {
            if (!databaseAvailable) {
                ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));
            }
            RequestObject.RegisterRequest req;
            req = gson.fromJson(ctx.body(), RequestObject.RegisterRequest.class);

            ArrayList<Object> results = us.register(req.username(), req.password(), req.email());
            boolean canRegister = (boolean) results.getFirst();
            String message = (String) results.getLast();
            if (canRegister) {
                ResponseObject.RegisterResponse response = new ResponseObject.RegisterResponse(req.username(), message, null);
                ctx.status(200).contentType("application/json").result(gson.toJson(response));
            }
            else if (message.equals("Error: data access error")) {
                ResponseObject.RegisterResponse response = new ResponseObject.RegisterResponse(null, null, "Error: Server error");
                ctx.status(500).contentType("application/json").result(gson.toJson(response));
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
            if (!databaseAvailable) {
                ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));
            }
            RequestObject.LoginRequest req;
            req = gson.fromJson(ctx.body(), RequestObject.LoginRequest.class);

            ArrayList<Object> result = as.login(req.username(), req.password());
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
            if (!databaseAvailable) {
                ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));
            }
            String auth = ctx.header("authorization");
            if (auth == null) {
                ResponseObject.LogoutResponse response = new ResponseObject.LogoutResponse("Error: Unauthorized");
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                ArrayList<Object> result = as.logout(auth);
                boolean isAuthorized = (boolean) result.getFirst();
                String message = (String) result.getLast();
                if (!isAuthorized && message.equals("Error: data access error")) {
                    ResponseObject.LogoutResponse response = new ResponseObject.LogoutResponse("Error: Server error");
                    ctx.status(500).contentType("application/json").result(gson.toJson(response));
                } else if (!isAuthorized) {
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
            if (!databaseAvailable) {
                ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));
            }
            String auth = ctx.header("authorization");
            if (auth == null) {
                ResponseObject.ListGamesResponse response = new ResponseObject.ListGamesResponse(null, "Error: Unauthorized");
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                ArrayList<Object> results = gs.listGames(auth);
                boolean canListGames = (boolean) results.getFirst();
                if (!canListGames) {
                    String message = (String) results.getLast();
                    if (message.equals("Error: data access error")) {
                        ResponseObject.ListGamesResponse response = new ResponseObject.ListGamesResponse(null,"Error: Server error");
                        ctx.status(500).contentType("application/json").result(gson.toJson(response));
                    } else {
                        ResponseObject.ListGamesResponse response = new ResponseObject.ListGamesResponse(null,"Error: Unauthorized");
                        ctx.status(401).contentType("application/json").result(gson.toJson(response));
                    }
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
            if (!databaseAvailable) {
                ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));
            }
            String auth = ctx.header("authorization");
            if (auth == null) {
                ResponseObject.CreateGameResponse response = new ResponseObject.CreateGameResponse(null, "Error: Unauthorized");
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                RequestObject.CreateGameRequest req;
                req = gson.fromJson(ctx.body(), RequestObject.CreateGameRequest.class);

                ArrayList<Object> result = gs.createGame(auth, req.gameName());
                boolean isAuthorized = (boolean) result.getFirst();
                Integer gameID = (Integer) result.getLast();

                if (!isAuthorized && gameID != null) {
                    ResponseObject.CreateGameResponse response = new ResponseObject.CreateGameResponse(null, "Error: Server error");
                    ctx.status(500).contentType("application/json").result(gson.toJson(response));
                }
                else if (isAuthorized && gameID != null) {
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
            if (!databaseAvailable) {
                ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));
            }
            String auth = ctx.header("authorization");
            if (auth == null) {
                ResponseObject.JoinGameResponse response = new ResponseObject.JoinGameResponse("Error: Unauthorized");
                ctx.status(401).contentType("application/json").result(gson.toJson(response));
            }
            else {
                RequestObject.JoinGameRequest req;
                req = gson.fromJson(ctx.body(), RequestObject.JoinGameRequest.class);

                ArrayList<Object> result = gs.joinGame(req.playerColor(), req.gameID(), auth);
                boolean canJoinGame = (boolean) result.getFirst();
                String message = (String) result.getLast();

                if (!canJoinGame && message.equals("Error: data access error")) {
                    ResponseObject.JoinGameResponse response = new ResponseObject.JoinGameResponse("Error: Server error");
                    ctx.status(500).contentType("application/json").result(gson.toJson(response));
                }
                else if (!canJoinGame && message.equals("Error: Bad request")) {
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
