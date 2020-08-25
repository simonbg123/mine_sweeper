import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Displays the board, controls, menu and announcements.
 */
class MainGUI extends JFrame implements ActionListener {

    private static final int WINDOW_WIDTH = 1186;
    private static final int WINDOW_HEIGHT = 829;
    static final int PANEL_WIDTH = 1180;
    static final int PANEL_HEIGHT = 800;

    private static final int OPTIONS_PANEL_WIDTH = 144;
    private static final int OPTIONS_PANEL_HEIGHT = 128;
    private static final int ANNOUNCEMENT_PANEL_WIDTH = PANEL_WIDTH - OPTIONS_PANEL_WIDTH - 25;
    private static final int ANNOUNCEMENT_PANEL_HEIGHT = 128;

    private static final String CONTINUE_BUTTON_STRING = "Continue";
    private static final String NEWGAME_BUTTON_STRING = "New Game";

    private static final String MODE_MENU_STRING = "Mode";
    private static final String DIFFICULTY_MENU_STRING = "Difficulty";
    private static final String BOARD_SIZE_MENU_STRING = "Board size";
    private static final String SINGLE_GAME_OPTION_STRING = "Single-Game";
    private static final String MULTILEVEL_OPTION_STRING = "Multilevel";
    private static final String EASY_OPTION_STRING = "Easy";
    private static final String MEDIUM_OPTION_STRING = "Medium";
    private static final String HARD_OPTION_STRING = "Hard";
    private static final String SMALL_BOARD_OPTION_STRING = "Small Board";
    private static final String MEDIUM_BOARD_OPTION_STRING = "Medium Board";
    private static final String LARGE_BOARD_OPTION_STRING = "Large Board";

    private static final String FLAG_BUTTON_ACTION = "Flag Button";

    private static final String LEVEL_COMPLETED_STRING = "LEVEL COMPLETED!";
    private static final String LEVELS_FINISHED_STRING = "CONGRATULATIONS, YOU WIN!!!";
    private static final String GAME_LOST_STRING = "BOOM! GAME OVER. TRY AGAIN!";
    private static final String NO_RESULT = "";

    private static final String BOARD_SIZE_CHANGE_WARNING = "This change will cause a new game to be started. Would you like to proceed?";
    private static final String BOARD_SIZE_CHANGE_WARNING_TITLE = "Board Size Change";


    private Board board; // logical board
    private Game game;
    private Manager manager;

    private String gameStateString;
    private String gameResultString;
    private boolean continueIsVisible = false;

    private BoardGUI boardGUI;


