package me.jdfunk.chess.app.cui;

import me.jdfunk.chess.app.Renderer;
import me.jdfunk.chess.engine.Engine;
import me.jdfunk.chess.engine.game.GameState;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.engine.logic.MoveValidator;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.shared.PieceColor;
import me.jdfunk.chess.shared.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class CUIRenderer extends Renderer {
    private static final Logger logger = Logger.getLogger(CUIRenderer.class);

    private Engine game;
    private boolean initialized;

    @Override
    public void init(Engine game) {
        this.game = game;
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void render() {
        GameState gameState = game.getGameState();
        if (!initialized) {logger.error("CUI Renderer not initialized");}

        //draw the board
        //draw each cell, if a piece is there, draw it as well
        List<Move> possibleMoves = new ArrayList<>();
        if (gameState.getSelectedPiece() != null) {
            possibleMoves = MoveValidator.getLegalMoves(gameState.getSelectedPiece(), gameState.getBoard());
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (gameState.getBoard().getPieceAt(new Position(j, 7 - i)) != null) {
                    System.out.print("|" + getPieceChar(gameState.getBoard().getPieceAt(new Position(j, 7- i))));
                } else {
                    int finalJ = j;
                    int finalI = i;
                    if (possibleMoves.stream().anyMatch(move -> move.getTo().equals(new Position(finalJ, 7 - finalI)))) {
                        System.out.print("|#");
                    } else {
                        System.out.print("| ");
                    }
                }
                if (j == 7) {
                    System.out.print("|");
                    System.out.println();
                }
            }
        }
        System.out.println();
    }

    private char getPieceChar(Piece piece) {
        char pieceChar;
        switch (piece.getType()) {
            case KING -> pieceChar = 'K';
            case QUEEN -> pieceChar = 'Q';
            case ROOK -> pieceChar = 'R';
            case BISHOP -> pieceChar = 'B';
            case KNIGHT -> pieceChar = 'N';
            case PAWN -> pieceChar = 'P';
            default -> pieceChar = ' ';
        }
        if (piece.getColor() == PieceColor.BLACK) {
            return Character.toLowerCase(pieceChar);
        }
        return pieceChar;
    }
}
