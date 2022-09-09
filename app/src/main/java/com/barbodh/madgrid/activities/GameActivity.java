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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.barbodh.madgrid.tools.BounceInterpolator;
import com.barbodh.madgrid.MadGrid;
import com.barbodh.madgrid.R;
import com.barbodh.madgrid.tools.SoundPlayer;

public class GameActivity extends AppCompatActivity {
    // data variable(s)
    private MadGrid madGrid;
    private boolean isPlaying = false; // indicates whether user is playing their turn
    private int turnIndex;
    private final Handler handler = new Handler(); // used for delaying executions
    private SoundPlayer soundPlayer;
    // data variables associated with settings
    public static final String SHARED_PREFS = "sharedPrefs"; // stores & loads information global in app
    private MediaPlayer mediaPlayer;
    private boolean music;
    private boolean sound;

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
        madGrid = new MadGrid(mode, highestScore);

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
        if(isPlaying) {
            madGrid.resetScore();
            madGrid.clearKey();
            initializeNewTurn();
        }
    }

    /**
     * Initializes a new turn at the beginning or after the user's successful turn
     * Precondition(s): none
     * Postcondition(s): Increments the key and displays new sequence to user.
     * 'isPlaying' boolean is changed to true to recognize user's response.
     */
    private void initializeNewTurn() {
        updateHighestScoreViews();
        updateScoreView();
        madGrid.incrementKey();
        displaySequence();
    }

    /**
     * Updates current score of the player
     * Precondition(s): none
     * Postcondition(s): 'score' is set to key length and its display is updated
     */
    private void updateScoreView() {
        madGrid.incrementScore();
        String stringScore = Integer.toString(madGrid.getScore());
        ((TextView)findViewById(R.id.game_text_placeholder_score_value)).setText(stringScore);
        if (madGrid.isHighestScore()) {
            ((TextView)findViewById(R.id.game_text_placeholder_highest_value)).setText(stringScore);
        }
    }

    /**
     * Displays newly incremented sequence to user before their turn
     * Precondition(s): none
     * Postcondition(s): 'key' values are displayed using sequential grid animations
     */
    private void displaySequence() {
        // initialization
        this.isPlaying = false;
        int delay = 750;
        final int delayIncrement = 750;
        // turn off button feedback to user
        handler.postDelayed(this::deactivateButtons, 450);

        // start display of sequence
        if (madGrid.getMode().equals("Reverse")) { // iterate backwards through key for 'Reverse' mode
            for (int index = madGrid.getKey().size() - 1; index >= 0; index--) {
                int item = madGrid.getKey().get(index);
                // handler object prevents simultaneous grid animations
                handler.postDelayed(() -> toBounce(item), delay);
                delay += delayIncrement;
            }
        }
        else { // iterate regularly through key for 'Classic' and 'Messy' modes
            for (int k : madGrid.getKey()) {
                // handler object prevents simultaneous grid animations
                handler.postDelayed(() -> toBounce(k), delay);
                delay += delayIncrement;
            }
        }

        // handler object delays change of value of 'isPlaying' until whole sequence is displayed
        handler.postDelayed(() -> {
            GameActivity.this.isPlaying = true;
            activateButtons(); // turn on button feedback to user
        }, delay);
    }

    /**
     * Helper method to activate button feedback to user when game is ongoing ('isPlaying' = true)
     * Precondition(s): none
     * Postcondition(s): buttons' background becomes lighter with ripple effect
     */
    private void activateButtons() {
        for (int index = 1; index <= 4; index++) {
            int id = determineButtonID(index);
            View view = findViewById(id);
            view.setBackgroundResource(R.drawable.grid_button_background);
        }
    }

    /**
     * Helper method to deactivate button feedback to user during sequence display ('isPlaying' = false)
     * Precondition(s): none
     * Postcondition(s): buttons' background becomes darker without ripple effect
     */
    private void deactivateButtons() {
        for (int index = 1; index <= 4; index++) {
            int id = determineButtonID(index);
            View view = findViewById(id);
            view.setBackgroundResource(R.drawable.grid_button_background_inactive);
        }
    }

    /**
     * Helper method to handle individual box animations
     * Precondition(s): 'buttonIndex' is within [1, 4] range
     * Postcondition(s): target button has bounced
     * @param buttonIndex - index of target button
     */
    private void toBounce(int buttonIndex) {
        int buttonID = determineButtonID(buttonIndex);
        Button button = (Button) findViewById(buttonID);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator bounceInterpolator = new BounceInterpolator(0.2, 20);
        animation.setInterpolator(bounceInterpolator);
        button.startAnimation(animation);
    }

    /**
     * When user is playing, determines correctness of their response and updates game status
     * Precondition(s): none
     * Postcondition(s): in case of correct response, method increments 'turnIndex' or initializes new turn; otherwise game ends
     * @param view - button clicked
     */
    public void handleBoxClick(View view) {
        // when user is not playing, i.e., 'isPlaying is false, this method has no functionality
        if (this.isPlaying) {
            if (this.sound) { // plays audio only if sound effects are enabled
                soundPlayer.playClickSound();
            }
            int boxIndex = determineButtonIndex(view);
            if (boxIndex == madGrid.getKey().get(turnIndex)) {
                if (turnIndex < madGrid.getKey().size() - 1) {
                    turnIndex++;
                } else {
                    turnIndex = 0;
                    initializeNewTurn();
                }
            } else {
                gameOver();
            }
        }
    }

    /**
     * Helper method to determines which box in the grid was clicked
     * Precondition(s): view is any of the 4 game_box buttons
     * Postcondition(s): returns integer box index
     * @param view - button clicked
     * @return box index
     */
    private int determineButtonIndex(View view) {
        if (view.getId() == R.id.game_button_box_1) {
            return 1;
        } else if (view.getId() == R.id.game_button_box_2) {
            return 2;
        } else if (view.getId() == R.id.game_button_box_3) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * Helper method to determine the ID of the clicked box
     * Precondition(s): index is an integer within interval [1, 4]
     * Postcondition(s): returns string ID of the box corresponding to input index
     * @param index - index of inputted box
     * @return box ID string corresponding to 'index'
     */
    private int determineButtonID(int index) {
        switch (index) {
            case 1:
                return R.id.game_button_box_1;
            case 2:
                return R.id.game_button_box_2;
            case 3:
                return R.id.game_button_box_3;
            default:
                return R.id.game_button_box_4;
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
            soundPlayer.playGameOverSound();
        }

        // save new highest score in SharedPreferences (if higher than previous highest score)
        saveNewHighestScore(); // saves new high score in SharedPreferences

        // send user to ResultsActivity and pass on necessary information
        Intent intent = new Intent(this, ResultsActivity.class);
        String scoreString = Integer.toString(madGrid.getScore());
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
        mediaPlayer.pause();
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
        // variables are loaded with default value of 'false'
        this.music = sharedPreferences.getBoolean("Music", false);
        this.sound = sharedPreferences.getBoolean("Sound", false);
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
            String newHighestString = Integer.toString(madGrid.getScore());
            editor.putString(madGrid.getMode(), newHighestString); // key: mode, value: mode's new high score
            editor.apply();
        }
    }
}