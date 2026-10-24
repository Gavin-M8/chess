package dataaccess;

import chess.ChessGame;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryDataAccess {
    private HashMap<String, UserData> users;
    private HashMap<String, GameData> games;
    private HashMap<String, AuthData> authtokens;

    public MemoryDataAccess() {
        this.users = new HashMap<>();
        this.games = new HashMap<>();
        this.authtokens = new HashMap<>();
    }

    public void clear() {
        users.clear();
    }

    public ArrayList<Object> register(String username, String password, String email) {
        ArrayList<Object> results = new ArrayList<>();
        if (username == null || password == null || email == null) {
            results.addFirst(false);
            results.addLast("Error: Bad Request");
        }
        else if (userExists(username)) {
            results.addFirst(false);
            results.addLast("Error: User already registered");
        }
        else {
            var user = new UserData(username, password, email);
            users.put(user.getUsername(), user);
            AuthData token = new AuthData(UUID.randomUUID().toString());
            authtokens.put(user.getUsername(), token);

            results.addFirst(true);
            results.addLast(token.getAuthToken());
        }
        return results;
    }

    public ArrayList<Object> login(String username, String password) {
        ArrayList<Object> result = new ArrayList<>();
        if (username == null || password == null) {
            result.add(0, false);
            result.add(1, "Error: Bad request");
        }
        else if (!userExists(username)) {
            result.add(0, false);
            result.add(1, "Error: User not registered");
        }
        else if (!users.get(username).getPassword().equals(password)) {
            result.add(0, false);
            result.add(1, "Error: Password incorrect");
        }
        else {
            result.addFirst(true);
            AuthData token = new AuthData(UUID.randomUUID().toString());
            authtokens.put(username, token);
            result.add(1, token.getAuthToken());
        }
        return result;
    }

    public boolean logout(String authToken) {
        AuthData potentialAuth = new AuthData(authToken);
        boolean canLogout = isAuthorized(potentialAuth);
        if (canLogout) {
            authtokens.values().removeIf(auth -> auth.equals(potentialAuth));
        }
        return canLogout;
    }

    public ArrayList<Map<String, Object>> listGames(String authToken) {
        AuthData potentialAuth = new AuthData(authToken);

        if (isAuthorized(potentialAuth) && !games.isEmpty()) {
            ArrayList<Map<String, Object>> gamesList = new ArrayList<>();

            for (GameData game : games.values()) {
                gamesList.add(game.getGame());
            }

            return gamesList;
        }
        else if (games.isEmpty()) {
            return new ArrayList<>();
        }
        else {
            return null;
        }
    }

    public ArrayList<Object> createGame(String authToken, String gameName) {
        ArrayList<Object> results = new ArrayList<>();
        AuthData potentialAuth = new AuthData(authToken);

        if (isAuthorized(potentialAuth) && gameName != null) {
            SecureRandom secure = new SecureRandom();
            int gameID = secure.nextInt(1000,10000);
            results.addFirst(true);
            results.addLast(gameID);

            GameData newGame = new GameData(gameID, null, null, gameName);
            games.put(gameName, newGame);
        }
        else if (isAuthorized(potentialAuth)) {
            results.addFirst(true);
            results.addLast(null);
        }
        else {
            results.addFirst(false);
            results.addLast(null);
        }

        return results;
    }

    public ArrayList<Object> joinGame(ChessGame.TeamColor playerColor, Integer gameID, String authToken) {
        ArrayList<Object> results = new ArrayList<>();
        AuthData potentialAuth = new AuthData(authToken);

        if (playerColor == null || gameID == null) {
            results.addFirst(false);
            results.addLast("Error: Bad request");
        }
        else if (!isAuthorized(potentialAuth)) {
            results.addFirst(false);
            results.addLast("Error: Unauthorized");
        }
        else if (colorTaken(playerColor, gameID)) {
            results.addFirst(false);
            results.addLast("Error: Already taken");
        }
        else {
            results.addFirst(true);
            results.addLast(null);

        }

        return results;
    }

    private boolean userExists(String username) {
        return users.containsKey(username);
    }

    private boolean isAuthorized(AuthData auth) {
        return authtokens.containsValue(auth);
    }

    private boolean colorTaken(ChessGame.TeamColor playerColor, Integer gameID) {
        for (GameData game : games.values()) {
            if (playerColor == ChessGame.TeamColor.BLACK) {
                if (game.getBlackUsername() != null) {
                    return true;
                }
            }
            else {
                if (game.getWhiteUsername() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public UserData getUser(String username) {
        return users.getOrDefault(username, null);
    }

    public AuthData getToken(String username) {
        return authtokens.getOrDefault(username, null);
    }
}
