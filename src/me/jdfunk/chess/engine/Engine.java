package me.jdfunk.chess.engine;

import me.jdfunk.chess.engine.game.GameState;
import me.jdfunk.chess.shared.logger.Logger;

public class Engine {
    private final Logger logger = Logger.getLogger(Engine.class);

    GameState gameState = new GameState();


    public void handleMouseClick(int x, int y, int button) {
        logger.info("Mouse click: " + x + ", " + y + ", " + button);
    }


    public GameState getGameState() {
        return gameState;
    }
}
