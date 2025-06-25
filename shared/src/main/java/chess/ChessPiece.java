package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

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
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //Do I need if else statements saying "If Bishop, then piece can move this certain way...if Queen, then it can move this way" etc.?
        //if(getPieceType = Bishop)
            //then return all diagonal squares from starting position until edge of board
        //else if(getPieceType = Queen
            //then return all diagonal and orthogonal squares until edge of board
        Collection<ChessMove> moves = new ArrayList<>();

        PieceType type = getPieceType();

        if (type == PieceType.BISHOP) {
            int[][] directions = { {1, 1}, {-1, -1}, {1, -1}, {-1, 1} };

            int startRow = myPosition.getRow();
            int startCol = myPosition.getColumn();

            for (int[] dir: directions) {
                int row = startRow + dir[0];
                int col = startCol + dir[1];

                while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                    ChessPosition newPos = new ChessPosition(row, col);
                    moves.add(new ChessMove(myPosition, newPos, null));

                    row += dir[0];
                    col += dir[1];
                }

            }
        }
        return moves;
    }
}
