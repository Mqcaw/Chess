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

        //Single push
        Position oneStep = pos.offset(0, dir);
        if (board.isWithinBounds(oneStep) && board.getPieceAt(oneStep) == null) {
            moves.add(new Move(pos, oneStep, this, null));

            //Double push
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
                moves.add(new Move(pos, capturePos, this, target));
            }

            //En passant
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
}
