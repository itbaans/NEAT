import java.util.Random;

public class Test {
    public static void main(String[] args) {
        
        // Random rand = new Random();

        // //System.out.println(rand.nextDouble() * 2 - 1);
        // double percentage = 25;
        // percentage = rand.nextDouble() * ((percentage + 25) - percentage) + percentage;

        // System.out.println(percentage);

        Population pTest = new Population();

        pTest.testMutatingWieghts();
        //updateAllPossibleConnections();

    }

    private static void updateAllPossibleConnections() {


        int max = 10;
        int no_of_inputs = 3;
        int no_of_outputs = 3;


        for (int i = 1; i <= no_of_inputs; i++) {

            //input connections to all possible nodes except other input nodes
            for (int j = no_of_inputs + 1; j <= max; j++) {

                System.out.println(i+" "+j);

            }

        }

        int min = no_of_inputs + no_of_outputs + 1;

        for (int i = min; i <= max; i++) {

            //hiddent to hidden connections
            for (int j = min; j <= max; j++) {

                if(j != i) {
                    System.out.println(i+" "+j);
                }

            }

            //hidden to output connections
            for (int o = no_of_inputs + 1; o <= no_of_outputs + no_of_inputs; o++) {

                System.out.println(i+" "+o);

            }

        }

    }
}
