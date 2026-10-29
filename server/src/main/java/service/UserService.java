package service;

import dataaccess.MemoryDataAccess2;
import java.util.ArrayList;

public class UserService {

    private MemoryDataAccess2 da;

    public UserService(MemoryDataAccess2 da) {
        this.da = da;
    }

    public void clear() {
        da.clear();
    }

    public ArrayList<Object> register(String username, String password, String email) {
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
