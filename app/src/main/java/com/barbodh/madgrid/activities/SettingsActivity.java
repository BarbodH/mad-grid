package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.barbodh.madgrid.R;

public class SettingsActivity extends AppCompatActivity {

    ////////// Field(s) //////////

    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean music;
    private boolean sound;

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadSettings();
        updateSettingsView();
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
        editor.putBoolean("Music", musicOn);
        editor.putBoolean("Sound", soundOn);
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
    }

    /**
     * Determines the state of switches based on the previously saved settings in
     * {@code SharedPreferences}.
     */
    private void updateSettingsView() {
        ((SwitchCompat) findViewById(R.id.settings_switch_music)).setChecked(this.music);
        ((SwitchCompat) findViewById(R.id.settings_switch_sound)).setChecked(this.sound);
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