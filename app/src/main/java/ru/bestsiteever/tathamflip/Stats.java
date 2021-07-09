package ru.bestsiteever.tathamflip;

import java.util.ArrayList;

public class Stats {
    private class ResultEntry {
        public int numMoves;
        public long ms;
        public ResultEntry(int numMoves, long ms) {
            this.numMoves = numMoves;
            this.ms = ms;
        }
    }

    private ArrayList<ResultEntry> results = new ArrayList<>();

    public void addResult(int numMoves, long ms) {
        results.add(new ResultEntry(numMoves, ms));
    }

    public void reset() {
        results.clear();
    }

    public int getNumResults() {
        return results.size();
    }

    // TODO get averages, get best etc.
}
