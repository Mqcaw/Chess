package me.jdfunk.chess.app.java2d;

import me.jdfunk.chess.app.resource.Config;
import me.jdfunk.chess.engine.Engine;
import me.jdfunk.chess.engine.game.GameState;
import me.jdfunk.chess.engine.game.Position;
import me.jdfunk.chess.shared.PieceType;
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
            game.update();
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

        if (game.isAwaitingPromotion()) {
            drawPromotionOverlay((Graphics2D) g);
        }

        framePresented();
        drawFpsOverlay((Graphics2D) g);
    }

    private void drawPromotionOverlay(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        int zoneSize = Config.defaultCellSizePixels;
        int gap = 8;

        int totalW = zoneSize * 4 + gap * 3;
        int x0 = (w - totalW) / 2;
        int y0 = (h - zoneSize) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, w, h);

        drawPromotionZone(g2, new Rectangle(x0 + (zoneSize + gap) * 0, y0, zoneSize, zoneSize), "Q");
        drawPromotionZone(g2, new Rectangle(x0 + (zoneSize + gap) * 1, y0, zoneSize, zoneSize), "R");
        drawPromotionZone(g2, new Rectangle(x0 + (zoneSize + gap) * 2, y0, zoneSize, zoneSize), "B");
        drawPromotionZone(g2, new Rectangle(x0 + (zoneSize + gap) * 3, y0, zoneSize, zoneSize), "N");
    }

    private void drawPromotionZone(Graphics2D g2, Rectangle r, String label) {
        g2.setColor(new Color(240, 240, 240, 230));
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        Font f = g2.getFont().deriveFont(Font.BOLD, 18f);
        g2.setFont(f);
        FontMetrics fm = g2.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(label)) / 2;
        int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(label, tx, ty);
    }

    private PieceType promotionHitTest(int mouseX, int mouseY) {
        int w = getWidth();
        int h = getHeight();

        int zoneSize = Config.defaultCellSizePixels;
        int gap = 8;

        int totalW = zoneSize * 4 + gap * 3;
        int x0 = (w - totalW) / 2;
        int y0 = (h - zoneSize) / 2;

        Rectangle q = new Rectangle(x0 + (zoneSize + gap) * 0, y0, zoneSize, zoneSize);
        Rectangle r = new Rectangle(x0 + (zoneSize + gap) * 1, y0, zoneSize, zoneSize);
        Rectangle b = new Rectangle(x0 + (zoneSize + gap) * 2, y0, zoneSize, zoneSize);
        Rectangle n = new Rectangle(x0 + (zoneSize + gap) * 3, y0, zoneSize, zoneSize);

        Point p = new Point(mouseX, mouseY);
        if (q.contains(p)) return PieceType.QUEEN;
        if (r.contains(p)) return PieceType.ROOK;
        if (b.contains(p)) return PieceType.BISHOP;
        if (n.contains(p)) return PieceType.KNIGHT;
        return null;
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
                if (game.isAwaitingPromotion() && SwingUtilities.isLeftMouseButton(e)) {
                    PieceType hit = promotionHitTest(e.getX(), e.getY());
                    if (hit != null) {
                        game.choosePromotion(hit);
                        return;
                    }
                    return;
                }

                Position cell = new Position(e.getX() / Config.defaultCellSizePixels, 7 - (e.getY() / Config.defaultCellSizePixels));
                game.handleMousePressed(cell, e.getButton());

                if (SwingUtilities.isLeftMouseButton(e)) {
                    renderer.setDraggingButton1(true);
                    renderer.setDragX(e.getX());
                    renderer.setDragY(e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (game.isAwaitingPromotion()) return;

                Position cell = new Position(e.getX() / Config.defaultCellSizePixels, 7 - (e.getY() / Config.defaultCellSizePixels));
                game.handleMouseReleased(cell, e.getButton());

                if (SwingUtilities.isLeftMouseButton(e)) {
                    renderer.setDraggingButton1(false);
                    renderer.setDragX(-1);
                    renderer.setDragY(-1);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (game.isAwaitingPromotion()) return;

                Position cell = new Position(e.getX() / Config.defaultCellSizePixels, 7 - (e.getY() / Config.defaultCellSizePixels));
                if (renderer.isDraggingButton1()) {
                    renderer.setDragX(e.getX());
                    renderer.setDragY(e.getY());
                }
                game.handleMouseDragged(cell);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (game.isAwaitingPromotion()) return;

                Position cell = new Position(e.getX() / Config.defaultCellSizePixels, 7 - (e.getY() / Config.defaultCellSizePixels));
                game.handleMouseMoved(cell);
            }
        };

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
    }

    private Position cellFromPixels(int x, int y) {
        return new Position(
                x / Config.defaultCellSizePixels,
                7 - (y / Config.defaultCellSizePixels)
        );
    }

    public GameState getGameState() {
        return game.getGameState();
    }

    public Engine getEngine() {
        return game;
    }

    public int getSceneSize() {
        return size;
    }
    public void setSceneSize(int size) {
        this.size = size;
    }
}
