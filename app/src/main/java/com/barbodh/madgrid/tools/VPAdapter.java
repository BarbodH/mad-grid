package com.barbodh.madgrid.tools;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * A custom adapter for managing fragments within a {@code ViewPager}.
 */
public class VPAdapter extends FragmentPagerAdapter {

    ////////// Field(s) //////////

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitle = new ArrayList<>();

    ////////// Constructor(s) //////////

    /**
     * Constructs a {@code VPAdapter} with specified fragment manager and behavior.
     *
     * @param fm       the fragment manager to use for managing fragments
     * @param behavior determines the behavior of the adapter
     */
    public VPAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    /**
     * Retrieves the fragment at the specified position.
     *
     * @param position the position of the fragment to retrieve
     * @return the fragment at the specified position
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    /**
     * Retrieves the total number of fragments in the adapter.
     *
     * @return the total number of fragments
     */
    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    /**
     * Adds a fragment to the adapter with the specified title.
     *
     * @param fragment the fragment to add
     * @param title    the title associated with the fragment
     */
    public void addFragment(Fragment fragment, String title) {
        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);
    }

    /**
     * Retrieves the title of the fragment at the specified position.
     *
     * @param position the position of the fragment
     * @return the title of the fragment at the specified position
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }
}
