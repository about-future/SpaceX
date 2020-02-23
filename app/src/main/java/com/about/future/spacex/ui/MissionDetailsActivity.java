package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.about.future.spacex.R;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.viewmodel.MissionsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.MISSION_NUMBER_KEY;
import static com.about.future.spacex.utils.Constants.PAGER_POSITION_KEY;

public class MissionDetailsActivity extends AppCompatActivity {
    @BindView(R.id.mission_pager)
    ViewPager2 mPager;

    private int mMissionNumber = 1;
    private int mPagerPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_details);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(MISSION_NUMBER_KEY)) {
                mMissionNumber = intent.getIntExtra(MISSION_NUMBER_KEY, 1);
            }

        }

        setupViewPager();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MISSION_NUMBER_KEY, mMissionNumber);
        outState.putInt(PAGER_POSITION_KEY, mPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mMissionNumber = savedInstanceState.getInt(MISSION_NUMBER_KEY);
        mPagerPosition = savedInstanceState.getInt(PAGER_POSITION_KEY);
        setupViewPager();

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        MissionsViewModel viewModel = ViewModelProviders.of(this).get(MissionsViewModel.class);
        viewModel.getMissionsFromDb().observe(this, missions -> {
            if (missions != null && missions.size() > 0) {
                pagerAdapter.setMissions(missions);
                mPager.setAdapter(pagerAdapter);

                if (mPagerPosition > -1) {
                    mPager.setCurrentItem(mPagerPosition, false);
                } else {
                    for (int i = 0; i < missions.size(); i++) {
                        if (missions.get(i).getFlightNumber() == mMissionNumber) {
                            mPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
