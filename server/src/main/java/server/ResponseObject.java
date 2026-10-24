package server;

import java.util.ArrayList;

public interface ResponseObject {
    record ClearResponse(String message) implements ResponseObject {};
    record RegisterResponse(String username, String authToken, String message) implements ResponseObject {};
    record LoginResponse(String username, String authToken, String message) implements ResponseObject {};
    record LogoutResponse(String message) implements ResponseObject {};
    record ListGamesResponse(ArrayList<java.util.Map<String, Object>> games, String message) implements ResponseObject {};
    record CreateGameResponse(Integer gameID, String message) implements ResponseObject {};
    record JoinGameResponse(String message) implements ResponseObject {};

}
