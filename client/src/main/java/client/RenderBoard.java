package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import ui.EscapeSequences;

public class RenderBoard {

    public static String renderBoard(ChessBoard board, RenderPerspective color) {
        StringBuilder boardString = new StringBuilder();

        if (color == RenderPerspective.WHITE) {
            for (int row = 8; row >= 1; row--) {
                for (int col = 1; col <= 8; col++) {
                    boolean isWhite = (row + col) % 2 == 1;
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    boardString.append(getSquareColor(isWhite));

                    if (piece != null) {
                        boardString.append(getPieceColor(piece.getTeamColor()));
                        boardString.append(" ").append(getPieceChar(piece)).append(" ");
                    } else {
                        boardString.append("   ");
                    }
                }
                boardString.append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_TEXT_COLOR).append("\n");
            }
        } else {
            for (int row = 1; row <= 8; row++) {
                for (int col = 8; col >= 1; col--) {
                    boolean isWhite = (row + col) % 2 == 1;
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    boardString.append(getSquareColor(isWhite));

                    if (piece != null) {
                        boardString.append(getPieceColor(piece.getTeamColor()));
                        boardString.append(" ").append(getPieceChar(piece)).append(" ");
                    } else {
                        boardString.append("   ");
                    }
                }
                boardString.append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_TEXT_COLOR).append("\n");

            }
        }


        return boardString.toString();
    }

    private static String getPieceChar(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        return switch (type) {
            case QUEEN -> "Q";
            case PAWN -> "P";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case KING -> "K";
            case BISHOP -> "B";
            case null -> EscapeSequences.EMPTY;

        };
    }

    public enum RenderPerspective {
        WHITE,
        BLACK,
    }

    public static String getSquareColor(boolean isWhite) {
        if (isWhite) return EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        else return EscapeSequences.SET_BG_COLOR_DARK_GREY;
    }

    public static String getPieceColor(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) return EscapeSequences.SET_TEXT_COLOR_WHITE;
        else return EscapeSequences.SET_TEXT_COLOR_BLACK;
    }
}
