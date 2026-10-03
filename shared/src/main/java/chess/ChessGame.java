package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    @Override
    public String toString() {
        board.printBoard();
        return "ChessGame{}";
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {teamTurn = team;}

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {return moves;}

        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);

        for (ChessMove move : potentialMoves) {
            ChessPosition endPos = move.getEndPosition();
            if (board.getPiece(endPos) != null) {
                ChessPiece oldPiece = board.getPiece(endPos);
                board.removePiece(startPosition);
                board.addPiece(endPos, piece);

                if (!isInCheck(teamTurn) && !isInCheckmate(teamTurn) && !isInStalemate(teamTurn)) {
                    moves.add(move);
                }

                board.addPiece(endPos, oldPiece);
                board.addPiece(startPosition, piece);
            }
            else {
                board.removePiece(startPosition);
                board.addPiece(endPos, piece);

                if (!isInCheck(teamTurn) && !isInCheckmate(teamTurn) && !isInStalemate(teamTurn)) {
                    moves.add(move);
                }

                board.removePiece(endPos);
                board.addPiece(startPosition, piece);
            }
        }

        return moves;



    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        boolean canMakeMove = false;
        boolean canPromote = false;
        TeamColor turn = getTeamTurn();
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null) {throw new InvalidMoveException();}

        ChessPiece.PieceType type = piece.getPieceType();
        ChessPiece.PieceType promotion = move.getPromotionPiece();

        if (promotion != null) {
            canPromote = true;
        }


        for (ChessMove validMove : moves) {
            if (validMove.equals(move)) {
                canMakeMove = true;
                break;
            }
        }

        if (canMakeMove) {
            if (canPromote) {
                board.addPiece(move.getEndPosition(), new ChessPiece(turn, promotion));
                board.removePiece(move.getStartPosition());
                if (turn == TeamColor.WHITE) {
                    setTeamTurn(TeamColor.BLACK);
                }
                else {
                    setTeamTurn(TeamColor.WHITE);
                }
            }
            else {
                board.addPiece(move.getEndPosition(), new ChessPiece(turn, type));
                board.removePiece(move.getStartPosition());
                if (turn == TeamColor.WHITE) {
                    setTeamTurn(TeamColor.BLACK);
                }
                else {
                    setTeamTurn(TeamColor.WHITE);
                }
            }
        }
        else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        int[] rows = new int[] {1,2,3,4,5,6,7,8};
        int[] cols = new int[] {1,2,3,4,5,6,7,8};

        for (int row : rows) {
            for (int col : cols) {
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> potentialMoves = piece.pieceMoves(board, position);

                    for (ChessMove move : potentialMoves) {
                        ChessPosition endPos = move.getEndPosition();

                        if (board.getPiece(endPos) != null) {

                            if (board.getPiece(endPos).getPieceType().equals(ChessPiece.PieceType.KING)) {
                                return true;
                            }
                        }
                    }
                }

            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            int[] rows = new int[] {1,2,3,4,5,6,7,8};
            int[] cols = new int[] {1,2,3,4,5,6,7,8};

            for (int row : rows) {
                for (int col : cols) {
                    ChessPosition position = new ChessPosition(row,col);
                    ChessPiece piece = board.getPiece(position);

                    if (piece != null && piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, position);

                        for (ChessMove move : potentialMoves) {
                            ChessPosition endPos = move.getEndPosition();
                            if (board.getPiece(endPos) != null) {
                                ChessPiece oldPiece = board.getPiece(endPos);
                                board.removePiece(position);
                                board.addPiece(endPos, piece);

                                if (!isInCheck(teamColor)) {
                                    board.addPiece(endPos, oldPiece);
                                    board.addPiece(position, piece);
                                    return false;
                                }
                                else {
                                    board.addPiece(endPos, oldPiece);
                                    board.addPiece(position, piece);
                                }

                            }
                            else {
                                board.removePiece(position);
                                board.addPiece(endPos, piece);
                                if (!isInCheck(teamColor)) {
                                    board.removePiece(endPos);
                                    board.addPiece(position, piece);
                                    return false;
                                }
                                else {
                                    board.removePiece(endPos);
                                    board.addPiece(position, piece);
                                }

                            }
                        }
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            int[] rows = new int[] {1,2,3,4,5,6,7,8};
            int[] cols = new int[] {1,2,3,4,5,6,7,8};

            for (int row : rows) {
                for (int col : cols) {
                    ChessPosition position = new ChessPosition(row,col);
                    ChessPiece piece = board.getPiece(position);

                    if (piece != null && piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, position);

                        for (ChessMove move : potentialMoves) {
                            ChessPosition endPos = move.getEndPosition();
                            if (board.getPiece(endPos) != null) {
                                ChessPiece oldPiece = board.getPiece(endPos);
                                board.removePiece(position);
                                board.addPiece(endPos, piece);

                                if (!isInCheck(teamColor)) {
                                    board.addPiece(endPos, oldPiece);
                                    board.addPiece(position, piece);
                                    return false;
                                }
                                else {
                                    board.addPiece(endPos, oldPiece);
                                    board.addPiece(position, piece);
                                }

                            }
                            else {
                                board.removePiece(position);
                                board.addPiece(endPos, piece);
                                if (!isInCheck(teamColor)) {
                                    board.removePiece(endPos);
                                    board.addPiece(position, piece);
                                    return false;
                                }
                                else {
                                    board.removePiece(endPos);
                                    board.addPiece(position, piece);
                                }

                            }
                        }
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
