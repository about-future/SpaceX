package com.android.future.spacex;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.future.spacex.data.AppDatabase;
import com.android.future.spacex.data.AppExecutors;
import com.android.future.spacex.data.MainViewModel;
import com.android.future.spacex.entity.Mission;
import com.android.future.spacex.data.MissionsAdapter;
import com.android.future.spacex.data.MissionsLoader;
import com.android.future.spacex.utils.MissionsPreferences;
import com.android.future.spacex.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpaceXActivity extends AppCompatActivity implements
        MissionsAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Mission>> {

    private static final int MISSIONS_LOADER_ID = 892;
    public static final String MISSION_NUMBER_KEY = "mission_number";
    public static final String TOTAL_MISSIONS_KEY = "total_missions";

    @BindView(R.id.missions_rv)
    RecyclerView mMissionsRecyclerView;

    private MissionsAdapter mMissionsAdapter;
    private AppDatabase mDb;
    private int mTotalMissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_x);

        ButterKnife.bind(this);

//        Typeface brandonBlack = Typeface.createFromAsset(getAssets(), "Brandon_blk.otf");
//        TextView missionNameTextView = findViewById(R.id.mission_name);
//        TextView missionNameTextView2 = findViewById(R.id.mission_name2);
//        TextView missionNameTextView22 = findViewById(R.id.mission_name22);
//        missionNameTextView.setTypeface(brandonBlack);
//        missionNameTextView2.setTypeface(brandonBlack);
//        missionNameTextView22.setTypeface(brandonBlack);

        LinearLayoutManager linearLayoutManager;

        if (ScreenUtils.isPortraitMode(this)) {
            linearLayoutManager = new LinearLayoutManager(this);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    mMissionsRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            mMissionsRecyclerView.addItemDecoration(mDividerItemDecoration);
        } else {
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        }
        mMissionsRecyclerView.setLayoutManager(linearLayoutManager);
        mMissionsRecyclerView.setHasFixedSize(false);
        mMissionsAdapter = new MissionsAdapter(this, this);
        mMissionsRecyclerView.setAdapter(mMissionsAdapter);

        //TODO background for each clicked item and a better divider

        mDb = AppDatabase.getInstance(getApplicationContext());

        // If missions were already loaded once, just query the DB and display them, otherwise init the loader
        if (MissionsPreferences.getLoadingStatus(this)) {
            setupViewModel();
        } else {
            //Init missions loader
            getSupportLoaderManager().restartLoader(MISSIONS_LOADER_ID, null, this);
        }
    }

    private void setupViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getMissions().observe(this, new Observer<List<Mission>>() {
            @Override
            public void onChanged(@Nullable List<Mission> missions) {
                if (missions != null) {
                    mMissionsAdapter.setMissions(missions);
                    mTotalMissions = missions.size();
                }
            }
        });
    }

    @Override
    public void onItemClickListener(int missionNumber) {
        Intent missionDetailsIntent = new Intent(SpaceXActivity.this, MissionDetailsActivity.class);
        missionDetailsIntent.putExtra(MISSION_NUMBER_KEY, missionNumber);
        missionDetailsIntent.putExtra(TOTAL_MISSIONS_KEY, mTotalMissions);
        startActivity(missionDetailsIntent);
    }

    @NonNull
    @Override
    public Loader<List<Mission>> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case MISSIONS_LOADER_ID:
                // If the loaded id matches missions loader, return a new missions loader
                return new MissionsLoader(this);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Mission>> loader, final List<Mission> data) {
        switch (loader.getId()) {
            case MISSIONS_LOADER_ID:
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.missionDao().insertMissions(data);
                        Log.v("INSERT ALL", "DONE!");
                        MissionsPreferences.setLoadingStatus(getApplicationContext(), true);
                    }
                });

                setupViewModel();

                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Mission>> loader) {
        //mMissionsAdapter.swapMissions(null);
    }
}
