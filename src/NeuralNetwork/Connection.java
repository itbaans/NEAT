package NeuralNetwork;
public class Connection {
    
    int in_id;
    int out_id;
    double wieght;
    boolean isEnabled;
    int innov;

    public Connection(int in_id, int out_id, double wieght, boolean isEnabled, int innov) {
        
        this.in_id = in_id;
        this.out_id = out_id;
        this.wieght = wieght;
        this.isEnabled = isEnabled;
        this.innov = innov;
    }

    

}
