package interfaces;

public interface IManager {

    enum Mode {
        SINGLE_GAME,
        MULTILEVEL
    }
    enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    void setNextLevel();
    void setNewGame();
    void toggleFlagMode();
    void setMode(Mode mode);
    void setDifficulty(Difficulty difficulty);
    void setBoardSizeAndRestart(int x, int y);

    int getCurrentLevel();
    int getWinLevel();
    int getnBombs();


}
