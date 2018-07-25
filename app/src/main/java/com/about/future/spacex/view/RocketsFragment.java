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
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.LaunchPadsAdapter;
import com.about.future.spacex.data.LaunchPadsLoader;
import com.about.future.spacex.data.RocketsAdapter;
import com.about.future.spacex.data.RocketsLoader;
import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.viewmodel.LaunchPadsViewModel;
import com.about.future.spacex.viewmodel.RocketsViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RocketsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Rocket>>, RocketsAdapter.ListItemClickListener {

    private static final int ROCKETS_LOADER_ID = 495;
    public static final String ROCKET_ID_KEY = "rocket_id";
    public static final String TOTAL_ROCKETS_KEY = "total_rockets";

    private AppDatabase mDb;
    private RocketsAdapter mRocketsAdapter;
    private int mTotalRockets;

    @BindView(R.id.swipe_refresh_rockets_list_layout)
    SwipeRefreshLayout mSwipeRefreshRocketsLayout;
    @BindView(R.id.rockets_rv)
    RecyclerView mRocketsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rockets_list, container, false);
        ButterKnife.bind(this, view);

        mRocketsRecyclerView.setHasFixedSize(false);
        mRocketsAdapter = new RocketsAdapter(getContext(), this);
        mRocketsRecyclerView.setAdapter(mRocketsAdapter);

        int columnCount = getResources().getInteger(R.integer.rocket_list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRocketsRecyclerView.setLayoutManager(sglm);

        mDb = AppDatabase.getInstance(getContext());

        // If rockets were already loaded once, just query the DB and display them, otherwise init the loader
        if (SpaceXPreferences.getRocketsStatus(getActivityCast())) {
            // Load data
            setupViewModel();
        } else {
            // Get data
            getData();
        }

        mSwipeRefreshRocketsLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshRocketsLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        return view;
    }

    private void setupViewModel() {
        RocketsViewModel rocketsViewModel = ViewModelProviders.of(this).get(RocketsViewModel.class);
        rocketsViewModel.getRockets().observe(this, new Observer<List<Rocket>>() {
            @Override
            public void onChanged(@Nullable List<Rocket> rockets) {
                if (rockets != null) {
                    mRocketsAdapter.setRockets(rockets);
                    mTotalRockets = rockets.size();
                }
            }
        });
    }

    private void getData() {
        //Init mission loader
        getLoaderManager().restartLoader(ROCKETS_LOADER_ID, null, this);
    }

    public SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    @Override
    public void onItemClickListener(int rocketId) {
        Intent rocketDetailsIntent = new Intent(getActivity(), RocketDetailsActivity.class);
        rocketDetailsIntent.putExtra(ROCKET_ID_KEY, rocketId);
        rocketDetailsIntent.putExtra(TOTAL_ROCKETS_KEY, mTotalRockets);
        startActivity(rocketDetailsIntent);
    }

    @NonNull
    @Override
    public Loader<List<Rocket>> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case ROCKETS_LOADER_ID:
                // If the loaded id matches rocket loader, return a new rocket loader
                return new RocketsLoader(getActivityCast());
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Rocket>> loader, final List<Rocket> data) {
        switch (loader.getId()) {
            case ROCKETS_LOADER_ID:
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.rocketDao().insertRockets(data);
                        SpaceXPreferences.setRocketsStatus(getContext(), true);
                    }
                });

                setupViewModel();

//                if (data != null) {
//                    mRocketsAdapter.setRockets(data);
//                    mTotalRockets = data.size();
//                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Rocket>> loader) {
        mRocketsAdapter.setRockets(null);
    }
}
