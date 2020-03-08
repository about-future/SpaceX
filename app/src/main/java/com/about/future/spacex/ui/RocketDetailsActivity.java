package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.about.future.spacex.databinding.ActivityMissionDetailsBinding;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.RocketsViewModel;

import static com.about.future.spacex.utils.Constants.ROCKET_ID_KEY;
import static com.about.future.spacex.utils.Constants.ROCKET_PAGE_NUMBER_KEY;

public class RocketDetailsActivity extends AppCompatActivity {
    private int mPageNumber = -1;
    private String mRocketId = "falcon9";
    private ActivityMissionDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.makeStatusBarTransparent(getWindow());
        binding = ActivityMissionDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(ROCKET_ID_KEY)) {
                mRocketId = intent.getStringExtra(ROCKET_ID_KEY);
            }
        }

        setupViewPager();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ROCKET_PAGE_NUMBER_KEY, binding.viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPageNumber = savedInstanceState.getInt(ROCKET_PAGE_NUMBER_KEY);
        setupViewPager();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        RocketsViewModel viewModel = ViewModelProviders.of(this).get(RocketsViewModel.class);
        viewModel.getRocketsFromDb().observe(this, rockets -> {
            if (rockets != null && rockets.size() > 0) {
                pagerAdapter.setRockets(rockets);
                binding.viewPager.setAdapter(pagerAdapter);

                if (mPageNumber > -1) {
                    binding.viewPager.setCurrentItem(mPageNumber, false);
                } else {
                    for (int i = 0; i < rockets.size(); i++) {
                        if (mRocketId.equals(rockets.get(i).getRocketId())) {
                            binding.viewPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
