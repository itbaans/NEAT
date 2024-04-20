package Game;

import javax.swing.*;

import org.apache.commons.geometry.euclidean.twod.Vector2D;

import NeuralNetwork.NueralNetwork;

import java.awt.*;
import java.awt.event.*;

public class BotServival extends JPanel implements KeyListener {
    
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int MAP_ROWS = HEIGHT / 20;
    private static final int MAP_COLS = WIDTH / 20;
    private static final int DELAY = 10; // milliseconds
    private Map map = new Map(MAP_ROWS, MAP_COLS, 0.08, 0, 0, MAP_COLS - 1, MAP_ROWS - 1);
    Player player = new Player(25, 25);
    NueralNetwork myNetwork = new NueralNetwork(10, 2);
    

    public BotServival() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

    }

    @Override
    public void addNotify() {
        super.addNotify();
        startGame();
    }

    private void startGame() {
        Thread gameThread = new Thread(this::gameLoop);
        gameThread.start();
    }

    private void gameLoop() {
        while (true) {
            update();
            repaint();
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //break;
            
        }
    }

    private void update() {
        
        player.update();

        // for(Tile[] tls : map.tiles) {
        //     for(Tile t : tls) {
        //         if(player.inVision(t.getPos().x, t.getPos().y) && t.isCollision()) System.out.println("BLACK");
        //     }
        // }

        for(int i = 0; i < player.tracers.length; i++) {
            boolean igotit = false;
            for(Tile[] tls : map.tiles) {
                for(Tile t : tls) {
                    if(t.isCollision()) {
                        if(player.touchesTile(player.pos, player.tracers[i], t.pos.x, t.pos.y, 20)) {
                            player.whatTracersSee[0][i] = -1;
                            igotit = true;
                            break;
                        }
                    }
                }
                if(igotit) break;
            }

            if(!igotit)  player.whatTracersSee[0][i] = 0;                   
        }

        // myNetwork.setInputs(player.whatTracersSee[0]);
        // if(myNetwork.getOutputs()[0] > myNetwork.getOutputs()[1]) player.steerLeft();
        // else player.steerRight();

        for(double vals : player.whatTracersSee[0]) {
            System.out.println(vals);
        }
        System.out.println("--------------");
        //System.out.println(player.pos.getX() +" "+player.pos.getY());
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
        player.draw((Graphics2D) g);
        player.drawVisionRange((Graphics2D) g);
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
                player.steerLeft();
                break;
            // case KeyEvent.VK_S:
            //     player.steerDown();
            //     break;
            case KeyEvent.VK_D:
                player.steerRight();
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simple Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new BotServival(), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

