package com.barbodh.madgrid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.barbodh.madgrid.activities.fragments.GuideClassic;
import com.barbodh.madgrid.activities.fragments.GuideMessy;
import com.barbodh.madgrid.activities.fragments.GuideReverse;
import com.barbodh.madgrid.R;
import com.barbodh.madgrid.tools.VPAdapter;
import com.google.android.material.tabs.TabLayout;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        TabLayout tabLayout = findViewById(R.id.guide_tab_layout_navbar);
        ViewPager viewpager = findViewById(R.id.guide_viewpager);

        tabLayout.setupWithViewPager(viewpager);
        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new GuideClassic(), "Classic");
        vpAdapter.addFragment(new GuideReverse(), "Reverse");
        vpAdapter.addFragment(new GuideMessy(), "Messy");
        viewpager.setAdapter(vpAdapter);
    }

    /**
     * Returns to the homepage
     * Precondition(s): none
     * Postcondition(s): MainActivity is started
     * @param view - user interface
     */
    public void returnHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}