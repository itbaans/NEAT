package TheGame2;

import javax.swing.*;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

import NEAT_STUFF.AlotOfConstants;
import NEAT_STUFF.Population;

//import java.time.Instant;
import java.util.ArrayList;
//import java.util.Collections;
//import java.time.Duration;
//import java.util.Arrays;

import NeuralNetwork.*;

import java.awt.*;
import java.awt.event.*;

public class BotServival extends JPanel implements KeyListener {
    
    private static final int WIDTH = GameConstants.width * GameConstants.tileSize;
    private static final int HEIGHT = GameConstants.height * GameConstants.tileSize;
    private static final int DELAY = 5; // milliseconds
    private MapGenerator map = new MapGenerator();
    boolean goalReached;
    boolean collided;
    boolean timeOut;
    private DNA winner;
    Player[] players;
    int noOfPlayers;
    int currentPlayer = 0;
    int maxTicks = 900;
    boolean forcedEnd = false;
    
    public BotServival(int plyrs) {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        noOfPlayers = plyrs;
        players = new Player[noOfPlayers];

        for(int i = 0; i < noOfPlayers; i++) {
            players[i] = new Player(map.start.x * GameConstants.tileSize, map.start.y * GameConstants.tileSize);
        }

    }

    public void setPlayerBrains(DNA[] dnas) {
        //if(dnas.length != players.length) return;
        for(int i = 0; i < players.length; i++){
            players[i].setBrain(dnas[i]);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        startGame();
    }

    private void startGame() {
        Thread gameThread = new Thread(this::trainingLoop);
        gameThread.start();
    }

    private void trainingLoop() {

        setPlayerBrains(Population.getPopulationDNAs());

        while(Population.currentGen <= AlotOfConstants.generations) {

            System.out.println("Current Gen: "+Population.currentGen);

            while(true) {

                boolean allDead = true;

                for(int i = 0; i < players.length; i++) {

                    if(players[i].isAlive()) {

                        update(i);
                        allDead = false;
                    }
                }

                if(Population.currentGen != 0 && Population.currentGen % 10 == 0) {
                    repaint();

                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(allDead) {
                    try {
                        Population.evolve();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
            
                    map.generateMap();
                    for(int i = 0; i < noOfPlayers; i++) {
                        players[i] = new Player(map.start.x * GameConstants.tileSize, map.start.y * GameConstants.tileSize);
                    }
                    setPlayerBrains(Population.getPopulationDNAs());
                    break;
                }
                
            }           
        }     

        winner = Population.getMaxFitnessDNA();

        for (int i = 0; i < players.length; i++) {
            if(players[i].brain.getMyDNA() == winner) {
                currentPlayer = i;
                break;
            }
        }

        winnerLoop();

    }

    public void winnerLoop() {

        while(!forcedEnd) {

            while (players[currentPlayer].isAlive()) {

                update(currentPlayer);
                repaint();

                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            map.generateMap();
            players[currentPlayer] = new Player(map.start.x * GameConstants.tileSize, map.start.y * GameConstants.tileSize);
            players[currentPlayer].setBrain(winner);
            
        }
    }

    private double getFitness(boolean goal, boolean collision, boolean timeout, int timeTaken, int ind) {

        double baseFitness = 1000;
        double collisionPenalty = 1000;
        double progressReward = 200;

        if(goal) { 
            double timeFactor = 1 - (timeTaken / (double)maxTicks);
            return baseFitness + (timeFactor * 1000) + 2000;
        }

        else if (collision) {

            double initialDistanceToGaol = getDistance(new Point(map.start.x * GameConstants.tileSize, map.start.y * GameConstants.tileSize), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));
            double currentDistToGoal = getDistance(new Point((int)players[ind].pos.getX(), (int)players[ind].pos.getY()), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));

            double progressFactor = 0;
            if(initialDistanceToGaol - currentDistToGoal > 0) {
                progressFactor = (initialDistanceToGaol - currentDistToGoal) / initialDistanceToGaol;
            }

            return baseFitness + ((progressFactor * progressReward) + (timeTaken / maxTicks) * 100) - collisionPenalty;

        }

        else {
            double initialDistanceToGaol = getDistance(new Point(map.start.x * GameConstants.tileSize, map.start.y * GameConstants.tileSize), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));
            double currentDistToGoal = getDistance(new Point((int)players[ind].pos.getX(), (int)players[ind].pos.getY()), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));

            double progressFactor = 0;
            if(initialDistanceToGaol - currentDistToGoal > 0) {
                progressFactor = (initialDistanceToGaol - currentDistToGoal) / initialDistanceToGaol;
            }

            if(progressFactor < 0.1) {
                return baseFitness - (1 - progressFactor) * 1000;
            }

            else {
                return baseFitness + (progressFactor * progressReward);
            }

        }

    }

    public double getDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    public void getBrainSignal(int ind) {

        ArrayList<Double> inps = new ArrayList<>();

        for(int i = 0; i < players[ind].whatTracersSee[0].length; i++) {
            inps.add(players[ind].whatTracersSee[0][i]);
            inps.add(players[ind].whatTracersSee[1][i]);
        }

        double[] temp = getDirectionToGoal(new Point((int)players[ind].pos.getX(), (int)players[ind].pos.getY()), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));

