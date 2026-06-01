import java.util.ArrayList;

/**
 * World.java
 * Represents the 2D grid environment for the Agentic World simulation.
 * Manages the grid, all agents, food, walls, and the simulation loop.
 */
public class World {

    // Grid cell constants
    public static final int EMPTY = 0;
    public static final int FOOD  = 1;
    public static final int WALL  = 2;

    private int[][] grid;
    private int rows;
    private int cols;
    private ArrayList<Agent> agents;
    private int turn;
    private int foodEaten;
    private int kills;

    /**
     * Constructs a World with the given dimensions.
     * Walls, food, and agents are placed during setup.
     */
    public World(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
        this.agents = new ArrayList<>();
        this.turn = 0;
        this.foodEaten = 0;
        this.kills = 0;
        placeWalls();
        placeFood(30);
        spawnAgents();
    }

    // ---------------------------------------------------------------
    // Setup Methods
    // ---------------------------------------------------------------

    /** Places wall clusters on the grid. */
    private void placeWalls() {
        int[][] wallCells = {
            {2,3},{2,4},{2,5},{3,5},{4,5},{4,6},{4,7},
            {7,10},{7,11},{7,12},{8,12},{9,12},{9,11},{9,10},
            {1,14},{2,14},{3,14},{3,15},{3,16},
            {10,2},{11,2},{12,2},{12,3},{12,4}
        };
        for (int[] cell : wallCells) {
            int r = cell[0], c = cell[1];
            if (r < rows && c < cols) {
                grid[r][c] = WALL;
            }
        }
    }

    /** Randomly places a given number of food items on empty cells. */
    private void placeFood(int count) {
        int placed = 0;
        int attempts = 0;
        while (placed < count && attempts < 1000) {
            int r = (int)(Math.random() * rows);
            int c = (int)(Math.random() * cols);
            if (grid[r][c] == EMPTY) {
                grid[r][c] = FOOD;
                placed++;
            }
            attempts++;
        }
    }

    /** Spawns one of each agent type at random empty positions. */
    private void spawnAgents() {
        agents.add(new Explorer(this, findEmptyCell()));
        agents.add(new Collector(this, findEmptyCell()));
        agents.add(new Predator(this, findEmptyCell()));
        agents.add(new Avoider(this, findEmptyCell()));
        agents.add(new Ghost(this, findEmptyCell()));
    }

    /**
     * Finds a random empty cell with no agent on it.
     * @return int[] of {row, col}
     */
    public int[] findEmptyCell() {
        int r, c;
        int attempts = 0;
        do {
            r = (int)(Math.random() * rows);
            c = (int)(Math.random() * cols);
            attempts++;
        } while ((grid[r][c] != EMPTY || getAgentAt(r, c) != null) && attempts < 500);
        // If we ran out of attempts, fall back to top-left safe cell
        if (attempts >= 500) {
            for (int fr = 0; fr < rows; fr++)
                for (int fc = 0; fc < cols; fc++)
                    if (grid[fr][fc] == EMPTY && getAgentAt(fr, fc) == null)
                        return new int[]{fr, fc};
        }
        return new int[]{r, c};
    }

    // ---------------------------------------------------------------
    // Simulation Loop
    // ---------------------------------------------------------------

    /**
     * Runs the simulation for the given number of turns,
     * printing the grid and status each turn.
     */
    public void run(int numTurns) {
        System.out.println("=== Agentic World Simulation ===");
        System.out.println("Grid: " + rows + " x " + cols);
        System.out.println("Agents: " + agents.size());
        System.out.println("================================\n");

        display();

        for (int i = 0; i < numTurns; i++) {
            step();
            display();
            if (getLivingAgents().isEmpty()) {
                System.out.println("All agents have been eliminated. Simulation ended.");
                break;
            }
            // Small pause so output is readable when run in terminal
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        }

        System.out.println("\n=== Simulation Complete ===");
        System.out.println("Turns run:   " + turn);
        System.out.println("Food eaten:  " + foodEaten);
        System.out.println("Kills:       " + kills);
        System.out.println("Survivors:   " + getLivingAgents().size());
    }

    /** Advances the simulation by one turn. */
    public void step() {
        turn++;
        ArrayList<Agent> living = getLivingAgents();
        for (Agent a : living) {
            if (a.isAlive()) {
                a.act();
            }
        }
        // Small chance to respawn a food item
        if (Math.random() < 0.15) {
            placeFood(1);
        }
    }

    // ---------------------------------------------------------------
    // Display
    // ---------------------------------------------------------------

