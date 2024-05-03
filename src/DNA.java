import java.util.LinkedList;

import NeuralNetwork.Connection;
import NeuralNetwork.Node_N;

public class DNA {

    int id;
    LinkedList<Node_N> n_genes = new LinkedList<>();
    LinkedList<Connection> c_genes = new LinkedList<>();
    public DNA(LinkedList<Node_N> n_genes, LinkedList<Connection> c_genes) {
        this.n_genes = n_genes;
        this.c_genes = c_genes;
    }

    public int getMaxNodeID() {

        int maxId = 1;

        for (int i = 0; i < n_genes.size(); i++) {

            if(n_genes.get(i).getNode_id() >= maxId) maxId = n_genes.get(i).getNode_id();

        }

        return maxId;

    }

    public int getMaxConnID() {

        int maxId = 1;

        for (int i = 0; i < c_genes.size(); i++) {

            if(c_genes.get(i).getInnov() >= maxId) maxId = c_genes.get(i).getInnov();

        }

        return maxId;

    }

    public Node_N getNode(int id) {
        for (Node_N n : n_genes) {
            if(n.getNode_id() == id) return n;
        }
        return null;
    }

    public Connection getConnection(int in, int out) {

        for (Connection c : c_genes) {

            if(c.getIn_id() == in && c.getOut_id() == out) {

                return c;

            }

        }

        return null;

    }

}
