package me.jdfunk.chess.app.asset.highlight;

import java.awt.*;

public class Highlight {
    private final int col;
    private final int row;
    private final Color color;
    private final HighlightType type;

    public Highlight(int col, int row, Color color, HighlightType type) {
        this.color = color;
        this.type = type;
        this.col = col;
        this.row = row;
    }

    public HighlightType getType() { return type; }
    public Color getColor() { return color; }
    public int getCol() { return col; }
    public int getRow() { return row; }
}