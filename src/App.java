import java.util.LinkedList;
import java.util.Random;
import javax.swing.*;
import java.awt.*;

class NeuralNetworkPanel extends JPanel {
    private LinkedList<LinkedList<Node_N>> layers;

    public NeuralNetworkPanel(LinkedList<LinkedList<Node_N>> layers) {
        this.layers = layers;
        setPreferredSize(new Dimension(600, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        int layerWidth = getWidth() / layers.size();
        int nodeRadius = 20;
    
        for (int i = 0; i < layers.size(); i++) {
            int x = i * layerWidth + layerWidth / 2;
            LinkedList<Node_N> currentLayer = layers.get(i);
            int yIncrement = getHeight() / (currentLayer.size() + 1);
    
            for (int j = 0; j < currentLayer.size(); j++) {
                int y = (j + 1) * yIncrement;
    
                // Define different colors for different layers
                if (i == 0) {
                    g.setColor(Color.RED); // Input layer color
                } else if (i == layers.size() - 1) {
                    g.setColor(Color.GREEN); // Output layer color
                } else {
                    g.setColor(Color.BLUE); // Hidden layer color
                }
    
                g.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
    
                // Draw connections
                if (i < layers.size() - 1) {
                    int t = i + 1;
                    while (t < layers.size()) {
                        int nextTX = t * layerWidth + layerWidth / 2;
                        int ytNextIncrement = getHeight() / (layers.get(t).size() + 1);
                        for (int l = 0; l < layers.get(t).size(); l++) {
                            int tyNext = (l + 1) * ytNextIncrement;
                            double weight = currentLayer.get(j).getConnectionWeight(layers.get(t).get(l));
                            if (weight != 0) {
                                g.setColor(Color.BLACK);
                                g.drawLine(x, y, nextTX, tyNext);
                                g.drawString(String.format("%.2f", weight), (x + nextTX) / 2, (y + tyNext) / 2);
                            }
                        }
                        t++;
                    }
                }
            }
        }
    }
}


public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        Random rand = new Random();

        Node_N n1 = new Node_N(1, false, true);
        Node_N n2 = new Node_N(2, false, true);
        n1.input = 3;
        n2.input = 4;

        Node_N n3 = new Node_N(3, true, false);
        Node_N n4 = new Node_N(4, true, false);
        Node_N n5 = new Node_N(5, true, false);
        Node_N n6 = new Node_N(6, true, true);
        Node_N n7 = new Node_N(7, true, true);

        Connection c1 = new Connection(1, 3, rand.nextDouble() * 2 - 1, true, 1);
        Connection c2 = new Connection(1, 4, rand.nextDouble() * 2 - 1, true, 2);
        Connection c3 = new Connection(2, 5, rand.nextDouble() * 2 - 1, true, 3);
        Connection c4 = new Connection(2, 6, rand.nextDouble() * 2 - 1, true, 4);
        Connection c5 = new Connection(6, 4, rand.nextDouble() * 2 - 1, true, 5);
        Connection c6 = new Connection(6, 7, rand.nextDouble() * 2 - 1, true, 2);
        Connection c7 = new Connection(7, 5, rand.nextDouble() * 2 - 1, true, 2);

        LinkedList<Node_N> n_genes = new LinkedList<>();
        n_genes.add(n1);
        n_genes.add(n2);
        n_genes.add(n3);
        n_genes.add(n4);
        n_genes.add(n5);
        n_genes.add(n6);
        n_genes.add(n7);

        LinkedList<Connection> c_genes = new LinkedList<>();
        c_genes.add(c1);
        c_genes.add(c2);
        c_genes.add(c3);
        c_genes.add(c4);
        c_genes.add(c5);
        c_genes.add(c6);
        c_genes.add(c7);

        NueralNetwork network = new NueralNetwork(n_genes, c_genes);
        network.getOutputs();
        System.out.println(network.layers.size());
        network.displayStructure();

        JFrame frame = new JFrame("Neural Network Structure");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        NeuralNetworkPanel panel = new NeuralNetworkPanel(network.layers);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
