import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Displays the board, controls, menu and announcements.
 */
class GUI extends JFrame {

    private static GUI instance = null;

    private static final int SMALL_GRID_SPACING = 2;
    private static final int MEDIUM_GRID_SPACING = 1;
    private static final int LARGE_GRID_SPACING = 1;
    private static final int SMALL_GRID_CELL_SIZE = 50;
    private static final int MEDIUM_GRID_CELL_SIZE = 36;
    private static final int LARGE_GRID_CELL_SIZE = 28;
    private static final int WINDOW_WIDTH = 1186;
    private static final int WINDOW_HEIGHT = 829;
    private static final int PANEL_WIDTH = 1180;
    private static final int PANEL_HEIGHT = 800;

    private static final int OPTIONS_PANEL_WIDTH = 144;
    private static final int OPTIONS_PANEL_HEIGHT = 128;
    private static final int ANNOUNCEMENT_PANEL_WIDTH = PANEL_WIDTH - OPTIONS_PANEL_WIDTH - 25;
    private static final int ANNOUNCEMENT_PANEL_HEIGHT = 128;

    static final String CONTINUE_BUTTON_STRING = "Continue";
    static final String NEWGAME_BUTTON_STRING = "New Game";
    static final String SINGLE_GAME_OPTION_STRING = "Single-Game";
    static final String MULTILEVEL_OPTION_STRING = "Multilevel";
    static final String EASY_OPTION_STRING = "Easy";
    static final String MEDIUM_OPTION_STRING = "Medium";
    static final String HARD_OPTION_STRING = "Hard";
    static final String SMALL_BOARD_OPTION_STRING = "Small Board";
    static final String MEDIUM_BOARD_OPTION_STRING = "Medium Board";
    static final String LARGE_BOARD_OPTION_STRING = "Large Board";

    static final String FLAG_BUTTON_ACTION = "Flag Button";

    private int spacing;
    private int boardSizeX;
    private int boardSizeY;
    private int cellSize;
    private int gridHeight;
    private int gridWidth;

    private Board board; // logical board
    private Game game;

    private String gameStateString;
    private String gameResultString;
    private boolean continueIsVisible = false;

    // the listener for these elements is given in Manager in the init() method since
    // this class is constructed inside Manager's constructor
    private JButton continueButton;
    private JButton newGameButton;
    private JButton flagButton;

    private JRadioButtonMenuItem singleGameModeOption;
    private JRadioButtonMenuItem multilevelModeOption;
    private JRadioButtonMenuItem difficultyEasyOption;
    private JRadioButtonMenuItem difficultyMediumOption;
    private JRadioButtonMenuItem difficultyHardOption;
    private JRadioButtonMenuItem smallGridOption;
    private JRadioButtonMenuItem mediumGridOption;
    private JRadioButtonMenuItem largeGridOption;

    private BoardGUI boardGUI;



