package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.about.future.spacex.R;
import com.about.future.spacex.ui.adapters.MyPagerAdapter;
import com.about.future.spacex.viewmodel.RocketsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

//import static com.about.future.spacex.utils.Constants.PAGER_ID_KEY;
import static com.about.future.spacex.utils.Constants.ROCKET_ID_KEY;
import static com.about.future.spacex.utils.Constants.ROCKET_PAGE_NUMBER_KEY;

public class RocketDetailsActivity extends AppCompatActivity {
    @BindView(R.id.rocket_pager)
    ViewPager2 mPager;

    private int mPageNumber = -1; //1
    private String mRocketId = "falcon9";
    //private int mPageSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket_details);
        ButterKnife.bind(this);

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
        outState.putInt(ROCKET_PAGE_NUMBER_KEY, mPager.getCurrentItem());
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
                mPager.setAdapter(pagerAdapter);

                if (mPageNumber > -1) {
                    mPager.setCurrentItem(mPageNumber, false);
                } else {
                    for (int i = 0; i < rockets.size(); i++) {
                        if (mRocketId.equals(rockets.get(i).getRocketId())) {
                            mPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            }
        });
    }
}
