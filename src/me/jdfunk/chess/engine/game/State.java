package me.jdfunk.chess.engine.game;

public enum State {
    NORMAL,
    CHECKMATE,
    STALEMATE_NO_LEGAL_MOVES,
    DRAW_INSUFFICIENT_MATERIAL,
    DRAW_THREEFOLD_REPETITION
}
