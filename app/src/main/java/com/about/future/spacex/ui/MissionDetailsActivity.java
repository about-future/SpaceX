package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.about.future.spacex.R;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.ui.fragments.MissionsFragment.MISSION_NUMBER_KEY;

public class MissionDetailsActivity extends AppCompatActivity {
    @BindView(R.id.mission_pager)
    ViewPager2 mPager;

    private MyPagerAdapter mPagerAdapter;
    private int mMissionNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_details);
        ButterKnife.bind(this);

        //return SpaceXPreferences.getTotalNumberOfMissions(getApplicationContext());

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(MISSION_NUMBER_KEY)) {
                mMissionNumber = intent.getIntExtra(MISSION_NUMBER_KEY, 1);
            }

        }

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        mPager = findViewById(R.id.mission_pager);

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
}
