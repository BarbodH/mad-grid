package com.barbodh.madgrid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.barbodh.madgrid.R;
import com.barbodh.madgrid.activities.CreditsActivity;
import com.barbodh.madgrid.activities.MainActivity;

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
        ((SwitchCompat) findViewById(R.id.settings_switch_music)).setChecked(this.music);
        ((SwitchCompat) findViewById(R.id.settings_switch_vibration)).setChecked(this.vibration);
        ((SwitchCompat) findViewById(R.id.settings_switch_sound)).setChecked(this.sound);
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
        boolean musicOn = ((SwitchCompat) findViewById(R.id.settings_switch_music)).isChecked();
        boolean VibrationOn = ((SwitchCompat) findViewById(R.id.settings_switch_vibration)).isChecked();
        boolean soundOn = ((SwitchCompat) findViewById(R.id.settings_switch_sound)).isChecked();
        editor.putBoolean("Music", musicOn);
        editor.putBoolean("Vibration", VibrationOn);
        editor.putBoolean("Sound", soundOn);
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