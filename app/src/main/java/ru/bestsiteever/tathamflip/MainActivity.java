package ru.bestsiteever.tathamflip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    Button playBtn;
    Spinner boardSizeSpinner;
    Spinner ruleTypeSpinner;
    private final String[] boardSizesArray = {"3x3", "3x4", "4x4", "4x5", "5x5"};
    private final int defaultBoardSizeIndex = 2; // 4x4
    // private final String[] ruleTypesArray = {"plus", "cross"};
    private final String[] ruleTypesArray = {"plus"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view init
        playBtn = (Button)findViewById(R.id.play_button);
        boardSizeSpinner = (Spinner)findViewById(R.id.spinner_board_size);
        ruleTypeSpinner = (Spinner)findViewById(R.id.spinner_rule_type);

        // board sizes
        ArrayAdapter<String> boardSizeAdapter = new ArrayAdapter<String> (this, R.layout.spinner_item, boardSizesArray);
        boardSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardSizeSpinner.setAdapter(boardSizeAdapter);
        boardSizeSpinner.setSelection(defaultBoardSizeIndex);

        // rule types
        ArrayAdapter<String> rulesAdapter = new ArrayAdapter<String> (this, R.layout.spinner_item, ruleTypesArray);
        rulesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ruleTypeSpinner.setAdapter(rulesAdapter);
        ruleTypeSpinner.setSelection(0);

        playBtn.setOnClickListener(this::playBtnClick);
    }

    public void playBtnClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        int numCellsX, numCellsY;
        String[] boardSizeArray = boardSizeSpinner.getSelectedItem().toString().split("x");
        try {
            numCellsX = Integer.parseInt(boardSizeArray[0]);
            numCellsY = Integer.parseInt(boardSizeArray[1]);
        } catch(Exception e) {
            numCellsX = FlipLogic.DEFAULT_WIDTH_HEIGHT;
            numCellsY = FlipLogic.DEFAULT_WIDTH_HEIGHT;
            Log.e("fail", "failed to parse?", e);
        }

        intent.putExtra("numCellsX", numCellsX);
        intent.putExtra("numCellsY", numCellsY);
        startActivity(intent);
    }
}
