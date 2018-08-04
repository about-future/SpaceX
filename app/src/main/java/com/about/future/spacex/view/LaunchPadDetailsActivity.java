package com.about.future.spacex.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.about.future.spacex.R;
import com.about.future.spacex.utils.SpaceXPreferences;

import static com.about.future.spacex.view.LaunchPadsFragment.LAUNCH_PAD_ID_KEY;

public class LaunchPadDetailsActivity extends AppCompatActivity {
    private int mLaunchPadId = 1;

    private ViewPager mPager;
    private LaunchPadDetailsActivity.MyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_pad_details);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(LAUNCH_PAD_ID_KEY)) {
                mLaunchPadId = intent.getIntExtra(LAUNCH_PAD_ID_KEY, 1);
            }
        }

        mPagerAdapter = new LaunchPadDetailsActivity.MyPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.launch_pad_pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mLaunchPadId - 1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(LAUNCH_PAD_ID_KEY, mLaunchPadId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLaunchPadId = savedInstanceState.getInt(LAUNCH_PAD_ID_KEY);
        mPager.setCurrentItem(mLaunchPadId - 1);
        mPagerAdapter.notifyDataSetChanged();

        super.onRestoreInstanceState(savedInstanceState);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return LaunchPadDetailsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return SpaceXPreferences.getTotalNumberOfLaunchPads(getApplicationContext());
        }
    }
}
