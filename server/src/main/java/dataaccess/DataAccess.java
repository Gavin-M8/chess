package dataaccess;

import chess.ChessGame;

import java.util.ArrayList;
import java.util.Map;

public interface DataAccess {

    // user
    UserData getUser(String username) throws DataAccessException;
    String getUsername(String authToken) throws DataAccessException;
    boolean userExists(String username) throws DataAccessException;
    void createUser(String username, String password, String email) throws DataAccessException;

    // auth
    ArrayList<AuthData> getToken(String username) throws DataAccessException;
    boolean isAuthorized(String auth) throws DataAccessException;
    void createAuth(String username) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

    // game
    GameData getGame(Integer gameID) throws DataAccessException;
    Integer createGame(String gameName) throws DataAccessException;
    boolean colorTaken(ChessGame.TeamColor playerColor, Integer gameID) throws DataAccessException;
    boolean gamesEmpty() throws DataAccessException;
    ArrayList<Map<String, Object>> getGamesList() throws DataAccessException;
    void joinGame(ChessGame.TeamColor playerColor, Integer gameID, String authToken) throws DataAccessException;

    // clear data
    void clear() throws DataAccessException;


}
