package TheGame;

import javax.swing.*;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

import NEAT_STUFF.AlotOfConstants;
import NEAT_STUFF.Population;

import java.time.Instant;
import java.util.ArrayList;
//import java.util.Collections;
import java.time.Duration;
//import java.util.Arrays;

import NeuralNetwork.*;

import java.awt.*;
import java.awt.event.*;

public class BotServival extends JPanel implements KeyListener {
    
    private static final int WIDTH = GameConstants.width * GameConstants.tileSize;
    private static final int HEIGHT = GameConstants.height * GameConstants.tileSize;
    private static final int DELAY = 2; // milliseconds
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
    public static int currentGen = 0;

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

        while(currentGen <= GameConstants.generations) {

            System.out.println("Current Gen: "+currentGen);

            int ticks = 0;   

            while (currentPlayer < noOfPlayers) {

                update();

                if(currentGen % 10 == 0 && currentGen > 0) {
                    repaint();

                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ticks++;
                
            
                if (ticks >= maxTicks) {
                    //System.out.println("Maximum time reached. Ending game loop.");
                    timeOut = true;
                    ticks = 0;
                }

                if(goalReached || timeOut || collided) {

                    players[currentPlayer].brain.setFitness(getFitness(goalReached, collided, timeOut, ticks));

                    currentPlayer++;
                    goalReached = false;
                    timeOut = false;
                    collided = false;
                    map.resetVisited();

                }
                // break;

            }

            // break;

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
            currentGen++;
            currentPlayer = 0;
            
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

            int ticks = 0;   

            while (!goalReached && !timeOut && !collided) {

                update();
                repaint();

                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ticks++;
                
            
                if (ticks >= maxTicks) {
                    //System.out.println("Maximum time reached. Ending game loop.");
                    timeOut = true;
                    ticks = 0;
                }

                if(goalReached || timeOut || collided) {

                    goalReached = false;
                    timeOut = false;
                    collided = false;
                    map.resetVisited();
                    map.generateMap();
                    players[currentPlayer] = new Player(map.start.x * GameConstants.tileSize, map.start.y * GameConstants.tileSize);
                    players[currentPlayer].setBrain(winner);

                }
                // break;

            }
            
        }
    }

    private double getFitness(boolean goal, boolean collision, boolean timeout, int timeTaken) {

        double baseFitness = 1000;
        double collisionPenalty = 1000;
        double revistedPenalty = Math.exp(players[currentPlayer].reVisitedTiles);
        double revistiWeight = 0.003;
        double progressReward = 200;

        revistedPenalty = Math.min(Math.exp(players[currentPlayer].reVisitedTiles) * revistiWeight, 2000);

        if(goal) { 
        
            double timeFactor = 1 - (timeTaken / maxTicks);
            return baseFitness + (timeFactor * 1000) + 2000 ;
        }

        else if (collision) {

            double initialDistanceToGaol = getDistance(new Point(map.start.x * GameConstants.tileSize, map.start.y * GameConstants.tileSize), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));
            double currentDistToGoal = getDistance(new Point((int)players[currentPlayer].pos.getX(), (int)players[currentPlayer].pos.getY()), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));

            double progressFactor = 0;
            if(initialDistanceToGaol - currentDistToGoal > 0) {
                progressFactor = (initialDistanceToGaol - currentDistToGoal) / initialDistanceToGaol;
            }

