//package service;
//import dataaccess.AuthData;
//import dataaccess.MemoryDataAccess;
//
//import java.util.ArrayList;
//import java.util.UUID;
//
//public class AuthService {
//
//
//    public ArrayList<Object> login(String username, String password) {
//        ArrayList<Object> result = new ArrayList<>();
//        if (username == null || password == null) {
//            result.add(0, false);
//            result.add(1, "Error: Bad request");
//        }
//        else if (!userExists(username)) {
//            result.add(0, false);
//            result.add(1, "Error: User not registered");
//        }
//        else if (!users.get(username).getPassword().equals(password)) {
//            result.add(0, false);
//            result.add(1, "Error: Password incorrect");
//        }
//        else {
//            result.addFirst(true);
//            AuthData token = new AuthData(UUID.randomUUID().toString());
//            authtokens.get(username).add(token);
//            result.add(1, token.getAuthToken());
//        }
//        return result;
//    }
//
//    public boolean logout(String authToken) {
//        AuthData potentialAuth = new AuthData(authToken);
//        boolean canLogout = isAuthorized(potentialAuth);
//        if (canLogout) {
//            for (ArrayList<AuthData> authList : authtokens.values()) {
//                authList.removeIf(potentialAuth::equals);
//            }
//        }
//        return canLogout;
//    }
//
//
//}
