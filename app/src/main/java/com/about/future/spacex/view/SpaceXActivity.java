package com.about.future.spacex.view;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.about.future.spacex.R;
import com.about.future.spacex.SettingsActivity;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpaceXActivity extends AppCompatActivity {

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_tabs)
    TabLayout tabLayout;
    @BindView(R.id.main_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_x);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        setTitle("");

        // Some devices have
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 22) {
            upgradeSecurityProvider();
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // If the app runs for the first time, set subscriptions to updates and notifications topics
        if (!SpaceXPreferences.getTopicSubscriptionStatus(this)) {
            // Subscribe to topics
            FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.updates_key));
            FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.notifications_key));
            SpaceXPreferences.setTopicSubscriptionStatus(this);
        }
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

    // TODO: create links from mission to rocket and launchpad

    private void upgradeSecurityProvider() {
        try{
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
        }catch (Exception ex){
            Log.e("SpaceXActivity", "Unknown issue trying to install a new security provider", ex);
        }
    }
}
