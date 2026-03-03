package me.jdfunk.chess.app.java2d;

import me.jdfunk.chess.app.Renderer;
import me.jdfunk.chess.app.asset.PieceSet;
import me.jdfunk.chess.app.asset.highlight.Highlight;
import me.jdfunk.chess.app.asset.highlight.HighlightType;
import me.jdfunk.chess.app.resource.Config;
import me.jdfunk.chess.app.resource.ImageLoader;
import me.jdfunk.chess.app.resource.ResourceManager;
import me.jdfunk.chess.engine.Engine;
import me.jdfunk.chess.engine.game.GameState;
import me.jdfunk.chess.engine.game.Move;
import me.jdfunk.chess.engine.logic.MoveValidator;
import me.jdfunk.chess.engine.piece.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Java2DRenderer extends Renderer {
    private boolean initialized = false;
    private Window window;
    private Graphics2D graphics2D;

    private PieceSet pieceSet = ResourceManager.getPieceSet(Config.defaultPieceSet);

    @Override
    public void init(Engine game) {
        window = new Window(this, game);
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }


    //TODO: am i loading from file every frame? yes, need to fix
    @Override
    public void render() {
        GameState gameState = window.getGameState();
        if (graphics2D == null) return;
        //main render function for a game, no need to worry about Swing for the game rendering
        int size = window.getSceneSize();
        int tileSize = Config.defaultCellSizePixels;

        graphics2D.drawImage(ImageLoader.loadImage(String.valueOf(ResourceManager.getTheme(Config.defaultBoardTheme).getImagePath())), 0, 0, size, size, null);


        List<Move> possibleMoves;
        if (gameState.getSelectedPiece() != null) {
            possibleMoves = MoveValidator.getLegalMoves(gameState.getSelectedPiece(), gameState.getBoard());
            HighlightManager.removeHighlights(HighlightType.MOVE_OPTION);
            HighlightManager.removeHighlights(HighlightType.CAPTURE);
            for (Move move : possibleMoves) {
                if (move.getCapturedPiece() != null) {
                    HighlightManager.addHighlight(new Highlight(move.getTo().getX(), move.getTo().getY(), Config.moveOptionColor, HighlightType.CAPTURE));
                } else {
                    HighlightManager.addHighlight(new Highlight(move.getTo().getX(), move.getTo().getY(), Config.moveOptionColor, HighlightType.MOVE_OPTION));
                }
            }
        }

        HighlightManager.renderAll(this);

        for (Piece piece : gameState.getBoard().getPieces()) {
            Image image = ImageLoader.loadImage(String.valueOf(ResourceManager.getPathForPieceSet(ResourceManager.getPieceSet(Config.defaultPieceSet), piece)));

            BufferedImage bufferedImage = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, tileSize, tileSize, null);
            g.dispose();

            graphics2D.drawImage(bufferedImage, piece.getPosition().getX() * tileSize, (7 - piece.getPosition().getY()) * tileSize, null);
        }

    }

    public Graphics2D getGraphics2D() {
        return graphics2D;
    }
    public void setGraphics2D(Graphics2D graphics2D) {
        this.graphics2D = graphics2D;
    }

    public Window getWindow() {
        return window;
    }

}
