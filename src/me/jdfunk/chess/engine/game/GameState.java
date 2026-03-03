package me.jdfunk.chess.engine.game;

import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.shared.PieceColor;
import me.jdfunk.chess.shared.logger.Logger;

public class GameState {
    private static final Logger logger = Logger.getLogger(GameState.class);

    private Board board;
    private PieceColor player1Color = PieceColor.WHITE;
    private PieceColor playerToMove = PieceColor.WHITE;
    private Piece selectedPiece;

    private State state = State.NORMAL;
    public GameState() {
        this.board = new Board();
        updateGameResult();
    }

    public GameState(PieceColor player1Color) {
        this();
        this.player1Color = player1Color;
    }

    public void selectPiece(Position position) {
        if (position == null) {
            selectedPiece = null;
            return;
        }
        Piece piece = board.getPieceAt(position);
        if (piece != null && piece.getColor() == playerToMove) {
            selectedPiece = piece;
        } else {
            selectedPiece = null;
        }
    }

    public boolean makeMove(Move move) {
        if (state != State.NORMAL) return false;
        if (move.getMovingPiece().getColor() != playerToMove) return false;

        boolean success = board.applyMove(move);
        if (success){
            changeTurn();
            updateGameResult();
        } else {
            logger.error("Could not apply move, " + move);
        }

        return success;
    }

    private void changeTurn() {
        playerToMove = playerToMove == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
    }

    private void updateGameResult() {
        state = StateChecks.evaluate(playerToMove, board);
        if (state != State.NORMAL) {
            logger.info("Game over: " + state + " (side to move: " + playerToMove + ")");
        }
    }

    public State getState() {
        return state;
    }

    public Board getBoard() {
        return board;
    }
    public void setBoard(Board board) {
        this.board = board;
        updateGameResult();
    }

    public PieceColor getPlayer1Color() {
        return player1Color;
    }
    public void setPlayer1Color(PieceColor player1Color) {
        this.player1Color = player1Color;
    }

    public PieceColor getPlayerToMove() {
        return playerToMove;
    }
    public void setPlayerToMove(PieceColor playerToMove) {
        this.playerToMove = playerToMove;
        updateGameResult();
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }
}
