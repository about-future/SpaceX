package com.about.future.spacex.ui.fragments;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.about.future.spacex.R;
import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.ui.adapters.LandingPadsAdapter;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.viewmodel.LandingPadsViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.LANDING_PADS_RECYCLER_POSITION_KEY;

public class LandingPadsFragment extends Fragment implements LandingPadsAdapter.ListItemClickListener {
    private int mLandingPadsPosition = RecyclerView.NO_POSITION;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private LandingPadsAdapter mLandingPadsAdapter;
    private LandingPadsViewModel mViewModel;

    @BindView(R.id.swipe_refresh_landing_pads_list_layout)
    SwipeRefreshLayout mSwipeToRefreshLayout;
    @BindView(R.id.landing_pads_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.landing_pads_no_connection_message)
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
        if (savedInstanceState != null && savedInstanceState.containsKey(LANDING_PADS_RECYCLER_POSITION_KEY)) {
            mLandingPadsPosition = savedInstanceState.getInt(LANDING_PADS_RECYCLER_POSITION_KEY);
        }

        View view = inflater.inflate(R.layout.fragment_landing_pads_list, container, false);
        ButterKnife.bind(this, view);

        // Setup RecyclerView and Adaptor
        setupRecyclerView();
        // Init view model
        mViewModel = ViewModelProviders.of(this).get(LandingPadsViewModel.class);

        mSwipeToRefreshLayout.setOnRefreshListener(() -> {
            mSwipeToRefreshLayout.setRefreshing(false);
            SpaceXPreferences.setLandingPadsStatus(getContext(), true);
            getLandingPads();
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
        mLandingPadsAdapter = new LandingPadsAdapter(getContext(), this);
        mRecyclerView.setAdapter(mLandingPadsAdapter);
    }

    private SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    private void restorePosition() {
        if (mLandingPadsPosition == RecyclerView.NO_POSITION) mLandingPadsPosition = 0;
        // Scroll the RecyclerView to mPosition
        mRecyclerView.scrollToPosition(mLandingPadsPosition);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Save RecyclerView state
        if (mLinearLayoutManager != null) {
            mLandingPadsPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        } else if (mStaggeredGridLayoutManager != null) {
            mLandingPadsPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(mStaggeredPosition)[0];
        }

        outState.putInt(LANDING_PADS_RECYCLER_POSITION_KEY, mLandingPadsPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLandingPads();
    }

    // If landing pads were already loaded once, just query the DB and display them,
    // otherwise get them from server
    private void getLandingPads() {
        // If a forced download is requested, getLandingPadsStatus flag is true
        // and we download all landing pads from server
        if (SpaceXPreferences.getLandingPadsStatus(getActivityCast())) {
            // If there is a network connection
            if (NetworkUtils.isConnected(getActivityCast())) {
                //loadingStateUi();
                getLandingPadsFromServer();
            } else {
                // Show connection error
                if (SpaceXPreferences.getLandingPadsFirstLoad(getActivityCast())) {
                    errorStateUi(2);
                } else {
                    Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Otherwise, get them from DB
            getLandingPadsFromDB();
        }
    }

    private void getLandingPadsFromServer() {
        Log.v("GET PADS", "FROM SERVER");

        mViewModel.getLandingPadsFromServer().observe(this, checkResultDisplay -> {
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
                        if (SpaceXPreferences.getLandingPadsFirstLoad(getActivityCast())) {
                            errorStateUi(1);
                        } else {
                            Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        if (mSwipeToRefreshLayout != null)
                            mSwipeToRefreshLayout.setRefreshing(false);

                        List<LandingPad> landingPads = checkResultDisplay.data;

                        if (landingPads != null && landingPads.size() > 0) {
                            SpaceXPreferences.setLandingPadsStatus(getContext(), false);
                            SpaceXPreferences.setLandingPadsFirstLoad(getActivityCast(), false);
                            getLandingPadsFromDB();
                        } else {
                            // Update UI
                            if (SpaceXPreferences.getLandingPadsFirstLoad(getActivityCast())) {
                                errorStateUi(0);
                            } else {
                                Toast.makeText(getActivityCast(), getString(R.string.no_landing_pads_available), Toast.LENGTH_SHORT).show();
                            }
                        }

                        break;
                }
            }
        });
    }

    private void getLandingPadsFromDB() {
        Log.v("GET PADS", "FROM DB");

        // Try loading data from DB, if no data was found show empty list
        mViewModel.getLandingPadsFromDb().observe(this, landingPads -> {
            if (landingPads != null && landingPads.size() > 0) {
                // Update UI
                successStateUi();
                // Show data
                mLandingPadsAdapter.setLandingPads(landingPads);
                restorePosition();
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
                mErrorMessage.setText(getString(R.string.no_landing_pads_available));
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
    public void onItemClickListener(String selectedLandingPad) {
        //Intent landingPadDetailsIntent = new Intent(getActivity(), LandingPadDetailsActivity.class);
        //landingPadDetailsIntent.putExtra(LAUNCH_PAD_ID_KEY, selectedLandingPad);
        //startActivity(landingPadDetailsIntent);
    }
}
