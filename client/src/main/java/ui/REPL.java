package ui;

import java.util.Scanner;

import client.ChessClient;
import client.ChessClient.*;

public class REPL {

    private String serverURL;
    private ChessClient client;

    public REPL(String serverURL) {
        this.serverURL = serverURL;
        client = new ChessClient(serverURL);
    }


    public void run() {

        System.out.println("Welcome to Chess! Type help to get started!");

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            // convert line to command object

            try {
                result = client.eval();
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
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

