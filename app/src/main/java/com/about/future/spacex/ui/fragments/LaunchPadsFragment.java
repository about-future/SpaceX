package com.about.future.spacex.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.about.future.spacex.R;
import com.about.future.spacex.ui.adapters.LaunchPadsAdapter;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.ui.LaunchPadDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.viewmodel.LaunchPadsViewModel;
import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.utils.SpaceXPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.LAUNCH_PADS_RECYCLER_POSITION_KEY;
import static com.about.future.spacex.utils.Constants.LAUNCH_PAD_ID_KEY;

public class LaunchPadsFragment extends Fragment implements LaunchPadsAdapter.ListItemClickListener {
    private int mLaunchPadsPosition = RecyclerView.NO_POSITION;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private LaunchPadsAdapter mLaunchPadsAdapter;
    private LaunchPadsViewModel mViewModel;

    @BindView(R.id.swipe_refresh_launch_pads_list_layout)
    SwipeRefreshLayout mSwipeToRefreshLayout;
    @BindView(R.id.launch_pads_rv)
    RecyclerView mRecyclerView;
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

        // Setup RecyclerView and Adaptor
        setupRecyclerView();
        // Init view model
        mViewModel = ViewModelProviders.of(this).get(LaunchPadsViewModel.class);

        mSwipeToRefreshLayout.setOnRefreshListener(() -> {
            mSwipeToRefreshLayout.setRefreshing(false);
            SpaceXPreferences.setLaunchPadsStatus(getContext(), true);
            getLaunchPads();
        });

        return view;
    }

    private void setupRecyclerView() {
        if (ScreenUtils.isPortraitMode(getActivityCast())) {
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    mRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            mRecyclerView.addItemDecoration(mDividerItemDecoration);
        } else {
            int columnCount = getResources().getInteger(R.integer.mission_list_column_count);
            mStaggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        }

        mRecyclerView.setHasFixedSize(false);
        mLaunchPadsAdapter = new LaunchPadsAdapter(getContext(), this);
        mRecyclerView.setAdapter(mLaunchPadsAdapter);
    }

    private SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    private void restorePosition() {
        if (mLaunchPadsPosition == RecyclerView.NO_POSITION) mLaunchPadsPosition = 0;
        // Scroll the RecyclerView to mPosition
        mRecyclerView.scrollToPosition(mLaunchPadsPosition);
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
    public void onResume() {
        super.onResume();
        getLaunchPads();
    }

    // If launch pads were already loaded once, just query the DB and display them,
    // otherwise get them from server
    private void getLaunchPads() {
        // If a forced download is requested, getLaunchPadsStatus flag is true
        // and we download all launch pads from server
        if (SpaceXPreferences.getLaunchPadsStatus(getActivityCast())) {
            // If there is a network connection
            if (NetworkUtils.isConnected(getActivityCast())) {
                loadingStateUi();
                getLaunchPadsFromServer();
            } else {
                // Show connection error
                //showDialog(getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE);
                Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise, get them from DB
            getLaunchPadsFromDB();
        }
    }

    private void getLaunchPadsFromServer() {
        Log.v("GET PADS", "FROM SERVER");

        mViewModel.getLaunchPadsFromServer().observe(this, checkResultDisplay -> {
            if (checkResultDisplay != null) {
                switch (checkResultDisplay.state) {
                    case ResultDisplay.STATE_LOADING:
                        // Update UI
                        loadingStateUi();
                        break;
                    case ResultDisplay.STATE_ERROR:
                        // Update UI
                        errorStateUi(1);
                        if (mSwipeToRefreshLayout != null)
                            mSwipeToRefreshLayout.setRefreshing(false);

                        // Show error message
                        Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        if (mSwipeToRefreshLayout != null)
                            mSwipeToRefreshLayout.setRefreshing(false);

                        List<LaunchPad> launchPads = checkResultDisplay.data;

                        if (launchPads != null && launchPads.size() > 0) {
                            SpaceXPreferences.setLaunchPadsStatus(getContext(), false);
                            getLaunchPadsFromDB();

                            // Save the total number of launch pads
                            SpaceXPreferences.setTotalNumberOfLaunchPads(getActivityCast(), launchPads.size());
                        } else {
                            // Update UI
                            errorStateUi(0);
                        }

                        break;
                }
            }
        });
    }

    private void getLaunchPadsFromDB() {
        Log.v("GET PADS", "FROM DB");

        // Try loading data from DB, if no data was found show empty list
        mViewModel.getLaunchPadsFromDb().observe(this, launchPads -> {
            if (launchPads != null && launchPads.size() > 0) {
                // Update UI
                successStateUi();
                // Show data
                mLaunchPadsAdapter.setLaunchPads(launchPads);
            } else {
                // Update UI
                errorStateUi(0);
            }
        });
    }

    private void loadingStateUi() {
        mRecyclerView.setVisibility(View.GONE);
        mNoConnectionMessage.setVisibility(View.VISIBLE);
        mNoConnectionMessage.setText(getString(R.string.loading_pads));
    }

    private void errorStateUi(int errorType) {
        mRecyclerView.setVisibility(View.GONE);
        mNoConnectionMessage.setVisibility(View.INVISIBLE);
        switch (errorType) {
            case 0:
                mNoConnectionMessage.setText(getString(R.string.no_pads_available));
                break;
            case 1:
                mNoConnectionMessage.setText(getString(R.string.unknown_error));
                break;
            default:
                mNoConnectionMessage.setText(getString(R.string.no_connection));
                break;
        }
    }

    private void successStateUi() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoConnectionMessage.setVisibility(View.GONE);
    }

    @Override
    public void onItemClickListener(int launchPadId) {
        Intent launchPadDetailsIntent = new Intent(getActivity(), LaunchPadDetailsActivity.class);
        launchPadDetailsIntent.putExtra(LAUNCH_PAD_ID_KEY, launchPadId);
        startActivity(launchPadDetailsIntent);
    }
}
