package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.about.future.spacex.databinding.ActivityCoreDetailsBinding;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.viewmodel.CoresViewModel;

import static com.about.future.spacex.utils.Constants.CORE_PAGE_NUMBER_KEY;
import static com.about.future.spacex.utils.Constants.CORE_SERIAL_KEY;

public class CoreDetailsActivity extends AppCompatActivity {
    private String mCoreSerial = "Merlin1A";
    private int mPagerPosition = -1;
    private ActivityCoreDetailsBinding binding;

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
        outState.putString(CORE_SERIAL_KEY, mCoreSerial);
        outState.putInt(CORE_PAGE_NUMBER_KEY, binding.viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPagerPosition = savedInstanceState.getInt(CORE_PAGE_NUMBER_KEY);
        mCoreSerial = savedInstanceState.getString(CORE_SERIAL_KEY);
        setupViewPager();

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupViewPager() {
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        CoresViewModel viewModel = ViewModelProviders.of(this).get(CoresViewModel.class);
        viewModel.getCoresFromDb().observe(this, cores -> {
            if (cores != null && cores.size() > 0) {
                pagerAdapter.setCores(cores);
                binding.viewPager.setAdapter(pagerAdapter);

                if (mPagerPosition > -1) {
                    binding.viewPager.setCurrentItem(mPagerPosition, false);
                } else {
                    for (int i = 0; i < cores.size(); i++) {
                        if (mCoreSerial.equals(cores.get(i).getCoreSerial())) {
                            binding.viewPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
