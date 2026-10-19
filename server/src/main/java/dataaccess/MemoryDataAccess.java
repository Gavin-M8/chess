package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
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

    public void register(String username, String password, String email) {
        var user = new UserData(username, password, email);
        users.put(user.getUsername(), user);
        AuthData token = new AuthData(UUID.randomUUID().toString());
        authtokens.put(user.getUsername(), token);
    }

    public ArrayList<Object> login(String username, String password) {
        ArrayList<Object> result = new ArrayList<>();
        if (!users.containsKey(username)) {
            result.add(0, false);
            result.add(1, "User not registered");
        }
        else {
            if (!users.get(username).getPassword().equals(password)) {
                result.add(0, false);
                result.add(1, "Password incorrect");
            }
            else {
                result.add(0, true);
                AuthData token = new AuthData(UUID.randomUUID().toString());
                authtokens.put(username, token);
                result.add(1, token.getAuthToken());
            }
        }
        return result;
    }

    public boolean logout(String authToken) {
        AuthData potentialAuth = new AuthData(authToken);
        boolean canLogout = authtokens.containsValue(potentialAuth);
        if (canLogout) {
            authtokens.values().removeIf(auth -> auth.equals(potentialAuth));
        }
        return canLogout;
    }

    public UserData getUser(String username) {
        return users.getOrDefault(username, null);
    }

    public AuthData getToken(String username) {
        return authtokens.getOrDefault(username, null);
    }
}
