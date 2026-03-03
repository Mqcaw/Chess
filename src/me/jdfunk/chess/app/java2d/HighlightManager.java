package me.jdfunk.chess.app.java2d;

import me.jdfunk.chess.app.asset.highlight.Highlight;
import me.jdfunk.chess.app.asset.highlight.HighlightType;
import me.jdfunk.chess.app.resource.Config;
import me.jdfunk.chess.engine.game.Position;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class HighlightManager {
    private static Map<HighlightType, List<Highlight>> highlights = new EnumMap<>(HighlightType.class);

    private static final int MOVE_OPTION_RADIUS = Config.defaultCellSizePixels / 7;

    public static void addHighlight(Highlight highlight) {
        highlights.computeIfAbsent(highlight.getType(), k -> new ArrayList<>())
                .add(highlight);
    }

    public static void removeHighlights(HighlightType type) {
        highlights.remove(type);
    }

    public static void removeHighlight(Highlight highlight) {
        highlights.get(highlight.getType()).remove(highlight);
    }

    public static void clearAll() {
        highlights.clear();
    }

    public static void renderAll(Java2DRenderer renderer) {
        int tileSize = renderer.getWindow().getSceneSize() / Config.defaultBoardSize;
        for (var entry : highlights.entrySet()) {
            HighlightType type = entry.getKey();
            List<Highlight> list = entry.getValue();

            for (Highlight h : list) {
                renderer.getGraphics2D().setColor(h.getColor());
                Point point = new Point(h.getCol() * tileSize, (7 - h.getRow()) * tileSize);

                switch (type) {
                    case MOVE_OPTION ->
                            renderer.getGraphics2D().fillOval((point.x + (tileSize / 2)) - MOVE_OPTION_RADIUS, (point.y + (tileSize / 2)) - MOVE_OPTION_RADIUS, MOVE_OPTION_RADIUS * 2, MOVE_OPTION_RADIUS * 2);
                    case HOVER -> renderer.getGraphics2D().fillRect(point.x, point.y, tileSize, tileSize);
                    case CAPTURE -> {
                        int triSize = tileSize / 4;
                        int x = (h.getCol() * tileSize);
                        int y = ((7 - h.getRow()) * tileSize);

                        for (int i = 0; i < 4; i++) {
                            Polygon p = new Polygon();

                            switch (i) {
                                case 0 -> { // top left
                                    p.addPoint(x, y);
                                    p.addPoint(x + triSize, y);
                                    p.addPoint(x, y + triSize);
                                }
                                case 1 -> { // top right
                                    p.addPoint(x + tileSize, y);
                                    p.addPoint(x + tileSize - triSize, y);
                                    p.addPoint(x + tileSize, y + triSize);
                                }
                                case 2 -> { // bottom right
                                    p.addPoint(x + tileSize, y + tileSize);
                                    p.addPoint(x + tileSize - triSize, y + tileSize);
                                    p.addPoint(x + tileSize, y + tileSize - triSize);
                                }
                                case 3 -> { // bottom left
                                    p.addPoint(x, y + tileSize);
                                    p.addPoint(x + triSize, y + tileSize);
                                    p.addPoint(x, y + tileSize - triSize);
                                }
                            }
                            renderer.getGraphics2D().fillPolygon(p);
                        }
                    }
                }
            }
        }
    }



}
