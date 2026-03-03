package me.jdfunk.chess.app.asset;

import java.nio.file.Path;

public class BoardTheme {
    private final String name;
    private final Path imagePath;

    public BoardTheme(String name, Path imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public Path getImagePath() {
        return imagePath;
    }
}
