package ru.bestsiteever.tathamflip;

public class FlipLogic {
    public static final int INVALID_STATE = -1;
    public static final int SOLVED_STATE = 0; // solved = empty
    public static final int FILLED_STATE = 1;
    public static final int DEFAULT_WIDTH_HEIGHT = 4;

    public int getNumCellsX() {
        return numCellsX;
    }

    public int getNumCellsY() {
        return numCellsY;
    }

    private final int numCellsX;
    private final int numCellsY;
    private int[][] board;
    // cells to touch on the board to solve the puzzle. All false = puzzle already solved
    private boolean[][] solution;
    private int numTurnsToScramble;

    FlipLogic(int w, int h) {
        this.numCellsX = w;
        this.numCellsY = h;
        board = new int[numCellsX][numCellsY]; // board[x][y]
        solution = new boolean[numCellsX][numCellsY];
        numTurnsToScramble = w * h * 4;
    }

    // returns in solved state
    public void reset() {
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[i].length; ++j) {
                board[i][j] = SOLVED_STATE;
                solution[i][j] = false;
            }
        }
    }

    // scrambles
    public void scramble() {
        reset();
        for (int i = 0; i < numTurnsToScramble; ++i) {
            int x = (int)(Math.random() * numCellsX);
            int y = (int)(Math.random() * numCellsX);
            makeTurn(x, y);
        }
    }

    public boolean[][] getSolution() {
        return solution;
    }

    public int getCellState(int x, int y) {
        if (!areValidCoords(x,y))
            return INVALID_STATE;
        return board[x][y];
    }

    public void makeTurn(int x, int y) {
        solution[x][y] = !solution[x][y];
        // just the cross logic so far
        int[][] coordsToFlip = {{x,y}, {x+1,y}, {x-1,y}, {x,y+1}, {x,y-1}};
        for (int[] c : coordsToFlip)
            flipOne(c[0], c[1]);
    }

    public boolean isSolved() {
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[i].length; ++j) {
                if (SOLVED_STATE != board[i][j])
                    return false;
            }
        }
        return true;
    }

    private boolean areValidCoords(int x, int y) {
        return x >= 0 && x < numCellsX && y >= 0 && y < numCellsY;
    }

    private void flipOne(int x, int y) {
        if (!areValidCoords(x,y))
            return;
        board[x][y] = nextState(board[x][y]);
    }

    // >1 states with more sophisticated boards
    private static int nextState(int state) {
        return (state == SOLVED_STATE) ? FILLED_STATE : SOLVED_STATE;
    }

}
