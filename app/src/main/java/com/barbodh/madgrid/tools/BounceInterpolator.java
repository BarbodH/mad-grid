package com.barbodh.madgrid.tools;

import android.view.animation.Interpolator;

/**
 * An interpolator for creating bouncing animations in {@code GameActivity}.
 */
public class BounceInterpolator implements Interpolator {

    ////////// Field(s) //////////

    private final double amplitude;
    private final double frequency;

    ////////// Constructor(s) //////////

    /**
     * Constructs a {@code BounceInterpolator} with specified amplitude and frequency.
     *
     * @param amplitude amplitude of bounce animation
     * @param frequency frequency of bounce animation
     */
    public BounceInterpolator(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    ////////// Utility //////////

    @Override
    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / amplitude) * Math.cos(frequency * time) + 1);
    }
}
