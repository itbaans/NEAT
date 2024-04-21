package NeuralNetwork;
import java.util.Hashtable;

public  class Node_N {

    int node_id;
    boolean isHiddenInput;
    boolean isHiddenOutput;
    Hashtable<Node_N, Double> outs = new Hashtable<>();
    double input;
    double output;

    @Override
    public String toString() {
        return "Node_N [node_id=" + node_id + ", isHiddenInput=" + isHiddenInput + ", isHiddenOutput=" + isHiddenOutput
                + "]";
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

    public Node_N(int id, boolean h_i, boolean h_o) {

        node_id = id;
        isHiddenInput = h_i;
        isHiddenOutput = h_o;

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

        return 1 / (1 + Math.exp(-x));

    }


}