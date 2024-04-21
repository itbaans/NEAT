import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import Game.BotServival;
import NeuralNetwork.Connection;
import java.util.Random;
import NeuralNetwork.Node_N;
import NeuralNetwork.NueralNetwork;

public class Population {

    BotServival[] games;
    int populationSize;
    DNA[] populationDNAs;
    int no_of_inputs;
    int no_of_outputs;
    Random rand = new Random();
    ArrayList<int[]> connections = new ArrayList<>();
    NueralNetwork tesNetwork;
    private int MAX_INNOV_ID;

    public Population(int size, int ins, int outs) {

        populationSize = size;
        games = new BotServival[populationSize];
        populationDNAs = new DNA[populationSize];
        no_of_inputs = ins;
        no_of_outputs = outs;

    }

    public Population() {

        //for testing

        populationSize = 1;
        populationDNAs = new DNA[populationSize];
        no_of_inputs = 3;
        no_of_outputs = 3;
        startOfTimes();
        tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
        tesNetwork.displayStructure();
    
    }


    public void startSimulation() {

        startOfTimes();

        //1. loop to give dnas to games

        //2. games are played

        //3. cross overs are donee 

        //4. mutations are done

        //5. repeat to step 1


    }

    

    public void testMutatingWieghts() {

        double[] mutatingPossibilities = {AlotOfConstants.probChangeWieght, AlotOfConstants.probNewConn, AlotOfConstants.probNewNode, AlotOfConstants.probRemoveConn, AlotOfConstants.probRemoveNode};

        int pick = Statistics.poolSelect(mutatingPossibilities);
    
        switch (pick) {
            case 0: //change of wieghts
                mutateWithWeights(populationDNAs[0]);
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                System.out.println("WEIGHT CHANGED");
                tesNetwork.displayStructure();
                break;
                
            case 1: //new conn
                mutateWithConnection(populationDNAs[0]);
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                //System.out.println(populationDNAs[0].c_genes.size());
                System.out.println("NEW CONNECTION ADDED");
                System.out.println("NEW SIZE: "+populationDNAs[0].c_genes.size());
                tesNetwork.displayStructure();
                break;
    
            case 2: //new node
                mutateWithNewNode(populationDNAs[0]);
                updateConnectionsList(pick, pick); //this is only done for testing for now
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                //System.out.println(populationDNAs[0].n_genes.size());
                System.out.println("NEW NODE");
                tesNetwork.displayStructure();
                break;
    
            case 3: //remove conn
                mutateWithRemoveConnection(populationDNAs[0]);
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                System.out.println("REMOVE CONN");
                System.out.println("NEW SIZE: "+populationDNAs[0].c_genes.size());
                tesNetwork.displayStructure();
                break;
    
            case 4: //remove node
                mutateWithRemoveNode(populationDNAs[0]);
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                System.out.println("REMOVE NODE");
                tesNetwork.displayStructure();
            }

    }

    private void printGenes() {

        for (DNA dna : populationDNAs) {
            System.out.println(dna.n_genes.toString());
            System.out.println("------------------------------");
            System.out.println(dna.c_genes.toString());
            System.out.println();
        }

    }

    private void mutateWithWeights(DNA dna) {

        int randomNumber = rand.nextInt(3) + 1;

        //shuffling because i want to pick first n random connections.
        Collections.shuffle(dna.c_genes);
        double[] percentages = {5, 15, 25, 50, 75};

        //here higher percentages have more chance to be picked
        double percentage = Statistics.theCollectionPikcer(percentages, false);

        //ranging it further
        percentage = rand.nextDouble() * ((percentage + 25) - percentage) + percentage;

        int n = (int)Math.ceil(dna.c_genes.size() * (percentage / 100));

        n = (n == 0) ? 1 : n;

        switch(randomNumber) {

            case 1 : //add/subract small value

                for (int i = 0; i < n; i++) {

                    if(AlotOfConstants.isUNIFORM) {
                        //max min
                        double change = rand.nextDouble() * (AlotOfConstants.MAX_SCALING - (AlotOfConstants.MIN_SCALING)) + (AlotOfConstants.MIN_SCALING);
                        change = rand.nextDouble() < 0.5 ? -change : change;
                        double value = dna.c_genes.get(i).getWieght() + change;
                        dna.c_genes.get(i).setWieght(clamp(value, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE));
                    }
                    else {
                        //mean std dev
                        double change = rand.nextGaussian() * AlotOfConstants.GUASIAN_STD + AlotOfConstants.GUASIAN_MEAN;
                        change = rand.nextDouble() < 0.5 ? -change : change;
                        double value = dna.c_genes.get(i).getWieght() + change;
                        dna.c_genes.get(i).setWieght(clamp(value, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE));
                    }

                }

                break;

            
            case 2 : //scaling by small value

                for (int i = 0; i < n; i++) {

                    double scale = rand.nextDouble() * (AlotOfConstants.MAX_SCALING - (AlotOfConstants.MIN_SCALING)) + (AlotOfConstants.MIN_SCALING);
                    dna.c_genes.get(i).setWieght(dna.c_genes.get(i).getWieght() * scale);

                }

                break;

            case 3 : //reset weights

                //here lower percentages have more chance to be picked since i dont want to reset alot of wieghts
                percentage = Statistics.theCollectionPikcer(percentages, true);
                percentage = rand.nextDouble() * ((percentage + 5) - percentage) + percentage;
                n = (int)Math.ceil(dna.c_genes.size() * (percentage / 100));
                n = (n == 0) ? 1 : n;


                for (int i = 0; i < n; i++) {

                    dna.c_genes.get(i).setWieght(rand.nextDouble() * 2 - 1);

                }

        }

        return;
        
    }