        inps.add(temp[0]);
        inps.add(temp[1]);
        inps.add(players[ind].pos.distance(Vector2D.of(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize)));

        double[] inputs = inps.stream().mapToDouble(Double::doubleValue).toArray();

        // for(double d : inputs) {
        //     System.out.print(d+" ");
        // }

        // double[] inputs = new double[players[currentPlayer].whatTracersSee[0].length * players[currentPlayer].whatTracersSee.length];

        // System.arraycopy(players[currentPlayer].whatTracersSee[0], 0, inputs,0, players[currentPlayer].whatTracersSee[0].length);
        // System.arraycopy(players[currentPlayer].whatTracersSee[1], 0, inputs,players[currentPlayer].whatTracersSee[0].length, players[currentPlayer].whatTracersSee[1].length);

        players[ind].brain.setInputs(inputs);

        double[] outputs = players[ind].brain.getOutputs();

        if(isMax(outputs, outputs[0])) players[ind].steerLeft();
        else if(isMax(outputs, outputs[2])) players[ind].steerRight();
        //else do nothing

    }

    public static double[] getDirectionToGoal(Point player, Point goal) {
        // Calculate direction vector
        double dx = goal.x - player.x;
        double dy = goal.y - player.y;
        
        // Calculate magnitude of the vector
        double magnitude = Math.sqrt(dx*dx + dy*dy);
        
        // Normalize the vector
        // If magnitude is 0, return (0, 0) to avoid division by zero
        if (magnitude == 0) {
            return new double[]{0, 0};
        }
        
        double normalizedX = dx / magnitude;
        double normalizedY = dy / magnitude;
        
        // Return normalized direction
        return new double[]{normalizedX, normalizedY};
    }

    private boolean isMax(double[] arr, double v) {
        for(double a : arr){
            if(v < a) return false;
        }
        return true;
    }

    private void update(int ind) {
        
        players[ind].update();

        players[ind].ticks++;

        if(players[ind].ticks >= maxTicks) {
            players[ind].timeout = true;
            players[ind].brain.setFitness(getFitness(false, false, true, maxTicks, ind));
        }

        for(Tile t : map.edgeTiles) {
            if(players[ind].isColliding(t.pos.x, t.pos.y, GameConstants.tileSize)) {
                players[ind].collided = true;
                players[ind].brain.setFitness(getFitness(false, true, false, players[ind].ticks, ind));
            }
        }

        if(players[ind].isColliding(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize, GameConstants.goalArea * GameConstants.tileSize)){
            players[ind].goalReached = true;
            players[ind].brain.setFitness(getFitness(true, false, false, players[ind].ticks, ind));
        }

        for(int i = 0; i < players[ind].tracers.length; i++) {

            ArrayList<double[]> stuffImSeing = new ArrayList<>();

            if(players[ind].touchesTile(players[ind].pos, players[ind].tracers[i], map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize, GameConstants.goalArea * GameConstants.tileSize)) {
                double[] objectNDist = new double[2];
                objectNDist[0] = 100;
                objectNDist[1] = players[ind].pos.distance(Vector2D.of(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));
                stuffImSeing.add(objectNDist);
            }

            for(Tile t : map.edgeTiles) {

                if(players[ind].touchesTile(players[ind].pos, players[ind].tracers[i], t.pos.x, t.pos.y, GameConstants.tileSize)) {
                    double[] objectNDist = new double[2];
                    objectNDist[0] = -100;
                    objectNDist[1] = players[ind].pos.distance(Vector2D.of(t.pos.x, t.pos.y));
                    stuffImSeing.add(objectNDist);
                }
            }
            
            double[] min = {0 , Double.MAX_VALUE};
            for(double[] obd : stuffImSeing) {
                if(obd[1] < min[1]) min = obd;
            }

            players[ind].whatTracersSee[0][i] = min[0];
            players[ind].whatTracersSee[1][i] = min[1];

        }

        getBrainSignal(ind);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
        // players[currentPlayer].draw((Graphics2D) g);
        // players[currentPlayer].drawVisionRange((Graphics2D) g);
        for(int i = 0; i < noOfPlayers; i++) {
            if(players[i].isAlive()) {
                players[i].draw((Graphics2D) g);
                //players[i].drawVisionRange((Graphics2D) g);
            }
        }
    }

    private void drawMap(Graphics g) {
        g.setColor(Color.WHITE);
        map.draw(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        int keyCode = e.getKeyCode();
        switch(keyCode) {
            // case KeyEvent.VK_W:
            //     player.steerUp();
            //     break;
            case KeyEvent.VK_A:
                forcedEnd = true;
                break;
            // // case KeyEvent.VK_S:
            // //     player.steerDown();
            // //     break;
            // case KeyEvent.VK_D:
            //     players[currentPlayer].steerRight();
            //     break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {

    Population population = new Population(AlotOfConstants.popSize, AlotOfConstants.inputs, AlotOfConstants.outputs);
    BotServival game = new BotServival(AlotOfConstants.popSize);
    //game.trainingLoop();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simple Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(game, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}

