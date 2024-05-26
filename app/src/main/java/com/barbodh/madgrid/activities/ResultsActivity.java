package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.barbodh.madgrid.R;

public class ResultsActivity extends AppCompatActivity {

    ////////// Field(s) //////////

    private String stringMode;
    public static final String SHARED_PREFS = "sharedPrefs";

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        var intent = getIntent();

        // Retrieve necessary information from "GameActivity"
        var scoreString = intent.getStringExtra("score");
        var isHighest = intent.getBooleanExtra("isHighest", false);
        this.stringMode = intent.getStringExtra("mode");

        // Update results view for user's "results_scoreValue"
        ((TextView) findViewById(R.id.results_text_placeholder_score_value)).setText(scoreString);
        ((TextView) findViewById(R.id.results_text_placeholder_highest_value)).setText(loadHighestScore());
        // Display congratulations message if new high score is achieved
        if (isHighest) {
            var congratsMessage = String.format("New High Score for %s mode.", this.stringMode);
            ((TextView) findViewById(R.id.results_text_placeholder_highest_score_message)).setText(congratsMessage);
        } else {
            ((TextView) findViewById(R.id.results_text_placeholder_highest_score_message)).setText("");
        }
    }

    ////////// Event Handler(s) //////////

    /**
     * Navigates to {@code SettingsActivity}.
     *
     * @param view Triggered UI element; 'Settings' button
     */
    public void openSettings(View view) {
        var intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates to {@code GameActivity} with the same game mode.
     *
     * @param view Triggered UI element; 'Restart' button
     */
    public void openGame(View view) {
        var intent = new Intent(this, GameActivity.class);
        intent.putExtra("mode", stringMode);
        startActivity(intent);
    }

    /**
     * Navigates {@code MainActivity}.
     *
     * @param view Triggered UI element; 'Home' button
     */
    public void openHome(View view) {
        var intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    ////////// Utility //////////

    /**
     * Loads the highest score of the current game mode from {@code SharedPreferences}.
     */
    public String loadHighestScore() {
        var sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(this.stringMode, "0");
    }
}