package NEAT_STUFF;
import javax.swing.*;

import NeuralNetwork.Node_N;
import NeuralNetwork.NueralNetwork;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class GraphicalObjectsDisplay extends JFrame {

    // Population tesPopulation = new Population();
    // LinkedList<LinkedList<Node_N>> layers = tesPopulation.tesNetwork.getLayers();

    // public GraphicalObjectsDisplay() {
    //     // Create a panel to display graphical objects
    //     JPanel displayPanel = new JPanel() {
    //         @Override
    //         protected void paintComponent(Graphics g) {
    //             super.paintComponent(g);
    //             // Add your custom painting code here
    //             //g.drawRect(50, 50, 100, 100); // Example: drawing a rectangle
    //             drawNetwork(g);
    //         }

    //         protected void drawNetwork(Graphics g) {

    //             int layerWidth = getWidth() / layers.size();
    //             int nodeRadius = 20;
            
    //             for (int i = 0; i < layers.size(); i++) {
    //                 int x = i * layerWidth + layerWidth / 2;
    //                 LinkedList<Node_N> currentLayer = layers.get(i);
    //                 int yIncrement = getHeight() / (currentLayer.size() + 1);
            
    //                 for (int j = 0; j < currentLayer.size(); j++) {
    //                     int y = (j + 1) * yIncrement;
            
    //                     // Define different colors for different layers
    //                     if (i == 0) {
    //                         g.setColor(Color.RED); // Input layer color
    //                     } else if (i == layers.size() - 1) {
    //                         g.setColor(Color.GREEN); // Output layer color
    //                     } else {
    //                         g.setColor(Color.BLUE); // Hidden layer color
    //                     }
            
    //                     g.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
            
    //                     // Draw connections
    //                     if (i < layers.size() - 1) {
    //                         int t = i + 1;
    //                         while (t < layers.size()) {
    //                             int nextTX = t * layerWidth + layerWidth / 2;
    //                             int ytNextIncrement = getHeight() / (layers.get(t).size() + 1);
    //                             for (int l = 0; l < layers.get(t).size(); l++) {
    //                                 int tyNext = (l + 1) * ytNextIncrement;
    //                                 double weight = currentLayer.get(j).getConnectionWeight(layers.get(t).get(l));
    //                                 if (weight != 0) {
    //                                     g.setColor(Color.BLACK);
    //                                     g.drawLine(x, y, nextTX, tyNext);
    //                                     g.drawString(String.format("%.2f", weight), (x + nextTX) / 2, (y + tyNext) / 2);
    //                                 }
    //                             }
    //                             t++;
    //                         }
    //                     }
    //                 }
    //             }



    //         }


    //     };

    //     // Create buttons
    //     JButton button1 = new JButton("MUTATE");
    //     JButton button2 = new JButton("get the connection list");

    //     // Add buttons and display panel to the frame
    //     add(displayPanel, BorderLayout.CENTER);
    //     add(button1, BorderLayout.NORTH);
    //     add(button2, BorderLayout.SOUTH);

    //      // Add action listeners to buttons
    //     button1.addActionListener(new ActionListener() {
    //         @Override
    //         public void actionPerformed(ActionEvent e) {
    //             // Add functionality for button 1 click here
    //             tesPopulation.testMutatingWieghts();
    //             layers = tesPopulation.tesNetwork.getLayers();
    //             repaint();
    //         }
    //     });

    //     button2.addActionListener(new ActionListener() {
    //         @Override
    //         public void actionPerformed(ActionEvent e) {
    //             // Add functionality for button 1 click here
    //             tesPopulation.printConnectionList();
    //         }
    //     });

    //     // Set frame properties
    //     setTitle("Graphical Objects Display");
    //     setSize(400, 300);
    //     setDefaultCloseOperation(EXIT_ON_CLOSE);
    //     setVisible(true);
    // }

    // public static void main(String[] args) {
    //     new GraphicalObjectsDisplay();
    // }
}