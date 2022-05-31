package com.example.mad_grid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

    public void saveSettings(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean musicOn = ((Switch) findViewById(R.id.settingsMusicSwitch)).isChecked();
        boolean VibrationOn = ((Switch) findViewById(R.id.settingsVibrationSwitch)).isChecked();
        boolean soundOn = ((Switch) findViewById(R.id.settingsSoundSwitch)).isChecked();
        editor.putBoolean("Music", musicOn);
        editor.putBoolean("Vibration", VibrationOn);
        editor.putBoolean("Sound", soundOn);
        Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
        editor.apply();
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        this.music = sharedPreferences.getBoolean("Music", true);
        this.vibration = sharedPreferences.getBoolean("Vibration", true);
        this.sound = sharedPreferences.getBoolean("Sound", true);
    }

    private void updateSettingsView() {
        ((Switch) findViewById(R.id.settingsMusicSwitch)).setChecked(this.music);
        ((Switch) findViewById(R.id.settingsVibrationSwitch)).setChecked(this.vibration);
        ((Switch) findViewById(R.id.settingsSoundSwitch)).setChecked(this.sound);
    }
}