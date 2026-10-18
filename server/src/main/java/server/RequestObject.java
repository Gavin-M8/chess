package server;

import chess.ChessGame;

public interface RequestObject {
    record ClearRequest() implements RequestObject {};
    record RegisterRequest(String username, String password, String email) implements RequestObject {};
    record LoginRequest(String username, String password) implements RequestObject {};
    record LogoutRequest() implements RequestObject {};
    record ListGameRequest() implements RequestObject {};
    record CreateGameRequest(String gameName) implements RequestObject {};
    record JoinGameRequest(ChessGame.TeamColor playerColor, int gameID) implements RequestObject {};
}
