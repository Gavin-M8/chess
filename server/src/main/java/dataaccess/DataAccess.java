package dataaccess;

import chess.ChessGame;

import java.util.ArrayList;
import java.util.Map;

public interface DataAccess {

    // user
    UserData getUser(String username);
    String getUsername(String authToken);
    boolean userExists(String username);
    void createUser(String username, String password, String email);

    // auth
    ArrayList<AuthData> getToken(String username);
    boolean isAuthorized(String auth);
    void createAuth(String username);
    void deleteAuth(String authToken);

    // game
    GameData getGame(Integer gameID);
    Integer createGame(String gameName);
    boolean colorTaken(ChessGame.TeamColor playerColor, Integer gameID);
    boolean gamesEmpty();
    ArrayList<Map<String, Object>> getGamesList();
    void joinGame(ChessGame.TeamColor playerColor, Integer gameID, String authToken);

    // clear data
    void clear();


}
