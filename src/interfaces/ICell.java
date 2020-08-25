package interfaces;

public interface ICell {

    int getnCloseBombs();

    boolean isBomb();

    boolean isVisible();

    boolean hasFlag();

    void toggleFlag();
}
