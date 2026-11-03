package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import service.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLDataAccessTests {

    private MySQLDataAccess da = new MySQLDataAccess();
    private UserService us = new UserService(da);
    private AuthService as = new AuthService(da);
    private GameService gs = new GameService(da);

    private String existingAuth;

    MySQLDataAccessTests() {};

    @Test
    @Order(1)
    void register() throws DataAccessException {
        ArrayList<Object> result1 = new ArrayList<>();
        ArrayList<Object> result2 = new ArrayList<>();
        ArrayList<Object> result3 = new ArrayList<>();

        try {
            result1 = us.register(null, "secret", "bob@mail.com");
            result2 = us.register("bob", "secret", "bob@mail.com");
            result3 = us.register("bob", "secret", "bob@mail.com");
        } catch (DataAccessException e) {fail();}

        assertFalse((Boolean) result1.getFirst());
        assertTrue((Boolean) result2.getFirst());
        assertFalse((Boolean) result3.getFirst());
    }

    @Test
    @Order(2)
    void clear() throws DataAccessException {
        boolean didClear = us.clear();
        assertTrue(didClear);
        us.register("test", "secret", "email.com");
    }

    @Test
    @Order(3)
    void login() {
        ArrayList<Object> result1 = as.login(null, "secret");
        ArrayList<Object> result2 = as.login("bob", "secret");
        ArrayList<Object> result3 = as.login("test", "too-secret");
        ArrayList<Object> result4 = as.login("test", "secret");

        assertFalse((Boolean) result1.getFirst());
        assertFalse((Boolean) result2.getFirst());
        assertFalse((Boolean) result3.getFirst());
        assertTrue((Boolean) result4.getFirst());
    }

    @Test
    @Order(4)
    void logout() throws DataAccessException {
        us.clear();
        us.register("test", "secret", "email.com");

        ArrayList<Object> result0 = as.login("test", "secret");
        existingAuth = (String) result0.getLast();

        ArrayList<Object> result1 = as.logout("fake");
        ArrayList<Object> result2 = as.logout(existingAuth);

        assertFalse((Boolean) result1.getFirst());
        assertTrue((Boolean) result2.getFirst());

    }

    @Test
    @Order(5)
    void createGame() throws DataAccessException {
        us.clear();
        us.register("test", "secret", "email.com");

        ArrayList<Object> result0 = as.login("test", "secret");
        existingAuth = (String) result0.getLast();

        ArrayList<Object> result1 = gs.createGame(null, "just-some-game");
        ArrayList<Object> result2 = gs.createGame(existingAuth, null);
        ArrayList<Object> result3 = gs.createGame(existingAuth, "just-some-game");

        assertFalse((Boolean) result1.getFirst());
        assertTrue((Boolean) result2.getFirst());
        assertTrue((Boolean) result3.getFirst());

    }

    @Test
    @Order(6)
    void joinGame() throws DataAccessException {
        us.clear();
        us.register("test", "secret", "email.com");

        ArrayList<Object> result0 = as.login("test", "secret");
        existingAuth = (String) result0.getLast();
        ArrayList<Object> result00 = gs.createGame(existingAuth, "test-game");
        Integer gameID = (Integer) result00.getLast();

        ArrayList<Object> result1 = gs.joinGame(null, gameID, existingAuth);
        ArrayList<Object> result2 = gs.joinGame(ChessGame.TeamColor.WHITE, gameID, null);
        ArrayList<Object> result3 = gs.joinGame(ChessGame.TeamColor.WHITE, gameID, existingAuth);
        ArrayList<Object> result4 = gs.joinGame(ChessGame.TeamColor.WHITE, gameID, existingAuth);

        assertFalse((Boolean) result1.getFirst());
        assertFalse((Boolean) result2.getFirst());
        assertTrue((Boolean) result3.getFirst());
        assertFalse((Boolean) result4.getFirst());
    }

    @Test
    @Order(7)
    void listGames() throws DataAccessException {
        us.clear();
        us.register("test", "secret", "email.com");
        ArrayList<Object> result0 = as.login("test", "secret");
        existingAuth = (String) result0.getLast();
        gs.createGame(existingAuth, "test-game2");

        ArrayList<Object> result1 = gs.listGames(null);
        ArrayList<Object> result2 = gs.listGames(existingAuth);

        us.clear();
        us.register("test", "secret", "email.com");
        existingAuth = (String) as.login("test", "secret").getLast();

        ArrayList<Object> result3 = gs.listGames(existingAuth);

        assertFalse((Boolean) result1.getFirst());
        assertTrue((Boolean) result2.getFirst());
        assertTrue((Boolean) result3.getFirst());
    }


}
