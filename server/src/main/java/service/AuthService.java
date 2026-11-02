package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

public class AuthService {

    private DataAccess da;

    public AuthService(DataAccess da) {
        this.da = da;
    }

    public ArrayList<Object> login(String username, String password) {
        ArrayList<Object> result = new ArrayList<>();
        try {
            if (username == null || password == null) {
                result.add(0, false);
                result.add(1, "Error: Bad request");
            } else if (!da.userExists(username)) {
                result.add(0, false);
                result.add(1, "Error: User not registered");
            } else if (!BCrypt.checkpw(password, da.getUser(username).getPassword())) {
                result.add(0, false);
                result.add(1, "Error: Password incorrect");
            } else {
                result.addFirst(true);
                da.createAuth(username);
                result.add(1, da.getToken(username).getLast().getAuthToken());
            }
            return result;
        } catch (DataAccessException e) {
            result.add(0, false);
            result.add(1, "Error: Data access error");
            return result;
        }
    }

    public ArrayList<Object> logout(String authToken) {
        ArrayList<Object> result = new ArrayList<>();
        try {
            boolean canLogout = da.isAuthorized(authToken);
            if (canLogout) {
                da.deleteAuth(authToken);
            }
            result.add(0, canLogout);
            result.add(1, "No data access error");
            return result;
        } catch (DataAccessException e) {
            result.add(0, false);
            result.add(1, "Error: Data access error");
            return result;
        }
    }


}