    private GUI(String gameStateString) {

        setLayout(null);

        this.gameStateString = gameStateString;
        gameResultString = "";

        board = Board.getInstance();
        game = Game.getInstance();

        setTitle("Mine Sweeper");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();

        TopLeftPanel optionsPanel = new TopLeftPanel();
        optionsPanel.setBounds(5, 5, OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT);
        add(optionsPanel);

        AnnouncementPanel announcementPanel = new AnnouncementPanel();
        announcementPanel.setBounds(5 + OPTIONS_PANEL_WIDTH + 5, 5, ANNOUNCEMENT_PANEL_WIDTH, ANNOUNCEMENT_PANEL_HEIGHT );
        add(announcementPanel);


        JMenu modeMenu = new JMenu("Mode");
        ButtonGroup modeButtonGroup = new ButtonGroup();
        singleGameModeOption = new JRadioButtonMenuItem(SINGLE_GAME_OPTION_STRING);
        multilevelModeOption = new JRadioButtonMenuItem(MULTILEVEL_OPTION_STRING);
        if (Manager.DEFAULT_MODE == Manager.Mode.MULTILEVEL) {
            multilevelModeOption.setSelected(true);
        }
        else if (Manager.DEFAULT_MODE == Manager.Mode.SINGLE_GAME) {
            singleGameModeOption.setSelected(true);
        }

        modeButtonGroup.add(singleGameModeOption);
        modeButtonGroup.add(multilevelModeOption);
        modeMenu.add(singleGameModeOption);
        modeMenu.add(multilevelModeOption);

        JMenu difficultyMenu = new JMenu("Difficulty");
        ButtonGroup difficultyButtonGroup = new ButtonGroup();
        difficultyEasyOption = new JRadioButtonMenuItem(EASY_OPTION_STRING);
        difficultyMediumOption = new JRadioButtonMenuItem(MEDIUM_OPTION_STRING);
        difficultyHardOption = new JRadioButtonMenuItem(HARD_OPTION_STRING);

        if (Manager.DEFAULT_DIFFICULTY == Manager.Difficulty.EASY) {
            difficultyEasyOption.setSelected(true);
        }
        else if (Manager.DEFAULT_DIFFICULTY == Manager.Difficulty.MEDIUM) {
            difficultyMediumOption.setSelected(true);
        }
        else if (Manager.DEFAULT_DIFFICULTY == Manager.Difficulty.HARD) {
            difficultyHardOption.setSelected(true);
        }

        difficultyButtonGroup.add(difficultyEasyOption);
        difficultyButtonGroup.add(difficultyMediumOption);
        difficultyButtonGroup.add(difficultyHardOption);
        difficultyMenu.add(difficultyEasyOption);
        difficultyMenu.add(difficultyMediumOption);
        difficultyMenu.add(difficultyHardOption);

        JMenu boardSizeMenu = new JMenu("Board Size");
        ButtonGroup boardSizeButtonGroup = new ButtonGroup();
        smallGridOption = new JRadioButtonMenuItem(SMALL_BOARD_OPTION_STRING);
        mediumGridOption = new JRadioButtonMenuItem(MEDIUM_BOARD_OPTION_STRING);
        largeGridOption = new JRadioButtonMenuItem(LARGE_BOARD_OPTION_STRING);

        if (board.getSizeY() == Manager.SMALL_GRID_SIZE_Y) {
            smallGridOption.setSelected(true);
        }
        else if (board.getSizeY() == Manager.MEDIUM_GRID_SIZE_Y) {
            mediumGridOption.setSelected(true);
        }
        else if (board.getSizeY() == Manager.LARGE_GRID_SIZE_Y) {
            largeGridOption.setSelected(true);
        }

        boardSizeButtonGroup.add(smallGridOption);
        boardSizeButtonGroup.add(mediumGridOption);
        boardSizeButtonGroup.add(largeGridOption);
        boardSizeMenu.add(smallGridOption);
        boardSizeMenu.add(mediumGridOption);
        boardSizeMenu.add(largeGridOption);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(modeMenu);
        menuBar.add(difficultyMenu);
        menuBar.add(boardSizeMenu);

        setJMenuBar(menuBar);

        setVisible(true);
        setResizable(false);

        repaint();

    }

    void init() {
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

        if (boardGUI != null) remove(boardGUI);

        boardGUI = new BoardGUI();
        boardGUI.setBounds((PANEL_WIDTH - gridWidth) / 2, (PANEL_HEIGHT - gridHeight) * 6 / 9, gridWidth, gridHeight);

        add(boardGUI);
    }

    public static GUI getInstance(String str) {
        if (instance == null) {
            instance = new GUI(str);
        }
        return instance;
    }
    public static GUI getInstance() {
        if (instance == null) {
            instance = new GUI("");
        }
        return instance;
    }

    class BoardGUI extends JPanel {

        public BoardGUI() {
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            addMouseListener(game);
        }

