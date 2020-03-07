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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.about.future.spacex.R;
import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.ui.CoreDetailsActivity;
import com.about.future.spacex.ui.LandingPadDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.ui.adapters.CoresAdapter;
import com.about.future.spacex.ui.adapters.LandingPadsAdapter;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.viewmodel.CoresViewModel;
import com.about.future.spacex.viewmodel.LandingPadsViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.CORE_RECYCLER_POSITION_KEY;
import static com.about.future.spacex.utils.Constants.CORE_SERIAL_KEY;
import static com.about.future.spacex.utils.Constants.LANDING_PADS_RECYCLER_POSITION_KEY;
import static com.about.future.spacex.utils.Constants.LANDING_PAD_ID_KEY;

public class CoresFragment extends Fragment implements CoresAdapter.ListItemClickListener {
    private int mCoresPosition = RecyclerView.NO_POSITION;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private CoresAdapter mCoresAdapter;
    private CoresViewModel mViewModel;

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
        if (savedInstanceState != null && savedInstanceState.containsKey(CORE_RECYCLER_POSITION_KEY)) {
            mCoresPosition = savedInstanceState.getInt(CORE_RECYCLER_POSITION_KEY);
        }

        View view = inflater.inflate(R.layout.fragment_cores, container, false);
        ButterKnife.bind(this, view);

        // Setup RecyclerView and Adaptor
        setupRecyclerView();
        // Init view model
        mViewModel = ViewModelProviders.of(this).get(CoresViewModel.class);

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
        mCoresAdapter = new CoresAdapter(getContext(), this);
        mRecyclerView.setAdapter(mCoresAdapter);
    }

    private SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    private void restorePosition() {
        if (mCoresPosition == RecyclerView.NO_POSITION) mCoresPosition = 0;
        // Scroll the RecyclerView to mPosition
        mRecyclerView.scrollToPosition(mCoresPosition);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Save RecyclerView state
        if (mLinearLayoutManager != null) {
            mCoresPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        } else if (mStaggeredGridLayoutManager != null) {
            mCoresPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(mStaggeredPosition)[0];
        }

        outState.putInt(CORE_RECYCLER_POSITION_KEY, mCoresPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLandingPads();
    }

    // If cores were already loaded once, just query the DB and display them,
    // otherwise get them from server
    private void getLandingPads() {
        // If a forced download is requested, getCoresStatus flag is true
        // and we download all landing pads from server
        if (SpaceXPreferences.getCoresStatus(getActivityCast())) {
            // If there is a network connection
            if (NetworkUtils.isConnected(getActivityCast())) {
                //loadingStateUi();
                getCoresFromServer();
            } else {
                // Show connection error
                if (SpaceXPreferences.getCoresFirstLoad(getActivityCast())) {
                    errorStateUi(2);
                } else {
                    Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Otherwise, get them from DB
            getCoresFromDB();
        }
    }

    private void getCoresFromServer() {
        Log.v("GET CORES", "FROM SERVER");

        mViewModel.getCoresFromServer().observe(this, checkResultDisplay -> {
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
                        if (SpaceXPreferences.getCoresFirstLoad(getActivityCast())) {
                            errorStateUi(1);
                        } else {
                            Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        if (mSwipeToRefreshLayout != null)
                            mSwipeToRefreshLayout.setRefreshing(false);

                        List<Core> cores = checkResultDisplay.data;

                        if (cores != null && cores.size() > 0) {
                            SpaceXPreferences.setCoresStatus(getContext(), false);
                            SpaceXPreferences.setCoresFirstLoad(getActivityCast(), false);
                            getCoresFromDB();
                        } else {
                            // Update UI
                            if (SpaceXPreferences.getCoresFirstLoad(getActivityCast())) {
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

    private void getCoresFromDB() {
        Log.v("GET CORES", "FROM DB");

        // Try loading data from DB, if no data was found show empty list
        mViewModel.getCoresFromDb().observe(this, cores -> {
            if (cores != null && cores.size() > 0) {
                // Update UI
                successStateUi();
                // Show data
                mCoresAdapter.setCores(cores);
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
                mErrorMessage.setText(getString(R.string.no_cores_available));
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
    public void onItemClickListener(String selectedCore) {
        Intent intent = new Intent(getActivity(), CoreDetailsActivity.class);
        intent.putExtra(CORE_SERIAL_KEY, selectedCore);
        startActivity(intent);
    }
}
