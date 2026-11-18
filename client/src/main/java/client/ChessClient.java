package client;

import java.util.ArrayList;

public class ChessClient {

    private final ServerFacade server;
    private UserState userState = UserState.SIGNED_OUT;

    public ChessClient(String serverURL) {
        this.server = new ServerFacade(serverURL);
    }

    // handle repl commands:

    public String eval(CommandObject command) {
        CommandObject.Command cmd = command.getCommand();
        String message = command.getMessage();
        String authToken = command.getAuthToken();
        String[] params;
        if (message != null) {
            params = message.toLowerCase().split("\\s+");
        } else {
            params = null;
        }

        return switch (cmd) {
            case REGISTER -> register(params);
            case QUIT -> "quit";
            default -> help();
        };
    }

        // PRE LOGIN

            // help
            public String help() {
                return
                    """
                    these are the things you can do:
                    `help` for help
                    `register` to register as a new user. params: [username, password, email]
                    """;
            }

            // login
            // call serverFacade login method

            // register
            // call serverFacade register method
            public String register(String[] params) {
                try {
                    if ( params == null || params.length != 3) {
                        return "Register requires three parameters: username, password, and email.";
                    } else {
                        String username = params[0];
                        String password = params[1];
                        String email = params[2];

                        userState = UserState.SIGNED_IN;

                        return server.register(username, password, email).toString();
                    }
                } catch (Throwable e) {
                    return String.format("%s", e.getMessage());
                }
            }


        // POST LOGIN

            // logout
            // call serverFacade logout method

            // create game
            // call serverFacade create game method

            // list games
            // call serverFacade list games method

            // play game
            // call serverFacade joinGame method
            // render the board from the user's perspective

            // observe game
            // render the chosen game from the white player's perspective
}
