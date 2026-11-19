package client;

import chess.ChessGame;
import chess.ChessBoard;
import model.ResponseObject.*;
import ui.EscapeSequences;

import java.util.HashMap;
import java.util.Map;

public class ChessClient {

    private final ServerFacade server;
    public UserState userState = UserState.LOGGED_OUT;
    private String authToken;
    private HashMap<Integer, String> gamesList;
    private HashMap<Integer, String> secretGamesList;
    private int gameNumber;

    public ChessClient(String serverURL) {
        this.server = new ServerFacade(serverURL);
        this.gamesList = new HashMap<>();
        this.secretGamesList = new HashMap<>();
        this.gameNumber = 1;
    }


    public String eval(CommandObject command) {
        CommandObject.Command cmd = command.getCommand();
        String message = command.getMessage();
        String[] params;
        if (message != null) {
            params = message.toLowerCase().split("\\s+");
        } else {
            params = null;
        }

        return switch (cmd) {
            case QUIT -> "quit";
            case REGISTER -> register(params);
            case LOGIN -> login(params);
            case LOGOUT -> logout(authToken);
            case CREATE_GAME -> createGame(authToken, params);
            case LIST_GAMES -> listGames(authToken);
            case PLAY_GAME -> playGame(authToken, params);
            case OBSERVE_GAME -> observeGame(authToken, params);
            case ADMIN_CLEAR -> clear();
            default -> help();
        };
    }

    public String help() {
        return
            """
            Commands:
            
            `help`                                      print a list of commands
            `quit`                                      exit the chess program
            `register <username> <password> <email>`    register as a new user
            `login <username> <password>`               login as a returning user
            `logout`                                    logout the current user
            `create <gameName>`                         create a new chess game
            `play <gameNumber> <playerColor>`           join a chess game as a player
            `observe <gameNumber>`                      join a chess game as an observer
            """;
    }

    public String login(String[] params) {
        try {
            if ( params == null || params.length != 2) {
                return "login requires two parameters: <username> <password>";
            } else {
                String username = params[0];
                String password = params[1];

                LoginResponse response = server.login(username, password);
                authToken = response.authToken();
                userState = UserState.LOGGED_IN;

                return String.format("Successfully logged in as %s", username);
            }
        } catch (Throwable e) {
            return String.format("%s", e.getMessage());
        }
    }

    public String register(String[] params) {
        try {
            if ( params == null || params.length != 3) {
                return "register requires three parameters: <username> <password> <email>";
            } else {
                String username = params[0];
                String password = params[1];
                String email = params[2];

                RegisterResponse response = server.register(username, password, email);
                authToken = response.authToken();
                userState = UserState.LOGGED_IN;

                return String.format("Successfully registered %s", username);
            }
        } catch (Throwable e) {
            return String.format("%s", e.getMessage());
        }
    }

    public String logout(String authToken) {
        try {
            if (authToken == null || userState != UserState.LOGGED_IN) {
                return "Cannot logout, user not logged in";
            } else {
                userState = UserState.LOGGED_OUT;
                server.logout(authToken);
                return "Successfully logged out";
            }
        } catch (Throwable e) {
            return String.format("%s", e.getMessage());
        }
    }

    public String createGame(String authToken, String[] params) {
        try {
            if (authToken == null || userState != UserState.LOGGED_IN) {
                return "Cannot create game, user not logged in";
            }
            else if (params == null || params.length != 1 || params[0].matches("\\s*")) {
                return "create_game requires one parameter: <gameName>";
            }
            else {
                String gameName = params[0];
                CreateGameResponse response = server.createGame(authToken, gameName);
                secretGamesList.put(response.gameID(), gameName);
                gamesList.put(gameNumber, gameName);
                String result = String.format("Created game: %s  %s", gameNumber, gameName);
                gameNumber += 1;
                return result;
            }
        } catch (Throwable e) {
            return String.format("%s", e.getMessage());
        }
    }

