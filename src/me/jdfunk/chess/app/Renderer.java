package me.jdfunk.chess.app;

import me.jdfunk.chess.engine.game.GameState;

import java.awt.image.BufferedImage;

public abstract class Renderer {
    public abstract void init();
    public abstract boolean isInitialized();

    public abstract void render(GameState gameState);
}
