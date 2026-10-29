package service;

import chess.ChessGame;
import java.util.ArrayList;
import java.util.Map;
import dataaccess.MemoryDataAccess2;

public class GameService {

    MemoryDataAccess2 da = new MemoryDataAccess2();

    public ArrayList<Object> listGames(String authToken) {
        ArrayList<Object> results = new ArrayList<>();

        if (!da.isAuthorized(authToken)) {
            results.addFirst(false);
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            results.addLast(list);
        }
        else if (da.isAuthorized(authToken) && !da.gamesEmpty()) {
            results.addFirst(true);
            results.addLast(da.getGamesList());
        }
        else {
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            results.addFirst(true);
            results.addLast(list);
        }
        return results;
    }

    public ArrayList<Object> createGame(String authToken, String gameName) {
        ArrayList<Object> results = new ArrayList<>();

        if (!da.isAuthorized(authToken)) {
            results.addFirst(false);
            results.addLast(null);
        }
        else if (da.isAuthorized(authToken) && gameName != null) {
            Integer gameID = da.createGame(gameName);
            results.addFirst(true);
            results.addLast(gameID);
        }
        else {
            results.addFirst(true);
            results.addLast(null);
        }

        return results;
    }

    public ArrayList<Object> joinGame(ChessGame.TeamColor playerColor, Integer gameID, String authToken) {
        ArrayList<Object> results = new ArrayList<>();

        if (playerColor == null || gameID == null) {
            results.addFirst(false);
            results.addLast("Error: Bad request");
        }
        else if (!da.isAuthorized(authToken)) {
            results.addFirst(false);
            results.addLast("Error: Unauthorized");
        }
        else if (da.colorTaken(playerColor, gameID)) {
            results.addFirst(false);
            results.addLast("Error: Already taken");
        }
        else {
            da.joinGame(playerColor, gameID, authToken);
            results.addFirst(true);
            results.addLast(null);
        }

        return results;
    }


}
