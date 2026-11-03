package chess;

import java.util.ArrayList;

public class PieceMovesUtilities {

    public static ArrayList<Object> pawnLogic(ChessBoard board, ChessPosition myPosition) {
        ArrayList<Object> result = new ArrayList<>();
        int[][][] offsets;
        ChessPiece piece = board.getPiece(myPosition);

        boolean canPromote = false;
        boolean blackEnemyLeft = ((myPosition.getColumn() > 1 && myPosition.getRow() > 1) &&
                board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) != null) &&
                (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.WHITE);

        boolean blackEnemyRight = ((myPosition.getColumn() < 8 && myPosition.getRow() > 1) &&
                board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) != null) &&
                (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).getTeamColor() == ChessGame.TeamColor.WHITE);

        boolean whiteEnemyLeft = ((myPosition.getColumn() > 1 && myPosition.getRow() < 8) &&
                board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) != null) &&
                (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).getTeamColor() == ChessGame.TeamColor.BLACK);

        boolean whiteEnemyRight = ((myPosition.getColumn() < 8 && myPosition.getRow() < 8) &&
                board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) != null) &&
                (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).getTeamColor() == ChessGame.TeamColor.BLACK);

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (myPosition.getRow() == 2) {
                canPromote = true;
            }
            if (myPosition.getRow() == 7) {
                if (blackEnemyLeft && blackEnemyRight) {
                    offsets = new int[][][]{{{-1, -1}}, {{-1, 1}}, {{-1, 0}, {-2, 0}}};
                } else if (blackEnemyLeft) {
                    offsets = new int[][][]{{{-1, -1}}, {{-1, 0}, {-2, 0}}};
                } else if (blackEnemyRight) {
                    offsets = new int[][][]{{{-1, 1}}, {{-1, 0}, {-2, 0}}};
                } else {
                    offsets = new int[][][]{{{-1, 0}, {-2, 0}}};
                }
            } else {
                if (blackEnemyLeft && blackEnemyRight) {
                    offsets = new int[][][]{{{-1, -1}}, {{-1, 1}}, {{-1, 0}}};
                } else if (blackEnemyLeft) {
                    offsets = new int[][][]{{{-1, -1}}, {{-1, 0}}};
                } else if (blackEnemyRight) {
                    offsets = new int[][][]{{{-1, 1}}, {{-1, 0}}};
                } else {
                    offsets = new int[][][]{{{-1, 0}}};
                }
            }
        } else {
            if (myPosition.getRow() == 7) {
                canPromote = true;
            }
            if (myPosition.getRow() == 2) {
                if (whiteEnemyLeft && whiteEnemyRight) {
                    offsets = new int[][][]{{{1, -1}}, {{1, 1}}, {{1, 0}, {2, 0}}};
                } else if (whiteEnemyLeft) {
                    offsets = new int[][][]{{{1, -1}}, {{1, 0}, {2, 0}}};
                } else if (whiteEnemyRight) {
                    offsets = new int[][][]{{{1, 1}}, {{1, 0}, {2, 0}}};
                } else {
                    offsets = new int[][][]{{{1, 0}, {2, 0}}};
                }
            } else {
                if (whiteEnemyLeft && whiteEnemyRight) {
                    offsets = new int[][][]{{{1, -1}}, {{1, 1}}, {{1, 0}}};
                } else if (whiteEnemyLeft) {
                    offsets = new int[][][]{{{1, -1}}, {{1, 0}}};
                } else if (whiteEnemyRight) {
                    offsets = new int[][][]{{{1, 1}}, {{1, 0}}};
                } else {
                    offsets = new int[][][]{{{1, 0}}};
                }
            }
        }

        result.addFirst(offsets);
        result.addLast(canPromote);
        return result;
    }

    public static int[][][] getQueenOffsets() {
        return new int[][][]{
                {{0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}},
                {{1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7}},
                {{1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}},
                {{-1, 1}, {-2, 2}, {-3, 3}, {-4, 4}, {-5, 5}, {-6, 6}, {-7, 7}},
                {{-1, -1}, {-2, -2}, {-3, -3}, {-4, -4}, {-5, -5}, {-6, -6}, {-7, -7}},
                {{-1, 0}, {-2, 0}, {-3, 0}, {-4, 0}, {-5, 0}, {-6, 0}, {-7, 0}},
                {{0, -1}, {0, -2}, {0, -3}, {0, -4}, {0, -5}, {0, -6}, {0, -7}},
                {{1, -1}, {2, -2}, {3, -3}, {4, -4}, {5, -5}, {6, -6}, {7, -7}}
        };
    }

    public static int[][][] getBishopOffsets() {
        return new int[][][]{
                {{1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7}},
                {{-1, 1}, {-2, 2}, {-3, 3}, {-4, 4}, {-5, 5}, {-6, 6}, {-7, 7}},
                {{-1, -1}, {-2, -2}, {-3, -3}, {-4, -4}, {-5, -5}, {-6, -6}, {-7, -7}},
                {{1, -1}, {2, -2}, {3, -3}, {4, -4}, {5, -5}, {6, -6}, {7, -7}}
        };
    }

    public static int[][][] getRookOffsets() {
        return new int[][][]{
                {{0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}},
                {{1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}},
                {{-1, 0}, {-2, 0}, {-3, 0}, {-4, 0}, {-5, 0}, {-6, 0}, {-7, 0}},
                {{0, -1}, {0, -2}, {0, -3}, {0, -4}, {0, -5}, {0, -6}, {0, -7}},
        };
    }

    public static int[][][] getKingOffsets() {
        return new int[][][]{
                {{1, 1}},
                {{0, 1}},
                {{-1, 1}},
                {{-1, 0}},
                {{-1, -1}},
                {{0, -1}},
                {{1, -1}},
                {{1, 0}}
        };
    }

    public static int[][][] getKnightOffsets() {
        return new int[][][]{
                {{2, 1}},
                {{2, -1}},
                {{1, 2}},
                {{1, -2}},
                {{-1, -2}},
                {{-1, 2}},
                {{-2, -1}},
                {{-2, 1}}
        };
    }


}
