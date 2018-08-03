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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.RocketsAdapter;
import com.about.future.spacex.data.RocketsLoader;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.model.rocket.RocketMini;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.viewmodel.RocketsViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RocketsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Rocket>>, RocketsAdapter.ListItemClickListener {

    private static final int ROCKETS_LOADER_ID = 495;
    public static final String ROCKET_ID_KEY = "rocket_id";

    private AppDatabase mDb;
    private RocketsAdapter mRocketsAdapter;

    @BindView(R.id.swipe_refresh_rockets_list_layout)
    SwipeRefreshLayout mSwipeRefreshRocketsLayout;
    @BindView(R.id.rockets_rv)
    RecyclerView mRocketsRecyclerView;
    @BindView(R.id.rockets_no_connection_cloud)
    ImageView mNoConnectionImageView;
    @BindView(R.id.rockets_no_connection_message)
    TextView mNoConnectionMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rockets_list, container, false);
        ButterKnife.bind(this, view);

        int columnCount = getResources().getInteger(R.integer.rocket_list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRocketsRecyclerView.setLayoutManager(sglm);
        mRocketsRecyclerView.setHasFixedSize(false);
        mRocketsAdapter = new RocketsAdapter(getContext(), this);
        mRocketsRecyclerView.setAdapter(mRocketsAdapter);

        mDb = AppDatabase.getInstance(getContext());

        // If rockets were already loaded once, just query the DB and display them,
        // otherwise init the loader and get data from server
        if (SpaceXPreferences.getRocketsStatus(getActivityCast())) {
            // Load data from DB
            setupViewModel();
        } else {
            // Get data from internet
            getData();
        }

        mSwipeRefreshRocketsLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Get data from internet
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
        rocketsViewModel.getRockets().observe(this, new Observer<List<RocketMini>>() {
            @Override
            public void onChanged(@Nullable List<RocketMini> rockets) {
                if (rockets != null) {
                    mRocketsAdapter.setRockets(rockets);
                }
            }
        });
    }

    private void getData() {
        // Get of refresh data, if there is a network connection
        if (NetworkUtils.isConnected(getActivityCast())) {
            mRocketsRecyclerView.setVisibility(View.VISIBLE);
            mNoConnectionImageView.setVisibility(View.INVISIBLE);
            mNoConnectionMessage.setVisibility(View.INVISIBLE);

            //Init or restart rockets loader
            getLoaderManager().restartLoader(ROCKETS_LOADER_ID, null, this);
        } else {
            // Otherwise, if rockets were loaded before, just display a toast
            if (SpaceXPreferences.getRocketsStatus(getActivityCast())) {
                // Display connection error message as a Toast
                Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, display a connection error message and a no connection icon
                mRocketsRecyclerView.setVisibility(View.INVISIBLE);
                mNoConnectionImageView.setVisibility(View.VISIBLE);
                mNoConnectionMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    public SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    @Override
    public void onItemClickListener(int rocketId) {
        Intent rocketDetailsIntent = new Intent(getActivity(), RocketDetailsActivity.class);
        rocketDetailsIntent.putExtra(ROCKET_ID_KEY, rocketId);
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
                if (data != null && data.size() > 0) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // If data indeed was retrieved, insert launch pads into the DB
                            mDb.rocketDao().insertRockets(data);
                            // This loader is activate the first time the activity is open or when
                            // a swipe to refresh gesture is made. Each time we set the rockets
                            // status preference to TRUE, so the next time we need to load data,
                            // the app will opt for loading it from DB
                            SpaceXPreferences.setRocketsStatus(getContext(), true);
                        }
                    });

                    // Save the total number of rockets
                    SpaceXPreferences.setTotalNumberOfRockets(getActivityCast(), data.size());
                }

                // Setup the view model, especially if this is the first time the data is loaded
                setupViewModel();

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
