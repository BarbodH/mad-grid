package com.example.mad_grid;

import android.view.animation.Interpolator;

public class BounceInterpolator implements Interpolator {
    // data variables
    private double amplitude;
    private double frequency;

    /**
     *
     * @param amplitude - amplitude of bounce animation
     * @param frequency - frequency of bounce animation
     */
    BounceInterpolator(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    @Override
    public float getInterpolation(float time) {
        return (float)(-1 * Math.pow(Math.E, -time/amplitude) * Math.cos(frequency * time) + 1);
    }
}
