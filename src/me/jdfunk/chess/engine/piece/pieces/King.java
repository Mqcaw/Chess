package me.jdfunk.chess.engine.piece.pieces;

import me.jdfunk.chess.engine.game.Board;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.engine.piece.PieceColor;
import me.jdfunk.chess.engine.piece.PieceType;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Position position, PieceColor color) {
        super(position, color, PieceType.KING);
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        Position pos = getPosition();
        int[][] offsets = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};

        for (int[] o : offsets) {
            Position target = pos.offset(o[0], o[1]);
            if (!board.isWithinBounds(target)) continue;
            Piece piece = board.getPieceAt(target);
            if (piece == null) {
                moves.add(new Move(pos, target, this, null));
            } else if (piece.getColor() != getColor()) {
                moves.add(new Move(pos, target, this, piece));
            }
        }

        if (!hasMoved()) {
            tryCastle(board, moves, 1);
            tryCastle(board, moves, -1);
        }

        return moves;
    }

    private void tryCastle(Board board, List<Move> moves, int dir) {
        Position pos = getPosition();
        int steps = dir == 1 ? 2 : 3;
        for (int i = 1; i <= steps; i++) {
            if (board.getPieceAt(pos.offset(dir * i, 0)) != null) return;
        }
        Position rookPos = pos.offset(dir == 1 ? 3 : -4, 0);
        Piece rook = board.getPieceAt(rookPos);
        if (rook == null || rook.getType() != PieceType.ROOK
                || rook.getColor() != getColor() || rook.hasMoved()) return;

        // capturedPiece is null — castling is not a capture, but rook reference is still useful
        // so you may want a dedicated castlingRook field on Move for this case
        moves.add(new Move(pos, pos.offset(dir * 2, 0), this, null, rook));
    }
}