import java.util.ArrayList;
import java.util.List;

import NeuralNetwork.DNA;

public class Specie {

    List<DNA> list = new ArrayList<>();
    
    public DNA getRepresentative() {
        return representative;
    }

    DNA representative;
    double avgFitness;

    public void setReprentative() {
        if(!list.isEmpty()) representative = list.get(0);
    }

    public double getAvgFitness() {
        return avgFitness;
    }

    public void setAvgFitness(double avgFitness) {
        this.avgFitness = avgFitness;
    }


}