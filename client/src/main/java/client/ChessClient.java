package client;

import chess.ChessGame;
import chess.ChessBoard;
import com.google.gson.Gson;
import server.ResponseObject.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChessClient {

    private final ServerFacade server;
    private UserState userState = UserState.LOGGED_OUT;
    private String authToken;
    private HashMap<Integer, String> gamesList;

    public ChessClient(String serverURL) {
        this.server = new ServerFacade(serverURL);
        this.gamesList = new HashMap<>();
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
            `create_game <gameName>`                    create a new chess game
            `play_game <playerColor> <gameID>`          join a chess game as a player
            `observe_game <gameID>`                     join a chess game as an observer
            """;
    }

    public String login(String[] params) {
        try {
            if ( params == null || params.length != 2) {
                return "login requires two parameters: <username> <password>";
            } else {
                String username = params[0];
                String password = params[1];
                userState = UserState.LOGGED_IN;

                LoginResponse response = server.login(username, password);
                authToken = response.authToken();

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
                userState = UserState.LOGGED_IN;

                RegisterResponse response = server.register(username, password, email);
                authToken = response.authToken();

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
            else if (params == null || params.length != 1) {
                return "create_game requires one parameter: <gameName>";
            }
            else {
                String gameName = params[0];
                CreateGameResponse response = server.createGame(authToken, gameName);
                gamesList.put(response.gameID(), gameName);

                return String.format("Created game:\n gameName:%s     gameID:%s", gameName, response.gameID());
            }
        } catch (Throwable e) {
            return String.format("%s", e.getMessage());
        }
    }

    public String listGames(String authToken) {
        String formattedGamesList = "Name:   ID:";
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
                    gamesList.put(gameID, gameName);
                    formattedGamesList += String.format("\n%s   %s", gameName, gameID);
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
                return "play_game requires two parameters: <playerColor> <gameID>";
            }
            else {
                String playerColor = params[0];
                ChessGame.TeamColor color;
                if (playerColor.equals("white")) {
                    color = ChessGame.TeamColor.WHITE;
                } else if (playerColor.equals("black")) {
                    color = ChessGame.TeamColor.BLACK;
                }
                else {
                    return "Invalid playerColor: must be `white` or `black`";
                }
                int gameID;
                try {
                    gameID = Integer.parseInt(params[1]);
                } catch (NumberFormatException e) {
                    return "Invalid gameID: please input a four digit gameID";
                }
                if (!gamesList.containsKey(gameID)) {
                    return "Invalid gameID";
                }
                try {
                    JoinGameResponse response = server.joinGame(authToken, color, gameID);
                } catch (ResponseException e) {
                    return e.getMessage();
                }
                ChessGame game = new ChessGame();
                ChessBoard board = game.getBoard();
                board.resetBoard();
                String renderedBoard;

                if (playerColor.equals("white")) {renderedBoard = RenderBoard.renderBoard(board, RenderBoard.RenderPerspective.WHITE);}
                else {renderedBoard = RenderBoard.renderBoard(board, RenderBoard.RenderPerspective.BLACK);}

                return String.format("Joined game %s as %s \n\n%s", gameID, playerColor, renderedBoard);
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
            return "Successfully cleared database";
        } catch (Throwable e) {
            return e.getMessage();
        }
    }
}
