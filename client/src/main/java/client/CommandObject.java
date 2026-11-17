package client;

public class CommandObject {

    private Command command;
    private String authToken;
    private String message;

    public CommandObject(Command command, String authToken, String message) {
        this.command = command;
        this.authToken = authToken;
        this.message = message;
    }

    public enum Command {
        HELP,
        QUIT,
        LOGIN,
        REGISTER,
        LOGOUT,
        CREATE_GAME,
        LIST_GAMES,
        PLAY_GAME,
        OBSERVE_GAME,
    }

}
