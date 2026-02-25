package me.jdfunk.chess.engine.game;

import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.engine.piece.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Piece> pieces;
    private List<Move> moves;

    public Board() {
        pieces = new ArrayList<>();
        moves = new ArrayList<>();
    }

    public Board(List<Piece> pieces, List<Move> moves) {
        this.pieces = pieces;
        this.moves = moves;
    }

    public void initBoard() {

    }

    public Board simulate(Move move) {
        Board copy = this.deepCopy();

        Piece movingPiece = copy.getPieceAt(move.getFrom());

        //Remove captured piece
        if (move.getCapturedPiece() != null) {
            copy.removePieceAt(move.getCapturedPiece().getPosition());
        }

        //Move the piece
        movingPiece.setPosition(move.getTo());

        //Handle castling
        if (move.getRookMoved() != null) {
            Piece rook = copy.getPieceAt(move.getRookMoved().getPosition());
            boolean kingside = move.getTo().getX() > move.getFrom().getX();
            int rookDestX = kingside ? move.getTo().getX() - 1 : move.getTo().getX() + 1;
            rook.setPosition(new Position(rookDestX, move.getFrom().getY()));
        }

        return copy;
    }

    public Board deepCopy() {
        List<Piece> copiedPieces = new ArrayList<>();

        for (Piece piece : this.pieces) {
            copiedPieces.add(copyPiece(piece));
        }

        //Copy move history
        List<Move> copiedMoves = new ArrayList<>(this.moves);

        Board copy = new Board(copiedPieces, copiedMoves);

        //Re-resolve piece references inside the last move to point to the copied pieces
        if (!copiedMoves.isEmpty()) {
            Move lastMove = copiedMoves.get(copiedMoves.size() - 1);
            copy.remapLastMove(copiedPieces, lastMove);
        }

        return copy;
    }

    private Piece copyPiece(Piece piece) {
        Position copiedPos = new Position(piece.getPosition().getX(), piece.getPosition().getY());

        Piece copy = switch (piece.getType()) {
            case PAWN   -> new Pawn(copiedPos, piece.getColor());
            case KNIGHT -> new Knight(copiedPos, piece.getColor());
            case BISHOP -> new Bishop(copiedPos, piece.getColor());
            case ROOK   -> new Rook(copiedPos, piece.getColor());
            case QUEEN  -> new Queen(copiedPos, piece.getColor());
            case KING   -> new King(copiedPos, piece.getColor());
        };

        if (piece.hasMoved()) copy.setHasMoved(true);

        return copy;
    }

    private void remapLastMove(List<Piece> copiedPieces, Move lastMove) {
        Piece remappedMoving = findByPosition(copiedPieces, lastMove.getMovingPiece().getPosition());
        Piece remappedCaptured = lastMove.getCapturedPiece() != null
                ? findByPosition(copiedPieces, lastMove.getCapturedPiece().getPosition())
                : null;
        Piece remappedRook = lastMove.getRookMoved() != null
                ? findByPosition(copiedPieces, lastMove.getRookMoved().getPosition())
                : null;

        lastMove.remapPieceReferences(remappedMoving, remappedCaptured, remappedRook);
    }

    private Piece findByPosition(List<Piece> pieces, Position pos) {
        return pieces.stream()
                .filter(p -> p.getPosition().equals(pos))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No piece found at " + pos + " during deep copy"));
    }

    public Piece getPieceAt(Position position) {
        for (Piece piece : pieces) {
            if (piece.getPosition().equals(position)) return piece;
        }
        return null;
    }

    public void removePieceAt(Position position) {
        pieces.removeIf(p -> p.getPosition().equals(position));
    }

    public boolean isWithinBounds(Position position) {
        return (position.getX() >= 0 && position.getX() <= 7 && position.getY() >= 0 && position.getY() <= 7);
    }

    public Move getLastMove() {
        return moves.get(moves.size() - 1);
    }

    public List<Piece> getPieces() {
        return pieces;
    }
    public List<Move> getMoves() {
        return moves;
    }
    public void addMove(Move move) {
        moves.add(move);
    }
    public void addPiece(Piece piece) {
        pieces.add(piece);
    }
}
