import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Manages tile revealing events and state of a particular game
 */
public class Game implements MouseListener {

    enum State {
        PLAYING,
        WON,
        LOST
    }

    private static Game instance = null;

    private Board board;
    private GUI gui;
    private Manager manager;
    private State state;
    private boolean isFirstMove;

    private Game() {
        board = Board.getInstance();
        state = State.PLAYING;
        isFirstMove = true;
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    void initialize() {
        gui = GUI.getInstance();
        manager = Manager.getInstance();

    }

    void reset() {
        state = State.PLAYING;
        isFirstMove = true;
    }

    void revealTile(int i, int j) {

        TileRevealResult result = board.revealTile(i, j);

        if (isFirstMove) { // guarantees a first good move
            while (result == TileRevealResult.LOSS) {
                board.initialize(manager.getnBombs());
                result = board.revealTile(i, j);
            }
            isFirstMove = false;
        }


        if (result == TileRevealResult.WIN) {
            state = State.WON;
        }
        else if (result == TileRevealResult.LOSS) {
            state = State.LOST;
        }

        manager.update(state);

    }

    State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (!(state == State.PLAYING)) return;

        int i = gui.getIfromY(e.getY());
        int j = gui.getJfromX(e.getX());

        if (board.tileIsVisible(i, j)) return; // title was already visible

        revealTile(i, j);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
