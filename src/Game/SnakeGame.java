package Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SnakeGame extends JPanel implements KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int CELL_SIZE = 20;
    private static final int NUM_ROWS = HEIGHT / CELL_SIZE;
    private static final int NUM_COLS = WIDTH / CELL_SIZE;
    private static final int DELAY = 50; // milliseconds

    private List<Point> snake;
    private Point food;
    private int direction;
    private boolean gameOver;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        initializeGame();
    }

    private void initializeGame() {
        snake = new ArrayList<>();
        snake.add(new Point(NUM_COLS / 2, NUM_ROWS / 2)); // Initial snake position
        direction = KeyEvent.VK_RIGHT; // Initial direction
        generateFood();
        gameOver = false;
        startGame();
    }

    private void startGame() {
        Thread gameThread = new Thread(this::gameLoop);
        gameThread.start();
    }

    private void gameLoop() {
        while (!gameOver) {
            update();
            repaint();
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (gameOver) return;

        // Move the snake
        moveSnake();

        // Check for collisions
        checkCollisions();

        // Check if food has been eaten
        if (snake.get(0).equals(food)) {
            snake.add(food);
            generateFood();
        }
    }

    private void moveSnake() {
        Point head = new Point(snake.get(0));
        switch (direction) {
            case KeyEvent.VK_UP:
                head.y--;
                break;
            case KeyEvent.VK_DOWN:
                head.y++;
                break;
            case KeyEvent.VK_LEFT:
                head.x--;
                break;
            case KeyEvent.VK_RIGHT:
                head.x++;
                break;
        }
        snake.add(0, head);
        if (!snake.get(0).equals(food)) {
            snake.remove(snake.size() - 1);
        }
    }

    private void checkCollisions() {
        Point head = snake.get(0);
        // Check collision with food (handled in update method)
        // Check collision with boundaries
        if (head.x < 0 || head.x >= NUM_COLS || head.y < 0 || head.y >= NUM_ROWS) {
            gameOver = true;
            return;
        }
        // Check collision with itself
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                return;
            }
        }
    }

    private void generateFood() {
        int foodX = (int) (Math.random() * NUM_COLS);
        int foodY = (int) (Math.random() * NUM_ROWS);
        food = new Point(foodX, foodY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSnake(g);
        drawFood(g);
        if (gameOver) {
            drawGameOver(g);
        }
    }

    private void drawSnake(Graphics g) {
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    private void drawFood(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String gameOverMsg = "Game Over! Press R to restart";
        int msgWidth = g.getFontMetrics().stringWidth(gameOverMsg);
        g.drawString(gameOverMsg, (WIDTH - msgWidth) / 2, HEIGHT / 2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) {
            direction = KeyEvent.VK_UP;
        } else if (keyCode == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP) {
            direction = KeyEvent.VK_DOWN;
        } else if (keyCode == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) {
            direction = KeyEvent.VK_LEFT;
        } else if (keyCode == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) {
            direction = KeyEvent.VK_RIGHT;
        } else if (keyCode == KeyEvent.VK_R && gameOver) {
            initializeGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new SnakeGame(), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
