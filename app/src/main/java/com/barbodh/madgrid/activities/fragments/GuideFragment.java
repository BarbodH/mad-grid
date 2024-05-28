package com.barbodh.madgrid.activities.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.barbodh.madgrid.MadGrid;
import com.barbodh.madgrid.R;
import com.barbodh.madgrid.enums.GameMode;
import com.barbodh.madgrid.tools.SoundPlayer;

import java.util.Locale;
import java.util.Objects;

public abstract class GuideFragment extends Fragment {

    ////////// Field(s) //////////

    private final Handler handler = new Handler();
    private MadGrid madGrid;
    private final GameMode mode;
    private boolean sound;
    private SoundPlayer soundPlayer;

    ////////// Constructor(s) //////////

    public GuideFragment(GameMode mode) {
        this.mode = mode;
    }

    ////////// Initializer(s) //////////

    /**
     * Initializes the fragment for the {@code onCreateView} method of child classes.
     * {@code onCreateView} methods are not inherited for fragments.
     *
     * @param inflater  the {@link LayoutInflater} to inflate the layout
     * @param container the parent view that the fragment's UI should be attached to
     * @return the root view of the inflated layout
     * @throws IllegalArgumentException If an invalid game mode is encountered
     */
    protected View inflateFragment(LayoutInflater inflater, ViewGroup container) {
        // Determine the appropriate layout
        var resource = switch (mode) {
            case CLASSIC -> R.layout.fragment_guide_classic;
            case REVERSE -> R.layout.fragment_guide_reverse;
            case MESSY -> R.layout.fragment_guide_messy;
        };
        var rootView = inflater.inflate(resource, container, false);

        // Load settings, only for sound effects
        var sharedPreferences = this.requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        sound = sharedPreferences.getBoolean("Sound", false); // default value: false

        soundPlayer = new SoundPlayer(getActivity());

        // Adjust grid dimensions dynamically according to device dimensions
        adjustGridDimensions(rootView);

        // Initialize buttons and set appropriate event listeners
        var buttons = new Button[]{
                rootView.findViewById(R.id.guide_button_box_1),
                rootView.findViewById(R.id.guide_button_box_2),
                rootView.findViewById(R.id.guide_button_box_3),
                rootView.findViewById(R.id.guide_button_box_4)
        };
        for (Button button : buttons) {
            button.setOnClickListener(view -> handleBoxClick(view, rootView));
        }

        var buttonTutorial = rootView.findViewById(R.id.guide_button_tutorial);
        buttonTutorial.setOnClickListener(view -> initializeTutorial(rootView));

        // Instantiate Mad Grid class instance
        madGrid = new MadGrid(mode.getValue(), 0, buttons);

        return rootView;
    }

    ////////// Event Handler(s) //////////

    /**
     * Initializes a tutorial session.
     *
     * @param rootView the root view representing the layout of the tutorial session
     */
    private void initializeTutorial(View rootView) {
        madGrid.resetTurnIndex();
        madGrid.clearKey();
        madGrid.incrementKey();
        var delay = madGrid.displaySequence(getActivity())[0];
        switchDescriptionColors(rootView, delay, 0);

        // Set up the tutorial button to
        var buttonTutorial = (Button) rootView.findViewById(R.id.guide_button_tutorial);
        buttonTutorial.setBackgroundResource(R.drawable.guide_button_tutorial_background_inactive);
        buttonTutorial.setText(String.format(Locale.ENGLISH, "Level: %d/5", madGrid.getLevel()));

        // Mark the first solution
        handler.postDelayed(() -> markSolution(rootView), delay);
    }

    /**
     * Handles box click during a tutorial session. Guide grid is responsive only during the user's
     * turn, i.e., playing status is true.
     *
     * @param view     the triggered UI element; grid button
     * @param rootView the root view representing the layout of the tutorial session
     */
    private void handleBoxClick(View view, View rootView) {
        // Initialization
        var TUTORIAL_LENGTH = 5;

        // When user is not playing, this method has no functionality
        if (madGrid.isPlaying()) {
            // Correct response is received by the user
            if (view.getId() == madGrid.getKey().get(madGrid.getTurnIndex()).getId()) {
                clearSolution(rootView);

                // Correct solution; level is not finished yet
                if (madGrid.getTurnIndex() < madGrid.getLevel() - 1) {
                    if (this.sound) this.soundPlayer.playClickSound();
                    madGrid.incrementTurnIndex();
                    markSolution(rootView);
                }
                // Correct solution; level is finished
                else {
                    // Level is finished; tutorial is not finished yet
                    if (madGrid.getLevel() < TUTORIAL_LENGTH) {
                        if (this.sound) this.soundPlayer.playClickSound();
                        madGrid.resetTurnIndex();
                        madGrid.incrementKey();

                        var delays = madGrid.displaySequence(getActivity());
                        switchDescriptionColors(rootView, delays[0], delays[1]);

                        var buttonTutorial = (Button) rootView.findViewById(R.id.guide_button_tutorial);
                        buttonTutorial.setText(String.format(Locale.ENGLISH, "Level: %d/5", madGrid.getLevel()));

                        handler.postDelayed(() -> markSolution(rootView), delays[0]);
                    }
                    // Level is finished; tutorial is finished as well
                    else {
                        if (this.sound) this.soundPlayer.playSuccessSound();
                        displayDialogTutorialCompleted();
                        endTutorial(rootView);
                    }
                }
            }
            // Incorrect response is received by the user
            else {
                if (this.sound) soundPlayer.playGameOverSound();
                displayDialogTutorialFailed();
                endTutorial(rootView);
            }
        }
    }

