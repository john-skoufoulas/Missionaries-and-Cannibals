import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter Number of Cannibals/Missionaries: ");
        int N = input.nextInt();
        System.out.println("Enter Capacity of Boat (>=2): ");
        int M = input.nextInt();
        System.out.println("Enter Max Routes Allowed: ");
        int K = input.nextInt();

        input.close();

        if (M < 2) {
            System.err.println("Invalid Boat Capacity");
            System.exit(1);
        }

        State initialState = new State(N, M, K); // Initial state
        SpaceSearcher searcher = new SpaceSearcher(K);

        long start = System.currentTimeMillis();
        State terminalState = searcher.AStarClosedSet(initialState);
        long end = System.currentTimeMillis();
        if(terminalState == null) System.out.println("Could not find a solution.");
        else
        {
			// Print the path from beginning to start
            State temp = terminalState; // Begin from the end
            ArrayList<State> path = new ArrayList<>();
			path.add(terminalState);

			// If father is null, then we are at the root
            while (temp.getParentState() != null) {
                path.add(temp.getParentState());
                temp = temp.getParentState();
            }

			// Reverse the path and print
            Collections.reverse(path);
            for (State item: path) {
                item.print();
            }

            System.out.println("Number of routes: " + terminalState.getRoutesCount());
            System.out.println();

            // Total time of searching in seconds
            System.out.println("Search time: " + (double)(end - start) / 1000 + " sec.");
        }
    }
}