package com.example.mad_grid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    // data variables
    private final String[] modeStrings = {"Classic", "Reverse", "Crazy"};
    private int mode; // stores index [0, 2] for 'modeString' array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mode = 0; // default game mode 0: 'Classic'
        updateModeDisplay();
    }

    /**
     * Opens the game page and passes on mode
     * Precondition(s): none
     * Postcondition(s): GameActivity is opened and 'mode' is passed in 'intent'
     * @param view - user interface
     */
    public void openGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("mode", modeStrings[this.mode]);
        startActivity(intent);
    }

    /**
     * opens guide page
     * Precondition(s): none
     * Postcondition(s): GuideActivity is started
     * @param view - user interface
     */
    public void openGuide(View view) {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
    }

    /**
     * opens settings page
     * Precondition(s): none
     * Postcondition(s): SettingsActivity is started
     * @param view - user interface
     */
    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Helper method - updates game mode displayed on homepage as follows:
     *  0: Classic
     *  1: Reverse
     *  2: Crazy
     * Precondition(s): 'mode' is an integer between 0 and 2
     * Postcondition(s): game mode display is updated
     */
    private void updateModeDisplay() {
        ((TextView)findViewById(R.id.homepage_text_mode)).setText(modeStrings[this.mode]);
    }

    /**
     * Increases game difficulty when right chevron icon is clicked
     * Precondition(s): 'mode' is an integer between 0 and 2
     * Postcondition(s): 'mode' (difficulty) is incremented
     * @param view - user interface
     */
    public void increaseDifficulty(View view) {
        if (this.mode != 2) {
            this.mode++;
        }
        updateModeDisplay();
    }

    /**
     * Decreases game difficulty when left chevron icon is clicked
     * Precondition(s): 'mode' is an integer between 0 and 2
     * Postcondition(s): 'mode' (difficulty) is decremented
     * @param view - user interface
     */
    public void decreaseDifficulty(View view) {
        if (this.mode != 0) {
            this.mode--;
        }
        updateModeDisplay();
    }

}