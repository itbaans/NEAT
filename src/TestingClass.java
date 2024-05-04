import NeuralNetwork.Connection;
import NeuralNetwork.DNA;

public class TestingClass {

    public static void main(String[] args) throws CloneNotSupportedException {
        
        Population p = new Population(10);
        //Speciation.printSpecies();
        //printDNAs(Population.populationDNAs);

    }
    
    public static void printDNAs(DNA[] dnas) {

        for(DNA dna : dnas) {

            System.out.println("DNA ID: "+dna.id);

            for(Connection c : dna.c_genes) {

                System.out.print(" ["+c.getIn_id()+" ,"+c.getOut_id()+"], Innov: ("+c.getInnov()+") ");
                
            }

            System.out.println();

        }

    }
    





}
