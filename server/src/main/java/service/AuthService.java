package service;

import dataaccess.MemoryDataAccess2;
import java.util.ArrayList;

public class AuthService {

    MemoryDataAccess2 da = new MemoryDataAccess2();

    public ArrayList<Object> login(String username, String password) {
        ArrayList<Object> result = new ArrayList<>();
        if (username == null || password == null) {
            result.add(0, false);
            result.add(1, "Error: Bad request");
        }
        else if (!da.userExists(username)) {
            result.add(0, false);
            result.add(1, "Error: User not registered");
        }
        else if (!da.getUser(username).getPassword().equals(password)) {
            result.add(0, false);
            result.add(1, "Error: Password incorrect");
        }
        else {
            result.addFirst(true);
            da.createAuth(username);
            result.add(1, da.getToken(username).getLast());
        }
        return result;
    }

    public boolean logout(String authToken) {
        boolean canLogout = da.isAuthorized(authToken);
        if (canLogout) {
            da.deleteAuth(authToken);
        }
        return canLogout;
    }


}
