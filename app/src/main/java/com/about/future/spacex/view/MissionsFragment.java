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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.model.mission.MissionMini;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.MissionsAdapter;
import com.about.future.spacex.data.MissionsLoader;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.widget.UpdateIntentService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MissionsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Mission>>, MissionsAdapter.ListItemClickListener {

    private static final int MISSIONS_LOADER_ID = 892;
    public static final String MISSION_NUMBER_KEY = "mission_number";

    private AppDatabase mDb;
    private MissionsAdapter mMissionsAdapter;

    @BindView(R.id.swipe_refresh_missions_list_layout)
    SwipeRefreshLayout mSwipeRefreshMissionListLayout;
    @BindView(R.id.missions_rv)
    RecyclerView mMissionsRecyclerView;
    @BindView(R.id.missions_no_connection_cloud)
    ImageView mNoConnectionImageView;
    @BindView(R.id.missions_no_connection_message)
    TextView mNoConnectionMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missions_list, container, false);
        ButterKnife.bind(this, view);

        if (ScreenUtils.isPortraitMode(getActivityCast())) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            mMissionsRecyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    mMissionsRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            mMissionsRecyclerView.addItemDecoration(mDividerItemDecoration);
        } else {
            int columnCount = getResources().getInteger(R.integer.mission_list_column_count);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            mMissionsRecyclerView.setLayoutManager(sglm);
        }

        mMissionsRecyclerView.setHasFixedSize(false);
        mMissionsAdapter = new MissionsAdapter(getContext(), this);
        mMissionsRecyclerView.setAdapter(mMissionsAdapter);

        mDb = AppDatabase.getInstance(getContext());

        // If missions were already loaded once, just query the DB and display them,
        // otherwise init the loader and get data from server
        if (SpaceXPreferences.getMissionsStatus(getActivityCast())) {
            // Load data from DB
            setupViewModel();
        } else {
            // Get data from internet
            getData();
        }

        mSwipeRefreshMissionListLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Get data from internet
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
        missionsViewModel.getMissions().observe(this, new Observer<List<MissionMini>>() {
            @Override
            public void onChanged(@Nullable List<MissionMini> missions) {
                if (missions != null) {
                    mMissionsAdapter.setMissions(missions);
                }
            }
        });
    }

    private void getData() {
        // Get of refresh data, if there is a network connection
        if (NetworkUtils.isConnected(getActivityCast())) {
            mMissionsRecyclerView.setVisibility(View.VISIBLE);
            mNoConnectionImageView.setVisibility(View.INVISIBLE);
            mNoConnectionMessage.setVisibility(View.INVISIBLE);

            //Init or restart mission loader
            getLoaderManager().restartLoader(MISSIONS_LOADER_ID, null, this);
        } else {
            // Otherwise, if missions were loaded before, just display a toast
            if (SpaceXPreferences.getMissionsStatus(getActivityCast())) {
                Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, display a connection error message and a no connection icon
                mMissionsRecyclerView.setVisibility(View.INVISIBLE);
                mNoConnectionImageView.setVisibility(View.VISIBLE);
                mNoConnectionMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    public SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    @Override
    public void onItemClickListener(int missionNumber) {
        Intent missionDetailsIntent = new Intent(getActivity(), MissionDetailsActivity.class);
        missionDetailsIntent.putExtra(MISSION_NUMBER_KEY, missionNumber);
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
                if (data != null && data.size() > 0) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // If data indeed was retrieved, insert launch pads into the DB
                            mDb.missionDao().insertMissions(data);
                            // This loader is activate the first time the activity is open or when
                            // a swipe to refresh gesture is made. Each time we set the missions
                            // status preference to TRUE, so the next time we need to load data,
                            // the app will opt for loading it from DB
                            SpaceXPreferences.setMissionsStatus(getContext(), true);
                        }
                    });

                    // Save the total number of missions
                    SpaceXPreferences.setTotalNumberOfMissions(getActivityCast(), data.size());
                }

                // Update widget
                UpdateIntentService.startActionUpdateMissionWidget(getActivityCast());


                // Setup the view model, especially if this is the first time the data is loaded
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
