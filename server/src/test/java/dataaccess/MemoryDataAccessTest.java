package dataaccess;

import org.junit.jupiter.api.Test;

public class MemoryDataAccessTest {

    @Test
    void clear() {
        var da = new MemoryDataAccess();
        da.clear();
    }

//    @Test
//    void register() {
//        var user = new UserData("cow", "cow@byu.edu", "moo");
//        var da = new MemoryDataAccess();
//        da.register(user, null);
//        assertEquals(user, da.getUser(user.username()));
//    }

}
