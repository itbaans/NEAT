package NEAT_STUFF;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import NeuralNetwork.Connection;
import NeuralNetwork.DNA;
import NeuralNetwork.Node_N;
import NeuralNetwork.NueralNetwork;

public class Population {

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
        //players = new Player[populationSize];
        populationDNAs = new DNA[populationSize];
        no_of_inputs = ins;
        no_of_outputs = outs;

        startOfTimes();

    }

    //testing multiple dnas
    public Population(int popSize) throws CloneNotSupportedException {

        populationSize = popSize;
        populationDNAs = new DNA[populationSize];
        no_of_inputs = 3;
        no_of_outputs = 3;

        startSimulation();

    }

    public void XORtest() throws CloneNotSupportedException {

        //NueralNetwork[] NNs = new NueralNetwork[populationSize];
        startOfTimes();

        int generations = 0;
        double[][] xor_inputs = {{0.0, 0.0}, {0.0, 1.0}, {1.0, 0.}, {1.0, 1.0}};
        double[][] xor_outputs = {{0}, {1}, {1}, {0}};

        while(generations < 300) {
            //              
            //eval
            for(int i = 0; i < populationDNAs.length; i++) {
    
                populationDNAs[i].fitness = 4;
                resetNodes(populationDNAs[i].n_genes);
                NueralNetwork nn = new NueralNetwork(populationDNAs[i]);
                
                for(int j = 0; j < xor_inputs.length; j++) {
                    
                    nn.setInputs(xor_inputs[j]);
                    double[] outs = nn.getOutputs();
                    double fitnes = Math.pow((outs[0] - xor_outputs[j][0]), 2);
                    populationDNAs[i].fitness -= fitnes;
                    
                }
            }

            //System.out.println("ff");
            Speciation.updateSpeciesAvgFitness();
            //Speciation.printSpecies();
            
            CrossOver.theKillingSpree();
            CrossOver.theReproduction();
            //printDNAfitnesses();
            printMaxFitnessDNA();
            System.out.println("generation: "+generations);
            
            generations++;

        }

        resetNodes(getMaxFitnessDNA().n_genes);
        NueralNetwork nn = new NueralNetwork(getMaxFitnessDNA());

        nn.setInputs(xor_inputs[0]);
        System.out.println("Input: "+xor_inputs[0][0]+", "+xor_inputs[0][1]);
        System.out.println("Output: "+nn.getOutputs()[0]);

        nn.setInputs(xor_inputs[1]);
        System.out.println("Input: "+xor_inputs[1][0]+", "+xor_inputs[1][1]);
        System.out.println("Output: "+nn.getOutputs()[0]);

        nn.setInputs(xor_inputs[2]);
        System.out.println("Input: "+xor_inputs[2][0]+", "+xor_inputs[2][1]);
        System.out.println("Output: "+nn.getOutputs()[0]);

        nn.setInputs(xor_inputs[3]);
        System.out.println("Input: "+xor_inputs[3][0]+", "+xor_inputs[3][1]);
        System.out.println("Output: "+nn.getOutputs()[0]);
    

    }

    public static void evolve() throws CloneNotSupportedException {
        Speciation.updateSpeciesAvgFitness();
        CrossOver.theKillingSpree();
        CrossOver.theReproduction();
        printMaxFitnessDNA();
    }

    public void printDNAfitnesses() {

        System.out.print("[");
        for(DNA dna : populationDNAs) {
            System.out.println("ID: "+dna.id);
            System.out.print(dna.getFitness()+" ,");
        }
        System.out.println("]");


    }

    public static void printMaxFitnessDNA() {

        double max = Double.MIN_VALUE;
        DNA d = null;
        for(DNA dna : populationDNAs) {
            if(dna.getFitness() > max) {
                max = dna.getFitness();
                d = dna;
            }
        }
        System.out.println("MAX FITNESS: "+max);
        d.printDNA_n();

    }

    public DNA getMaxFitnessDNA() {

        DNA d = null;
        double max = Double.MIN_VALUE;
        for(DNA dna : populationDNAs) {
            if(dna.getFitness() > max) {
                max = dna.getFitness();
                d = dna;
            }
        }
        return d;
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

                Mutation.mutateWithWeights(dna);
                break;
                
            case 1: //new conn
                //System.out.println("NEW CONNECTION ADDED");
                Mutation.mutateWithConnection(dna);

                break;
    
            case 2: //new node
                //System.out.println("NEW NODE");
                Mutation.mutateWithNewNode(dna);
                break;
    
            case 3: //remove conn
                //System.out.println("REMOVE CONN");
                Mutation.mutateWithRemoveConnection(dna);
                break;
    
            case 4: //remove node
                //System.out.println("REMOVE NODE");
                Mutation.mutateWithRemoveNode(dna);
            }

    }

    static void resetNodes(LinkedList<Node_N> list) {

        for(int i = 0; i < list.size(); i++) {
            list.get(i).resetNode();
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

        }

        Speciation.speciate();
    }

    public static int getPopulationSize() {
        return populationSize;
    }

    public static DNA[] getPopulationDNAs() {
        return populationDNAs;
    }

    public static int getNo_of_inputs() {
        return no_of_inputs;
    }

    public static int getNo_of_outputs() {
        return no_of_outputs;
    }

    public static Random getRand() {
        return rand;
    }

    public static Hashtable<String, Integer> getConnections() {
        return connections;
    }

    public NueralNetwork getTesNetwork() {
        return tesNetwork;
    }

    public static int getMAX_INNOV_ID() {
        return MAX_INNOV_ID;
    }

    public static int getSPECIE_ID() {
        return SPECIE_ID;
    }

    public static int getDNA_ID() {
        return DNA_ID;
    }

    public static Map<Integer, Specie> getSpecies() {
        return species;
    }

}



