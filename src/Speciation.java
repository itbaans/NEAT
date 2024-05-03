import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import NeuralNetwork.Connection;

public class Speciation {

    //start of speciation
    public static void speciate(DNA[] populationDNAs) {

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

        // for (Map.Entry<DNA, Map<DNA, Double>> entry : distances.entrySet()) {
        //     DNA dna1 = entry.getKey();
        //     Map<DNA, Double> innerMap = entry.getValue();
        //     for (Map.Entry<DNA, Double> innerEntry : innerMap.entrySet()) {
        //       DNA dna2 = innerEntry.getKey();
        //       Double distance = innerEntry.getValue();
        //       System.out.println("Distance between " + dna1.id + " and " + dna2.id + ": " + distance);
        //     }
        // }

        for (int i = 0; i < populationDNAs.length; i++) {
            if(!isAlreadyInSpecies(populationDNAs[i])) {
                putInSpecie(distances, populationDNAs[i]);
            }
        }

    }



    public static void putInSpecie(Map<DNA, Map<DNA, Double>> distances, DNA individual) {

        boolean gotIt = false;

        for (Map.Entry<DNA, Map<DNA, Double>> entry : distances.entrySet()) {
            DNA otherIndividual = entry.getKey();
            if (otherIndividual != individual) {
                double distance = distances.get(individual).get(otherIndividual);
                if (distance < AlotOfConstants.COMP_THRSHOLD) {
                    gotIt = true;
                    if(isAlreadyInSpecies(otherIndividual)) {
                        Integer key = getSpecieKey(otherIndividual);
                        Population.species.get(key).list.add(individual);
                    }
                    else {
                        Population.SPECIE_ID++;
                        Specie sp = new Specie();
                        sp.list.add(otherIndividual);
                        sp.list.add(individual);
                        sp.setReprentative();
                        Population.species.put(Population.SPECIE_ID, sp);
                    }
                    return;

                }
            }
        }

        if(!gotIt) {
            Population.SPECIE_ID++;
            Specie sp = new Specie();
            sp.list.add(individual);
            sp.setReprentative();
            Population.species.put(Population.SPECIE_ID, sp);

        }

    }

    public static boolean isAlreadyInSpecies(DNA dna) {

        Set<Integer> keys = Population.species.keySet();

        for(Integer i : keys) {

            if(Population.species.get(i).list.contains(dna)) return true;

        }

        return false;
    }

    public static int getSpecieKey(DNA dna) {

        Set<Integer> keys = Population.species.keySet();

        for(Integer i : keys) {

            if(Population.species.get(i).list.contains(dna)) return i;

        }

        return -1;
    }

    public static double calculateCompatibilityDistance(DNA dna1, DNA dna2) {

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

            if(c[0] == null && c[1] != null) {
                sumOfWeightDiffs += c[1].getWieght();
                if(Population.isDisjointConnection(dna1, c[1].getIn_id(), c[1].getOut_id())) disJoints++;
                else excess++;
            }

            if(c[1] == null && c[0] != null) {
                sumOfWeightDiffs += c[0].getWieght();
                if(Population.isDisjointConnection(dna2, c[0].getIn_id(), c[0].getOut_id())) disJoints++;
                else excess++;
            }

        }

        double avgWeightDiffs = sumOfWeightDiffs / (longerLength + shorterLength);

        double d = (AlotOfConstants.cForDisjoint * disJoints) / longerLength;
        double e = (AlotOfConstants.cForExcess * excess) / longerLength;
        double w = AlotOfConstants.cForWeights * avgWeightDiffs;

        return d + e + w;
        
    }

    public static void printSpecies() {
        for (Map.Entry<Integer, Specie> entry : Population.species.entrySet()) {
          Integer speciesId = entry.getKey();
          Specie specie = entry.getValue();
          System.out.println("Specie " + speciesId + ":");
          System.out.println("  Members:");
          for (DNA dna : specie.list) {
            System.out.println("    " + dna.id);
          }
        }
    }

    



    
}
