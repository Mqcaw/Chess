package me.jdfunk.chess.engine.logic;

import me.jdfunk.chess.engine.game.Board;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.shared.PieceColor;
import me.jdfunk.chess.shared.PieceType;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {

    public static boolean isLegal(Move move, Board board) {
        Piece piece = move.getMovingPiece();
        List<Move> moves = getLegalMoves(piece, board);
        return moves.contains(move);
    }


    public static List<Move> getLegalMoves(Piece piece, Board board) {
        List<Move> pseudoLegal = piece.getPseudoLegalMoves(board);
        List<Move> legal = new ArrayList<>();

        for (Move move : pseudoLegal) {
            if (!leavesKingInCheck(move, board)) {
                legal.add(move);
            }
        }

        return legal;
    }

    public static List<Move> getAllLegalMoves(PieceColor color, Board board) {
        List<Move> legal = new ArrayList<>();
        for (Piece piece : board.getPieces()) {
            if (piece.getColor() == color) {
                legal.addAll(getLegalMoves(piece, board));
            }
        }
        return legal;
    }

    private static boolean leavesKingInCheck(Move move, Board board) {
        Board simulated = board.simulate(move);
        PieceColor movingColor = move.getMovingPiece().getColor();
        Position kingPos = findKing(movingColor, simulated);
        return isSquareAttacked(kingPos, movingColor, simulated);
    }

    private static boolean isSquareAttacked(Position pos, PieceColor defendingColor, Board board) {
        for (Piece piece : board.getPieces()) {
            if (piece.getColor() == defendingColor) continue;
            for (Move move : piece.getPseudoLegalMoves(board)) {
                if (move.getTo().equals(pos)) return true;
            }
        }
        return false;
    }

    private static Position findKing(PieceColor color, Board board) {
        return board.getPieces().stream()
                .filter(p -> p.getColor() == color && p.getType() == PieceType.KING)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No king found for " + color))
                .getPosition();
    }

    public static boolean isInCheck(PieceColor color, Board board) {
        Position kingPos = findKing(color, board);
        return isSquareAttacked(kingPos, color, board);
    }

    public boolean isCheckmate(PieceColor color, Board board) {
        return isInCheck(color, board) && getAllLegalMoves(color, board).isEmpty();
    }

    public boolean isStalemate(PieceColor color, Board board) {
        return !isInCheck(color, board) && getAllLegalMoves(color, board).isEmpty();
    }
}