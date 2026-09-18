import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessMove;
import java.util.Collection;


public class CustomTests {
    public static void main(String[] args){
        ChessBoard testBoard = new ChessBoard();

        ChessPosition bishopPosition = new ChessPosition(5,2);
        ChessPosition rookPosition = new ChessPosition(4,1);
        ChessPosition queenPosition = new ChessPosition(7,4);
        ChessPosition pawnPosition = new ChessPosition(2,5);


        ChessPiece bishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece rook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece queen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece pawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        testBoard.addPiece(bishopPosition, bishop);
        testBoard.addPiece(rookPosition, rook);
        testBoard.addPiece(queenPosition, queen);
        testBoard.addPiece(pawnPosition, pawn);

        testBoard.printBoard();

        ChessPiece piece = testBoard.getPiece(bishopPosition);
        Collection<ChessMove> moves = piece.pieceMoves(testBoard, bishopPosition);

        System.out.println(moves);
    }
}
