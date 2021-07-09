package ru.bestsiteever.tathamflip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    private Button scrambleButton;
    private Button backButton;
    private Button resetButton;
    private RelativeLayout winLayout;
    private TextView winTitle;
    private TextView winInfo;
    private TextView topText;
    private FlipBoard board;
    private int numCellsX;
    private int numCellsY;
    private Stats stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        scrambleButton = (Button)findViewById(R.id.btn_new_game);
        backButton = (Button)findViewById(R.id.back_to_mm_btn);
        resetButton = (Button)findViewById(R.id.btn_reset_solve);
        //solutionButton = (Button)findViewById(R.id.btn_show_solution);
        board = (FlipBoard)findViewById(R.id.flipBoard);
        topText = (TextView)findViewById(R.id.top_game_text);
        winLayout = (RelativeLayout) findViewById(R.id.frame_win);
        winTitle = (TextView)findViewById(R.id.win_frame_title);
        winInfo = (TextView)findViewById(R.id.win_stats_text);
        stats = new Stats();
        numCellsX = getIntent().getIntExtra("numCellsX", FlipLogic.DEFAULT_WIDTH_HEIGHT);
        numCellsY = getIntent().getIntExtra("numCellsY", FlipLogic.DEFAULT_WIDTH_HEIGHT);

        board.setBoardSize(numCellsX, numCellsY);
        winLayout.setVisibility(View.GONE);

        // timer
        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                long ms = board.getGameState().getMs();
                long postDelayMillis = 50;
                if (ms > 0)
                    topText.setText(board.getGameState().getTimeFormatted());
                timerHandler.postDelayed(this, postDelayMillis);
            }
        };

        board.setOnWinListener(state -> {
            int numMoves = state.getNumMoves();
            long ms = state.getMs();
            String timeFormatted = board.getGameState().getTimeFormatted();
            //(Toast.makeText(GameActivity.this, "Thats a PB!", Toast.LENGTH_LONG)).show(); // TODO toast in case of PB or something
            winLayout.setVisibility(View.VISIBLE);
            stats.addResult(numMoves, ms);
            scrambleButton.setEnabled(true);
            topText.setText(timeFormatted);
            winTitle.setText("Solve #" + stats.getNumResults());
            winInfo.setText(timeFormatted + "\n" + numMoves + " moves\n");
        });

        scrambleButton.setOnClickListener(v -> {
            winLayout.setVisibility(View.GONE);
            board.scramble();
            scrambleButton.setEnabled(false);
            topText.setText("ready");
        });

        // temporary action : reset the board and never allow timed solve again (until going back)
        resetButton.setOnClickListener(v -> {
            board.solve();
            scrambleButton.setEnabled(true);
            stats.reset();
            topText.setText(numCellsX + "x" + numCellsY + " sandbox");
            winLayout.setVisibility(View.GONE);
        });

        backButton.setOnClickListener(v -> {
            finish();
        });

        // begin scrambled
        board.scramble();
        scrambleButton.setEnabled(false);
        topText.setText(numCellsX + "x" + numCellsY);
        timerHandler.postDelayed(timerRunnable, 0);
    }
}
