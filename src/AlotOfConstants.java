public class AlotOfConstants {
    
    public static boolean isUNIFORM = true;
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

    public static double MAX_BIAS = 30;
    public static double MIN_BIAS = -30;

    public static double probNewNode = 0.2;
    public static double probRemoveNode = 0.2;
    public static double probNewConn = 0.2;
    public static double probRemoveConn = 0.1;
    public static double probChangeWieght = 0.15;
    public static double probChangeBias = 0.15;

    public static float weightsMutationRate = 0.8f;
    public static float weightsResetRate = 0.1f;
    //public static float weightsLearningRate = 0.1f;

    public static int maxNetworkSize = 5;


    public static double percToKill = 0.5;




}
