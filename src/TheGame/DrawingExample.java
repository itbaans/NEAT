package TheGame;

import javax.swing.*;
import java.awt.*;

public class DrawingExample extends JPanel {

    MapGenerator map;
    // Constructor to set up the panel
    public DrawingExample() {
        // Set preferred size for the panel
        this.setPreferredSize(new Dimension(800, 800));
        // map = new MapGenerator(80, 80, 7, 4, 0.52f);
        // map.generateMap();
    }

    // Override paintComponent to perform custom drawing
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass method

        map.draw(g);
    }

    // Main method to set up the frame and show the panel
    public static void main(String[] args) {
        JFrame frame = new JFrame("Swing Graphics Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DrawingExample drawingPanel = new DrawingExample();
        frame.add(drawingPanel);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }
}
