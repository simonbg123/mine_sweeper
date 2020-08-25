import interfaces.IBoard;
import interfaces.ICell;

import java.util.LinkedList;
import java.util.Random;

/**
 * Represents the game board logical structure. It modifies the board state.
 */
class Board implements IBoard {

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

    class Cell implements ICell {
        private int nCloseBombs;
        private boolean isBomb;
        private boolean isVisible;
        private boolean hasFlag;

        Cell() {
            this.nCloseBombs = 0;
            this.isBomb = false;
            this.isVisible = false;
            this.hasFlag = false;
        }

        @Override
        public int getnCloseBombs() {
            return nCloseBombs;
        }
        @Override
        public boolean isBomb() {
            return isBomb;
        }
        @Override
        public boolean isVisible() {
            return isVisible;
        }
        @Override
        public boolean hasFlag() { return hasFlag; }
        @Override
        public void toggleFlag() {
            hasFlag = !hasFlag;
        }
    }

    private int sizeX;
    private int sizeY;
    private Cell[][] grid;
    private int nTilesToUncover;
    private int nBombs; // stores the last number of bombs for purposes of re-initializing after a first-move fail.

    private Board(int m, int n) {
        setGrid(m, n);
    }

    static Board getInstance() {
        return instance;
    }

    @Override
    public void initialize(int numBombs) {
        nBombs = numBombs;
        nTilesToUncover = sizeX * sizeY - numBombs;
        // create cells
        for (int row = 0; row < sizeY; ++row ) {
            for (int col = 0; col < sizeX; ++col) {
                grid[row][col] = new Cell();
            }
        }

        placeBombs(numBombs);
    }

    @Override
    public void reinitialize() {
        initialize(nBombs);
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

    @Override
    public RevealResult revealTile(int i, int j) {

        Cell cell = grid[i][j];

        if (cell.isVisible) return RevealResult.CONTINUE; // an already discovered tile was selected

        cell.isVisible = true;

        if (cell.isBomb) {
            return RevealResult.LOSS;
        }
        else if (cell.nCloseBombs == 0) {
            cell.isVisible = false; // flipNeighbours will switch it back to true
            flipNeighbours(i, j);
        }
        else {
            --nTilesToUncover;
        }

        return nTilesToUncover == 0 ? RevealResult.WIN : RevealResult.CONTINUE;
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

    @Override
    public Cell getCell(int i, int j) {
        return grid[i][j];
    }
    @Override
    public int nTilesToUncover() {
        return nTilesToUncover;
    }
    @Override
    public int getSizeX() {
        return sizeX;
    }
    @Override
    public int getSizeY() {
        return sizeY;
    }
    @Override
    public void setGrid(int sizeY, int sizeX) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        grid = new Cell[sizeY][sizeX];
    }
}