            return Math.max((baseFitness + (progressFactor * progressReward) + (timeTaken / maxTicks) * 100) - collisionPenalty, 10);

        }

        else {
            double initialDistanceToGaol = getDistance(new Point(map.start.x * GameConstants.tileSize, map.start.y * GameConstants.tileSize), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));
            double currentDistToGoal = getDistance(new Point((int)players[currentPlayer].pos.getX(), (int)players[currentPlayer].pos.getY()), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));

            double progressFactor = 0;
            if(initialDistanceToGaol - currentDistToGoal > 0) {
                progressFactor = (initialDistanceToGaol - currentDistToGoal) / initialDistanceToGaol;
            }

            return Math.max((baseFitness + (progressFactor * progressReward)) - (revistedPenalty), 10);

        }


    }

    public double getDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    public void getBrainSignal() {

        ArrayList<Double> inps = new ArrayList<>();

        for(int i = 0; i < players[currentPlayer].whatTracersSee[0].length; i++) {
            inps.add(players[currentPlayer].whatTracersSee[0][i]);
            inps.add(players[currentPlayer].whatTracersSee[1][i]);
        }

        double[] temp = getDirectionToGoal(new Point((int)players[currentPlayer].pos.getX(), (int)players[currentPlayer].pos.getY()), new Point(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));

        inps.add(temp[0]);
        inps.add(temp[1]);
        inps.add(players[currentPlayer].pos.distance(Vector2D.of(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize)));

        double[] inputs = inps.stream().mapToDouble(Double::doubleValue).toArray();

        // for(double d : inputs) {
        //     System.out.print(d+" ");
        // }

        // double[] inputs = new double[players[currentPlayer].whatTracersSee[0].length * players[currentPlayer].whatTracersSee.length];

        // System.arraycopy(players[currentPlayer].whatTracersSee[0], 0, inputs,0, players[currentPlayer].whatTracersSee[0].length);
        // System.arraycopy(players[currentPlayer].whatTracersSee[1], 0, inputs,players[currentPlayer].whatTracersSee[0].length, players[currentPlayer].whatTracersSee[1].length);

        players[currentPlayer].brain.setInputs(inputs);

        double[] outputs = players[currentPlayer].brain.getOutputs();

        if(isMax(outputs, outputs[0])) players[currentPlayer].steerLeft();
        else if(isMax(outputs, outputs[2])) players[currentPlayer].steerRight();
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

    private void update() {
        
        players[currentPlayer].update();

        // for(Tile[] tls : map.tiles) {
        //     for(Tile t : tls) {
        //         if(player.inVision(t.getPos().x, t.getPos().y) && t.isCollision()) System.out.println("BLACK");
        //     }
        // }

        for(Tile[] tls : map.tiles) {
            for(Tile t : tls) {
                if(t.isCollision()) {
                    if(players[currentPlayer].isColliding(t.pos.x, t.pos.y, GameConstants.tileSize)) {
                        collided = true;
                        //System.out.println("I collided");
                    }
                }
                else if(!t.isCollision()) {
                    if(players[currentPlayer].isColliding(t.pos.x, t.pos.y, GameConstants.tileSize) && !t.isVisited) {
                        t.isVisited = true;
                    }
                    if(players[currentPlayer].isColliding(t.pos.x, t.pos.y, GameConstants.tileSize) && t.isVisited) {
                        players[currentPlayer].reVisitedTiles++;
                    }
                    if(players[currentPlayer].isColliding(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize, GameConstants.goalArea * GameConstants.tileSize)){
                        goalReached = true;
                    }
                }
            }
        }

        for(int i = 0; i < players[currentPlayer].tracers.length; i++) {

            ArrayList<double[]> stuffImSeing = new ArrayList<>();

            if(players[currentPlayer].touchesTile(players[currentPlayer].pos, players[currentPlayer].tracers[i], map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize, GameConstants.goalArea * GameConstants.tileSize)) {
                double[] objectNDist = new double[2];
                objectNDist[0] = 100;
                objectNDist[1] = players[currentPlayer].pos.distance(Vector2D.of(map.goal.x * GameConstants.tileSize, map.goal.y * GameConstants.tileSize));
                stuffImSeing.add(objectNDist);
            }

            for(Tile[] tls : map.tiles) {
                for(Tile t : tls) {
                    if(t.isCollision()) {
                        if(players[currentPlayer].touchesTile(players[currentPlayer].pos, players[currentPlayer].tracers[i], t.pos.x, t.pos.y, GameConstants.tileSize)) {
                            double[] objectNDist = new double[2];
                            objectNDist[0] = -100;
                            objectNDist[1] = players[currentPlayer].pos.distance(Vector2D.of(t.pos.x, t.pos.y));
                            stuffImSeing.add(objectNDist);
                        }
                    }
                }
            }
            
            double[] min = {0 , Double.MAX_VALUE};
            for(double[] obd : stuffImSeing) {
                if(obd[1] < min[1]) min = obd;
            }

            players[currentPlayer].whatTracersSee[0][i] = min[0];
            players[currentPlayer].whatTracersSee[1][i] = min[1];

        }

        getBrainSignal();
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
        players[currentPlayer].draw((Graphics2D) g);
        players[currentPlayer].drawVisionRange((Graphics2D) g);
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

