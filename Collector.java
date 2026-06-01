import java.util.ArrayList;

/**
 * Collector.java
 * A Collector seeks out the nearest food item on the grid and moves
 * toward it each turn using greedy Manhattan-distance pathfinding.
 * When it steps onto a food cell, it eats the food.
 *
 * Symbol: C
 * Behavior: Scans all cells for food, finds the closest, moves one step
 *           in the direction that minimizes distance. If no food exists,
 *           wanders randomly.
 */
public class Collector extends Agent {

    public Collector(World world, int[] pos) {
        super(world, pos, 'C', "Collector");
    }

    @Override
    public void act() {
        int[] target = findNearestFood();

        if (target == null) {
            wander(); // no food on the map
            return;
        }

        // Get all legal moves and pick the one closest to the food
        ArrayList<int[]> moves = getLegalMoves();
        if (moves.isEmpty()) return;

        int[] best = null;
        int bestDist = Integer.MAX_VALUE;
        for (int[] move : moves) {
            int d = Math.abs(move[0] - target[0]) + Math.abs(move[1] - target[1]);
            if (d < bestDist) {
                bestDist = d;
                best = move;
            }
        }

        if (best != null) {
            moveTo(best[0], best[1]);

            // Eat food if we just stepped onto it
            if (world.getCell(getRow(), getCol()) == World.FOOD) {
                world.setCell(getRow(), getCol(), World.EMPTY);
                world.incrementFoodEaten();
                System.out.println("  [C] Collector ate food at ("
                    + getRow() + ", " + getCol() + ")");
            }
        }
    }

    /**
     * Scans the entire grid and returns {row, col} of the nearest food,
     * or null if no food exists.
     */
    private int[] findNearestFood() {
        int[] nearest = null;
        int nearestDist = Integer.MAX_VALUE;

        for (int r = 0; r < world.getRows(); r++) {
            for (int c = 0; c < world.getCols(); c++) {
                if (world.getCell(r, c) == World.FOOD) {
                    int d = distanceTo(r, c);
                    if (d < nearestDist) {
                        nearestDist = d;
                        nearest = new int[]{r, c};
                    }
                }
            }
        }
        return nearest;
    }
}
