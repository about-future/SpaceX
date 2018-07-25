package com.about.future.spacex.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.about.future.spacex.R;

import static com.about.future.spacex.view.RocketsFragment.ROCKET_ID_KEY;
import static com.about.future.spacex.view.RocketsFragment.TOTAL_ROCKETS_KEY;

public class RocketDetailsActivity extends AppCompatActivity {
    private static final int DEFAULT_ROCKET_ID = 1;
    private int mRocketId = DEFAULT_ROCKET_ID;
    private int mTotalRockets = DEFAULT_ROCKET_ID;

    private ViewPager mPager;
    private RocketDetailsActivity.MyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket_details);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(TOTAL_ROCKETS_KEY))
                    mTotalRockets = intent.getIntExtra(TOTAL_ROCKETS_KEY, 1);

                if (intent.hasExtra(ROCKET_ID_KEY)) {
                    mRocketId = intent.getIntExtra(ROCKET_ID_KEY, DEFAULT_ROCKET_ID);
                }
            }
        }

        mPagerAdapter = new RocketDetailsActivity.MyPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.rocket_pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mRocketId - 1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(TOTAL_ROCKETS_KEY, mTotalRockets);
        outState.putInt(ROCKET_ID_KEY, mRocketId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRocketId = savedInstanceState.getInt(ROCKET_ID_KEY);
        mTotalRockets = savedInstanceState.getInt(TOTAL_ROCKETS_KEY);
        mPager.setCurrentItem(mRocketId - 1);
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
            return RocketDetailsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return mTotalRockets;
        }
    }
}
