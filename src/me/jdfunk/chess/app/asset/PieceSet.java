package me.jdfunk.chess.app.asset;

import java.nio.file.Path;

public class PieceSet {
    private final String name;
    private final Path folder;

    public PieceSet(String name, Path folder) {
        this.name = name;
        this.folder = folder;
    }

    public String getName() {
        return name;
    }


    public Path getFolder() {
        return folder;
    }
}
