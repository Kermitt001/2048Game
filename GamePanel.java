import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

public class GamePanel extends JPanel {
    private Game2048 game;
    private final int TILE_SIZE = 100;
    private final int GRID_SIZE = 4;
    private final int PADDING = 10;
    private final int PANEL_SIZE = GRID_SIZE * TILE_SIZE + (GRID_SIZE + 1) * PADDING;
    
    // Animation
    private Timer animationTimer;
    private List<Game2048.TileAnimation> currentAnimations = null;
    private double animationProgress = 0.0;
    private final int ANIMATION_DURATION = 150; // ms
    private final int ANIMATION_DELAY = 15; // ms (approx 60fps)
    private final double ANIMATION_STEP = (double)ANIMATION_DELAY / ANIMATION_DURATION;

    public GamePanel(Game2048 game) {
        this.game = game;
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE + 50)); // +50 for score
        setBackground(new Color(187, 173, 160));
        setFocusable(true);
        
        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationProgress += ANIMATION_STEP;
                if (animationProgress >= 1.0) {
                    animationProgress = 1.0;
                    currentAnimations = null;
                    animationTimer.stop();
                    // Check game states after animation finishes
                    checkGameStatus();
                }
                repaint();
            }
        });
        
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (animationTimer.isRunning()) return; // Block input during animation
                
                boolean moved = false;
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_UP:
                    case java.awt.event.KeyEvent.VK_W:
                        moved = game.moveUp();
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                    case java.awt.event.KeyEvent.VK_S:
                        moved = game.moveDown();
                        break;
                    case java.awt.event.KeyEvent.VK_LEFT:
                    case java.awt.event.KeyEvent.VK_A:
                        moved = game.moveLeft();
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                    case java.awt.event.KeyEvent.VK_D:
                        moved = game.moveRight();
                        break;
                }

                if (moved) {
                    game.addRandomTile();
                    currentAnimations = game.getLastAnimations();
                    animationProgress = 0.0;
                    animationTimer.start();
                }
            }
        });
    }
    
    private void checkGameStatus() {
        if (game.hasWon()) {
            javax.swing.JOptionPane.showMessageDialog(GamePanel.this, "You Win! Score: " + game.getScore());
        }
        if (game.isGameOver()) {
            javax.swing.JOptionPane.showMessageDialog(GamePanel.this, "Game Over! Score: " + game.getScore());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw Score
        g.setColor(new Color(187, 173, 160).darker());
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + game.getScore(), PADDING, 30);

        // Draw Background Grid (Empty cells)
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int x = PADDING + j * (TILE_SIZE + PADDING);
                int y = 50 + PADDING + i * (TILE_SIZE + PADDING);
                drawTileBackground(g, x, y);
            }
        }
        
        if (currentAnimations != null && !currentAnimations.isEmpty()) {
            // Draw animating tiles
            for (Game2048.TileAnimation anim : currentAnimations) {
                int x1 = PADDING + anim.c1 * (TILE_SIZE + PADDING);
                int y1 = 50 + PADDING + anim.r1 * (TILE_SIZE + PADDING);
                int x2 = PADDING + anim.c2 * (TILE_SIZE + PADDING);
                int y2 = 50 + PADDING + anim.r2 * (TILE_SIZE + PADDING);
                
                int curX = x1 + (int)((x2 - x1) * animationProgress);
                int curY = y1 + (int)((y2 - y1) * animationProgress);
                
                drawTile(g, anim.value, curX, curY);
            }
        } else {
            // Draw static board
            int[][] board = game.getBoard();
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    if (board[i][j] != 0) {
                        int x = PADDING + j * (TILE_SIZE + PADDING);
                        int y = 50 + PADDING + i * (TILE_SIZE + PADDING);
                        drawTile(g, board[i][j], x, y);
                    }
                }
            }
        }
    }

    private void drawTileBackground(Graphics g, int x, int y) {
        g.setColor(new Color(205, 193, 180));
        g.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 15, 15);
    }

    private void drawTile(Graphics g, int value, int x, int y) {
        g.setColor(getTileColor(value));
        g.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 15, 15);

        if (value > 0) {
            g.setColor(value <= 4 ? new Color(119, 110, 101) : Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, value < 100 ? 36 : value < 1000 ? 32 : 24));
            
            String s = String.valueOf(value);
            FontMetrics fm = g.getFontMetrics();
            int tx = x + (TILE_SIZE - fm.stringWidth(s)) / 2;
            int ty = y + (TILE_SIZE - fm.getHeight()) / 2 + fm.getAscent();
            
            g.drawString(s, tx, ty);
        }
    }

    private Color getTileColor(int value) {
        switch (value) {
            case 0: return new Color(205, 193, 180);
            case 2: return new Color(238, 228, 218);
            case 4: return new Color(237, 224, 200);
            case 8: return new Color(242, 177, 121);
            case 16: return new Color(245, 149, 99);
            case 32: return new Color(246, 124, 95);
            case 64: return new Color(246, 94, 59);
            case 128: return new Color(237, 207, 114);
            case 256: return new Color(237, 204, 97);
            case 512: return new Color(237, 200, 80);
            case 1024: return new Color(237, 197, 63);
            case 2048: return new Color(237, 194, 46);
            default: return Color.BLACK;
        }
    }
}
