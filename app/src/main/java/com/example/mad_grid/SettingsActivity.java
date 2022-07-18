package com.example.mad_grid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    // data variables
    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean music;
    private boolean vibration;
    private boolean sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadSettings();
        updateSettingsView();
    }

    /**
     * Loads previously saved settings from Shared Preferences
     * Precondition(s): none
     * Postcondition(s): settings boolean data variables are initialized with a default value of 'true'
     */
    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        this.music = sharedPreferences.getBoolean("Music", true);
        this.vibration = sharedPreferences.getBoolean("Vibration", true);
        this.sound = sharedPreferences.getBoolean("Sound", true);
    }

    /**
     * Determine the state of switches based on previously saved settings in Shared Preferences
     * Precondition(s): 'music', 'vibration', and 'sound' boolean data variables are already loaded & initialized
     * Postcondition(s): Switches are checked/unchecked according to loaded boolean settings in Shared Preferences
     */
    private void updateSettingsView() {
        ((SwitchCompat) findViewById(R.id.settingsMusicSwitch)).setChecked(this.music);
        ((SwitchCompat) findViewById(R.id.settingsVibrationSwitch)).setChecked(this.vibration);
        ((SwitchCompat) findViewById(R.id.settingsSoundSwitch)).setChecked(this.sound);
    }

    /**
     * Saves settings and returns to homepage
     * Precondition(s): none
     * Postcondition(s): settings booleans are saved to Shared Preferences and MainActivity is started
     * @param view - user interface
     */
    public void returnHome(View view) {
        // save settings
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean musicOn = ((SwitchCompat) findViewById(R.id.settingsMusicSwitch)).isChecked();
        boolean VibrationOn = ((SwitchCompat) findViewById(R.id.settingsVibrationSwitch)).isChecked();
        boolean soundOn = ((SwitchCompat) findViewById(R.id.settingsSoundSwitch)).isChecked();
        editor.putBoolean("Music", musicOn);
        editor.putBoolean("Vibration", VibrationOn);
        editor.putBoolean("Sound", soundOn);
        editor.apply();

        // return to homepage (start MainActivity)
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}