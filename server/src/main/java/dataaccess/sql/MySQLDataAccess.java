package dataaccess.sql;

import chess.ChessGame;
import dataaccess.AuthData;
import dataaccess.DataAccess;
import dataaccess.GameData;
import dataaccess.UserData;

import java.util.ArrayList;
import java.util.Map;

public class MySQLDataAccess implements DataAccess {
    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public String getUsername(String authToken) {
        return "";
    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public void createUser(String username, String password, String email) {

    }

    @Override
    public ArrayList<AuthData> getToken(String username) {
        return null;
    }

    @Override
    public boolean isAuthorized(String auth) {
        return false;
    }

    @Override
    public void createAuth(String username) {

    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public Integer createGame(String gameName) {
        return 0;
    }

    @Override
    public boolean colorTaken(ChessGame.TeamColor playerColor, Integer gameID) {
        return false;
    }

    @Override
    public boolean gamesEmpty() {
        return false;
    }

    @Override
    public ArrayList<Map<String, Object>> getGamesList() {
        return null;
    }

    @Override
    public void joinGame(ChessGame.TeamColor playerColor, Integer gameID, String authToken) {

    }

    @Override
    public void clear() {

    }
}
