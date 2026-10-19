package dataaccess;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals("User not registered", results.get(1));
    }

    @Test
    void loginFailWrongPassword() {
        var user = new UserData("jimbo", "jimbo@gmail.com", "secret-password");
        var da = new MemoryDataAccess();
        da.register(user.getUsername(), user.getPassword(), user.getEmail());
        ArrayList<Object> results = da.login(user.getUsername(), "super-secret-password");
        assertEquals(false, results.getFirst());
        assertEquals("Password incorrect", results.get(1));

    }

}
