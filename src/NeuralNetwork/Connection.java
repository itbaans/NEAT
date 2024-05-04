package NeuralNetwork;
public class Connection implements Comparable<Connection>, Cloneable {
    
    int in_id;
    int out_id;
    double wieght;
    boolean isEnabled;
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Connection [in_id=" + in_id + ", out_id=" + out_id + ", wieght=" + wieght + ", isEnabled=" + isEnabled
                + ", innov=" + innov + "]";
    }

    int innov;

    public int getInnov() {
        return innov;
    }

    public Connection(int in_id, int out_id, double wieght, boolean isEnabled, int innov) {
        
        this.in_id = in_id;
        this.out_id = out_id;
        this.wieght = wieght;
        this.isEnabled = isEnabled;
        this.innov = innov;
    }

    public void setWieght(double wieght) {
        this.wieght = wieght;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public int getIn_id() {
        return in_id;
    }

    public int getOut_id() {
        return out_id;
    }

    public double getWieght() {
        return wieght;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public int compareTo(Connection o) {
        return Integer.compare(this.innov, o.innov);
    }

    

}
