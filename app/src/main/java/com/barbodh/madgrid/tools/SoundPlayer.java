package com.barbodh.madgrid.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.barbodh.madgrid.R;

public class SoundPlayer {
    // data variables
    private static SoundPool soundPool; // includes all the required sound effects
    private static int clickSound;
    private static int gameOverSound;
    private static int successSound;

    /**
     * Constructor method
     * Precondition(s): none
     * Postcondition(s): instantiates a SoundPlayer object
     * @param context - activity
     */
    public SoundPlayer(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        clickSound = soundPool.load(context, R.raw.click, 1);
        gameOverSound = soundPool.load(context, R.raw.game_over, 1);
        successSound = soundPool.load(context, R.raw.success, 1);
    }

    /**
     * Plays 'click.wav' audio file
     * Precondition(s): none
     * Postcondition(s): 'click.wav' audio file is played with the specified attributes
     */
    public void playClickSound() {
        soundPool.play(clickSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * Plays 'game_over.wav' audio file
     * Precondition(s): none
     * Postcondition(s): 'game_over.wav' audio file is played with the specified attributes
     */
    public void playGameOverSound() {
        soundPool.play(gameOverSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * Plays 'success.mp3' audio file
     * Precondition(s): none
     * Postcondition(s): 'success.mp3' audio file is played with the specified attributes
     */
    public void playSuccessSound() {
       soundPool.play(successSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
