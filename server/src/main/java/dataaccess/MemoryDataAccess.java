package dataaccess;

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

    public void register(UserData user) {
        users.put(user.getUsername(), user);
        AuthData token = new AuthData(UUID.randomUUID().toString());
        authtokens.put(user.getUsername(), token);
    }

    public UserData getUser(String username) {
        return users.getOrDefault(username, null);
    }

    public AuthData getToken(String username) {
        return authtokens.getOrDefault(username, null);
    }
}
