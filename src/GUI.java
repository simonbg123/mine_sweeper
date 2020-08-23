import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionListener;

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
    private JButton restartButton;

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

        AnnouncementPanel announcementPanel = new AnnouncementPanel(new BorderLayout());
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
            for (int i = 0; i < boardSizeY; ++i) {
                for (int j = 0; j < boardSizeX; ++j) {

                    raised = false;
                    Board.Cell cell = grid[i][j];
                    String str = "";
                    Game.State gameState = game.getState();

                    if (cell.isVisible()) {
                        if (cell.isBomb()) {
                            if (gameState == Game.State.WON) {
                                g.setColor(Color.green);
                            }
                            else { g.setColor(Color.red); }

                            str = "(( ! ))";
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
                        if (cell.isBomb() && gameState != Game.State.PLAYING) {
                            if (gameState == Game.State.WON) {
                                g.setColor(Color.green);
                            }
                            else if (gameState == Game.State.LOST) {
                                g.setColor(Color.orange);
                            }
                            str = "(( ! ))";
                        }
                        else {
                            g.setColor(Color.gray);
                            raised = true;
                        }
                    }

                    if (raised) {
                        g.fill3DRect(
                                startX + spacing + j * cellSize,
                                startY + spacing + i * cellSize,
                                cellSize - 2 * spacing,
                                cellSize - 2 * spacing,
                                true
                        );
                    }
                    else {
                        g.fillRect(
                                startX + spacing + j * cellSize,
                                startY + spacing + i * cellSize,
                                cellSize - 2 * spacing,
                                cellSize - 2 * spacing
                        );
                    }
                    if (!str.isBlank()) {
                        g.setColor(Color.BLACK);
                        g.setFont(new Font("Monospace", Font.BOLD, cellSize*4/ (cell.isBomb()? 15: 9) ));
                        g.drawString(
                                str,
                                startX + spacing + j * cellSize + cellSize / (cell.isBomb()? 5 : 3),
                                startY + spacing + i * cellSize + cellSize * (cell.isBomb()? 13 : 15) / 24
                        );
                    }

                }
            }

        }
    }

    class TopLeftPanel extends JPanel {

        TopLeftPanel() {
            setLayout(null);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            restartButton = new JButton(NEWGAME_BUTTON_STRING);
            restartButton.setBounds(2, 2, OPTIONS_PANEL_WIDTH - 4, OPTIONS_PANEL_HEIGHT / 2 - 3 );
            //restartButton.setDisabledSelectedIcon(restartButton.getDisabledSelectedIcon());

            // the listener for this button is given in Manager in the init() method since
            // this class is constructed inside Manager's constructor
            continueButton = new JButton("Continue");
            continueButton.setBounds(2, OPTIONS_PANEL_HEIGHT / 2, OPTIONS_PANEL_WIDTH - 4, OPTIONS_PANEL_HEIGHT / 2 - 3);

            add(restartButton);
            add(continueButton);


        }

        public void paintComponent(Graphics g) {
            continueButton.setEnabled(continueIsVisible);
        }
    }

    class AnnouncementPanel extends JPanel {

        JLabel gameStateLabel;
        JLabel gameResultLabel;

        AnnouncementPanel(LayoutManager layout) {
            super(layout);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            gameStateLabel = new JLabel();
            gameStateLabel.setFont(new Font("Monospaced", Font.BOLD, 23));
            gameStateLabel.setForeground(Color.BLUE);
            gameStateLabel.setVerticalAlignment(SwingConstants.CENTER);
            add(gameStateLabel);

            gameResultLabel = new JLabel();
            gameResultLabel.setFont(new Font("Monospaced", Font.BOLD, 30));
            gameResultLabel.setForeground(Color.BLUE);
            gameResultLabel.setVerticalAlignment(SwingConstants.CENTER);
            add(gameResultLabel);

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

    void setRestartButtonListener(ActionListener l) {
        restartButton.addActionListener(l);
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
}
