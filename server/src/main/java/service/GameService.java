//package service;
//
//import chess.ChessGame;
//import model.AuthData;
//import model.GameData;
//
//import java.security.SecureRandom;
//import java.util.ArrayList;
//import java.util.Map;
//
//import dataaccess.MemoryDataAccess;
//
//public class GameService {
//
//    public ArrayList<Object> listGames(String authToken) {
//        AuthData potentialAuth = new AuthData(authToken);
//        ArrayList<Object> results = new ArrayList<>();
//
//        if (!isAuthorized(potentialAuth)) {
//            results.addFirst(false);
//            ArrayList<Map<String, Object>> list = new ArrayList<>();
//            results.addLast(list);
//        }
//        else if (isAuthorized(potentialAuth) && !games.isEmpty()) {
//            ArrayList<Map<String, Object>> gamesList = new ArrayList<>();
//
//            for (GameData game : games.values()) {
//                gamesList.add(game.getGame());
//            }
//
//            results.addFirst(true);
//            results.addLast(gamesList);
//        }
//        else {
//            ArrayList<Map<String, Object>> list = new ArrayList<>();
//            results.addFirst(true);
//            results.addLast(list);
//        }
//        return results;
//    }
//
//    public ArrayList<Object> createGame(String authToken, String gameName) {
//        ArrayList<Object> results = new ArrayList<>();
//        AuthData potentialAuth = new AuthData(authToken);
//
//        if (!isAuthorized(potentialAuth)) {
//            results.addFirst(false);
//            results.addLast(null);
//        }
//        else if (isAuthorized(potentialAuth) && gameName != null) {
//            SecureRandom secure = new SecureRandom();
//            int gameID = secure.nextInt(1000,10000);
//            results.addFirst(true);
//            results.addLast(gameID);
//
//            GameData newGame = new GameData(gameID, null, null, gameName);
//            games.put(gameName, newGame);
//        }
//        else {
//            results.addFirst(true);
//            results.addLast(null);
//        }
//
//        return results;
//    }
//
//    public ArrayList<Object> joinGame(ChessGame.TeamColor playerColor, Integer gameID, String authToken) {
//        ArrayList<Object> results = new ArrayList<>();
//        AuthData potentialAuth = new AuthData(authToken);
//
//        if (playerColor == null || gameID == null) {
//            results.addFirst(false);
//            results.addLast("Error: Bad request");
//        }
//        else if (!isAuthorized(potentialAuth)) {
//            results.addFirst(false);
//            results.addLast("Error: Unauthorized");
//        }
//        else if (colorTaken(playerColor, gameID)) {
//            results.addFirst(false);
//            results.addLast("Error: Already taken");
//        }
//        else {
//            results.addFirst(true);
//            results.addLast(null);
//            GameData game = getGame(gameID);
//            String username = getUsername(authToken);
//            if (playerColor == ChessGame.TeamColor.WHITE) {
//                game.setWhiteUsername(username);
//            }
//            else {
//                game.setBlackUsername(username);
//            }
//
//        }
//
//        return results;
//    }
//
//
//}
