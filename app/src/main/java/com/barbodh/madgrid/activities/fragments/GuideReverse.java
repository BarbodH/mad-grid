package com.barbodh.madgrid.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.barbodh.madgrid.enums.GameMode;

public class GuideReverse extends GuideFragment {

    ////////// Constructor(s) //////////

    public GuideReverse() {
        super(GameMode.REVERSE);
    }

    ////////// Initializer //////////

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflateFragment(inflater, container);
    }
}