public class AlotOfConstants {
    
    public static boolean isUNIFORM = true;
    public static double MAX_UNIFORM_CHANGE = 0.05;
    public static double MIN_UNIFORM_CHANGE = -0.05;
    public static double GUASIAN_MEAN = 0;
    public static double GUASIAN_STD = 0.05;
    public static double MAX_SCALING = 1.1;
    public static double MIN_SCALING = 0.9;

    //clamping values depends on activation function according to GPT
    //need more research on this.......
    public static double MAX_CLAMP_VALUE = 1;
    public static double MIN_CLAMP_VALUE = -1;

    public static double probNewNode = 0.2;
    public static double probRemoveNode = 0.05;
    public static double probNewConn = 0.2;
    public static double probRemoveConn = 0.05;
    public static double probChangeWieght = 0.5;



}