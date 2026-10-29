package dataaccess;

import chess.ChessGame;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class MemoryDataAccess2 implements DataAccess{
    private HashMap<String, UserData> users;
    private HashMap<String, GameData> games;
    private HashMap<String, ArrayList<AuthData>> authtokens;

    public MemoryDataAccess2() {
        this.users = new HashMap<>();
        this.games = new HashMap<>();
        this.authtokens = new HashMap<>();
    }


    public UserData getUser(String username) {
        return users.getOrDefault(username, null);
    }

    public String getUsername(String authToken) {
        AuthData auth = new AuthData(authToken);
        for (var entry : authtokens.entrySet()) {
            String username = entry.getKey();
            ArrayList<AuthData> authentication = entry.getValue();
            for (AuthData token : authentication) {
                if (token.equals(auth)) {
                    return username;
                }
            }
        }
        return null;
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public void createUser(String username, String password, String email) {
        var user = new UserData(username, password, email);
        users.put(username, user);
        AuthData token = new AuthData(UUID.randomUUID().toString());
        ArrayList<AuthData> tokens = new ArrayList<>();
        tokens.add(token);
        authtokens.put(user.getUsername(), tokens);
    }


    public ArrayList<AuthData> getToken(String username) {
        return authtokens.getOrDefault(username, null);
    }

    public boolean isAuthorized(AuthData auth) {
        for (ArrayList<AuthData> authentications : authtokens.values()) {
            for (AuthData token : authentications) {
                if (token.equals(auth)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void createAuth(String username) {
        AuthData token = new AuthData(UUID.randomUUID().toString());
        authtokens.get(username).add(token);
    }

    public void deleteAuth(String authToken) {
        AuthData potentialAuth = new AuthData(authToken);
        for (ArrayList<AuthData> authList : authtokens.values()) {
            authList.removeIf(potentialAuth::equals);
        }
    }


    public GameData getGame(Integer gameID) {
        for (GameData game : games.values()) {
            if (game.getGameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public Integer createGame(String gameName) {
        SecureRandom secure = new SecureRandom();
        var gameID = secure.nextInt(1000,10000);
        GameData newGame = new GameData(gameID, null, null, gameName);
        games.put(gameName, newGame);
        return gameID;
    }

    public boolean colorTaken(ChessGame.TeamColor playerColor, Integer gameID) {
        GameData game = getGame(gameID);
        if (playerColor == ChessGame.TeamColor.BLACK) {
            return game.getBlackUsername() != null;
        }
        else {
            return game.getWhiteUsername() != null;
        }
    }


    public void clear() {
        users.clear();
        games.clear();
        authtokens.clear();
    }

    }
