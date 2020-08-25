import interfaces.IBoard;
import interfaces.ICell;
import interfaces.IGame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class BoardGUI extends JPanel implements MouseListener {

    private static final int SMALL_GRID_SPACING = 2;
    private static final int MEDIUM_GRID_SPACING = 1;
    private static final int LARGE_GRID_SPACING = 1;
    private static final int SMALL_GRID_CELL_SIZE = 50;
    private static final int MEDIUM_GRID_CELL_SIZE = 36;
    private static final int LARGE_GRID_CELL_SIZE = 28;

    private int spacing;
    private int boardSizeX;
    private int boardSizeY;
    private int cellSize;
    private int gridHeight;
    private int gridWidth;

    private MainGUI mainGUI;
    private IBoard board;
    private IGame game;



    BoardGUI(MainGUI mainGui, IBoard board, IGame game) {

        this.mainGUI = mainGui;
        this.board = board;
        this.game = game;
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        addMouseListener(this);

        initBoard();


    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    void initBoard() {
        boardSizeX = board.getSizeX();
        boardSizeY = board.getSizeY();

        switch (boardSizeX) {
            case Manager.SMALL_GRID_SIZE_X:
                spacing = SMALL_GRID_SPACING;
                cellSize = SMALL_GRID_CELL_SIZE;
                break;
            case Manager.MEDIUM_GRID_SIZE_X:
                spacing = MEDIUM_GRID_SPACING;
                cellSize = MEDIUM_GRID_CELL_SIZE;
                break;
            case Manager.LARGE_GRID_SIZE_X:
                spacing = LARGE_GRID_SPACING;
                cellSize = LARGE_GRID_CELL_SIZE;
                break;
        }

        // each cell is padded by 1 spacing on the left and the right
        // 1 extra spacing is added on the left-most cell and right-most cell for evenness.
        // Two full spacings are added as a border for the whole boarding.
        gridHeight = boardSizeY * cellSize + 6 * spacing;
        gridWidth = boardSizeX * cellSize + 6 * spacing;


    }

    public void paintComponent(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, gridWidth, gridHeight);
        int startX = 3 * spacing;
        int startY = 3 * spacing;

        // i's correspond to Y coordinates
        boolean raised;
        boolean drawBomb;
        boolean drawFlag;
        for (int i = 0; i < boardSizeY; ++i) {
            for (int j = 0; j < boardSizeX; ++j) {

                raised = false;
                drawBomb = false;
                drawFlag = false;

                ICell cell = board.getCell(i, j);
                String str = "";
                Game.State gameState = game.getState();

                if (cell.isVisible()) {
                    if (cell.isBomb()) {
                        if (gameState == Game.State.WON) {
                            g.setColor(Color.green);
                        }
                        else { g.setColor(Color.red); }

                        drawBomb = true;
                    }
                    else if (cell.getnCloseBombs() > 0) {
                        g.setColor(Color.yellow);
                        str = Integer.toString(cell.getnCloseBombs());
                    }
                    else {
                        g.setColor(Color.lightGray);
                    }
                }
                else { //if (!cell.isVisible() {
                    if (cell.isBomb() && gameState == Game.State.WON) {
                        g.setColor(Color.GREEN);
                        drawFlag = true;
                    }
                    else if (cell.isBomb() && gameState == Game.State.LOST) {
                        g.setColor(Color.orange);
                        drawBomb = true;
                    }
                    else {
                        g.setColor(Color.gray);
                        raised = true;

                        if (gameState == Game.State.PLACING_FLAGS && !cell.hasFlag()) {
                            str = "?";
                        }
                        else if (cell.hasFlag()) {
                            drawFlag = true;
                        }
                    }
                }

                int paintStartX = startX + spacing + j * cellSize;
                int paintStartY = startY + spacing + i * cellSize;

                if (raised) {
                    g.fill3DRect(
                            paintStartX,
                            paintStartY,
                            cellSize - 2 * spacing,
                            cellSize - 2 * spacing,
                            true
                    );
                }
                else {
                    g.fillRect(
                            paintStartX,
                            paintStartY,
                            cellSize - 2 * spacing,
                            cellSize - 2 * spacing
                    );

                }
                if (!str.isBlank()) {
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Monospace", Font.BOLD, cellSize*4 / 9 ));
                    g.drawString(
                            str,
                            startX + spacing + j * cellSize + cellSize / 3,
                            startY + spacing + i * cellSize + cellSize * 15 / 24
                    );
                }

                else if (drawBomb) {
                    paintBomb(g, paintStartX, paintStartY);
                }
                else if (drawFlag) {
                    paintFlag(g, paintStartX, paintStartY);
                }

            }
        }
    }

    private void paintBomb(Graphics g, int x, int y) {

        int bombsize = (cellSize == LARGE_GRID_CELL_SIZE ? 13 : 26 );
        int sizeFactor = (cellSize == LARGE_GRID_CELL_SIZE ? 1 : 2 );

        int start = (cellSize - 2 * spacing - bombsize) / 2;
        g.setColor(Color.black);

        // paint the first two narrow rectangles
        g.fillRect(x + start, y + start + 6 * sizeFactor, bombsize, 1 * sizeFactor);
        g.fillRect(x + start + 6 * sizeFactor, y + start, 1 * sizeFactor, bombsize);

        // paint the two medium rectangles
        g.fillRect(x + start + 2 * sizeFactor, y + start + 4 * sizeFactor, 9 * sizeFactor, 5 * sizeFactor);
        g.fillRect(x + start + 4 * sizeFactor, y + start + 2 * sizeFactor, 5 * sizeFactor, 9 * sizeFactor);

        // paint the middle square
        g.fillRect(x + start + 3 * sizeFactor, y + start + 3 * sizeFactor, 7 * sizeFactor, 7 * sizeFactor);

        //four little squares
        g.fillRect(x + start + 2 * sizeFactor, y + start + 2 * sizeFactor, 1 * sizeFactor, 1 * sizeFactor);
        g.fillRect(x + start + 2 * sizeFactor, y + start + 10 * sizeFactor, 1 * sizeFactor, 1 * sizeFactor);
        g.fillRect(x + start + 10 * sizeFactor, y + start + 2 * sizeFactor, 1 * sizeFactor, 1 * sizeFactor);
        g.fillRect(x + start + 10 * sizeFactor, y + start + 10 * sizeFactor, 1 * sizeFactor, 1 * sizeFactor);

        //white reflexion
        g.setColor(Color.WHITE);
        g.fillRect(x + start + 4 * sizeFactor, y + start + 4 * sizeFactor, 2 * sizeFactor, 2 * sizeFactor);

    }

    private void paintFlag(Graphics g, int x, int y) {

        switch (cellSize) {
            case SMALL_GRID_CELL_SIZE:
                x += 6;
                y += 6;
            case MEDIUM_GRID_CELL_SIZE:
                //base
                g.setColor(Color.BLACK);
                g.fillRect(x + 7, y + 27, 22, 4);
                g.fillRect(x + 15, y + 25, 10, 2);
                g.fillRect(x + 19, y + 19, 2, 6);
                // flag
                g.setColor(Color.RED);
                g.fillRect(x + 15, y + 17, 6, 2);
                g.fillRect(x + 11, y + 15, 10, 2);
                g.fillRect(x + 7, y + 13, 14, 2);
                g.fillRect(x + 5, y + 11, 16, 2);
                g.fillRect(x + 7, y + 9, 14, 2);
                g.fillRect(x + 9, y + 7, 12, 2);
                g.fillRect(x + 13, y + 5, 8, 2);
                g.fillRect(x + 17, y + 3, 4, 2);

                break;
            case LARGE_GRID_CELL_SIZE:
                x += 1;
                //base
                g.setColor(Color.BLACK);
                g.fillRect(x + 4, y + 22, 18, 2);
                g.fillRect(x + 10, y + 20, 8, 2);
                g.fillRect(x + 14, y + 16, 2, 4);
                // flag
                g.setColor(Color.RED);
                g.fillRect(x + 10, y + 14, 6, 2);
                g.fillRect(x + 6, y + 12, 10, 2);
                g.fillRect(x + 4, y + 10, 12, 2);
                g.fillRect(x + 6, y + 8, 10, 2);
                g.fillRect(x + 8, y + 6, 8, 2);
                g.fillRect(x + 12, y + 4, 4, 2);
                break;
        }
    }

    /**
     * Get grid j index from JPanel X coordinate
     * @param x
     * @return
     */
    private int getJfromX(int x) {
        return (x - 3 * spacing) / cellSize;
    }

    /**
     * Get grid i index from JPanel Y coordinate
     * @param y
     * @return
     */
    private int getIfromY(int y) {
        return (y - 3 * spacing) / cellSize;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        var state = game.getState();

        if (!(state == Game.State.PLAYING || state == Game.State.PLACING_FLAGS)) return;

        int i = getIfromY(e.getY());
        int j = getJfromX(e.getX());

        var cell = board.getCell(i, j);

        if (cell.isVisible()) return; // title was already visible

        if (state == Game.State.PLACING_FLAGS) {
            cell.toggleFlag();
            mainGUI.refresh();
            return;
        }

        game.revealTile(i, j);
        mainGUI.refresh();
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


