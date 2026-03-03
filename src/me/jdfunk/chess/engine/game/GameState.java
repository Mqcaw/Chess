package me.jdfunk.chess.engine.game;

import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.shared.PieceColor;
import me.jdfunk.chess.shared.logger.Logger;

public class GameState {
    private static final Logger logger = Logger.getLogger(GameState.class);

    private Board board;
    private PieceColor playerToMove = PieceColor.WHITE;
    private Piece selectedPiece;

    public GameState() {
        this.board = new Board();
    }

    public void selectPiece(Position position) {
        selectedPiece = board.getPieceAt(position);
        if (selectedPiece == null) {
            logger.error("Could not select piece, likely no piece not found at position: " + position);
        }
    }

    public boolean makeMove(Move move) {
        if (move.getMovingPiece().getColor() != playerToMove) return false;

        boolean success = board.applyMove(move);
        if (success){
            changeTurn();
        } else {
            logger.error("Could not apply move, " + move);
        }

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
