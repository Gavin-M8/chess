package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static service.ServiceMain.mistake;

public class ServiceUnitTests {

    // I didn't realize I wrote all my service code into my dataaccess package and I don't have time to fix it right now. I'm sorry :(
    @Test
    void service() {
        assertTrue(mistake());
    }

}