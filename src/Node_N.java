import java.util.Hashtable;

public  class Node_N {

    int node_id;
    boolean isHiddenInput;
    boolean isHiddenOutput;
    Hashtable<Node_N, Double> outs = new Hashtable<>();
    double input;
    double output;

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