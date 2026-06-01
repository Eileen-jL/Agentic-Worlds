import java.util.ArrayList;

/**
 * Predator.java
 * A Predator hunts the nearest non-Predator agent each turn.
 * It moves one step toward its target, and if it steps onto a cell
 * occupied by another agent, that agent is eliminated.
 *
 * Symbol: P
 * Behavior: Finds the closest living prey, moves greedily toward it.
 *           If it occupies the same cell as prey, the prey dies.
 *           If no prey exists, it wanders randomly.
 */
public class Predator extends Agent {

    public Predator(World world, int[] pos) {
        super(world, pos, 'P', "Predator");
    }

    @Override
    public void act() {
        Agent prey = findNearestPrey();

        if (prey == null) {
            wander();
            return;
        }

        // Gather moves — predator can step onto a prey's cell
        ArrayList<int[]> candidates = new ArrayList<>();
        for (int[] d : DIRECTIONS) {
            int nr = getRow() + d[0];
            int nc = getCol() + d[1];
            if (world.isWalkable(nr, nc)) {
                // Allow stepping onto a prey's cell
                Agent occupant = world.getAgentAt(nr, nc);
                if (occupant == null || occupant == prey) {
                    candidates.add(new int[]{nr, nc});
                }
            }
        }

        if (candidates.isEmpty()) return;

        // Move to the candidate closest to the prey
        int[] best = null;
        int bestDist = Integer.MAX_VALUE;
        for (int[] move : candidates) {
            int d = Math.abs(move[0] - prey.getRow()) + Math.abs(move[1] - prey.getCol());
            if (d < bestDist) {
                bestDist = d;
                best = move;
            }
        }

        if (best != null) {
            // Check BEFORE moving whether we're stepping onto the prey's cell
            boolean killOnArrival = (best[0] == prey.getRow() && best[1] == prey.getCol());
            moveTo(best[0], best[1]);

            if (killOnArrival && prey.isAlive()) {
                prey.die();
                world.incrementKills();
                System.out.println("  [P] Predator eliminated " + prey.getType()
                    + " at (" + getRow() + ", " + getCol() + ")!");
            }
        }
    }

    /**
     * Returns the nearest living non-Predator agent, or null if none.
     */
    private Agent findNearestPrey() {
        Agent nearest = null;
        int nearestDist = Integer.MAX_VALUE;

        for (Agent a : world.getLivingAgents()) {
            if (a == this || a.getType().equals("Predator")) continue;
            int d = distanceTo(a);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = a;
            }
        }
        return nearest;
    }
}
