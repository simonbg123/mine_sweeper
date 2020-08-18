import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Scanner;

public class Game implements MouseListener {
    private Board board;
    private Scanner scan;

    Game(Board board) {
        this.board = board;
        scan = new Scanner(System.in);
    }

    void initialize(int numBombs) {
        board.initialize(numBombs);

    }

    boolean play() {
        TurnResult result = TurnResult.CONTINUE;
        while (result == TurnResult.CONTINUE) {
            int[] target = getCellToFlip();
            result = board.flipTile(target[0], target[1]);
            board.showUpdatedBoard(result == TurnResult.WIN);
        }

        return result == TurnResult.WIN;
    }

    private int[] getCellToFlip() {
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
            if (user_i < 1 || user_i > board.getSize() || user_j < 1 || user_j > board.getSize()) continue;
            i = user_i - 1;
            j = user_j - 1;
            if (board.tileIsVisible(i, j)) {
                System.out.println("Tile is already visible.");
            }
            validInput = true;
        }

        return new int[]{i, j};
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
