import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 25;
    private final int GRID_SIZE = 20;
    private final int BOARD_SIZE = TILE_SIZE * GRID_SIZE;
    
    private ArrayList<Point> snake;
    private Point food;
    private char direction = 'R'; 
    private boolean isGameOver = false;
    private boolean isPaused = false;
    private boolean gameStarted = false;
    private Timer timer;
    private int score = 0;
    private int highScore = 0;

    public SnakeGame() {
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setBackground(new Color(10, 15, 25)); // Deep space blue
        setFocusable(true);
        addKeyListener(this);
        
        // Timer set to 120ms for smooth but challenging speed
        timer = new Timer(200, this);
    }

    private void initGame() {
        snake = new ArrayList<>();
        snake.add(new Point(10, 10));
        snake.add(new Point(10, 11));
        score = 0;
        direction = 'U';
        isGameOver = false;
        gameStarted = true;
        spawnFood();
        timer.start();
    }

    private void spawnFood() {
        Random rand = new Random();
        food = new Point(rand.nextInt(GRID_SIZE), rand.nextInt(GRID_SIZE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!gameStarted) {
            drawScreen(g2, "SNAKE 2026", "Press ENTER to Start");
            return;
        }

        if (isGameOver) {
            if (score > highScore) highScore = score;
            drawScreen(g2, "GAME OVER", "Score: " + score + " | Press SPACE");
            return;
        }

        // Draw Food with a "Glow"
        g2.setColor(new Color(255, 0, 255));
        g2.fillOval(food.x * TILE_SIZE + 4, food.y * TILE_SIZE + 4, TILE_SIZE - 8, TILE_SIZE - 8);
        
        // Draw Snake with Gradient
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            if (i == 0) g2.setColor(new Color(0, 255, 150)); // Neon Cyan Head
            else g2.setColor(new Color(0, 150, 255, 255 - (i * 5))); // Fading Body
            
            g2.fillRoundRect(p.x * TILE_SIZE + 1, p.y * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2, 8, 8);
        }

        // UI Overlay
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Verdana", Font.BOLD, 14));
        g2.drawString("SCORE: " + score, 15, 25);
        g2.drawString("HIGH: " + highScore, 15, 45);
        if (isPaused) g2.drawString("PAUSED", BOARD_SIZE/2 - 30, 25);
    }

    private void drawScreen(Graphics2D g2, String title, String sub) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Verdana", Font.BOLD, 40));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (BOARD_SIZE - fm.stringWidth(title)) / 2, BOARD_SIZE / 2 - 20);
        
        g2.setFont(new Font("Verdana", Font.PLAIN, 18));
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(sub, (BOARD_SIZE - fm2.stringWidth(sub)) / 2, BOARD_SIZE / 2 + 30);
    }

    public void move() {
        if (isPaused || !gameStarted) return;

        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);

        if (direction == 'U') newHead.y--;
        else if (direction == 'D') newHead.y++;
        else if (direction == 'L') newHead.x--;
        else if (direction == 'R') newHead.x++;

        // Borderless Mode: Snake appears on the other side
        if (newHead.x < 0) newHead.x = GRID_SIZE - 1;
        else if (newHead.x >= GRID_SIZE) newHead.x = 0;
        if (newHead.y < 0) newHead.y = GRID_SIZE - 1;
        else if (newHead.y >= GRID_SIZE) newHead.y = 0;

        for (Point p : snake) {
            if (p.equals(newHead)) {
                isGameOver = true;
                timer.stop();
            }
        }

        snake.add(0, newHead);
        if (newHead.equals(food)) {
            score += 10;
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ENTER && !gameStarted) initGame();
        if (key == KeyEvent.VK_SPACE && isGameOver) initGame();
        if (key == KeyEvent.VK_P) isPaused = !isPaused;

        if (key == KeyEvent.VK_UP && direction != 'D') direction = 'U';
        else if (key == KeyEvent.VK_DOWN && direction != 'U') direction = 'D';
        else if (key == KeyEvent.VK_LEFT && direction != 'R') direction = 'L';
        else if (key == KeyEvent.VK_RIGHT && direction != 'L') direction = 'R';
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Pro 2026");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}