package chess;
import java.util.*;

import static chess.PieceMovesUtilities.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {return Objects.hash(pieceColor, type);}

    @Override
    public String toString() {return "{" + pieceColor + " " + type + "}" ;}

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {return pieceColor;}

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {return type;}

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        PieceType type = piece.getPieceType();
        List<ChessMove> moves = new ArrayList<>();
        int[][][] offsets;

        if (type == PieceType.PAWN) {
            ArrayList<Object> results = pawnLogic(board, myPosition);
            offsets = (int[][][]) results.getFirst();
            boolean canPromote = (boolean) results.getLast();
            int[][] potentiallyBlocked = new int[][]{{1, 0}, {-1, 0}, {2, 0}, {-2, 0}};

            directionLoop:
            for (int[][] direction : offsets) {
                for (int[] offset : direction) {
                    ChessPosition target = myPosition.addOffset(offset[0], offset[1]);
                    boolean canMove = board.isValidMove(target, getTeamColor())[0];
                    boolean isCapture = board.isValidMove(target, getTeamColor())[1];

                    if (canPromote) {
                        if (canMove && isCapture) {
                            for (int[] potentialOffset : potentiallyBlocked) {
                                if (Arrays.equals(offset, potentialOffset)) {
                                    continue directionLoop;
                                }
                            }
                            moves.add(new ChessMove(myPosition, target, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, target, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, target, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, target, PieceType.KNIGHT));
                            continue directionLoop;
                        } else if (canMove) {
                            moves.add(new ChessMove(myPosition, target, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, target, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, target, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, target, PieceType.KNIGHT));
                        } else {
                            continue directionLoop;
                        }
                    } else {
                        if (canMove && isCapture) {
                            for (int[] potentialOffset : potentiallyBlocked) {
                                if (Arrays.equals(offset, potentialOffset)) {
                                    continue directionLoop;
                                }
                            }
                            moves.add(new ChessMove(myPosition, target, null));
                            continue directionLoop;
                        } else if (canMove) {
                            moves.add(new ChessMove(myPosition, target, null));
                        } else {
                            continue directionLoop;
                        }
                    }
                }
            }
        }

        else {
            switch (type) {
                case BISHOP -> offsets = getBishopOffsets();
                case QUEEN -> offsets = getQueenOffsets();
                case KING -> offsets = getKingOffsets();
                case ROOK -> offsets = getRookOffsets();
                case KNIGHT -> offsets = getKnightOffsets();
                case null, default -> offsets = new int[][][]{{{}}};
            }

            directionLoop:
            for (int[][] direction : offsets) {
                for (int[] offset : direction) {
                    ChessPosition target = myPosition.addOffset(offset[0], offset[1]);
                    boolean canMove = board.isValidMove(target, getTeamColor())[0];
                    boolean isCapture = board.isValidMove(target, getTeamColor())[1];

                    if (canMove && isCapture) {
                        moves.add(new ChessMove(myPosition, target, null));
                        continue directionLoop;
                    } else if (canMove) {
                        moves.add(new ChessMove(myPosition, target, null));
                    } else {
                        continue directionLoop;
                    }
                }
            }
        }
        return moves;
    }
}