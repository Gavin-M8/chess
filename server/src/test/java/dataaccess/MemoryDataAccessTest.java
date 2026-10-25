package dataaccess;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryDataAccessTest {

    @Test
    void clear() {
        var da = new MemoryDataAccess();
        da.clear();
    }

    @Test
    void register() {
        var user = new UserData("billy bob", "billyb@gmail.com", "billyb123");
        var da = new MemoryDataAccess();
        da.register(user.getUsername(), user.getPassword(), user.getEmail());
        assertEquals(user, da.getUser(user.getUsername()));
    }

    @Test
    void loginSuccess() {
        var user = new UserData("billy bob", "billyb@gmail.com", "billyb123");
        var da = new MemoryDataAccess();
        da.register(user.getUsername(), user.getPassword(), user.getEmail());
        ArrayList<Object> results = da.login(user.getUsername(), user.getPassword());
        assertEquals(true, results.getFirst());
    }
    @Test
    void loginFailNeedsRegister() {
        var da = new MemoryDataAccess();
        ArrayList<Object> results = da.login("jimbo", "secret-password");
        assertEquals(false, results.getFirst());
        assertEquals("Error: User not registered", results.get(1));
    }

    @Test
    void loginFailWrongPassword() {
        var user = new UserData("jimbo", "jimbo@gmail.com", "secret-password");
        var da = new MemoryDataAccess();
        da.register(user.getUsername(), user.getPassword(), user.getEmail());
        ArrayList<Object> results = da.login(user.getUsername(), "super-secret-password");
        assertEquals(false, results.getFirst());
        assertEquals("Error: Password incorrect", results.get(1));

    }

    @Test
    void logoutSuccess() {
        var user = new UserData("billy bob", "billyb@gmail.com", "billyb123");
        var da = new MemoryDataAccess();
        da.register(user.getUsername(), user.getPassword(), user.getEmail());
        da.login(user.getUsername(), user.getPassword());
        ArrayList<AuthData> auth = da.getToken(user.getUsername());
        boolean didLogout = da.logout(auth.getFirst().getAuthToken());
        assertTrue(didLogout);
    }

    @Test
    void logoutFail() {
        var user = new UserData("billy bob", "billyb@gmail.com", "billyb123");
        var da = new MemoryDataAccess();
        da.register(user.getUsername(), user.getPassword(), user.getEmail());
        da.login(user.getUsername(), user.getPassword());
        boolean didLogout = da.logout("thats-some-auth");
        assertFalse(didLogout);
    }

    @Test
    void createGameSuccess() {
        var user = new UserData("billy bob", "billyb@gmail.com", "billyb123");
        var da = new MemoryDataAccess();
        da.register(user.getUsername(), user.getPassword(), user.getEmail());
        da.login(user.getUsername(), user.getPassword());
        ArrayList<AuthData> auth = da.getToken(user.getUsername());
        ArrayList<Object> result = da.createGame(auth.getFirst().getAuthToken(), "myGame");
        boolean canCreateGame = (boolean) result.getFirst();
        int gameID = (int) result.getLast();
        assertTrue(canCreateGame);
        assertTrue(1000 <= gameID && gameID < 10000);
    }

    @Test
    void createGameFail() {
        var user = new UserData("billy bob", "billyb@gmail.com", "billyb123");
        var da = new MemoryDataAccess();
        da.register(user.getUsername(), user.getPassword(), user.getEmail());
        da.login(user.getUsername(), user.getPassword());
        ArrayList<Object> result = da.createGame("fake auth", "myGame");
        boolean canCreateGame = (boolean) result.getFirst();
        assertFalse(canCreateGame);
        assertNull(result.getLast());
    }


}
