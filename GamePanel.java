import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Dimension;

public class GamePanel extends JPanel {
    private Game2048 game;
    private final int TILE_SIZE = 100;
    private final int GRID_SIZE = 4;
    private final int PADDING = 10;
    private final int PANEL_SIZE = GRID_SIZE * TILE_SIZE + (GRID_SIZE + 1) * PADDING;

    public GamePanel(Game2048 game) {
        this.game = game;
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE + 50)); // +50 for score
        setBackground(new Color(187, 173, 160));
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int[][] board = game.getBoard();
        
        // Draw Score
        g.setColor(new Color(187, 173, 160).darker());
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + game.getScore(), PADDING, 30);

        // Draw Grid
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int x = PADDING + j * (TILE_SIZE + PADDING);
                int y = 50 + PADDING + i * (TILE_SIZE + PADDING); // Offset by 50 for score
                
                int value = board[i][j];
                drawTile(g, value, x, y);
            }
        }
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
