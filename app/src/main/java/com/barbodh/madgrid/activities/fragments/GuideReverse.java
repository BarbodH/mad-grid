package com.barbodh.madgrid.activities.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.barbodh.madgrid.MadGrid;
import com.barbodh.madgrid.R;

import java.util.Locale;
import java.util.Objects;

public class GuideReverse extends Fragment {
    // data variables
    private final Handler handler = new Handler(); // used for delaying executions
    private MadGrid madGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide_reverse, container, false);

        // adjust grid dimensions dynamically according to device dimensions
        adjustGridDimensions(rootView);

        // initialize buttons and set appropriate event listeners
        Button[] buttons = {
                (Button) rootView.findViewById(R.id.guide_button_box_1),
                (Button) rootView.findViewById(R.id.guide_button_box_2),
                (Button) rootView.findViewById(R.id.guide_button_box_3),
                (Button) rootView.findViewById(R.id.guide_button_box_4)
        };
        for (Button button : buttons) {
            button.setOnClickListener(view -> {
                handleBoxClick(view, rootView);
            });
        }

        Button buttonTutorial = (Button) rootView.findViewById(R.id.guide_button_tutorial);
        buttonTutorial.setOnClickListener(view -> initializeTutorial(rootView));

        // instantiate Mad Grid class instance
        this.madGrid = new MadGrid("Reverse", 0, buttons);

        return rootView;
    }

    /**
     * Helper method for initialization of a tutorial session
     * Precondition(s): none
     * Postcondition(s): new tutorial session is initialized
     * @param rootView - root interface
     */
    private void initializeTutorial(View rootView) {
        madGrid.resetTurnIndex();
        madGrid.clearKey();
        madGrid.incrementKey();
        int delay = madGrid.displaySequence(getActivity())[0];
        switchDescriptionColors(rootView, delay, 0);

        // set up the tutorial button to
        Button buttonTutorial = (Button) rootView.findViewById(R.id.guide_button_tutorial);
        buttonTutorial.setBackgroundResource(R.drawable.guide_button_tutorial_background_inactive);
        buttonTutorial.setText(String.format(Locale.ENGLISH, "Level: %d/5", madGrid.getLevel()));

        // mark the first solution
        handler.postDelayed(() -> {
            markSolution(rootView);
        }, delay);
    }

    /**
     * Helper method for handling box click during a tutorial session
     * Precondition(s): none
     * Postcondition(s): - guide grid boxes are responsive only during the user's turn (i.e., playing status is true)
     *                   - incorrect responses by the user are ignored
     *                   - in case of correct response, the following 3 cases are handled appropriately:
     *                      + level is not finished
     *                      + level is finished; tutorial is not finished
     *                      + level is finished; tutorial is finished
     *                      - correct solution is marked on the boxes in order to guide the user
     * @param view - user interface
     * @param rootView - root interface
     */
    private void handleBoxClick(View view, View rootView) {
        // initialization
        int TUTORIAL_LENGTH = 5;

        // when user is not playing, this method has no functionality
        if (madGrid.getPlayingStatus()) {
            // when user does not input correct solution, this method has no functionality
            if (view.getId() == madGrid.getKey().get(madGrid.getTurnIndex()).getId()) {
                clearSolution(rootView);
                // correct solution; level is not finished yet
                if (madGrid.getTurnIndex() < madGrid.getLevel() - 1) {
                    madGrid.incrementTurnIndex();
                    markSolution(rootView);
                }
                // correct solution; level is finished
                else {
                    // level is finished; tutorial is not finished yet
                    if (madGrid.getLevel() < TUTORIAL_LENGTH) {
                        madGrid.resetTurnIndex();
                        madGrid.incrementKey();

                        int[] delays = madGrid.displaySequence(getActivity());
                        switchDescriptionColors(rootView, delays[0], delays[1]);

                        Button buttonTutorial = (Button) rootView.findViewById(R.id.guide_button_tutorial);
                        buttonTutorial.setText(String.format(Locale.ENGLISH, "Level: %d/5", madGrid.getLevel()));

                        handler.postDelayed(() -> { markSolution(rootView); }, delays[0]);
                    }
                    // level is finished; tutorial is finished as well
                    else {
                        for (Button button : madGrid.getButtons()) {
                            button.setBackgroundResource(R.drawable.grid_button_background_inactive);
                        }

                        TextView watchDescription = rootView.findViewById(R.id.guide_fragment_reverse_text_button_inactive_description);
                        watchDescription.setTextColor(getResources().getColor(R.color.black));

                        String message = "You have finished the tutorial!";
                        (Toast.makeText(getActivity(), message, Toast.LENGTH_LONG)).show();

                        Button buttonTutorial = (Button) rootView.findViewById(R.id.guide_button_tutorial);
                        buttonTutorial.setBackgroundResource(R.drawable.guide_button_tutorial_background);
                        buttonTutorial.setText(getResources().getString(R.string.guide_button_tutorial));
                    }
                }
            }
        }
    }

    /**
     * Helper method for marking the solution box
     * Precondition(s): none
     * Postcondition(s): solution box inner text is set to the following: <em>turnIndex</em>/<em>level</em>
     * @param rootView - root interface
     */
    private void markSolution(View rootView) {
        Button solution = rootView.findViewById(madGrid.getKey().get(madGrid.getTurnIndex()).getId());
        solution.setText(String.format(Locale.ENGLISH, "%d/%d", madGrid.getTurnIndex() + 1, madGrid.getLevel()));
    }

    /**
     * Helper method for clearing the text on the previous solution box
     * Precondition(s): none
     * Postcondition(s): solution box inner text is omitted
     * @param rootView - root interface
     */
    private void clearSolution(View rootView) {
        Button previousSolution = rootView.findViewById(madGrid.getKey().get(madGrid.getTurnIndex()).getId());
        previousSolution.setText("");
    }

    /**
     * Helper method for highlighting 'watch' & 'replicate' messages during the tutorial session
     * Precondition(s): - <code>delay</code> & <code>delayInitial</code> are positive integers
     * Postcondition(s): - 'watch' message is highlighted during sequence display process
     *                   - 'replicate' message is highlighted during user's turn
     * @param rootView - root interface
     * @param delay - total duration of sequence display for current level in milliseconds
     * @param delayInitial - initial delay before the first switch in milliseconds
     */
    private void switchDescriptionColors(View rootView, int delay, int delayInitial) {
        // initialization
        TextView watchDescription = (TextView) rootView.findViewById(R.id.guide_fragment_reverse_text_button_inactive_description);
        TextView replicateDescription = (TextView) rootView.findViewById(R.id.guide_fragment_reverse_text_button_active_description);

        // initial colors ('watch' must be displayed while sequence is being displayed)
        handler.postDelayed(() -> {
            watchDescription.setTextColor(getResources().getColor(R.color.black));
            replicateDescription.setTextColor(Color.parseColor("#C0C0C0"));
        }, delayInitial);

        // final colors ('replicate' mut be displayed while the user is playing
        handler.postDelayed(() -> {
            watchDescription.setTextColor(Color.parseColor("#C0C0C0"));
            replicateDescription.setTextColor(getResources().getColor(R.color.black));
        }, delay);
    }

    /**
     * Adjusts grid dimensions dynamically according to device dimensions
     * Precondition(s): none
     * Postcondition(s): Grid width & height are set to 95% of the device's width/height (whichever is smaller)
     */
    private void adjustGridDimensions(View rootView) {
        // initialization
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // smaller dimension (width/height) should be used, in case of a tablet
        int madGrid_dimension = (int) ((Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels)) * 0.95);

        // set adjusted dimension size to gridLayout
        View view = rootView.findViewById(R.id.guide_grid_layout);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = madGrid_dimension;
        layoutParams.height = madGrid_dimension;
        view.setLayoutParams(layoutParams);
    }
}