//todo make this configurable: one game, or increasing levels
// change size of board
// difficulty (for one game = number of bombs; for increasing levels, starting number of bombs + increment)
public class Main {

    private static Main instance = new Main();

    public static final int DEFAULT_SIZE = 12;
    private static final int DEFAULT_NUMBER_OF_BOMBS = 4;
    private static final int WIN_LEVEL = 8;
    private static final int BOMB_INCREMENT = 1;

    private static String LEVEL_COMPLETED = "Level completed!";
    private static String LEVELS_FINISHED = "Congratulations, you have won!!!";
    private static String GAME_LOST = "You have lost. Try again!";


    private int nBombs;
    private int winLevel;
    private int bombIncrement;
    private int currentLevel;

    private GUI gui;
    private Game game;
    private Board board;

    private Main() {
        nBombs = DEFAULT_NUMBER_OF_BOMBS;
        winLevel = WIN_LEVEL;
        bombIncrement = BOMB_INCREMENT;
        currentLevel = 1;
        board = Board.getInstance();
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
    //todo add event listeners linked to menu options to change Main's attributes
    // as well button listener for start button which will initialize the board
    // and reset Game
    // as well as continue button

    public static void main(String[] args) {

        init();
        // provide first announcement
    }
}