    private void mutateWithNewNode(DNA dna) {

        int new_id = dna.getMaxNodeID() + 1;

        int randInd = rand.nextInt(dna.c_genes.size());

        Connection randConn = dna.c_genes.get(randInd);

        MAX_INNOV_ID++;
        int[] t = {randConn.getIn_id() , new_id, MAX_INNOV_ID};
        connections.add(t);
        Connection conn1 = new Connection(randConn.getIn_id(), new_id, rand.nextDouble() * 2 - 1, true, MAX_INNOV_ID);

        MAX_INNOV_ID++;
        int[] t1 = {new_id , randConn.getIn_id(), MAX_INNOV_ID};
        connections.add(t1);
        Connection conn2 = new Connection(new_id, randConn.getOut_id(), rand.nextDouble() * 2 - 1, true, MAX_INNOV_ID);

        updateConnectionsList(new_id, randConn.getIn_id());
        randConn.setEnabled(false);

        dna.n_genes.add(new Node_N(new_id, true, true));
        dna.c_genes.add(conn1);
        dna.c_genes.add(conn2);

        return;

    }

    private void updateConnectionsList(int newID, int alrSelected) {

        int min = no_of_inputs + no_of_outputs + 1;
        int max = getMaxGeneID();

        //hiddent to hidden connections
        for (int j = min; j <= max; j++) {

            if(j != newID && j != alrSelected) {

                MAX_INNOV_ID++;
                int[] t = {newID, j, MAX_INNOV_ID};

                MAX_INNOV_ID++;
                int[] t1 = {j, newID, MAX_INNOV_ID};

                connections.add(t);
                connections.add(t1);
            }

        }

        //hidden to output connections
        for (int o = no_of_inputs + 1; o <= no_of_outputs + no_of_inputs; o++) {

            MAX_INNOV_ID++;
            int[] t = {newID, o, MAX_INNOV_ID};
            connections.add(t);

        }

    }

    private void mutateWithRemoveNode(DNA dna) {
        
        for (int i = 0; i < dna.n_genes.size(); i++) {
            Node_N node = dna.n_genes.get(i);

            if(node.isHiddenInput() && node.isHiddenOutput()) {

                int delID = node.getNode_id();

                // Remove the node and its connections
                dna.n_genes.remove(node);
                dna.c_genes.removeIf(conn -> conn.getIn_id() == delID || conn.getOut_id() == delID);
                break;

            }

        }

        return;

    }

    private static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    private void mutateWithConnection(DNA dna) {

        Collections.shuffle(connections);

        for (int[] c : connections) {

            if(isDisjointConnection(dna, c)){
                
                if(!checkIfLoopExist(dna.c_genes, c)) {

                    dna.c_genes.add(new Connection(c[0], c[1], rand.nextDouble() * 2 - 1, true, c[2]));
                    return;
    
                }

            }

        }
     
        return;

    }

    private void mutateWithRemoveConnection(DNA dna) {

        int ind = rand.nextInt(dna.c_genes.size());

        dna.c_genes.remove(ind);

        return;

    }

    private boolean isExcessConnection(DNA dna, int[] c) {

        if(c[0] > dna.getMaxNodeID() || c[1]> dna.getMaxNodeID()) return true;

        boolean check1 = true;
        boolean check2 = true;

        for (Node_N n : dna.n_genes) {

            if(c[0] == n.getNode_id()) check1 = false;
            if(c[1] == n.getNode_id()) check2 = false;

            if(!check1 && !check2) return false;

        }

        return true;

    }

    private boolean isDisjointConnection(DNA dna, int[] c) {

        for (Connection t : dna.c_genes) {

            if(c[0] == t.getIn_id() && c[1] == t.getOut_id()) return false;

        }

        return !isExcessConnection(dna, c);

    }


    private boolean checkIfLoopExist(LinkedList<Connection> conns, int[] newConn) {

        Graph graph = new Graph();

        graph.addConnection(newConn[0], newConn[1]);

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
                int[] t = {i, j, MAX_INNOV_ID};
                connections.add(t);

            }

        }


    }


    private int getMaxGeneID() {

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

            for (int[] c : connections) {

                c_genes.add(new Connection(c[0], c[1], rand.nextDouble() * 2 - 1, true, c[2]));

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


}
