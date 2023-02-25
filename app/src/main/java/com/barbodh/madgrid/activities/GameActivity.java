package com.barbodh.madgrid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.barbodh.madgrid.MadGrid;
import com.barbodh.madgrid.R;
import com.barbodh.madgrid.tools.SoundPlayer;

public class GameActivity extends AppCompatActivity {
    // data variable(s)
    private MadGrid madGrid;
    private SoundPlayer soundPlayer;
    // data variables associated with settings
    public static final String SHARED_PREFS = "sharedPrefs"; // stores & loads information global in app
    private MediaPlayer mediaPlayer;
    private boolean music;
    private boolean sound;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // object initializations
        soundPlayer = new SoundPlayer(this);

        // retrieve game mode from MainActivity/ResultsActivity
        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        ((TextView)findViewById(R.id.game_text_mode)).setText(mode);

        // load highest score
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String stringHighestScore = sharedPreferences.getString(mode, "0");
        int highestScore = Integer.parseInt(stringHighestScore);

        // initialize MadGrid instance using game mode & highest score
        Button[] buttons = {
                (Button) findViewById(R.id.game_button_box_1),
                (Button) findViewById(R.id.game_button_box_2),
                (Button) findViewById(R.id.game_button_box_3),
                (Button) findViewById(R.id.game_button_box_4)
        };
        madGrid = new MadGrid(mode, highestScore, buttons);

        // start background music only if music is enabled
        loadSettings();
        mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.game_music);
        if (this.music) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        // adjust grid dimensions dynamically according to device dimensions
        adjustGridDimensions();

        // start game
        initializeNewTurn();
    }

    /**
     * Adjusts grid dimensions dynamically according to device dimensions
     * Precondition(s): none
     * Postcondition(s): Grid width & height are set to 95% of the device's width/height (whichever is smaller)
     */
    private void adjustGridDimensions() {
        // initialization
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // smaller dimension (width/height) should be used, in case of a tablet
        int madGrid_dimension = (int) ((Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels)) * 0.95);

        // set adjusted dimension size to gridLayout
        View view = findViewById(R.id.game_grid_layout);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = madGrid_dimension;
        layoutParams.height = madGrid_dimension;
        view.setLayoutParams(layoutParams);
    }

    /**
     * Aborts current game & returns home
     * Precondition(s): none
     * Postcondition(s): MainActivity is started
     * @param view - user interface
     */
    public void returnHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Aborts current game & starts new one
     * Precondition(s): none
     * Postcondition: new game is started with cleared score, turn, and key
     * @param view - user interface
     */
    public void resetGame(View view) {
        if (madGrid.getPlayingStatus()) {
            madGrid.clearKey();
            initializeNewTurn();
        }
    }

    /**
     * Initializes a new turn at the beginning or after the user's successful turn
     * Precondition(s): none
     * Postcondition(s): - increments the key and displays new sequence to user.
     *                   - playing status is changed to true to recognize user's response.
     */
    private void initializeNewTurn() {
        updateHighestScoreViews();
        updateScoreView();

        madGrid.incrementKey();
        int[] delay = madGrid.displaySequence(this);

        // deactivate reset button and activate after sequence display is finished
        // duration of sequence display is indicated by 'delay'
        View resetButtonView = findViewById(R.id.game_button_reset);
        handler.postDelayed(() -> resetButtonView.setBackgroundResource(R.drawable.game_control_button_inactive), delay[1]);
        handler.postDelayed(() -> resetButtonView.setBackgroundResource(R.drawable.game_control_button_active), delay[0]);
    }

    /**
     * Updates current score of the player
     * Precondition(s): none
     * Postcondition(s): 'score' is set to key length and its display is updated
     */
    private void updateScoreView() {
        String stringScore = Integer.toString(madGrid.getKey().size());
        ((TextView)findViewById(R.id.game_text_placeholder_score_value)).setText(stringScore);
        if (madGrid.isHighestScore()) {
            ((TextView)findViewById(R.id.game_text_placeholder_highest_value)).setText(stringScore);
        }
    }

    /**
     * When user is playing, determines correctness of their response and updates game status
     * Precondition(s): none
     * Postcondition(s): in case of correct response, method increments 'turnIndex' or initializes new turn; otherwise game ends
     * @param view - button clicked
     */
    public void handleBoxClick(View view) {
        // when user is not playing, this method has no functionality
        if (madGrid.getPlayingStatus()) {
            if (view.getId() == madGrid.getKey().get(madGrid.getTurnIndex()).getId()) {
                // play audio only if sound effects are enabled
                if (this.sound) this.soundPlayer.playClickSound();

                if (madGrid.getTurnIndex() < madGrid.getKey().size() - 1) {
                    madGrid.incrementTurnIndex();
                } else {
                    madGrid.resetTurnIndex();
                    initializeNewTurn();
                }
            } else {
                madGrid.getKey().remove(0);
                gameOver();
            }
        }
    }

    /**
     * Finishes game and redirects user to ResultsActivity, along with necessary information
     * Precondition(s): none
     * Postcondition(s): user is redirected to ResultsActivity; intent object contains achieved score,
     * mode, and whether or not they achieved new high score.
     */
    private void gameOver() {
        // play game over audio only if sound effects are enabled
        if (this.sound) {
            this.soundPlayer.playGameOverSound();
        }

        // save new highest score in SharedPreferences (if higher than previous highest score)
        saveNewHighestScore(); // saves new high score in SharedPreferences

        // send user to ResultsActivity and pass on necessary information
        Intent intent = new Intent(this, ResultsActivity.class);
        String scoreString = Integer.toString(madGrid.getKey().size());
        intent.putExtra("score", scoreString);
        intent.putExtra("isHighest", madGrid.isHighestScore());
        intent.putExtra("mode", madGrid.getMode());
        startActivity(intent);
    }

    /**
     * Pauses media player (for background music)
     * Precondition(s): none
     * Postcondition(s): media player (for background music) is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    /**
     * Helper method to update highest score view during game
     * Precondition(s): none
     * Postcondition(s): highest score view is updated to display newly achieved high score
     */
    private void updateHighestScoreViews() {
        String highestScoreString = String.valueOf(madGrid.getHighestScore());
        ((TextView)findViewById(R.id.game_text_placeholder_highest_value)).setText(highestScoreString);
    }

    /**
     * Helper method to load settings from SharedPreferences
     * Precondition(s): none
     * Postcondition(s): settings preferences are loaded
     */
    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        // second parameter corresponds to default values
        this.music = sharedPreferences.getBoolean("Music", false);
        this.sound = sharedPreferences.getBoolean("Sound", false);
        int speed = sharedPreferences.getInt("Speed", 1);
        madGrid.setSpeed(speed);
    }

    /**
     * Helper method to save new high score in SharedPreferences
     * Precondition(s): 'newHighest' score is a positive integer
     * Postcondition(s): 'newHighest' score value is shared to SharedPreferences
     */
    public void saveNewHighestScore() {
        if (madGrid.isHighestScore()) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String newHighestString = Integer.toString(madGrid.getKey().size());
            editor.putString(madGrid.getMode(), newHighestString); // key: mode, value: mode's new high score
            editor.apply();
        }
    }
}