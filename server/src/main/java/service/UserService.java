//package service;
//import dataaccess.AuthData;
//import dataaccess.MemoryDataAccess;
//import dataaccess.UserData;
//
//import java.util.ArrayList;
//import java.util.UUID;
//
//public class UserService {
//
//    public void clear() {
//        users.clear();
//        games.clear();
//        authtokens.clear();
//    }
//
//    public ArrayList<Object> register(String username, String password, String email) {
//        ArrayList<Object> results = new ArrayList<>();
//        if (username == null || password == null || email == null) {
//            results.addFirst(false);
//            results.addLast("Error: Bad Request");
//        }
//        else if (userExists(username)) {
//            results.addFirst(false);
//            results.addLast("Error: User already registered");
//        }
//        else {
//            var user = new UserData(username, password, email);
//            users.put(user.getUsername(), user);
//            AuthData token = new AuthData(UUID.randomUUID().toString());
//            ArrayList<AuthData> tokens = new ArrayList<>();
//            tokens.add(token);
//            authtokens.put(user.getUsername(), tokens);
//
//            results.addFirst(true);
//            results.addLast(token.getAuthToken());
//        }
//        return results;
//    }
//}
