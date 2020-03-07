package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.about.future.spacex.databinding.ActivityCoreDetailsBinding;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.viewmodel.CoresViewModel;
import com.about.future.spacex.viewmodel.LandingPadsViewModel;

import static com.about.future.spacex.utils.Constants.CORE_PAGE_NUMBER_KEY;
import static com.about.future.spacex.utils.Constants.CORE_SERIAL_KEY;
import static com.about.future.spacex.utils.Constants.LANDING_PAD_PAGE_NUMBER_KEY;

public class CoreDetailsActivity extends AppCompatActivity {
    private ActivityCoreDetailsBinding binding;

    private int mPageNumber = -1;
    private String mCoreSerial = "Merlin1A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoreDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(CORE_SERIAL_KEY)) {
                mCoreSerial = intent.getStringExtra(CORE_SERIAL_KEY);
            }
        }

        setupViewPager();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CORE_PAGE_NUMBER_KEY, binding.coresPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPageNumber = savedInstanceState.getInt(CORE_PAGE_NUMBER_KEY);
        setupViewPager();
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        CoresViewModel viewModel = ViewModelProviders.of(this).get(CoresViewModel.class);
        viewModel.getCoresFromDb().observe(this, cores -> {
            if (cores != null && cores.size() > 0) {
                pagerAdapter.setCores(cores);
                binding.coresPager.setAdapter(pagerAdapter);

                if (mPageNumber > -1) {
                    binding.coresPager.setCurrentItem(mPageNumber, false);
                } else {
                    for (int i = 0; i < cores.size(); i++) {
                        if (mCoreSerial.equals(cores.get(i).getCoreSerial())) {
                            binding.coresPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
