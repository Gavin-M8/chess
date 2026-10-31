package dataaccess.sql;

import chess.ChessGame;
import dataaccess.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class MySQLDataAccess implements DataAccess {

    MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

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

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username),
              INDEX(password)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `auth` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username),
              INDEX(auth)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `gameName` varchar(256) NOT NULL,
              `gameID` int NOT NULL,
              'json' TEXT NOT NULL,
              'whiteUsername' varchar(256) DEFAULT NULL,
              'blackUsername' varchar(256) DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(gameName),
              INDEX(gameID)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not configure database");
        }
    }
}
