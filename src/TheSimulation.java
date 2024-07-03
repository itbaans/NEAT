import NEAT_STUFF.AlotOfConstants;
import NEAT_STUFF.Population;
import TheGame.BotServival;

public class TheSimulation {
    
    Population population = new Population(AlotOfConstants.popSize, AlotOfConstants.inputs, AlotOfConstants.outputs);
    BotServival game = new BotServival(AlotOfConstants.popSize);

    public void simulate() {

        // game.setPlayerBrains(Population.populationDNAs);
        // game.play();

    }





}
