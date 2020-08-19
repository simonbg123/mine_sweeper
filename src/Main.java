//todo make this configurable: one game, or increasing levels
// change size of board
// difficulty (for one game = number of bombs; for increasing levels, starting number of bombs + increment)
public class Main {

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
        winLevel = WIN_LEVEL;
        bombIncrement = setBombIncrement();
        currentLevel = 1;
        board.initialize(nBombs);
        gui = GUI.getInstance();
        game = Game.getInstance();

    }

    public static Main getInstance() {
        return instance;
    }

    private static void init() {
        // the Game singleton acquires its reference to GUI and Main
        // once all instances have been constructed
        Game.getInstance().initialize();
        //config.gui.addListener, etc.
    }

    void update(Game.State gameState) {

        if (gameState == Game.State.WON) {
            if (currentLevel == winLevel) {
                // todo announce victory
            }
            else {
                //todo announce level completed, show continue button (or directives to click anywhere)

                //todo below happens only when user presses continue
                /*nBombs += bombIncrement;
                ++currentLevel;
                String announcement = getLevelAnnouncement(currentLevel, nBombs);
                // todo announce current level
                board.initialize(nBombs);
                game.setState(Game.State.PLAYING);*/
                // will actually be displayed when user presses continue

            }
        }
        else if (gameState == Game.State.LOST) {
            // todo announce lost
        }
        else {
            //todo announce number of tiles remaining
        }

        gui.repaint();

    }

    private String getLevelAnnouncement(int level, int nBombs) {
        return "LEVEL " + level + "/" + winLevel + " : " + nBombs + " bombs";
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
        return 10 * board.getSizeX() * board.getSizeY() / 100;
    }

    //todo add event listeners linked to menu options to change Main's attributes
    // as well button listener for start button which will initialize the board
    // and reset Game
    // as well as continue button

    public static void main(String[] args) {

        init();
        // provide first announcement
    }
}
