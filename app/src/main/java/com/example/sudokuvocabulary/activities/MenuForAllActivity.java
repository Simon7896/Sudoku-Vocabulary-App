package com.example.sudokuvocabulary.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.sudokuvocabulary.R;

public class MenuForAllActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_toolbar, menu);
        //disable these icons (bc not actual buttons)
        menu.findItem(R.id.sunImg).setEnabled(false);
        menu.findItem(R.id.moonImg).setEnabled(false);
        //menu.findItem(R.id.TimerText).setEnabled(false);

        //handling dark switch action
        MenuItem itemSwitch = menu.findItem(R.id.action_darkSwitch);
        itemSwitch.setActionView(R.layout.switch_item); //switch item layout;
        SwitchCompat darkSwitch = menu.findItem(R.id.action_darkSwitch).getActionView().findViewById(R.id.switchTemplate);

        // set the default dark switch value to system theme
        boolean currentTheme = (getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        darkSwitch.setChecked(currentTheme);

        //listener
        darkSwitch.setOnCheckedChangeListener((compoundButton, isNightModeOn) -> {
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_tutorialBtn:
                Intent intent = new Intent(this, TutorialActivity.class);
                startActivity(intent);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
