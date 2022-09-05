package com.example.mad_grid;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GuideClassic extends Fragment {
    private final Handler handler = new Handler(); // used for delaying executions
    ProgressBar progressBar;
    int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide_classic, container, false);

        VideoView videoView = (VideoView) rootView.findViewById(R.id.guide_fragment_video_classic);
        String path = "android.resource://" + Objects.requireNonNull(getActivity()).getPackageName() + "/" + R.raw.guide_video_classic;
        videoView.setVideoURI(Uri.parse(path));

        FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.placeholder_classic_guide_video_thumbnail);

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.start();
                handler.postDelayed(() -> videoView.setZOrderOnTop(true), 250);
            }
        });

        return rootView;
    }

    /**
     * onPause() method is called when user exits current fragment
     * Precondition(s): none
     * Postcondition(s): demonstration video and progress bar are both reset
     */
    @Override
    public void onPause() {
        super.onPause();

        VideoView videoView = (VideoView) getView().findViewById(R.id.guide_fragment_video_classic);
        String path = "android.resource://" + Objects.requireNonNull(getActivity()).getPackageName() + "/" + R.raw.guide_video_classic;
        videoView.setVideoURI(Uri.parse(path));
        videoView.pause();
        videoView.setZOrderOnTop(false);
    }

    /**
     * Display progress when demonstration video is started
     * Precondition(s): none
     * Postcondition(s): progress bar fills along with the video and resets at the end
     */
    public void progressBarAnimation() {
        // initialization
        progressBar = (ProgressBar) Objects.requireNonNull(getView()).findViewById(R.id.guide_progressbar_classic);
        final int CLASSIC_GUIDE_VIDEO_LENGTH = 24540; // milliseconds
        final int NUMBER_OF_INTERVALS = 100;

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter++;
                progressBar.setProgress(counter);

                if (counter == NUMBER_OF_INTERVALS) {
                    counter = 0;
                    progressBar.setProgress(counter);
                    timer.cancel();
                }
            }
        };

        timer.schedule(timerTask, 0, (CLASSIC_GUIDE_VIDEO_LENGTH / 100));
     }
}