package NeuralNetwork;
import java.util.Hashtable;
import java.util.Random;

//import org.apache.commons.math4.legacy.genetics.Population;

public  class Node_N implements Comparable<Node_N>, Cloneable {

    int node_id;
    boolean isHiddenInput;
    boolean isHiddenOutput;
    Hashtable<Node_N, Double> outs = new Hashtable<>();
    double input;
    double output;
    double bias;
 
    public Object clone() throws CloneNotSupportedException {
        Node_N clone = (Node_N) super.clone();
        clone.outs = (Hashtable<Node_N, Double>) outs.clone();
        return clone;
    }

    public Node_N(int id, boolean h_i, boolean h_o) {

        node_id = id;
        isHiddenInput = h_i;
        isHiddenOutput = h_o;
        // Random rand = new Random();
        // if(h_i) bias = rand.nextDouble() * 60 - 30;

    }

    public void resetNode() {
        input = 0;
        output = 0;
        clearOuts();
    }
    
    public void resetNodeVals(){
        input = 0;
        output = 0;
    }

    public double getConnectionWeight(Node_N node) {
        if(outs.get(node) != null) return outs.get(node);
        else return 0;
    }


    public void setConnection(Node_N outNode, double wieght) {

        outs.put(outNode, wieght);

    }

    public void activateNode() {
        output = activation(input);
    }

    public double activation(double x) {

        return 1 / (1 + Math.exp(-(x + bias)));

    }

    public void clearOuts() {
        outs.clear();
    }

    @Override
    public int compareTo(Node_N other) {
        return Integer.compare(this.node_id, other.node_id);
    }

    public int getNode_id() {
        return node_id;
    }

    public void setNode_id(int node_id) {
        this.node_id = node_id;
    }

    public boolean isHiddenInput() {
        return isHiddenInput;
    }

    public void setHiddenInput(boolean isHiddenInput) {
        this.isHiddenInput = isHiddenInput;
    }

    public boolean isHiddenOutput() {
        return isHiddenOutput;
    }

    public void setHiddenOutput(boolean isHiddenOutput) {
        this.isHiddenOutput = isHiddenOutput;
    }

    public Hashtable<Node_N, Double> getOuts() {
        return outs;
    }

    public void setOuts(Hashtable<Node_N, Double> outs) {
        this.outs = outs;
    }

    public double getInput() {
        return input;
    }

    public void setInput(double input) {
        this.input = input;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }


}