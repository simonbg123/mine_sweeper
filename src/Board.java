import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Board {
    private int n;
    private Cell[][] grid;
    private int numBombs;
    private int nTilesToUncover;

    public Board(int n, int numBombs) {
        this.n = n;
        this.numBombs = numBombs;
        grid = new Cell[n][n];
        nTilesToUncover = n * n - numBombs;
        initializeBoard();
    }

    private void initializeBoard() {

        // create cells and bombs
        int bombsToPlace = numBombs;
        for (int row = 0; row < n; ++row ) {
            for (int col = 0; col < n; ++col) {
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

    private void shuffleBombs() {
        Random random = new Random();
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                int i = random.nextInt(n);
                int j = random.nextInt(n);

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
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {


                if (grid[row][col].isBomb) {
                    for (int[] delta : deltas) {
                        int i_child = row + delta[0];
                        int j_child = col + delta[1];

                        if (i_child < 0 || i_child >= n || j_child < 0 || j_child >= n) {
                            continue;
                        }
                        ++grid[i_child][j_child].bombIndex;
                    }
                }
            }
        }
    }

    public TurnResult flipTile(int i, int j) {

        Cell cell = grid[i][j];
        cell.isVisible = true;

        if (cell.isBomb) {
            return TurnResult.LOSS;
        }

        if (cell.bombIndex == 0) {
            cell.isVisible = false; // flipNeighbours will switch it back to true
            flipNeighbours(i, j);
        }
        else {
            --nTilesToUncover;
        }

        return nTilesToUncover == 0 ? TurnResult.WIN : TurnResult.CONTINUE;
    }
    //todo this will be overloaded with version that takes a gui arg
    // ... which will call the ggui element's show method with the Board (or its grid) as arg.
    public void showUpdatedBoard(boolean winningBoard) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");

        for (Cell[] row : grid) {
            sb.append("----".repeat(n));
            sb.append("\n");
            for (Cell cell : row) {
                sb.append("| ");
                if (winningBoard || cell.isVisible) {
                    if (cell.isBomb) sb.append("* ");
                    else sb.append("" + cell.bombIndex + " ");
                }
                else sb.append("? ");
            }
            sb.append("|\n");
        }
        sb.append("----".repeat(n));
        sb.append("\n\n");

        System.out.println(sb.toString());
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
            int[] visited = openList.remove();
            Cell current = grid[visited[0]][visited[1]];
            if (current.isVisible) { // verifies that the cell has been visited
                continue;
            }

            current.isVisible = true;
            --nTilesToUncover;

            if (current.bombIndex > 0) {
                continue;
            }

            for (int[] delta : deltas) {

                int i_child = visited[0] + delta[0];
                int j_child = visited[1] + delta[1];

                if (i_child < 0 || i_child >= n || j_child < 0 || j_child >= n) {
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



    private class Cell {
        int bombIndex;
        boolean isBomb;
        boolean isVisible;

        public Cell() {
            this.bombIndex = 0;
            this.isBomb = false;
            this.isVisible = false;
        }
    }

}
