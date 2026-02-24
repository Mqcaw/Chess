package me.jdfunk.chess.engine.game;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        if (y < 0) y = 0;
        if (y >= 8) y = 7;
        if (x < 0) x = 0;
        if (x >= 8) x = 7;

        this.x = x;
        this.y = y;
    }

    public Position copy() {
        return new Position(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return 31 + y + x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
