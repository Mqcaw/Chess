package me.jdfunk.chess.engine.piece;

import me.jdfunk.chess.engine.game.Position;

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

}
