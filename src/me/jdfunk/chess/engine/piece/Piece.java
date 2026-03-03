package me.jdfunk.chess.engine.piece;

import me.jdfunk.chess.engine.game.Board;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.shared.PieceColor;
import me.jdfunk.chess.shared.PieceType;

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
        char t = switch (type) {
            case PAWN -> 'P';
            case ROOK -> 'R';
            case KNIGHT -> 'N';
            case BISHOP -> 'B';
            case QUEEN -> 'Q';
            case KING -> 'K';
        };
        if (color == PieceColor.BLACK) t = Character.toLowerCase(t);

        char p = switch (position.getX()) {
            case 0 -> 'a';
            case 1 -> 'b';
            case 2 -> 'c';
            case 3 -> 'd';
            case 4 -> 'e';
            case 5 -> 'f';
            case 6 -> 'g';
            case 7 -> 'h';
            default -> ' ';
        };
        return (t + "" + p + (position.getY() + 1));
    }
}
