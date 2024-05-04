import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import NeuralNetwork.Connection;
import NeuralNetwork.DNA;
import NeuralNetwork.Node_N;

public class Mutation {
    
    static void mutateWithWeightsAndBiases(DNA dna) {

        //3 choices, pick 1 randomly
        int randomNumber = Population.rand.nextInt(3) + 1;

        //shuffling because i want to pick first n random connections.
        Collections.shuffle(dna.c_genes);
        Collections.shuffle(dna.n_genes);

        double[] percentages = {5, 15, 25, 50, 75};

        //here higher percentages have more chance to be picked
        double percentage = Statistics.theCollectionPikcer(percentages, false);

        //ranging it further
        percentage = Population.rand.nextDouble() * ((percentage + 25) - percentage) + percentage;
        //n is for connection list
        int n = (int)Math.ceil(dna.c_genes.size() * (percentage / 100));
        //n2 is for nodes list
        int n2 = (int)Math.ceil(dna.n_genes.size() * (percentage / 100));

        n = (n == 0) ? 1 : n;

        switch(randomNumber) {

            case 1 : //add/subract small value

                for (int i = 0; i < n; i++) {

                    if(AlotOfConstants.isUNIFORM) {

                        double change = Population.rand.nextDouble() * (AlotOfConstants.MAX_SCALING - (AlotOfConstants.MIN_SCALING)) + (AlotOfConstants.MIN_SCALING);
                        change = Population.rand.nextDouble() < 0.5 ? -change : change;
                        double value = dna.c_genes.get(i).getWieght() + change;
                        dna.c_genes.get(i).setWieght(clamp(value, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE));


                    }
                    else {
                        //mean std dev
                        double change = Population.rand.nextGaussian() * AlotOfConstants.GUASIAN_STD + AlotOfConstants.GUASIAN_MEAN;
                        change = Population.rand.nextDouble() < 0.5 ? -change : change;
                        double value = dna.c_genes.get(i).getWieght() + change;
                        dna.c_genes.get(i).setWieght(clamp(value, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE));
                        
                    }

                }

                for (int i = 0; i < n2; i++) {

                    if(AlotOfConstants.isUNIFORM) {

                        if(dna.n_genes.get(i).isHiddenInput()) {

                            double change = Population.rand.nextDouble() * (AlotOfConstants.MAX_SCALING - (AlotOfConstants.MIN_SCALING)) + (AlotOfConstants.MIN_SCALING);
                            change = Population.rand.nextDouble() < 0.5 ? -change : change;
                            double value = dna.n_genes.get(i).getBias() + change;
                            dna.n_genes.get(i).setBias(clamp(value, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE));


                        }


                    }
                    else {
                        
                        if(dna.n_genes.get(i).isHiddenInput()) {

                            double change = Population.rand.nextGaussian() * AlotOfConstants.GUASIAN_STD + AlotOfConstants.GUASIAN_MEAN;
                            change = Population.rand.nextDouble() < 0.5 ? -change : change;
                            double value = dna.n_genes.get(i).getBias() + change;
                            dna.n_genes.get(i).setBias(clamp(value, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE));

                        }
                        
                    }

                }

                break;

            
            case 2 : //scaling by small value

                for (int i = 0; i < n; i++) {

                    double scale = Population.rand.nextDouble() * (AlotOfConstants.MAX_SCALING - (AlotOfConstants.MIN_SCALING)) + (AlotOfConstants.MIN_SCALING);
                    scale = clamp(scale, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE);
                    dna.c_genes.get(i).setWieght(dna.c_genes.get(i).getWieght() * scale);

                }

                for (int i = 0; i < n2; i++) {

                    double scale = Population.rand.nextDouble() * (AlotOfConstants.MAX_SCALING - (AlotOfConstants.MIN_SCALING)) + (AlotOfConstants.MIN_SCALING);
                    scale = clamp(scale, AlotOfConstants.MIN_CLAMP_VALUE, AlotOfConstants.MAX_CLAMP_VALUE);
                    if(dna.n_genes.get(i).isHiddenInput()) dna.n_genes.get(i).setBias(dna.n_genes.get(i).getBias() * scale);

                }

                break;

            case 3 : //reset weights

                //here lower percentages have more chance to be picked since i dont want to reset alot of wieghts
                percentage = Statistics.theCollectionPikcer(percentages, true);
                percentage = Population.rand.nextDouble() * ((percentage + 5) - percentage) + percentage;
                n = (int)Math.ceil(dna.c_genes.size() * (percentage / 100));
                n = (n == 0) ? 1 : n;

                n2 = (int)Math.ceil(dna.n_genes.size() * (percentage / 100));
                n2 = (n2 == 0) ? 1 : n2;

                for (int i = 0; i < n; i++) {

                    dna.c_genes.get(i).setWieght(Population.rand.nextDouble() * 2 - 1);

                }

                for (int i = 0; i < n2; i++) {

                    if(dna.n_genes.get(i).isHiddenInput()) dna.n_genes.get(i).setBias(Population.rand.nextDouble() * 2 - 1);

                }

        }

        Collections.sort(dna.n_genes);
        return;
        
    }

