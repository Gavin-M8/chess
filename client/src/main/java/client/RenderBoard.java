package client;

import chess.ChessBoard;

public class RenderBoard {

    // takes in a ChessBoard and returns a string version of it to be printed
    public static String renderBoard(ChessBoard board, RenderPerspective color) {
            // for row in the board
                // for col in the row

                    // check if it should be black or white
                    // set the square color
                    // set the piece type and color
                    // add correct spacing
        return board.toString();
    }

    public enum RenderPerspective {
        WHITE,
        BLACK,
    }
}
