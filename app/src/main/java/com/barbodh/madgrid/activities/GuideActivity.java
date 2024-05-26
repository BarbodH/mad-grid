package com.barbodh.madgrid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.barbodh.madgrid.R;
import com.barbodh.madgrid.activities.fragments.GuideClassic;
import com.barbodh.madgrid.activities.fragments.GuideMessy;
import com.barbodh.madgrid.activities.fragments.GuideReverse;
import com.barbodh.madgrid.tools.VPAdapter;
import com.google.android.material.tabs.TabLayout;

public class GuideActivity extends AppCompatActivity {

    ////////// Initializer //////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        var tabLayout = (TabLayout) findViewById(R.id.guide_tab_layout_navbar);
        var viewpager = (ViewPager) findViewById(R.id.guide_viewpager);
        tabLayout.setupWithViewPager(viewpager);

        var vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new GuideClassic(), "Classic");
        vpAdapter.addFragment(new GuideReverse(), "Reverse");
        vpAdapter.addFragment(new GuideMessy(), "Messy");
        viewpager.setAdapter(vpAdapter);
    }

    ////////// Event Handler(s) //////////

    /**
     * Navigates to {@code MainActivity}.
     *
     * @param view the triggered UI element; back button
     */
    public void returnHome(View view) {
        var intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}