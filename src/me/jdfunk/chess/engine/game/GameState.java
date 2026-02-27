package me.jdfunk.chess.engine.game;

import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.engine.piece.PieceColor;

public class GameState {
    private Board board;
    private PieceColor playerToMove = PieceColor.WHITE;
    private Piece selectedPiece;


    public GameState(Board board) {
        this.board = board;
    }

    public GameState() {
        this.board = new Board();
    }

    public void selectPiece(int index) {
        selectedPiece = board.getPieces().get(index);
    }

    public boolean makeMove(Move move) {
        if (move.getMovingPiece().getColor() != playerToMove) return false;

        boolean success = board.applyMove(move);
        if (success) changeTurn();

        return success;
    }

    private void changeTurn() {
        playerToMove = playerToMove == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
    }


    public Board getBoard() {
        return board;
    }
    public void setBoard(Board board) {
        this.board = board;
    }

    public PieceColor getPlayerToMove() {
        return playerToMove;
    }
    public void setPlayerToMove(PieceColor playerToMove) {
        this.playerToMove = playerToMove;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }
}
