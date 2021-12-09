import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

enum Position {RIGHT, LEFT}

public class State implements Comparable<State>
{
    private int cannibalsLeft;
    private int cannibalsRight;
    private int missionariesLeft;
    private int missionariesRight;
    private Position boatPosition;
    private int routesCount;

    private int N; // Number of Cannibals/Missionaries
    private int M; // Boat capacity
    private int K; // Max number of routes

    // Heuristic score
    private int score;

    private State father = null;

    State(int N, int M, int K) {
        this.N = N;
        this.M = M;
        this.K = K;

        routesCount = 0;
        cannibalsLeft = N;
        missionariesLeft = N;
        boatPosition = Position.LEFT;
        cannibalsRight = 0;
        missionariesRight = 0;
    }


    // Constructor for creating copy of the state.
    State(State father) {
        this.N = father.N;
        this.M = father.M;
        this.K = father.K;
        this.cannibalsLeft = father.cannibalsLeft;
        this.cannibalsRight = father.cannibalsRight;
        this.missionariesLeft = father.missionariesLeft;
        this.missionariesRight = father.missionariesRight;
        this.boatPosition = father.boatPosition;
        this.routesCount = father.routesCount;
        this.father = father;
    }

    // Getters & Setters
    public int getCannibalLeft() {
        return cannibalsLeft;
    }

    public void setCannibalLeft(int cannibalLeft) {
        this.cannibalsLeft = cannibalLeft;
    }

    public int getMissionaryLeft() {
        return missionariesLeft;
    }

    public void setMissionaryLeft(int missionaryLeft) {
        this.missionariesLeft = missionaryLeft;
    }

    public int getCannibalRight() {
        return cannibalsRight;
    }

    public void setCannibalRight(int cannibalRight) {
        this.cannibalsRight = cannibalRight;
    }

    public int getMissionaryRight() {
        return missionariesRight;
    }

    public void setMissionaryRight(int missionaryRight) {
        this.missionariesRight = missionaryRight;
    }

    public void setBoatPosition(Position pos) {this.boatPosition = pos;}

    public int getRoutesCount() { return this.routesCount; }

    public void incrementRoutesCount() { this.routesCount++; }

    public State getParentState() {
        return father;
    }

    public Position getBoatPosition() {
        return this.boatPosition;
    }

    public void setParentState(State parentState) {
        this.father = parentState;
    }

    // State Functions

    // Checks constraints for State validity.
    // Returns true if the state is valid, else false.
    private boolean constraints (int m, int c, Position p) {
        // Boat constraint
        // Check if any Missionaries will board
        if (m == 0) {
            // At least one Cannibal or <= boat capacity Cannibals can board
            if (c <= 0 || c > M) {
                return false;
            }
        }
        else {
            // Missionaries & Cannibals that board must fit in the boat
            // and Cannibals must be less than or equal to the Missionaries
            if (m + c > M || m < c) {
                return false;
            }
        }

        // Initial constraint
        if (p == Position.LEFT) {
            // Check if there are any Missionaries left on this side (left)
            if (missionariesLeft - m != 0) {
                // Remaining Cannibals must be less than or equal
                // to the remaining Missionaries
                if (cannibalsLeft - c > missionariesLeft - m) {
                    return false;
                }
            }
        }
        else {
            // Check if there are any Missionaries left on this side (right)
            if (missionariesRight - m != 0) {
                // Remaining Cannibals must be less than or equal
                // to the remaining Missionaries
                if (cannibalsRight - c > missionariesRight - m) {
                    return false;
                }
            }
        }

        // Final constraint
        if (p == Position.LEFT) {
            // Check the other side (right) to see if there
            // will be Missionaries there after the boat arrives
            if (missionariesRight + m != 0) {
                // Existing + arriving Cannibals must be less than or equal
                // to the existing + arriving Missionaries
                if (cannibalsRight + c > missionariesRight + m) {
                    return false;
                }
            }
        }
        else {
            // Check the other side (left) to see if there
            // will be Missionaries there after the boat arrives
            if (missionariesLeft + m != 0) {
                // Existing + arriving Cannibals must be less than or equal
                // to the existing + arriving Missionaries
                if (cannibalsLeft + c > missionariesLeft + m) {
                    return false;
                }
            }
        }
        return true;
    }