    static void mutateWithNewNode(DNA dna) {

        //here i am assuming the dna n genes are sorted according to their ids
        //also nodes from 0 -> (no of inputs + no of outputs) order is never changed
        Node_N newNode = null;

        for(int i = Population.no_of_inputs + Population.no_of_outputs; i < dna.n_genes.size(); i++) {

            if(dna.n_genes.get(i).getNode_id() > (i + 1)) {
                
                newNode = new Node_N(i+1, true, true);
                break;

            }
        }

        if(newNode == null) newNode = new Node_N(dna.getMaxNodeID() + 1, true, true);

        Connection c = dna.c_genes.get(Population.rand.nextInt(dna.c_genes.size()));

        int in = c.getIn_id();
        int out = c.getOut_id();

        Integer id1 = Population.connections.get(in+","+newNode.getNode_id());
        Integer id2 = Population.connections.get(newNode.getNode_id()+","+out);

        c.setEnabled(false);

        if(id1 == null) {
            Population.MAX_INNOV_ID++;
            dna.c_genes.add(new Connection(in, newNode.getNode_id(), Population.rand.nextDouble() * 2 - 1, true, Population.MAX_INNOV_ID));
            Population.connections.put(in+","+newNode.getNode_id(), Population.MAX_INNOV_ID);
        }

        else dna.c_genes.add(new Connection(in, newNode.getNode_id(), Population.rand.nextDouble() * 2 - 1, true, id1));


        if(id2 == null) {
            Population.MAX_INNOV_ID++;
            dna.c_genes.add(new Connection(newNode.getNode_id(), out, Population.rand.nextDouble() * 2 - 1, true, Population.MAX_INNOV_ID));
            Population.connections.put(newNode.getNode_id()+","+out, Population.MAX_INNOV_ID);
        }

        else dna.c_genes.add(new Connection(newNode.getNode_id(), out, Population.rand.nextDouble() * 2 - 1, true, id2));


        dna.n_genes.add(newNode);

        Collections.sort(dna.n_genes);
        Population.clearNodeOuts(dna.n_genes);
        
    }


    static void mutateWithRemoveNode(DNA dna) {
        
        if(dna.n_genes.size() <= (Population.no_of_inputs + Population.no_of_outputs)) return;

        int rInd = Population.rand.nextInt(dna.n_genes.size() - (Population.no_of_inputs + Population.no_of_outputs)) + (Population.no_of_inputs + Population.no_of_outputs);
        int delID = dna.n_genes.get(rInd).getNode_id();

        Graph g = new Graph();
        Set<Integer> startNodes = new HashSet<>();
        Set<Integer> outNodes = new HashSet<>();

        for(int i = 1; i <= Population.no_of_inputs; i++) {
            g.graph.put(i, new ArrayList<>());
            startNodes.add(i);
        }

        for(int i = Population.no_of_inputs + 1; i <= Population.no_of_outputs + Population.no_of_inputs; i++) {
            g.graph.put(i, new ArrayList<>());
            outNodes.add(i);
        }

        for (Connection c : dna.c_genes) {

            if(c.isEnabled() && c.getIn_id() != delID && c.getOut_id() != delID) {
                g.addConnection(c.getIn_id(), c.getOut_id());
            }           

        }

        if(!g.hasNoIsolatedNodes(startNodes, outNodes)) {
            Collections.sort(dna.n_genes);
            Population.clearNodeOuts(dna.n_genes);
            return;
        }

        
        //System.out.println("C_GENES SIZE BFR: "+dna.c_genes.size());
        dna.c_genes.removeIf(connection -> connection.getIn_id() == delID || connection.getOut_id() == delID);
        //System.out.println("C_GENES SIZE AFTR: "+dna.c_genes.size());

        //System.out.println("N_GENES SIZE BFR: "+dna.n_genes.size());
        dna.n_genes.remove(rInd);
        //System.out.println("N_GENES SIZE AFTR: "+dna.n_genes.size());
        Collections.sort(dna.n_genes);
        Population.clearNodeOuts(dna.n_genes);

    }

