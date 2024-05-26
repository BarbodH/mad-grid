package com.barbodh.madgrid.tools;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.barbodh.madgrid.R;

/**
 * A sound player for playing audio resources in the application.
 */
public class SoundPlayer {

    ////////// Field(s) //////////

    private static SoundPool soundPool;
    private static int clickSound;
    private static int gameOverSound;
    private static int successSound;

    ////////// Constructor(s) //////////

    /**
     * Constructs a {@code SoundPlayer} with specified {@code context}.
     *
     * @param context the application context used for loading audio resources.
     */
    public SoundPlayer(Context context) {
        var builder = new SoundPool.Builder();
        builder.setMaxStreams(2); // Maximum simultaneous streams
        var attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        builder.setAudioAttributes(attributes);
        soundPool = builder.build();

        clickSound = soundPool.load(context, R.raw.click, 1);
        gameOverSound = soundPool.load(context, R.raw.game_over, 1);
        successSound = soundPool.load(context, R.raw.success, 1);
    }

    ////////// Utility //////////

    /**
     * Plays the "click.wav" audio file.
     */
    public void playClickSound() {
        soundPool.play(clickSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * Plays "game_over.wav" audio file.
     */
    public void playGameOverSound() {
        soundPool.play(gameOverSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * Plays "success.mp3" audio file.
     */
    public void playSuccessSound() {
        soundPool.play(successSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
