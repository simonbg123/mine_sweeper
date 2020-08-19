import java.util.Scanner;

//todo make this configurable: one game, or increasing levels
public class Main {

    private static Main instance = new Main();

    public static final int DEFAULT_SIZE = 9;
    private static final int STARTING_NUMBER_OF_BOMBS = 2;
    private static final int WIN_LEVEL = 10;
    private static final int BOMB_INCREMENT = 1;

    private int startingNumberOfBombs;
    private int winLevel;
    private int bombIncrement;

    private GUI gui;
    private Game game;
    private Board board;

    private Main() {
        startingNumberOfBombs = STARTING_NUMBER_OF_BOMBS;
        winLevel = WIN_LEVEL;
        bombIncrement = BOMB_INCREMENT;
        board = Board.getInstance();
        board.initialize(startingNumberOfBombs);
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

    private boolean play() {

        int level = 1;
        boolean win = false;
        int nBombs = startingNumberOfBombs;

        while (level <= winLevel) {
            System.out.println(getLevelAnnouncement(level,nBombs));
            board.initialize(nBombs);
            level += 1;
            nBombs += bombIncrement;
            win = game.play();
            if (win) {
                //through GUI
                System.out.println("\nLevel completed!\n");
            }
            else {
                return false;
            }
        }

        return true;
    }

    private String getLevelAnnouncement(int level, int nBombs) {
        return "LEVEL " + level + "/" + winLevel + " : " + nBombs + " bombs";
    }

    // basic play through increasing levels
    //todo idea: make this a Thread. The run method loops and constantly repaints the GUI. Allows for cool effects
    // such as reacting immediately when mouse over changes color.
    public static void main(String[] args) {


        Scanner scan = new Scanner(System.in);

        init();

        Main config = Main.getInstance();
        while(true) {

            boolean win = config.play();

            // todo show in GUI instead
            String message = win ? "\n\nCongratulations, you have won!\n" : "\n\nYou have lost.\n";
            System.out.println(message);

            //todo will need mechanism to listen for user input
            String input = "";
            while (!input.trim().matches("^[yYnN]$")) {
                System.out.println("Wanna try again? (y | n)");
                input = scan.nextLine();
            }
            if (input.equals("n")) {
                break;
            }
            else {
                // moot. Possibly change config here.
                config.startingNumberOfBombs = STARTING_NUMBER_OF_BOMBS;
            }
        }

        System.out.println("\nThank you for playing!");
    }


}
