package Game;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Temp {
    private double x, y; // Position
    private double angle; // Rotation
    private double speed = 0.1; // Movement speed in units per millisecond
    private double rotationSpeed = 0.001; // Rotation speed in radians per millisecond

    public Temp(double x, double y) {
        this.x = x;
        this.y = y;
        this.angle = 0; // Initial angle
    }

    public void update(long deltaTime) {
        // Calculate movement based on speed and deltaTime
        double moveDistance = speed * deltaTime;
        double dx = moveDistance * Math.cos(angle);
        double dy = moveDistance * Math.sin(angle);
        x += dx;
        y += dy;

        // Handle rotation
        angle += rotationSpeed * deltaTime;
    }

    public void render(Graphics2D g2d) {
        // Render the player (e.g., a simple circle representing the player)
        int size = 20;
        g2d.setColor(Color.RED);
        g2d.fillOval((int) (x - size / 2), (int) (y - size / 2), size, size);
    }

    public void rotateLeft() {
        // Rotate left (counter-clockwise)
        angle -= rotationSpeed;
    }

    public void rotateRight() {
        // Rotate right (clockwise)
        angle += rotationSpeed;
    }

    // Other methods for handling movement controls (e.g., forward, backward)

    public static void main(String[] args) {
        final int WIDTH = 800;
        final int HEIGHT = 600;

        JFrame frame = new JFrame("Player Controls");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Temp player = new Temp(WIDTH / 2, HEIGHT / 2);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        player.rotateLeft();
                        System.out.println("left");
                        break;
                    case KeyEvent.VK_RIGHT:
                        player.rotateRight();
                        System.out.println("right");
                        break;
                    // Add other controls for movement (forward, backward) here
                }
            }
        });

        frame.add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                player.render(g2d);
            }
        });

        frame.setVisible(true);

        long lastTime = System.nanoTime();
        while (true) {
            long now = System.nanoTime();
            long deltaTime = now - lastTime;
            lastTime = now;

            player.update(deltaTime / 1000000); // Convert nanoseconds to milliseconds
            frame.repaint();

            try {
                Thread.sleep(10); // Small delay to control frame rate
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
