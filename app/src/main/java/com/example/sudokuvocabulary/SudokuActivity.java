package com.example.sudokuvocabulary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SudokuActivity extends AppCompatActivity implements View.OnClickListener {
    private QuestionCardView mQuestionCard;
    private SudokuView mSudokuView;
    private SudokuModel mSudokuModel;
    private String[] mWords, mTranslations;
    private int mCellRow=0, mCellColumn=0, mCellValue=0;
    private String mWordPrompt;
    private String mChoicePicked;
    private static final String KEY_GRID_AS_ARRAY = "gridAsArray";
    private static final String KEY_SOLUTION_AS_ARRAY = "solutionArray";
    private static final String KEY_NUM_OF_EMPTY_CELLS = "numOfCellsFilled";
    private static final String KEY_POPUP_VISIBLE = "popupVisible";

   // boolean timerStarted = false;
    private long startTime; //timer
   // private long endTime; //timer

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupTutorialButton();

        mWords = getIntent().getStringArrayExtra(getString(R.string.words_key));
        mTranslations = getIntent().getStringArrayExtra(getString(R.string.translations_key));

        mSudokuModel = new SudokuModel();
        mSudokuView = findViewById(R.id.sudokuGridView);

        // Set the words to draw on the grid and the dimensions of the grid
        mSudokuView.setInitialGrid(mSudokuModel.getGridAsMatrix(), mWords);
        // SetSudokuSize needs to pass different values to this via intent
        mSudokuView.setSubGridDimensions(3, 3);

        mQuestionCard = findViewById(R.id.questionCardView);
        mQuestionCard.hide();

        mSudokuView.setOnTouchListener((view, motionEvent) -> {
            boolean isValid = false;
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN
                    && !mQuestionCard.isVisible()) {

                mCellRow = (int) (Math.ceil(motionEvent.getY() / mSudokuView.getCellHeight())) - 1;
                mCellColumn = (int) ((Math.ceil(motionEvent.getX()) / mSudokuView.getCellWidth()));
                mCellValue = mSudokuModel.getSolutionAt(mCellRow, mCellColumn);

                if (mSudokuModel.cellNotEmpty(mCellRow, mCellColumn)) {
                    Toast.makeText(this, mWords[mCellValue-1], Toast.LENGTH_SHORT).show();
                    return true;
                }

                isValid = true;

                mWordPrompt = mWords[mSudokuModel.getSolutionAt(mCellRow, mCellColumn)-1];
                mQuestionCard.setCard(mWordPrompt, mTranslations);
                setButtonListeners(mQuestionCard.getWordChoiceButtons());
                mQuestionCard.show();
            }
            return isValid;
        });
    }

    @Override
    public void onClick(View view) {
        mChoicePicked = (String) ((Button) view).getText();
        String toastMessage;

        if (isCorrect()) {
            mSudokuModel.checkAndFillCellAt(mCellRow, mCellColumn, mCellValue);
            mSudokuView.setWordToDrawAt(mCellRow, mCellColumn, mTranslations[mCellValue-1]);
            mSudokuView.invalidate();
            toastMessage = getString(R.string.game_correct_toast_text);
        } else {
            toastMessage = getString(R.string.game_incorrect_toast_text);
        }
        mQuestionCard.setVisibility(View.GONE);
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        if (mSudokuModel.isGridFilled()) {
            Intent intent = newIntent(
                    SudokuActivity.this, mWords, mTranslations);
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putIntArray(KEY_GRID_AS_ARRAY, mSudokuModel.getGridAsArray());
        savedInstanceState.putIntArray(KEY_SOLUTION_AS_ARRAY, mSudokuModel.getSolutionAsArray());
        savedInstanceState.putInt(KEY_NUM_OF_EMPTY_CELLS, mSudokuModel.getNumberOfEmptyCells());
        savedInstanceState.putInt(getString(R.string.cell_row_key), mCellRow);
        savedInstanceState.putInt(getString(R.string.cell_column_key), mCellColumn);
        savedInstanceState.putInt(getString(R.string.cell_value_key), mCellValue);
        savedInstanceState.putBoolean(KEY_POPUP_VISIBLE, (mQuestionCard.getVisibility() == View.VISIBLE));
        savedInstanceState.putStringArray(getString(R.string.words_key), mWords);
        savedInstanceState.putStringArray(getString(R.string.translations_key), mTranslations);
        savedInstanceState.putString(getString(R.string.word_prompt_key), mWordPrompt);
        savedInstanceState.putStringArray(getString(R.string.word_grid_key),
                SudokuModel.flatten(mSudokuView.getWordsToDraw()));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSudokuModel = new SudokuModel();
        mSudokuModel.setGridFromArray(savedInstanceState.getIntArray(KEY_GRID_AS_ARRAY));
        mSudokuModel.setSolutionFromArray(savedInstanceState.getIntArray(KEY_SOLUTION_AS_ARRAY));
        mSudokuModel.setNumberOfEmptyCells(savedInstanceState.getInt(KEY_NUM_OF_EMPTY_CELLS));

        mWords = savedInstanceState.getStringArray(getString(R.string.words_key));
        mTranslations = savedInstanceState.getStringArray(getString(R.string.translations_key));
        mWordPrompt = savedInstanceState.getString(getString(R.string.word_prompt_key));

        mSudokuView = findViewById(R.id.sudokuGridView);
        String[][] wordsToDraw = SudokuModel.expand(
                savedInstanceState.getStringArray(getString(R.string.word_grid_key)));
        mSudokuView.setWordsToDraw(wordsToDraw);

        mCellRow = savedInstanceState.getInt(getString(R.string.cell_row_key));
        mCellColumn = savedInstanceState.getInt(getString(R.string.cell_column_key));
        mCellValue = savedInstanceState.getInt(getString(R.string.cell_value_key));

        mQuestionCard = findViewById(R.id.questionCardView);
        mQuestionCard.setCard(mWordPrompt, mTranslations);
        setButtonListeners(mQuestionCard.getWordChoiceButtons());
        mQuestionCard.setVisibility(savedInstanceState.getBoolean(KEY_POPUP_VISIBLE));
    }

    @NonNull
    public Intent newIntent(Context packageContext, String[] words, String[] translations) {
        startTime = System.currentTimeMillis();
        Intent intent2 = new Intent(this, GameCompleteActivity.class);
        intent2.putExtra("startTime",startTime);
        startActivity(intent2);

        Intent intent = new Intent(packageContext, GameCompleteActivity.class);
        intent.putExtra(getString(R.string.words_key), words);
        intent.putExtra(getString(R.string.translations_key), translations);
        return intent;
    }

    private boolean isCorrect() {
        return mChoicePicked.equals(
                mTranslations[mSudokuModel.getSolutionAt(mCellRow, mCellColumn)-1]);
    }

    private void setupTutorialButton() {
        ImageView tutorialBtn = findViewById(R.id.tutorialBtn);
        tutorialBtn.setOnClickListener(view -> {

            Intent intent = new Intent(SudokuActivity.this, TutorialActivity.class);
            startActivity(intent);
        });
    }

    private void setButtonListeners(Button[] buttons) {
        for (Button button: buttons) {
            button.setOnClickListener(this);
        }
    }

}