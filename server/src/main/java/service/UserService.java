package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import java.util.ArrayList;

public class UserService {

    private DataAccess da;

    public UserService(DataAccess da) {
        this.da = da;
    }

    public boolean clear() {
        try {
            da.clear();
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public ArrayList<Object> register(String username, String password, String email) throws DataAccessException {
        ArrayList<Object> results = new ArrayList<>();
        try {
            if (username == null || password == null || email == null) {
                results.addFirst(false);
                results.addLast("Error: Bad Request");
            }
            else if (da.userExists(username)) {
                results.addFirst(false);
                results.addLast("Error: User already registered");
            }
            else {
                da.createUser(username, password, email);
                da.createAuth(username);
                results.addFirst(true);
                results.addLast(da.getToken(username).getLast().getAuthToken());
            }
            return results;
        } catch (DataAccessException e){
            results.addFirst(false);
            results.addLast("Error: data access error");
            return results;
        }

    }
}
