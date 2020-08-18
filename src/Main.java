import java.util.Scanner;

//todo make this configurable: one game, or increasing levels
public class Main {

    public static final int DEFAULT_SIZE = 8;
    private static final int STARTING_LEVEL = 2;
    private static final int WIN_LEVEL = 10;
    private static final int LEVEL_INCREMENT = 1;

    private int startingLevel;
    private int winLevel;
    private int levelIncrement;

    private GUI gui;
    private Game game;

    public Main(GUI gui, Game game) {
        this.gui = gui;
        this.game = game;
        startingLevel = STARTING_LEVEL;
        winLevel = WIN_LEVEL;
        levelIncrement = LEVEL_INCREMENT;
    }



    private boolean play() {

        int level = startingLevel;
        boolean win = false;

        while (level < winLevel) {
            System.out.println("LEVEL: " + level + " bombs\n");
            game.initialize(level);
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
    //todo idea: make this a Thread. The run method loops and constantly repaints the GUI. Allows for cool effects
    // such as reacting immediately when mouse over changes color.
    public static void main(String[] args) {


        Scanner scan = new Scanner(System.in);

        //default starting board
        //todo add options to change board size etc.
        Board board = new Board(DEFAULT_SIZE);
        Game game = new Game(board);
        GUI gui = new GUI(board, game);
        Main config = new Main(gui, game);

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
                config.startingLevel = STARTING_LEVEL;
            }
        }

        System.out.println("\nThank you for playing!");
    }
}
