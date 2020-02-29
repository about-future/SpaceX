package com.about.future.spacex.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.about.future.spacex.R;
import com.about.future.spacex.ui.adapters.RocketsAdapter;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.ui.RocketDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.viewmodel.RocketsViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.ROCKET_ID_KEY;

public class RocketsFragment extends Fragment implements RocketsAdapter.ListItemClickListener {
    private RocketsAdapter mRocketsAdapter;
    private RocketsViewModel mViewModel;

    @BindView(R.id.swipe_refresh_rockets_list_layout)
    SwipeRefreshLayout mSwipeToRefreshLayout;
    @BindView(R.id.rockets_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.rockets_no_connection_message)
    TextView mNoConnectionMessage;
    @BindView(R.id.loading_layout)
    LinearLayout mLoadingLayout;
    @BindView(R.id.special_error_layout)
    LinearLayout mErrorLayout;
    @BindView(R.id.special_error_message)
    TextView mErrorMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rockets_list, container, false);
        ButterKnife.bind(this, view);

        // Setup RecyclerView and Adaptor
        setupRecyclerView();
        // Init view model
        mViewModel = ViewModelProviders.of(this).get(RocketsViewModel.class);

        mSwipeToRefreshLayout.setOnRefreshListener(() -> {
            mSwipeToRefreshLayout.setRefreshing(false);
            SpaceXPreferences.setRocketsStatus(getContext(), true);
            getRockets();
        });

        return view;
    }

    private void setupRecyclerView() {
        int columnCount = getResources().getInteger(R.integer.rocket_list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.setHasFixedSize(false);
        mRocketsAdapter = new RocketsAdapter(getContext(), this);
        mRecyclerView.setAdapter(mRocketsAdapter);
    }

    private SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        getRockets();
    }

    // If rockets were already loaded once, just query the DB and display them,
    // otherwise get them from server
    private void getRockets() {
        // If a forced download is requested, getRocketsStatus flag is true
        // and we download all rockets from server
        if (SpaceXPreferences.getRocketsStatus(getActivityCast())) {
            // If there is a network connection
            if (NetworkUtils.isConnected(getActivityCast())) {
                //loadingStateUi();
                getRocketsFromServer();
            } else {
                // Show connection error
                if (SpaceXPreferences.getRocketsFirstLoad(getActivityCast())) {
                    errorStateUi(2);
                } else {
                    Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Otherwise, get them from DB
            getRocketsFromDB();
        }
    }

    private void getRocketsFromServer() {
        Log.v("GET ROCKETS", "FROM SERVER");

        mViewModel.getRocketsFromServer().observe(this, checkResultDisplay -> {
            if (checkResultDisplay != null) {
                switch (checkResultDisplay.state) {
                    case ResultDisplay.STATE_LOADING:
                        // Update UI
                        loadingStateUi();
                        break;
                    case ResultDisplay.STATE_ERROR:
                        // Update UI
                        if (mSwipeToRefreshLayout != null)
                            mSwipeToRefreshLayout.setRefreshing(false);

                        // Show error message
                        if (SpaceXPreferences.getRocketsFirstLoad(getActivityCast())) {
                            errorStateUi(1);
                        } else {
                            Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        if (mSwipeToRefreshLayout != null)
                            mSwipeToRefreshLayout.setRefreshing(false);

                        List<Rocket> rockets = checkResultDisplay.data;

                        if (rockets != null && rockets.size() > 0) {
                            SpaceXPreferences.setRocketsStatus(getContext(), false);
                            SpaceXPreferences.setRocketsFirstLoad(getActivityCast(), false);
                            getRocketsFromDB();
                        } else {
                            // Update UI
                            if (SpaceXPreferences.getRocketsFirstLoad(getActivityCast())) {
                                errorStateUi(0);
                            } else {
                                Toast.makeText(getActivityCast(), getString(R.string.no_rocket_available), Toast.LENGTH_SHORT).show();
                            }
                        }

                        break;
                }
            }
        });
    }

    private void getRocketsFromDB() {
        Log.v("GET ROCKETS", "FROM DB");

        // Try loading data from DB, if no data was found show empty list
        mViewModel.getMiniRocketsFromDb().observe(this, rockets -> {
            if (rockets != null && rockets.size() > 0) {
                // Update UI
                successStateUi();
                // Show data
                mRocketsAdapter.setRockets(rockets);
            } else {
                // Update UI
               errorStateUi(0);
            }
        });
    }

    private void loadingStateUi() {
        mRecyclerView.setVisibility(View.GONE);
        mNoConnectionMessage.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
    }

    private void errorStateUi(int errorType) {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);

        switch (errorType) {
            case 0:
                mErrorLayout.setVisibility(View.VISIBLE);
                mErrorMessage.setText(getString(R.string.no_rocket_available));
                mNoConnectionMessage.setVisibility(View.GONE);
                break;
            case 1:
                mErrorLayout.setVisibility(View.VISIBLE);
                mErrorMessage.setText(getString(R.string.unknown_error));
                mNoConnectionMessage.setVisibility(View.GONE);
                break;
            default:
                mErrorLayout.setVisibility(View.GONE);
                mNoConnectionMessage.setVisibility(View.VISIBLE);
                mNoConnectionMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_cloud_off, 0, 0);
                mNoConnectionMessage.setText(getString(R.string.no_connection));
                break;
        }
    }

    private void successStateUi() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoConnectionMessage.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClickListener(String selectedRocket) {
        Intent rocketDetailsIntent = new Intent(getActivity(), RocketDetailsActivity.class);
        rocketDetailsIntent.putExtra(ROCKET_ID_KEY, selectedRocket);
        startActivity(rocketDetailsIntent);
    }
}
