package me.jdfunk.chess.engine.piece.pieces;

import me.jdfunk.chess.engine.game.Board;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.engine.piece.PieceColor;
import me.jdfunk.chess.engine.piece.PieceType;

import java.util.List;

public class Bishop extends Piece {

    public Bishop(Position position, PieceColor color) {
        super(position, color, PieceType.BISHOP);
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board) {
        List<Move> moves;
        int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1}};
        moves = addSlidingMoves(board, directions);
        return moves;
    }
}