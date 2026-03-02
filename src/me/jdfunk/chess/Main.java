package me.jdfunk.chess;

import me.jdfunk.chess.app.CUIRenderer;
import me.jdfunk.chess.app.Renderer;
import me.jdfunk.chess.engine.game.GameState;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.shared.logger.Logger;

import javax.swing.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    static void main(String[] args) {
        logger.info("Starting Chess Application...");

        Renderer renderer = new CUIRenderer();
        renderer.init();

        GameState gameState = new GameState();
        System.out.println(gameState.getBoard().getPieces());

        renderer.render(gameState);

        //Position position = new Position(4, 1);
        //gameState.makeMove(new Move(position, position.offset(0, 2), gameState.getBoard().getPieceAt(position), null));

        gameState.selectPiece((new Position(4, 1)));

        renderer.render(gameState);
    }
}
