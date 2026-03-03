package me.jdfunk.chess.app.java2d;

import me.jdfunk.chess.app.resource.Config;
import me.jdfunk.chess.engine.Engine;
import me.jdfunk.chess.engine.game.GameState;
import me.jdfunk.chess.shared.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Window {
    private static final Logger logger = Logger.getLogger(Window.class);

    private JPanel windowPanel;
    private Scene scene;
    private static JFrame frame;
    private Java2DRenderer renderer;

    public Window(Java2DRenderer renderer, Engine game) {
        try {
            this.renderer = renderer;
            createAndShowUI(game);
        } catch (Exception e) {
            logger.error("Failed to start the app.", e);
        }
    }

    private void createAndShowUI(Engine game) throws Exception {
        logger.info("Loading UI.");
        frame = new JFrame(Config.windowName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.windowPanel = new JPanel();
        this.windowPanel.setLayout(new BoxLayout(this.windowPanel, BoxLayout.X_AXIS));
        logger.info("Creating scene.");
        this.scene = new Scene(renderer, game);
        logger.trace("Scene created.");

        windowPanel.add(scene);
        frame.add(windowPanel);


        logger.info("Starting scene thread.");
        this.scene.start();
        logger.trace("Scene thread started.");

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(Config.resizable);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scene.setSceneSize((Math.min(frame.getWidth(), frame.getHeight())));
            }
        });

        logger.trace("UI loaded.");
    }

    public Engine getEngine() {
        return scene.getEngine();
    }

    public GameState getGameState() {
        return scene.getGameState();
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) scene.getGraphics();
    }

    public int getSceneSize() {
        return scene.getSceneSize();
    }

}
