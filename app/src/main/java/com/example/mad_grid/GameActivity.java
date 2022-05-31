package com.example.mad_grid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    // data variable(s)
    private String stringMode;
    boolean isPlaying = false; // indicates whether user is playing their turn
    ArrayList<Integer> key = new ArrayList<>(); // stores the correct sequence of box indexes
    int score = 0;
    int turnIndex = 0;
    private final Handler handler = new Handler(); // used for delaying executions
    private SoundPlayer soundPlayer;
    private Vibrator vibrator;
    public static final String SHARED_PREFS = "sharedPrefs"; // stores & loads information global in app
    private String highestScoreString;
    private MediaPlayer mediaPlayer;
    private boolean music;
    private boolean vibration;
    private boolean sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // object initializations
        soundPlayer = new SoundPlayer(this);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        // retrieve game mode from MainActivity/ResultsActivity
        Intent intent = getIntent();
        this.stringMode = intent.getStringExtra("mode");
        ((TextView)findViewById(R.id.game_mode)).setText(this.stringMode);

        // start background music only if music is enabled
        loadSettings();
        if (this.music) {
            mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.background_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        // start game
        initializeNewTurn();
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
            this.score = 0;
            this.turnIndex = 0;
            this.key.clear();
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
        loadHighestScore();
        updateHighestScoreViews();
        updateScore();
        incrementKey();
        displaySequence();
    }

    /**
     * Updates current score of the player
     * Precondition(s): none
     * Postcondition(s): 'score' is set to key length and its display is updated
     */
    private void updateScore() {
        this.score = this.key.size();
        String stringScore = Integer.toString(this.score);
        ((TextView)findViewById(R.id.game_score)).setText(stringScore);
        int highestScore = Integer.parseInt(this.highestScoreString);
        if (this.score > highestScore) {
            ((TextView)findViewById(R.id.game_highest)).setText(stringScore);
        }
    }

    /**
     * Adds new integers to the key depending on game mode
     * Precondition(s): 'stringMode' is already initialized as 'Classic', 'Difficult', or 'Expert'
     * Postcondition(s): integer within [1, 4] is added to key.
     */
    private void incrementKey() {
        // initialization
        Random rand = new Random();
        int increment = stringMode.equals("Classic") ? 1 : stringMode.equals("Difficult") ? 2 : 3;
        // processing & calculation
        for (int i = 0; i < increment; i++) {
            key.add(rand.nextInt(4) + 1);
        }
        if (this.vibration) {
            vibrator.vibrate(100);
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
        int delayIncrement = stringMode.equals("Expert") ? 300 : 750;

        // start display of sequence
        for (int k : this.key) {
            // handler object prevents simultaneous grid animations
            handler.postDelayed(() -> toBounce(k), delay);
            delay += delayIncrement;
        }

        // handler object delays change of value of 'isPlaying' until whole sequence is displayed
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GameActivity.this.isPlaying = true;
                Toast.makeText(GameActivity.this, "Start your turn!", Toast.LENGTH_SHORT).show();
            }
        }, delay);
    }

    /**
     * Helper method to handle individual box animations
     * Precondition(s): 'buttonIndex' is within [1, 4] range
     * Postcondition(s): target button has bounced
     * @param buttonIndex - index of target button
     */
    private void toBounce(int buttonIndex) {
        // initialization
        int buttonID = determineButtonID(buttonIndex);
        Button button = (Button)findViewById(buttonID);
        Animation animation;
        BounceInterpolator bounceInterpolator = new BounceInterpolator(0.2, 20);

        // animation speed is higher in 'Expert' mode
        if (this.stringMode.equals("Expert")) {
            animation = AnimationUtils.loadAnimation(this, R.anim.bounce_fast);
        } else {
            animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        }
        animation.setInterpolator(bounceInterpolator);
        button.startAnimation(animation);
    }

    /**
     * When user is playing, determines correctness of their response and updates game status
     * Precondition(s): none
     * Postcondition(s): increments score if provided response is correct. Otherwise ends game.
     * In case of correct response, method also updates 'turnIndex'
     * @param view - button clicked
     */
    public void handleBoxClick(View view) {
        // when user is not playing, i.e., 'isPlaying is false, this method has no functionality
        if (this.isPlaying) {
            if (this.sound) { // plays audio only if sound effects are enabled
                soundPlayer.playClickSound();
            }
            int boxIndex = determineButtonIndex(view);
            if (boxIndex == key.get(turnIndex)) {
                if (turnIndex < key.size() - 1) {
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
        if (view.getId() == R.id.game_box1) {
            return 1;
        } else if (view.getId() == R.id.game_box2) {
            return 2;
        } else if (view.getId() == R.id.game_box3) {
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
                return R.id.game_box1;
            case 2:
                return R.id.game_box2;
            case 3:
                return R.id.game_box3;
            default:
                return R.id.game_box4;
        }
    }

    /**
     * Finishes game and redirects user to ResultsActivity, along with necessary information
     * Precondition(s): none
     * Postcondition(s): user is redirected to ResultsActivity; intent object contains achieved score,
     * mode, and whether or not they achieved new high score.
     */
    private void gameOver() {
        // initialization
        boolean isHighest = false;

        // play game over audio only if sound effects are enabled
        if (this.sound) {
            soundPlayer.playGameOverSound();
        }

        // determine if user has achieved new high score
        int highestScore = Integer.parseInt(this.highestScoreString);
        if (this.score > highestScore) {
            saveNewHighestScore(this.score); // saves new high score in SharedPreferences
            isHighest = true;
        }

        // send user to ResultsActivity and pass on necessary information
        Intent intent = new Intent(this, ResultsActivity.class);
        String scoreString = Integer.toString(this.score);
        intent.putExtra("score", scoreString);
        intent.putExtra("isHighest", isHighest);
        intent.putExtra("mode", this.stringMode);
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
     * Helper method to save new high score in SharedPreferences
     * Precondition(s): 'newHighest' score is a positive integer
     * Postcondition(s): 'newHighest' score value is shared to SharedPreferences
     * @param newHighest - achieved high score
     */
    private void saveNewHighestScore(int newHighest) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String newHighestString = Integer.toString(newHighest);
        editor.putString(this.stringMode, newHighestString); // key: mode, value: mode's new high score
        editor.apply();
    }

    /**
     * Helper method to load current game mode's highest score from SharedPreferences
     * Precondition(s): none
     * Postcondition(s): highest score of current game mode is loaded
     */
    private void loadHighestScore() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        this.highestScoreString = sharedPreferences.getString(this.stringMode, "0"); // default value: 0
    }

    /**
     * Helper method to update highest score view during game
     * Precondition(s): none
     * Postcondition(s): highest score view is updated to display newly achieved high score
     */
    private void updateHighestScoreViews() {
        ((TextView)findViewById(R.id.game_highest)).setText(this.highestScoreString);
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
        this.vibration = sharedPreferences.getBoolean("Vibration", false);
        this.sound = sharedPreferences.getBoolean("Sound", false);
    }
}