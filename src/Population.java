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

    MadeForNeat[] players;
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

    }

    public Population(MadeForNeat[] players, int inputs, int outputs) {

        populationSize = players.length;
        this.players = players;
        no_of_inputs = inputs;
        no_of_outputs = outputs;
        populationDNAs = new DNA[populationSize];

        
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

        //XOR test
       
        // # 2-input XOR inputs and expected outputs.
        // xor_inputs = [(0.0, 0.0), (0.0, 1.0), (1.0, 0.0), (1.0, 1.0)]
        // xor_outputs = [(0.0,), (1.0,), (1.0,), (0.0,)]


        // def eval_genomes(genomes, config):
        // for genome_id, genome in genomes:
        //     genome.fitness = 4.0
        //     net = neat.nn.FeedForwardNetwork.create(genome, config)
        //     for xi, xo in zip(xor_inputs, xor_outputs):
        //         output = net.activate(xi)
        //         genome.fitness -= (output[0] - xo[0]) ** 2


        //NueralNetwork[] NNs = new NueralNetwork[populationSize];
        startOfTimes();

        // // for(int i = 0; i < NNs.length; i++) {
        //     NNs[i] = new NueralNetwork(populationDNAs[i]);
        // }

        int generations = 0;
        double[][] xor_inputs = {{0.0, 0.0}, {0.0, 1.0}, {1.0, 0.}, {1.0, 1.0}};
        double[][] xor_outputs = {{0}, {1}, {1}, {0}};

        // for(int i = 0; i < NNs.length; i++) {
    
        //     NNs[i].setFitness(4);
        //     for(int j = 0; j < xor_inputs.length; j++) {

        //         NNs[i].setInputs(xor_inputs[j]);
        //         double[] outs = NNs[i].getOutputs();
        //         //System.out.println("NN "+i+" outs: "+outs[0]+" input: "+j);
        //         double fitnes = Math.pow((outs[0] - xor_outputs[j][0]), 2);
        //         NNs[i].setFitness(NNs[i].getFitness() - fitnes);
        //         NNs[i].displayStructure();

        //     }
        // }
        
        double target = 3.999;
        //getMaxFitnessDNA().getFitness() < target ||
        while(generations < 300) {
            //              
            //eval
            for(int i = 0; i < populationDNAs.length; i++) {
    
                populationDNAs[i].fitness = 4;
                clearNodeOuts(populationDNAs[i].n_genes);
                NueralNetwork nn = new NueralNetwork(populationDNAs[i]);
                //populationDNAs[i].printDNA_n();
                
                for(int j = 0; j < xor_inputs.length; j++) {
                    
                    nn.setInputs(xor_inputs[j]);
                    double[] outs = nn.getOutputs();
                    //System.out.println("NN "+i+" outs: "+outs[0]+" input: "+j);
                    double fitnes = Math.pow((outs[0] - xor_outputs[j][0]), 2);
                    populationDNAs[i].fitness -= fitnes;
                    // if(j == 0){
                    //     System.out.println("ID: "+populationDNAs[i].id+" Input"+xor_inputs[j][0]+", "+xor_inputs[j][1]);
                    //     nn.displayStructure();
                    // }
    
                }
            }

            //System.out.println("ff");
            Speciation.updateSpeciesAvgFitness();
            //Speciation.printSpecies();
            
            CrossOver.theKillingSpree();
            CrossOver.theReproduction();

            // for(int i = 0; i < NNs.length; i++) {

            //     NNs[i].setMyDNA(populationDNAs[i]);
            //     NNs[i].decodeGenses();
                
            // }

            //printDNAfitnesses();
            printMaxFitnessDNA();
            // if(generations == 299) {
            //     for (DNA d : populationDNAs) {
            //         d.printDNA_n();
            //     }
            // }
            System.out.println("generation: "+generations);
            
            generations++;

        }
        //System.out.println("?");
        // double[][] xor_inputs = {{0.0, 0.0}, {0.0, 1.0}, {1.0, 0.}, {1.0, 1.0}};
        // double[][] xor_outputs = {{0}, {1}, {1}, {0}};

        clearNodeOuts(getMaxFitnessDNA().n_genes);
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

    public void printDNAfitnesses() {

        System.out.print("[");
        for(DNA dna : populationDNAs) {
            System.out.println("ID: "+dna.id);
            System.out.print(dna.getFitness()+" ,");
        }
        System.out.println("]");


    }

    public void printMaxFitnessDNA() {

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
                //System.out.println("WEIGHT CHANGED");
                Mutation.mutateWithWeights(dna);
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
            // //temp
            // populationDNAs[p].fitness = rand.nextDouble() * 2 - 1;

        }

        Speciation.speciate();
    }

}



