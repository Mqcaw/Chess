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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Java2DRenderer extends Renderer {
    private boolean initialized = false;
    private Window window;
    private Graphics2D graphics2D;
    private static BufferedImage cachedBoardImage;
    private static HashMap<String, BufferedImage> pieceImages = new HashMap<>();
    private PieceSet pieceSet;


    @Override
    public void init(Engine game) {
        window = new Window(this, game);
        pieceSet = ResourceManager.getPieceSet(Config.defaultPieceSet);
        cachedBoardImage = ImageLoader.loadImage(String.valueOf(ResourceManager.getTheme(Config.defaultBoardTheme).getImagePath()));
        ResourceManager.cachePieceSetImages(Config.defaultPieceSet, pieceImages);
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void render() {
        GameState gameState = window.getGameState();
        if (graphics2D == null) return;
        //main render function for a game, no need to worry about Swing for the game rendering
        int size = window.getSceneSize();
        int tileSize = Config.defaultCellSizePixels;

        graphics2D.drawImage(cachedBoardImage, 0, 0, size, size, null);


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
            Image image = pieceImages.get(ResourceManager.getPathForPieceSet(pieceSet, piece).getFileName().toString());


            BufferedImage bufferedImage = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, tileSize, tileSize, null);
            g.dispose();

            graphics2D.drawImage(bufferedImage, piece.getPosition().getX() * tileSize, (7 - piece.getPosition().getY()) * tileSize, null);
        }

    }


    public static BufferedImage getCachedBoardImage() {
        return cachedBoardImage;
    }
    public static void setCachedBoardImage(String themeName) {
        cachedBoardImage = ImageLoader.loadImage(String.valueOf(ResourceManager.getTheme(themeName).getImagePath()));
    }

    public PieceSet getPieceSet() {
        return pieceSet;
    }
    public void setPieceSet(PieceSet pieceSet) {
        this.pieceSet = pieceSet;
        ResourceManager.cachePieceSetImages(pieceSet.getName(), pieceImages);
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
