package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.barbodh.madgrid.MadGrid;
import com.barbodh.madgrid.MadGridApplication;
import com.barbodh.madgrid.R;
import com.barbodh.madgrid.model.GameUpdate;
import com.barbodh.madgrid.model.MultiplayerGame;
import com.barbodh.madgrid.tools.SoundPlayer;
import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.StompClient;

public class GameActivity extends AppCompatActivity {

    ////////// Field(s) //////////

    // Game
    private MadGrid madGrid;
    private SoundPlayer soundPlayer;

    // Settings
    public static final String SHARED_PREFS = "sharedPrefs";
    private MediaPlayer mediaPlayer;
    private boolean music;
    private boolean sound;
    private final Handler handler = new Handler();

    private final Gson gson = new Gson();
    private StompClient stompClient;
    private int type;
    private MultiplayerGame multiplayerGame;
    private String playerId;
    private Disposable disposableTopic;

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Object initializations
        soundPlayer = new SoundPlayer(this);

        // Retrieve game mode from MainActivity/ResultsActivity
        var intent = getIntent();
        var mode = intent.getStringExtra("mode");
        ((TextView) findViewById(R.id.game_text_mode)).setText(mode);

        // Load highest score
        var sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        var stringHighestScore = sharedPreferences.getString(mode, "0");
        var highestScore = Integer.parseInt(stringHighestScore);

        // Initialize MadGrid instance using game mode & highest score
        var buttons = new Button[]{
                findViewById(R.id.game_button_box_1),
                findViewById(R.id.game_button_box_2),
                findViewById(R.id.game_button_box_3),
                findViewById(R.id.game_button_box_4),
                findViewById(R.id.game_button_box_5),
                findViewById(R.id.game_button_box_6),
                findViewById(R.id.game_button_box_7),
                findViewById(R.id.game_button_box_8),
                findViewById(R.id.game_button_box_9)
        };
        madGrid = new MadGrid(mode, highestScore, buttons);

