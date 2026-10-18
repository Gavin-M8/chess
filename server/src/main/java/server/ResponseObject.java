package server;

import chess.ChessGame;

import java.util.ArrayList;
import java.util.HashMap;

public interface ResponseObject {
    record ClearResponse(String message) implements ResponseObject {};
    record RegisterResponse(String username, String token, String message) implements ResponseObject {};
    record LoginResponse(String username, String token, String message) implements ResponseObject {};
    record LogoutResponse(String message) implements ResponseObject {};
    record ListGameResponse(ArrayList<HashMap<String, Object>> games, String message) implements ResponseObject {};
    record CreateGameResponse(int gameID, String message) implements ResponseObject {};
    record JoinGameResponse(String message) implements ResponseObject {};

}