    static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    static void mutateWithConnection(DNA dna) {

        Hashtable<Node_N, Set<Node_N>> setsOfUnConnectedNodes = new Hashtable<>();

        for (int i = 0; i < Population.no_of_inputs; i++) {
            Set<Node_N> set = nodesWithNoPathTo(dna.n_genes, dna.n_genes.get(i));
            if(set.size() > 0) setsOfUnConnectedNodes.put(dna.n_genes.get(i), set);
        }

        for (int i = Population.no_of_inputs + Population.no_of_outputs; i < dna.n_genes.size(); i++) {
            Set<Node_N> set = nodesWithNoPathTo(dna.n_genes, dna.n_genes.get(i));
            if(set.size() > 0) setsOfUnConnectedNodes.put(dna.n_genes.get(i), set);
        }

        Node_N randKey = getRandomKey(setsOfUnConnectedNodes);

        Node_N[] selectedSet = setsOfUnConnectedNodes.get(randKey).toArray(new Node_N[0]);

        Node_N n2 = selectedSet[Population.rand.nextInt(selectedSet.length)];

        int count = 0;
        
        if(!n2.isHiddenInput() && dna.n_genes.size() > Population.no_of_inputs + Population.no_of_outputs) {
            while(!n2.isHiddenInput() && count < 100) {
                randKey = getRandomKey(setsOfUnConnectedNodes);
                selectedSet = setsOfUnConnectedNodes.get(randKey).toArray(new Node_N[0]);
                n2 = selectedSet[Population.rand.nextInt(selectedSet.length)];
                count++;
            }
        }

        if(randKey.isHiddenOutput() && n2.isHiddenInput() && !Population.checkIfLoopExist(dna.c_genes, randKey.getNode_id(), n2.getNode_id())) {

            Integer id = Population.connections.get(randKey.getNode_id()+","+n2.getNode_id());

            if(id == null) {

                Population.MAX_INNOV_ID++;
                dna.c_genes.add(new Connection(randKey.getNode_id(), n2.getNode_id(), Population.rand.nextDouble() * 2 - 1, true, Population.MAX_INNOV_ID));
                Population.connections.put(randKey.getNode_id()+","+n2.getNode_id(), Population.MAX_INNOV_ID);

            }

            else {

                for(int i = 0; i < dna.c_genes.size(); i++) {
                    if(dna.c_genes.get(i).getIn_id() == id) {
                        dna.c_genes.get(i).setEnabled(true);
                        return;
                    }
                }

                dna.c_genes.add(new Connection(randKey.getNode_id(), n2.getNode_id(), Population.rand.nextDouble() * 2 - 1, true, id));
                
            }

        }
        Population.clearNodeOuts(dna.n_genes);
        
        return;

    }

    //i died here
    static void mutateWithRemoveConnection(DNA dna) {
        int maxAttempts = 100;
        int attempts = 0;
        
        //System.out.println("SIZE BFR: "+dna.c_genes.size());

        Set<Integer> startNodes = new HashSet<>();
        Set<Integer> outNodes = new HashSet<>();

        for(int i = 1; i <= Population.no_of_inputs; i++) {
            startNodes.add(i);
        }

        for(int i = Population.no_of_inputs + 1; i <= Population.no_of_outputs + Population.no_of_inputs; i++) {
            outNodes.add(i);
        }
        

        Graph g = new Graph();
        for (Connection con : dna.c_genes) {
            if(con.isEnabled()) g.addConnection(con.getIn_id(), con.getOut_id());
        }
        
        while (attempts < maxAttempts) {
            Connection c = dna.c_genes.get(Population.rand.nextInt(dna.c_genes.size()));
            
            if(c.isEnabled()) {

                g.removeConnection(c.getIn_id(), c.getOut_id());
            
                if (g.hasNoIsolatedNodes(startNodes, outNodes)) {
                    dna.c_genes.remove(c);
                    //System.out.println("removed");
                    break;
                }
                
                g.addConnection(c.getIn_id(), c.getOut_id());

            }
            attempts++;
        }

        //System.out.println("SIZE AFTR: "+dna.c_genes.size());
        Population.clearNodeOuts(dna.n_genes);
    }


    private static <K, V> K getRandomKey(Hashtable<K, V> hashtable) {
        if (hashtable.isEmpty()) {
            return null;
        }
        Random random = new Random();
        List<K> keys = new ArrayList<>(hashtable.keySet());
        return keys.get(random.nextInt(keys.size()));
    }

    private static Set<Node_N> nodesWithNoPathTo(LinkedList<Node_N> graph, Node_N node) {
        Set<Node_N> reachableNodes = getReachableNodes(graph, node);
        Set<Node_N> allNodes = new HashSet<>();
        for (Node_N n : graph) {
            allNodes.add(n);
        }
        allNodes.remove(node);
        allNodes.removeAll(reachableNodes);
        return allNodes;
    }

    private static Set<Node_N> getReachableNodes(LinkedList<Node_N> graph, Node_N node) {
        Set<Node_N> reachableNodes = new HashSet<>();
        dfs(node, reachableNodes);
        return reachableNodes;
    }

    private static void dfs(Node_N node, Set<Node_N> visited) {
        if (!visited.contains(node)) {
            visited.add(node);
            for (Node_N neighbor : node.getOuts().keySet()) {
                dfs(neighbor, visited);
            }
        }
    }


    static void pickOneOfMutation(DNA dna) {

        double[] mutatingPossibilities = {AlotOfConstants.probChangeWieght, AlotOfConstants.probNewConn, AlotOfConstants.probNewNode, AlotOfConstants.probRemoveConn, AlotOfConstants.probRemoveNode};

        int pick = Statistics.poolSelect(mutatingPossibilities);
    
        switch (pick) {
            case 0: 
                mutateWithWeightsAndBiases(dna);             
                break;
                
            case 1: //new conn         
                mutateWithConnection(dna);            
                break;
    
            case 2: //new node
                mutateWithNewNode(dna);
                break;
    
            case 3: //remove conn
                mutateWithRemoveConnection(dna);
                break;
    
            case 4: //remove node
                mutateWithRemoveNode(dna);
            }

    }


}
