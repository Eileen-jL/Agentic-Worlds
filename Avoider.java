import java.util.ArrayList;

/**
 * Avoider.java
 * An Avoider actively flees from the nearest Predator.
 * Each turn it picks the legal move that maximizes its distance
 * from the closest threat.
 *
 * Symbol: A
 * Behavior: Finds the nearest Predator, then moves to the adjacent
 *           cell that is farthest from it. If no Predators exist,
 *           it wanders randomly.
 */
public class Avoider extends Agent {

    public Avoider(World world, int[] pos) {
        super(world, pos, 'A', "Avoider");
    }

    @Override
    public void act() {
        Agent threat = findNearestPredator();

        if (threat == null) {
            wander();
            return;
        }

        ArrayList<int[]> moves = getLegalMoves();
        if (moves.isEmpty()) return;

        // Pick the move that maximizes distance from the threat
        int[] best = null;
        int bestDist = -1;
        for (int[] move : moves) {
            int d = Math.abs(move[0] - threat.getRow())
                  + Math.abs(move[1] - threat.getCol());
            if (d > bestDist) {
                bestDist = d;
                best = move;
            }
        }

        if (best != null) {
            moveTo(best[0], best[1]);
        }
    }

    /** Returns the nearest living Predator, or null if none exist. */
    private Agent findNearestPredator() {
        Agent nearest = null;
        int nearestDist = Integer.MAX_VALUE;

        for (Agent a : world.getLivingAgents()) {
            if (!a.getType().equals("Predator")) continue;
            int d = distanceTo(a);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = a;
            }
        }
        return nearest;
    }
}
