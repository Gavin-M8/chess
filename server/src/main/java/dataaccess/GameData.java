package dataaccess;

import chess.ChessGame;

public class GameData {
    private ChessGame game;
    private String gameName;
    private int gameID;

    GameData (String gameName, int gameID) {
        this.game = new ChessGame();
        this.gameName = gameName;
        this.gameID = gameID;
    }

    public String getGameName() {return gameName;}

    public int getGameID() {return gameID;}
}
