package com.barbodh.madgrid;

import android.content.Context;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.barbodh.madgrid.tools.BounceInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MadGrid {
    // data variables
    private boolean playingStatus; // indicates whether the user is playing their turn
    private int turnIndex;
    private int speed;
    private final int highestScore;
    private final String mode;
    private final ArrayList<Button> key; // stores the correct sequence of buttons
    // key length is equivalent to current score
    private final Button[] buttons; // stores reference to button objects used by GameActivity
    private final Handler handler = new Handler(); // used for delaying executions

    //////////////////// Constructor(s) ////////////////////

    /**
     * Constructor initializing MadGrid class instance in GameActivity
     * Precondition(s): - <code>mode</code> is either equal to 'Classic', 'Reverse', or 'Messy'
     *                  - <code>highestScore</code> is a non-negative integer
     *                  - <code>buttons</code> is a Button array of length 4
     * Postcondition(s): MadGrid object is initialized with according to the given mode, highest score, and buttons
     * @param mode - game mode (string version)
     * @param highestScore - highest score of the current game mode
     * @param buttons - array of button objects displayed on the user interface
     */
    public MadGrid(String mode, int highestScore, Button[] buttons) {
        // precondition checking
        if (invalidMode(mode)) throw new IllegalArgumentException(String.format(
                "Invalid Game Mode!\nValid inputs include: 'Classic', 'Reverse', 'Messy'.\nProvided: %s", mode
        ));
        if (highestScore < 0) throw new IllegalArgumentException(String.format(
                "Invalid Highest Score!\nHighest score must be a non-negative integer.\nProvided: %d", highestScore
        ));
        if (buttons.length != 4) throw new IllegalArgumentException(String.format(
                "Invalid Array of Buttons!\nOnly 4 buttons are expected by MadGrid instance.\nProvided: %d", buttons.length
        ));

        this.playingStatus = false;
        this.turnIndex = 0;
        this.speed = 0; // arbitrary value; must be updated by GameActivity according to settings
        this.highestScore = highestScore;
        this.mode = mode;
        this.key = new ArrayList<>(); // type: Integer (explicit)
        this.buttons = buttons;
    }

    /**
     * Helper method for validating an input game mode
     * @param mode - game mode
     * @return boolean indicating whether the given mode is invalid
     */
    private boolean invalidMode(String mode) {
        List<String> validModes = Arrays.asList("Classic", "Reverse", "Messy");
        return !validModes.contains(mode);
    }

    //////////////////// Modifier Methods ////////////////////

    /**
     * Modifier method for setting the sequence display speed
     * Precondition(s): <code>speed</code> is within range [0, 3]
     * Postcondition(s): <code>speed</code> data variable is set to the given argument
     * @param speed - integer indicating sequence display speed
     */
    public void setSpeed(int speed) {
        if (speed > 3 || speed < 0) throw new IllegalArgumentException(String.format(
                "Invalid Speed!\nSpeed must be an integer within [0, 3] range.\nProvided: %d", speed
        ));
        this.speed = speed;
    }

    /**
     * Modifier method for setting the playing status
     * Precondition(s): none
     * Postcondition(s): <code>playingStatus</code> data variable is set to the given argument
     * @param playingStatus - boolean indicating playing status
     */
    public void setPlayingStatus(boolean playingStatus) {
        this.playingStatus = playingStatus;
    }

    /**
     * Modifier method for updating turn index while the user is playing
     * Precondition(s): none
     * Postcondition(s): <code>turnIndex</code> is incremented by 1
     */
    public void incrementTurnIndex() {
        this.turnIndex++;
    }

    /**
     * Modifier method for updating turn index upon the start of a new level
     * Precondition(s): none
     * Postcondition(s): <code>turnIndex</code> is set to 0
     */
    public void resetTurnIndex() {
        this.turnIndex = 0;
    }

    /**
     * Adds new integers to the key depending on game mode
     * Precondition(s): <code>mode</code> is already initialized as 'Classic', 'Reverse', or 'Messy'
     * Postcondition(s): integer within [1, 4] is added to the key according to game mode
     */
    public void incrementKey() {
        // initialization
        Random rand = new Random();

        // 'Classic' game mode: increment by 1
        if (this.mode.equals("Classic")) {
            this.key.add(this.buttons[rand.nextInt(4)]);
        }
        // 'Reverse' game mode: increment by 1; reverse key
        else if (this.mode.equals("Reverse")) {
            this.key.add(0, this.buttons[rand.nextInt(this.buttons.length)]);
        }
        // 'Messy' game mode: increment by 1; reset entire key each time
        else {
            int newKeyLength = this.key.size() + 1;
            this.key.clear();
            for (int index = 0; index < newKeyLength; index++) {
                this.key.add(this.buttons[rand.nextInt(this.buttons.length)]);
            }
        }
    }

    /**
     * Modifier method for clearing <code>key</code> ArrayList
     * Precondition(s): none
     * Postcondition(s): <code>key</code> is cleared (all items removed)
     */
    public void clearKey() {
        this.key.clear();
    }

    //////////////////// Accessor Methods ////////////////////

    /**
     * Accessor method for the current playing status
     * Precondition(s): none
     * Postcondition(s): <code>playingStatus</code> data variable is returned
     * @return <code>playingStatus</code> - boolean value indicating whether the user is playing their turn
     */
    public boolean getPlayingStatus() {
        return this.playingStatus;
    }

    /**
     * Accessor method for the turn index
     * Precondition(s): none
     * Postcondition(s): <code>turnIndex</code> data variable is returned
     * @return <code>turnIndex</code> - integer indicating the current turn index while the user is playing
     */
    public int getTurnIndex() {
        return this.turnIndex;
    }

    /**
     * Accessor method for <code>mode</code>
     * Precondition(s): none
     * Postcondition(s): string value of <code>mode</code> is returned
     * @return <code>mode</code> - game mode (string version)
     */
    public String getMode() {
        return this.mode;
    }

    /**
     * Accessor method for <code>key</code>
     * Precondition(s): none
     * Postcondition(s): ArrayList <code>key</code> of type Integer is returned
     * @return <code>key</code> - correct sequence of boxes
     */
    public ArrayList<Button> getKey() {
        return this.key;
    }

    /**
     * Accessor method for <code>highestScore</code>
     * Precondition(s): none
     * Postcondition(s): integer value of <code>highestScore</code> is returned
     * @return <code>highestScore</code> - highest score of the current game mode
     */
    public int getHighestScore() {
        return this.highestScore;
    }

    /**
     * Accessor method determining if current score (key length) is larger than highest score
     * Precondition(s): none
     * Postcondition(s): returns true if score (i.e., <code>key</code> length) is larger than <code>highestScore</code> and false otherwise
     * @return boolean indicating whether highest score is surpassed
     */
    public boolean isHighestScore() {
        return this.key.size() > this.highestScore;
    }

    /**
     * Accessor method for the grid buttons
     * Precondition(s): none
     * Postcondition(s): <code>buttons</code> data variable is returned
     * @return array of buttons
     */
    public Button[] getButtons() {
        return this.buttons;
    }

    public int getLevel() {
        return this.key.size();
    }

    //////////////////// Utility Methods ////////////////////

    /**
     * Displays newly incremented sequence to user before their turn
     * Precondition(s): - <code>context</code> points to the activity (GameActivity/GuideActivity) calling the method
     *                  - <code>speed</code> data variable is within range [0, 3]
     *                  - helper methods are implemented and functional
     *                      + activateButtons
     *                      + deactivateButtons
     *                      + bounceButton
     * Postcondition(s): <code>key</code> elements are displayed using sequential button animations and delay values are returned
     * @return integer array containing the following delay values respectively:
     *          - <code>delay</code> indicating the total delay of the display sequence process
     *          - <code>delayButtonDeactivation</code> indicating the initial delay of button deactivation (useful for reset button)
     * delay - integer indicating duration of sequence display process
     */
    public int[] displaySequence(Context context) {
        // initialization
        this.playingStatus = false;
        int delay, delayIncrement, delayButtonDeactivation;
        int[] returnData = new int[2];
        switch (this.speed) { // delay = delay_1.0x / speed
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
                        "Invalid Speed!\nSpeed data variable must be within range [0, 3].\nCurrent value: %d", this.speed
                ));
        }

        // turn off button feedback to user
        handler.postDelayed(this::deactivateButtons, delayButtonDeactivation);

        // start display of sequence
        // handler object prevents simultaneous grid animations
        if (this.mode.equals("Reverse")) { // iterate backwards through key for 'Reverse' mode
            for (int index = this.key.size() - 1; index >= 0; index--) {
                Button button = this.key.get(index);
                handler.postDelayed(() -> bounceButton(context, button), delay);
                delay += delayIncrement;
            }
        }
        else { // iterate regularly through key for 'Classic' and 'Messy' modes
            for (Button button : this.key) {
                handler.postDelayed(() -> bounceButton(context, button), delay);
                delay += delayIncrement;
            }
        }

        // handler object delays change of value of 'playingStatus' until whole sequence is displayed
        handler.postDelayed(() -> {
            this.playingStatus = true;
            activateButtons(); // turn on button feedback to user
        }, delay);

        return (new int[] {delay, delayButtonDeactivation});
    }

    /**
     * Helper method to activate button feedback to user when game is ongoing (<code>playingStatus</code> = true)
     * Precondition(s): none
     * Postcondition(s): buttons' background becomes lighter with ripple effect
     */
    private void activateButtons() {
        for (Button button : buttons)
            button.setBackgroundResource(R.drawable.grid_button_background);
    }

    /**
     * Helper method to deactivate button feedback to user during sequence display (<code>playingStatus</code> = false)
     * Precondition(s): none
     * Postcondition(s): buttons' background becomes darker without ripple effect
     */
    private void deactivateButtons() {
        for (Button button : buttons)
            button.setBackgroundResource(R.drawable.grid_button_background_inactive);
    }

    /**
     * Helper method to handle individual button animations
     * Precondition(s): <code>button</code> is a valid reference to a grid button on the user interface
     * Postcondition(s): target button bounces at a rate specified by <code>speed</code> data variable
     * @param context - activity (GameActivity/GuideActivity) calling the method
     * @param button - button object on the user interface to bounce
     */
    private void bounceButton(Context context, Button button) {
        Animation animation;
        switch (this.speed) {
            case 0: // speed: 1.0x
                animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.bounce_1_0);
                break;
            case 1: // speed: 1.5x
                animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.bounce_1_5);
                break;
            case 2: // speed: 2.0x
                animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.bounce_2_0);
                break;
            case 3: // speed: 2.5x
                animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.bounce_2_5);
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "Invalid Speed!\nSpeed data variable must be within range [0, 3].\nCurrent value: %d", this.speed
                ));
        }
        BounceInterpolator bounceInterpolator = new BounceInterpolator(0.2, 20);
        animation.setInterpolator(bounceInterpolator);
        button.startAnimation(animation);
    }
}