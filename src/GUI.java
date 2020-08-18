import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class GUI extends JFrame {

    private static final int DEFAULT_SPACING = 2;
    private static final int DEFAULT_CELL_SIZE = 65;
    private static final int WINDOW_WIDTH = 1186;
    private static final int WINDOW_HEIGHT = 829;
    private static final int PANEL_WIDTH = 1180;
    private static final int PANEL_HEIGHT = 800;

    private int spacing;
    private int boardSize;
    private int cellSize;
    private int gridSize;

    GUI() {

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
        setVisible(true);
        setResizable(false);

        BoardGUI gameBoard = new BoardGUI();
        gameBoard.addMouseListener(new Click());
        gameBoard.setBounds((PANEL_WIDTH - gridSize) / 2, (PANEL_HEIGHT - gridSize) / 2, gridSize, gridSize);
        add(gameBoard);





    }

    class BoardGUI extends JPanel {

        public void paintComponent(Graphics g) {

            g.setColor(Color.black);
            g.fillRect(
                    0, 0,
                    gridSize, gridSize);
            g.setColor(Color.gray);
            int startX = 3 * spacing;
            int startY = 3 * spacing;

            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
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

    class Click implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("Hello there");
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
}
