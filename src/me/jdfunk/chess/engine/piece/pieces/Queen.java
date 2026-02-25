package me.jdfunk.chess.engine.piece.pieces;

import me.jdfunk.chess.engine.game.Board;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.engine.piece.PieceColor;
import me.jdfunk.chess.engine.piece.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(Position position, PieceColor color) {
        super(position, color, PieceType.QUEEN);
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        addSlidingMoves(board, moves, directions);
        return moves;
    }
}