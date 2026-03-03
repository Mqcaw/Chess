package me.jdfunk.chess.app.resource;

import me.jdfunk.chess.app.asset.BoardTheme;
import me.jdfunk.chess.app.asset.PieceSet;
import me.jdfunk.chess.engine.piece.Piece;
import me.jdfunk.chess.shared.PieceColor;
import me.jdfunk.chess.shared.PieceType;
import me.jdfunk.chess.shared.logger.Logger;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//TODO: ResourceManager should be reworked later
public class ResourceManager {
    private static final Logger logger = Logger.getLogger(ResourceManager.class);

    private static Path BASE_DIRECTORY = getJarDirectory();
    private static final Path RESOURCES_DIR = BASE_DIRECTORY.resolve("resources");
    private static final Path THEMES_DIR = RESOURCES_DIR.resolve("themes");
    private static final Path PIECES_DIR = RESOURCES_DIR.resolve("pieces");
    private static final Path ICONS_DIR = RESOURCES_DIR.resolve("icons");

    private static HashMap<String, BoardTheme> themeCache = new HashMap<>();
    private static HashMap<String, PieceSet> pieceSetCache = new HashMap<>();

    private static Properties configFile = new Properties();

    public static void init() {
        logger.info("Resource directory: " + RESOURCES_DIR);
        try {
            loadConfig();
            loadThemes();
            loadPieceSets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static void loadConfig() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(RESOURCES_DIR, "*.properties")) {
            for (Path path : stream) {
                String name = stripExtension(path.getFileName().toString());
                if (name.equals("config")) {
                    configFile.load(new FileInputStream(path.toFile()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Config.windowName = configFile.getProperty("windowName");
        Config.defaultBoardSize = Integer.parseInt(configFile.getProperty("defaultBoardSize"));
        Config.defaultCellSizePixels = Integer.parseInt(configFile.getProperty("defaultCellSizePixels"));
        Config.defaultBoardTheme = configFile.getProperty("defaultBoardTheme");
        Config.defaultPieceSet = configFile.getProperty("defaultPieceSet");
        Config.resizable = Boolean.parseBoolean(configFile.getProperty("resizable"));
        Config.targetFrameRate = Integer.parseInt(configFile.getProperty("targetFrameRate"));

        String[] parts = configFile.getProperty("moveOptionColor").split(" ");
        Config.moveOptionColor = new Color(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

        for (String key : configFile.stringPropertyNames()) {
            logger.trace(key + " = " + configFile.getProperty(key));
        }
    }


    private static void loadThemes() throws IOException {
        themeCache.clear();
        if (!Files.exists(THEMES_DIR)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(THEMES_DIR, "*.jpg")) {
            for (Path path : stream) {
                String name = stripExtension(path.getFileName().toString());
                themeCache.put(name, new BoardTheme(name, path));
            }
        }
    }
    private static void loadPieceSets() throws IOException {
        pieceSetCache.clear();
        if (!Files.exists(PIECES_DIR)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(PIECES_DIR)) {
            for (Path dir : stream) {
                if (Files.isDirectory(dir)) {
                    String name = dir.getFileName().toString();
                    pieceSetCache.put(name, new PieceSet(name, dir));
                }
            }
        }
    }

    public static Path getPathForPieceSet(PieceSet pieceSet, Piece piece) {
        if (pieceSet.getName().equals("mono")) {
            return pieceSet.getFolder().resolve(getTypeChar(piece.getType()) + ".png");
        } else if (pieceSet.getName().equals("disguised")) {
            return pieceSet.getFolder().resolve(getColorChar(piece.getColor()) + ".png");
        } else {
            return pieceSet.getFolder().resolve(getColorChar(piece.getColor()) + getTypeChar(piece.getType()) + ".png");
        }
    }

    private static void ensureStructure() throws IOException {
        Files.createDirectories(THEMES_DIR);
        Files.createDirectories(PIECES_DIR);
        Files.createDirectories(ICONS_DIR);
    }
    public static String getTypeChar(PieceType type) {
        return switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
    }
    public static String getColorChar(PieceColor color) {
        return switch (color) {
            case WHITE -> "w";
            case BLACK -> "b";
        };
    }
    private static String stripExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return (i > 0) ? filename.substring(0, i) : filename;
    }
    public static Path getJarDirectory() {
        try {
            Path jarPath = Paths.get(ResourceManager.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());

            if (jarPath.toString().endsWith(".jar"))
                return jarPath.getParent();
        } catch (Exception e) {
            return Paths.get(".");
        }
        return null;
    }
    public static Path getResourcesDirectory() {
        return RESOURCES_DIR;
    }

    public static BoardTheme getTheme(String name) {
        return themeCache.get(name);
    }
    public static PieceSet getPieceSet(String name) {
        return pieceSetCache.get(name);
    }
}
