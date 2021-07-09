package ru.bestsiteever.tathamflip;

import android.os.SystemClock;

public class GameState {
    // number of moves it took to solve
    private int numMoves = 0;
    // moment in time when solve has been started
    private long startedSolve = 0;
    // moment in time when solve has been started
    private long finishedSolve = 0;
    private PlayState state = PlayState.PLAY_STATE_IDLE;
    private boolean isShowingSolution = false;

    public boolean isShowingSolution() {
        return isShowingSolution;
    }

    public void showSolution() {
        isShowingSolution = true;
    }

    public PlayState getState() {
        return state;
    }

    public enum PlayState {
        PLAY_STATE_IDLE, // after reset or after solved
        PLAY_STATE_SCRAMBLED, // right after scramble but before first move
        PLAY_STATE_SOLVING, // during the solve, timer is ticking
    }

    public int getNumMoves() {
        return numMoves;
    }

    public long getMs() {
        // hasn't yet started
        if (0 == startedSolve)
            return 0;
        // is running
        if (0 == finishedSolve)
            return SystemClock.elapsedRealtime() - startedSolve;
        // is finished
        return finishedSolve - startedSolve;
    }

    // returns "2.17"
    public String getTimeFormatted() {
        return String.format("%.2f", (float)getMs()/1000f); // TODO colon-separated
    }

    public void incNumMoves() {
        ++numMoves;
    }

    public void scramble() {
        state = PlayState.PLAY_STATE_SCRAMBLED;
        startedSolve = 0;
        finishedSolve = 0;
        numMoves = 0;
        isShowingSolution = false;
    }

    public void reset() {
        state = PlayState.PLAY_STATE_IDLE;
        startedSolve = 0;
        finishedSolve = 0;
        numMoves = 0;
        isShowingSolution = false;
    }

    // records solve start time (after first move)
    public void startSolve() {
        startedSolve = SystemClock.elapsedRealtime();
        finishedSolve = 0;
        state = PlayState.PLAY_STATE_SOLVING;
    }

    // records solve end time (after last move)
    public void endSolve() {
        finishedSolve = SystemClock.elapsedRealtime();
        state = PlayState.PLAY_STATE_IDLE;
    }
}
