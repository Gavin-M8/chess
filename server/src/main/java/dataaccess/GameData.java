package dataaccess;

import chess.ChessGame;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameData {
    private ChessGame game;
    private String gameName;
    private Integer gameID;
    private String whiteUsername;
    private String blackUsername;

    GameData (int gameID, String whiteUsername, String blackUsername, String gameName) {
        this.game = new ChessGame();
        this.gameName = gameName;
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
    }

    public String getGameName() {return gameName;}

    public int getGameID() {return gameID;}

    public String getBlackUsername() {return blackUsername;}

    public String getWhiteUsername() {return whiteUsername;}

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public HashMap<String, Object> getGame() {
        return new HashMap<String, Object>(Map.of(
                "gameID", gameID,
                "whiteUsername", whiteUsername,
                "blackUsername", blackUsername,
                "gameName", gameName
        ));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameData gameData = (GameData) o;
        return Objects.equals(gameID, gameData.gameID) &&
                Objects.equals(game, gameData.game) &&
                Objects.equals(gameName, gameData.gameName) &&
                Objects.equals(whiteUsername, gameData.whiteUsername) &&
                Objects.equals(blackUsername, gameData.blackUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, gameName, gameID, whiteUsername, blackUsername);
    }

    @Override
    public String toString() {
        return "GameData{" +
                "game=" + game +
                ", gameName='" + gameName + '\'' +
                ", gameID=" + gameID +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                '}';
    }
}
