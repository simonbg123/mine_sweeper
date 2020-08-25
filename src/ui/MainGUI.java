package ui;

import domain.Board;
import domain.Game;
import domain.Manager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays the board, controls, menu and announcements.
 */
public class MainGUI extends JFrame implements ActionListener {

    private static final int WINDOW_WIDTH = 1186;
    private static final int WINDOW_HEIGHT = 829;
    static final int PANEL_WIDTH = 1180;
    static final int PANEL_HEIGHT = 800;

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

    private static final String BOARD_SIZE_CHANGE_WARNING = "This change will cause a new game to be started. Would you like to proceed?";
    private static final String BOARD_SIZE_CHANGE_WARNING_TITLE = "Board Size Change";


    private Board board; // logical board
    private Game game;
    private Manager manager;

    private BoardGUI boardGUI;
    private TopLeftPanel topLeftPanel;
    private AnnouncementPanel announcementPanel;

    public MainGUI() {

        board = Board.getInstance();
        game = Game.getInstance();
        manager = new Manager(board, game);

        setLayout(null);

        setTitle("Mine Sweeper");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        boardGUI = new BoardGUI(this, board, game);
        add(boardGUI);

        announcementPanel = new AnnouncementPanel(this, manager);
        add(announcementPanel);

        topLeftPanel = new TopLeftPanel(this);
        add(topLeftPanel);

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
        announcementPanel.setGameStateString();

        if (gameState == Game.State.WON) {
            if (manager.getCurrentLevel() == manager.getWinLevel()) {
                announcementPanel.setGameResultString(AnnouncementPanel.LEVELS_FINISHED_STRING);

            }
            else {
                announcementPanel.setGameResultString(AnnouncementPanel.LEVEL_COMPLETED_STRING);
                topLeftPanel.setContinueIsVisible(true);
            }
        }
        else if (gameState == Game.State.LOST) {
            announcementPanel.setGameResultString(AnnouncementPanel.GAME_LOST_STRING);
        }

        repaint(0);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String actionCommand = e.getActionCommand();
        switch (actionCommand) {
            case TopLeftPanel.CONTINUE_BUTTON_STRING:

                manager.setNextLevel();

                topLeftPanel.setContinueIsVisible(false);
                announcementPanel.setGameStateString();
                announcementPanel.setGameResultString(AnnouncementPanel.NO_RESULT);
                refresh();
                break;

            case TopLeftPanel.NEWGAME_BUTTON_STRING:

                manager.setNewGame();

                topLeftPanel.setContinueIsVisible(false);
                announcementPanel.setGameStateString();
                announcementPanel.setGameResultString(AnnouncementPanel.NO_RESULT);
                refresh();
                break;

            case AnnouncementPanel.FLAG_BUTTON_ACTION:

                Game.State gameState = game.getState();
                if (gameState == Game.State.WON || gameState == Game.State.LOST) {
                    return;
                }
                // toggle Flag Buton
                manager.toggleFlagMode();
                refresh();

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
}

