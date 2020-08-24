import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Manages game and board settings. Manages the overall game flow by monitoring
 * level completion, handling game restart and continue events.
 * Also manages game status announcements and GUI refreshing.
 */
public class Manager implements ActionListener {

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
    private static final int WIN_LEVEL = 5;

    static final Difficulty DEFAULT_DIFFICULTY = Difficulty.MEDIUM;
    static final Mode DEFAULT_MODE = Mode.MULTILEVEL;
    static final int DEFAULT_GRID_SIZE_Y = MEDIUM_GRID_SIZE_Y;
    static final int DEFAULT_GRID_SIZE_X = MEDIUM_GRID_SIZE_X;

    private static final String LEVEL_COMPLETED = "LEVEL COMPLETED!";
    private static final String LEVELS_FINISHED = "CONGRATULATIONS, YOU WIN!!!";
    private static final String GAME_LOST = "BOOM! GAME OVER. TRY AGAIN!";

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

    private static Manager instance = new Manager();

    private Manager() {
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

    public static Manager getInstance() {
        return instance;
    }

    void init() { // Manager has to be fully constructed for the below operations to take place
        Game.getInstance().initialize();
        gui.setContinueButtonListener(this);
        gui.setNewGameButtonListener(this);
        gui.setSingleGameModeListener(this);
        gui.setMultilevelModeListener(this);
        gui.setEasyDifficultyListener(this);
        gui.setMediumDifficultyListener(this);
        gui.setHardDifficultyListener(this);
        gui.setSmallGridListener(this);
        gui.setMediumGridListener(this);
        gui.setLargeGridListener(this);
        gui.setFlagButtonListener(this);
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

        gui.repaint(0);

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
                    nBombs = 8 * sizeX * sizeY / 100;
                    break;
                case HARD:
                    nBombs = 13 * sizeX * sizeY / 100;
            }
        }
        else if (mode == Mode.SINGLE_GAME) {
            switch (difficulty) {
                case EASY:
                    nBombs = 7 * sizeX * sizeY / 100;
                    break;
                case MEDIUM:
                    nBombs = 12 * sizeX * sizeY / 100;
                    break;
                case HARD:
                    nBombs = 17 * sizeX * sizeY / 100;
            }
        }
        return nBombs;
    }

    private int setBombIncrement() {
        return 8 * board.getSizeX() * board.getSizeY() / 100 / winLevel;
    }

    private int setWinLevel() {
        return mode == Mode.MULTILEVEL ? WIN_LEVEL : 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String actionCommand = e.getActionCommand();

        switch (actionCommand) {
            case GUI.FLAG_BUTTON_ACTION:
                Game.State gameState = game.getState();
                if (gameState == Game.State.WON || gameState == Game.State.LOST) {
                    return;
                }
                // toggle Flag Buton
                game.setState( gameState == Game.State.PLACING_FLAGS ? Game.State.PLAYING : Game.State.PLACING_FLAGS);
                gui.repaint();
                break;
            case GUI.CONTINUE_BUTTON_STRING:
                gui.setContinueIsVisible(false);

                nBombs += bombIncrement;
                ++currentLevel;
                board.initialize(nBombs);
                gui.setGameStateString(getGameStateString());
                game.reset();
                gui.setGameResultString("");

                gui.repaint();
                break;
            case GUI.NEWGAME_BUTTON_STRING:
                startNewGame();
                break;
            case GUI.SINGLE_GAME_OPTION_STRING:
                mode = Mode.SINGLE_GAME;
                break;
            case GUI.MULTILEVEL_OPTION_STRING:
                mode = Mode.MULTILEVEL;
                break;
            case GUI.EASY_OPTION_STRING:
                difficulty = Difficulty.EASY;
                break;
            case GUI.MEDIUM_OPTION_STRING:
                difficulty = Difficulty.MEDIUM;
                break;
            case GUI.HARD_OPTION_STRING:
                difficulty = Difficulty.HARD;
                break;
            case GUI.SMALL_BOARD_OPTION_STRING:
                if (board.getSizeX() == SMALL_GRID_SIZE_X) return; // current value was selected
                processBoardSizeChangeRequest(actionCommand);
                break;
            case GUI.MEDIUM_BOARD_OPTION_STRING:
                if (board.getSizeX() == MEDIUM_GRID_SIZE_X) return; // current value was selected
                processBoardSizeChangeRequest(actionCommand);
                break;
            case GUI.LARGE_BOARD_OPTION_STRING:
                if (board.getSizeX() == LARGE_GRID_SIZE_X) return; // current value was selected
                processBoardSizeChangeRequest(actionCommand);
                break;
        }
    }

    private void startNewGame() {
        gui.setContinueIsVisible(false);

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
        startNewGame();
    }

    int getnBombs() {
        return nBombs;
    }
}
