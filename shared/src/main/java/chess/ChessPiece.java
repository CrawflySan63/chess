package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        Collection<ChessMove> moves = new ArrayList<>();

        PieceType type = getPieceType();

        if (type == PieceType.BISHOP) {
            int[][] directions = { {1, 1}, {-1, -1}, {1, -1}, {-1, 1} };

            sliderMove(board, myPosition, moves, directions);
        } else if (type == PieceType.KING) {
            int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};

            stepMove(board, myPosition, moves, directions);
        } else if (type == PieceType.KNIGHT) {
            int[][] directions = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

            stepMove(board, myPosition, moves, directions);
        } else if (type == PieceType.PAWN) {
            int startRow = myPosition.getRow();
            int startCol = myPosition.getColumn();

            int direction = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
            int startRank = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
            int promotionRank = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

            ChessPosition oneStep = new ChessPosition(startRow + direction, startCol);
            if (board.getPiece(oneStep) == null && oneStep.getRow() >= 1 && oneStep.getRow() <= 8) {
                if (oneStep.getRow() == promotionRank) {
                    for (PieceType promoteTo : new PieceType[] {PieceType.QUEEN, PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP}) {
                        moves.add(new ChessMove(myPosition, oneStep, promoteTo));
                    }
                } else {
                    moves.add(new ChessMove(myPosition, oneStep, null));
                }

                if (startRow == startRank) {
                    ChessPosition twoStep = new ChessPosition(startRow + 2 * direction, startCol);
                    if (board.getPiece(twoStep) == null) {
                        moves.add(new ChessMove(myPosition, twoStep, null));
                    }
                }
            }

            for (int colOffset : new int[] {-1, 1}) {
                int targetCol = startCol + colOffset;
                int targetRow = startRow + direction;

                // Skip out-of-bounds positions early
                if (targetCol < 1 || targetCol > 8 || targetRow < 1 || targetRow > 8) {
                    continue;
                }

                ChessPosition diagPos = new ChessPosition(targetRow, targetCol);
                ChessPiece target = board.getPiece(diagPos);

                if (target == null || target.getTeamColor() == this.getTeamColor()) {
                    continue;
                }

                //we now have a valid enemy piece to capture
                if (targetRow == promotionRank) {
                    for (PieceType promoteTo : new PieceType[] {
                            PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT}) {
                        moves.add(new ChessMove(myPosition, diagPos, promoteTo));
                    }
                } else {
                    moves.add(new ChessMove(myPosition, diagPos, null));
                }
            }
        } else if (type == PieceType.ROOK) {
            int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

            sliderMove(board, myPosition, moves, directions);
        } else if (type == PieceType.QUEEN) {
            int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

            sliderMove(board, myPosition, moves, directions);
        }
        return moves;
    }

    private void stepMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int[][] directions) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for (int[] dir: directions) {
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece occupant = board.getPiece(newPos);

                if (occupant == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else {
                    if (occupant.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                }
            }
        }
    }

    private void sliderMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int[][] directions) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for (int[] dir: directions) {
            int row = startRow + dir[0];
            int col = startCol + dir[1];

            while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece occupant = board.getPiece(newPos);

                if (occupant == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else {
                    if (occupant.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }
}
