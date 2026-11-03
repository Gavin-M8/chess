package server;

import com.google.gson.Gson;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.Map;

public class ResponseUtilities {
    static Gson gson = new Gson();

    public static void sendClearResponse(Context ctx, int code, String message) {
        ResponseObject.ClearResponse response = new ResponseObject.ClearResponse(message);
        ctx.status(code).contentType("application/json").result(gson.toJson(response));
    }

    public static void sendRegisterResponse(Context ctx, int code, String username, String authToken, String message) {
        ResponseObject.RegisterResponse response = new ResponseObject.RegisterResponse(username, authToken, message);
        ctx.status(code).contentType("application/json").result(gson.toJson(response));
    }

    public static void sendLoginResponse(Context ctx, int code, String username, String authToken, String message) {
        ResponseObject.LoginResponse response = new ResponseObject.LoginResponse(username, authToken, message);
        ctx.status(code).contentType("application/json").result(gson.toJson(response));
    }

    public static void sendLogoutResponse(Context ctx, int code, String message) {
        ResponseObject.LogoutResponse response = new ResponseObject.LogoutResponse(message);
        ctx.status(code).contentType("application/json").result(gson.toJson(response));
    }

    public static void sendJoinGameResponse(Context ctx, int code, String message) {
        ResponseObject.JoinGameResponse response = new ResponseObject.JoinGameResponse(message);
        ctx.status(code).contentType("application/json").result(gson.toJson(response));
    }

    public static void sendCreateGameResponse(Context ctx, int code, Integer gameID, String message) {
        ResponseObject.CreateGameResponse response = new ResponseObject.CreateGameResponse(gameID, message);
        ctx.status(code).contentType("application/json").result(gson.toJson(response));
    }

    public static void sendListGamesResponse(Context ctx, int code, ArrayList<Map<String, Object>> games, String message) {
        ResponseObject.ListGamesResponse response = new ResponseObject.ListGamesResponse(games, message);
        ctx.status(code).contentType("application/json").result(gson.toJson(response));
    }

}
