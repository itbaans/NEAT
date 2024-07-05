package NEAT_STUFF;
public class AlotOfConstants {

    public static int popSize = 30;
    public static int inputs = 22;
    public static int outputs = 3;

    public static boolean isUNIFORM = false;
    public static double MAX_UNIFORM_CHANGE = 0.05;
    public static double MIN_UNIFORM_CHANGE = -0.05;
    public static double GUASIAN_MEAN = 0;
    public static double GUASIAN_STD = 0.05;
    public static double MAX_SCALING = 1.1;
    public static double MIN_SCALING = 0.9;

    //for sppeciation
    public static double COMP_THRSHOLD = 1.5;

    public static double cForDisjoint = 1;
    public static double cForExcess = 1;
    public static double cForWeights = 0.3;

    //clamping values depends on activation function according to GPT
    //need more research on this.......
    public static double MAX_CLAMP_VALUE = 30;
    public static double MIN_CLAMP_VALUE = -30;

    public static double MAX_BIAS = 15;
    public static double MIN_BIAS = -15;

    public static double probNewNode = 0.2;
    public static double probRemoveNode = 0.2;
    public static double probNewConn = 0.2;
    public static double probRemoveConn = 0.1;
    public static double probChangeWieght = 0.15;
    public static double probChangeBias = 0.15;

    public static float weightsMutationRate = 0.8f;
    public static float weightsResetRate = 0.1f;
    //public static float weightsLearningRate = 0.1f;

    public static int maxNetworkSize = 10;
    public static double percToKill = 0.5;

    private static final double ADJUSTMENT_RATE = 0.02; // How quickly probabilities change
    private static final double MIN_PROB = 0.05; // Minimum probability for any operation
    private static double lastAverageFitness = 0;

    public static void updateProbabilities(double currentAverageFitness) {
        double fitnessChange = currentAverageFitness - lastAverageFitness;
        lastAverageFitness = currentAverageFitness;

        if (fitnessChange > 0) {
            // Fitness is increasing, favor weight and bias changes
            adjustProbabilities(true);
        } else if (fitnessChange < 0) {
            // Fitness is decreasing, favor structural changes
            adjustProbabilities(false);
        }
        // If fitnessChange == 0, we don't change probabilities

        normalizeProbabilities();
    }

    private static void adjustProbabilities(boolean favorWeightChanges) {
        double increase = ADJUSTMENT_RATE;
        double decrease = -ADJUSTMENT_RATE / 2; // Decrease at half the rate of increase

        if (favorWeightChanges) {
            probChangeWieght += increase;
            probChangeBias += increase;
            probNewNode += decrease;
            probNewConn += decrease;
            probRemoveNode += decrease;
            probRemoveConn += decrease;
        } else {
            probNewNode += increase;
            probNewConn += increase;
            probRemoveNode += increase / 2;
            probRemoveConn += increase / 2;
            probChangeWieght += decrease;
            probChangeBias += decrease;
        }
    }

    private static void normalizeProbabilities() {
        double total = probNewNode + probRemoveNode + probNewConn + 
                       probRemoveConn + probChangeWieght + probChangeBias;

        // Ensure no probability goes below MIN_PROB
        probNewNode = Math.max(probNewNode, MIN_PROB);
        probRemoveNode = Math.max(probRemoveNode, MIN_PROB);
        probNewConn = Math.max(probNewConn, MIN_PROB);
        probRemoveConn = Math.max(probRemoveConn, MIN_PROB);
        probChangeWieght = Math.max(probChangeWieght, MIN_PROB);
        probChangeBias = Math.max(probChangeBias, MIN_PROB);

        // Recalculate total after enforcing minimums
        total = probNewNode + probRemoveNode + probNewConn + 
                probRemoveConn + probChangeWieght + probChangeBias;

        // Normalize to ensure sum is 1
        probNewNode /= total;
        probRemoveNode /= total;
        probNewConn /= total;
        probRemoveConn /= total;
        probChangeWieght /= total;
        probChangeBias /= total;
    }



}
