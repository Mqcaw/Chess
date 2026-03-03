package me.jdfunk.chess.app.resource;

import me.jdfunk.chess.shared.logger.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageLoader {
    private static final Logger logger = Logger.getLogger(ImageLoader.class);

    public static BufferedImage loadImage(String relativePath) {
        Path externalPath = ResourceManager.getResourcesDirectory().resolve(relativePath);
        if (Files.exists(externalPath)) {
            try {
                return ImageIO.read(externalPath.toFile());
            } catch (IOException e) {
                logger.error("Failed to load external image: " + externalPath, e);;
            }
        }

        logger.error("Image not found at: " + relativePath, new RuntimeException());
        return null;
    }
}
