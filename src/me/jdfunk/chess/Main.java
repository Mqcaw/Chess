package me.jdfunk.chess;

import me.jdfunk.chess.app.cui.CUIRenderer;
import me.jdfunk.chess.app.Renderer;
import me.jdfunk.chess.app.java2d.Java2DRenderer;
import me.jdfunk.chess.app.resource.ResourceManager;
import me.jdfunk.chess.engine.Engine;
import me.jdfunk.chess.engine.game.GameState;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.shared.logger.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    static void main(String[] args) {
        logger.setMinimumLevel(Logger.LogLevel.TRACE);

        if (args.length == 0) logger.warn("No arguments provided. Is this an error?");
        logger.info("Starting Chess Application");

        try {
            ResourceManager.init();
        } catch (Exception e) {
            logger.error("Could not initialize resources", e);
            return;
        }

        Engine game = new Engine();
        //game.getGameState().selectPiece(new Position(4, 1));

        //debug renderer
        Renderer cui = new CUIRenderer();
        cui.init(game);
        cui.render();

        //main renderer
        Java2DRenderer java2DRenderer = new Java2DRenderer();
        java2DRenderer.init(game);
    }
}
