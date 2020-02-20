package com.about.future.spacex.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.about.future.spacex.ui.fragments.LaunchPadsFragment;
import com.about.future.spacex.ui.fragments.MissionsFragment;
import com.about.future.spacex.ui.fragments.RocketsFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MissionsFragment();
            case 1:
                return new RocketsFragment();
            default:
                return new LaunchPadsFragment();
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}
