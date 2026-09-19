package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

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
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() { return type; }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.BISHOP) {

            List<ChessMove> moves = new ArrayList<>();
            int[][][] offsets = {
                    {{1,1}, {2,2}, {3,3}, {4,4}, {5,5}, {6,6}, {7,7}},
                    {{-1,1}, {-2,2}, {-3,3}, {-4,4}, {-5,5}, {-6,6}, {-7,7}},
                    {{-1,-1}, {-2,-2}, {-3,-3}, {-4,-4}, {-5,-5}, {-6,-6}, {-7,-7}},
                    {{1,-1}, {2,-2}, {3,-3}, {4,-4}, {5,-5}, {6,-6}, {7,-7}}
            };

            directionLoop:
            for (int[][] direction : offsets) {
                positionLoop:
                for (int[] offset : direction) {
                    ChessPosition target = myPosition.addOffset(offset[0], offset[1]);
                    boolean canMove = board.isValidMove(target, getTeamColor())[0];
                    boolean isCapture = board.isValidMove(target, getTeamColor())[1];

                    if (canMove && isCapture) {
                        moves.add(new ChessMove(myPosition, target, null));
                        continue directionLoop;
                    }
                    else if (canMove) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    else {
                        continue directionLoop;
                    }
                }
            }
            return moves;
        }

        else if (piece.getPieceType() == PieceType.KING) {
            List<ChessMove> moves = new ArrayList<>();
            int[][][] offsets = {
                    {{1,1}},
                    {{0,1}},
                    {{-1,1}},
                    {{-1,0}},
                    {{-1,-1}},
                    {{0,-1}},
                    {{1,-1}},
                    {{1,0}}
            };

            directionLoop:
            for (int[][] direction : offsets) {
                positionLoop:
                for (int[] offset : direction) {
                    ChessPosition target = myPosition.addOffset(offset[0], offset[1]);
                    boolean canMove = board.isValidMove(target, getTeamColor())[0];
                    boolean isCapture = board.isValidMove(target, getTeamColor())[1];

                    if (canMove && isCapture) {
                        moves.add(new ChessMove(myPosition, target, null));
                        continue directionLoop;
                    }
                    else if (canMove) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    else {
                        continue directionLoop;
                    }
                }
            }
            return moves;
        }

        else if (piece.getPieceType() == PieceType.QUEEN) {

            List<ChessMove> moves = new ArrayList<>();
            int[][][] offsets = {
                    {{0,1}, {0,2}, {0,3}, {0,4}, {0,5}, {0,6}, {0,7}},
                    {{1,1}, {2,2}, {3,3}, {4,4}, {5,5}, {6,6}, {7,7}},
                    {{1,0}, {2,0}, {3,0}, {4,0}, {5,0}, {6,0}, {7,0}},
                    {{-1,1}, {-2,2}, {-3,3}, {-4,4}, {-5,5}, {-6,6}, {-7,7}},
                    {{-1,-1}, {-2,-2}, {-3,-3}, {-4,-4}, {-5,-5}, {-6,-6}, {-7,-7}},
                    {{-1,0}, {-2,0}, {-3,0}, {-4,0}, {-5,0}, {-6,0}, {-7,0}},
                    {{0,-1}, {0,-2}, {0,-3}, {0,-4}, {0,-5}, {0,-6}, {0,-7}},
                    {{1,-1}, {2,-2}, {3,-3}, {4,-4}, {5,-5}, {6,-6}, {7,-7}}
            };

            directionLoop:
            for (int[][] direction : offsets) {
                positionLoop:
                for (int[] offset : direction) {
                    ChessPosition target = myPosition.addOffset(offset[0], offset[1]);
                    boolean canMove = board.isValidMove(target, getTeamColor())[0];
                    boolean isCapture = board.isValidMove(target, getTeamColor())[1];

                    if (canMove && isCapture) {
                        moves.add(new ChessMove(myPosition, target, null));
                        continue directionLoop;
                    }
                    else if (canMove) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    else {
                        continue directionLoop;
                    }
                }
            }
            return moves;
        }

        else if (piece.getPieceType() == PieceType.ROOK) {

            List<ChessMove> moves = new ArrayList<>();
            int[][][] offsets = {
                    {{0,1}, {0,2}, {0,3}, {0,4}, {0,5}, {0,6}, {0,7}},
                    {{1,0}, {2,0}, {3,0}, {4,0}, {5,0}, {6,0}, {7,0}},
                    {{-1,0}, {-2,0}, {-3,0}, {-4,0}, {-5,0}, {-6,0}, {-7,0}},
                    {{0,-1}, {0,-2}, {0,-3}, {0,-4}, {0,-5}, {0,-6}, {0,-7}},
            };

            directionLoop:
            for (int[][] direction : offsets) {
                positionLoop:
                for (int[] offset : direction) {
                    ChessPosition target = myPosition.addOffset(offset[0], offset[1]);
                    boolean canMove = board.isValidMove(target, getTeamColor())[0];
                    boolean isCapture = board.isValidMove(target, getTeamColor())[1];

                    if (canMove && isCapture) {
                        moves.add(new ChessMove(myPosition, target, null));
                        continue directionLoop;
                    }
                    else if (canMove) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    else {
                        continue directionLoop;
                    }
                }
            }
            return moves;
        }

        else if (piece.getPieceType() == PieceType.KNIGHT) {

            List<ChessMove> moves = new ArrayList<>();
            int[][][] offsets = {
                    {{2,1}},
                    {{2,-1}},
                    {{1,2}},
                    {{1,-2}},
                    {{-1,-2}},
                    {{-1,2}},
                    {{-2,-1}},
                    {{-2,1}}
            };

            directionLoop:
            for (int[][] direction : offsets) {
                positionLoop:
                for (int[] offset : direction) {
                    ChessPosition target = myPosition.addOffset(offset[0], offset[1]);
                    boolean canMove = board.isValidMove(target, getTeamColor())[0];
                    boolean isCapture = board.isValidMove(target, getTeamColor())[1];

                    if (canMove && isCapture) {
                        moves.add(new ChessMove(myPosition, target, null));
                        continue directionLoop;
                    }
                    else if (canMove) {
                        moves.add(new ChessMove(myPosition, target, null));
                    }
                    else {
                        continue directionLoop;
                    }
                }
            }
            return moves;
        }

        else if (piece.getPieceType() == PieceType.PAWN) {

            // logic to handle special pawn conditions
            boolean canPromote = false;
            boolean blackEnemyLeft = (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) != null) && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.WHITE);
            boolean blackEnemyRight = (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) != null) && (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).getTeamColor() == ChessGame.TeamColor.WHITE);
            boolean whiteEnemyLeft = (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) != null) && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.BLACK);
            boolean whiteEnemyRight = (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) != null) && (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).getTeamColor() == ChessGame.TeamColor.BLACK);

            List<ChessMove> moves = new ArrayList<>();
            int[][][] offsets;

            if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                if (myPosition.getRow() == 2) {canPromote = true;}
                if (myPosition.getRow() == 7) {
                    if (blackEnemyLeft && blackEnemyRight) { offsets = new int[][][] { {{-1,-1}},{{-1,1}},{{-1,0},{-2,0}} }; }
                    else if (blackEnemyLeft) { offsets = new int[][][] { {{-1,-1}},{{-1,0},{-2,0}} }; }
                    else if (blackEnemyRight) { offsets = new int[][][] { {{-1,1}},{{-1,0},{-2,0}} }; }
                    else { offsets = new int[][][] { {{-1,0},{-2,0}} }; }
                }
                else {
                    if (blackEnemyLeft && blackEnemyRight) { offsets = new int[][][] {{{-1, -1}}, {{-1, 1}}, {{-1, 0}}};}
                    else if (blackEnemyLeft) { offsets = new int[][][] {{{-1, -1}}, {{-1, 0}}};}
                    else if (blackEnemyRight) { offsets = new int[][][] {{{-1, 1}}, {{-1, 0}}};}
                    else { offsets = new int[][][] {{{-1, 0}}};}
                }
            }
            else {
                if (myPosition.getRow() == 7) {canPromote = true;}
                if (myPosition.getRow() == 2) {
                    if (whiteEnemyLeft && whiteEnemyRight) { offsets = new int[][][] { {{1,-1}},{{1,1}},{{1,0},{2,0}} }; }
                    else if (whiteEnemyLeft) { offsets = new int[][][] { {{1,-1}},{{1,0},{2,0}} }; }
                    else if (whiteEnemyRight) { offsets = new int[][][] { {{1,1}},{{1,0},{2,0}} }; }
                    else { offsets = new int[][][] { {{1,0},{2,0}} }; }
                }
                else {
                    if (whiteEnemyLeft && whiteEnemyRight) { offsets = new int[][][] { {{1,-1}},{{1,1}},{{1,0}} }; }
                    else if (whiteEnemyLeft) { offsets = new int[][][] { {{1,-1}},{{1,0}} }; }
                    else if (whiteEnemyRight) { offsets = new int[][][] { {{1,1}},{{1,0}} }; }
                    else { offsets = new int[][][] { {{1,0}} }; }
                }
            }

            int[][] potentiallyBlocked = new int[][] {{1,0}, {-1,0}, {2,0}, {-2,0}};

            directionLoop:
            for (int[][] direction : offsets) {
                positionLoop:
                for (int[] offset : direction) {
                    ChessPosition target = myPosition.addOffset(offset[0], offset[1]);
                    boolean canMove = board.isValidMove(target, getTeamColor())[0];
                    boolean isCapture = board.isValidMove(target, getTeamColor())[1];

                    if (canPromote) {
                        if (canMove && isCapture) {

                            for (int[] potentialOffset : potentiallyBlocked) {
                                if (Arrays.equals(offset,potentialOffset)) {
                                    continue directionLoop;
                                }
                            }

                            moves.add(new ChessMove(myPosition, target, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, target, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, target, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, target, PieceType.KNIGHT));
                            continue directionLoop;
                        }
                        else if (canMove) {
                            moves.add(new ChessMove(myPosition, target, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, target, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, target, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, target, PieceType.KNIGHT));
                        }
                        else {
                            continue directionLoop;
                        }
                    }
                    else {
                        if (canMove && isCapture) {

                            for (int[] potentialOffset : potentiallyBlocked) {
                                if (Arrays.equals(offset,potentialOffset)) {
                                    continue directionLoop;
                                }
                            }

                            moves.add(new ChessMove(myPosition, target, null));
                            continue directionLoop;
                        }
                        else if (canMove) {
                            moves.add(new ChessMove(myPosition, target, null));
                        }
                        else {
                            continue directionLoop;
                        }
                    }



                }
            }
            return moves;
        }

        return List.of();

    }
}
