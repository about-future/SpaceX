package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.about.future.spacex.R;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.ui.fragments.LaunchPadsFragment.LAUNCH_PAD_ID_KEY;

public class LaunchPadDetailsActivity extends AppCompatActivity {
    private int mLaunchPadId = 1;

    @BindView(R.id.launch_pad_pager)
    ViewPager2 mPager;

    private MyPagerAdapter mPagerAdapter;

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

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mLaunchPadId - 1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(LAUNCH_PAD_ID_KEY, mLaunchPadId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLaunchPadId = savedInstanceState.getInt(LAUNCH_PAD_ID_KEY);
        mPager.setCurrentItem(mLaunchPadId - 1);
        mPagerAdapter.notifyDataSetChanged();

        super.onRestoreInstanceState(savedInstanceState);
    }
}
