package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.about.future.spacex.databinding.ActivityMissionDetailsBinding;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.MissionsViewModel;

import static com.about.future.spacex.utils.Constants.MISSION_NUMBER_KEY;
import static com.about.future.spacex.utils.Constants.PAGER_POSITION_KEY;

public class MissionDetailsActivity extends AppCompatActivity {
    private int mMissionNumber = 1;
    private int mPagerPosition = -1;
    private ActivityMissionDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.makeStatusBarTransparent(getWindow());
        binding = ActivityMissionDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        outState.putInt(PAGER_POSITION_KEY, binding.viewPager.getCurrentItem());
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
                binding.viewPager.setAdapter(pagerAdapter);

                if (mPagerPosition > -1) {
                    binding.viewPager.setCurrentItem(mPagerPosition, false);
                } else {
                    for (int i = 0; i < missions.size(); i++) {
                        if (missions.get(i).getFlightNumber() == mMissionNumber) {
                            binding.viewPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