        // Start background music only if music is enabled
        loadSettings();
        mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.game_music);

        // Adjust grid dimensions dynamically according to device dimensions
        adjustGridDimensions();

        // Handle multiplayer game initialization
        type = intent.getIntExtra("type", 0);
        if (type == 1) {
            stompClient = MadGridApplication.getInstance().getStompClient();
            multiplayerGame = intent.getParcelableExtra("multiplayer_game");
            playerId = intent.getStringExtra("player_id");

            // Make opponent text and score visible
            var opponentValuePlaceholder = (TextView) findViewById(R.id.game_text_placeholder_opponent_value);
            opponentValuePlaceholder.setText("0");
            opponentValuePlaceholder.setVisibility(View.VISIBLE);
            findViewById(R.id.game_text_opponent).setVisibility(View.VISIBLE);

            // Subscribe to topic for receiving and handling game updates
            disposableTopic = stompClient.topic("/player/" + playerId + "/game/notify").subscribe(topicMessage -> {
                var multiplayerGame = gson.fromJson(topicMessage.getPayload(), MultiplayerGame.class);
                var player1 = multiplayerGame.getPlayer1();
                var player2 = multiplayerGame.getPlayer2();
                if (!player1.getId().equals(playerId) && !player2.getId().equals(playerId)) {
                    throw new RuntimeException("Player ID does not match the ID of players for the provided game.");
                }
                var player = player1.getId().equals(playerId) ? player1 : player2;
                var opponent = player1.getId().equals(playerId) ? player2 : player1;

                if (multiplayerGame.isActive()) {
                    runOnUiThread(() -> ((TextView) findViewById(R.id.game_text_placeholder_opponent_value)).setText(String.valueOf(opponent.getScore())));
                } else {
                    if (player1.getScore() == player2.getScore()) {
                        throw new RuntimeException("Players cannot have ended with the same score.");
                    }
                    var winner = player1.getScore() > player2.getScore() ? player1 : player2;
                    if (player == winner) {
                        Log.d("GameActivity", "User " + playerId + " has won the game.");
                    } else {
                        Log.d("GameActivity", "User " + playerId + " has lost the game.");
                    }
                    gameOver(player == winner ? 1 : 0, player.getScore(), opponent.getScore());
                }
            });
        } else if (type != 0) {
            throw new RuntimeException("Invalid game type.");
        }

        // Start game
        initializeNewTurn();
    }

    ////////// Event Listener(s) //////////

    /**
     * Aborts the current game and navigates to {@code MainActivity}.
     *
     * @param view the triggered UI element; "Home" button
     */
    public void returnHome(View view) {
        var intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Aborts the current game and starts a new one.
     *
     * @param view the triggered UI element; "Reset" button
     */
    public void resetGame(View view) {
        if (madGrid.isPlaying()) {
            madGrid.clearKey();
            initializeNewTurn();
        }
    }

    /**
     * When user is playing, determines correctness of their response and updates game status.
     *
     * @param view the triggered UI element; button clicked during the game
     */
    public void handleBoxClick(View view) {
        if (madGrid.isPlaying()) {
            if (view.getId() == madGrid.getKey().get(madGrid.getTurnIndex()).getId()) {
                if (this.sound) {
                    this.soundPlayer.playClickSound();
                }

                if (madGrid.getTurnIndex() < madGrid.getKey().size() - 1) {
                    madGrid.incrementTurnIndex();
                } else {
                    madGrid.resetTurnIndex();
                    initializeNewTurn();

                    if (type == 1) {
                        var gameUpdate = new GameUpdate(multiplayerGame.getId(), playerId, true);
                        stompClient.send("/game/update", gson.toJson(gameUpdate)).subscribe();
                    }
                }
            } else {
                madGrid.getKey().remove(0);

                if (type == 1) {
                    madGrid.deactivateButtons();
                    var gameUpdate = new GameUpdate(multiplayerGame.getId(), playerId, false);
                    stompClient.send("/game/update", gson.toJson(gameUpdate)).subscribe();
                    // Game update topic is responsible for invoking `gameOver` in multiplayer game
                } else {
                    gameOver();
                }
            }
        }
    }

    ////////// Utility //////////

    /**
     * Adjusts grid dimensions dynamically according to device dimensions.
     */
    private void adjustGridDimensions() {
        var displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Smaller dimension (width/height) should be used, in case of a tablet
        var madGrid_dimension = (int) ((Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels)) * 0.95);

        // Set adjusted dimension size to gridLayout
        var view = findViewById(R.id.game_grid_layout);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = madGrid_dimension;
        layoutParams.height = madGrid_dimension;
        view.setLayoutParams(layoutParams);
    }

    /**
     * Initializes a new turn at the beginning or after the user's successful turn.
     */
    private void initializeNewTurn() {
        updateHighestScoreViews();
        updateScoreView();

        madGrid.incrementKey();
        var delay = madGrid.displaySequence(this);

        // Deactivate reset button and activate after sequence display is finished
        // Duration of sequence display is indicated by the delay value
        var resetButtonView = findViewById(R.id.game_button_reset);
        handler.postDelayed(() -> resetButtonView.setBackgroundResource(R.drawable.game_control_button_inactive), delay[1]);
        handler.postDelayed(() -> resetButtonView.setBackgroundResource(R.drawable.game_control_button_active), delay[0]);
    }

    /**
     * Updates the current score of the player.
     */
    private void updateScoreView() {
        var stringScore = Integer.toString(madGrid.getKey().size());
        ((TextView) findViewById(R.id.game_text_placeholder_score_value)).setText(stringScore);
        if (madGrid.isHighestScore()) {
            ((TextView) findViewById(R.id.game_text_placeholder_highest_value)).setText(stringScore);
        }
    }

    /**
     * Finishes game and redirects user to {@code ResultsActivity}, along with necessary information.
     */
    private void gameOver() {
        gameOver(0, -1, -1);
    }

    private void gameOver(int result, int playerScore, int opponentScore) {
        if (this.sound) {
            this.soundPlayer.playGameOverSound();
        }

        // Save new highest score in SharedPreferences (if higher than previous highest score)
        saveNewHighestScore();

        // Send user to ResultsActivity and pass on the necessary information
        var intent = new Intent(this, ResultsActivity.class);

        intent.putExtra("score", madGrid.getKey().size());
        intent.putExtra("isHighest", madGrid.isHighestScore());
        intent.putExtra("mode", madGrid.getMode());
        intent.putExtra("type", type);

        if (type == 1) {
            intent.putExtra("result", result);
            // Override score because if user's last response was correct, provided score will be 1 higher than actual
            intent.putExtra("score", playerScore);
            intent.putExtra("opponent_score", opponentScore);
        }

        startActivity(intent);
    }

    /**
     * Pauses media player for the background music.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    /**
     * Starts media player the for background music when activity is closed or stopped (app overview
     * button is clicked).
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (this.music) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    /**
     * Called when the activity is no longer visible to the user. Disposes of the `disposableTopic`
     * to release resources and prevent memory leaks.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (disposableTopic != null && !disposableTopic.isDisposed()) {
            disposableTopic.dispose();
        }
    }

    /**
     * Updates the highest score view during the game.
     */
    private void updateHighestScoreViews() {
        var highestScoreString = String.valueOf(madGrid.getHighestScore());
        ((TextView) findViewById(R.id.game_text_placeholder_highest_value)).setText(highestScoreString);
    }

    /**
     * Loads settings from {@code SharedPreferences}.
     */
    private void loadSettings() {
        var sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        // Second parameter corresponds to default values
        this.music = sharedPreferences.getBoolean("Music", false);
        this.sound = sharedPreferences.getBoolean("Sound", false);
    }

    /**
     * Saves the new high score in {@code SharedPreferences}.
     */
    public void saveNewHighestScore() {
        if (madGrid.isHighestScore()) {
            var sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            var editor = sharedPreferences.edit();
            var newHighestString = Integer.toString(madGrid.getKey().size());
            // Key: mode, value: new high score for the current game mode
            editor.putString(madGrid.getMode(), newHighestString);
            editor.apply();
        }
    }
}