package dataaccess;

import org.junit.jupiter.api.Test;

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
        da.register(user);
        assertEquals(user, da.getUser(user.getUsername()));
    }

}
