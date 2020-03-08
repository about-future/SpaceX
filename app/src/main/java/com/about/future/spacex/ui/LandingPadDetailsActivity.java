package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.about.future.spacex.databinding.ActivityLandingPadDetailsBinding;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.viewmodel.LandingPadsViewModel;

import static com.about.future.spacex.utils.Constants.LANDING_PAD_ID_KEY;
import static com.about.future.spacex.utils.Constants.LANDING_PAD_PAGE_NUMBER_KEY;

public class LandingPadDetailsActivity extends AppCompatActivity {
    private ActivityLandingPadDetailsBinding binding;
    private int mPageNumber = -1;
    private String mLaunchPadId = "LZ-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ScreenUtils.makeStatusBarTransparent(getWindow());
        binding = ActivityLandingPadDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(LANDING_PAD_ID_KEY)) {
                mLaunchPadId = intent.getStringExtra(LANDING_PAD_ID_KEY);
            }
        }

        setupViewPager();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(LANDING_PAD_PAGE_NUMBER_KEY, binding.landingPadsPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPageNumber = savedInstanceState.getInt(LANDING_PAD_PAGE_NUMBER_KEY);
        setupViewPager();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        LandingPadsViewModel viewModel = ViewModelProviders.of(this).get(LandingPadsViewModel.class);
        viewModel.getLandingPadsFromDb().observe(this, landingPads -> {
            if (landingPads != null && landingPads.size() > 0) {
                pagerAdapter.setLandingPads(landingPads);
                binding.landingPadsPager.setAdapter(pagerAdapter);

                if (mPageNumber > -1) {
                    binding.landingPadsPager.setCurrentItem(mPageNumber, false);
                } else {
                    for (int i = 0; i < landingPads.size(); i++) {
                        if (mLaunchPadId.equals(landingPads.get(i).getId())) {
                            binding.landingPadsPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
