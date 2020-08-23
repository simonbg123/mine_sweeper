import java.util.LinkedList;
import java.util.Random;

/**
 * Represents the game board logical structure. It modifies the board state.
 */
class Board {

    /**
     * n rows, n columns
     */
    private static Board instance = new Board(Manager.DEFAULT_GRID_SIZE_Y, Manager.DEFAULT_GRID_SIZE_X);

    /**
     * used for incrementing neighbour bomb index values
     */
    private static int[][] deltas = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };

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
        // create cells
        for (int row = 0; row < sizeY; ++row ) {
            for (int col = 0; col < sizeX; ++col) {
                grid[row][col] = new Cell();
            }
        }

        placeBombs(numBombs);

    }

    private void placeBombs(int bombsToPlace) {

        Random random = new Random();

        while (bombsToPlace > 0) {
            int i = random.nextInt(sizeY);
            int j = random.nextInt(sizeX);
            Cell cell = grid[i][j];
            while (cell.isBomb) {
                i = random.nextInt(sizeY);
                j = random.nextInt(sizeX);
                cell = grid[i][j];
            }
            cell.isBomb = true;
            getBombIndexValues(i, j);
            --bombsToPlace;
        }
    }

    private void getBombIndexValues(int row, int col) {

        for (int[] delta : deltas) {
            int i_child = row + delta[0];
            int j_child = col + delta[1];

            if (i_child < 0 || i_child >= sizeY || j_child < 0 || j_child >= sizeX) {
                continue;
            }
            ++grid[i_child][j_child].nCloseBombs;
        }

    }

    TileRevealResult revealTile(int i, int j) {

        Cell cell = grid[i][j];

        if (cell.isVisible) return TileRevealResult.CONTINUE; // an already discovered tile was selected

        cell.isVisible = true;

        if (cell.isBomb) {
            return TileRevealResult.LOSS;
        }
        else if (cell.nCloseBombs == 0) {
            cell.isVisible = false; // flipNeighbours will switch it back to true
            flipNeighbours(i, j);
        }
        else {
            --nTilesToUncover;
        }

        return nTilesToUncover == 0 ? TileRevealResult.WIN : TileRevealResult.CONTINUE;
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
