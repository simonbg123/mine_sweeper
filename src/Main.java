import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main implements ActionListener {

    enum Mode {
        SINGLE_GAME,
        MULTILEVEL
    }
    enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }


    static final int SMALL_GRID_SIZE_Y = 10;
    static final int SMALL_GRID_SIZE_X = 18;
    static final int MEDIUM_GRID_SIZE_Y = 15;
    static final int MEDIUM_GRID_SIZE_X = 25;
    static final int LARGE_GRID_SIZE_Y = 20;
    static final int LARGE_GRID_SIZE_X = 36;
    private static final int WIN_LEVEL = 8;

    static final Difficulty DEFAULT_DIFFICULTY = Difficulty.EASY;
    static final Mode DEFAULT_MODE = Mode.MULTILEVEL;
    static final int DEFAULT_GRID_SIZE_Y = SMALL_GRID_SIZE_Y;
    static final int DEFAULT_GRID_SIZE_X = SMALL_GRID_SIZE_X;

    private static final String LEVEL_COMPLETED = "LEVEL COMPLETED!";
    private static final String LEVELS_FINISHED = "CONGRATULATIONS, YOU HAVE WON!!!";
    private static final String GAME_LOST = "YOU HAVE LOST. TRY AGAIN!";

    private static final String BOARD_SIZE_CHANGE_WARNING = "This change will cause a new game to be started. Would you like to proceed?";
    private static final String BOARD_SIZE_CHANGE_WARNING_TITLE = "Board Size Change";

    private Mode mode;
    private Difficulty difficulty;
    private int nBombs;
    private int winLevel;
    private int bombIncrement;
    private int currentLevel;

    private GUI gui;
    private Game game;
    private Board board;

    private static Main instance = new Main();


    private Main() {
        board = Board.getInstance();
        mode = DEFAULT_MODE;
        difficulty = DEFAULT_DIFFICULTY;
        nBombs = setInitialnBombs();
        winLevel = setWinLevel();
        bombIncrement = setBombIncrement();
        currentLevel = 1;
        board.initialize(nBombs);
        gui = GUI.getInstance(getGameStateString());
        game = Game.getInstance();
    }

    public static Main getInstance() {
        return instance;
    }

    private void init() {
        // the Game singleton acquires its reference to GUI and Main
        // once all instances have been constructed
        Game.getInstance().initialize();
        gui.setContinueButtonListener(this);
        gui.setRestartButtonListener(this);
        gui.setSingleGameModeListener(this);
        gui.setMultilevelModeListener(this);
        gui.setEasyDifficultyListener(this);
        gui.setMediumDifficultyListener(this);
        gui.setHardDifficultyListener(this);
        gui.setSmallGridListener(this);
        gui.setMediumGridListener(this);
        gui.setLargeGridListener(this);
    }

    void update(Game.State gameState) {

        gui.setGameStateString(getGameStateString());

        if (gameState == Game.State.WON) {
            if (currentLevel == winLevel) {
                gui.setGameResultString(LEVELS_FINISHED);

            }
            else {
                gui.setGameResultString(LEVEL_COMPLETED);
                gui.setContinueIsVisible(true);
            }
        }
        else if (gameState == Game.State.LOST) {
            gui.setGameResultString(GAME_LOST);
        }
        //else if (gameState == Game.State.PLAYING){}

        gui.repaint(1);

    }

    private String getGameStateString() {
        return  "<html>LEVEL &nbsp;&nbsp&nbsp;&nbsp&nbsp;: " + currentLevel + "/" + winLevel + "<br>" +
                "BOMBS &nbsp;&nbsp&nbsp;&nbsp&nbsp;: " + nBombs + "<br>" +
                "TILES LEFT : " + board.nTilesToUncover() + "</html>";
    }

    private int setInitialnBombs() {
        int nBombs = 0;
        int sizeX = board.getSizeX();
        int sizeY = board.getSizeY();
        if (mode == Mode.MULTILEVEL) {
            switch (difficulty){
                case EASY:
                    nBombs = 3 * sizeX * sizeY / 100;
                    break;
                case MEDIUM:
                    nBombs = 9 * sizeX * sizeY / 100;
                    break;
                case HARD:
                    nBombs = 15 * sizeX * sizeY / 100;
            }
        }
        else if (mode == Mode.SINGLE_GAME) {
            switch (difficulty) {
                case EASY:
                    nBombs = 4 * sizeX * sizeY / 100;
                    break;
                case MEDIUM:
                    nBombs = 14 * sizeX * sizeY / 100;
                    break;
                case HARD:
                    nBombs = 24 * sizeX * sizeY / 100;
            }
        }
        return nBombs;
    }

    private int setBombIncrement() {
        return 10 * board.getSizeX() * board.getSizeY() / 100 / winLevel;
    }

    private int setWinLevel() {
        return mode == Mode.MULTILEVEL ? WIN_LEVEL : 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String actionCommand = e.getActionCommand();

        if (actionCommand.equals(GUI.CONTINUE_BUTTON_STRING)) {

            gui.setContinueIsVisible(false);

            nBombs += bombIncrement;
            ++currentLevel;
            board.initialize(nBombs);
            gui.setGameStateString(getGameStateString());
            game.reset();
            gui.setGameResultString("");

            gui.repaint();
        }
        else if (actionCommand.equals(GUI.RESTART_BUTTON_STRING)) {
            restartGame();
        }
        else if (actionCommand.equals(GUI.SINGLE_GAME_OPTION_STRING)) {
            mode = Mode.SINGLE_GAME;
        }
        else if (actionCommand.equals(GUI.MULTILEVEL_OPTION_STRING)) {
            mode = Mode.MULTILEVEL;
        }
        else if (actionCommand.equals(GUI.EASY_OPTION_STRING)) {
            difficulty = Difficulty.EASY;
        }
        else if (actionCommand.equals(GUI.MEDIUM_OPTION_STRING)) {
            difficulty = Difficulty.MEDIUM;
        }
        else if (actionCommand.equals(GUI.HARD_OPTION_STRING)) {
            difficulty = Difficulty.HARD;
        }
        else if (actionCommand.equals(GUI.SMALL_BOARD_OPTION_STRING)) {
            if (board.getSizeX() == SMALL_GRID_SIZE_X) return; // current value was selected
            processBoardSizeChangeRequest(actionCommand);
        }
        else if (actionCommand.equals(GUI.MEDIUM_BOARD_OPTION_STRING)) {
            if (board.getSizeX() == MEDIUM_GRID_SIZE_X) return; // current value was selected
            processBoardSizeChangeRequest(actionCommand);
        }
        else if (actionCommand.equals(GUI.LARGE_BOARD_OPTION_STRING)) {
            if (board.getSizeX() == LARGE_GRID_SIZE_X) return; // current value was selected
            processBoardSizeChangeRequest(actionCommand);
        }
    }

    private void restartGame() {
        nBombs = setInitialnBombs();
        bombIncrement = setBombIncrement();
        winLevel = setWinLevel();
        currentLevel = 1;
        board.initialize(nBombs);
        gui.setGameStateString(getGameStateString());
        gui.setGameResultString("");
        game.reset();

        gui.repaint();

    }

    private void processBoardSizeChangeRequest(String change) {
        int answer = JOptionPane.showOptionDialog(
                gui,
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
            case GUI.SMALL_BOARD_OPTION_STRING:
                sizeX = SMALL_GRID_SIZE_X;
                sizeY = SMALL_GRID_SIZE_Y;
                break;
            case GUI.MEDIUM_BOARD_OPTION_STRING:
                sizeX = MEDIUM_GRID_SIZE_X;
                sizeY = MEDIUM_GRID_SIZE_Y;
                break;
            case GUI.LARGE_BOARD_OPTION_STRING:
                sizeX = LARGE_GRID_SIZE_X;
                sizeY = LARGE_GRID_SIZE_Y;
        }

        board.setSizeX(sizeX);
        board.setSizeY(sizeY);
        board.setGrid();
        gui.init();
        restartGame();
    }

    public int getnBombs() {
        return nBombs;
    }

    public static void main(String[] args) {

        Main.getInstance().init();
    }
}
