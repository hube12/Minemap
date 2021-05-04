package kaptainwutax.minemap.util.tsp;

import com.seedfinding.latticg.util.Pair;
import kaptainwutax.mcutils.util.pos.BPos;

import java.util.List;

public class Program {

    public Pair<Double, List<Integer>> startApplication(List<BPos> bPosList) {
        if (bPosList.size() < 10) {
            Parameters.NNSize = bPosList.size() / 2;
        } else {
            Parameters.NNSize = 10;
        }
        Environment environment = new Environment(TspReader.getDistances(bPosList));
        Statistics statistics = new Statistics(environment);
        environment.generateNearestNeighborList();
        environment.generateAntPopulation();
        environment.generateEnvironment();
        int n = 0;
        while (n < Parameters.iterationsMax) {
            environment.constructSolutions();
            environment.updatePheromone();

            n++;
        }
        return statistics.calculateStatistics();
    }

}
