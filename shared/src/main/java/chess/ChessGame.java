package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;
    private ChessMove lastMove;

    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;

    private boolean whiteQueenRookMoved = false;  // a1 rook
    private boolean whiteKingRookMoved = false;   // h1 rook

    private boolean blackQueenRookMoved = false;  // a8 rook
    private boolean blackKingRookMoved = false;   // h8 rook

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
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> baseMoves = piece.pieceMoves(board, startPosition);

        //if Pawn, add possible en passant moves
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            baseMoves = addSpecialPawnMoves(piece, startPosition, baseMoves);
        }

        //if King add possible castling moves
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            baseMoves.addAll(getCastlingMoves(piece, startPosition));
        }

        Set<ChessMove> valid = new HashSet<>();

        for (ChessMove move : baseMoves) {
            ChessBoard copy = deepCopyBoard(board);
            applyMove(copy, move);

            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                ChessPosition newKingPos = move.getEndPosition();
                ChessPosition enemyKingPos = findEnemyKing(copy, piece.getTeamColor());  // search on updated board

                if (enemyKingPos != null && moveAdjacentTo(newKingPos, enemyKingPos)) {
                    continue;  // skip if kings would be adjacent after move
                }
            }

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

        if (pieceToMove.getPieceType() == ChessPiece.PieceType.KING) {
            if (pieceToMove.getTeamColor() == TeamColor.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        } else if (pieceToMove.getPieceType() == ChessPiece.PieceType.ROOK) {
            int row = start.getRow();
            int col = start.getColumn();

            if (pieceToMove.getTeamColor() == TeamColor.WHITE) {
                if (row == 1 && col == 1) {
                    whiteQueenRookMoved = true;
                }
                if (row == 1 && col == 8) {
                    whiteKingRookMoved = true;
                }
            } else {
                if (row == 8 && col == 1) {
                    blackQueenRookMoved = true;
                }
                if (row == 8 && col == 8) {
                    blackKingRookMoved = true;
                }
            }
        }

        applyMove(board, move);
        lastMove = move; //updates lastMove so that on the subsequent move, checking for en Passant capability is available
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
        if (!isInCheck(teamColor)) {
            return false;
        }

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
        if (isInCheck(teamColor)) {
            return false;
        }

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

    private ChessPosition findEnemyKing(ChessBoard board, TeamColor myColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null &&
                        piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() != myColor) {
                    return pos;
                }
            }
        }
        return null;
    }

    private boolean moveAdjacentTo(ChessPosition a, ChessPosition b) {
        int rowDiff = Math.abs(a.getRow() - b.getRow());
        int colDiff = Math.abs(a.getColumn() - b.getColumn());
        return rowDiff <= 1 && colDiff <= 1;
    }

    private Collection<ChessMove> getCastlingMoves(ChessPiece kingPiece, ChessPosition kingPos) {
        Collection<ChessMove> castlingMoves= new ArrayList<>();

        if (kingPiece.getTeamColor() == TeamColor.WHITE &&
                (kingPos.getRow() != 1 || kingPos.getColumn() != 5)) {
            return castlingMoves;
        }
        if (kingPiece.getTeamColor() == TeamColor.BLACK &&
                (kingPos.getRow() != 8 || kingPos.getColumn() != 5)) {
            return castlingMoves;
        }
        if (kingPiece.getTeamColor() == TeamColor.WHITE) {
            //make sure king hasn't moved
            //make sure rooks haven't moved
            if (whiteKingMoved) {
                return castlingMoves;
            }
            if (!whiteKingRookMoved) {
                //make sure spaces between king and rook are empty
                //make sure no spots are under attack
                //if both of these things pass, add move to castlingMoves
                if (board.getPiece(new ChessPosition(1, 6)) == null &&
                        board.getPiece(new ChessPosition(1, 7)) == null) {

                    boolean safe = true;
                    for (int col : new int[]{5, 6, 7}) {
                        ChessBoard testBoard = deepCopyBoard(board);
                        // previous square
                        ChessPosition to = new ChessPosition(1, col);
                        applyMove(testBoard, new ChessMove(kingPos, to, null));
                        if (isInCheckOnBoard(testBoard, TeamColor.WHITE)) {
                            safe = false;
                            break;
                        }
                    }

                    if (safe) {
                        castlingMoves.add(new ChessMove(kingPos, new ChessPosition(1, 7), null));
                    }
                }
            }
            if (!whiteQueenRookMoved) {
                //make sure spaces between king and rook are empty
                //make sure no spots are under attack
                //if both of these things pass, add move to castlingMoves
                if (board.getPiece(new ChessPosition(1, 4)) == null &&
                        board.getPiece(new ChessPosition(1, 3)) == null &&
                        board.getPiece(new ChessPosition(1, 2)) == null) {

                    boolean safe = true;
                    for (int col : new int[]{5, 4, 3}) {
                        ChessBoard testBoard = deepCopyBoard(board);
                        // previous square
                        ChessPosition to = new ChessPosition(1, col);
                        applyMove(testBoard, new ChessMove(kingPos, to, null));
                        if (isInCheckOnBoard(testBoard, TeamColor.WHITE)) {
                            safe = false;
                            break;
                        }
                    }

                    if (safe) {
                        castlingMoves.add(new ChessMove(kingPos, new ChessPosition(1, 3), null));
                    }
                }
            }
        }
        if (kingPiece.getTeamColor() == TeamColor.BLACK) {
            if (blackKingMoved) {
                return castlingMoves;
            }
            if (!blackKingRookMoved) {
                //make sure spaces between king and rook are empty
                //make sure no spots are under attack
                //if both of these things pass, add move to castlingMoves
                if (board.getPiece(new ChessPosition(8, 6)) == null &&
                        board.getPiece(new ChessPosition(8, 7)) == null) {

                    boolean safe = true;
                    for (int col : new int[]{5, 6, 7}) {
                        ChessBoard testBoard = deepCopyBoard(board);
                        // previous square
                        ChessPosition to = new ChessPosition(8, col);
                        applyMove(testBoard, new ChessMove(kingPos, to, null));
                        if (isInCheckOnBoard(testBoard, TeamColor.BLACK)) {
                            safe = false;
                            break;
                        }
                    }

                    if (safe) {
                        castlingMoves.add(new ChessMove(kingPos, new ChessPosition(8, 7), null));
                    }
                }
            }
            if (!blackQueenRookMoved) {
                //make sure spaces between king and rook are empty
                //make sure no spots are under attack
                //if both of these things pass, add move to castlingMoves
                if (board.getPiece(new ChessPosition(8, 3)) == null &&
                        board.getPiece(new ChessPosition(8, 4)) == null &&
                        board.getPiece(new ChessPosition(8, 2)) == null) {

                    boolean safe = true;
                    for (int col : new int[]{5, 4, 3}) {
                        ChessBoard testBoard = deepCopyBoard(board);
                        // previous square
                        ChessPosition to = new ChessPosition(8, col);
                        applyMove(testBoard, new ChessMove(kingPos, to, null));
                        if (isInCheckOnBoard(testBoard, TeamColor.BLACK)) {
                            safe = false;
                            break;
                        }
                    }

                    if (safe) {
                        castlingMoves.add(new ChessMove(kingPos, new ChessPosition(8, 3), null));
                    }
                }
            }
        }

        return castlingMoves;
    }

    private Collection<ChessMove> addSpecialPawnMoves(ChessPiece piece, ChessPosition position, Collection<ChessMove> baseMoves) {
        List<ChessMove> updatedMoves = new ArrayList<>(baseMoves);

        int startRow = position.getRow();
        int startCol = position.getColumn();
        int direction = (piece.getTeamColor() == TeamColor.WHITE) ? 1 : -1;

        //Logic for En Passant
        int enPassantRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 5 : 4;
        if (startRow == enPassantRow && lastMove != null) {
            ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
            if (lastMovedPiece != null &&
                    lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN &&
                    lastMovedPiece.getTeamColor() != piece.getTeamColor()) {

                int fromRow = lastMove.getStartPosition().getRow();
                int toRow = lastMove.getEndPosition().getRow();

                if (Math.abs(fromRow - toRow) == 2 &&
                        Math.abs(lastMove.getEndPosition().getColumn() - startCol) == 1) {

                    int captureRow = startRow + direction;
                    int captureCol = lastMove.getEndPosition().getColumn();
                    ChessPosition capturePos = new ChessPosition(captureRow, captureCol);
                    updatedMoves.add(new ChessMove(position, capturePos, null));
                }
            }
        }

        return updatedMoves;
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

        if (kingPos == null) {
            return false;
        }

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

        if (pieceToMove.getPieceType() == ChessPiece.PieceType.PAWN &&
            board.getPiece(end) == null &&
            start.getColumn() != end.getColumn()) {

            int capturedPawnRow = (pieceToMove.getTeamColor() == TeamColor.WHITE) ? end.getRow() - 1 : end.getRow() + 1;
            ChessPosition capturedPos = new ChessPosition(capturedPawnRow, end.getColumn());
            board.addPiece(capturedPos, null);
        }

        if (pieceToMove.getPieceType() == ChessPiece.PieceType.KING &&
            start.getColumn() == 5) {
            if (pieceToMove.getTeamColor() == TeamColor.WHITE && end.getColumn() == 3) {
                //remove king at start, add king at end
                board.addPiece(start, null);
                board.addPiece(end, pieceToMove);
                //remove rook at its start position, add rook where it should go
                board.addPiece(new ChessPosition(1, 1), null);
                board.addPiece(new ChessPosition(1, 4), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
            }
            if (pieceToMove.getTeamColor() == TeamColor.WHITE && end.getColumn() == 7) {
                //remove king at start, add king at end
                board.addPiece(start, null);
                board.addPiece(end, pieceToMove);
                //remove rook at its start position, add rook where it should go
                board.addPiece(new ChessPosition(1, 8), null);
                board.addPiece(new ChessPosition(1, 6), new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK));
            }
            if (pieceToMove.getTeamColor() == TeamColor.BLACK && end.getColumn() == 3) {
                //remove king at start, add king at end
                board.addPiece(start, null);
                board.addPiece(end, pieceToMove);
                //remove rook at its start position, add rook where it should go
                board.addPiece(new ChessPosition(8, 1), null);
                board.addPiece(new ChessPosition(8, 4), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
            }
            if (pieceToMove.getTeamColor() == TeamColor.BLACK && end.getColumn() == 7) {
                //remove king at start, add king at end
                board.addPiece(start, null);
                board.addPiece(end, pieceToMove);
                //remove rook at its start position, add rook where it should go
                board.addPiece(new ChessPosition(8, 8), null);
                board.addPiece(new ChessPosition(8, 6), new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK));
            }
        }

        board.addPiece(start, null);

        if (move.getPromotionPiece() != null) {
            pieceToMove = new ChessPiece (pieceToMove.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(end, pieceToMove);
    }
}
