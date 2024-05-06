package Game;

import NeuralNetwork.DNA;
import NeuralNetwork.NueralNetwork;

public interface MadeForNeat {
    
    public abstract double getFitness();
    public abstract void setFitness(double val);
    public abstract void setBrain(DNA dna);

}
