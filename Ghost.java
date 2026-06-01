import java.util.ArrayList;

/**
 * Ghost.java
 * A Ghost can move through walls. It wanders the grid opportunistically,
 * collecting food whenever it steps on it. Because it ignores walls,
 * it can reach food that is completely enclosed.
 *
 * Symbol: G
 * Behavior: If food is adjacent (even behind a wall), moves there.
 *           Otherwise moves randomly through any non-agent cell.
 *           Cannot share a cell with another living agent.
 *
 * Creative element: Phases through walls — uses canMoveToIgnoreWalls()
 * from the Agent superclass instead of canMoveTo().
 */
public class Ghost extends Agent {

    public Ghost(World world, int[] pos) {
        super(world, pos, 'G', "Ghost");
    }

    @Override
    public void act() {
        // Collect all moves ignoring walls
        ArrayList<int[]> moves = getGhostMoves();
        if (moves.isEmpty()) return;

        // Prefer a move onto food if one is available
        for (int[] move : moves) {
            if (world.getCell(move[0], move[1]) == World.FOOD) {
                // Clear the food BEFORE moving so setCell uses the correct coords
                world.setCell(move[0], move[1], World.EMPTY);
                world.incrementFoodEaten();
                moveTo(move[0], move[1]);
                System.out.println("  [G] Ghost phased through and collected food at ("
                    + getRow() + ", " + getCol() + ")");
                return;
            }
        }

        // Otherwise move randomly
        int[] chosen = moves.get((int)(Math.random() * moves.size()));
        moveTo(chosen[0], chosen[1]);
    }

    /**
     * Returns all neighboring cells the Ghost can enter.
     * Walls are permitted; occupied cells are not.
     */
    private ArrayList<int[]> getGhostMoves() {
        ArrayList<int[]> moves = new ArrayList<>();
        for (int[] d : DIRECTIONS) {
            int nr = getRow() + d[0];
            int nc = getCol() + d[1];
            if (canMoveToIgnoreWalls(nr, nc)) {
                moves.add(new int[]{nr, nc});
            }
        }
        return moves;
    }
}
