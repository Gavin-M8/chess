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

            public String help() {
                return
                    """
                    Commands:
                    
                    `help`                                      print a list of commands
                    `quit`                                      exit the chess program
                    `register <username> <password> <email>`    register as a new user
                    `login <username> <password>`               login as a returning user
                    `logout`                                    logout the current user
                    `create_game <gameName>`                    create a new chess game
                    `play_game <playerColor> <gameID>`          join a chess game as a player
                    `observe_game <gameID>`                     join a chess game as an observer
                    
                    """;
            }

            public String login(String[] params) {
                try {
                    if ( params == null || params.length != 2) {
                        return "Login requires two parameters: username and password.";
                    } else {
                        String username = params[0];
                        String password = params[1];

                        userState = UserState.SIGNED_IN;

                        return server.login(username, password).toString();
                    }
                } catch (Throwable e) {
                    return String.format("%s", e.getMessage());
                }
            }

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
