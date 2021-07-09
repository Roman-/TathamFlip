package ru.bestsiteever.tathamflip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class FlipBoard extends View {
    private final int borderColor;
    private final int cellCleanColor;
    private final int cellMarkedColor;
    private int numCellsX = FlipLogic.DEFAULT_WIDTH_HEIGHT;
    private int numCellsY = FlipLogic.DEFAULT_WIDTH_HEIGHT;
    private FlipLogic flipLogic;
    private OnWinListener winListener;
    private GameState gameState;
    private final Paint paint = new Paint();

    public FlipBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gameState = new GameState();
        flipLogic = new FlipLogic(numCellsX, numCellsY);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.FlipBoard, 0, 0);
        try {
            borderColor = a.getInteger(R.styleable.FlipBoard_border_color, 0);
            cellCleanColor = a.getInteger(R.styleable.FlipBoard_cell_clean_color, 0);
            cellMarkedColor = a.getInteger(R.styleable.FlipBoard_cell_marked_color, 0);
        } finally {
            a.recycle();
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setOnWinListener(OnWinListener listener) {
        winListener = listener;
    }

    public void setBoardSize(int numCellsX, int numCellsY) {
        flipLogic = new FlipLogic(numCellsX, numCellsY);
        this.numCellsX = flipLogic.getNumCellsX();
        this.numCellsY = flipLogic.getNumCellsY();
        invalidate();
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);

        int dimension = Math.min(getMeasuredWidth(), getMeasuredHeight());

        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setAntiAlias(true);

        int cellWidth = canvas.getWidth() / numCellsX;
        int cellHeight = canvas.getHeight() / numCellsY;

        // black and white squares
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < numCellsX; ++i) {
            for (int j = 0; j < numCellsY; ++j) {
                paint.setColor(colorByCellState(flipLogic.getCellState(i, j)));
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight, paint);
            }
        }

        // borders
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(borderColor);
        paint.setStrokeWidth(2);
        // ver
        for (int i = 0; i <= numCellsX; ++i) {
            canvas.drawLine(cellWidth * i, 0,
                    cellWidth * i, canvas.getHeight(), paint);
        }
        // hor
        for (int j = 0; j <= numCellsY; ++j) {
            canvas.drawLine(0, cellHeight * j,
                    canvas.getWidth(), cellHeight * j, paint);
        }

        // solution
        if (gameState.isShowingSolution()) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.gray_700));
            paint.setStrokeWidth(5);
            int marginW = (int)(0.2f * cellWidth);
            int marginH = (int)(0.2f * cellHeight);
            boolean[][] solution = flipLogic.getSolution();
            // black and white squares
            for (int i = 0; i < numCellsX; ++i) {
                for (int j = 0; j < numCellsY; ++j) {
                    if (solution[i][j])
                        canvas.drawRect(i * cellWidth + marginW
                                ,j * cellHeight + marginH
                                , (i + 1) * cellWidth - marginW
                                ,(j + 1) * cellHeight - marginH
                                , paint);
                }
            }
        }
    }

    public void scramble() {
        flipLogic.scramble();
        gameState.scramble();
        invalidate();
    }

    public void solve() {
        flipLogic.reset();
        gameState.reset();
        invalidate();
    }

    protected int colorByCellState(int state) {
        switch(state) {
            case 0:
                return cellCleanColor;
            case 1:
                return cellMarkedColor;
            default:
                return 0;
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
        String debugAction = (event.getAction() == MotionEvent.ACTION_DOWN)
                ? "DOWN" : ((event.getAction() == MotionEvent.ACTION_POINTER_DOWN)
                ? "POINTER_DOWN" : ("i" + event.getAction()));
        Log.d("touch", debugAction);
         */

        if (event.getAction() != MotionEvent.ACTION_DOWN
                && event.getAction() != 261 // TODO this is second touch ??
                && event.getAction() != MotionEvent.ACTION_POINTER_DOWN) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        int coordX = (int)(x / (float)getWidth() * numCellsX);
        int coordY = (int)(y / (float)getHeight() * numCellsY);
        flipLogic.makeTurn(coordX, coordY);
        if (gameState.getState() == GameState.PlayState.PLAY_STATE_SCRAMBLED)
            gameState.startSolve();

        gameState.incNumMoves();

        if (gameState.getState() == GameState.PlayState.PLAY_STATE_SOLVING) {
            if (flipLogic.isSolved()) {
                gameState.endSolve();
                winListener.onWin(gameState);
                gameState.reset();
            }
        }

        invalidate(); // re-draw game board
        return true;
    }

    public void showSolution() {
        gameState.showSolution();
        invalidate();
    }
}