    /** Prints the current state of the grid to the console. */
    public void display() {
        System.out.println("--- Turn " + turn + " | Agents: " + getLivingAgents().size()
            + " | Food eaten: " + foodEaten + " | Kills: " + kills + " ---");

        // Build a character map from agents
        char[][] grid2D = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == WALL)       grid2D[r][c] = '#';
                else if (grid[r][c] == FOOD)  grid2D[r][c] = '.';
                else                           grid2D[r][c] = ' ';
            }
        }
        for (Agent a : getLivingAgents()) {
            grid2D[a.getRow()][a.getCol()] = a.getSymbol();
        }

        // Print top border
        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");

        for (int r = 0; r < rows; r++) {
            System.out.print("|");
            for (int c = 0; c < cols; c++) {
                System.out.print(grid2D[r][c]);
            }
            System.out.println("|");
        }

        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");
        System.out.println("Legend:  E=Explorer  C=Collector  P=Predator  A=Avoider  G=Ghost  .=Food  #=Wall");
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Grid Utilities (used by agents)
    // ---------------------------------------------------------------

    public int getCell(int r, int c) { return grid[r][c]; }
    public void setCell(int r, int c, int value) { grid[r][c] = value; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    public boolean isWalkable(int r, int c) {
        return inBounds(r, c) && grid[r][c] != WALL;
    }

    /** Returns the living agent at (r, c), or null if none. */
    public Agent getAgentAt(int r, int c) {
        for (Agent a : agents) {
            if (a.isAlive() && a.getRow() == r && a.getCol() == c) {
                return a;
            }
        }
        return null;
    }

    public ArrayList<Agent> getAgents() { return agents; }

    public ArrayList<Agent> getLivingAgents() {
        ArrayList<Agent> living = new ArrayList<>();
        for (Agent a : agents) {
            if (a.isAlive()) living.add(a);
        }
        return living;
    }

    public void incrementFoodEaten() { foodEaten++; }
    public void incrementKills()     { kills++; }
    public int getTurn()             { return turn; }

    // ---------------------------------------------------------------
    // Main entry point
    // ---------------------------------------------------------------

    public static void main(String[] args) {
        World world = new World(14, 20);
        world.run(50);
    }
}import java.util.ArrayList;

/**
 * World.java
 * Represents the 2D grid environment for the Agentic World simulation.
 * Manages the grid, all agents, food, walls, and the simulation loop.
 */
public class World {

    // Grid cell constants
    public static final int EMPTY = 0;
    public static final int FOOD  = 1;
    public static final int WALL  = 2;

    private int[][] grid;
    private int rows;
    private int cols;
    private ArrayList<Agent> agents;
    private int turn;
    private int foodEaten;
    private int kills;

    /**
     * Constructs a World with the given dimensions.
     * Walls, food, and agents are placed during setup.
     */
    public World(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
        this.agents = new ArrayList<>();
        this.turn = 0;
        this.foodEaten = 0;
        this.kills = 0;
        placeWalls();
        placeFood(30);
        spawnAgents();
    }

    // ---------------------------------------------------------------
    // Setup Methods
    // ---------------------------------------------------------------

    /** Places wall clusters on the grid. */
    private void placeWalls() {
        int[][] wallCells = {
            {2,3},{2,4},{2,5},{3,5},{4,5},{4,6},{4,7},
            {7,10},{7,11},{7,12},{8,12},{9,12},{9,11},{9,10},
            {1,14},{2,14},{3,14},{3,15},{3,16},
            {10,2},{11,2},{12,2},{12,3},{12,4}
        };
        for (int[] cell : wallCells) {
            int r = cell[0], c = cell[1];
            if (r < rows && c < cols) {
                grid[r][c] = WALL;
            }
        }
    }

    /** Randomly places a given number of food items on empty cells. */
    private void placeFood(int count) {
        int placed = 0;
        int attempts = 0;
        while (placed < count && attempts < 1000) {
            int r = (int)(Math.random() * rows);
            int c = (int)(Math.random() * cols);
            if (grid[r][c] == EMPTY) {
                grid[r][c] = FOOD;
                placed++;
            }
            attempts++;
        }
    }

    /** Spawns one of each agent type at random empty positions. */
    private void spawnAgents() {
        agents.add(new Explorer(this, findEmptyCell()));
        agents.add(new Collector(this, findEmptyCell()));
        agents.add(new Predator(this, findEmptyCell()));
        agents.add(new Avoider(this, findEmptyCell()));
        agents.add(new Ghost(this, findEmptyCell()));
    }

