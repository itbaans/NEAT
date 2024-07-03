package NEAT_STUFF;
import java.util.Random;

public class Test {
    public static void main(String[] args) {
        
        // Random rand = new Random();

        // //System.out.println(rand.nextDouble() * 2 - 1);
        // double percentage = 25;
        // percentage = rand.nextDouble() * ((percentage + 25) - percentage) + percentage;

        // System.out.println(percentage);

        // Population pTest = new Population();

        // pTest.testMutatingWieghts();
        // //updateAllPossibleConnections();

        //temp();

        int t = 30;

        double[] ps = {0.1, 0.3, 0.2, 0.2, 0.15, 0.025, 0.015, 0.01};

        double s = 0;

        for(double d : ps) {

            s+=(1 - d) / (ps.length - 1);

        }

        System.out.println(s);

    }

    public static void temp() {

        String[] t = {"testing", "1 2 3"};

        for (String s : t) {

            s = s+"p";
            System.out.println(s);

        }

        for (String s : t) {

            System.out.println(s);

        }
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
