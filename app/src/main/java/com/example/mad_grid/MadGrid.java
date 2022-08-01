package com.example.mad_grid;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Random;

public class MadGrid {
    // data variables
    private final String stringMode;
    private int score;
    private final int highestScore;
    public static final String SHARED_PREFS = "sharedPrefs"; // stores & loads information global in app
    private ArrayList<Integer> key; // stores the correct sequence of box indexes
    Context context = MadGridApp.getAppContext();

    /**
     * Constructor initializing MadGrid class instance in GameActivity
     * Precondition(s): 'stringMode' is either equal to 'Classic', 'Reverse', or 'Crazy'
     * Postcondition(s): MadGrid object is initialized with a key and given mode
     * @param stringMode - game mode (string version)
     * @param highestScore - highest score of current game mode
     */
    public MadGrid(String stringMode, int highestScore) {
        this.stringMode = stringMode;
        key = new ArrayList<Integer>();
        score = -1; // score is incremented before each turn, meaning that it'll evaluate to 0 on first turn
        this.highestScore = highestScore;
    }

    /**
     * Multi-parameter constructor intended for testing
     * Precondition(s): 'stringMode' is either equal to 'Classic', 'Reverse', or 'Crazy'
     *                  'score' and 'highestScore' are non-negative integers
     *                  'length' is a positive integer
     * Postcondition(s): MadGrid object is initialized with given parameters
     * @param stringMode - game mode (string version)
     * @param score - score of current game
     * @param highestScore - highest score of current game mode
     * @param keyLength - indicate the length of 'key' ArrayList
     */
    public MadGrid(String stringMode, int score, int highestScore, int keyLength) {
        this.stringMode = stringMode;
        this.score = score;
        this.highestScore = highestScore;

        // generate a key of given length
        Random rand = new Random();
        this.key = new ArrayList<>();
        for (int index = 0; index < keyLength; index++) {
            this.key.add(rand.nextInt(4) + 1);
        }
    }

    /**
     * Adds new integers to the key depending on game mode
     * Precondition(s): 'stringMode' is already initialized as 'Classic', 'Reverse', or 'Crazy'
     * Postcondition(s): integer within [1, 4] is added to the key according to game mode
     */
    public void incrementKey() {
        // initialization
        Random rand = new Random();

        // 'Classic' game mode: increment by 1
        if (this.stringMode.equals("Classic")) {
            this.key.add(rand.nextInt(4) + 1);
        }
        // 'Reverse' game mode: increment by 1; reverse key
        else if (this.stringMode.equals("Reverse")) {
            this.key.add(0, rand.nextInt(4) + 1);
        }
        // 'Crazy' game mode: increment by 1; reset entire key each time
        else {
            int newKeyLength = this.key.size() + 1;
            this.key.clear();
            for (int index = 0; index < newKeyLength; index++) {
                key.add(rand.nextInt(4) + 1);
            }
        }
    }

    /**
     * Determines if current score is larger than highest score
     * Precondition(s): none
     * Postcondition(s): returns true if 'score' is larger than 'highestScore' and false otherwise
     * @return boolean indicating whether highest score is surpassed
     */
    public boolean isHighestScore() {
        return this.score > this.highestScore;
    }

    /**
     * Modifier method for clearing 'key' ArrayList
     * Precondition(s): none
     * Postcondition(s): 'key' is cleared (all items removed)
     */
    public void clearKey() {
        this.key.clear();
    }

    /**
     * Increments score after a successful attempt
     * Precondition(s): none
     * Postcondition(s): 'score' integer is incremented by 1
     */
    public void incrementScore() {
        this.score++;
    }

    /**
     * Modifier method for resetting 'score'
     * Precondition(s): none
     * Postcondition(s): value of 'score' is set to -1
     */
    public void resetScore() {
        this.score = -1; // score is incremented before each turn, meaning that it'll evaluate to 0 on first turn;
    }

    /**
     * Accessor method for 'stringMode'
     * Precondition(s): none
     * Postcondition(s): string 'stringMode' is returned
     * @return getMode - game mode (string version)
     */
    public String getMode() {
        return this.stringMode;
    }

    /**
     * Accessor method for 'key'
     * Precondition(s): none
     * Postcondition(s): ArrayList 'key' of type Integer is returned
     * @return key - correct sequence of boxes
     */
    public ArrayList<Integer> getKey() {
        return this.key;
    }

    /**
     * Accessor method for 'score'
     * Precondition(s): none
     * Postcondition(s): integer 'score' is returned
     * @return score - current game score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Accessor method for 'highestScore'
     * Precondition(s): none
     * Postcondition(s): integer 'highestScore' is returned
     * @return highestScore - highest score of game mode
     */
    public int getHighestScore() {
        return this.highestScore;
    }


}
