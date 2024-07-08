package NEAT_STUFF;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import NeuralNetwork.Connection;
import NeuralNetwork.DNA;
import NeuralNetwork.Node_N;

public class CrossOver {

    private static int currentStage = 1;
    static int stagnantGenerations;
    static Random rand = new Random();

    private static float sizeWeight = 1.5f;
    private static float fitnessWeight = 2f;

    public static void theRealEvolution() {

        Population.updateMaxFitness();
        checkStageTransition();

        switch (currentStage) {
            case 1:
                stageOneEvolution();
                break;
            case 2:
                stageTwoEvolution();
                break;
            case 3:
                stageThreeEvolution();
                break;
        }

    }

    private static void stageOneEvolution() {

        Integer[] keys = Population.species.keySet().toArray(new Integer[0]);

        for(Integer k : keys) {

            freeEvolution(Population.species.get(k));

        }

    }

    private static void stageTwoEvolution() {

        theKillingSpree();

    }
    private static void stageThreeEvolution() {

        sizeWeight = 1f;
        fitnessWeight = 3f;
        AlotOfConstants.crossSpRate = 0.08f;
        
        theKillingSpree();

    }


    private static void checkStageTransition() {
        if (currentStage == 1 && (Population.currentGen > 50 || stagnantGenerations > 10)) {
            currentStage = 2;
            System.out.println("Transitioning to Stage 2");
        } else if (currentStage == 2 && (Population.currentGen > 150 || stagnantGenerations > 20)) {
            currentStage = 3;
            System.out.println("Transitioning to Stage 3");
        }
    }

    private static void theKillingSpree() {

        Integer[] keys = Population.species.keySet().toArray(new Integer[0]);

        Speciation.updateSpeciesAvgFitness();

        double totalSpecFitn = 0;
        for(int i = 0; i < keys.length; i++) {
            totalSpecFitn += Population.species.get(keys[i]).getAvgFitness();
        }

        double[] elimationPossibilities = new double[keys.length];

        for(int i = 0; i < elimationPossibilities.length; i++) {
            elimationPossibilities[i] = calculateEliminationProbability(Population.species.get(keys[i]), totalSpecFitn);
        }

        double[] addingPossibilities = new double[keys.length];
        for(int i = 0; i < elimationPossibilities.length; i++) {
            addingPossibilities[i] = calculateAdditionProbability(Population.species.get(keys[i]), totalSpecFitn);
        }

        int maxKills = (int)((rand.nextDouble(AlotOfConstants.minKill, AlotOfConstants.maxKill)) * AlotOfConstants.popSize);

        int actualKills = 0;

        for(int i = 0; i < maxKills; i++) {

            int selected = Statistics.theCollectionPikcerGetInd(elimationPossibilities, false);
            if(killMember(Population.species.get(selected), selected)) actualKills++;

        }

        theReproduction(addingPossibilities, actualKills);

    }

    private static void crossOfDiffSpecies(Specie s1, Specie s2) {

       DNA p1 = getContender(getPossibilities(s1), s1.list);
       DNA p2 = getContender(getPossibilities(s2), s2.list);

       DNA child;
       try {
           child = actualCrossOver(p1, p2);
           Mutation.pickOneOfMutation(child);
           Speciation.putInSpecieByRepresentative(child);
       } catch (CloneNotSupportedException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }

    }

    private static void theReproduction(double[] possiblities, int toAdd) {

        Integer[] keys = Population.species.keySet().toArray(new Integer[0]);

        for(int i = 0; i < toAdd; i++) {

            if(Math.random() > AlotOfConstants.crossSpRate) {
                int selected = Statistics.theCollectionPikcerGetInd(possiblities, false);
            
                if(Population.species.get(selected) != null) {
                    addMember(Population.species.get(selected));
                }
                else {
                    int k = rand.nextInt(keys.length);
                    addMember(Population.species.get(keys[k]));
                }
            }
            else {
                crossOfDiffSpecies(Population.species.get(keys[rand.nextInt(keys.length)]), Population.species.get(keys[rand.nextInt(keys.length)]));
            }

        }

    }

    private static double calculateEliminationProbability(Specie specie, double totalFitness) {
        double sizeFactor = (specie.list.size() / (double) AlotOfConstants.popSize) * sizeWeight;
        double fitnessFactor = (1 - (specie.getAvgFitness() / totalFitness)) * fitnessWeight;
        return (sizeFactor + fitnessFactor) / 2;
    }

    private static double calculateAdditionProbability(Specie specie, double totalFitness) {
        double sizeFactor = 1 - (specie.list.size() / (double) AlotOfConstants.popSize);
        double fitnessFactor = (specie.getAvgFitness() / totalFitness);
        return (sizeFactor + fitnessFactor) / 2;
    }

