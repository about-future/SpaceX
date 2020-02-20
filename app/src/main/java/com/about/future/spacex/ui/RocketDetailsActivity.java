package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.about.future.spacex.R;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.ui.fragments.RocketsFragment.ROCKET_ID_KEY;

public class RocketDetailsActivity extends AppCompatActivity {
    @BindView(R.id.rocket_pager)
    ViewPager2 mPager;

    private MyPagerAdapter mPagerAdapter;
    private int mRocketId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket_details);
        ButterKnife.bind(this);

        //return SpaceXPreferences.getTotalNumberOfRockets(getApplicationContext());

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(ROCKET_ID_KEY)) {
                mRocketId = intent.getIntExtra(ROCKET_ID_KEY, 1);
            }
        }

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mRocketId - 1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ROCKET_ID_KEY, mRocketId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRocketId = savedInstanceState.getInt(ROCKET_ID_KEY);
        mPager.setCurrentItem(mRocketId - 1);
        mPagerAdapter.notifyDataSetChanged();

        super.onRestoreInstanceState(savedInstanceState);
    }
}
