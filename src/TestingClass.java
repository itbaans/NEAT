import NeuralNetwork.Connection;

public class TestingClass {

    public static void main(String[] args) {
        
        Population p = new Population(10);
        //p.printSpecies();
        printDNAs(p.populationDNAs);

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
