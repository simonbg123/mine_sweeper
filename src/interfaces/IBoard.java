package interfaces;

public interface IBoard {

    enum RevealResult {
        LOSS,
        WIN,
        CONTINUE
    }

    void initialize(int nBombs);
    void reinitialize();
    RevealResult revealTile(int i, int j);
    ICell getCell(int i, int j);
    int nTilesToUncover();
    int getSizeX();
    int getSizeY();
    void setGrid(int sizeX, int sizeY);
}
