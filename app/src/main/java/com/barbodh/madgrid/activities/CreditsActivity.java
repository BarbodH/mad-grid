package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.barbodh.madgrid.R;

public class CreditsActivity extends AppCompatActivity {

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        // Retrieve "TextView" object and apply credit animations
        var creditsText = (TextView) findViewById(R.id.credits_text);
        var creditTextAnimation = AnimationUtils.loadAnimation(this, R.anim.move_vertical);
        creditsText.startAnimation(creditTextAnimation);
    }

    ////////// Event Handler(s) //////////

    /**
     * Navigates to {@code SettingsActivity}
     *
     * @param view Triggered UI element; back button
     */
    public void returnToSettings(View view) {
        var intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}