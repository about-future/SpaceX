package com.android.future.spacex;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.future.spacex.data.AddMissionViewModel;
import com.android.future.spacex.data.AddMissionViewModelFactory;
import com.android.future.spacex.data.AppDatabase;
import com.android.future.spacex.data.Mission;

public class MissionDetailsActivity extends AppCompatActivity {

    private static final int DEFAULT_MISSION_NUMBER = -1;
    private int mMissionNumber = DEFAULT_MISSION_NUMBER;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_details);

        mDb = AppDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(SpaceXActivity.MISSION_NUMBER_KEY)) {
            //if ()
            mMissionNumber = intent.getIntExtra(SpaceXActivity.MISSION_NUMBER_KEY, DEFAULT_MISSION_NUMBER);
            //final LiveData<Mission> missionLiveData = mDb.missionDao().loadMissionDetails(mMissionNumber);
            AddMissionViewModelFactory factory = new AddMissionViewModelFactory(mDb, mMissionNumber);
            final AddMissionViewModel viewModel = ViewModelProviders.of(this, factory).get(AddMissionViewModel.class);
            viewModel.getMissionLiveData().observe(this, new Observer<Mission>() {
                @Override
                public void onChanged(@Nullable Mission mission) {
                    viewModel.getMissionLiveData().removeObserver(this);
                    //populateUi();
                }
            });
        }
    }
}
