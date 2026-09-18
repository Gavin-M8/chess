import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessMove;
import java.util.Collection;


public class CustomTests {
    public static void main(String[] args){
        ChessBoard testBoard = new ChessBoard();
        ChessPosition position = new ChessPosition(1,1);

        testBoard.addPiece(position, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        testBoard.printBoard();

        ChessPiece piece = testBoard.getPiece(position);
        Collection<ChessMove> moves = piece.pieceMoves(testBoard, position);

        System.out.println(moves);
    }
}
