package me.jdfunk.chess.engine.piece.pieces;

import me.jdfunk.chess.engine.game.Board;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.shared.PieceColor;
import me.jdfunk.chess.shared.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(Position position, PieceColor color) {
        super(position, color, PieceType.PAWN);
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        Position pos = getPosition();
        int dir = getColor() == PieceColor.WHITE ? 1 : -1;

        int promotionRank = (getColor() == PieceColor.WHITE) ? 7 : 0;

        //Single push
        Position oneStep = pos.offset(0, dir);
        if (board.isWithinBounds(oneStep) && board.getPieceAt(oneStep) == null) {
            addPawnMoveOrPromotions(moves, pos, oneStep, null, promotionRank);

            //Double push (cannot be a promotion)
            int startRank = getColor() == PieceColor.WHITE ? 1 : 6;
            if (pos.getY() == startRank) {
                Position twoStep = pos.offset(0, dir * 2);
                if (board.getPieceAt(twoStep) == null) {
                    moves.add(new Move(pos, twoStep, this, null));
                }
            }
        }

        //Diagonal captures
        for (int dx : new int[]{-1, 1}) {
            Position capturePos = pos.offset(dx, dir);
            if (!board.isWithinBounds(capturePos)) continue;

            Piece target = board.getPieceAt(capturePos);
            if (target != null && target.getColor() != getColor()) {
                addPawnMoveOrPromotions(moves, pos, capturePos, target, promotionRank);
            }

            //En passant (cannot be a promotion in standard chess)
            Move lastMove = null;
            if (board.getMoves().size() != 0) {
                lastMove = board.getLastMove();
            }
            if (lastMove != null) {
                Piece lastMoved = lastMove.getMovingPiece();
                if (lastMoved.getType() == PieceType.PAWN
                        && lastMoved.getColor() != getColor()
                        && Math.abs(lastMove.getFrom().getY() - lastMove.getTo().getY()) == 2
                        && lastMove.getTo().equals(pos.offset(dx, 0))) {
                    moves.add(new Move(pos, capturePos, this, lastMoved, true));
                }
            }
        }

        return moves;
    }

    private void addPawnMoveOrPromotions(List<Move> out, Position from, Position to, Piece captured, int promotionRank) {
        if (to.getY() == promotionRank) {
            for (PieceType promo : new PieceType[]{PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT}) {
                Move m = new Move(from, to, this, captured);
                m.setPromotionType(promo);
                out.add(m);
            }
        } else {
            out.add(new Move(from, to, this, captured));
        }
    }
}
