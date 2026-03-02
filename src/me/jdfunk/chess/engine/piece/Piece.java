package me.jdfunk.chess.engine.piece;

import me.jdfunk.chess.engine.game.Board;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.shared.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    private Position position;
    private PieceColor color;
    private PieceType type;
    private boolean hasMoved = false;

    public Piece(Position position, PieceColor color, PieceType type) {
        this.position = position;
        this.color = color;
        this.type = type;
    }

    public abstract List<Move> getPseudoLegalMoves(Board board);

    protected List<Move> addSlidingMoves(Board board, int[][] directions) {
        List<Move> moves = new ArrayList<>();
        Position pos = getPosition();
        for (int[] dir : directions) {
            Position current = pos.offset(dir[0], dir[1]);
            while (board.isWithinBounds(current)) {
                Piece target = board.getPieceAt(current);
                if (target == null) {
                    moves.add(new Move(pos, current, this, null));
                } else {
                    if (target.getColor() != getColor()) {
                        moves.add(new Move(pos, current, this, target));
                    }
                    break;
                }
                current = current.offset(dir[0], dir[1]);
            }
        }
        return moves;
    }

    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public PieceColor getColor() {
        return color;
    }
    public void setColor(PieceColor color) {
        this.color = color;
    }

    public PieceType getType() {
        return type;
    }
    public void setType(PieceType type) {
        this.type = type;
    }

    public boolean hasMoved() {
        return hasMoved;
    }
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public String toString() {
        char t = ' ';
        switch (type) {
            case PAWN: t = 'P'; break;
            case ROOK: t = 'R'; break;
            case KNIGHT: t = 'N'; break;
            case BISHOP: t = 'B'; break;
            case QUEEN: t = 'Q'; break;
            case KING: t = 'K'; break;

        }
        if (color == PieceColor.BLACK) t = Character.toLowerCase(t);

        char p = ' ';
        switch (position.getX()) {
            case 0: p = 'a'; break;
            case 1: p = 'b'; break;
            case 2: p = 'c'; break;
            case 3: p = 'd'; break;
            case 4: p = 'e'; break;
            case 5: p = 'f'; break;
            case 6: p = 'g'; break;
            case 7: p = 'h'; break;
        }
        return (t + "" + p + (position.getY() + 1));
    }
}
