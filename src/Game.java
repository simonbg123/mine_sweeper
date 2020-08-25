import interfaces.IBoard;
import interfaces.IGame;

/**
 * Manages tile revealing events and state of a particular game
 */
public class Game implements IGame {

    private static Game instance = null;

    private Board board;
    private State state;
    private boolean isFirstMove;

    private Game() {
        board = Board.getInstance();
        state = State.PLAYING;
        isFirstMove = true;
    }

    static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    void reset() {
        state = State.PLAYING;
        isFirstMove = true;
    }

    @Override
    public void revealTile(int i, int j) {

        var result = board.revealTile(i, j);

        if (isFirstMove) { // guarantees a first good move
            while (result == IBoard.RevealResult.LOSS) {
                board.reinitialize();
                result = board.revealTile(i, j);
            }
            isFirstMove = false;
        }


        if (result == IBoard.RevealResult.WIN) {
            state = State.WON;
        }
        else if (result == IBoard.RevealResult.LOSS) {
            state = State.LOST;
        }
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
}
