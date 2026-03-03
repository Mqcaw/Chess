package me.jdfunk.chess.app;

import me.jdfunk.chess.engine.Engine;
import me.jdfunk.chess.engine.game.GameState;

import java.awt.image.BufferedImage;

public abstract class Renderer {
    public abstract void init(Engine engine);
    public abstract boolean isInitialized();
    public abstract void render();
}
