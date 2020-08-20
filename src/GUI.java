import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionListener;


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

    static final String CONTINUE_BUTTON__STRING = "Continue";
    static final String RESTART_BUTTON__STRING = "Restart";
    static final String SINGLE_GAME_OPTION_STRING = "Single-Game";
    static final String MULTILEVEL_OPTION_STRING = "Multilevel";
    static final String EASY_OPTION_STRING = "Easy";
    static final String MEDIUM_OPTION_STRING = "Medium";
    static final String HARD_OPTION_STRING = "Hard";
    static final String SMALL_GRID_OPTION_STRING = "Small";
    static final String MEDIUM_GRID_OPTION_STRING = "Medium";
    static final String LARGE_GRID_OPTION_STRING = "Large";

    private int spacing;
    private int boardSizeX;
    private int boardSizeY;
    private int cellSize;
    private int gridHeight;
    private int gridWidth;

    private Board board; // logical board
    private Game game;

    private String announcementString;
    private boolean continueIsVisible = false;

    // the listener for these elements is given in Main in the init() method since
    // this class is constructed inside Main's constructor
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


    private GUI(String announcementStr) {

        setLayout(null);

        this.announcementString = announcementStr;

        board = Board.getInstance();
        game = Game.getInstance();

        setTitle("Mine Sweeper");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();


        OptionsPanel optionsPanel = new OptionsPanel();
        optionsPanel.setBounds(5, 5, OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT);
        add(optionsPanel);

        AnnouncementPanel announcementPanel = new AnnouncementPanel(new BorderLayout());
        announcementPanel.setBounds(5 + OPTIONS_PANEL_WIDTH + 5, 5, ANNOUNCEMENT_PANEL_WIDTH, ANNOUNCEMENT_PANEL_HEIGHT );
        add(announcementPanel);


        //getContentPane().setBackground(Color.cyan);

        setVisible(true);
        setResizable(false);

        repaint();

    }

    void init() {
        boardSizeX = board.getSizeX();
        boardSizeY = board.getSizeY();

        switch (boardSizeX) {
            case Main.SMALL_GRID_SIZE_X:
                spacing = SMALL_GRID_SPACING;
                cellSize = SMALL_GRID_CELL_SIZE;
                break;
            case Main.MEDIUM_GRID_SIZE_X:
                spacing = MEDIUM_GRID_SPACING;
                cellSize = MEDIUM_GRID_CELL_SIZE;
                break;
            case Main.LARGE_GRID_SIZE_X:
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
            for (int i = 0; i < boardSizeY; ++i) {
                for (int j = 0; j < boardSizeX; ++j) {

                    Board.Cell cell = grid[i][j];
                    String str = "";
                    Game.State gameState = game.getState();

                    if (!cell.isVisible() && gameState != Game.State.WON) {
                        g.setColor(Color.gray);
                    }
                    else if (cell.isBomb()) {
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
                    g.fillRect(
                            startX + spacing + j * cellSize,
                            startY + spacing + i * cellSize,
                            cellSize - 2 * spacing,
                            cellSize - 2 * spacing
                    );
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

    class OptionsPanel extends JPanel {

        private JMenu modeMenu;
        private JMenu difficultyMenu;
        private JMenu boardSizeMenu;
        private ButtonGroup difficultyButtonGroup;
        private ButtonGroup boardSizeButtonGroup;

        public OptionsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            modeMenu = new JMenu("Mode");
            singleGameModeOption = new JRadioButtonMenuItem(SINGLE_GAME_OPTION_STRING);
            multilevelModeOption = new JRadioButtonMenuItem(MULTILEVEL_OPTION_STRING);
            modeMenu.add(singleGameModeOption);
            modeMenu.add(multilevelModeOption);

            difficultyMenu = new JMenu("Difficulty");
            difficultyButtonGroup = new ButtonGroup();
            difficultyEasyOption = new JRadioButtonMenuItem(EASY_OPTION_STRING);
            difficultyMediumOption = new JRadioButtonMenuItem(MEDIUM_OPTION_STRING);
            difficultyHardOption = new JRadioButtonMenuItem(HARD_OPTION_STRING);
            difficultyButtonGroup.add(difficultyEasyOption);
            difficultyButtonGroup.add(difficultyMediumOption);
            difficultyButtonGroup.add(difficultyHardOption);
            difficultyMenu.add(difficultyEasyOption);
            difficultyMenu.add(difficultyMediumOption);
            difficultyMenu.add(difficultyHardOption);

            boardSizeMenu = new JMenu("Board Size");
            boardSizeButtonGroup = new ButtonGroup();
            smallGridOption = new JRadioButtonMenuItem(SMALL_GRID_OPTION_STRING);
            mediumGridOption = new JRadioButtonMenuItem(MEDIUM_GRID_OPTION_STRING);
            largeGridOption = new JRadioButtonMenuItem(LARGE_GRID_OPTION_STRING);
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

            restartButton = new JButton("Restart");
            restartButton.setSize(OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT / 3);

            setJMenuBar(menuBar);
            add(restartButton);

        }

        public void paintComponent(Graphics g) {

            /*g.setColor(Color.WHITE);
            g.fillRect(0, 0, OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT);*/




        }
    }

    class AnnouncementPanel extends JPanel {


        JLabel announcementLabel;


        AnnouncementPanel(LayoutManager layout) {
            super(layout);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            // the listener for this button is given in Main in the init() method since
            // this class is constructed inside Main's constructor
            continueButton = new JButton("Continue");
            continueButton.setVisible(continueIsVisible);
            add(continueButton, BorderLayout.EAST);

            announcementLabel = new JLabel();
            announcementLabel.setFont(new Font("Monospace", Font.BOLD, 20));
            add(announcementLabel, BorderLayout.CENTER);
        }

        public void paintComponent(Graphics g) {

            announcementLabel.setText(announcementString);
            announcementLabel.setHorizontalAlignment(SwingConstants.CENTER);
            continueButton.setVisible(continueIsVisible);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, ANNOUNCEMENT_PANEL_WIDTH, ANNOUNCEMENT_PANEL_HEIGHT);

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

    void setAnnouncementString(String str) {
        this.announcementString = str;
    }

    void setContinueIsVisible(boolean continueIsVisible) {
        this.continueIsVisible = continueIsVisible;
    }

    void setContinueButtonListener(ActionListener l) {
        continueButton.addActionListener(l);
    }

    void setRestartButtonListener(ActionListener l) {
        restartButton.addActionListener(l);
    }

    public JButton getContinueButton() {
        return continueButton;
    }

    public JButton getRestartButton() {
        return restartButton;
    }

    public JRadioButtonMenuItem getSingleGameModeOption() {
        return singleGameModeOption;
    }

    void setSingleGameModeListener(ActionListener l) {
        singleGameModeOption.addActionListener(l);
    }

    public JRadioButtonMenuItem getMultilevelModeOption() {
        return multilevelModeOption;
    }

    void setMultilevelModeListener(ActionListener l) {
        multilevelModeOption.addActionListener(l);
    }

    public JRadioButtonMenuItem getDifficultyEasyOption() {
        return difficultyEasyOption;
    }

    void setEasyDifficultyListener(ActionListener l) {
        difficultyEasyOption.addActionListener(l);
    }

    public JRadioButtonMenuItem getDifficultyMediumOption() {
        return difficultyMediumOption;
    }

    void setMediumDifficultyListener(ActionListener l) {
        difficultyMediumOption.addActionListener(l);
    }

    public JRadioButtonMenuItem getDifficultyHardOption() {
        return difficultyHardOption;
    }

    void setHardDifficultyListener(ActionListener l) {
        difficultyHardOption.addActionListener(l);
    }

    public JRadioButtonMenuItem getSmallGridOption() {
        return smallGridOption;
    }

    void setSmallGridListener(ActionListener l) {
        smallGridOption.addActionListener(l);
    }

    public JRadioButtonMenuItem getMediumGridOption() {
        return mediumGridOption;
    }

    void setMediumGridListener(ActionListener l) {
        mediumGridOption.addActionListener(l);
    }

    public JRadioButtonMenuItem getLargeGridOption() {
        return largeGridOption;
    }

    void setLargeGridListener(ActionListener l) {
        largeGridOption.addActionListener(l);
    }
}
