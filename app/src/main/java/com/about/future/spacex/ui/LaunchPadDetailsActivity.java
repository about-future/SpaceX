package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.about.future.spacex.R;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.viewmodel.LaunchPadsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.LAUNCH_PAD_ID_KEY;

public class LaunchPadDetailsActivity extends AppCompatActivity {
    @BindView(R.id.launch_pads_pager)
    ViewPager2 mPager;

    private int mLaunchPadId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_pad_details);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(LAUNCH_PAD_ID_KEY)) {
                mLaunchPadId = intent.getIntExtra(LAUNCH_PAD_ID_KEY, 1);
            }
        }

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        LaunchPadsViewModel viewModel = ViewModelProviders.of(this).get(LaunchPadsViewModel.class);
        viewModel.getLaunchPadsFromDb().observe(this, launchPads -> {
            if (launchPads != null && launchPads.size() > 0) {
                pagerAdapter.setLaunchPads(launchPads);
                mPager.setAdapter(pagerAdapter);
                mPager.setCurrentItem(mLaunchPadId);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(LAUNCH_PAD_ID_KEY, mLaunchPadId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLaunchPadId = savedInstanceState.getInt(LAUNCH_PAD_ID_KEY);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
