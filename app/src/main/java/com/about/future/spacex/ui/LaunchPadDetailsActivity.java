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
import static com.about.future.spacex.utils.Constants.LAUNCH_PAD_PAGE_NUMBER_KEY;

public class LaunchPadDetailsActivity extends AppCompatActivity {
    @BindView(R.id.launch_pads_pager)
    ViewPager2 mPager;

    private int mPageNumber = -1;
    private String mLaunchPadId = "ksc_lc_39a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_pad_details);
        ButterKnife.bind(this);

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
        outState.putInt(LAUNCH_PAD_PAGE_NUMBER_KEY, mPager.getCurrentItem());
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
        LaunchPadsViewModel viewModel = ViewModelProviders.of(this).get(LaunchPadsViewModel.class);
        viewModel.getLaunchPadsFromDb().observe(this, launchPads -> {
            if (launchPads != null && launchPads.size() > 0) {
                pagerAdapter.setLaunchPads(launchPads);
                mPager.setAdapter(pagerAdapter);

                if (mPageNumber > -1) {
                    mPager.setCurrentItem(mPageNumber, false);
                } else {
                    for (int i = 0; i < launchPads.size(); i++) {
                        if (mLaunchPadId.equals(launchPads.get(i).getSiteId())) {
                            mPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
