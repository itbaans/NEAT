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

        //5. repeat to step 1


    }

    public void mutateWithConnection(DNA dna) {

        Collections.shuffle(connections);






    }

    public void checkIfLoopExist(LinkedList<Connection> conns) {

        


    }


    public void updateAllPossibleConnections() {

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
