package com.example.mad_grid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class GuideActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        tabLayout = findViewById(R.id.guide_tab_layout_navbar);
        viewpager = findViewById(R.id.guide_viewpager);

        tabLayout.setupWithViewPager(viewpager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new GuideClassic(), "Classic");
        vpAdapter.addFragment(new GuideReverse(), "Reverse");
        vpAdapter.addFragment(new GuideCrazy(), "Crazy");
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