    public MainGUI() {

        board = Board.getInstance();
        game = Game.getInstance();
        manager = new Manager(board, game);

        setLayout(null);


        setGameStateString();
        setGameResultString(NO_RESULT);


        setTitle("Mine Sweeper");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        boardGUI = new BoardGUI(this, board, game);
        add(boardGUI);

        TopLeftPanel optionsPanel = new TopLeftPanel();
        add(optionsPanel);

        AnnouncementPanel announcementPanel = new AnnouncementPanel();
        add(announcementPanel);


        JMenu modeMenu = new JMenu(MODE_MENU_STRING);
        ButtonGroup modeButtonGroup = new ButtonGroup();
        var singleGameModeOption = new JRadioButtonMenuItem(SINGLE_GAME_OPTION_STRING);
        var multilevelModeOption = new JRadioButtonMenuItem(MULTILEVEL_OPTION_STRING);
        if (Manager.DEFAULT_MODE == Manager.Mode.MULTILEVEL) {
            multilevelModeOption.setSelected(true);
        }
        else if (Manager.DEFAULT_MODE == Manager.Mode.SINGLE_GAME) {
            singleGameModeOption.setSelected(true);
        }

        modeButtonGroup.add(singleGameModeOption);
        modeButtonGroup.add(multilevelModeOption);
        singleGameModeOption.addActionListener(this);
        multilevelModeOption.addActionListener(this);
        modeMenu.add(singleGameModeOption);
        modeMenu.add(multilevelModeOption);

        JMenu difficultyMenu = new JMenu(DIFFICULTY_MENU_STRING);
        ButtonGroup difficultyButtonGroup = new ButtonGroup();
        var difficultyEasyOption = new JRadioButtonMenuItem(EASY_OPTION_STRING);
        var difficultyMediumOption = new JRadioButtonMenuItem(MEDIUM_OPTION_STRING);
        var difficultyHardOption = new JRadioButtonMenuItem(HARD_OPTION_STRING);

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
        difficultyEasyOption.addActionListener(this);
        difficultyMediumOption.addActionListener(this);
        difficultyHardOption.addActionListener(this);
        difficultyMenu.add(difficultyEasyOption);
        difficultyMenu.add(difficultyMediumOption);
        difficultyMenu.add(difficultyHardOption);

        JMenu boardSizeMenu = new JMenu(BOARD_SIZE_MENU_STRING);
        ButtonGroup boardSizeButtonGroup = new ButtonGroup();
        var smallGridOption = new JRadioButtonMenuItem(SMALL_BOARD_OPTION_STRING);
        var mediumGridOption = new JRadioButtonMenuItem(MEDIUM_BOARD_OPTION_STRING);
        var largeGridOption = new JRadioButtonMenuItem(LARGE_BOARD_OPTION_STRING);

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
        smallGridOption.addActionListener(this);
        mediumGridOption.addActionListener(this);
        largeGridOption.addActionListener(this);
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

    void refresh() {

        Game.State gameState = game.getState();
        setGameStateString();

        if (gameState == Game.State.WON) {
            if (manager.getCurrentLevel() == manager.getWinLevel()) {
                gameResultString = LEVELS_FINISHED_STRING;

            }
            else {
                gameResultString = LEVEL_COMPLETED_STRING;
                setContinueIsVisible(true);
            }
        }
        else if (gameState == Game.State.LOST) {
            gameResultString = GAME_LOST_STRING;
        }

        repaint(0);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String actionCommand = e.getActionCommand();
        switch (actionCommand) {
            case SINGLE_GAME_OPTION_STRING:
                manager.setMode(Manager.Mode.SINGLE_GAME);
                break;

            case MULTILEVEL_OPTION_STRING:
                manager.setMode(Manager.Mode.MULTILEVEL);
                break;

            case EASY_OPTION_STRING:
                manager.setDifficulty(Manager.Difficulty.EASY);
                break;

            case MEDIUM_OPTION_STRING:
                manager.setDifficulty(Manager.Difficulty.MEDIUM);
                break;

            case HARD_OPTION_STRING:
                manager.setDifficulty(Manager.Difficulty.HARD);
                break;

            case SMALL_BOARD_OPTION_STRING:
                if (board.getSizeX() == Manager.SMALL_GRID_SIZE_X) return; // current value was selected
                processBoardSizeChangeRequest(actionCommand);
                break;
            case MEDIUM_BOARD_OPTION_STRING:
                if (board.getSizeX() == Manager.MEDIUM_GRID_SIZE_X) return; // current value was selected
                processBoardSizeChangeRequest(actionCommand);
                break;
            case LARGE_BOARD_OPTION_STRING:
                if (board.getSizeX() == Manager.LARGE_GRID_SIZE_X) return; // current value was selected
                processBoardSizeChangeRequest(actionCommand);
                break;
        }
    }

    private void processBoardSizeChangeRequest(String change) {
        int answer = JOptionPane.showOptionDialog(
                this,
                BOARD_SIZE_CHANGE_WARNING,
                BOARD_SIZE_CHANGE_WARNING_TITLE,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                JOptionPane.YES_OPTION
        );
        if (answer != JOptionPane.OK_OPTION) return;

        int sizeX = 0, sizeY = 0;

        switch (change) {
            case SMALL_BOARD_OPTION_STRING:
                sizeX = Manager.SMALL_GRID_SIZE_X;
                sizeY = Manager.SMALL_GRID_SIZE_Y;
                break;
            case MEDIUM_BOARD_OPTION_STRING:
                sizeX = Manager.MEDIUM_GRID_SIZE_X;
                sizeY = Manager.MEDIUM_GRID_SIZE_Y;
                break;
            case LARGE_BOARD_OPTION_STRING:
                sizeX = Manager.LARGE_GRID_SIZE_X;
                sizeY = Manager.LARGE_GRID_SIZE_Y;
        }

        manager.setBoardSizeAndRestart(sizeY, sizeX);
        boardGUI.setDimensions();
        refresh();
    }


    class TopLeftPanel extends JPanel implements ActionListener{

        private JButton continueButton;
        private JButton newGameButton;

        TopLeftPanel() {
            setLayout(null);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            setBounds(5, 5, OPTIONS_PANEL_WIDTH, OPTIONS_PANEL_HEIGHT);


            newGameButton = new JButton(NEWGAME_BUTTON_STRING);
            newGameButton.setBounds(2, 2, OPTIONS_PANEL_WIDTH - 4, OPTIONS_PANEL_HEIGHT / 2 - 3 );
            newGameButton.addActionListener(this);
            //newGameButton.setDisabledSelectedIcon(newGameButton.getDisabledSelectedIcon());

            // the listener for this button is given in Manager in the resetBoard() method since
            // this class is constructed inside Manager's constructor
            continueButton = new JButton(CONTINUE_BUTTON_STRING);
            continueButton.setBounds(2, OPTIONS_PANEL_HEIGHT / 2, OPTIONS_PANEL_WIDTH - 4, OPTIONS_PANEL_HEIGHT / 2 - 3);
            continueButton.addActionListener(this);

            add(newGameButton);
            add(continueButton);

        }

        public void paintComponent(Graphics g) {
            continueButton.setEnabled(continueIsVisible);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();

            switch (actionCommand) {
                case FLAG_BUTTON_ACTION:

                    Game.State gameState = game.getState();
                    if (gameState == Game.State.WON || gameState == Game.State.LOST) {
                        return;
                    }

                    // toggle Flag Buton
                    manager.toggleFlagMode();

                    refresh();
                    break;

                case CONTINUE_BUTTON_STRING:

                    manager.setNextLevel();

                    setContinueIsVisible(false);
                    setGameStateString();
                    setGameResultString(NO_RESULT);
                    refresh();
                    break;

                case NEWGAME_BUTTON_STRING:

                    manager.setNewGame();

                    setContinueIsVisible(false);
                    setGameStateString();
                    setGameResultString(NO_RESULT);
                    refresh();
                    break;
            }
        }
    }

    class AnnouncementPanel extends JPanel implements MouseListener, ActionListener {

        private JButton flagButton;

        JLabel gameStateLabel;
        JLabel gameResultLabel;

        AnnouncementPanel() {
            setLayout(null);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            setBounds(5 + OPTIONS_PANEL_WIDTH + 5, 5, ANNOUNCEMENT_PANEL_WIDTH, ANNOUNCEMENT_PANEL_HEIGHT );

            gameStateLabel = new JLabel();
            gameStateLabel.setFont(new Font("Monospaced", Font.BOLD, 23));
            gameStateLabel.setForeground(Color.BLACK);
            gameStateLabel.setVerticalAlignment(SwingConstants.CENTER);
            gameStateLabel.setBounds(35, 0, 250, ANNOUNCEMENT_PANEL_HEIGHT);
            add(gameStateLabel);

            gameResultLabel = new JLabel();
            gameResultLabel.setFont(new Font("Monospaced", Font.BOLD, 30));
            gameResultLabel.setForeground(Color.BLACK);
            gameResultLabel.setVerticalAlignment(SwingConstants.CENTER);
            gameResultLabel.setBounds(295, 0, ANNOUNCEMENT_PANEL_WIDTH - 330, ANNOUNCEMENT_PANEL_HEIGHT);
            add(gameResultLabel);

            flagButton = new JButton(new ImageIcon(getClass().getResource("/res/gray_flag.png")));
            // below works too
            // flagButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("res/gray_flag.png")));
            flagButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            flagButton.setBounds(PANEL_WIDTH - OPTIONS_PANEL_WIDTH - 125, 30, 64, 64);
            flagButton.setBackground(Color.lightGray);
            flagButton.setActionCommand(FLAG_BUTTON_ACTION);
            flagButton.addMouseListener(this);
            flagButton.addActionListener(this);
            add(flagButton);

        }

        public void paintComponent(Graphics g) {

            gameStateLabel.setText(gameStateString);
            gameResultLabel.setText(gameResultString);

            g.setColor(new JButton().getBackground());
            g.fillRect(0, 0, ANNOUNCEMENT_PANEL_WIDTH, ANNOUNCEMENT_PANEL_HEIGHT);
            g.setColor(Color.BLUE);
            g.fillRect(6, 6, ANNOUNCEMENT_PANEL_WIDTH - 12, ANNOUNCEMENT_PANEL_HEIGHT - 12);
            g.setColor(new JButton().getBackground());
            g.fillRect(9, 9, ANNOUNCEMENT_PANEL_WIDTH - 18, ANNOUNCEMENT_PANEL_HEIGHT - 18);
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

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(FLAG_BUTTON_ACTION)) {
                manager.toggleFlagMode();
                refresh();
            }
        }
    }





    void setGameStateString() {
        gameStateString = "<html>LEVEL &nbsp;&nbsp&nbsp;&nbsp&nbsp;: " + manager.getCurrentLevel() + "/" + manager.getWinLevel() + "<br>" +
                "BOMBS &nbsp;&nbsp&nbsp;&nbsp&nbsp;: " + manager.getnBombs() + "<br>" +
                "TILES LEFT : " + board.nTilesToUncover() + "</html>";
    }

    void setGameResultString(String gameResultString) {
        this.gameResultString = gameResultString;
    }

    void setContinueIsVisible(boolean continueIsVisible) {
        this.continueIsVisible = continueIsVisible;
    }

}

