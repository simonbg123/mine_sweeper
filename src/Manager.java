import interfaces.IManager;

public class Manager implements IManager {


    static final int SMALL_GRID_SIZE_Y = 10;
    static final int SMALL_GRID_SIZE_X = 18;
    static final int MEDIUM_GRID_SIZE_Y = 15;
    static final int MEDIUM_GRID_SIZE_X = 25;
    static final int LARGE_GRID_SIZE_Y = 20;
    static final int LARGE_GRID_SIZE_X = 36;
    private static final int WIN_LEVEL = 5;

    static final Difficulty DEFAULT_DIFFICULTY = Difficulty.MEDIUM;
    static final Mode DEFAULT_MODE = Mode.MULTILEVEL;
    static final int DEFAULT_GRID_SIZE_Y = MEDIUM_GRID_SIZE_Y;
    static final int DEFAULT_GRID_SIZE_X = MEDIUM_GRID_SIZE_X;

    private Mode mode;
    private Difficulty difficulty;
    private int nBombs;
    private int winLevel;
    private int bombIncrement;
    private int currentLevel;

    private Board board;
    private Game game;

    public Manager(Board board, Game game) {
        this.board = board;
        this.game = game;
        mode = DEFAULT_MODE;
        difficulty = DEFAULT_DIFFICULTY;
        nBombs = setInitialnBombs();
        winLevel = setWinLevel();
        bombIncrement = setBombIncrement();
        currentLevel = 1;
        board.initialize(nBombs);

    }


    private int setInitialnBombs() {
        int nBombs = 0;
        int sizeX = board.getSizeX();
        int sizeY = board.getSizeY();
        if (mode == Mode.MULTILEVEL) {
            switch (difficulty){
                case EASY:
                    nBombs = 3 * sizeX * sizeY / 100;
                    break;
                case MEDIUM:
                    nBombs = 8 * sizeX * sizeY / 100;
                    break;
                case HARD:
                    nBombs = 13 * sizeX * sizeY / 100;
            }
        }
        else if (mode == Mode.SINGLE_GAME) {
            switch (difficulty) {
                case EASY:
                    nBombs = 7 * sizeX * sizeY / 100;
                    break;
                case MEDIUM:
                    nBombs = 12 * sizeX * sizeY / 100;
                    break;
                case HARD:
                    nBombs = 17 * sizeX * sizeY / 100;
            }
        }
        return nBombs;
    }

    private int setBombIncrement() {
        return 8 * board.getSizeX() * board.getSizeY() / 100 / winLevel;
    }

    private int setWinLevel() {
        return mode == Mode.MULTILEVEL ? WIN_LEVEL : 1;
    }

    @Override
    public void setNextLevel(){
        nBombs += bombIncrement;
        ++currentLevel;
        board.initialize(nBombs);
        game.reset();
    }

    @Override
    public void setNewGame() {
        nBombs = setInitialnBombs();
        bombIncrement = setBombIncrement();
        winLevel = setWinLevel();
        currentLevel = 1;
        board.initialize(nBombs);
        game.reset();
    }

    @Override
    public void toggleFlagMode() {
        game.setState( game.getState() == Game.State.PLACING_FLAGS ? Game.State.PLAYING : Game.State.PLACING_FLAGS);
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void setBoardSizeAndRestart(int sizeX, int sizeY) {

        board.setGrid(sizeX, sizeY);
        setNewGame();
    }

    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public int getWinLevel() {
        return winLevel;
    }

    @Override
    public int getnBombs() {
        return nBombs;
    }
}
