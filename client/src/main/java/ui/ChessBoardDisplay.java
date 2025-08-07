package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class ChessBoardDisplay {
    public void drawBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        System.out.print(ERASE_SCREEN);
        if (perspective == ChessGame.TeamColor.WHITE) {
            drawWhitePerspective(board);
        } else {
            drawBlackPerspective(board);
        }
    }

    private void drawWhitePerspective(ChessBoard board) {
        for (int row = 8; row >= 1; row --) {
            drawRow(board, row, true);
        }
        drawColumnLabels(true);
    }

    private void drawBlackPerspective(ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            drawRow(board, row, false);
        }
        drawColumnLabels(false);
    }

    private void drawRow(ChessBoard board, int row, boolean leftToRight) {
        System.out.print(" " + row + " ");
        for (int col = leftToRight ? 1 : 8; leftToRight ? col <= 8 : col >= 1; col += leftToRight ? 1 : -1) {
            ChessPosition pos = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(pos);
            boolean lightSquare = (row + col) % 2 == 0;

            System.out.print(lightSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY);
            System.out.print(SET_TEXT_COLOR_WHITE);

            if (piece == null) {
                System.out.print(EMPTY);
            } else {
                System.out.print(getPieceIcon(piece));
            }

            System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        }
        System.out.println(" " + row);
    }

    private void drawColumnLabels(boolean leftToRight) {
        System.out.print(" \u2003");
        for (char col = leftToRight ? 'a' : 'h'; leftToRight ? col <= 'h' : col >= 'a'; col += leftToRight ? 1 : -1) {
            System.out.print("\u2003" + col + " ");
        }
        System.out.println();
    }

    private String getPieceIcon(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}
