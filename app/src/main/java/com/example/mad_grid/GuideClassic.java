package com.example.mad_grid;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.VideoView;

import java.util.Objects;

public class GuideClassic extends Fragment {
    private final Handler handler = new Handler(); // used for delaying executions

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide_classic, container, false);

        VideoView videoView = (VideoView) rootView.findViewById(R.id.guide_fragment_classic_video);
        String path = "android.resource://" + Objects.requireNonNull(getActivity()).getPackageName() + "/" + R.raw.guide_video_classic;
        videoView.setVideoURI(Uri.parse(path));

        FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.guide_fragment_classic_frame_layout);

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

        VideoView videoView = (VideoView) Objects.requireNonNull(getView()).findViewById(R.id.guide_fragment_classic_video);
        String path = "android.resource://" + Objects.requireNonNull(getActivity()).getPackageName() + "/" + R.raw.guide_video_classic;
        videoView.setVideoURI(Uri.parse(path));
        videoView.pause();
        videoView.setZOrderOnTop(false);
    }
}