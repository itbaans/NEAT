package NEAT_STUFF;
import NeuralNetwork.Connection;
import NeuralNetwork.DNA;
import NeuralNetwork.NueralNetwork;

public class TestingClass {

    public static void main(String[] args) throws CloneNotSupportedException {
        
        
        Population p = new Population(150, 2, 1);
        
        p.XORtest();



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