    public String listGames(String authToken) {
        String formattedGamesList = EscapeSequences.SET_TEXT_UNDERLINE +  "#  Name" + EscapeSequences.RESET_TEXT_UNDERLINE;
        try {
            if (authToken == null || userState != UserState.LOGGED_IN) {
                return "Cannot list games, user not logged in";
            }
            else {
                ListGamesResponse response = server.listGames(authToken);
                for (Map<String, Object> game : response.games()) {
                    Object rawId = game.get("gameID");
                    int gameID = ((Number) rawId).intValue();
                    String gameName = (String) game.get("gameName");
                    if (!gamesList.containsValue(gameName)) {
                        secretGamesList.put(gameID, gameName);
                        gamesList.put(gameNumber, gameName);
                        formattedGamesList += String.format("\n%s  %s", gameNumber, gameName);
                        gameNumber += 1;
                    } else {
                        formattedGamesList += String.format("\n%s  %s", getGameNumber(gameName), gameName);
                    }
                }
                if (gamesList.isEmpty()) {
                    formattedGamesList += "\n( no games, type `create <gameName>` to make one! ) ";
                }

                return formattedGamesList;
            }
        } catch (Throwable e) {
            return String.format("%s", e.getMessage());
        }
    }

    public String playGame(String authToken, String[] params) {
        try {
            if (authToken == null || userState != UserState.LOGGED_IN) {
                return "Cannot play game, user not logged in";
            }
            else if (params == null || params.length != 2) {
                return "play requires two parameters: <playerColor> <gameNumber>";
            }
            else {
                String playerColor = params[1];
                ChessGame.TeamColor color;
                if (playerColor.equals("white")) {
                    color = ChessGame.TeamColor.WHITE;
                } else if (playerColor.equals("black")) {
                    color = ChessGame.TeamColor.BLACK;
                }
                else {
                    return "Invalid playerColor: must be `white` or `black`";
                }
                int gameNumberInput;
                try {
                    gameNumberInput = Integer.parseInt(params[0]);
                } catch (NumberFormatException e) {
                    return "Invalid gameNumber: please input a number";
                }
                if (!gamesList.containsKey(gameNumberInput)) {
                    return "Invalid gameNumber: game does not exist";
                }
                try {
                    JoinGameResponse response = server.joinGame(authToken, color, getGameID(gameNumberInput));
                } catch (ResponseException e) {
                    return e.getMessage();
                }
                ChessGame game = new ChessGame();
                ChessBoard board = game.getBoard();
                board.resetBoard();
                String renderedBoard;

                if (playerColor.equals("white")) {renderedBoard = RenderBoard.renderBoard(board, RenderBoard.RenderPerspective.WHITE);}
                else {renderedBoard = RenderBoard.renderBoard(board, RenderBoard.RenderPerspective.BLACK);}

                return String.format("Joined game %s as %s \n\n%s", gameNumberInput, playerColor, renderedBoard);
            }
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    public String observeGame(String authToken, String[] params) {
        try {
            if (authToken == null || userState != UserState.LOGGED_IN) {
                return "Cannot observe game, user not logged in";
            }
            else if (params == null || params.length != 1) {
                return "observe_game requires one parameters: <gameID>";
            }
            else {
                int gameID;
                try {
                    gameID = Integer.parseInt(params[0]);
                } catch (NumberFormatException e) {
                    return "Invalid gameID: please input a four digit gameID";
                }
                ChessGame game = new ChessGame();
                ChessBoard board = game.getBoard();
                board.resetBoard();

                String renderedBoard = RenderBoard.renderBoard(board, RenderBoard.RenderPerspective.WHITE);

                return String.format("Observing game %s\n\n%s", gameID, renderedBoard);
            }
        } catch (Throwable e) {
            return String.format("%s", e.getMessage());
        }
    }

    // clear
    public String clear() {
        try {
            server.clear();
            gameNumber = 1;
            authToken = null;
            userState = UserState.LOGGED_OUT;
            return "Successfully cleared database";
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    private int getGameNumber(String gameName) {
        for (int gameNumber : gamesList.keySet()) {
            if (gamesList.get(gameNumber).equals(gameName)) {
                return gameNumber;
            }
        }
        return 0;
    }

    private int getGameID(int gameNumber) {
        String gameName = gamesList.get(gameNumber);

        for (int gameID : secretGamesList.keySet()) {
            if (secretGamesList.get(gameID).equals(gameName)) {
                return gameID;
            }
        }
        return 0;
    }
}
