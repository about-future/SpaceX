package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.about.future.spacex.databinding.ActivityMissionDetailsBinding;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.LaunchPadsViewModel;

import static com.about.future.spacex.utils.Constants.LAUNCH_PAD_ID_KEY;
import static com.about.future.spacex.utils.Constants.LAUNCH_PAD_PAGE_NUMBER_KEY;

public class LaunchPadDetailsActivity extends AppCompatActivity {
    private int mPageNumber = -1;
    private String mLaunchPadId = "ksc_lc_39a";
    private ActivityMissionDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.makeStatusBarTransparent(getWindow());
        binding = ActivityMissionDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(LAUNCH_PAD_ID_KEY)) {
                mLaunchPadId = intent.getStringExtra(LAUNCH_PAD_ID_KEY);
            }
        }

        setupViewPager();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(LAUNCH_PAD_PAGE_NUMBER_KEY, binding.viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPageNumber = savedInstanceState.getInt(LAUNCH_PAD_PAGE_NUMBER_KEY);
        setupViewPager();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        LaunchPadsViewModel viewModel = new ViewModelProvider(this).get(LaunchPadsViewModel.class);
        viewModel.getLaunchPadsFromDb().observe(this, launchPads -> {
            if (launchPads != null && launchPads.size() > 0) {
                pagerAdapter.setLaunchPads(launchPads);
                binding.viewPager.setAdapter(pagerAdapter);

                if (mPageNumber > -1) {
                    binding.viewPager.setCurrentItem(mPageNumber, false);
                } else {
                    for (int i = 0; i < launchPads.size(); i++) {
                        if (mLaunchPadId.equals(launchPads.get(i).getId())) {
                            binding.viewPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
