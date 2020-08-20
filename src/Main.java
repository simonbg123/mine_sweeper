import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//todo make this configurable: one game, or increasing levels
// change size of board
// difficulty (for one game = number of bombs; for increasing levels, starting number of bombs + increment)
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

    private static Main instance = new Main();

    public static final int SMALL_GRID_SIZE_Y = 10;
    public static final int SMALL_GRID_SIZE_X = 18;
    public static final int MEDIUM_GRID_SIZE_Y = 15;
    public static final int MEDIUM_GRID_SIZE_X = 25;
    public static final int LARGE_GRID_SIZE_Y = 20;
    public static final int LARGE_GRID_SIZE_X = 36;
    private static final int WIN_LEVEL = 8;

    private static String LEVEL_COMPLETED = "Level completed!";
    private static String LEVELS_FINISHED = "Congratulations, you have won!!!";
    private static String GAME_LOST = "You have lost. Try again!";

    private Mode mode;
    private Difficulty difficulty;
    private int nBombs;
    private int winLevel;
    private int bombIncrement;
    private int currentLevel;

    private GUI gui;
    private Game game;
    private Board board;

    private Main() {
        board = Board.getInstance();
        mode = Mode.MULTILEVEL;
        difficulty = Difficulty.EASY;
        nBombs = setInitialnBombs();
        winLevel = setWinLevel();
        bombIncrement = setBombIncrement();
        currentLevel = 1;
        board.initialize(nBombs);
        gui = GUI.getInstance(getLevelAnnouncement());
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

        if (gameState == Game.State.WON) {
            if (currentLevel == winLevel) {
                gui.setAnnouncementString(LEVELS_FINISHED);
            }
            else {
                gui.setAnnouncementString(LEVEL_COMPLETED);
                gui.setContinueIsVisible(true);
            }
        }
        else if (gameState == Game.State.LOST) {
            gui.setAnnouncementString(GAME_LOST);
        }
        else if (gameState == Game.State.PLAYING){
            gui.setAnnouncementString(getLevelAnnouncement());

        }

        gui.repaint(1);

    }

    private String getLevelAnnouncement() {
        return  "<html>LEVEL " + currentLevel + "/" + winLevel + "<br>" +
                "BOMBS      : " + nBombs + "<br>" +
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

        Object o = e.getSource();

        if (o == gui.getContinueButton()) {

            gui.setContinueIsVisible(false);

            nBombs += bombIncrement;
            ++currentLevel;
            board.initialize(nBombs);
            gui.setAnnouncementString(getLevelAnnouncement());
            game.setState(Game.State.PLAYING);

            gui.repaint();
        }
        else if (o == gui.getRestartButton()) {

            restartGame();
        }
        else if (o == gui.getSingleGameModeOption()) {
            mode = Mode.SINGLE_GAME;
        }
        else if (o == gui.getMultilevelModeOption()) {
            mode = Mode.MULTILEVEL;
        }
        else if (o == gui.getDifficultyEasyOption()) {
            difficulty = Difficulty.EASY;
        }
        else if (o == gui.getDifficultyMediumOption()) {
            difficulty = Difficulty.MEDIUM;
        }
        else if (o == gui.getDifficultyHardOption()) {
            difficulty = Difficulty.HARD;
        }
        else if (o == gui.getSmallGridOption()) {
            board.setSizeX(SMALL_GRID_SIZE_X);
            board.setSizeY(SMALL_GRID_SIZE_Y);
            board.setGrid();
            gui.init();
            restartGame();
        }
        else if (o == gui.getMediumGridOption()) {
            board.setSizeX(MEDIUM_GRID_SIZE_X);
            board.setSizeY(MEDIUM_GRID_SIZE_Y);
            board.setGrid();
            gui.init();
            restartGame();
        }
        else if (o == gui.getLargeGridOption()) {
            board.setSizeX(LARGE_GRID_SIZE_X);
            board.setSizeY(LARGE_GRID_SIZE_Y);
            board.setGrid();
            gui.init();
            restartGame();

        }
    }

    private void restartGame() {
        nBombs = setInitialnBombs();
        bombIncrement = setBombIncrement();
        winLevel = setWinLevel();
        currentLevel = 1;
        board.initialize(nBombs);
        gui.setAnnouncementString(getLevelAnnouncement());
        game.reset();

        gui.repaint();

    }


    public static void main(String[] args) {

        Main.getInstance().init();
    }
}
