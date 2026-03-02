package me.jdfunk.chess.engine.game;

import me.jdfunk.chess.engine.logic.MoveValidator;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.engine.piece.PieceColor;
import me.jdfunk.chess.engine.piece.pieces.*;
import me.jdfunk.chess.shared.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private static final Logger logger = Logger.getLogger(Board.class);
    private List<Piece> pieces;
    private List<Move> moves;

    public Board() {
        pieces = new ArrayList<>();
        moves = new ArrayList<>();
        initBoard();
    }

    public Board(List<Piece> pieces, List<Move> moves) {
        this.pieces = pieces;
        this.moves = moves;
    }

    public void initBoard() {
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(new Position(i, 1), PieceColor.WHITE));
        }

        pieces.add(new Rook(new Position(0, 0), PieceColor.WHITE));
        pieces.add(new Knight(new Position(1, 0), PieceColor.WHITE));
        pieces.add(new Bishop(new Position(2, 0), PieceColor.WHITE));
        pieces.add(new Queen(new Position(3, 0), PieceColor.WHITE));
        pieces.add(new King(new Position(4, 0), PieceColor.WHITE));
        pieces.add(new Bishop(new Position(5, 0), PieceColor.WHITE));
        pieces.add(new Knight(new Position(6, 0), PieceColor.WHITE));
        pieces.add(new Rook(new Position(7, 0), PieceColor.WHITE));

        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(new Position(i, 6), PieceColor.BLACK));
        }

        pieces.add(new Rook(new Position(0, 7), PieceColor.BLACK));
        pieces.add(new Knight(new Position(1, 7), PieceColor.BLACK));
        pieces.add(new Bishop(new Position(2, 7), PieceColor.BLACK));
        pieces.add(new Queen(new Position(3, 7), PieceColor.BLACK));
        pieces.add(new King(new Position(4, 7), PieceColor.BLACK));
        pieces.add(new Bishop(new Position(5, 7), PieceColor.BLACK));
        pieces.add(new Knight(new Position(6, 7), PieceColor.BLACK));
        pieces.add(new Rook(new Position(7, 7), PieceColor.BLACK));
    }

    public boolean applyMoveUnchecked(Move move) {
        //Do not trust move.getMovingPiece()/getCapturedPiece() references here
        Piece moving = getPieceAt(move.getFrom());
        if (moving == null) {
            logger.error("No moving piece found at " + move.getFrom(), new Exception("No moving piece found at " + move.getFrom() + " during applyMoveUnchecked"));
        }

        Piece captured = getPieceAt(move.getTo());
        if (captured != null) {
            removePieceAt(captured.getPosition());
        }

        moving.setPosition(move.getTo());

        // Castling handling based on coordinates rather than move's piece references.
        // (King moves two squares horizontally.)
        int dx = move.getTo().getX() - move.getFrom().getX();
        if (moving.getType() == me.jdfunk.chess.engine.piece.PieceType.KING && Math.abs(dx) == 2) {
            boolean kingside = dx > 0;
            int rookFromX = kingside ? 7 : 0;
            int rookToX = kingside ? move.getTo().getX() - 1 : move.getTo().getX() + 1;

            Piece rook = getPieceAt(new Position(rookFromX, move.getFrom().getY()));
            if (rook != null) {
                rook.setPosition(new Position(rookToX, move.getFrom().getY()));
            }
        }

        moves.add(move);
        return true;
    }

    public boolean applyMove(Move move) {
        //can trust move.getMovingPiece()/getCapturedPiece() references here
        if (!MoveValidator.isLegal(move, this)) return false;
        return applyMoveUnchecked(move);
    }

    public Board simulate(Move move) {
        Board copy = this.deepCopy();
        copy.applyMoveUnchecked(move);
        return copy;
    }

    public Board deepCopy() {
        List<Piece> copiedPieces = new ArrayList<>();
        for (Piece piece : this.pieces) {
            copiedPieces.add(copyPiece(piece));
        }

        Board copy = new Board(copiedPieces, new ArrayList<>());

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

    public Piece getPieceAt(Position position) {
        //return pieces.stream().filter(p -> p.getPosition().equals(position)).findFirst().orElse(null);
        for (Piece piece : pieces) {
            if (piece.getPosition().equals(position)) {
                return piece;
            }
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
