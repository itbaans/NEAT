package FlappyBird;

import javax.swing.*;
import java.awt.event.*;

public class FlappyBirdHuman extends FlappyBirdNEAT implements KeyListener {
    private Bird playerBird;

    public FlappyBirdHuman() {
        super();
        addKeyListener(this);
    }

    @Override
    protected void initializePopulation() {
        playerBird = new Bird(); // No DNA for human player
        birds.add(playerBird);
    }

    @Override
    protected void updateGame() {
        // Update player bird
        if (playerBird.isAlive()) {
            playerBird.update();
            checkCollision(playerBird);
        }

        // Update pipes
        for (int i = pipes.size() - 1; i >= 0; i--) {
            Pipe pipe = pipes.get(i);
            pipe.update();
            if (pipe.isOffscreen()) {
                pipes.remove(i);
            }
        }

        // Add new pipe
        framesUntilNextPipe--;
        if (framesUntilNextPipe <= 0) {
            addPipe();
            framesUntilNextPipe = 100;
        }
    }

    @Override
    protected boolean allBirdsDead() {
        return !playerBird.isAlive();
    }

    @Override
    protected void evolvePopulation() {
        // No evolution for human player
        resetGame();
    }

    @Override
    protected void resetGame() {
        pipes.clear();
        playerBird.reset();
        framesUntilNextPipe = 100;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (playerBird.isAlive()) {
                playerBird.flap();
            } else {
                resetGame();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird - Human Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        FlappyBirdHuman game = new FlappyBirdHuman();
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.requestFocusInWindow();
    }
}