import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class GUI extends JFrame {

    private static GUI instance = null;

    private static final int DEFAULT_SPACING = 2;
    private static final int DEFAULT_CELL_SIZE = 60;
    private static final int WINDOW_WIDTH = 1186;
    private static final int WINDOW_HEIGHT = 829;
    private static final int PANEL_WIDTH = 1180;
    private static final int PANEL_HEIGHT = 800;

    private int spacing;
    private int boardSize;
    private int cellSize;
    private int gridSize;

    private Board board; // logical board
    private Game game;

    private GUI() {

        board = Board.getInstance();
        game = Game.getInstance();

        spacing = DEFAULT_SPACING;
        boardSize = Main.DEFAULT_SIZE;
        cellSize = DEFAULT_CELL_SIZE;
        // each cell is padded by 1 spacing on the left and the right
        // 1 extra spacing is added on the left-most cell and right-most cell for evenness.
        // Two full spacings are added as a border for the whole boarding.
        gridSize = boardSize * cellSize + 6 * spacing;


        setTitle("Mine Sweeper");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BoardGUI gameBoard = new BoardGUI();
        gameBoard.addMouseListener(game);
        add(gameBoard);

        setVisible(true);
        setResizable(false);

    }

    public static GUI getInstance() {
        if (instance == null) {
            instance = new GUI();
        }
        return instance;
    }

    class BoardGUI extends JPanel {

        public void paintComponent(Graphics g) {

            setBounds((PANEL_WIDTH - gridSize) / 2, (PANEL_HEIGHT - gridSize) / 2, gridSize, gridSize);

            Board.Cell[][] grid = board.getGrid();

            g.setColor(Color.black);
            g.fillRect(0, 0, gridSize, gridSize);
            int startX = 3 * spacing;
            int startY = 3 * spacing;

            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {

                    Board.Cell cell = grid[i][j];

                    if (!cell.isVisible()) {
                        g.setColor(Color.gray);
                    }
                    else if (cell.isBomb()) {
                        g.setColor(Color.red);
                    }
                    else if (cell.getnCloseBombs() > 0) {
                        g.setColor(Color.yellow);
                    }
                    else {
                        g.setColor(Color.lightGray);
                    }
                    g.fillRect(
                            startX + spacing + i * cellSize,
                            startY + spacing + j * cellSize,
                            cellSize - 2 * spacing,
                            cellSize - 2 * spacing
                    );

                }
            }

        }
    }
}
