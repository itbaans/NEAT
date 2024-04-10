// import java.util.Collections;
// import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Enumeration;

public class NueralNetwork {
    
    LinkedList<LinkedList<Node_N>> layers = new LinkedList<>();
    LinkedList<Connection> connectionGenes = new LinkedList<>();
    LinkedList<Node_N> nodeGenes = new LinkedList<>();
    Random rand = new Random();

    public NueralNetwork(LinkedList<Node_N> nGenes, LinkedList<Connection> cGenes) {

        nodeGenes = nGenes;
        connectionGenes = cGenes;
        decodeGenses();

    }

    public void displayStructure() {
        for (int i = 0; i < layers.size(); i++) {
            System.out.println("Layer " + i + ":");
            LinkedList<Node_N> currentLayer = layers.get(i);
            for (Node_N node : currentLayer) {
                System.out.print("  Node " + node.node_id + ": Connected to ");
                Enumeration<Node_N> keys = node.outs.keys();
                while(keys.hasMoreElements()) {
                    Node_N key = keys.nextElement();
                    System.out.print("Node " + key.node_id + " (Weight: " + node.outs.get(key)+ "), ");
                    
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public void decodeGenses() {

        for(Connection c : connectionGenes) {

            for(Node_N n : nodeGenes) {

                if(c.in_id == n.node_id) {

                    for(Node_N n2 : nodeGenes) {

                        if(c.out_id == n2.node_id) {

                            if(c.isEnabled) n.setConnection(n2, c.wieght);

                        }

                    }

                }

            }

        }

        LinkedList<Node_N> ins = new LinkedList<>();
        LinkedList<Node_N> outs = new LinkedList<>();
        layers.add(ins);
        layers.add(outs);
        
        for(Node_N n : nodeGenes) {

            if(!n.isHiddenInput) ins.add(n);
            if(!n.isHiddenOutput) outs.add(n);

        }

        layering();


    }

    public void layering() {

        int newLayerInd = 1;

        for(int i = 0; i < layers.size() - 1; i++) {

            LinkedList<Node_N> tempList = new LinkedList<>();

            for (Node_N n : layers.get(i)) {

                Enumeration<Node_N> keys = n.outs.keys();
                while(keys.hasMoreElements()) {
                    Node_N key = keys.nextElement();
                    if(!tempList.contains(key)) tempList.add(key);
                }

            }

            LinkedList<Node_N> newLayer = new LinkedList<>();
            for (Node_N n : tempList) {

                if(n.isHiddenOutput) {

                    boolean allClear = true;
                    
                    for (Node_N n2 : tempList) {

                        if(n2.isHiddenOutput && n2 != n) {

                            if(n2.outs.contains(n)) {
                                allClear = false;
                                break;
                            }

                        }

                    }

                    if(allClear) {
                        newLayer.add(n);
                    }
                    
                }

            }

            if(newLayer.size() > 0) {
                layers.add(newLayerInd, newLayer);
                newLayerInd++;
            }

        }


    }

    public NueralNetwork(int no_of_inputs, int no_of_outputs) {

        LinkedList<Node_N> ins = new LinkedList<>();
        LinkedList<Node_N> outs = new LinkedList<>();

        for(int i = 0; i < no_of_inputs; i++) {
            ins.add(new Node_N(i + 1 , false, true));
        }      

        for(int i = 0; i < no_of_outputs; i++) {
            outs.add(new Node_N(i + 1 , true, false));
        }

        int count = 1;
        for (Node_N n : ins) {
            
            for (Node_N n2 : outs) {

                double wgt = rand.nextDouble() * 2 - 1;
                n.setConnection(n2, wgt);
                connectionGenes.add(new Connection(n.node_id, n2.node_id, wgt, true, count));
                count++;

            }

        }

        layers.add(ins);
        layers.add(outs);

    }

    public void getOutputs() {
    
        for(int i = 0; i < layers.size(); i++) {

            for(Node_N n : layers.get(i)) {

                if(i > 0) n.activateNode();
                if(i == 0) n.output = n.input;
    
            }

            if(i < layers.size() - 1) {

                for(Node_N n : layers.get(i)) {

                    Enumeration<Node_N> keys = n.outs.keys();
                    while(keys.hasMoreElements()) {
                        Node_N key = keys.nextElement();
                        key.input += n.output * n.outs.get(key);
                    }
    
                }

            }

            if(i == layers.size() - 1) {

                for(Node_N n : layers.get(i)) {

                    System.out.println(n.output);
        
                }

            }

        }

    }


}

// public void sortNodeList(LinkedList<Node_N> nodeList) {
    //     // Define custom comparator to sort the list
    //     Comparator<Node_N> comparator = new Comparator<Node_N>() {
    //         @Override
    //         public int compare(Node_N node1, Node_N node2) {
    //             // First, check if both nodes are hidden inputs or hidden outputs
    //             if ((node1.isHiddenInput && node1.isHiddenOutput) && (node2.isHiddenInput && node2.isHiddenOutput)) {
    //                 // If both are hidden inputs or hidden outputs, maintain their order
    //                 return 0;
    //             } else if (node1.isHiddenInput && !node2.isHiddenInput) {
    //                 // If only node1 is hidden input, it should come later in the list
    //                 return 1;
    //             } else if (!node1.isHiddenInput && node2.isHiddenInput) {
    //                 // If only node2 is hidden input, it should come later in the list
    //                 return -1;
    //             } else if (node1.isHiddenOutput && !node2.isHiddenOutput) {
    //                 // If only node1 is hidden output, it should come earlier in the list
    //                 return -1;
    //             } else if (!node1.isHiddenOutput && node2.isHiddenOutput) {
    //                 // If only node2 is hidden output, it should come earlier in the list
    //                 return 1;
    //             } else {
    //                 // If neither node is hidden input or hidden output, maintain their order
    //                 return 0;
    //             }
    //         }
    //     };

    //     // Sort the list using the custom comparator
    //     Collections.sort(nodeList, comparator);
    // }
