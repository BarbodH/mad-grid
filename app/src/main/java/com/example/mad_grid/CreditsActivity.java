package com.example.mad_grid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        // retrieve TextView object & apply credit animations
        TextView creditsText = (TextView) findViewById(R.id.credits_text);
        Animation creditTextAnimation = AnimationUtils.loadAnimation(this, R.anim.move_vertical);
        creditsText.startAnimation(creditTextAnimation);
    }

    /**
     * returns to settings page
     * Precondition(s): none
     * Postcondition(s): SettingsActivity is started
     * @param view - user interface
     */
    public void returnToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}