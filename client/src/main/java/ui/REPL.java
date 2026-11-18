package ui;

import java.util.List;
import java.util.Scanner;

import client.ChessClient;
import client.ChessClient.*;
import client.CommandObject;
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

        System.out.println("Welcome to Chess! Type help to get started!");

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            CommandObject cmd = getCmd(line);

            try {
                result = client.eval(cmd);

                // fix the formatting of the result, store most recent authToken in authToken variable

                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
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
            case "create_game" -> command = CommandObject.Command.CREATE_GAME;
            case "list_games" -> command = CommandObject.Command.LIST_GAMES;
            case "play_game" -> command = CommandObject.Command.PLAY_GAME;
            case "observe_game" -> command = CommandObject.Command.OBSERVE_GAME;
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
        System.out.print("\n>>> ");
    }

}