        public void paintComponent(Graphics g) {
            Board.Cell[][] grid = board.getGrid();

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

                    Board.Cell cell = grid[i][j];
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

    class TopLeftPanel extends JPanel {

        TopLeftPanel() {
            setLayout(null);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            newGameButton = new JButton(NEWGAME_BUTTON_STRING);
            newGameButton.setBounds(2, 2, OPTIONS_PANEL_WIDTH - 4, OPTIONS_PANEL_HEIGHT / 2 - 3 );
            //newGameButton.setDisabledSelectedIcon(newGameButton.getDisabledSelectedIcon());

            // the listener for this button is given in Manager in the init() method since
            // this class is constructed inside Manager's constructor
            continueButton = new JButton(CONTINUE_BUTTON_STRING);
            continueButton.setBounds(2, OPTIONS_PANEL_HEIGHT / 2, OPTIONS_PANEL_WIDTH - 4, OPTIONS_PANEL_HEIGHT / 2 - 3);

            add(newGameButton);
            add(continueButton);

        }

        public void paintComponent(Graphics g) {
            continueButton.setEnabled(continueIsVisible);
        }
    }

    class AnnouncementPanel extends JPanel implements MouseListener {

        JLabel gameStateLabel;
        JLabel gameResultLabel;

        AnnouncementPanel() {
            setLayout(null);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            gameStateLabel = new JLabel();
            gameStateLabel.setFont(new Font("Monospaced", Font.BOLD, 23));
            gameStateLabel.setForeground(Color.BLACK);
            gameStateLabel.setVerticalAlignment(SwingConstants.CENTER);
            add(gameStateLabel);

            gameResultLabel = new JLabel();
            gameResultLabel.setFont(new Font("Monospaced", Font.BOLD, 30));
            gameResultLabel.setForeground(Color.BLACK);
            gameResultLabel.setVerticalAlignment(SwingConstants.CENTER);
            add(gameResultLabel);

            flagButton = new JButton(new ImageIcon(getClass().getResource("/res/gray_flag.png")));
            // below works too
            // flagButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("res/gray_flag.png")));
            flagButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            flagButton.setActionCommand(FLAG_BUTTON_ACTION);
            flagButton.addMouseListener(this);
            add(flagButton);

        }

        public void paintComponent(Graphics g) {

            gameStateLabel.setText(gameStateString);
            gameStateLabel.setBounds(35, 0, 250, ANNOUNCEMENT_PANEL_HEIGHT);

            gameResultLabel.setText(gameResultString);
            gameResultLabel.setBounds(295, 0, ANNOUNCEMENT_PANEL_WIDTH - 330, ANNOUNCEMENT_PANEL_HEIGHT);

            g.setColor(new JButton().getBackground());
            g.fillRect(0, 0, ANNOUNCEMENT_PANEL_WIDTH, ANNOUNCEMENT_PANEL_HEIGHT);
            g.setColor(Color.BLUE);
            g.fillRect(6, 6, ANNOUNCEMENT_PANEL_WIDTH - 12, ANNOUNCEMENT_PANEL_HEIGHT - 12);
            g.setColor(new JButton().getBackground());
            g.fillRect(9, 9, ANNOUNCEMENT_PANEL_WIDTH - 18, ANNOUNCEMENT_PANEL_HEIGHT - 18);

            flagButton.setBounds(PANEL_WIDTH - OPTIONS_PANEL_WIDTH - 125, 30, 64, 64);
            flagButton.setBackground(Color.lightGray);

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            flagButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createBevelBorder(BevelBorder.LOWERED), BorderFactory.createLineBorder(Color.DARK_GRAY, 1)));
            this.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            flagButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createBevelBorder(BevelBorder.RAISED), BorderFactory.createLineBorder(Color.DARK_GRAY, 1)));
            this.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            flagButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createBevelBorder(BevelBorder.RAISED), BorderFactory.createLineBorder(Color.DARK_GRAY, 1)));
            this.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            flagButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            this.repaint();
        }
    }



    /**
     * Get grid j index from JPanel X coordinate
     * @param x
     * @return
     */
    int getJfromX(int x) {
        return (x - 3 * spacing) / cellSize;
    }

    /**
     * Get grid i index from JPanel Y coordinate
     * @param y
     * @return
     */
    int getIfromY(int y) {
        return (y - 3 * spacing) / cellSize;
    }

    void setGameStateString(String str) { this.gameStateString = str; }

    void setGameResultString(String str) { this.gameResultString = str; }

    void setContinueIsVisible(boolean continueIsVisible) {
        this.continueIsVisible = continueIsVisible;
    }

    void setContinueButtonListener(ActionListener l) {
        continueButton.addActionListener(l);
    }

    void setNewGameButtonListener(ActionListener l) {
        newGameButton.addActionListener(l);
    }

    void setSingleGameModeListener(ActionListener l) {
        singleGameModeOption.addActionListener(l);
    }

    void setMultilevelModeListener(ActionListener l) {
        multilevelModeOption.addActionListener(l);
    }

    void setEasyDifficultyListener(ActionListener l) {
        difficultyEasyOption.addActionListener(l);
    }

    void setMediumDifficultyListener(ActionListener l) {
        difficultyMediumOption.addActionListener(l);
    }

    void setHardDifficultyListener(ActionListener l) {
        difficultyHardOption.addActionListener(l);
    }

    void setSmallGridListener(ActionListener l) {
        smallGridOption.addActionListener(l);
    }

    void setMediumGridListener(ActionListener l) {
        mediumGridOption.addActionListener(l);
    }

    void setLargeGridListener(ActionListener l) {
        largeGridOption.addActionListener(l);
    }

    void setFlagButtonListener(ActionListener l) {
        flagButton.addActionListener(l);
    }
}

