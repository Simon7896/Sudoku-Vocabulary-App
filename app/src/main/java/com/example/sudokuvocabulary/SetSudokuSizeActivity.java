package com.example.sudokuvocabulary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class SetSudokuSizeActivity extends AppCompatActivity implements View.OnClickListener{

    DBAdapter db;
    SwitchCompat mDarkSwitch;
    private PrefManager mPrefManager;

    private String themeSwitchKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SetSudokuSizeActivity", "onCreate Called!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sudoku_size);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupTutorialButton();

        mPrefManager = new PrefManager(this);

        // Key containing dark mode switch boolean value
        themeSwitchKey = getString(R.string.theme_value_key);

        //check for dark or light mode
        boolean themeSwitchState = mPrefManager.loadSavedPreferences(this, themeSwitchKey);

        // Restore the switch value to the previous setting
        mDarkSwitch = findViewById(R.id.darkSwitch);
        mDarkSwitch.setChecked(themeSwitchState);

        mDarkSwitch.setOnCheckedChangeListener((compoundButton, themeSwitchState1) -> {
            if (compoundButton.isPressed()) {
                mPrefManager.savePreferences(themeSwitchKey, themeSwitchState1);
                recreate();
            }
        });

        db = new DBAdapter(this);
        db.open();

        Button btn_4x4 = findViewById(R.id.btn_4x4);
        btn_4x4.setOnClickListener(this);

        Button btn_6x6 = findViewById(R.id.btn_6x6);
        btn_6x6.setOnClickListener(this);

        Button btn_9x9 = findViewById(R.id.btn_9x9);
        btn_9x9.setOnClickListener(this);

        Button btn_12x12 = findViewById(R.id.btn_12x12);
        btn_12x12.setOnClickListener(this);

    }

    private void setupTutorialButton() {
        ImageView tutorialBtn = findViewById(R.id.tutorialBtn);
        tutorialBtn.setOnClickListener(view -> {

            Intent intent = new Intent(SetSudokuSizeActivity.this, TutorialActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View v) {
        Cursor cursor  = db.getAllRows("animals");
                WordDictionary dictionary = new WordDictionary();
                while(!cursor.isAfterLast()) {
                    String word = cursor.getString(
                            cursor.getColumnIndexOrThrow("word"));
                    String translation = cursor.getString(
                            cursor.getColumnIndexOrThrow("translation"));
                    dictionary.add(word, translation);
                    cursor.moveToNext();
                }
                cursor.close();
                Intent intent = WordListsActivity.newIntent(
                        SetSudokuSizeActivity.this, dictionary);
                startActivity(intent);
    }

}