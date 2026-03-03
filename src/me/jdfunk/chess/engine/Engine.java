package me.jdfunk.chess.engine;

import me.jdfunk.chess.app.resource.Config;
import me.jdfunk.chess.engine.game.GameState;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.engine.game.State;
import me.jdfunk.chess.engine.logic.MoveValidator;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.shared.PieceColor;
import me.jdfunk.chess.shared.PieceType;
import me.jdfunk.chess.shared.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class Engine {
    private final Logger logger = Logger.getLogger(Engine.class);

    GameState gameState = new GameState();

    private boolean dragging = false;
    private Position dragFrom = null;
    private Position hoverCell = null;

    private boolean awaitingPromotion = false;
    private final List<Move> pendingPromotionMoves = new ArrayList<>();


    public void update() {
        if (gameState.getState() != State.NORMAL) return;
        if (awaitingPromotion) return;

        if (gameState.getPlayerToMove() != gameState.getPlayer1Color()) {
            List<Move> moves = MoveValidator.getAllLegalMoves(gameState.getPlayerToMove(), gameState.getBoard());
            if (!moves.isEmpty()) {
                // Prefer queen promotions for AI if available
                Move queenPromo = moves.stream()
                        .filter(Move::isPromotion)
                        .filter(m -> m.getPromotionType() == PieceType.QUEEN)
                        .findFirst()
                        .orElse(null);

                Move move = (queenPromo != null)
                        ? queenPromo
                        : moves.get((int) (Math.random() * moves.size()));

                gameState.makeMove(move);
            } else {
                logger.info("Game over, no legal moves for player " + gameState.getPlayerToMove());
            }
        }
    }

    public void choosePromotion(PieceType type) {
        if (!awaitingPromotion) return;

        Move chosen = pendingPromotionMoves.stream()
                .filter(Move::isPromotion)
                .filter(m -> m.getPromotionType() == type)
                .findFirst()
                .orElse(null);

        if (chosen == null) return;

        pendingPromotionMoves.clear();
        awaitingPromotion = false;

        gameState.makeMove(chosen);
        gameState.selectPiece(null);
        dragFrom = null;
        hoverCell = null;
        dragging = false;
    }


    public void handleMousePressed(Position cell, int button) {
        if (awaitingPromotion) return;

        if (button == 3) {
            cancelDragAndDeselect();
            return;
        }

        if (button != 1) return;

        Piece selected = gameState.getSelectedPiece();

        if (!dragging && selected != null && cell != null) {
            List<Move> legalMoves = MoveValidator.getLegalMoves(selected, gameState.getBoard());

            Move legalClickMove = legalMoves.stream()
                    .filter(move -> move.getMovingPiece() == selected)
                    .filter(move -> cell.equals(move.getTo()))
                    .findFirst()
                    .orElse(null);

            if (legalClickMove != null) {
                if (tryStartPromotionChoice(legalMoves, selected, cell)) {
                    return;
                }

                gameState.makeMove(legalClickMove);
                gameState.selectPiece(null);
                dragFrom = null;
                hoverCell = null;
                dragging = false;
                return;
            }
        }

        gameState.selectPiece(cell);
        if (gameState.getSelectedPiece() != null) {
            dragging = true;
            dragFrom = cell;
            hoverCell = cell;
        } else {
            dragging = false;
            dragFrom = null;
            hoverCell = null;
        }
    }

    public void handleMouseDragged(Position cell) {
        if (!dragging) return;
        hoverCell = cell;
    }

    public void handleMouseMoved(Position cell) {
        if (!dragging) return;
        hoverCell = cell;
    }

    public void handleMouseReleased(Position cell, int button) {
        if (awaitingPromotion) return;

        if (button == 3) {
            cancelDragAndDeselect();
            return;
        }

        if (button != 1) return;
        if (!dragging) return;

        Position to = cell;
        Piece selected = gameState.getSelectedPiece();

        dragging = false;
        hoverCell = to;

        if (selected == null || dragFrom == null || to == null) {
            gameState.selectPiece(null);
            dragFrom = null;
            return;
        }

        if (to.equals(dragFrom)) {
            dragFrom = null;
            return;
        }

        List<Move> legalMoves = MoveValidator.getLegalMoves(selected, gameState.getBoard());

        Move legal = legalMoves.stream()
                .filter(move -> move.getMovingPiece() == selected)
                .filter(move -> to.equals(move.getTo()))
                .findFirst()
                .orElse(null);

        if (legal != null) {
            if (tryStartPromotionChoice(legalMoves, selected, to)) {
                dragFrom = null;
                return;
            }

            gameState.makeMove(legal);
            gameState.selectPiece(null);
        } else {
            gameState.selectPiece(null);
        }

        dragFrom = null;
    }

    private boolean tryStartPromotionChoice(List<Move> legalMoves, Piece selected, Position to) {
        // Promotion is represented as 4 different legal moves with same from/to, different promotionType.
        List<Move> promos = legalMoves.stream()
                .filter(m -> m.getMovingPiece() == selected)
                .filter(m -> to.equals(m.getTo()))
                .filter(Move::isPromotion)
                .toList();

        if (promos.isEmpty()) return false;

        pendingPromotionMoves.clear();
        pendingPromotionMoves.addAll(promos);
        awaitingPromotion = true;

        // stop interactions until choice is made
        dragging = false;
        dragFrom = null;
        hoverCell = to;
        gameState.selectPiece(null);
        return true;
    }

    private void cancelDragAndDeselect() {
        dragging = false;
        dragFrom = null;
        hoverCell = null;
        gameState.selectPiece(null);
    }

    public boolean isAwaitingPromotion() {
        return awaitingPromotion;
    }

    public Position getHoverCell() {
        return hoverCell;
    }

    public GameState getGameState() {
        return gameState;
    }
}