    // Function that returns an ArrayList with all the valid descendant States
    ArrayList<State> getChildren() {
        ArrayList<State> children = new ArrayList<>();

        if (boatPosition == Position.LEFT) {
            for (int i = 0; i <= missionariesLeft; i++) {
                for (int j = 0; j <= cannibalsLeft; j++) {
                    // very important to create a copy of current state before each move.
                    State child = new State(this);

                    // If the new child is a valid state, calculate its attributes
                    // and add it to the ArrayList
                    if (constraints(i, j, boatPosition)) {
                        child.setCannibalLeft(cannibalsLeft - j);
                        child.setCannibalRight(cannibalsRight + j);
                        child.setMissionaryLeft(missionariesLeft - i);
                        child.setMissionaryRight(missionariesRight + i);
                        child.setBoatPosition(Position.RIGHT);
                        child.incrementRoutesCount();
                        child.heuristic();
                        children.add(child);
                    }
                }
            }
        }
        else if (boatPosition == Position.RIGHT) {
            for (int i = 0; i <= missionariesRight; i++) {
                for (int j = 0; j <= cannibalsRight; j++) {
                    // very important to create a copy of current state before each move.
                    State child = new State(this);

                    // If the new child is a valid state, calculate its attributes
                    // and add it to the ArrayList
                    if (constraints(i, j, boatPosition)) {
                        child.setCannibalLeft(cannibalsLeft + j);
                        child.setCannibalRight(cannibalsRight - j);
                        child.setMissionaryLeft(missionariesLeft + i);
                        child.setMissionaryRight(missionariesRight - i);
                        child.setBoatPosition(Position.LEFT);
                        child.incrementRoutesCount();
                        child.heuristic();
                        children.add(child);
                    }
                }
            }
        }
        return children;
    }

    // Heuristic Function
    // Calculates a score which indicates the minimu number of routes
    // needed to reach the final state.
    //
    // Doesn't take into account the "Missionaries >= Cannibals" constraint
    private void heuristic() {
        int sum = missionariesLeft + cannibalsLeft;
        if (sum % (M-1) > 1){
            score++;
        }
        else if (sum % (M-1) < 1) {
            score--;
        }
        if (boatPosition == Position.RIGHT) {
            score++;
        }
    }

    // Checks if given State is the final state
    public boolean isFinal() {
        // Final State: everyone has moved to the right side (destination)
        if (cannibalsLeft == 0 && missionariesLeft == 0) {
            return true;
        }
        return false;
	}

    // Overridden Function.
    // Returns true if the two state share the exact same attributes
    @Override
    public boolean equals(Object obj) {
        if(this.cannibalsRight != ((State)obj).cannibalsRight) return false;
        if(this.cannibalsLeft != ((State)obj).cannibalsLeft) return false;
        if(this.missionariesLeft != ((State)obj).missionariesLeft) return false;
        if(this.missionariesRight != ((State)obj).missionariesRight) return false;
        if(this.boatPosition != ((State)obj).boatPosition) return false;

        return true;
    }

    // Overridden Function.
    // Hashing Function
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + this.missionariesLeft;
        hash = 61 * hash + this.missionariesRight;
        hash = 61 * hash + this.cannibalsLeft;
        hash = 61 * hash + this.cannibalsRight;
        if (boatPosition == Position.LEFT) {
            hash = 61 * hash + 7;
        }
        else {
            hash = 61 * hash + 11;
        }

        return hash;
    }

    // Overridden Function.
    // Compares the score (calculated by heuristic) of each state,
    // as well as the current distance from the root.
    @Override
    public int compareTo(State s) {
        return Double.compare(this.score + this.routesCount, s.score + s.routesCount);
    }

    // Function that presents the State visually
    void print() {
        System.out.println("-------------------------------------");

        System.out.println("Left side -> C: " + cannibalsLeft + " | M: " + missionariesLeft);
        if(boatPosition == Position.LEFT) System.out.println("\nBoat is Left\n");
        else System.out.println("\nBoat is Right\n");
        System.out.println("Right side -> C: " + cannibalsRight + " | M: " + missionariesRight);

        System.out.println("-------------------------------------");
    }
}
