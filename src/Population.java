import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import Game.BotServival;
import NeuralNetwork.Connection;
import java.util.Random;
import NeuralNetwork.Node_N;

public class Population {

    BotServival[] games;
    int populationSize;
    DNA[] populationDNAs;
    int no_of_inputs;
    int no_of_outputs;
    Random rand = new Random();
    ArrayList<int[]> connections = new ArrayList<>();


    public Population(int size, int ins, int outs) {

        populationSize = size;
        games = new BotServival[populationSize];
        populationDNAs = new DNA[populationSize];
        no_of_inputs = ins;
        no_of_outputs = outs;

    }


    public void startSimulation() {

        startOfTimes();

        //1. loop to give dnas to games

        //2. games are played

        //3. cross overs are donee 

        //4. mutations are done

        updateAllPossibleConnections();

        //5. repeat to step 1


    }

    private DNA mutateWithWeights(DNA dna) {


        for(Connection c : dna.c_genes) {

            


        }

    }

    private DNA mutateWithNewNode(DNA dna) {

        int new_id = dna.n_genes.size() + 1;

        int randInd = rand.nextInt(dna.c_genes.size());

        Connection randConn = dna.c_genes.get(randInd);

        Connection conn1 = new Connection(randConn.getIn_id(), new_id, rand.nextDouble() * 2 - 1, true, dna.c_genes.size() + 1);
        Connection conn2 = new Connection(new_id, randConn.getOut_id(), rand.nextDouble() * 2 - 1, true, dna.c_genes.size() + 2);
        randConn.setEnabled(false);

        dna.n_genes.add(new Node_N(new_id, true, true));
        dna.c_genes.add(conn1);
        dna.c_genes.add(conn2);

        return new DNA(dna.n_genes, dna.c_genes);

    }

    private DNA mutateWithConnection(DNA dna) {

        LinkedList<Connection> newConnGenes = dna.c_genes;

        Collections.shuffle(connections);

        int ranInd = rand.nextInt(connections.size());

        int in = connections.get(ranInd)[0];
        int out = connections.get(ranInd)[1];

        Connection newConnection = new Connection(in, out, rand.nextDouble() * 2 - 1, true, dna.c_genes.size() + 1);
        newConnGenes.add(newConnection);

        if(!isExcessConnection(dna.n_genes.size(), newConnection) && isDisjointConnection(dna.c_genes, newConnection, dna.n_genes.size()) && !checkIfLoopExist(newConnGenes)) {
           return new DNA(dna.n_genes, newConnGenes);
        }
        newConnGenes.remove(newConnection);
        return null;
    }

    private boolean isExcessConnection(int maxSize, Connection c) {

        return (c.getIn_id() > maxSize || c.getOut_id() > maxSize);

    }

    private boolean isDisjointConnection(LinkedList<Connection> conns, Connection c, int maxSize) {

        for (Connection t : conns) {

            if(c.getIn_id() == t.getIn_id() && c.getOut_id() == t.getOut_id()) return false;

        }

        return c.getIn_id() <= maxSize && c.getOut_id() <= maxSize;

    }


    private boolean checkIfLoopExist(LinkedList<Connection> conns) {

        Graph graph = new Graph();

        for (Connection c : conns) {

            graph.addConnection(c.getIn_id(), c.getOut_id());

        }

        return graph.hasLoop();

    }


    private void updateAllPossibleConnections() {

        connections.clear();

        ArrayList<Node_N> inputNodes = new ArrayList<>();
        ArrayList<Node_N> hiddenNodes = new ArrayList<>();
        ArrayList<Node_N> outputNodes = new ArrayList<>();

        LinkedList<Node_N> maxNodes = getMaxNodeGene();


        for (int i = 0; i < maxNodes.size(); i++) {

            if(maxNodes.get(i).isHiddenInput() && maxNodes.get(i).isHiddenOutput()) hiddenNodes.add(getMaxNodeGene().get(i));
            if(!maxNodes.get(i).isHiddenInput() && maxNodes.get(i).isHiddenOutput()) inputNodes.add(getMaxNodeGene().get(i));
            if(maxNodes.get(i).isHiddenInput() && !maxNodes.get(i).isHiddenOutput()) outputNodes.add(getMaxNodeGene().get(i));

        }

        if(inputNodes.size() != 0) {

            for (Node_N i_n : inputNodes) {

                if(hiddenNodes.size() != 0) {

                    for (Node_N h_n : hiddenNodes) {

                        int[] t = {i_n.getNode_id(), h_n.getNode_id()};
                        connections.add(t);

                    }

                }
                
                if(outputNodes.size() != 0) {

                    for (Node_N o_n : outputNodes) {

                        int[] t = {i_n.getNode_id(), o_n.getNode_id()};
                        connections.add(t);

                    }

                }

            }     

        }

        if(hiddenNodes.size() != 0) {

            for (Node_N h_n : hiddenNodes) {

                for (Node_N h_n2 : hiddenNodes) {

                    if(h_n.getNode_id() != h_n2.getNode_id()) {

                        int[] t = {h_n.getNode_id(), h_n2.getNode_id()};
                        connections.add(t);

                    }

                }
                
                if(outputNodes.size() != 0) {

                    for (Node_N o_n : outputNodes) {

                        int[] t = {h_n.getNode_id(), o_n.getNode_id()};
                        connections.add(t);

                    }

                }

            }     

        }

    }



    private LinkedList<Node_N> getMaxNodeGene() {

        LinkedList<Node_N> maxNGenes = new LinkedList<>();

        for (DNA d : populationDNAs) {

            if(d.n_genes.size() > maxNGenes.size()) maxNGenes = d.n_genes;

        }

        return maxNGenes;
        

    }

    private void startOfTimes() {

        for (int p = 0; p < populationSize; p++) {

            LinkedList<Node_N> n_genes = new LinkedList<>();
            LinkedList<Connection> c_genes = new LinkedList<>();

            int connID = 1;

            for (int i = 0; i < no_of_inputs; i++) {

                n_genes.add(new Node_N(i + 1, false, true));      

                for (int j = 0; j < no_of_outputs; j++) {

                    n_genes.add(new Node_N(j + no_of_inputs + 1, true, false));
                    c_genes.add(new Connection(i + 1, j + no_of_inputs + 1, rand.nextDouble() * 2 - 1, false, connID));
                    connID++;

                }

            }

            populationDNAs[p] = new DNA(n_genes, c_genes);


        }

        updateAllPossibleConnections();

    }


}

class DNA {

    LinkedList<Node_N> n_genes = new LinkedList<>();
    LinkedList<Connection> c_genes = new LinkedList<>();
    public DNA(LinkedList<Node_N> n_genes, LinkedList<Connection> c_genes) {
        this.n_genes = n_genes;
        this.c_genes = c_genes;
    }


}
