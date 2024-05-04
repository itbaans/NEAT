import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import Game.MadeForNeat;
import Game.Player;
import NeuralNetwork.Connection;
import NeuralNetwork.DNA;
import NeuralNetwork.Node_N;
import NeuralNetwork.NueralNetwork;

public class Population {

    MadeForNeat[] games;
    static int populationSize;
    static DNA[] populationDNAs;
    static int no_of_inputs;
    static int no_of_outputs;
    static Random rand = new Random();
    static Hashtable<String, Integer> connections = new Hashtable<>();
    NueralNetwork tesNetwork;
    static int MAX_INNOV_ID;
    static int SPECIE_ID;
    static int DNA_ID;
    static Map<Integer, Specie> species = new HashMap<>();

    public Population(int size, int ins, int outs) {

        populationSize = size;
        games = new Player[populationSize];
        populationDNAs = new DNA[populationSize];
        no_of_inputs = ins;
        no_of_outputs = outs;

    }

    // //for testing a single dna
    // public Population() {

    //     populationSize = 1;
    //     populationDNAs = new DNA[populationSize];
    //     no_of_inputs = 3;
    //     no_of_outputs = 3;
    //     startOfTimes();
    //     tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
    //     tesNetwork.displayStructure();
    
    // }

    //testing multiple dnas
    public Population(int popSize) throws CloneNotSupportedException {

        populationSize = popSize;
        populationDNAs = new DNA[populationSize];
        no_of_inputs = 3;
        no_of_outputs = 3;

        startSimulation();

    }


    public void startSimulation() throws CloneNotSupportedException {

        startOfTimes();

        //1. loop to give dnas to players

        int count = 0;

        //2. games are played (for some amount of time)
        while (count < 10) {

            System.out.println("******");
            Speciation.printSpecies();
            System.out.println("******");

            for(int i = 0; i < populationSize; i++) {
                populationDNAs[i].setFitness(rand.nextDouble() * 50);
            }
    
            Speciation.updateSpeciesAvgFitness();
            //3. cross overs are donee 
            CrossOver.theKillingSpree();
            CrossOver.theReproduction();
    
            count++;

        }

        //4. mutations are done

        //5. repeat to step 1


    }

    

    public void testMutatingWieghts(DNA dna) {

        double[] mutatingPossibilities = {AlotOfConstants.probChangeWieght, AlotOfConstants.probNewConn, AlotOfConstants.probNewNode, AlotOfConstants.probRemoveConn, AlotOfConstants.probRemoveNode};

        int pick = Statistics.poolSelect(mutatingPossibilities);
    
        switch (pick) {
            case 0: //change of wieghts
                //System.out.println("WEIGHT CHANGED");
                Mutation.mutateWithWeightsAndBiases(dna);
                //tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                //tesNetwork.displayStructure();
                break;
                
            case 1: //new conn
                //System.out.println("NEW CONNECTION ADDED");
                Mutation.mutateWithConnection(dna);
                //tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                //System.out.println(populationDNAs[0].c_genes.size());               
                //System.out.println("NEW SIZE: "+populationDNAs[0].c_genes.size());
                //tesNetwork.displayStructure();
                break;
    
            case 2: //new node
                //System.out.println("NEW NODE");
                Mutation.mutateWithNewNode(dna);
                //tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                //System.out.println(populationDNAs[0].n_genes.size());
                //tesNetwork.displayStructure();
                break;
    
            case 3: //remove conn
                //System.out.println("REMOVE CONN");
                Mutation.mutateWithRemoveConnection(dna);
                //tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);                
                //System.out.println("NEW SIZE: "+populationDNAs[0].c_genes.size());
                //tesNetwork.displayStructure();
                break;
    
            case 4: //remove node
                //System.out.println("REMOVE NODE");
                Mutation.mutateWithRemoveNode(dna);
                //tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);                  
                //tesNetwork.displayStructure();
            }

    }

    

    static void clearNodeOuts(LinkedList<Node_N> list) {

        for(int i = 0; i < list.size(); i++) {
            list.get(i).clearOuts();
        }
    }


   


    static boolean isExcessConnection(DNA dna, int in, int out) {

        if(in > dna.getMaxNodeID() || out > dna.getMaxNodeID()) return true;

        boolean check1 = true;
        boolean check2 = true;

        for (Node_N n : dna.n_genes) {

            if(in == n.getNode_id()) check1 = false;
            if(out == n.getNode_id()) check2 = false;

            if(!check1 && !check2) return false;

        }

        return true;

    }

    static boolean isDisjointConnection(DNA dna, int in, int out) {

        for (Connection t : dna.c_genes) {

            if(in == t.getIn_id() && out == t.getOut_id()) return false;

        }

        return !isExcessConnection(dna, in, out);

    }


    static boolean checkIfLoopExist(LinkedList<Connection> conns, int in, int out) {

        Graph graph = new Graph();

        graph.addConnection(in, out);

        for (Connection c : conns) {

            graph.addConnection(c.getIn_id(), c.getOut_id());

        }

        return graph.hasLoop();

    }


    private void initializeAllPossibleConnections() {

        for (int i = 1; i <= no_of_inputs; i++) {

            //input connections to all possible nodes except other input nodes
            for (int j = no_of_inputs + 1; j <= no_of_inputs + no_of_outputs; j++) {

                MAX_INNOV_ID++;
                String con = i+","+j;
                connections.put(con, MAX_INNOV_ID);

            }

        }

    }


    static int getMaxGeneID() {

        int maxId = 1;

        for (DNA d : populationDNAs) {

            if(d.getMaxNodeID() > maxId) maxId = d.getMaxNodeID();

        }

        return maxId;
        
    }

    //initializing fully connected genes for first generation
    private void startOfTimes() {

        initializeAllPossibleConnections();

        for (int p = 0; p < populationSize; p++) {

            LinkedList<Node_N> n_genes = new LinkedList<>();
            LinkedList<Connection> c_genes = new LinkedList<>();

            for (int i = 0; i < no_of_inputs; i++) {
                n_genes.add(new Node_N(i + 1, false, true));      

            }

            for(int o = 0; o < no_of_outputs; o++) {
                n_genes.add(new Node_N((o+ 1) + no_of_inputs, true, false));
            }   

            Enumeration<String> keys = connections.keys();
            while(keys.hasMoreElements()) {
                String key = keys.nextElement();
                String[] split = key.split(",");
                c_genes.add(new Connection(Integer.parseInt(split[0]), Integer.parseInt(split[1]), rand.nextDouble() * 2 - 1, true, connections.get(key)));
                    
            }

            DNA_ID++;
            populationDNAs[p] = new DNA(n_genes, c_genes);
            populationDNAs[p].id = DNA_ID;
            // //temp
            // populationDNAs[p].fitness = rand.nextDouble() * 2 - 1;

        }

        Speciation.speciate();
    }

}



