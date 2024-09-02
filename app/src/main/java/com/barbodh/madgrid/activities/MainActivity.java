package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.barbodh.madgrid.R;

public class MainActivity extends AppCompatActivity {

    ////////// Field(s) //////////

    private final String[] modeStrings = {"Classic", "Reverse", "Messy"};
    private int mode; // Stores index within range [0, 2] for "modeString" array

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mode = 0; // Default game mode 0: "Classic"
        updateModeDisplay();
    }

    ////////// Event Handler(s) //////////

    /**
     * Navigates to {@code GameActivity} and provides game mode.
     *
     * @param view the triggered UI element; "Start Game" button
     */
    public void openGame(View view) {
        var intent = new Intent(this, GameActivity.class);
        intent.putExtra("mode", modeStrings[this.mode]);
        startActivity(intent);
    }

    public void openMultiplayer(View view) {
        var intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("mode", this.mode);
        startActivity(intent);
    }

    /**
     * Navigates to {@code GuideActivity}.
     *
     * @param view the triggered UI element; "Guide" button
     */
    public void openGuide(View view) {
        var intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates to {@code SettingsActivity}.
     *
     * @param view the triggered UI element; "Settings" button
     */
    public void openSettings(View view) {
        var intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Increments game mode index.
     *
     * @param view the triggered UI element; right chevron icon
     */
    public void increaseDifficulty(View view) {
        if (this.mode != 2) {
            this.mode++;
        }
        updateModeDisplay();
    }

    /**
     * Decreases game mode index.
     *
     * @param view the triggered UI element; left chevron icon
     */
    public void decreaseDifficulty(View view) {
        if (this.mode != 0) {
            this.mode--;
        }
        updateModeDisplay();
    }

    ////////// Utility //////////

    /**
     * Updates game mode index displayed on homepage as follows:
     * 0 -> Classic,
     * 1 -> Reverse,
     * 2 -> Messy
     */
    private void updateModeDisplay() {
        ((TextView) findViewById(R.id.homepage_text_mode)).setText(modeStrings[this.mode]);
    }
}