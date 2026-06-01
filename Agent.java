import java.util.ArrayList;

/**
 * Agent.java
 * Abstract superclass for all agents in the Agentic World simulation.
 *
 * Demonstrates:
 *   - Abstract class with abstract method (act)
 *   - Encapsulation (private fields, public getters)
 *   - Inheritance base for all agent subclasses
 */
public abstract class Agent {

    private int row;
    private int col;
    private char symbol;
    private String type;
    private boolean alive;
    protected World world;

    /**
     * Constructs an Agent at the given position.
     * @param world   the simulation world
     * @param pos     int[] {row, col} starting position
     * @param symbol  single character displayed on the grid
     * @param type    human-readable agent type name
     */
    public Agent(World world, int[] pos, char symbol, String type) {
        this.world  = world;
        this.row    = pos[0];
        this.col    = pos[1];
        this.symbol = symbol;
        this.type   = type;
        this.alive  = true;
    }

    // ---------------------------------------------------------------
    // Abstract method â every subclass must define its own behavior
    // ---------------------------------------------------------------

    /**
     * Executes this agent's behavior for one turn.
     * Subclasses implement their unique decision-making logic here.
     */
    public abstract void act();

    // ---------------------------------------------------------------
    // Shared movement helpers (available to all subclasses)
    // ---------------------------------------------------------------

    /** The four cardinal directions: up, down, left, right. */
    protected static final int[][] DIRECTIONS = {{-1,0},{1,0},{0,-1},{0,1}};

    /**
     * Moves this agent to (newRow, newCol).
     * Does not check legality â call canMoveTo() first.
     */
    protected void moveTo(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }

    /**
     * Returns true if the agent can legally step onto (r, c):
     * in bounds, not a wall, and no other living agent already there.
     */
    protected boolean canMoveTo(int r, int c) {
        return world.inBounds(r, c)
            && world.getCell(r, c) != World.WALL
            && world.getAgentAt(r, c) == null;
    }

    /**
     * Same as canMoveTo but allows walking through walls.
     * Used by the Ghost agent.
     */
    protected boolean canMoveToIgnoreWalls(int r, int c) {
        return world.inBounds(r, c)
            && world.getAgentAt(r, c) == null;
    }

    /**
     * Moves one step in a random legal direction.
     * Used as a fallback when no smarter move is available.
     */
    protected void wander() {
        ArrayList<int[]> moves = getLegalMoves();
        if (!moves.isEmpty()) {
            int[] chosen = moves.get((int)(Math.random() * moves.size()));
            moveTo(chosen[0], chosen[1]);
        }
    }

    /** Returns all legal moves from the current position. */
    protected ArrayList<int[]> getLegalMoves() {
        ArrayList<int[]> moves = new ArrayList<>();
        for (int[] d : DIRECTIONS) {
            int nr = row + d[0], nc = col + d[1];
            if (canMoveTo(nr, nc)) moves.add(new int[]{nr, nc});
        }
        return moves;
    }

    /**
     * Manhattan distance from this agent to position (tr, tc).
     */
    protected int distanceTo(int tr, int tc) {
        return Math.abs(row - tr) + Math.abs(col - tc);
    }

    /**
     * Manhattan distance from this agent to another agent.
     */
    protected int distanceTo(Agent other) {
        return distanceTo(other.row, other.col);
    }

    // ---------------------------------------------------------------
    // Getters and setters
    // ---------------------------------------------------------------

    public int     getRow()    { return row; }
    public int     getCol()    { return col; }
    public char    getSymbol() { return symbol; }
    public String  getType()   { return type; }
    public boolean isAlive()   { return alive; }

    public void die() {
        this.alive = false;
    }

    @Override
    public String toString() {
        return type + " at (" + row + ", " + col + ") alive=" + alive;
    }
}
