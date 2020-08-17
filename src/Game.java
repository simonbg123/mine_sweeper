import java.util.Scanner;

public class Game {
    int size;
    int numBombs;
    Board board;
    Scanner scan;

    //todo ideas: could take a reference to GUI board element in constructor.
    // after each sequence of play, call a helper method in GUI. That helper method
    // renders a grid based on a 2D array.
    // or the whole game play could be in a button listener.
    // ... Better to make the button listener start the game, not implement it.
    // ... call its play method and display result appropriately.

    public Game(int size, int numBombs) {
        this.size = size;
        this.numBombs = numBombs;
        board = new Board(size, numBombs);
        scan = new Scanner(System.in);
    }

    public boolean play() {
        TurnResult result = TurnResult.CONTINUE;
        while (result == TurnResult.CONTINUE) {
            int[] target = getCellToFlip();
            result = board.flipTile(target[0], target[1]);
            board.showUpdatedBoard(result == TurnResult.WIN);
        }

        return result == TurnResult.WIN;
    }

    public int[] getCellToFlip() {
        int i = 0, j = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("\n" + board.nTilesToUncover() + " tiles to uncover..." +
                    "\nPlease enter row and column of cell to flip (ex: 2 8) :");
            String input = scan.nextLine();
            if (!input.matches("[1-9][0-9]*\\s+[1-9][0-9]*")) continue;
            String[] ints = input.split("\\s+");

            int user_i = Integer.parseInt(ints[0]);
            int user_j = Integer.parseInt(ints[1]);
            if (user_i < 1 || user_i > size || user_j < 1 || user_j > size) continue;
            i = user_i - 1;
            j = user_j - 1;
            if (board.tileIsVisible(i, j)) {
                System.out.println("Tile is already visible.");
            }
            validInput = true;
        }

        return new int[]{i, j};
    }
}
