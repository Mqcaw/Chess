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

    private long fpsSceneStartNs = System.nanoTime();
    private int framesInScene = 0;
    private volatile double lastFps = 0.0;
    private static final long FPS_SCENE_NS = 150_000_000L; // 250ms update cadence


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

        framePresented();
        drawFpsOverlay((Graphics2D) g);
    }

    private void framePresented() {
        framesInScene ++;
        long nowNs = System.nanoTime();
        long elapsedNs = nowNs - fpsSceneStartNs;

        if (elapsedNs >= FPS_SCENE_NS) {
            lastFps = framesInScene / (elapsedNs / 1_000_000_000.0);
            fpsSceneStartNs = nowNs;
            framesInScene = 0;
        }
    }

    private void drawFpsOverlay(Graphics2D g2) {
        String text = "FPS: " + Math.round(lastFps);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        FontMetrics fm = g2.getFontMetrics();

        int pad = 4;
        int x = 0;
        int y = 0;

        int w = fm.stringWidth(text) + pad * 2;
        int h = fm.getAscent() + fm.getDescent() + pad * 2;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(x, y, w, h);

        g2.setColor((lastFps > 144) ? Color.WHITE : (lastFps > 100 ? Color.GREEN : (lastFps > 60 ? Color.YELLOW : Color.RED)));
        g2.drawString(text, x + pad, y + pad + fm.getAscent());
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
