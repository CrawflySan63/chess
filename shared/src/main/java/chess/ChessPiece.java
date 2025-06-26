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
        } else if (type == PieceType.KING) {
            int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};

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
        } else if (type == PieceType.KNIGHT) {
            int[][] directions = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

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
        } else if (type == PieceType.PAWN) {

            int startRow = myPosition.getRow();
            int startCol = myPosition.getColumn();

            if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
                int [][] directions = {{1, 0}, {1, 1}, {1, -1}, {2, 0}};
            } else if (this.getTeamColor() == ChessGame.TeamColor.BLACK) {
                int[][] directions = {{-1, 0}, {-1, -1}, {-1, 1}, {-2, 0}};
            }
        } else if (type == PieceType.ROOK) {
            int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

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
        } else if (type == PieceType.QUEEN) {
            int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

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
        return moves;
    }
}
