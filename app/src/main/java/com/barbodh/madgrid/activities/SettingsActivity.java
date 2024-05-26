package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.barbodh.madgrid.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    ////////// Field(s) //////////

    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean music;
    private boolean sound;
    private int speed;
    private TextView speedometerValue;

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadSettings();
        updateSettingsView();

        // Set up "Display Sequence Settings" seekbar and displayed value
        var seekbar = (SeekBar) findViewById(R.id.settings_seekbar_speedometer);
        speedometerValue = findViewById(R.id.settings_text_speedometer_value);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedometerValue.setText(convertProgressToSpeed(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    ////////// Event Handler(s) //////////

    /**
     * Saves settings and navigates to {@code MainActivity}
     *
     * @param view the triggered UI element; back button
     */
    public void returnHome(View view) {
        var sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        var editor = sharedPreferences.edit();
        var musicOn = ((SwitchCompat) findViewById(R.id.settings_switch_music)).isChecked();
        var soundOn = ((SwitchCompat) findViewById(R.id.settings_switch_sound)).isChecked();
        var speedValue = ((SeekBar) findViewById(R.id.settings_seekbar_speedometer)).getProgress();
        editor.putBoolean("Music", musicOn);
        editor.putBoolean("Sound", soundOn);
        editor.putInt("Speed", speedValue);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Opens the project GitHub repository on the browser.
     *
     * @param view the triggered UI element; "View on GitHub" layout
     */
    public void openGitHub(View view) {
        openURL("https://github.com/BarbodH/Mad-Grid");
    }

    /**
     * Opens app page on Google Play
     *
     * @param view the triggered UI element; "Rate on Google Play" layout
     */
    public void openGooglePlay(View view) {
        openURL("https://play.google.com/store/apps/details?id=com.barbodh.madgrid");
    }

    /**
     * Navigates to {@code CreditsActivity}.
     *
     * @param view the triggered UI element; "Credits" layout
     */
    public void openCredits(View view) {
        startActivity(new Intent(this, CreditsActivity.class));
    }

    ////////// Utility //////////

    /**
     * Invokes the {@code returnHome} method to save settings before leaving the page.
     *
     * @noinspection deprecation
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        var view = findViewById(android.R.id.content).getRootView();
        returnHome(view);
    }

    /**
     * Loads previously saved settings from {@code SharedPreferences}.
     */
    private void loadSettings() {
        var sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        this.music = sharedPreferences.getBoolean("Music", true);
        this.sound = sharedPreferences.getBoolean("Sound", true);
        this.speed = sharedPreferences.getInt("Speed", 1); // 0 gets mapped to 1.0x
    }

    /**
     * Determines the state of switches based on the previously saved settings in
     * {@code SharedPreferences}.
     */
    private void updateSettingsView() {
        ((SwitchCompat) findViewById(R.id.settings_switch_music)).setChecked(this.music);
        ((SwitchCompat) findViewById(R.id.settings_switch_sound)).setChecked(this.sound);
        ((SeekBar) findViewById(R.id.settings_seekbar_speedometer)).setProgress(this.speed);
        ((TextView) findViewById(R.id.settings_text_speedometer_value)).setText(convertProgressToSpeed(this.speed));
    }

    /**
     * Converts seekbar progress value to speed multiplier.
     *
     * @param progress seekbar progress integer value within range [0, 3]
     * @return the speed value with one decimal place precision
     */
    private String convertProgressToSpeed(int progress) {
        if (progress < 0 || progress > 3) {
            throw new IllegalArgumentException("Seekbar progress value must be an integer in interval [0, 3].");
        }

        var floatProgress = ((float) progress + 2) / 2;
        return String.format(Locale.getDefault(), "%.1fx", floatProgress);
    }

    /**
     * Opens the input URL.
     *
     * @param url string URL
     */
    private void openURL(String url) {
        var uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}