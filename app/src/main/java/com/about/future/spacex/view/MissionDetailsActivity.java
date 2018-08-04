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

import static com.about.future.spacex.view.MissionsFragment.MISSION_NUMBER_KEY;

public class MissionDetailsActivity extends AppCompatActivity {

    private int mMissionNumber = 1;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_details);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(MISSION_NUMBER_KEY)) {
                mMissionNumber = intent.getIntExtra(MISSION_NUMBER_KEY, 1);
            }

        }

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.mission_pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mMissionNumber - 1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MISSION_NUMBER_KEY, mMissionNumber);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mMissionNumber = savedInstanceState.getInt(MISSION_NUMBER_KEY);
        mPager.setCurrentItem(mMissionNumber - 1);
        mPagerAdapter.notifyDataSetChanged();

        super.onRestoreInstanceState(savedInstanceState);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MissionDetailsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return SpaceXPreferences.getTotalNumberOfMissions(getApplicationContext());
        }
    }
}
