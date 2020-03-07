package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.about.future.spacex.databinding.ActivityCapsuleDetailsBinding;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.viewmodel.CapsulesViewModel;
import com.about.future.spacex.viewmodel.LandingPadsViewModel;

import static com.about.future.spacex.utils.Constants.CAPSULE_PAGE_NUMBER_KEY;
import static com.about.future.spacex.utils.Constants.CAPSULE_SERIAL_KEY;

public class CapsuleDetailsActivity extends AppCompatActivity {
    private ActivityCapsuleDetailsBinding binding;

    private int mPageNumber = -1;
    private String mCapsuleSerial = "C101";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCapsuleDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(CAPSULE_SERIAL_KEY)) {
                mCapsuleSerial = intent.getStringExtra(CAPSULE_SERIAL_KEY);
            }
        }

        setupViewPager();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CAPSULE_PAGE_NUMBER_KEY, binding.capsulesPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPageNumber = savedInstanceState.getInt(CAPSULE_PAGE_NUMBER_KEY);
        setupViewPager();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        CapsulesViewModel viewModel = ViewModelProviders.of(this).get(CapsulesViewModel.class);
        viewModel.getCapsulesFromDb().observe(this, capsules -> {
            if (capsules != null && capsules.size() > 0) {
                pagerAdapter.setCapsules(capsules);
                binding.capsulesPager.setAdapter(pagerAdapter);

                if (mPageNumber > -1) {
                    binding.capsulesPager.setCurrentItem(mPageNumber, false);
                } else {
                    for (int i = 0; i < capsules.size(); i++) {
                        if (mCapsuleSerial.equals(capsules.get(i).getCapsuleSerial())) {
                            binding.capsulesPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
