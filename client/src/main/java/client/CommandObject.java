package client;

import java.util.Objects;

public class CommandObject {

    private Command command;
    private String authToken;
    private String message;

    public CommandObject(Command command, String authToken, String message) {
        this.command = command;
        this.authToken = authToken;
        this.message = message;
    }

    public Command getCommand() {
        return command;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getMessage() {
        return message;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommandObject that = (CommandObject) o;
        return command == that.command && Objects.equals(authToken, that.authToken) && Objects.equals(message, that.message);
    }

    @Override
    public String toString() {
        return "CommandObject{" +
                "command=" + command +
                ", authToken='" + authToken + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, authToken, message);
    }
}
