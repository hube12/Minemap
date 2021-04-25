package kaptainwutax.minemap.util.tsp;

/**
 * Represents the ant agent. It process their own state transition rules in the environment.
 */
public class Ant {

    /**
     * Cost of the current path, it is the sum of all edges distances.
     */
    private double tourCost;

    /**
     * Current tour, contains a sequence of edges that was travelled by the ant.
     * Start at the first node and ends at the same node, ex: [A,B,C,D,E,A].
     */
    private int[] tour;

    /**
     * All nodes that were visited by the ant. Are the restrictions of the TSP problem,
     * where an ant must visit all vertices only one time in a travel.
     */
    private boolean[] visited;

    /**
     * Let the information about the edges, vertices and pheromones accessible.
     */
    private Environment environment;

    /**
     * Create an adapted ant to the environment.
     *
     * @param tourSize
     * @param environment
     */
    public Ant(int tourSize, Environment environment) {
        super();
        this.tour = new int[tourSize + 1];
        this.visited = new boolean[tourSize];
        this.environment = environment;
    }

    /**
     * Calculate the travel cost where in each vertex the ant choose the
     * most near neighbor still not visited starting in a random vertex.
     *
     * @return cost
     */
    public double calculateNearestNeighborTour() {
        int phase = 0;
        clearVisited();
        startAtRandomPosition(phase);
        while (phase < environment.getNodesSize() - 1) {
            phase++;
            goToBestNext(phase);
        }
        finishTourCircuit();
        clearVisited();
        return this.tourCost;
    }

    /**
     * Reset the ant to be processed.
     */
    public void clearVisited() {
        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
        }
    }

    /**
     * Put the ant in a random vertex and mark it as visited.
     *
     * @param phase
     */
    public void startAtRandomPosition(int phase) {
        tour[phase] = (int) (Math.random() * environment.getNodesSize());
        visited[tour[phase]] = true;
    }

    /**
     * Move to the neighbor of the current vertex with the smaller distance
     *
     * @param phase
     */
    public void goToBestNext(int phase) {
        // Start considering the last move
        int nextCity = environment.getNodesSize();
        // Take the current city
        int currentCity = tour[phase - 1];
        // Start with a value that an edge never will achieve
        double minDistance = Double.MAX_VALUE;
        // For each non visited vertex, if the cost is lesser than minDistance select it
        for (int city = 0; city < environment.getNodesSize(); city++) {
            if (!visited[city] && environment.getCost(currentCity, city) < minDistance) {
                nextCity = city;
                minDistance = environment.getCost(currentCity, city);
            }
        }
        // Move to the next city
        tour[phase] = nextCity;
        visited[nextCity] = true;
    }

    /**
     * Sum all the (i->i+1) edges cost of the tour. The last vertex (n - 1)
     * reconnect with the first vertex (n), where vertex n is equal to vertex 0.
     *
     * @return tourCost
     */
    public double computeTourCost() {
        double tourCost = 0.0;
        for (int i = 0; i < environment.getNodesSize(); i++) {
            tourCost += environment.getCost(tour[i], tour[i + 1]);
        }
        return tourCost;
    }

    /**
     * Finish the ant circuit, bind the last vertex with first vertex (last vertex
     * n is equal to vertex 0) and calculate the route cost.
     */
    public void finishTourCircuit() {
        tour[environment.getNodesSize()] = tour[0];
        tourCost = computeTourCost();
    }

    /**
     * Calculate next neighbor tending probabilistically selecting the edge
     * with more pheromone and smaller distance. First is tried to find
     * a vector to move in the nearest neighbor list, but if all vectors were visited
     * then is selected the best in all remaining neighbors
     *
     * @param phase
     */
    public void goToNNListAsDecisionRule(int phase) {
        // Get the current city
        int currentCity = this.tour[phase - 1];
        double sumProbabilities = 0.0;
        // Vector of nearest neighbor probabilities proportional to the fitness of the edge
        double[] selectionProbabilities = new double[environment.getNNSize() + 1];
        // For each nearest neighbor vertex that was not visited yet add their fitness to the
        // probability vector
        for (int j = 0; j < environment.getNNSize(); j++) {
            if (visited[environment.getNNNode(currentCity, j)]) {
                selectionProbabilities[j] = 0.0;
            } else {
                selectionProbabilities[j] = environment.getCostInfo(currentCity, environment.getNNNode(currentCity, j));
                sumProbabilities += selectionProbabilities[j];
            }
        }
        if (sumProbabilities <= 0) {
            // If all nearest neighbor were visited select on best in the remaining neighbors
            goToBestNext(phase);
        } else {
            // Take a random value proportional to the sum of probabilities
            double rand = Math.random() * sumProbabilities;
            int j = 0;
            double probability = selectionProbabilities[j];
            // Selected the neighbor correspondent to the random proportional probability
            while (probability <= rand && j < selectionProbabilities.length - 1) {
                j++;
                probability += selectionProbabilities[j];
            }
            // If has problem with double round occurred
            if (j == environment.getNNSize()) {
                // Select the best neighbor
                goToBestNeighbor(phase);
                return;
            }
            // Visit the selected neighbor
            tour[phase] = environment.getNNNode(currentCity, j);
            visited[this.tour[phase]] = true;
        }
    }

    /**
     * Select the best non visited neighbor of the current vertex (the best neighbor
     * have the greater fitness calculated from pheromone and heuristic).
     *
     * @param phase
     */
    public void goToBestNeighbor(int phase) {
        int helpCity;
        int nextCity = environment.getNodesSize();
        // Take the current city
        int currentCity = this.tour[phase - 1];
        // Start the best with a value that never will be achieved
        double valueBest = -1.0;
        double help;
        // Select the non visited neighbor with the maximum fitness
        for (int i = 0; i < environment.getNNSize(); i++) {
            helpCity = environment.getNNNode(currentCity, i);
            if (!this.visited[helpCity]) {
                help = environment.getCostInfo(currentCity, helpCity);
                if (help > valueBest) {
                    valueBest = help;
                    nextCity = helpCity;
                }
            }
        }
        if (nextCity == environment.getNodesSize()) {
            // If was not found a vertex at the nearest neighbor list of the current vector
            goToBestNext(phase);
        } else {
            // Move to the vertex
            tour[phase] = nextCity;
            visited[this.tour[phase]] = true;
        }
    }

    /**
     * Return the cost of the current tour
     *
     * @return tourCost
     */
    public double getTourCost() {
        return tourCost;
    }

    /**
     * Return the vertex for a specific phase of the travel
     *
     * @param phase
     * @return vertex
     */
    public int getRoutePhase(int phase) {
        return tour[phase];
    }

    /**
     * Return the current tour
     *
     * @return tour
     */
    public int[] getTour() {
        return tour;
    }

}
