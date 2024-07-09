// FlappyBirdNEAT.java
package FlappyBird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import NEAT_STUFF.*;

import NeuralNetwork.*;

public class FlappyBirdNEAT extends JPanel implements ActionListener {
    protected static final int WIDTH = 800;
    protected static final int HEIGHT = 600;
    protected static final int DELAY = 5;
    protected static final int PIPE_WIDTH = 50;
    protected static final int PIPE_GAP = 140;
    // protected static final int POPULATION_SIZE = 100;
    // protected static final int MAX_GENERATIONS = 1000;

    protected ArrayList<Bird> birds;
    protected ArrayList<Pipe> pipes;
    protected Timer timer;
    protected Random random;
    protected int generation;
    protected int framesUntilNextPipe;

    public FlappyBirdNEAT() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        setFocusable(true);

        birds = new ArrayList<>();
        pipes = new ArrayList<>();
        random = new Random();
        generation = 1;
        framesUntilNextPipe = 0;

        initializePopulation();
        setBirdBrains(Population.getPopulationDNAs());

        timer = new Timer(DELAY, this);
        timer.start();
    }

    protected void initializePopulation() {
        for (int i = 0; i < AlotOfConstants.popSize; i++) {
            //DNA dna = new DNA(4, 1, 1); // 4 inputs, 1 hidden layer, 1 output
            birds.add(new Bird());
        }
    }

    protected void setBirdBrains(DNA[] dnas) {
        for(int i = 0; i < AlotOfConstants.popSize; i++) {
            birds.get(i).setBrain(dnas[i]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (allBirdsDead()) {
            evolvePopulation();
        } else {
            updateGame();
        }
        repaint();
    }

    protected boolean allBirdsDead() {
        for (Bird bird : birds) {
            if (bird.isAlive()) {
                return false;
            }
        }
        return true;
    }

    protected void evolvePopulation() {
        // Implement NEAT evolution here
        // This would involve selecting the fittest birds, crossover, and mutation
        if(Population.currentGen < AlotOfConstants.generations) {
            try {
                Population.evolve();
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            resetGame();
            setBirdBrains(Population.getPopulationDNAs());
            
        }
        
    }

    protected void resetGame() {
        pipes.clear();
        for (Bird bird : birds) {
            bird.reset();
        }
        framesUntilNextPipe = 0;
    }

    protected void updateGame() {
        // Update birds
        for (Bird bird : birds) {
            if (bird.isAlive()) {
                bird.fitness++;
                double[] inputs = getInputs(bird);
                boolean flap = bird.think(inputs);
                if (flap) {
                    bird.flap();
                }
                bird.update();
                checkCollision(bird);
            }
            else {
                bird.finalizeFitness();
            }
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

    protected double[] getInputs(Bird bird) {
        Pipe nextPipe = getNextPipe(bird);
        if (nextPipe == null) {
            return new double[]{0, 0, 0, 0};
        }
        double[] inputs = new double[6];

        inputs[0] = bird.y / HEIGHT;
        inputs[1] = (nextPipe.x - bird.x) / WIDTH;
        inputs[2] = nextPipe.topHeight / HEIGHT;
        inputs[3] = bird.velocity / 10.0;
        inputs[4] = (nextPipe.topHeight + FlappyBirdNEAT.PIPE_GAP) / HEIGHT;

        return inputs;
    }

    protected Pipe getNextPipe(Bird bird) {
        for (Pipe pipe : pipes) {
            if (pipe.x + PIPE_WIDTH > bird.x) {
                return pipe;
            }
        }
        return null;
    }

    protected void checkCollision(Bird bird) {

        //Check collision with ground
        if (bird.y + Bird.SIZE > HEIGHT || bird.y + Bird.SIZE < 0) {
            bird.die();
        }

        // Check collision with pipes
        for (Pipe pipe : pipes) {
            if (bird.x + Bird.SIZE > pipe.x && bird.x < pipe.x + PIPE_WIDTH) {
                if (bird.y < pipe.topHeight || bird.y + Bird.SIZE > pipe.topHeight + PIPE_GAP) {
                    bird.die();
                }
            }
        }
    }

    protected void addPipe() {
        int topHeight = random.nextInt(HEIGHT - PIPE_GAP - 100) + 50;
        pipes.add(new Pipe(WIDTH - 100, topHeight));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Pipe pipe : pipes) {
            pipe.draw(g);
        }
        for (Bird bird : birds) {
            if (bird.isAlive()) {
                bird.draw(g);
            }
        }
        g.setColor(Color.BLACK);
        g.drawString("Generation: " + generation, 10, 20);
        g.drawString("Alive: " + countAliveBirds(), 10, 40);
    }

    protected int countAliveBirds() {
        int count = 0;
        for (Bird bird : birds) {
            if (bird.isAlive()) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {

        Population population = new Population(AlotOfConstants.popSize, AlotOfConstants.inputs, AlotOfConstants.outputs);
        JFrame frame = new JFrame("Flappy Bird NEAT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new FlappyBirdNEAT(), BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class Bird {
    public static final int SIZE = 30;
    private static final double GRAVITY = 0.6;
    private static final double FLAP_FORCE = -10;

    double fitness = 0;

    double x, y, velocity;
    private boolean alive;
    private NueralNetwork brain;

    public Bird() {
        x = 100;
        y = 300;
        velocity = 0;
        alive = true;
        //brain = new NueralNetwork(dna);
    }

    public void finalizeFitness() {
        brain.setFitness(fitness);
    }

    public void setBrain(DNA dna) {
        this.brain = new NueralNetwork(dna);
    }

    public void update() {
        velocity += GRAVITY;
        y += velocity;
    }

    public void flap() {
        velocity = FLAP_FORCE;
    }

    public boolean think(double[] inputs) {
        brain.setInputs(inputs);

        // for(double d : inputs) {
        //     System.out.print(d + " ");
        // }    
        double[] outputs = brain.getOutputs();
        return outputs[0] > outputs[1];
    }

    public void die() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void reset() {
        x = 100;
        y = 300;
        velocity = 0;
        alive = true;
        fitness = 0;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int) x, (int) y, SIZE, SIZE);
    }
}

class Pipe {
    int x, topHeight;
    private static final int SPEED = 3;

    public Pipe(int x, int topHeight) {
        this.x = x;
        this.topHeight = topHeight;
    }

    public void update() {
        x -= SPEED;
    }

    public boolean isOffscreen() {
        return x + FlappyBirdNEAT.PIPE_WIDTH < 0;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, 0, FlappyBirdNEAT.PIPE_WIDTH, topHeight);
        g.fillRect(x, topHeight + FlappyBirdNEAT.PIPE_GAP, FlappyBirdNEAT.PIPE_WIDTH, FlappyBirdNEAT.HEIGHT - topHeight - FlappyBirdNEAT.PIPE_GAP);
    }
}

// FlappyBirdHuman.java
