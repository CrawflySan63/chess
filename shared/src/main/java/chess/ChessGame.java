package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.currentTurn = TeamColor.WHITE;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) return null;

        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> valid = new ArrayList<>();

        for (ChessMove move : allMoves) {
            ChessBoard copy = deepCopyBoard(board);
            applyMove(copy, move);
            if (!isInCheckOnBoard(copy, piece.getTeamColor())) {
                valid.add(move);
            }
        }

        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPiece pieceToMove = board.getPiece(start);

        if (pieceToMove == null) {
            throw new InvalidMoveException();
        }

        if (pieceToMove.getTeamColor() != currentTurn) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> valid = validMoves(start);
        if (valid == null || !valid.contains(move)) {
            throw new InvalidMoveException();
        }

        applyMove(board, move);
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckOnBoard(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
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

    private boolean isInCheckOnBoard(ChessBoard boardToTest, TeamColor teamColor) {
        // Find the king’s position
        ChessPosition kingPos = null;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = boardToTest.getPiece(new ChessPosition(row, col));
                if (piece != null &&
                        piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() == teamColor) {
                    kingPos = new ChessPosition(row, col);
                    break;
                }
            }
        }

        if (kingPos == null) return false;

        // Check if any opposing piece can move to the king's position
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = boardToTest.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> theirMoves = piece.pieceMoves(boardToTest, new ChessPosition(row, col));
                    for (ChessMove move : theirMoves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private ChessBoard deepCopyBoard(ChessBoard original) {
        ChessBoard copy = new ChessBoard();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = original.getPiece(position);

                if (piece != null) {
                    // Create a new piece with the same color and type
                    ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                    copy.addPiece(position, newPiece);
                }
            }
        }

        return copy;
    }

    private void applyMove(ChessBoard board, ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece pieceToMove = board.getPiece(start);

        board.addPiece(start, null);

        if (move.getPromotionPiece() != null) {
            pieceToMove = new ChessPiece (pieceToMove.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(end, pieceToMove);
    }
}
