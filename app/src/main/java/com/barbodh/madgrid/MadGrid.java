package com.barbodh.madgrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MadGrid {
    // data variables
    private final String mode;
    private final int highestScore;
    private final ArrayList<Integer> key; // stores the correct sequence of box indexes
    // key length is equivalent to current score

    /**
     * Constructor initializing MadGrid class instance in GameActivity
     * Precondition(s): - <code>mode</code> is either equal to 'Classic', 'Reverse', or 'Crazy'
     *                  - <code>highestScore</code> is a non-negative integer
     * Postcondition(s): MadGrid object is initialized with a key and given mode
     * @param mode - game mode (string version)
     * @param highestScore - highest score of the current game mode
     */
    public MadGrid(String mode, int highestScore) {
        // precondition checking
        if (invalidMode(mode)) throw new IllegalArgumentException(String.format(
                "Invalid Game Mode!\nValid inputs include: 'Classic', 'Reverse', 'Messy'\nProvided: %s", mode
        ));
        if (highestScore < 0) throw new IllegalArgumentException(String.format(
                "Invalid Highest Score!\nHighest score must be a non-negative integer\nProvided: %d", highestScore
        ));

        this.mode = mode;
        key = new ArrayList<>(); // type: Integer (explicit)
        this.highestScore = highestScore;
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
     * Adds new integers to the key depending on game mode
     * Precondition(s): <code>mode</code> is already initialized as 'Classic', 'Reverse', or 'Crazy'
     * Postcondition(s): integer within [1, 4] is added to the key according to game mode
     */
    public void incrementKey() {
        // initialization
        Random rand = new Random();

        // 'Classic' game mode: increment by 1
        if (this.mode.equals("Classic")) {
            this.key.add(rand.nextInt(4) + 1);
        }
        // 'Reverse' game mode: increment by 1; reverse key
        else if (this.mode.equals("Reverse")) {
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
     * Modifier method for clearing <code>key</code> ArrayList
     * Precondition(s): none
     * Postcondition(s): <code>key</code> is cleared (all items removed)
     */
    public void clearKey() {
        this.key.clear();
    }

    //////////////////// Accessor Methods ////////////////////

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
    public ArrayList<Integer> getKey() {
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
}
