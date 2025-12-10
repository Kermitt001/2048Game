import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main extends JFrame {

    private Game2048 game;
    private GamePanel panel;

    public Main() {
        game = new Game2048();
        panel = new GamePanel(game);

        add(panel);
        
        setTitle("2048 Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean moved = false;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        moved = game.moveUp();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        moved = game.moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        moved = game.moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        moved = game.moveRight();
                        break;
                }

                if (moved) {
                    game.addRandomTile();
                    panel.repaint();
                    
                    if (game.hasWon()) {
                        JOptionPane.showMessageDialog(Main.this, "You Win! Score: " + game.getScore());
                        // Optional: Reset logic
                    }
                    
                    if (game.isGameOver()) {
                        JOptionPane.showMessageDialog(Main.this, "Game Over! Score: " + game.getScore());
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
