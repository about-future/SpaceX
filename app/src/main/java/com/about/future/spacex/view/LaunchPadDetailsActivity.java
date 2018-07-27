package com.about.future.spacex.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.about.future.spacex.R;

import static com.about.future.spacex.view.LaunchPadsFragment.LAUNCH_PAD_ID_KEY;
import static com.about.future.spacex.view.LaunchPadsFragment.TOTAL_LAUNCH_PADS_KEY;

public class LaunchPadDetailsActivity extends AppCompatActivity {
    private static final int DEFAULT_LAUNCH_PAD_ID = 1;
    private int mLaunchPadId = DEFAULT_LAUNCH_PAD_ID;
    private int mTotalLaunchPads = DEFAULT_LAUNCH_PAD_ID;

    private ViewPager mPager;
    private LaunchPadDetailsActivity.MyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_pad_details);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(TOTAL_LAUNCH_PADS_KEY))
                    mTotalLaunchPads = intent.getIntExtra(TOTAL_LAUNCH_PADS_KEY, 1);

                if (intent.hasExtra(LAUNCH_PAD_ID_KEY)) {
                    mLaunchPadId = intent.getIntExtra(LAUNCH_PAD_ID_KEY, DEFAULT_LAUNCH_PAD_ID);
                }
            }
        }

        mPagerAdapter = new LaunchPadDetailsActivity.MyPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.launch_pad_pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mLaunchPadId - 1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(TOTAL_LAUNCH_PADS_KEY, mTotalLaunchPads);
        outState.putInt(LAUNCH_PAD_ID_KEY, mLaunchPadId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLaunchPadId = savedInstanceState.getInt(LAUNCH_PAD_ID_KEY);
        mTotalLaunchPads = savedInstanceState.getInt(TOTAL_LAUNCH_PADS_KEY);
        mPager.setCurrentItem(mLaunchPadId - 1);
        mPagerAdapter.notifyDataSetChanged();

        super.onRestoreInstanceState(savedInstanceState);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            return LaunchPadDetailsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return mTotalLaunchPads;
        }
    }
}
