import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.MenuListener;
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

    private JMenu modeMenu;
    private JMenu difficultyMenu;

    private JRadioButtonMenuItem singleGameMode;
    private JRadioButtonMenuItem multilevelMode;
    private JRadioButtonMenuItem difficultyEasy;
    private JRadioButtonMenuItem difficultyMedium;
    private JRadioButtonMenuItem difficultyHard;


    private GUI(String announcementStr) {

        setLayout(null);

        this.announcementString = announcementStr;

        board = Board.getInstance();
        game = Game.getInstance();

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


        setTitle("Mine Sweeper");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BoardGUI gameBoard = new BoardGUI();
        gameBoard.setBounds((PANEL_WIDTH - gridWidth) / 2, (PANEL_HEIGHT - gridHeight) * 6 / 9, gridWidth, gridHeight);
        add(gameBoard);

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

        public OptionsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            modeMenu = new JMenu("Mode");
            modeMenu.setSize(new Dimension(OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT / 3));
            singleGameMode = new JRadioButtonMenuItem(SINGLE_GAME_OPTION_STRING);
            multilevelMode = new JRadioButtonMenuItem(MULTILEVEL_OPTION_STRING);
            modeMenu.add(singleGameMode);
            modeMenu.add(multilevelMode);

            difficultyMenu = new JMenu("Difficulty");
            difficultyMenu.setSize(OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT / 3);
            difficultyEasy = new JRadioButtonMenuItem(EASY_OPTION_STRING);
            difficultyMedium = new JRadioButtonMenuItem(MEDIUM_OPTION_STRING);
            difficultyHard = new JRadioButtonMenuItem(HARD_OPTION_STRING);
            difficultyMenu.add(difficultyEasy);
            difficultyMenu.add(difficultyMedium);
            difficultyMenu.add(difficultyHard);

            JMenuBar menuBar = new JMenuBar();
            menuBar.add(modeMenu);
            menuBar.add(difficultyMenu);

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
}
