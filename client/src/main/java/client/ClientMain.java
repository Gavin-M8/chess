package client;

import chess.*;
import ui.REPL;

public class ClientMain {

    public static void main(String[] args) {
        String serverURL = "http://localhost:8080";
        if (args.length == 1) {
            serverURL = args[0];
        }
        try {
            REPL repl = new REPL(serverURL);
            repl.run();
        } catch (Throwable e) {
            System.out.printf("Error: Unable to start  server: %s%n", e.getMessage());
        }
    }
}
