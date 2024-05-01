import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Game.BotServival;
import Game.MadeForNeat;
import NeuralNetwork.Connection;
import java.util.Random;
import java.util.Set;

import NeuralNetwork.Node_N;
import NeuralNetwork.NueralNetwork;

public class Population {

    MadeForNeat[] games;
    int populationSize;
    DNA[] populationDNAs;
    int no_of_inputs;
    int no_of_outputs;
    Random rand = new Random();
    Hashtable<String, Integer> connections = new Hashtable<>();
    NueralNetwork tesNetwork;
    private int MAX_INNOV_ID;
    private int SPECIE_ID;

    Map<Integer, Specie> species = new HashMap<>();

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

        //2. games are played (for some amount of time)

        //3. cross overs are donee 

        //4. mutations are done

        //5. repeat to step 1


    }

    

    public void testMutatingWieghts() {

        double[] mutatingPossibilities = {AlotOfConstants.probChangeWieght, AlotOfConstants.probNewConn, AlotOfConstants.probNewNode, AlotOfConstants.probRemoveConn, AlotOfConstants.probRemoveNode};

        int pick = Statistics.poolSelect(mutatingPossibilities);
    
        switch (pick) {
            case 0: //change of wieghts
                System.out.println("WEIGHT CHANGED");
                mutateWithWeightsAndBiases(populationDNAs[0]);
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                tesNetwork.displayStructure();
                break;
                
            case 1: //new conn
                System.out.println("NEW CONNECTION ADDED");
                mutateWithConnection(populationDNAs[0]);
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                //System.out.println(populationDNAs[0].c_genes.size());               
                System.out.println("NEW SIZE: "+populationDNAs[0].c_genes.size());
                tesNetwork.displayStructure();
                break;
    
            case 2: //new node
                System.out.println("NEW NODE");
                mutateWithNewNode(populationDNAs[0]);
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);
                //System.out.println(populationDNAs[0].n_genes.size());
                tesNetwork.displayStructure();
                break;
    
            case 3: //remove conn
                System.out.println("REMOVE CONN");
                mutateWithRemoveConnection(populationDNAs[0]);
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);                
                System.out.println("NEW SIZE: "+populationDNAs[0].c_genes.size());
                tesNetwork.displayStructure();
                break;
    
            case 4: //remove node
                System.out.println("REMOVE NODE");
                mutateWithRemoveNode(populationDNAs[0]);
                tesNetwork = new NueralNetwork(populationDNAs[0].n_genes, populationDNAs[0].c_genes);                  
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

    private void mutateWithWeightsAndBiases(DNA dna) {

        //3 choices, pick 1 randomly
        int randomNumber = rand.nextInt(3) + 1;

        //shuffling because i want to pick first n random connections.
        Collections.shuffle(dna.c_genes);
        Collections.shuffle(dna.n_genes);

        double[] percentages = {5, 15, 25, 50, 75};

        //here higher percentages have more chance to be picked
        double percentage = Statistics.theCollectionPikcer(percentages, false);

        //ranging it further
        percentage = rand.nextDouble() * ((percentage + 25) - percentage) + percentage;
        //n is for connection list
        int n = (int)Math.ceil(dna.c_genes.size() * (percentage / 100));
        //n2 is for nodes list
        int n2 = (int)Math.ceil(dna.n_genes.size() * (percentage / 100));

        n = (n == 0) ? 1 : n;

        switch(randomNumber) {

            case 1 : //add/subract small value

                for (int i = 0; i < n; i++) {

                    if(AlotOfConstants.isUNIFORM) {

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

                for (int i = 0; i < n2; i++) {

                    if(AlotOfConstants.isUNIFORM) {

                        if(dna.n_genes.get(i).isHiddenInput()) {

                            double change = rand.nextDouble() * (AlotOfConstants.MAX_SCALING - (AlotOfConstants.MIN_SCALING)) + (AlotOfConstants.MIN_SCALING);
                            change = rand.nextDouble() < 0.5 ? -change : change;
                            double value = dna.n_genes.get(i).getBias() + change;
                            dna.n_genes.get(i).setBias(clamp(value, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE));


                        }


                    }
                    else {
                        
                        if(dna.n_genes.get(i).isHiddenInput()) {

                            double change = rand.nextGaussian() * AlotOfConstants.GUASIAN_STD + AlotOfConstants.GUASIAN_MEAN;
                            change = rand.nextDouble() < 0.5 ? -change : change;
                            double value = dna.n_genes.get(i).getBias() + change;
                            dna.n_genes.get(i).setBias(clamp(value, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE));

                        }
                        
                    }

                }

                break;

            
            case 2 : //scaling by small value

                for (int i = 0; i < n; i++) {

                    double scale = rand.nextDouble() * (AlotOfConstants.MAX_SCALING - (AlotOfConstants.MIN_SCALING)) + (AlotOfConstants.MIN_SCALING);
                    dna.c_genes.get(i).setWieght(dna.c_genes.get(i).getWieght() * scale);

                }

                for (int i = 0; i < n2; i++) {

                    double scale = rand.nextDouble() * (AlotOfConstants.MAX_SCALING - (AlotOfConstants.MIN_SCALING)) + (AlotOfConstants.MIN_SCALING);
                    if(dna.n_genes.get(i).isHiddenInput()) dna.n_genes.get(i).setBias(dna.n_genes.get(i).getBias() * scale);

                }

                break;

            case 3 : //reset weights

                //here lower percentages have more chance to be picked since i dont want to reset alot of wieghts
                percentage = Statistics.theCollectionPikcer(percentages, true);
                percentage = rand.nextDouble() * ((percentage + 5) - percentage) + percentage;
                n = (int)Math.ceil(dna.c_genes.size() * (percentage / 100));
                n = (n == 0) ? 1 : n;

                n2 = (int)Math.ceil(dna.n_genes.size() * (percentage / 100));
                n2 = (n2 == 0) ? 1 : n2;

                for (int i = 0; i < n; i++) {

                    dna.c_genes.get(i).setWieght(rand.nextDouble() * 2 - 1);

                }

                for (int i = 0; i < n2; i++) {

                    if(dna.n_genes.get(i).isHiddenInput()) dna.n_genes.get(i).setBias(rand.nextDouble() * 2 - 1);

                }

        }

        Collections.sort(dna.n_genes);
        return;
        
    }

    private void mutateWithNewNode(DNA dna) {

        //here i am assuming the dna n genes are sorted according to their ids
        //also nodes from 0 -> (no of inputs + no of outputs) order is never changed
        Node_N newNode = null;

        for(int i = no_of_inputs + no_of_outputs; i < dna.n_genes.size(); i++) {

            if(dna.n_genes.get(i).getNode_id() > (i + 1)) {
                
                newNode = new Node_N(i+1, true, true);
                break;

            }
        }

        if(newNode == null) newNode = new Node_N(dna.getMaxNodeID() + 1, true, true);

        Connection c = dna.c_genes.get(rand.nextInt(dna.c_genes.size()));

        int in = c.getIn_id();
        int out = c.getOut_id();

        Integer id1 = connections.get(in+","+newNode.getNode_id());
        Integer id2 = connections.get(newNode.getNode_id()+","+out);

        c.setEnabled(false);

        if(id1 == null) {
            MAX_INNOV_ID++;
            dna.c_genes.add(new Connection(in, newNode.getNode_id(), rand.nextDouble() * 2 - 1, true, MAX_INNOV_ID));
            connections.put(in+","+newNode.getNode_id(), MAX_INNOV_ID);
        }

        else dna.c_genes.add(new Connection(in, newNode.getNode_id(), rand.nextDouble() * 2 - 1, true, id1));


        if(id2 == null) {
            MAX_INNOV_ID++;
            dna.c_genes.add(new Connection(newNode.getNode_id(), out, rand.nextDouble() * 2 - 1, true, MAX_INNOV_ID));
            connections.put(newNode.getNode_id()+","+out, MAX_INNOV_ID);
        }

        else dna.c_genes.add(new Connection(newNode.getNode_id(), out, rand.nextDouble() * 2 - 1, true, id2));


        dna.n_genes.add(newNode);

        Collections.sort(dna.n_genes);
        clearNodeOuts(dna.n_genes);
        
    }


    private void mutateWithRemoveNode(DNA dna) {
        
        if(dna.n_genes.size() <= (no_of_inputs + no_of_outputs)) return;

        int rInd = rand.nextInt(dna.n_genes.size() - (no_of_inputs + no_of_outputs)) + (no_of_inputs + no_of_outputs);
        int delID = dna.n_genes.get(rInd).getNode_id();

        Graph g = new Graph();
        Set<Integer> startNodes = new HashSet<>();
        Set<Integer> outNodes = new HashSet<>();

        for(int i = 1; i <= no_of_inputs; i++) {
            startNodes.add(i);
        }

        for(int i = no_of_inputs + 1; i <= no_of_outputs + no_of_inputs; i++) {
            outNodes.add(i);
        }

        for (Connection c : dna.c_genes) {

            if(c.getIn_id() != delID && c.getOut_id() != delID) {
                g.addConnection(c.getIn_id(), c.getOut_id());
            }           

        }

        if(!g.hasNoIsolatedNodes(startNodes, outNodes)) {
            Collections.sort(dna.n_genes);
            clearNodeOuts(dna.n_genes);
            return;
        }

        
        System.out.println("C_GENES SIZE BFR: "+dna.c_genes.size());
        dna.c_genes.removeIf(connection -> connection.getIn_id() == delID || connection.getOut_id() == delID);
        System.out.println("C_GENES SIZE AFTR: "+dna.c_genes.size());

        System.out.println("N_GENES SIZE BFR: "+dna.n_genes.size());
        dna.n_genes.remove(rInd);
        System.out.println("N_GENES SIZE AFTR: "+dna.n_genes.size());
        Collections.sort(dna.n_genes);
        clearNodeOuts(dna.n_genes);

    }

    private static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    private void mutateWithConnection(DNA dna) {

        Hashtable<Node_N, Set<Node_N>> setsOfUnConnectedNodes = new Hashtable<>();

        for (int i = 0; i < no_of_inputs; i++) {
            Set<Node_N> set = nodesWithNoPathTo(dna.n_genes, dna.n_genes.get(i));
            if(set.size() > 0) setsOfUnConnectedNodes.put(dna.n_genes.get(i), set);
        }

        for (int i = no_of_inputs + no_of_outputs; i < dna.n_genes.size(); i++) {
            Set<Node_N> set = nodesWithNoPathTo(dna.n_genes, dna.n_genes.get(i));
            if(set.size() > 0) setsOfUnConnectedNodes.put(dna.n_genes.get(i), set);
        }

        Node_N randKey = getRandomKey(setsOfUnConnectedNodes);

        Node_N[] selectedSet = setsOfUnConnectedNodes.get(randKey).toArray(new Node_N[0]);

        Node_N n2 = selectedSet[rand.nextInt(selectedSet.length)];

        int count = 0;
        
        if(!n2.isHiddenInput() && dna.n_genes.size() > no_of_inputs + no_of_outputs) {
            while(!n2.isHiddenInput() && count < 100) {
                randKey = getRandomKey(setsOfUnConnectedNodes);
                selectedSet = setsOfUnConnectedNodes.get(randKey).toArray(new Node_N[0]);
                n2 = selectedSet[rand.nextInt(selectedSet.length)];
                count++;
            }
        }

        if(randKey.isHiddenOutput() && n2.isHiddenInput() && !checkIfLoopExist(dna.c_genes, randKey.getNode_id(), n2.getNode_id())) {

            Integer id = connections.get(randKey.getNode_id()+","+n2.getNode_id());

            if(id == null) {

                MAX_INNOV_ID++;
                dna.c_genes.add(new Connection(randKey.getNode_id(), n2.getNode_id(), rand.nextDouble() * 2 - 1, true, MAX_INNOV_ID));
                connections.put(randKey.getNode_id()+","+n2.getNode_id(), MAX_INNOV_ID);

            }

            else {

                for(int i = 0; i < dna.c_genes.size(); i++) {
                    if(dna.c_genes.get(i).getIn_id() == id) {
                        dna.c_genes.get(i).setEnabled(true);
                        return;
                    }
                }

                dna.c_genes.add(new Connection(randKey.getNode_id(), n2.getNode_id(), rand.nextDouble() * 2 - 1, true, id));
                
            }

        }
        clearNodeOuts(dna.n_genes);
        
        return;

    }

    private void clearNodeOuts(LinkedList<Node_N> list) {

        for(int i = 0; i < list.size(); i++) {
            list.get(i).clearOuts();
        }
    }


    //i died here
    private void mutateWithRemoveConnection(DNA dna) {
        int maxAttempts = 100;
        int attempts = 0;
        
        System.out.println("SIZE BFR: "+dna.c_genes.size());

        Set<Integer> startNodes = new HashSet<>();
        Set<Integer> outNodes = new HashSet<>();

        for(int i = 1; i <= no_of_inputs; i++) {
            startNodes.add(i);
        }

        for(int i = no_of_inputs + 1; i <= no_of_outputs + no_of_inputs; i++) {
            outNodes.add(i);
        }
        

        Graph g = new Graph();
        for (Connection con : dna.c_genes) {
            if(con.isEnabled()) g.addConnection(con.getIn_id(), con.getOut_id());
        }
        
        while (attempts < maxAttempts) {
            Connection c = dna.c_genes.get(rand.nextInt(dna.c_genes.size()));
            
            g.removeConnection(c.getIn_id(), c.getOut_id());
            
            if (g.hasNoIsolatedNodes(startNodes, outNodes)) {
                dna.c_genes.remove(c);
                System.out.println("removed");
                break;
            }
            
            g.addConnection(c.getIn_id(), c.getOut_id());
            attempts++;
        }

        System.out.println("SIZE AFTR: "+dna.c_genes.size());
        clearNodeOuts(dna.n_genes);
    }

    public void printConnectionList() {

        // Get an enumeration of the key-value pairs
        Enumeration<String> keys = connections.keys();

        // Iterate over the key-value pairs
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Integer value = connections.get(key);

            System.out.println("Key: " + key + ", Value: " + value);
        }

    }



    private <K, V> K getRandomKey(Hashtable<K, V> hashtable) {
        if (hashtable.isEmpty()) {
            return null;
        }
        Random random = new Random();
        List<K> keys = new ArrayList<>(hashtable.keySet());
        return keys.get(random.nextInt(keys.size()));
    }

    private Set<Node_N> nodesWithNoPathTo(LinkedList<Node_N> graph, Node_N node) {
        Set<Node_N> reachableNodes = getReachableNodes(graph, node);
        Set<Node_N> allNodes = new HashSet<>();
        for (Node_N n : graph) {
            allNodes.add(n);
        }
        allNodes.remove(node);
        allNodes.removeAll(reachableNodes);
        return allNodes;
    }

    private Set<Node_N> getReachableNodes(LinkedList<Node_N> graph, Node_N node) {
        Set<Node_N> reachableNodes = new HashSet<>();
        dfs(node, reachableNodes);
        return reachableNodes;
    }

    private void dfs(Node_N node, Set<Node_N> visited) {
        if (!visited.contains(node)) {
            visited.add(node);
            for (Node_N neighbor : node.getOuts().keySet()) {
                dfs(neighbor, visited);
            }
        }
    }


    private static boolean isExcessConnection(DNA dna, int in, int out) {

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

    private static boolean isDisjointConnection(DNA dna, int in, int out) {

        for (Connection t : dna.c_genes) {

            if(in == t.getIn_id() && out == t.getOut_id()) return false;

        }

        return !isExcessConnection(dna, in, out);

    }


    private boolean checkIfLoopExist(LinkedList<Connection> conns, int in, int out) {

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

            Enumeration<String> keys = connections.keys();
            while(keys.hasMoreElements()) {
                String key = keys.nextElement();
                String[] split = key.split(",");
                c_genes.add(new Connection(Integer.parseInt(split[0]), Integer.parseInt(split[1]), rand.nextDouble() * 2 - 1, true, connections.get(key)));
                    
            }

            
            populationDNAs[p] = new DNA(n_genes, c_genes);

        }

        speciate();
    }


    //start of speciation
    private void speciate() {

        Map<DNA, Map<DNA, Double>> distances = new HashMap<>();
        // Calculate compatibility distance between individuals
        for (int i = 0; i < populationDNAs.length; i++) {
            Map<DNA, Double> individualDistances = new HashMap<>();
            for (int j = 0; j < populationDNAs.length; j++) {
                if (populationDNAs[i] != populationDNAs[j]) {
                    double distance = calculateCompatibilityDistance(populationDNAs[i], populationDNAs[j]);
                    individualDistances.put(populationDNAs[j], distance);
                }
            }
            distances.put(populationDNAs[i], individualDistances);
        }

        for (int i = 0; i < populationDNAs.length; i++) {
            if(!isAlreadyInSpecies(populationDNAs[i])) {
                putInSpecie(distances, populationDNAs[i]);
            }
        }

    }



    private void putInSpecie(Map<DNA, Map<DNA, Double>> distances, DNA individual) {

        boolean gotIt = false;

        for (Map.Entry<DNA, Map<DNA, Double>> entry : distances.entrySet()) {
            DNA otherIndividual = entry.getKey();
            if (otherIndividual != individual) {
                double distance = distances.get(individual).get(otherIndividual);
                if (distance < AlotOfConstants.COMP_THRSHOLD) {
                    gotIt = true;
                    if(isAlreadyInSpecies(otherIndividual)) {
                        Integer key = getSpecieKey(otherIndividual);
                        species.get(key).list.add(individual);
                    }
                    else {
                        SPECIE_ID++;
                        Specie sp = new Specie();
                        sp.list.add(otherIndividual);
                        sp.list.add(individual);
                        sp.setReprentative();
                        species.put(SPECIE_ID, sp);
                    }
                    return;

                }
            }
        }

        if(!gotIt) {
            SPECIE_ID++;
            Specie sp = new Specie();
            sp.list.add(individual);
            sp.setReprentative();
            species.put(SPECIE_ID, sp);

        }

    }

    private boolean isAlreadyInSpecies(DNA dna) {

        Set<Integer> keys = species.keySet();

        for(Integer i : keys) {

            if(species.get(i).list.contains(dna)) return true;

        }

        return false;
    }

    private int getSpecieKey(DNA dna) {

        Set<Integer> keys = species.keySet();

        for(Integer i : keys) {

            if(species.get(i).list.contains(dna)) return i;

        }

        return -1;
    }

    private double calculateCompatibilityDistance(DNA dna1, DNA dna2) {

        Collections.sort(dna1.c_genes);
        Collections.sort(dna2.c_genes);

        int shorterLength = Math.min(dna1.c_genes.size(), dna2.c_genes.size());
        int longerLength = Math.max(dna1.c_genes.size(), dna2.c_genes.size());

        Connection[][] sideByside = new Connection[shorterLength+longerLength][2];
        int ind = 0;

        for (Connection c : dna1.c_genes) {
            boolean found = false;
            for (Connection c2 : dna2.c_genes) {
                if (c.getInnov() == c2.getInnov()) {
                    sideByside[ind][0] = c;
                    sideByside[ind][1] = c2;
                    found = true;
                    break;
                }
            }
            if (!found) {
                sideByside[ind][0] = c;
                sideByside[ind][1] = null;
            }
            ind++;
        }
        
        for (Connection c2 : dna2.c_genes) {
            boolean found = false;
            for (Connection c : dna1.c_genes) {
                if (c.getInnov() == c2.getInnov()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                sideByside[ind][0] = null;
                sideByside[ind][1] = c2;
            }
            ind++;
        }

        int disJoints = 0;
        int excess = 0;
        double sumOfWeightDiffs = 0;

        for(Connection[] c : sideByside) {

            if(c[0] != null && c[1] != null) {
                sumOfWeightDiffs += Math.abs(c[0].getWieght() - c[1].getWieght());
            }

            if(c[0] == null) {
                sumOfWeightDiffs += c[1].getWieght();
                if(isDisjointConnection(dna1, c[1].getIn_id(), c[1].getOut_id())) disJoints++;
                else excess++;
            }

            if(c[1] == null) {
                sumOfWeightDiffs += c[0].getWieght();
                if(isDisjointConnection(dna2, c[0].getIn_id(), c[0].getOut_id())) disJoints++;
                else excess++;
            }

        }

        double avgWeightDiffs = sumOfWeightDiffs / (longerLength + shorterLength);

        double d = (AlotOfConstants.cForDisjoint * disJoints) / longerLength;
        double e = (AlotOfConstants.cForExcess * excess) / longerLength;
        double w = AlotOfConstants.cForWeights * avgWeightDiffs;

        return d + e + w;
        
    }

}

class Specie {

    List<DNA> list = new ArrayList<>();
    DNA representative;

    public void setReprentative() {
        representative = list.get(0);
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
