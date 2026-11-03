package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DatabaseManager;
import dataaccess.DataAccessException;
import model.UserData;
import model.GameData;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() {}

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String password = rs.getNString("password");
                        String email = rs.getNString("email");
                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
        return null;
    }

    @Override
    public String getUsername(String auth) throws DataAccessException {
        AuthData authToken = new AuthData(auth);
        String json = new Gson().toJson(authToken);
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE auth=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, json);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getNString("username");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
        return null;
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
        return false;
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        var hashedPassword = hashPassword(password);
        executeUpdate(statement, username, hashedPassword, email);
    }


    @Override
    public ArrayList<AuthData> getToken(String username) throws DataAccessException {
        ArrayList<AuthData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT auth FROM auth WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("auth");
                        AuthData auth = new Gson().fromJson(json, AuthData.class);
                        result.add(auth);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
        return result;

    }

    @Override
    public boolean isAuthorized(String auth) throws DataAccessException {
        AuthData authToken = new AuthData(auth);
        String json = new Gson().toJson(authToken);
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT auth FROM auth WHERE auth=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, json);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
        return false;
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auth (username, auth) VALUES (?, ?)";
        String authString = UUID.randomUUID().toString();
        var auth = new AuthData(authString);
        String json = new Gson().toJson(auth);
        executeUpdate(statement, username, json);
        return authString;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE auth FROM auth WHERE auth=?";
        var auth = new AuthData(authToken);
        String json = new Gson().toJson(auth);
        executeUpdate(statement, json);
    }


    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("game");
                        GameData game = new Gson().fromJson(json, GameData.class);
                        return game;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
        return null;
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        SecureRandom secure = new SecureRandom();
        var gameID = secure.nextInt(1000,10000);
        GameData newGame = new GameData(gameID, null, null, gameName);

        var statement = "INSERT INTO games (gameName, gameID, game) VALUES (?, ?, ?)";
        String game = new Gson().toJson(newGame);
        executeUpdate(statement, gameName, gameID, game);
        return gameID;
    }

    @Override
    public boolean colorTaken(ChessGame.TeamColor playerColor, Integer gameID) throws DataAccessException {
        String statement;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            statement = "SELECT whiteUsername FROM games WHERE gameID=?";
        }
        else {
            statement = "SELECT blackUsername FROM games WHERE gameID=?";
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getObject(1) != null) {return true;}
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
        return false;
    }

    @Override
    public boolean gamesEmpty() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
        return true;
    }

    @Override
    public ArrayList<Map<String, Object>> getGamesList() throws DataAccessException {
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var json = rs.getString("game");
                        GameData game = new Gson().fromJson(json, GameData.class);
                        var gameMap = game.getGame();
                        result.add(gameMap);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Unable to read data");
        }
        return result;
    }

    @Override
    public void joinGame(ChessGame.TeamColor playerColor, Integer gameID, String authToken) throws DataAccessException {
        String statement;
        GameData game = getGame(gameID);
        String username = getUsername(authToken);

        if (playerColor == ChessGame.TeamColor.WHITE) {
            game.setWhiteUsername(username);
            statement = "UPDATE games SET whiteUsername=?, game=? WHERE gameID=?";
        }
        else {
            game.setBlackUsername(username);
            statement = "UPDATE games SET blackUsername=?, game=? WHERE gameID=?";
        }

        String gameJson = new Gson().toJson(game);
        executeUpdate(statement, username, gameJson, gameID);
    }


    @Override
    public void clear() throws DataAccessException {
        ArrayList<String> statements = new ArrayList<>();
        statements.add(0, "TRUNCATE TABLE users");
        statements.add(1, "TRUNCATE TABLE auth");
        statements.add(2, "TRUNCATE TABLE games");

        for (String statement : statements) {
            executeUpdate(statement);
        }
    }

    public String hashPassword(String password) {
       return BCrypt.hashpw(password, BCrypt.gensalt());
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
              `game` TEXT NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(gameName),
              INDEX(gameID)
            )
            """,
    };

    public void configureDatabase() throws DataAccessException {
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