    ////////// Utility //////////

    /**
     * Finishes an ongoing tutorial session.
     *
     * @param rootView the root view representing the layout of the tutorial session
     */
    private void endTutorial(View rootView) {
        for (Button button : madGrid.getButtons()) {
            button.setBackgroundResource(R.drawable.grid_button_background_inactive);
        }

        clearSolution(rootView);

        var layout = switch (mode) {
            case CLASSIC -> R.id.guide_fragment_classic_text_button_inactive_description;
            case REVERSE -> R.id.guide_fragment_reverse_text_button_inactive_description;
            case MESSY -> R.id.guide_fragment_messy_text_button_inactive_description;
        };
        var watchDescription = (TextView) rootView.findViewById(layout);
        watchDescription.setTextColor(getResources().getColor(R.color.black));

        var buttonTutorial = (Button) rootView.findViewById(R.id.guide_button_tutorial);
        buttonTutorial.setBackgroundResource(R.drawable.guide_button_tutorial_background);
        buttonTutorial.setText(getResources().getString(R.string.guide_button_tutorial));

        madGrid.setPlaying(false);
    }

    /**
     * Displays a dialog upon completion of a tutorial session.
     */
    private void displayDialogTutorialCompleted() {
        var dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_guide_correct);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        handler.postDelayed(dialog::dismiss, 2500);
    }

    /**
     * Displays a dialog upon failure of a tutorial session.
     */
    private void displayDialogTutorialFailed() {
        var dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_guide_incorrect);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        handler.postDelayed(dialog::dismiss, 2500);
    }

    /**
     * Sets solution box inner text as [turn index]/[level].
     *
     * @param rootView the root view representing the layout of the tutorial session
     */
    private void markSolution(View rootView) {
        var solution = (Button) rootView.findViewById(madGrid.getKey().get(madGrid.getTurnIndex()).getId());
        solution.setText(String.format(Locale.ENGLISH, "%d/%d", madGrid.getTurnIndex() + 1, madGrid.getLevel()));
    }

    /**
     * Clears the inner text of the previous solution box.
     *
     * @param rootView the root view representing the layout of the tutorial session
     */
    private void clearSolution(View rootView) {
        var previousSolution = (Button) rootView.findViewById(madGrid.getKey().get(madGrid.getTurnIndex()).getId());
        previousSolution.setText("");
    }

    /**
     * Highlights "watch" and "replicate" messages during the tutorial session."watch" message is
     * highlighted during the sequence display process and "replicate" message is highlighted during
     * the user's turn.
     *
     * @param rootView     the root view representing the layout of the tutorial session
     * @param delay        total duration of sequence display for current level in milliseconds
     * @param delayInitial initial delay before the first switch in milliseconds
     */
    private void switchDescriptionColors(View rootView, int delay, int delayInitial) {
        // Initialization
        var watch = 0;
        var replicate = 0;
        switch (mode) {
            case CLASSIC -> {
                watch = R.id.guide_fragment_classic_text_button_inactive_description;
                replicate = R.id.guide_fragment_classic_text_button_active_description;
            }
            case REVERSE -> {
                watch = R.id.guide_fragment_reverse_text_button_inactive_description;
                replicate = R.id.guide_fragment_reverse_text_button_active_description;
            }
            case MESSY -> {
                watch = R.id.guide_fragment_messy_text_button_inactive_description;
                replicate = R.id.guide_fragment_messy_text_button_active_description;
            }
        }
        var watchDescription = (TextView) rootView.findViewById(watch);
        var replicateDescription = (TextView) rootView.findViewById(replicate);

        // Initial colors ("watch" must be displayed while sequence is being displayed)
        handler.postDelayed(() -> {
            watchDescription.setTextColor(getResources().getColor(R.color.black));
            replicateDescription.setTextColor(Color.parseColor("#C0C0C0"));
        }, delayInitial);

        // Final colors ("replicate" mut be displayed while the user is playing
        handler.postDelayed(() -> {
            watchDescription.setTextColor(Color.parseColor("#C0C0C0"));
            replicateDescription.setTextColor(getResources().getColor(R.color.black));
        }, delay);
    }

    /**
     * Adjusts grid dimensions dynamically based on device dimensions. The grid width and height are
     * set to 95% of the smaller dimension (height or width) of the device.
     */
    private void adjustGridDimensions(View rootView) {
        // Initialization
        var displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Smaller dimension (width/height) should be used, in case of a tablet
        var madGrid_dimension = (int) ((Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels)) * 0.95);

        // Set adjusted dimension size to gridLayout
        var view = rootView.findViewById(R.id.guide_grid_layout);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = madGrid_dimension;
        layoutParams.height = madGrid_dimension;
        view.setLayoutParams(layoutParams);
    }
}