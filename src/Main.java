import java.util.Scanner;

//todo make this configurable: one game, or increasing levels
public class Main {

    private static final int DEFAULT_SIZE = 8;
    private static final int STARTING_LEVEL = 2;
    private static final int WIN_LEVEL = 10;
    private static final int LEVEL_INCREMENT = 1;

    private int board_size;
    private int startingLevel;
    private int winLevel;
    private int levelIncrement;

    private GUI gui;

    public Main(GUI gui) {
        this.gui = gui;
        board_size = DEFAULT_SIZE;
        startingLevel = STARTING_LEVEL;
        winLevel = WIN_LEVEL;
        levelIncrement = LEVEL_INCREMENT;
    }

    public Main(GUI gui, int board_size, int startingLevel, int winLevel, int getLevelIncrement) {
        this.gui = gui;
        this.board_size = board_size;
        this.startingLevel = startingLevel;
        this.winLevel = winLevel;
        this.levelIncrement = getLevelIncrement;
    }

    private boolean play() {

        int level = startingLevel;
        boolean win = false;

        while (level < winLevel) {
            System.out.println("LEVEL: " + level + " bombs\n");
            Game game = new Game(board_size, level);
            level += levelIncrement;
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

    // basic play through increasing levels
    public static void main(String[] args) {


        Scanner scan = new Scanner(System.in);

        GUI gui = new GUI();

        Main config = new Main(gui);




        while(true) {

            boolean win = config.play();

            // todo show in GUI instead
            String message = win ? "\n\nCongratulations, you have won!\n" : "\n\nYou have lost.\n";
            System.out.println(message);

            //todo will need mechanism to listen for user input
            // offer a one-board game: change the settings for that
            // will need a Main object to contain those settings
            // keep the final statics though.
            // depending on user input: build a Main with defaults
            // or other values.
            //
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
                config.startingLevel = STARTING_LEVEL;
            }
        }

        System.out.println("\nThank you for playing!");
    }
}
