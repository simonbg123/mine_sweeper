import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Game implements MouseListener {

    enum State {
        PLAYING,
        WON,
        LOST
    }

    private static Game instance = null;

    private Board board;
    private GUI gui;
    private Main main;
    private State state;

    private Game() {
        board = Board.getInstance();
        state = State.PLAYING;
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    void initialize() {
        gui = GUI.getInstance();
        main = Main.getInstance();

    }

    void reset() {
        state = State.PLAYING;
    }

    void playTurn(int x, int y) {

        TurnResult result = board.flipTile(y, x);

        if (result == TurnResult.WIN) {
            state = State.WON;
        }
        else if (result == TurnResult.LOSS) {
            state = State.LOST;
        }

        main.update(state);

    }

    public State getState() {
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
        playTurn(j, i);
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