    private static boolean killMember(Specie s, int key) {

        if(s.age >= AlotOfConstants.spMaturity) {

            Collections.sort(s.list);

            if(s.list.get(0).getFitness() < AlotOfConstants.goodFitnessThreshold) {
                s.list.remove(0);
                if(s.list.isEmpty()) Population.species.remove(key);
                else s.setReprentative();
                return true;
            }
        }

        else {
            freeEvolution(s);
        }

        return false;
 
    }

    private static void addMember(Specie s) {

        double[] possiblities = getPossibilities(s);

        DNA p1 = getContender(possiblities, s.list);
        DNA p2 = getContender(possiblities, s.list);

        DNA child;
        try {
            child = actualCrossOver(p1, p2);
            Mutation.pickOneOfMutation(child);
            Speciation.putInSpecieByRepresentative(child);
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static double[] getPossibilities(Specie s) {

        double sumOfFitn = s.list.stream().mapToDouble(DNA::getFitness).sum();
        double[] possiblities = new double[s.list.size()];

        for(int i = 0; i < possiblities.length; i++) {
            possiblities[i] = s.list.get(i).getFitness() / sumOfFitn;
        }

        return possiblities;
    }

    
    private static void freeEvolution(Specie s) {

        List<DNA> childs = new ArrayList<>();

        double sumOfFitn = s.list.stream().mapToDouble(DNA::getFitness).sum();

        double[] possiblities = new double[s.list.size()];

        for(int i = 0; i < possiblities.length; i++) {
            possiblities[i] = s.list.get(i).getFitness() / sumOfFitn;
        }

        for (int i = 0; i < s.list.size(); i++) {
            DNA p1 = getContender(possiblities, s.list);
            DNA p2 = getContender(possiblities, s.list);

            try {
                DNA child = actualCrossOver(p1, p2);
                Mutation.pickOneOfMutation(child);
                if(Speciation.calculateCompatibilityDistance(child, s.getRepresentative()) <= AlotOfConstants.COMP_THRSHOLD)
                    childs.add(child);
                else {
                    Speciation.putInSpecieByRepresentative(child);
                }
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        s.list = childs;
        s.setReprentative();
    }

    private static DNA actualCrossOver(DNA p1, DNA p2) throws CloneNotSupportedException {

        Collections.sort(p1.c_genes);
        Collections.sort(p2.c_genes);
        Random rand = new Random();

        LinkedList<Connection> newConns = new LinkedList<>();
        LinkedList<Node_N> newNodes = new LinkedList<>();

        Set<Integer> set = new HashSet<>();

        for (Connection c : p1.c_genes) {
            boolean found = false;
            for (Connection c2 : p2.c_genes) {
                if (c.getInnov() == c2.getInnov()) {

                    if(p1.getFitness() > p2.getFitness()) {

                        Connection temp = (Connection)c.clone();
                        if(!Population.checkIfLoopExist(newConns, temp.getIn_id(), temp.getOut_id())) newConns.add(temp);

                        set.add(c.getIn_id());
                        set.add(c.getOut_id());

                    }
                    else {
                        
                        Connection temp = (Connection)c2.clone();
                        if(!Population.checkIfLoopExist(newConns, temp.getIn_id(), temp.getOut_id())) newConns.add(temp);

                        set.add(c2.getIn_id());
                        set.add(c2.getOut_id());

                    }

                    found = true;
                    break;
                }
            }
            
            if (!found) {

                Connection temp = (Connection)c.clone();
                if(!Population.checkIfLoopExist(newConns, temp.getIn_id(), temp.getOut_id())) newConns.add(temp);

                set.add(c.getIn_id());
                set.add(c.getOut_id());
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
            if (!found) {

                Connection temp = (Connection)c2.clone();
                if(!Population.checkIfLoopExist(newConns, temp.getIn_id(), temp.getOut_id())) newConns.add(temp);

                set.add(c2.getIn_id());
                set.add(c2.getOut_id());
            }
            
        }

        for(Integer n : set) {
            //System.out.print(n.getNode_id() + " ");
            if(n <= Population.no_of_inputs) {
                newNodes.add(new Node_N(n, false, true));
            }
            else if(n > Population.no_of_inputs && n <= Population.no_of_inputs + Population.no_of_outputs) {
                newNodes.add(new Node_N(n, true, false));
            }

            else newNodes.add(new Node_N(n, true, true));
            

        }

        DNA theBirth = new DNA(newNodes, newConns);
        Population.DNA_ID++;
        theBirth.id = Population.DNA_ID;

        return theBirth;

    }

    private static DNA getContender(double[] possiblities, List<DNA> members) {

        int id = Statistics.poolSelect(possiblities);
        DNA con1 = members.get(id);
        id = Statistics.poolSelect(possiblities);
        DNA con2 = members.get(id);

        Random rand = new Random();
        
        DNA winner = rand.nextDouble() < 0.5 ? con1 : con2;

        return winner;

    }
    

    
}
