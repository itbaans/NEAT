import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import NeuralNetwork.Connection;
import NeuralNetwork.DNA;
import NeuralNetwork.Node_N;

public class CrossOver {

    static int theChange = (int)((double)Population.populationDNAs.length * AlotOfConstants.percToKill);


    public static void theKillingSpree() {

        double sumOfAvgFitness = 0;

        for(Map.Entry<Integer, Specie> entry : Population.species.entrySet()) {

            sumOfAvgFitness += entry.getValue().getAvgFitness();

        }

        Integer[] keys = Population.species.keySet().toArray(new Integer[0]);

        double[] spPossiblities = new double[keys.length];

        for(int c = 0; c < theChange; c++) {

            keys = Population.species.keySet().toArray(new Integer[0]);

            spPossiblities = new double[keys.length];

            for(int i = 0; i < keys.length; i++) {

                double avgFit = Population.species.get(keys[i]).getAvgFitness();
                spPossiblities[i] = (avgFit / sumOfAvgFitness) * 100;

            }

            int selected = Statistics.theCollectionPikcerGetInd(spPossiblities, true);

            Specie selectedSp = Population.species.get(keys[selected]);

            Collections.sort(selectedSp.list);

            for(int p = 0; p < Population.populationDNAs.length; p++) {
                if(Population.populationDNAs[p] == selectedSp.list.get(0)) {
                    Population.populationDNAs[p] = null;
                    break;
                }         
            }

            selectedSp.list.remove(0);
            if(selectedSp.list.isEmpty()) Population.species.remove(keys[selected]);
            else selectedSp.setReprentative();

            Speciation.updateSpeciesAvgFitness();
            sumOfAvgFitness = 0;
            
            for(Map.Entry<Integer, Specie> entry : Population.species.entrySet()) {

                sumOfAvgFitness += entry.getValue().getAvgFitness();

            }

        }

    }

    public static void theReproduction() throws CloneNotSupportedException {

        double sumOfAvgFitness = 0;

        for(Map.Entry<Integer, Specie> entry : Population.species.entrySet()) {

            sumOfAvgFitness += entry.getValue().getAvgFitness();

        }

        Integer[] keys = Population.species.keySet().toArray(new Integer[0]);

        double[] spPossiblities = new double[keys.length];

        for(int c = 0; c < theChange; c++) {

            keys = Population.species.keySet().toArray(new Integer[0]);

            spPossiblities = new double[keys.length];

            for(int i = 0; i < keys.length; i++) {

                double avgFit = Population.species.get(keys[i]).getAvgFitness();
                spPossiblities[i] = (avgFit / sumOfAvgFitness) * 100;

            }

            int selected = Statistics.theCollectionPikcerGetInd(spPossiblities, true);

            Specie selectedSp = Population.species.get(keys[selected]);

            double[] parentPossibilities = new double[selectedSp.list.size()];

            double sumOfParFit = 0;
            for(DNA d : selectedSp.list){
                sumOfParFit += d.getFitness();
            }

            for(int i = 0; i < selectedSp.list.size(); i++) {
                parentPossibilities[i] = selectedSp.list.get(i).getFitness() / sumOfParFit;
            }

            DNA par1 = getContender(parentPossibilities, selectedSp);
            DNA par2 = getContender(parentPossibilities, selectedSp);

            DNA child = actualCrossOver(par1, par2);

            Mutation.pickOneOfMutation(child);

            for(int p = 0; p < Population.populationDNAs.length; p++) {
                if(Population.populationDNAs[p] == null) {
                    Population.populationDNAs[p] = child;
                    break;
                }
            }

            Speciation.putInSpecieByRepresentative(child);


            Speciation.updateSpeciesAvgFitness();
            sumOfAvgFitness = 0;

            for(Map.Entry<Integer, Specie> entry : Population.species.entrySet()) {

                sumOfAvgFitness += entry.getValue().getAvgFitness();

            }

        }

    }


    private static DNA actualCrossOver(DNA p1, DNA p2) throws CloneNotSupportedException {

        Collections.sort(p1.c_genes);
        Collections.sort(p2.c_genes);
        Random rand = new Random();

        LinkedList<Connection> newConns = new LinkedList<>();
        LinkedList<Node_N> newNodes = new LinkedList<>();

        Set<Node_N> set = new HashSet<>();

        for (Connection c : p1.c_genes) {
            boolean found = false;
            for (Connection c2 : p2.c_genes) {
                if (c.getInnov() == c2.getInnov()) {

                    if(p1.getFitness() > p2.getFitness()) {

                        newConns.add((Connection)c.clone());

                        set.add(p1.getNode(c.getIn_id()));
                        set.add(p1.getNode(c.getOut_id()));


                    }
                    else {
                        
                        newConns.add((Connection)c2.clone());

                        set.add(p2.getNode(c2.getIn_id()));
                        set.add(p2.getNode(c2.getOut_id()));

                    }

                    found = true;
                    break;
                }
            }
            
            if (!found && rand.nextDouble() < 0.5) {

                newConns.add((Connection)c.clone());

                set.add(p1.getNode(c.getIn_id()));
                set.add(p1.getNode(c.getOut_id()));
            }
        }
        
        for (Connection c2 : p2.c_genes) {
            boolean found = false;
            for (Connection c : p1.c_genes) {
                if (c.getInnov() == c2.getInnov()) {
                    found = true;
                    break;
                }
            }
            if (!found && rand.nextDouble() < 0.5) {
                newConns.add((Connection)c2.clone());

                set.add(p2.getNode(c2.getIn_id()));
                set.add(p2.getNode(c2.getOut_id()));
            }
            
        }

        for(Node_N n : set) {
            newNodes.add((Node_N)n.clone());

        }

        DNA theBirth = new DNA(newNodes, newConns);
        Population.DNA_ID++;
        theBirth.id = Population.DNA_ID;

        return theBirth;

    }

    private static DNA getContender(double[] possiblities, Specie s) {

        int id = Statistics.poolSelect(possiblities);
        DNA con1 = s.list.get(id);
        id = Statistics.poolSelect(possiblities);
        DNA con2 = s.list.get(id);

        Random rand = new Random();
        
        DNA winner = rand.nextDouble() < 0.5 ? con1 : con2;

        return winner;

    }
    

    
}
