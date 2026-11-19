package ui;

import java.util.List;
import java.util.Scanner;

import client.ChessClient;
import client.ChessClient.*;
import client.CommandObject;
import client.UserState;
import model.GameData;

public class REPL {

    private final ChessClient client;
    private final String authToken;
    private List<GameData> gamesList;

    public REPL(String serverURL) {
        this.client = new ChessClient(serverURL);
        this.authToken = null;
    }

    public void run() {

        System.out.println(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE + "\nWelcome to Chess! Type `help` to get started");

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            CommandObject cmd = getCmd(line);

            try {
                result = client.eval(cmd);
                System.out.print(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_BOLD_FAINT + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println("\nThanks for playing chess!");
    }

    private CommandObject getCmd(String line) {
        String[] args = line.toLowerCase().split("\\s+", 2);
        String commandString = args[0];
        CommandObject.Command command;
        switch (commandString) {
            case "help" -> command = CommandObject.Command.HELP;
            case "quit" -> command = CommandObject.Command.QUIT;
            case "login" -> command = CommandObject.Command.LOGIN;
            case "register" -> command = CommandObject.Command.REGISTER;
            case "logout" -> command = CommandObject.Command.LOGOUT;
            case "create" -> command = CommandObject.Command.CREATE_GAME;
            case "list" -> command = CommandObject.Command.LIST_GAMES;
            case "play" -> command = CommandObject.Command.PLAY_GAME;
            case "observe" -> command = CommandObject.Command.OBSERVE_GAME;
            case "admin_clear" -> command = CommandObject.Command.ADMIN_CLEAR;
            default -> command = CommandObject.Command.HELP;
        }
        String message;
        if (args.length > 1) {
            message = args[1];
        } else {message = null;}

        return new CommandObject(command, authToken, message);
    }

    private void printPrompt() {
        String stateString;
        if (client.userState == UserState.LOGGED_IN) {
            stateString = "[ logged in ]";
        } else {
            stateString = "[ logged out ]";
        }
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + stateString + " >>> ");
    }

}

