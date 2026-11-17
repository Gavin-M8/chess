package client;

public class ChessClient {

    private final ServerFacade server;
    private UserState userState = UserState.SIGNED_OUT;

    public ChessClient(String serverURL) {
        this.server = new ServerFacade(serverURL);
    }

    // handle repl commands:

        // PRE LOGIN

            // help
            // return list of commands available

            // quit
            // exit the repl

            // login
            // call serverFacade login method

            // register
            // call serverFacade register method

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
