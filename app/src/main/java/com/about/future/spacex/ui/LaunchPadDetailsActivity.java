package com.about.future.spacex.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.about.future.spacex.R;
import com.about.future.spacex.ui.fragments.LaunchPadDetailsFragment;

import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.LAUNCH_PAD_ID_KEY;

public class LaunchPadDetailsActivity extends AppCompatActivity {
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

        LaunchPadDetailsFragment fragment = new LaunchPadDetailsFragment();
        fragment.setLaunchPadId(mLaunchPadId);

        getSupportFragmentManager().beginTransaction()
                        .replace(R.id.launch_pad_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
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
