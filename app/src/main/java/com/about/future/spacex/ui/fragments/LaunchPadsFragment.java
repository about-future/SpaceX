package com.about.future.spacex.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.ui.adapters.LaunchPadsAdapter;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.ui.LaunchPadDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.viewmodel.LaunchPadsViewModel;
import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.utils.SpaceXPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchPadsFragment extends Fragment implements LaunchPadsAdapter.ListItemClickListener {

    private static final int LAUNCH_PADS_LOADER_ID = 917;
    public static final String LAUNCH_PAD_ID_KEY = "launch_pad_id";

    private final String LAUNCH_PADS_RECYCLER_POSITION_KEY = "launch_pads_recycler_position";
    private int mLaunchPadsPosition = RecyclerView.NO_POSITION;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    private AppDatabase mDb;
    private LaunchPadsAdapter mLaunchPadsAdapter;

    @BindView(R.id.swipe_refresh_launch_pads_list_layout)
    SwipeRefreshLayout mSwipeRefreshLaunchPadsLayout;
    @BindView(R.id.launch_pads_rv)
    RecyclerView mLaunchPadsRecyclerView;
    @BindView(R.id.launch_pads_no_connection_cloud)
    ImageView mNoConnectionImageView;
    @BindView(R.id.launch_pads_no_connection_message)
    TextView mNoConnectionMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(LAUNCH_PADS_RECYCLER_POSITION_KEY)) {
            mLaunchPadsPosition = savedInstanceState.getInt(LAUNCH_PADS_RECYCLER_POSITION_KEY);
        }

        View view = inflater.inflate(R.layout.fragment_launch_pads_list, container, false);
        ButterKnife.bind(this, view);

        if (ScreenUtils.isPortraitMode(getActivityCast())) {
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mLaunchPadsRecyclerView.setLayoutManager(mLinearLayoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    mLaunchPadsRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            mLaunchPadsRecyclerView.addItemDecoration(mDividerItemDecoration);
        } else {
            int columnCount = getResources().getInteger(R.integer.mission_list_column_count);
            mStaggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            mLaunchPadsRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        }

        mLaunchPadsRecyclerView.setHasFixedSize(false);
        mLaunchPadsAdapter = new LaunchPadsAdapter(getContext(), this);
        mLaunchPadsRecyclerView.setAdapter(mLaunchPadsAdapter);

        mDb = AppDatabase.getInstance(getContext());

        // If launch pads were already loaded once, just query the DB and display them,
        // otherwise init the loader and get data from server
        if (SpaceXPreferences.getLaunchPadsStatus(getActivityCast())) {
            // Load data from DB
            setupViewModel();
        } else {
            // Get data from internet
            getData();
        }

        mSwipeRefreshLaunchPadsLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Get data from internet
                getData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLaunchPadsLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        return view;
    }

    private void setupViewModel() {
        LaunchPadsViewModel launchPadsViewModel = ViewModelProviders.of(this).get(LaunchPadsViewModel.class);
        launchPadsViewModel.getLaunchPads().observe(this, new Observer<List<LaunchPad>>() {
            @Override
            public void onChanged(@Nullable List<LaunchPad> launchPads) {
                if (launchPads != null) {
                    mLaunchPadsAdapter.setLaunchPads(launchPads);
                    restorePosition();
                }
            }
        });
    }

    private void getData() {
        // Get of refresh data, if there is a network connection
        if (NetworkUtils.isConnected(getActivityCast())) {
            mLaunchPadsRecyclerView.setVisibility(View.VISIBLE);
            mNoConnectionImageView.setVisibility(View.INVISIBLE);
            mNoConnectionMessage.setVisibility(View.INVISIBLE);

            //Init or restart launch pads loader
            getLoaderManager().restartLoader(LAUNCH_PADS_LOADER_ID, null, this);
        } else {
            // Otherwise, if LaunchPads were loaded before, just display a toast
            if (SpaceXPreferences.getLaunchPadsStatus(getActivityCast())) {
                Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, display a connection error message and a no connection icon
                mLaunchPadsRecyclerView.setVisibility(View.INVISIBLE);
                mNoConnectionImageView.setVisibility(View.VISIBLE);
                mNoConnectionMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    public SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    private void restorePosition() {
        if (mLaunchPadsPosition == RecyclerView.NO_POSITION) mLaunchPadsPosition = 0;
        // Scroll the RecyclerView to mPosition
        mLaunchPadsRecyclerView.scrollToPosition(mLaunchPadsPosition);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Save RecyclerView state
        if (mLinearLayoutManager != null) {
            mLaunchPadsPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        } else if (mStaggeredGridLayoutManager != null) {
            mLaunchPadsPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(mStaggeredPosition)[0];
        }

        outState.putInt(LAUNCH_PADS_RECYCLER_POSITION_KEY, mLaunchPadsPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClickListener(int launchPadId) {
        Intent launchPadDetailsIntent = new Intent(getActivity(), LaunchPadDetailsActivity.class);
        launchPadDetailsIntent.putExtra(LAUNCH_PAD_ID_KEY, launchPadId);
        startActivity(launchPadDetailsIntent);
    }

    @NonNull
    @Override
    public Loader<List<LaunchPad>> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case LAUNCH_PADS_LOADER_ID:
                // If the loaded id matches launch pad loader, return a new launch pad loader
                return new LaunchPadsLoader(getActivityCast());
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<LaunchPad>> loader, final List<LaunchPad> data) {
        switch (loader.getId()) {
            case LAUNCH_PADS_LOADER_ID:
                if (data != null && data.size() > 0) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // If data indeed was retrieved, insert launch pads into the DB
                            mDb.launchPadDao().insertLaunchPads(data);
                            // This loader is activate the first time the activity is open or when
                            // a swipe to refresh gesture is made. Each time we set the LaunchPad
                            // status preference to TRUE, so the next time we need to load data,
                            // the app will opt for loading it from DB
                            SpaceXPreferences.setLaunchPadsStatus(getContext(), true);
                        }
                    });

                    // Save the total number of launch pads
                    SpaceXPreferences.setTotalNumberOfLaunchPads(getActivityCast(), data.size());
                }

                // Setup the view model, especially if this is the first time the data is loaded
                setupViewModel();

                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<LaunchPad>> loader) {
        mLaunchPadsAdapter.setLaunchPads(null);
    }
}
