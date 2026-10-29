package dataaccess;

import chess.ChessGame;

import java.util.ArrayList;

public interface DataAccess {

    // user
    UserData getUser(String username);
    String getUsername(String authToken);
    boolean userExists(String username);
    void createUser(String username, String password, String email);

    // auth
    ArrayList<AuthData> getToken(String username);
    boolean isAuthorized(AuthData auth);
    void createAuth(String username);
    void deleteAuth(String authToken);

    // game
    GameData getGame(Integer gameID);
    Integer createGame(String gameName);
    boolean colorTaken(ChessGame.TeamColor playerColor, Integer gameID);

    // clear data
    void clear();


}
