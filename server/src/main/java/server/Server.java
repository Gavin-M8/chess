package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.sql.MySQLDataAccess;
import io.javalin.*;
import java.util.ArrayList;
import java.util.Map;
import service.*;

public class Server {

    private final Javalin javalin;
    private boolean databaseAvailable = true;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        var gson = new Gson();
        MySQLDataAccess dao = new MySQLDataAccess();
        try {dao.configureDatabase();}
        catch (DataAccessException e) {databaseAvailable = false;}
        UserService us = new UserService(dao);
        AuthService as = new AuthService(dao);
        GameService gs = new GameService(dao);

        javalin.delete("/db", ctx -> {
            if (!databaseAvailable) {ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));}
            boolean didClear = us.clear();
            if (didClear) {ResponseUtilities.sendClearResponse(ctx, 200, null);}
            else {ResponseUtilities.sendClearResponse(ctx, 500, "Error: Server error");}});

        javalin.post("/user", ctx -> {
            if (!databaseAvailable) {ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));}
            RequestObject.RegisterRequest req;
            req = gson.fromJson(ctx.body(), RequestObject.RegisterRequest.class);
            ArrayList<Object> results = us.register(req.username(), req.password(), req.email());
            boolean canRegister = (boolean) results.getFirst();
            String message = (String) results.getLast();
            if (canRegister) {ResponseUtilities.sendRegisterResponse(ctx, 200, req.username(), message, null);}
            else if (message.equals("Error: data access error")) {ResponseUtilities.sendRegisterResponse(ctx, 500, null, null, "Error: Server error");}
            else if (message.equals("Error: User already registered")){ResponseUtilities.sendRegisterResponse(ctx, 403, null, null, message);}
            else {ResponseUtilities.sendRegisterResponse(ctx, 400, null, null, message);}});

        javalin.post("/session", ctx -> {
            if (!databaseAvailable) {ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));}
            RequestObject.LoginRequest req;
            req = gson.fromJson(ctx.body(), RequestObject.LoginRequest.class);
            ArrayList<Object> result = as.login(req.username(), req.password());
            boolean canLogin = (boolean) result.getFirst();
            String message = (String) result.get(1);
            if (!canLogin && message.equals("Error: Bad request")) {ResponseUtilities.sendLoginResponse(ctx, 400, null, null, message);}
            else if (!canLogin && message.equals("Error: Data access error")) {ResponseUtilities.sendLoginResponse(ctx, 500, null, null, message);}
            else if (!canLogin && message.equals("Error: User not registered")) {ResponseUtilities.sendLoginResponse(ctx, 401, null, null, message);}
            else if (!canLogin && message.equals("Error: Password incorrect")) {ResponseUtilities.sendLoginResponse(ctx, 401, null, null, message);}
            else {ResponseUtilities.sendLoginResponse(ctx, 200, req.username(), message, null);}});

        javalin.delete("/session", ctx -> {
            if (!databaseAvailable) {ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));}
            String auth = ctx.header("authorization");
            if (auth == null) {ResponseUtilities.sendLogoutResponse(ctx, 401, "Error: Unauthorized");}
            else {
                ArrayList<Object> result = as.logout(auth);
                boolean isAuthorized = (boolean) result.getFirst();
                String message = (String) result.getLast();
                if (!isAuthorized && message.equals("Error: Data access error")) {ResponseUtilities.sendLogoutResponse(ctx, 500, "Error: Server error");}
                else if (!isAuthorized) {ResponseUtilities.sendLogoutResponse(ctx, 401, "Error: Unauthorized");}
                else {ResponseUtilities.sendLogoutResponse(ctx, 200, null);}}});

        javalin.get("/game", ctx -> {
            if (!databaseAvailable) {ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));}
            String auth = ctx.header("authorization");
            if (auth == null) {ResponseUtilities.sendListGamesResponse(ctx, 401, null, "Error: Unauthorized");}
            else {
                ArrayList<Object> results = gs.listGames(auth);
                boolean canListGames = (boolean) results.getFirst();
                if (!canListGames) {
                    String message = (String) results.getLast();
                    if (message.equals("Error: data access error")) {ResponseUtilities.sendListGamesResponse(ctx, 500, null, "Error: Server error");}
                    else {ResponseUtilities.sendListGamesResponse(ctx, 401, null, "Error: Unauthorized");}
                }
                else {
                    ArrayList<Map<String, Object>> gamesList = (ArrayList<Map<String, Object>>) results.getLast();
                    ResponseUtilities.sendListGamesResponse(ctx, 200, gamesList, null);
                }}});

        javalin.post("/game", ctx -> {
            if (!databaseAvailable) {ResponseUtilities.sendCreateGameResponse(ctx, 500, null, "Error: Server error");}
            String auth = ctx.header("authorization");
            if (auth == null) {ResponseUtilities.sendCreateGameResponse(ctx, 401, null, "Error: Unauthorized");}
            else {
                RequestObject.CreateGameRequest req;
                req = gson.fromJson(ctx.body(), RequestObject.CreateGameRequest.class);
                ArrayList<Object> result = gs.createGame(auth, req.gameName());
                boolean isAuthorized = (boolean) result.get(0);
                String message = (String) result.get(1);
                Integer gameID = (Integer) result.get(2);
                if (!isAuthorized && message.equals("Error: data access exception")) {ResponseUtilities.sendCreateGameResponse(ctx, 500, null, "Error: Server error");}
                else if (isAuthorized && gameID != null) {ResponseUtilities.sendCreateGameResponse(ctx, 200, gameID, null);}
                else if (isAuthorized) {ResponseUtilities.sendCreateGameResponse(ctx, 400, null, "Error: No game name");}
                else {ResponseUtilities.sendCreateGameResponse(ctx, 401, null, "Error: Unauthorized");}}});

        javalin.put("/game", ctx -> {
            if (!databaseAvailable) {ctx.status(500).contentType("application/json").result(gson.toJson("Error: Database unavailable"));}
            String auth = ctx.header("authorization");
            if (auth == null) {ResponseUtilities.sendJoinGameResponse(ctx, 401, "Error: Unauthorized");}
            else {
                RequestObject.JoinGameRequest req;
                req = gson.fromJson(ctx.body(), RequestObject.JoinGameRequest.class);
                ArrayList<Object> result = gs.joinGame(req.playerColor(), req.gameID(), auth);
                boolean canJoinGame = (boolean) result.getFirst();
                String message = (String) result.getLast();
                if (!canJoinGame && message.equals("Error: data access error")) {ResponseUtilities.sendJoinGameResponse(ctx, 500, "Error: Server error");}
                else if (!canJoinGame && message.equals("Error: Bad request")) {ResponseUtilities.sendJoinGameResponse(ctx, 400, message);}
                else if (!canJoinGame && message.equals("Error: Unauthorized")) {ResponseUtilities.sendJoinGameResponse(ctx, 401, message);}
                else if (!canJoinGame && message.equals("Error: Already taken")) {ResponseUtilities.sendJoinGameResponse(ctx, 403, message);}
                else {ResponseUtilities.sendJoinGameResponse(ctx, 200, message);}}});}

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {javalin.stop();}
}
