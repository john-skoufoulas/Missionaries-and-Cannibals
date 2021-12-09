import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class SpaceSearcher {
    private ArrayList<State> frontier;
    private HashSet<State> closedSet;

    //max number of routes allowed
    private int maxRoutes;

    SpaceSearcher(int K) {
        this.frontier = new ArrayList<>();
        this.closedSet = new HashSet<>();

        this.maxRoutes = K;
    }

    State AStarClosedSet(State initialState) {
        // step 1: put initial state in the frontier.
        this.frontier.add(initialState);

        // step 2: check for empty frontier.
        while (this.frontier.size() > 0) {
            // step 3: get the first node out of the frontier.
            State currentState = this.frontier.remove(0);

            // step 4: if final state, return.
            if (currentState.isFinal()) return currentState;

            // step 5: if the node is not in the closed set, put the children at the frontier.
            // else go to step 2.
            if (!this.closedSet.contains(currentState) && currentState.getRoutesCount() < maxRoutes) {
                this.closedSet.add(currentState);
                this.frontier.addAll(currentState.getChildren());

                // step 6: sort the frontier based on the heuristic score and the current routesCount to get best as first
                Collections.sort(this.frontier); // sort the frontier to get best as first
            }
        }
        return null;
    }
}
