package me.jdfunk.chess.app.java2d;

import me.jdfunk.chess.app.resource.Config;
import me.jdfunk.chess.engine.Engine;
import me.jdfunk.chess.engine.game.GameState;
import me.jdfunk.chess.shared.event.EventBus;
import me.jdfunk.chess.shared.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Scene extends JPanel implements Runnable{

    private Engine game;
    private Java2DRenderer renderer;

    private Thread gameThread;
    private boolean isRunning = false;

    private int size = Config.defaultCellSizePixels * Config.defaultBoardSize;

    private EventBus eventBus = new EventBus();

    private static final Logger logger = Logger.getLogger(Scene.class);

    public Scene(Java2DRenderer renderer, Engine game) throws Exception {
        //let swing use double buffering
        this.setDoubleBuffered(true);
        // we are using a game loop to repaint, so we don't want swing randomly doing it for us
        this.setIgnoreRepaint(true);

        this.game = game;
        this.renderer = renderer;

        initSwingMouseEvents();
    }

    public void start() {
        if (gameThread == null) {
            isRunning = true;
            gameThread = new Thread(this, "game-loop");
            gameThread.start();
        }
    }

    public void stop() {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            logger.error("Failed to join game thread.", e);
        }
    }

    public void restartGame() {
        this.game = new Engine();
    }

    @Override
    public void run() {
        while (isRunning) {
            repaint();
            try {
                long ms = (long) ((1.0f / Config.targetFrameRate) * 1000.0f);
                Thread.sleep(ms);
            } catch (InterruptedException e) { logger.error("Failed to sleep.", e); }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.setGraphics2D((Graphics2D) g);
        renderer.render();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(size, size);
    }

    private void initSwingMouseEvents() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                game.handleMouseClick(e.getX(), e.getY(), e.getButton());
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        };

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    public GameState getGameState() {
        return game.getGameState();
    }

    public int getSceneSize() {
        return size;
    }
    public void setSceneSize(int size) {
        this.size = size;
    }
}
