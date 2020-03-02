package com.about.future.spacex.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.about.future.spacex.R;
import com.about.future.spacex.SettingsActivity;
import com.about.future.spacex.ui.fragments.LaunchPadsFragment;
import com.about.future.spacex.ui.fragments.MissionsFragment;
import com.about.future.spacex.ui.fragments.RocketsFragment;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.ui.adapters.SectionsPagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpaceXActivity extends AppCompatActivity {
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.main_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.main_pager)
    ViewPager2 mViewPager;

    @BindView(R.id.main_collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolberLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_space_x);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        setTitle("");

        // Create the adapter that will return a fragment for each sections of the activity
        setupTabLayoutAndPager();

        // If the app runs for the first time, set subscriptions to updates and notifications topics
        if (!SpaceXPreferences.getTopicSubscriptionStatus(this)) {
            // Subscribe to topics
            //FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.updates_key));
            //FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.notifications_key));
            SpaceXPreferences.setTopicSubscriptionStatus(this, true);
        }

        //mCollapsingToolberLayout
//        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                float percentage = ((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
//                Log.v("Percetage", "is: " + percentage);
//
//                if (percentage >= 0.655) {
//                    mTabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//                } else {
//                    mTabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundGrey));
//                }
//                //fadedView.setAlpha(percentage);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Opens the following activity when the menu icon is pressed
            Intent startFollowingActivity = new Intent(this, SettingsActivity.class);
            startActivity(startFollowingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Older devices that run on Android API 19 to 22, have the default SSL3 protocol activated
    // for networking. Because SSL protocol is proven to be very vulnerable, we use this method
    // to upgrades the security provider and helps the device make use of newer protocols (like TLS)
    // when connecting to a server.
    /*private void upgradeSecurityProvider() {
        try {
            ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
                @Override
                public void onProviderInstalled() {
                    Log.e("SpaceXActivity", "New security provider installed.");
                }

                @Override
                public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
                    Log.e("SpaceXActivity", "New security provider install failed.");
                }
            });
        } catch (Exception e) {
            Log.e("SpaceXActivity", "Unknown issue trying to install a new security provider", e);
        }
    }*/

    private void setupTabLayoutAndPager2() {
        /*FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            // Create an adapter that knows which fragment should be shown on each page
            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle());

            // Set fragment into PagerAdapter
            List<Fragment> fragments = new ArrayList<>();
            fragments.add(new MissionsFragment());
            fragments.add(new RocketsFragment());
            fragments.add(new LaunchPadsFragment());
            pagerAdapter.setFragments(fragments);

            mViewPager.invalidate();

            // Set the adapter onto the view pager
            mViewPager.setAdapter(pagerAdapter);
        }

        // Set Listeners for PagerAdapter and TabLayout
        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    //tab.setIcon(R.drawable.ic_traffic);
                    tab.setText(getString(R.string.label_tab_missions));
                    break;
                case 1:
                    //tab.setIcon(R.drawable.ic_history);
                    tab.setText(getString(R.string.label_tab_rockets));
                    break;
                default:
                    //tab.setIcon(R.drawable.ic_history);
                    tab.setText(getString(R.string.label_tab_launch_pads));
            }
        }).attach();

        // Starting with the second tab, set each tab icon color as faded
        for (int i = 1; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null && tab.getIcon() != null) tab.getIcon().setAlpha(100);
        }

        // Set listener for setting the correct search hint
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getIcon() != null) tab.getIcon().setAlpha(255);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getIcon() != null) tab.getIcon().setAlpha(100);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });*/
    }



    private void setupTabLayoutAndPager() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            // Create an adapter that knows which fragment should be shown on each page
            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle());

            // Set fragment into PagerAdapter
            List<Fragment> fragments = new ArrayList<>();
            fragments.add(new MissionsFragment());
            fragments.add(new RocketsFragment());
            pagerAdapter.setFragments(fragments);

            mViewPager.invalidate();

            // Set the adapter onto the view pager
            mViewPager.setAdapter(pagerAdapter);
        }

        // Set Listeners for PagerAdapter and TabLayout
        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    //tab.setIcon(R.drawable.ic_traffic);
                    tab.setText("Upcoming");
                    break;
                case 1:
                    //tab.setIcon(R.drawable.ic_history);
                    tab.setText("Past");
                    break;
//                default:
//                    //tab.setIcon(R.drawable.ic_history);
//                    tab.setText(getString(R.string.label_tab_launch_pads));
            }
        }).attach();

        // Starting with the second tab, set each tab icon color as faded
        for (int i = 1; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null && tab.getIcon() != null) tab.getIcon().setAlpha(100);
        }

        // Set listener for setting the correct search hint
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getIcon() != null) tab.getIcon().setAlpha(255);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getIcon() != null) tab.getIcon().setAlpha(100);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }





    @Override
    public void onBackPressed() {
        ViewPager2 viewPager = findViewById(R.id.main_pager);
        if (viewPager != null) {
            switch (viewPager.getCurrentItem()) {
                case 2:
                    viewPager.setCurrentItem(1, true);
                    break;
                case 1:
                    viewPager.setCurrentItem(0, true);
                    break;
                default:
                    super.onBackPressed();
                    break;
            }
        } else {
            super.onBackPressed();
        }
    }
}
