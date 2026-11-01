package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import java.util.ArrayList;

public class UserService {

    private DataAccess da;

    public UserService(DataAccess da) {
        this.da = da;
    }

    public void clear() throws DataAccessException {
        da.clear();
    }

    public ArrayList<Object> register(String username, String password, String email) throws DataAccessException {
        ArrayList<Object> results = new ArrayList<>();
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
            results.addFirst(true);
            results.addLast(da.getToken(username).getLast().getAuthToken());
        }
        return results;
    }
}