    /**
     * Finds a random empty cell with no agent on it.
     * @return int[] of {row, col}
     */
    public int[] findEmptyCell() {
        int r, c;
        int attempts = 0;
        do {
            r = (int)(Math.random() * rows);
            c = (int)(Math.random() * cols);
            attempts++;
        } while ((grid[r][c] != EMPTY || getAgentAt(r, c) != null) && attempts < 500);
        // If we ran out of attempts, fall back to top-left safe cell
        if (attempts >= 500) {
            for (int fr = 0; fr < rows; fr++)
                for (int fc = 0; fc < cols; fc++)
                    if (grid[fr][fc] == EMPTY && getAgentAt(fr, fc) == null)
                        return new int[]{fr, fc};
        }
        return new int[]{r, c};
    }

    // ---------------------------------------------------------------
    // Simulation Loop
    // ---------------------------------------------------------------

    /**
     * Runs the simulation for the given number of turns,
     * printing the grid and status each turn.
     */
    public void run(int numTurns) {
        System.out.println("=== Agentic World Simulation ===");
        System.out.println("Grid: " + rows + " x " + cols);
        System.out.println("Agents: " + agents.size());
        System.out.println("================================\n");

        display();

        for (int i = 0; i < numTurns; i++) {
            step();
            display();
            if (getLivingAgents().isEmpty()) {
                System.out.println("All agents have been eliminated. Simulation ended.");
                break;
            }
            // Small pause so output is readable when run in terminal
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        }

        System.out.println("\n=== Simulation Complete ===");
        System.out.println("Turns run:   " + turn);
        System.out.println("Food eaten:  " + foodEaten);
        System.out.println("Kills:       " + kills);
        System.out.println("Survivors:   " + getLivingAgents().size());
    }

    /** Advances the simulation by one turn. */
    public void step() {
        turn++;
        ArrayList<Agent> living = getLivingAgents();
        for (Agent a : living) {
            if (a.isAlive()) {
                a.act();
            }
        }
        // Small chance to respawn a food item
        if (Math.random() < 0.15) {
            placeFood(1);
        }
    }

    // ---------------------------------------------------------------
    // Display
    // ---------------------------------------------------------------

    /** Prints the current state of the grid to the console. */
    public void display() {
        System.out.println("--- Turn " + turn + " | Agents: " + getLivingAgents().size()
            + " | Food eaten: " + foodEaten + " | Kills: " + kills + " ---");

        // Build a character map from agents
        char[][] grid2D = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == WALL)       grid2D[r][c] = '#';
                else if (grid[r][c] == FOOD)  grid2D[r][c] = '.';
                else                           grid2D[r][c] = ' ';
            }
        }
        for (Agent a : getLivingAgents()) {
            grid2D[a.getRow()][a.getCol()] = a.getSymbol();
        }

        // Print top border
        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");

        for (int r = 0; r < rows; r++) {
            System.out.print("|");
            for (int c = 0; c < cols; c++) {
                System.out.print(grid2D[r][c]);
            }
            System.out.println("|");
        }

        System.out.print("+");
        for (int c = 0; c < cols; c++) System.out.print("-");
        System.out.println("+");
        System.out.println("Legend:  E=Explorer  C=Collector  P=Predator  A=Avoider  G=Ghost  .=Food  #=Wall");
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Grid Utilities (used by agents)
    // ---------------------------------------------------------------

    public int getCell(int r, int c) { return grid[r][c]; }
    public void setCell(int r, int c, int value) { grid[r][c] = value; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    public boolean isWalkable(int r, int c) {
        return inBounds(r, c) && grid[r][c] != WALL;
    }

    /** Returns the living agent at (r, c), or null if none. */
    public Agent getAgentAt(int r, int c) {
        for (Agent a : agents) {
            if (a.isAlive() && a.getRow() == r && a.getCol() == c) {
                return a;
            }
        }
        return null;
    }

    public ArrayList<Agent> getAgents() { return agents; }

    public ArrayList<Agent> getLivingAgents() {
        ArrayList<Agent> living = new ArrayList<>();
        for (Agent a : agents) {
            if (a.isAlive()) living.add(a);
        }
        return living;
    }

    public void incrementFoodEaten() { foodEaten++; }
    public void incrementKills()     { kills++; }
    public int getTurn()             { return turn; }

    // ---------------------------------------------------------------
    // Main entry point
    // ---------------------------------------------------------------

    public static void main(String[] args) {
        World world = new World(14, 20);
        world.run(50);
    }
}
