package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess {

    MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getNString("username"), rs.getNString("password"), rs.getNString("email"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
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
    public void clear() throws DataAccessException {
        var statement = """
                TRUNCATE TABLE users
                TRUNCATE TABLE auth
                TRUNCATE TABLE games
                """;
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Unable to update database entry");
        }
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
