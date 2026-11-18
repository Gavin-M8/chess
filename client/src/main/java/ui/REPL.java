package ui;

import java.util.Scanner;

import client.ChessClient;
import client.ChessClient.*;
import client.CommandObject;

public class REPL {

    private String serverURL;
    private ChessClient client;
    private String authToken;

    public REPL(String serverURL) {
        this.serverURL = serverURL;
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

            // convert line to command object
            CommandObject cmd = getCmd(line);


            try {
                result = client.eval(cmd);
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
            default -> command = CommandObject.Command.HELP;
        }
        String message;
        if (args.length > 1) {
            message = args[1];
        } else {message = null;}

        return new CommandObject(command, authToken, message);
    }

    // send initial message to console

    // while (true) {

        // evaluate if input is valid
            // if yes, send CommandObject to ClientMain
            // if no, send error message and display help

        // print CommandObject response
            // call renderBoard to print board
            // print notification message

    // }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }

}

