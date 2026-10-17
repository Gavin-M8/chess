package dataaccess;

import chess.ChessGame;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "GameData{" +
                "game=" + game +
                ", gameName='" + gameName + '\'' +
                ", gameID=" + gameID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID && Objects.equals(game, gameData.game) && Objects.equals(gameName, gameData.gameName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, gameName, gameID);
    }
}
