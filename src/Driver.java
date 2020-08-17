import java.util.Scanner;

public class Driver {

    public static final int DEFAULT_SIZE = 8;
    public static final int STARTING_LEVEL = 2;
    public static final int WIN_LEVEL = 10;
    public static final int LEVEL_INCREMENT = 1;

    public static void main(String[] args) {

        // todo in GUI
        // the text-based option will start this.
        int size = DEFAULT_SIZE;
        int level = STARTING_LEVEL;
        boolean win = false;
        Scanner scan = new Scanner(System.in);

        System.out.println("\n*****MINE SWEEPER****\n");

        while(true) {
            while (level < WIN_LEVEL) {
                System.out.println("LEVEL: " + level + " bombs\n");
                Game game = new Game(size, level);
                level += LEVEL_INCREMENT;
                win = game.play();
                if (win) {
                    System.out.println("\nLevel completed!\n");
                }
                else break;
            }

            String message = win ? "\n\nCongratulations, you have won!\n" : "\n\nYou have lost.\n";
            System.out.println(message);

            String input = "";
            while (!input.trim().matches("^[yYnN]$")) {
                System.out.println("Wanna try again? (y | n)");
                input = scan.nextLine();
            }
            if (input.equals("n")) {
                break;
            }
            else {
                level = STARTING_LEVEL;
                win = false;
            }
        }

        System.out.println("\nThank you for playing!");
    }
}
