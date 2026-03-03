package me.jdfunk.chess.engine.game;

import me.jdfunk.chess.engine.logic.MoveValidator;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.shared.PieceColor;
import me.jdfunk.chess.shared.PieceType;

import java.util.*;

public final class StateChecks {
    private StateChecks() {}

    public static State evaluate(PieceColor sideToMove, Board board) {
        // 1) Repetition draw (threefold)
        // Do this early; it’s cheap and independent from check.
        if (isThreefoldRepetition(board)) {
            return State.DRAW_THREEFOLD_REPETITION;
        }

        // 2) Insufficient material draw
        if (isInsufficientMaterial(board)) {
            return State.DRAW_INSUFFICIENT_MATERIAL;
        }

        // 3) Checkmate / stalemate (no legal moves)
        List<Move> legal = MoveValidator.getAllLegalMoves(sideToMove, board);
        if (legal.isEmpty()) {
            boolean inCheck = MoveValidator.isInCheck(sideToMove, board);
            return inCheck ? State.CHECKMATE : State.STALEMATE_NO_LEGAL_MOVES;
        }

        return State.NORMAL;
    }

    public static boolean isThreefoldRepetition(Board currentBoard) {
        Map<String, Integer> counts = new HashMap<>();

        Board replay = new Board(); // initial position
        PieceColor sideToMove = PieceColor.WHITE;

        // Count initial position
        counts.merge(positionKey(replay, sideToMove), 1, Integer::sum);

        for (Move m : currentBoard.getMoves()) {
            replay.applyMoveUnchecked(m);
            sideToMove = (sideToMove == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

            String key = positionKey(replay, sideToMove);
            int c = counts.merge(key, 1, Integer::sum);
            if (c >= 3) return true;
        }

        return false;
    }

    private static String positionKey(Board board, PieceColor sideToMove) {
        StringBuilder sb = new StringBuilder(256);
        sb.append(sideToMove.name()).append('|');

        board.getPieces().stream()
                .sorted(Comparator.comparing(Piece::getColor).thenComparing(Piece::getType).thenComparingInt(a -> a.getPosition().getY()).thenComparingInt(a -> a.getPosition().getX()))
                .forEach(p -> {
                    sb.append(p.getColor().name().charAt(0))
                            .append(p.getType().name())
                            .append('@')
                            .append(p.getPosition().getX())
                            .append(',')
                            .append(p.getPosition().getY())
                            .append('#')
                            .append(p.hasMoved() ? '1' : '0')
                            .append(';');
                });

        return sb.toString();
    }

    /**
     * Practical insufficient-material detection:
     * - K vs K
     * - K+N vs K
     * - K+B vs K
     * - K+B vs K+B with bishops on same color squares
     *
     * Note: This assumes you can access piece type and position.
     */
    public static boolean isInsufficientMaterial(Board board) {
        Map<PieceColor, MaterialCount> counts = new EnumMap<>(PieceColor.class);
        counts.put(PieceColor.WHITE, new MaterialCount());
        counts.put(PieceColor.BLACK, new MaterialCount());

        for (Piece p : board.getPieces()) {
            MaterialCount c = counts.get(p.getColor());
            PieceType t = p.getType(); // If your Piece API differs, tell me what it is and I’ll adjust.
            switch (t) {
                case KING -> c.kings++;
                case QUEEN -> c.queens++;
                case ROOK -> c.rooks++;
                case PAWN -> c.pawns++;
                case BISHOP -> {
                    c.bishops++;
                    c.bishopSquareColorXor ^= squareColorBit(p.getPosition()); // needs getPosition()
                }
                case KNIGHT -> c.knights++;
                default -> { /* ignore */ }
            }
        }

        // Any pawns, rooks, queens -> not insufficient.
        for (MaterialCount c : counts.values()) {
            if (c.pawns > 0 || c.rooks > 0 || c.queens > 0) return false;
        }

        MaterialCount w = counts.get(PieceColor.WHITE);
        MaterialCount b = counts.get(PieceColor.BLACK);

        // Only kings
        if (w.bishops == 0 && w.knights == 0 && b.bishops == 0 && b.knights == 0) return true;

        // K+N vs K
        if (w.bishops == 0 && w.knights == 1 && b.bishops == 0 && b.knights == 0) return true;
        if (b.bishops == 0 && b.knights == 1 && w.bishops == 0 && w.knights == 0) return true;

        // K+B vs K
        if (w.bishops == 1 && w.knights == 0 && b.bishops == 0 && b.knights == 0) return true;
        if (b.bishops == 1 && b.knights == 0 && w.bishops == 0 && w.knights == 0) return true;

        // K+B vs K+B with bishops on same color
        if (w.bishops == 1 && w.knights == 0 && b.bishops == 1 && b.knights == 0) {
            // If both bishops are on same color squares, mate is impossible.
            // We tracked square colors via XOR; for exactly 1 bishop each, compare their color bits.
            int wColor = w.bishopSquareColorXor;
            int bColor = b.bishopSquareColorXor;
            return wColor == bColor;
        }

        return false;
    }

    /**
     * Returns 0 for dark squares, 1 for light squares (or vice versa; consistency is what matters).
     */
    private static int squareColorBit(Position pos) {
        // Assuming 0-based file/rank; if yours are 1-based adjust accordingly.
        return (pos.getX() + pos.getY()) & 1;
    }

    private static final class MaterialCount {
        int kings;
        int queens;
        int rooks;
        int pawns;
        int bishops;
        int knights;

        int bishopSquareColorXor;
    }
}