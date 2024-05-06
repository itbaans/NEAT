package NeuralNetwork;
// import java.util.Collections;
// import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Enumeration;

public class NueralNetwork {
    
    LinkedList<LinkedList<Node_N>> layers = new LinkedList<>();
    public void setMyDNA(DNA myDNA) {
        this.myDNA = myDNA;
    }

    DNA myDNA;
    Random rand = new Random();


    public NueralNetwork(DNA dna) {

        myDNA = dna;
        decodeGenses();

    }

    public void displayStructure() {

        double[] outpts = getOutputs();

        for (int i = 0; i < layers.size(); i++) {
            System.out.println("Layer " + i + ":");
            LinkedList<Node_N> currentLayer = layers.get(i);

            if(i == 0) {

                for(int t = 0; t < layers.get(i).size(); t++) {

                    System.out.println(layers.get(i).get(t).node_id+" ---input---> "+layers.get(i).get(t).output);

                }

            }

            if(i < layers.size() - 1) {

                for (Node_N node : currentLayer) {

                    System.out.print("  Node " + node.node_id + ": Connected to ");
                        Enumeration<Node_N> keys = node.outs.keys();
                        while(keys.hasMoreElements()) {
                            Node_N key = keys.nextElement();
                            System.out.print("Node " + key.node_id + " (Weight: " + node.outs.get(key)+ "), ");
                            
                        }
                        System.out.println();            
                    }

            }

            if(i == layers.size() - 1) {              

                for (int t = 0; t < outpts.length; t++) {

                    System.out.println(layers.get(i).get(t).node_id +" ---output---> "+outpts[t]);
                }

            }
            System.out.println();
        }
    }

    public void decodeGenses() {

        //System.out.println("here");

        layers.clear();

        // System.out.println("DNA ID: "+myDNA.id);

        // for(Connection c : myDNA.c_genes) {

        //     System.out.print(" ["+c.getIn_id()+" ,"+c.getOut_id()+"], Innov: ("+c.getInnov()+") ");
                
        // }

        //System.out.println();
        //System.out.println("here clear");
        for(Connection c : myDNA.c_genes) {

            for(Node_N n : myDNA.n_genes) {

                if(c.in_id == n.node_id) {

                    for(Node_N n2 : myDNA.n_genes) {

                        if(c.out_id == n2.node_id) {

                            if(c.isEnabled) n.setConnection(n2, c.wieght);

                        }

                    }

                }

            }

        }

        //System.out.println("layering 1");

        LinkedList<Node_N> ins = new LinkedList<>();
        LinkedList<Node_N> outs = new LinkedList<>();
        layers.add(ins);
        layers.add(outs);
        
        for(Node_N n : myDNA.n_genes) {

            if(!n.isHiddenInput) ins.add(n);
            if(!n.isHiddenOutput) outs.add(n);

        }

        //System.out.println("layering 2");

        layering();

        //System.out.println("out");
    }

    public void layering() {

        int newLayerInd = 1;

        for(int i = 0; i < layers.size() - 1; i++) {
            //System.out.println("i: " + i );
            LinkedList<Node_N> tempList = new LinkedList<>();

            for (Node_N n : layers.get(i)) {

                Enumeration<Node_N> keys = n.outs.keys();
                while(keys.hasMoreElements()) {
                    Node_N key = keys.nextElement();
                    if(!tempList.contains(key)) tempList.add(key);
                }

            }
            //System.out.println("i: " + i );
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
                        removeIfAlreadyInPrevLayers(n);
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

    private void removeIfAlreadyInPrevLayers(Node_N node) {

        for (int i = 0; i < layers.size(); i++) {
            if(layers.get(i).contains(node)) {
                layers.get(i).remove(node);
                break;
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
                myDNA.c_genes.add(new Connection(n.node_id, n2.node_id, wgt, true, count));
                count++;

            }

        }

        layers.add(ins);
        layers.add(outs);

    }

    public void setInputs(double[] inputs) {

        if(inputs.length != myDNA.n_genes.size()) return;

        int i = 0;

        for (Node_N node : myDNA.n_genes) {

            node.setInput(inputs[i]);
            i++;

        }

    }

    public double[] getOutputs() {

        double[] outputs = new double[layers.getLast().size()];
    
        for(int i = 0; i < layers.size(); i++) {

            for(int t = 0; t < layers.get(i).size(); t++) {

                if(i > 0) layers.get(i).get(t).activateNode();
                if(i == 0) layers.get(i).get(t).output = layers.get(i).get(t).input; 

            }

            if(i < layers.size() - 1) {

                for(int t = 0; t < layers.get(i).size(); t++) {

                    Enumeration<Node_N> keys = layers.get(i).get(t).outs.keys();
                    while(keys.hasMoreElements()) {
                        Node_N key = keys.nextElement();
                        key.input += layers.get(i).get(t).output * layers.get(i).get(t).outs.get(key);
                    }
    
                }

            }

            if(i == layers.size() - 1) {

                for(int t = 0; t < layers.get(i).size(); t++) {

                    outputs[t] = layers.get(i).get(t).getOutput();

                }

            }

        }

        return outputs;

    }

    public double getFitness() {
        return myDNA.getFitness();
    }

    public void setFitness(double fitness) {
        myDNA.setFitness(fitness);
    }

    public LinkedList<LinkedList<Node_N>> getLayers() {
        return layers;
    }

    public void setLayers(LinkedList<LinkedList<Node_N>> layers) {
        this.layers = layers;
    }

    public DNA getMyDNA() {
        return myDNA;
    }



}
