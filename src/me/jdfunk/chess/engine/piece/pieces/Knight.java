package me.jdfunk.chess.engine.piece.pieces;

import me.jdfunk.chess.engine.game.Board;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.engine.piece.PieceColor;
import me.jdfunk.chess.engine.piece.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(Position position, PieceColor color) {
        super(position, color, PieceType.KNIGHT);
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        Position pos = getPosition();
        int[][] offsets = {{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1},{-1,2}};

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

        return moves;
    }
}