package me.jdfunk.chess.engine.game;

import me.jdfunk.chess.engine.piece.Piece;

import java.util.Objects;

public class Move {
    private Position from;
    private Position to;
    private boolean isCapture;
    private Piece movingPiece;
    private Piece capturedPiece;
    private boolean isCastling = false;
    private Piece rookMoved = null;
    private boolean isEnPassant = false;

    //regular move
    public Move(Position from, Position to, Piece movingPiece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
        this.isCapture = this.capturedPiece != null;
    }

    //en passant move
    public Move(Position from, Position to, Piece movingPiece, Piece capturedPiece, Boolean isEnPassant) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
        this.isCapture = capturedPiece != null;
        this.isEnPassant = isEnPassant;
    }

    //castling move
    public Move(Position from, Position to, Piece movingPiece, Piece capturedPiece, Piece rookMoved) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
        this.isCapture = capturedPiece != null;
        this.isCastling = capturedPiece != null;
        this.rookMoved = rookMoved;
    }

    public void remapPieceReferences(Piece movingPiece, Piece capturedPiece, Piece rookMoved) {
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
        this.rookMoved = rookMoved;
    }

    public Position getFrom() {
        return from;
    }
    public void setFrom(Position from) {
        this.from = from;
    }

    public Position getTo() {
        return to;
    }
    public void setTo(Position to) {
        this.to = to;
    }

    public boolean isCapture() {
        return isCapture;
    }
    public void setCapture(boolean capture) {
        isCapture = capture;
    }

    public Piece getMovingPiece() {
        return movingPiece;
    }
    public void setMovingPiece(Piece movingPiece) {
        this.movingPiece = movingPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }
    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public boolean isCastling() {
        return isCastling;
    }
    public void setCastling(boolean castling) {
        isCastling = castling;
    }

    public Piece getRookMoved() {
        return rookMoved;
    }
    public void setRookMoved(Piece rookMoved) {
        this.rookMoved = rookMoved;
    }

    public boolean isEnPassant() {
        return isEnPassant;
    }
    public void setEnPassant(boolean enPassant) {
        isEnPassant = enPassant;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return isCapture() == move.isCapture() && isCastling() == move.isCastling() && isEnPassant() == move.isEnPassant() && Objects.equals(getFrom(), move.getFrom()) && Objects.equals(getTo(), move.getTo()) && Objects.equals(getMovingPiece(), move.getMovingPiece()) && Objects.equals(getCapturedPiece(), move.getCapturedPiece()) && Objects.equals(getRookMoved(), move.getRookMoved());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), isCapture(), getMovingPiece(), getCapturedPiece(), isCastling(), getRookMoved(), isEnPassant());
    }
}
