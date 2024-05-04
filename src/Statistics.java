import java.util.Random;

public class Statistics {

    public static void main(String[] args) {
        double[] percentages = {5, 15, 25, 50, 75, 100};

        for (int i = 0; i < 100; i++) { // Run the test 100 times
            double selectedPercentage = theCollectionPikcer(percentages, true);
            System.out.println("Selected percentage: " + selectedPercentage);
        }
        // double selectedPercentage = theCollectionPikcer(percentages);
        // System.out.println(selectedPercentage);  
    }
    
    public static double theCollectionPikcer(double[] percentages, boolean invrse) {

        double[] probablities = new double[percentages.length];

        double sum = 0;
        for(int i = 0; i < probablities.length; i++) {


            probablities[i] = invrse ? Power(percentages[i] / 100, -2) : Power(percentages[i] / 100, 2);
            sum += probablities[i];

        }

        for(int i = 0; i < probablities.length; i++) {
            probablities[i] = probablities[i] / sum;

        }

        return percentages[poolSelect(probablities)];

    }

    public static int theCollectionPikcerGetInd(double[] percentages, boolean invrse) {

        double[] probablities = new double[percentages.length];

        double sum = 0;
        for(int i = 0; i < probablities.length; i++) {


            probablities[i] = invrse ? Power(percentages[i] / 100, -2) : Power(percentages[i] / 100, 2);
            sum += probablities[i];

        }

        for(int i = 0; i < probablities.length; i++) {
            probablities[i] = probablities[i] / sum;

        }

        return poolSelect(probablities);

    }

    public static int poolSelect(double[] probablities) {

        int ind = 0;
        Random rand = new Random();
        double randomNumber = rand.nextDouble();

        while (randomNumber > 0) {

            randomNumber = randomNumber - probablities[ind];
            ind += 1;

        }
        ind -= 1;
        return ind;


    }

    private static double inverse(double val) {

        return (1 / val);

    }

    private static double Power(double val, int n) {

        return Math.pow(val, n);

    }



}
