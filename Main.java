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
        
        // Remove KeyListener from here as it's now in GamePanel
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
            main.panel.requestFocusInWindow();
        });
    }
}
