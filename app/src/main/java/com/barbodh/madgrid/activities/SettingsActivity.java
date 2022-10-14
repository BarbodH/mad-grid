package com.barbodh.madgrid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.barbodh.madgrid.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    // data variables
    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean music;
    private boolean sound;
    private int speed;
    private TextView speedometerValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadSettings();
        updateSettingsView();

        // set up 'Display Sequence Settings' seekbar & displayed value
        SeekBar seekbar = (SeekBar) findViewById(R.id.settings_seekbar_speedometer);
        speedometerValue = (TextView) findViewById(R.id.settings_text_speedometer_value);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedometerValue.setText(convertProgressToSpeed(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    /**
     * Loads previously saved settings from Shared Preferences
     * Precondition(s): none
     * Postcondition(s): settings data variables are initialized with default values:
     *                   true for 'music' & 'sound' booleans
     *                   1 for 'speed' integer
     */
    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        this.music = sharedPreferences.getBoolean("Music", true);
        this.sound = sharedPreferences.getBoolean("Sound", true);
        this.speed = sharedPreferences.getInt("Speed", 1); // 0 gets mapped to 1.0x
    }

    /**
     * Determines the state of switches based on previously saved settings in Shared Preferences
     * Precondition(s): settings data variables are already loaded & initialized
     * Postcondition(s): switches are checked/unchecked and seekbar is set up according to loaded settings in Shared Preferences
     */
    private void updateSettingsView() {
        ((SwitchCompat) findViewById(R.id.settings_switch_music)).setChecked(this.music);
        ((SwitchCompat) findViewById(R.id.settings_switch_sound)).setChecked(this.sound);
        ((SeekBar) findViewById(R.id.settings_seekbar_speedometer)).setProgress(this.speed);
        ((TextView) findViewById(R.id.settings_text_speedometer_value)).setText(convertProgressToSpeed(this.speed));
    }

    /**
     * Helper method to convert seekbar progress to speed
     * Precondition(s): 'progress' is an integer in interval [0, 3]
     * Postcondition(s): value is returned (1 decimal place) using the following seekbar progress to speed map:
     *                   0 -> 1.0
     *                   1 -> 1.5
     *                   2 -> 2.0
     *                   3 -> 2.5
     * @param progress - seekbar progress value
     * @return speed value (1 decimal place)
     */
    private String convertProgressToSpeed(int progress) {
        float floatProgress = ((float) progress + 2) / 2;
        return String.format(Locale.getDefault(), "%.1fx", floatProgress);
    }

    /**
     * Saves settings and returns to homepage
     * Precondition(s): none
     * Postcondition(s): settings values are saved to Shared Preferences and MainActivity is started
     * @param view - user interface
     */
    public void returnHome(View view) {
        // save settings
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean musicOn = ((SwitchCompat) findViewById(R.id.settings_switch_music)).isChecked();
        boolean soundOn = ((SwitchCompat) findViewById(R.id.settings_switch_sound)).isChecked();
        int speedValue = ((SeekBar) findViewById(R.id.settings_seekbar_speedometer)).getProgress();
        editor.putBoolean("Music", musicOn);
        editor.putBoolean("Sound", soundOn);
        editor.putInt("Speed", speedValue);
        editor.apply();

        // return to homepage (start MainActivity)
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Opens project repository on browser
     * Precondition(s): none
     * Postcondition(s): Mad-Grid GitHub repository is opened on a browser
     * @param view - user interface
     */
    public void openGitHub(View view) {
        openURL("https://github.com/BarbodH/Mad-Grid");
    }

    /**
     * NOTE: Mad Grid is not published yet; current method is linked to Google Play homepage
     * Opens app page on Google Play
     * Precondition(s): none
     * Postcondition(s): Mad Grid application page is opened on Google Play (either browser/app)
     * @param view - user interface
     */
    public void openGooglePlay(View view) {
        // NOTE: link should be replaced once Mad Grid is published on Google Play
        openURL("https://play.google.com/store/games");
    }

    /**
     * Helper method - opens input URL
     * Precondition(s): 'url' is a valid URL
     * Postcondition(s): input URL is opened on a browser
     * @param url - string URL
     */
    private void openURL(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * Opens the credits page
     * Precondition(s): none
     * Postcondition(s): CreditsActivity is started
     * @param view - user interface
     */
    public void openCredits(View view) {
        startActivity(new Intent(this, CreditsActivity.class));
    }
}