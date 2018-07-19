package com.about.future.spacex.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.MissionsAdapter;
import com.about.future.spacex.data.MissionsLoader;
import com.about.future.spacex.utils.SpaceXPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MissionsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Mission>>, MissionsAdapter.ListItemClickListener {

    private static final int MISSIONS_LOADER_ID = 892;
    public static final String MISSION_NUMBER_KEY = "mission_number";
    public static final String TOTAL_MISSIONS_KEY = "total_missions";

    private AppDatabase mDb;
    private MissionsAdapter mMissionsAdapter;
    private int mTotalMissions;

    @BindView(R.id.swipe_refresh_missions_list_layout)
    SwipeRefreshLayout mSwipeRefreshMissionListLayout;
    @BindView(R.id.missions_rv)
    RecyclerView mMissionsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missions_list, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mMissionsRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                mMissionsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mMissionsRecyclerView.addItemDecoration(mDividerItemDecoration);

//        int columnCount = getResources().getInteger(R.integer.rocket_list_column_count);
//        StaggeredGridLayoutManager sglm =
//                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
//        mMissionsRecyclerView.setLayoutManager(sglm);

        mMissionsRecyclerView.setHasFixedSize(false);
        mMissionsAdapter = new MissionsAdapter(getContext(), this);
        mMissionsRecyclerView.setAdapter(mMissionsAdapter);

        //TODO background for each clicked item and a better divider

        mDb = AppDatabase.getInstance(getContext());

        // If missions were already loaded once, just query the DB and display them, otherwise init the loader
        if (SpaceXPreferences.getLoadingStatus(getActivityCast())) {
            // Load data
            setupViewModel();
        } else {
            // Get data
            getData();
        }

        mSwipeRefreshMissionListLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshMissionListLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        return view;
    }

    private void setupViewModel() {
        MissionsViewModel missionsViewModel = ViewModelProviders.of(this).get(MissionsViewModel.class);
        missionsViewModel.getMissions().observe(this, new Observer<List<Mission>>() {
            @Override
            public void onChanged(@Nullable List<Mission> missions) {
                if (missions != null) {
                    mMissionsAdapter.setMissions(missions);
                    mTotalMissions = missions.size();
                }
            }
        });
    }

    private void getData() {
        //Init mission loader
        getLoaderManager().restartLoader(MISSIONS_LOADER_ID, null, this);
    }

    public SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    @Override
    public void onItemClickListener(int missionNumber) {
        Intent missionDetailsIntent = new Intent(getActivity(), MissionDetailsActivity.class);
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
                return new MissionsLoader(getActivityCast());
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
                        SpaceXPreferences.setLoadingStatus(getContext(), true);
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
        mMissionsAdapter.setMissions(null);
    }
}
