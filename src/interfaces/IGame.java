package interfaces;

public interface IGame {

    enum State {
        PLAYING,
        WON,
        LOST,
        PLACING_FLAGS
    }

    void revealTile(int i, int j);
    State getState();
    void setState(State state);
}
