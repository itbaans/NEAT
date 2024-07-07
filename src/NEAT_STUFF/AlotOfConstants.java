package NEAT_STUFF;

import TheGame.BotServival;
import TheGame.GameConstants;

public class AlotOfConstants {

    public static int popSize = 200;
    public static int inputs = 23;
    public static int outputs = 3;
    public static int generations = 200;

    public static double goodFitnessThreshold = 1000;

    public static boolean isUNIFORM = true;
    public static double MAX_UNIFORM_CHANGE = 0.05;
    public static double MIN_UNIFORM_CHANGE = -0.05;
    public static double GUASIAN_MEAN = 0;
    public static double GUASIAN_STD = 0.05;
    public static double MAX_SCALING = 1.1;
    public static double MIN_SCALING = 0.9;

    //for sppeciation
    public static double COMP_THRSHOLD = 2;

    public static double cForDisjoint = 2;
    public static double cForExcess = 2;
    public static double cForWeights = 0.7;

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

    public static int spMaturity = 10;

    public static float crossSpRate = 0.2f;

    public static float weightsMutationRate = 0.8f;
    public static float weightsResetRate = 0.1f;
    //public static float weightsLearningRate = 0.1f;

    public static int maxNetworkSize = 10;
    public static double maxKill = 0.5;
    public static double minKill = 0.3;

    private static final double ADJUSTMENT_RATE = 0.02; // How quickly probabilities change
    private static final double MIN_PROB = 0.05; // Minimum probability for any operation
    private static double lastAverageFitness = 0;

    private static final double MIN_THRESHOLD = 1.5;
    private static final double MAX_THRESHOLD = 3.0;
    private static final double SP_ADJUSTMENT_RATE = 0.1;
    private static final int TARGET_SPECIES = 8;

    public static void updateProbabilities(double currentAverageFitness) {
        double fitnessChange = currentAverageFitness - lastAverageFitness;
        lastAverageFitness = currentAverageFitness;

        if(BotServival.currentGen / GameConstants.generations < 0.5 && Population.getAvgFitness() < 1000) {
            adjustProbabilities(false);
        }

        else {
            if (fitnessChange > 0) {
                // Fitness is increasing, favor weight and bias changes
                adjustProbabilities(true);
            } else if (fitnessChange < 0) {
                // Fitness is decreasing, favor structural changes
                adjustProbabilities(false);
            }
            // If fitnessChange == 0, we don't change probabilities
        }

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

    public static void adjustSpeciationThreshold(int speciesCount) {

        if (speciesCount > TARGET_SPECIES) {
            COMP_THRSHOLD += SP_ADJUSTMENT_RATE;
        } else if (speciesCount < TARGET_SPECIES) {
            COMP_THRSHOLD -= SP_ADJUSTMENT_RATE;
        }
        
        // Ensure threshold stays within bounds
        COMP_THRSHOLD = Math.max(MIN_THRESHOLD, Math.min(MAX_THRESHOLD, COMP_THRSHOLD));
    }




}
