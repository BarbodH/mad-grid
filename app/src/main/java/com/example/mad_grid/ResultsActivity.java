package com.example.mad_grid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {
    // data variables
    private String stringMode;
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();

        // retrieve necessary information from GameActivity
        String scoreString = intent.getStringExtra("score");
        boolean isHighest = intent.getBooleanExtra("isHighest", false);
        this.stringMode = intent.getStringExtra("mode");

        // update results view for user
        ((TextView)findViewById(R.id.results_scoreValue)).setText(scoreString);
        ((TextView)findViewById(R.id.results_highestValue)).setText(loadHighestScore());
        // display congratulations message if new high score is achieved
        if (isHighest) {
            String congratsMessage = String.format("New High Score for %s mode.", this.stringMode);
            ((TextView)findViewById(R.id.results_highestPlaceholder)).setText(congratsMessage);
        } else {
            ((TextView)findViewById(R.id.results_highestPlaceholder)).setText("");
        }
    }

    /**
     * Helper method to load current game mode's highest score from SharedPreferences
     * Precondition(s): none
     * Postcondition(s): string representing highest score of current game mode is returned
     */
    public String loadHighestScore() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(this.stringMode, "0");
    }

    /**
     * Event listener to redirect user to settings page
     * Precondition(s): none
     * Postcondition(s): SettingsActivity is opened
     * @param view
     */
    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Event listener to redirects user to GameActivity of same mode
     * Precondition(s): none
     * Postcondition(s): GameActivity is opened; current game mode is passed on through intent object
     * @param view - user interface
     */
    public void openGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("mode", stringMode);
        startActivity(intent);
    }

    /**
     * Event listener to redirects user to MainActivity
     * Precondition(s): none
     * Postcondition(s): MainActivity is opened
     * @param view - user interface
     */
    public void openHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}