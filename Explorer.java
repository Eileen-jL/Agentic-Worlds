/**
 * Explorer.java
 * An Explorer systematically scans the map by moving in one direction
 * until hitting a wall or boundary, then rotating clockwise.
 *
 * Symbol: E
 * Behavior: Tries to continue in its current direction. If blocked,
 *           rotates 90° clockwise and tries again (up to all 4 directions).
 */
public class Explorer extends Agent {

    // Indices into DIRECTIONS: 0=up, 1=down, 2=left, 3=right
    private int dirIndex;

    public Explorer(World world, int[] pos) {
        super(world, pos, 'E', "Explorer");
        // DIRECTIONS: 0=up{-1,0}, 1=down{1,0}, 2=left{0,-1}, 3=right{0,1}
        // Start moving RIGHT = index 3
        this.dirIndex = 3;
    }

    @Override
    public void act() {
        // Try current direction; if blocked, rotate clockwise and try again
        for (int attempt = 0; attempt < 4; attempt++) {
            int[] d = DIRECTIONS[dirIndex];
            int nr  = getRow() + d[0];
            int nc  = getCol() + d[1];

            if (canMoveTo(nr, nc)) {
                moveTo(nr, nc);
                return;
            }
            // Blocked — rotate 90° clockwise and retry
            dirIndex = rotateClockwise(dirIndex);
        }
        // Completely boxed in — stay put
    }

    /**
     * Returns the next direction index after a 90° clockwise rotation.
     * DIRECTIONS layout: 0=up, 1=down, 2=left, 3=right
     * Clockwise cycle:   up(0) -> right(3) -> down(1) -> left(2) -> up(0)
     */
    private int rotateClockwise(int idx) {
        switch (idx) {
            case 0: return 3; // up    -> right
            case 3: return 1; // right -> down
            case 1: return 2; // down  -> left
            case 2: return 0; // left  -> up
            default: return 0;
        }
    }
}
