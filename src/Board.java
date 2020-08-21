import java.util.LinkedList;
import java.util.Random;

/**
 * Represents the game board logical structure
 */
public class Board {

    /**
     * n rows, n columns
     */
    private static Board instance = new Board(Main.DEFAULT_GRID_SIZE_Y, Main.DEFAULT_GRID_SIZE_X);

    class Cell {
        private int nCloseBombs;
        private boolean isBomb;
        private boolean isVisible;

        Cell() {
            this.nCloseBombs = 0;
            this.isBomb = false;
            this.isVisible = false;
        }

        int getnCloseBombs() {
            return nCloseBombs;
        }

        boolean isBomb() {
            return isBomb;
        }

        boolean isVisible() {
            return isVisible;
        }
    }

    private int sizeX;
    private int sizeY;
    private Cell[][] grid;
    private int nTilesToUncover;

    private Board(int m, int n) {
        this.sizeY = m;
        this.sizeX = n;
        setGrid();
    }

    static Board getInstance() {
        return instance;
    }

    void initialize(int numBombs) {

        nTilesToUncover = sizeX * sizeY - numBombs;
        // create cells and bombs
        int bombsToPlace = numBombs;
        for (int row = 0; row < sizeY; ++row ) {
            for (int col = 0; col < sizeX; ++col) {
                grid[row][col] = new Cell();
                if (bombsToPlace > 0) {
                    grid[row][col].isBomb = true;
                    --bombsToPlace;
                }
            }
        }

        shuffleBombs();

        getBombIndexValues();
    }

    void initializeDebug() {

        int[][] bombs = {{0,1}, {1,2}, {1,7}, {1,8}, {1,10}, {1,13}, {2,8}, {3,7}, {3,13},
                {5,5}, {6,1}, {6,5}, {6,9}, {7,11}, {7,17}, {8,15}, {9,15}};

        int numBombs = bombs.length;
        nTilesToUncover = sizeX * sizeY - numBombs;

        for (int row = 0; row < sizeY; ++row ) {
            for (int col = 0; col < sizeX; ++col) {
                grid[row][col] = new Cell();
            }
        }
        // place bombs
        Cell cell;
        for (int[] bomb : bombs) {
            cell = grid[bomb[0]][bomb[1]];
            cell.isBomb = true;
        }


        getBombIndexValues();
    }


    private void shuffleBombs() {
        Random random = new Random();
        for (int row = 0; row < sizeY; ++row) {
            for (int col = 0; col < sizeX; ++col) {
                int i = random.nextInt(sizeY);
                int j = random.nextInt(sizeX);

                //swap
                if (row != i && col != j) {
                    boolean temp = grid[row][col].isBomb;
                    grid[row][col].isBomb = grid[i][j].isBomb;
                    grid[i][j].isBomb = temp;
                }
            }
        }
    }

    private void getBombIndexValues() {
        int[][] deltas = {
                {-1, -1}, {-1, 0}, {-1, 1},
                { 0, -1},          { 0, 1},
                { 1, -1}, { 1, 0}, { 1, 1}
        };
        for (int row = 0; row < sizeY; ++row) {
            for (int col = 0; col < sizeX; ++col) {


                if (grid[row][col].isBomb) {
                    for (int[] delta : deltas) {
                        int i_child = row + delta[0];
                        int j_child = col + delta[1];

                        if (i_child < 0 || i_child >= sizeY || j_child < 0 || j_child >= sizeX) {
                            continue;
                        }
                        ++grid[i_child][j_child].nCloseBombs;
                    }
                }
            }
        }
    }

    public TurnResult flipTile(int i, int j) {

        Cell cell = grid[i][j];

        if (cell.isVisible) return TurnResult.CONTINUE; // an already discovered tile was selected

        cell.isVisible = true;

        if (cell.isBomb) {
            return TurnResult.LOSS;
        }
        else if (cell.nCloseBombs == 0) {
            cell.isVisible = false; // flipNeighbours will switch it back to true
            flipNeighbours(i, j);
        }
        else {
            --nTilesToUncover;
        }

        return nTilesToUncover == 0 ? TurnResult.WIN : TurnResult.CONTINUE;
    }


    private void flipNeighbours(int i, int j) {
        int[][] deltas = {
                {-1, -1}, {-1, 0}, {-1, 1},
                { 0, -1},          { 0, 1},
                { 1, -1}, { 1, 0}, { 1, 1}
        };

        //BFS search
        LinkedList<int[]> openList = new LinkedList<>();
        // no visitedList is needed: we check value of isVisible
        openList.add(new int[]{i, j});
        while (! openList.isEmpty()) {
            // process next node and add neighbours to openList
            int[] visiting = openList.remove();
            Cell current = grid[visiting[0]][visiting[1]];
            if (current.isVisible) { // verifies that the cell has been visited
                continue;
            }

            current.isVisible = true;
            --nTilesToUncover;
            if (nTilesToUncover == 0) return;

            if (current.nCloseBombs > 0) {
                continue;
            }

            for (int[] delta : deltas) {

                int i_child = visiting[0] + delta[0];
                int j_child = visiting[1] + delta[1];

                if (i_child < 0 || i_child >= sizeY || j_child < 0 || j_child >= sizeX) {
                    continue;
                }

                openList.add(new int[]{i_child, j_child});
            }
        }
    }

    public boolean tileIsVisible(int i, int j) {
        return grid[i][j].isVisible;
    }

    public int nTilesToUncover() {
        return nTilesToUncover;
    }

    public void setSizeX(int size) {
        this.sizeX = size;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeY(int size) {
        this.sizeY = size;
    }

    public int getSizeY() {
        return sizeY;
    }

    public Cell[][] getGrid() {
        return grid;
    }


    void setGrid() {
        grid = new Cell[sizeY][sizeX];
    }
}
