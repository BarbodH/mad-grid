package com.barbodh.madgrid;

import android.content.Context;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.barbodh.madgrid.tools.BounceInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MadGrid {

    ////////// Field(s) //////////

    @Setter
    private boolean playing;
    private int turnIndex;
    private int speed;
    private final int highestScore;
    private final String mode;
    private final ArrayList<Button> key; // Key length is equivalent to current score
    private final Button[] buttons;
    private final Handler handler = new Handler(); // Used for delaying executions

    ////////// Constructor(s) //////////

    /**
     * Constructs an instance of the {@code MadGrid} class.
     *
     * @param mode         the game mode, must be one of "Classic", "Reverse", or "Messy"
     * @param highestScore the highest score for the given game mode, must be non-negative
     * @param buttons      reference to buttons on the screen, must contain exactly 4 buttons
     * @throws IllegalArgumentException if mode is invalid, highestScore is negative, or buttons
     *                                  array length is not 4
     */
    public MadGrid(String mode, int highestScore, Button[] buttons) {
        // Precondition checking
        if (invalidMode(mode)) {
            throw new IllegalArgumentException(String.format(
                    "Invalid Game Mode!\nValid inputs include: 'Classic', 'Reverse', 'Messy'.\nProvided: %s", mode
            ));
        }
        if (highestScore < 0) {
            throw new IllegalArgumentException(String.format(
                    "Invalid Highest Score!\nHighest score must be a non-negative integer.\nProvided: %d", highestScore
            ));
        }
        if (buttons.length != 4) {
            throw new IllegalArgumentException(String.format(
                    "Invalid Array of Buttons!\nOnly 4 buttons are expected by MadGrid instance.\nProvided: %d", buttons.length
            ));
        }

        this.playing = false;
        this.turnIndex = 0;
        this.speed = 0; // Arbitrary value; must be updated by "GameActivity" according to settings
        this.highestScore = highestScore;
        this.mode = mode;
        this.key = new ArrayList<>();
        this.buttons = buttons;
    }

    /**
     * Validates the input game mode.
     *
     * @param mode the game mode to validate
     * @return {@code true} if the given mode is invalid, {@code false} otherwise
     */
    private boolean invalidMode(String mode) {
        var validModes = Arrays.asList("Classic", "Reverse", "Messy");
        return !validModes.contains(mode);
    }

    ////////// Modifier(s) //////////

    /**
     * Sets the sequence display speed.
     *
     * @param speed the speed to set, must be an integer within the range [0, 3]
     * @throws IllegalArgumentException if the speed is outside the range [0, 3]
     */
    public void setSpeed(int speed) {
        if (speed > 3 || speed < 0) {
            throw new IllegalArgumentException(String.format(
                    "Invalid Speed!\nSpeed must be an integer within [0, 3] range.\nProvided: %d", speed
            ));
        }
        this.speed = speed;
    }

    /**
     * Increments turn index while the user is playing.
     */
    public void incrementTurnIndex() {
        turnIndex++;
    }

    /**
     * Resets turn index upon the start of a new level.
     */
    public void resetTurnIndex() {
        turnIndex = 0;
    }

    /**
     * Adds a new integer to the key depending on the game mode.
     */
    public void incrementKey() {
        var rand = new Random();

        // "Classic" game mode: increment by 1
        if (mode.equals("Classic")) {
            key.add(buttons[rand.nextInt(4)]);
        }
        // "Reverse" game mode: increment by 1; reverse key
        else if (mode.equals("Reverse")) {
            key.add(0, buttons[rand.nextInt(buttons.length)]);
        }
        // "Messy" game mode: increment by 1; reset entire key each time
        else {
            var newKeyLength = key.size() + 1;
            key.clear();
            for (var index = 0; index < newKeyLength; index++) {
                key.add(buttons[rand.nextInt(buttons.length)]);
            }
        }
    }

    /**
     * Clears the key.
     */
    public void clearKey() {
        key.clear();
    }

    ////////// Accessor(s) //////////

    /**
     * Determines if the current score (length of {@code key}) is larger than the highest score.
     *
     * @return {@code true} if the current score is larger than the highest score, {@code false}
     * otherwise
     */
    public boolean isHighestScore() {
        return key.size() > highestScore;
    }

    public int getLevel() {
        return key.size();
    }

    ////////// Utility //////////

    /**
     * Displays the newly incremented sequence to the user before their turn.
     *
     * @param context the context in which to display the sequence
     * @return an integer array containing delay values: [total delay, button deactivation delay]
     * @throws IllegalArgumentException if the speed value is out of range [0, 3]
     */
    public int[] displaySequence(Context context) {
        playing = false;
        int delay, delayIncrement, delayButtonDeactivation;
        switch (speed) { // delay = delay_1.0x / speed
            case 0: // speed: 1.0x
                delay = 750;
                delayIncrement = 750;
                delayButtonDeactivation = 450;
                break;
            case 1: // speed: 1.5x
                delay = 500;
                delayIncrement = 500;
                delayButtonDeactivation = 300;
                break;
            case 2: // speed: 2.0x
                delay = 375;
                delayIncrement = 375;
                delayButtonDeactivation = 225;
                break;
            case 3: // speed: 2.5x
                delay = 300;
                delayIncrement = 300;
                delayButtonDeactivation = 180;
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "Invalid Speed!\nSpeed data variable must be within range [0, 3].\nCurrent value: %d", speed
                ));
        }

        // Turn off button feedback to user
        handler.postDelayed(this::deactivateButtons, delayButtonDeactivation);

        // Start display of sequence
        // Handler object prevents simultaneous grid animations
        if (mode.equals("Reverse")) { // Iterate backwards through key for "Reverse" mode
            for (var index = key.size() - 1; index >= 0; index--) {
                var button = key.get(index);
                handler.postDelayed(() -> bounceButton(context, button), delay);
                delay += delayIncrement;
            }
        } else { // Iterate regularly through key for "Classic" and "Messy" modes
            for (Button button : key) {
                handler.postDelayed(() -> bounceButton(context, button), delay);
                delay += delayIncrement;
            }
        }

        // Handler object delays change of value of "playingStatus" until whole sequence is displayed
        handler.postDelayed(() -> {
            playing = true;
            activateButtons(); // Turn on button feedback to user
        }, delay);

        return (new int[]{delay, delayButtonDeactivation});
    }

    /**
     * Activates button feedback to user when game is ongoing, i.e., {@code playingStatus} is
     * {@code true}.
     */
    private void activateButtons() {
        for (var button : buttons)
            button.setBackgroundResource(R.drawable.grid_button_background);
    }

    /**
     * Deactivates button feedback to user when during sequence display, i.e., {code playingStatus}
     * is {@code false}.
     */
    private void deactivateButtons() {
        for (var button : buttons)
            button.setBackgroundResource(R.drawable.grid_button_background_inactive);
    }

    /**
     * Handles individual button animations.
     *
     * @param context the context in which to perform the animation
     * @param button  the button to animate
     * @throws IllegalArgumentException if the speed value is out of range [0, 3]
     */
    private void bounceButton(Context context, Button button) {
        var animation = switch (speed) {
            case 0 -> // speed: 1.0x
                    AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.bounce_1_0);
            case 1 -> // speed: 1.5x
                    AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.bounce_1_5);
            case 2 -> // speed: 2.0x
                    AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.bounce_2_0);
            case 3 -> // speed: 2.5x
                    AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.bounce_2_5);
            default -> throw new IllegalArgumentException(String.format(
                    "Invalid Speed!\nSpeed data variable must be within range [0, 3].\nCurrent value: %d", speed
            ));
        };
        var bounceInterpolator = new BounceInterpolator(0.2, 20);
        animation.setInterpolator(bounceInterpolator);
        button.startAnimation(animation);
    }
}