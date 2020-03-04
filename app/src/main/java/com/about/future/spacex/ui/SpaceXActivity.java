package com.about.future.spacex.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.about.future.spacex.R;
import com.about.future.spacex.ui.fragments.LaunchPadsFragment;
import com.about.future.spacex.ui.fragments.MissionsFragment;
import com.about.future.spacex.ui.fragments.PastMissionsFragment;
import com.about.future.spacex.ui.fragments.RocketsFragment;
import com.about.future.spacex.ui.fragments.UpcomingMissionsFragment;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.ui.adapters.SectionsPagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.NAV_ID_KEY;

public class SpaceXActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.main_collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolberLayout;
    @BindView(R.id.main_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.main_pager)
    ViewPager2 mViewPager;

    private int mNavId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.makeStatusBarTransparent(getWindow());
        setContentView(R.layout.activity_space_x3);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        setTitle("");

        // Create the adapter that will return a fragment for each sections of the activity
        setupLaunchesPager();

        // If the app runs for the first time, set subscriptions to updates and notifications topics
        if (!SpaceXPreferences.getTopicSubscriptionStatus(this)) {
            // Subscribe to topics
            //FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.updates_key));
            //FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.notifications_key));
            SpaceXPreferences.setTopicSubscriptionStatus(this, true);
        }

        setupNavigation();

        if (savedInstanceState != null && savedInstanceState.containsKey(NAV_ID_KEY)) {
            mNavId = savedInstanceState.getInt(NAV_ID_KEY, 0);
            recreateTabsAndPager();
        } else {
            setupLaunchesPager();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ID_KEY, mNavId);
    }

    /*@Override
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
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_launches:
                if (mNavId != 0) {
                    mNavId = 0;
                    setupLaunchesPager();
                }
                break;
            case R.id.nav_rockets:
                if (mNavId != 1) {
                    mNavId = 1;
                    setupRocketsPager();
                }
                break;
            case R.id.nav_pads:
                if (mNavId != 2) {
                    mNavId = 2;
                    //setupPadssPager();
                }
                break;
            case R.id.nav_company:
                if (mNavId != 3) {
                    mNavId = 3;
                    //setupHistory();
                }
                break;
            case R.id.nav_media:
                if (mNavId != 4) {
                    mNavId = 4;
                    //setupWhatIsHotPager();
                }
                break;
            case R.id.nav_notifications:
                if (mNavId != 5) {
                    mNavId = 5;
                    //setupNotifications();
                }
                break;
            default: //case R.id.nav_settings:
                startActivity(new Intent(SpaceXActivity.this, SettingsActivity.class));
                mNavId = 6;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupRocketsPager() {
        FragmentManager fragmentManager = getFragmentManager();
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
        });
    }

    private void setupLaunchesPager() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            // Create an adapter that knows which fragment should be shown on each page
            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle());

            // Set fragment into PagerAdapter
            List<Fragment> fragments = new ArrayList<>();
            fragments.add(new UpcomingMissionsFragment());
            fragments.add(new PastMissionsFragment());
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

    private void recreateTabsAndPager() {
        switch (mNavId) {
            /*case 0:
                setupLaunchesPager();
                break;*/
            case 1:
                setupRocketsPager();
                break;
            case 2:
                //setupPadsPager();
                break;
            case 3:
                //setupHistory();
                break;
            case 4:
                //setupWhatIsHotPager();
                break;
            case 5:
                //setupNotifications();
                break;
            default:
                setupLaunchesPager();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

    private void setupNavigation() {
        setSupportActionBar(mToolbar);
        mToolbar.setOnClickListener(v -> mAppBarLayout.setExpanded(true));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }
}
