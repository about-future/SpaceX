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
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.ui.MissionDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.ui.adapters.MissionsAdapter;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.widget.UpdateIntentService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.MISSIONS_RECYCLER_POSITION_KEY;
import static com.about.future.spacex.utils.Constants.MISSION_NUMBER_KEY;

public class MissionsFragment extends Fragment implements MissionsAdapter.ListItemClickListener {
    private int mMissionsPosition = RecyclerView.NO_POSITION;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private MissionsAdapter mMissionsAdapter;
    private MissionsViewModel mViewModel;

    @BindView(R.id.swipe_refresh_missions_list_layout)
    SwipeRefreshLayout mSwipeToRefreshLayout;
    @BindView(R.id.missions_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.missions_no_connection_message)
    TextView mNoConnectionMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(MISSIONS_RECYCLER_POSITION_KEY)) {
            mMissionsPosition = savedInstanceState.getInt(MISSIONS_RECYCLER_POSITION_KEY);
        }

        View view = inflater.inflate(R.layout.fragment_missions_list, container, false);
        ButterKnife.bind(this, view);

        // Setup RecyclerView and Adaptor
        setupRecyclerView();

        // Init view model
        mViewModel = ViewModelProviders.of(this).get(MissionsViewModel.class);

        mSwipeToRefreshLayout.setOnRefreshListener(() -> {
            mSwipeToRefreshLayout.setRefreshing(false);
            SpaceXPreferences.setMissionsStatus(getContext(), true);
            getMissions();
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

        mRecyclerView.setHasFixedSize(false);  //TODO: Maybe it has to be true
        mMissionsAdapter = new MissionsAdapter(getContext(), this);
        mRecyclerView.setAdapter(mMissionsAdapter);
    }

    private SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    //TODO: Use this or implement a better one
    private void restorePosition() {
        if (mMissionsPosition == RecyclerView.NO_POSITION) mMissionsPosition = 0;
        // Scroll the RecyclerView to mPosition
        mRecyclerView.scrollToPosition(mMissionsPosition);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Save RecyclerView state
        if (mLinearLayoutManager != null) {
            mMissionsPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        } else if (mStaggeredGridLayoutManager != null) {
            mMissionsPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(mStaggeredPosition)[0];
        }

        outState.putInt(MISSIONS_RECYCLER_POSITION_KEY, mMissionsPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Every time this fragment is resumed, reset the missions list in our adaptor,
        // so the time left until launch could be recalculated for the upcoming missions and
        // any changes made on "Units" setting could be reflect here too.
        getMissions();
        /*if (mMissions != null) {
            mMissionsAdapter.setMissions(mMissions);
            //resumePosition();
        }*/

        // Update widget
        UpdateIntentService.startActionUpdateMissionWidget(getActivityCast());
    }

    // If missions were already loaded once, just query the DB and display them,
    // otherwise get them from server
    private void getMissions() {
        // If a forced download is requested, getMissionsStatus flag is true and we download all missions from server
        if (SpaceXPreferences.getMissionsStatus(getActivityCast())) {
            // If there is a network connection
            if (NetworkUtils.isConnected(getActivityCast())) {
                loadingStateUi();
                getMissionsFromServer();
            } else {
                // Show connection error
                //showDialog(getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE);
                Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise, get them from DB
            getMissionsFromDB();
        }
    }

    private void getMissionsFromServer() {
        Log.v("GET MISSIONS", "FROM SERVER");

        mViewModel.getMissionsFromServer().observe(this, checkResultDisplay -> {
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

                        List<Mission> missions = checkResultDisplay.data;

                        if (missions != null && missions.size() > 0) {
                            SpaceXPreferences.setMissionsStatus(getContext(), false);
                            getMissionsFromDB();

                            // Save the total number of missions
                            SpaceXPreferences.setTotalNumberOfMissions(getActivityCast(), missions.size());
                        } else {
                            // Update UI
                            errorStateUi(0);
                        }

                        break;
                }
            }
        });
    }

    private void getMissionsFromDB() {
        Log.v("GET MISSIONS", "FROM DB");

        // Try loading data from DB, if no data was found show empty list
        mViewModel.getMissionsFromDb().observe(this, missions -> {
            if (missions != null && missions.size() > 0) {
                // Update UI
                successStateUi();
                // Show data
                mMissionsAdapter.setMissions(missions);

                // Update widget
                UpdateIntentService.startActionUpdateMissionWidget(getActivityCast());
            } else {
                // Update UI
                errorStateUi(0);
            }
        });
    }

    private void loadingStateUi() {
        mRecyclerView.setVisibility(View.GONE);
        mNoConnectionMessage.setVisibility(View.VISIBLE);
        mNoConnectionMessage.setText(getString(R.string.loading_missions));
    }

    private void errorStateUi(int errorType) {
        mRecyclerView.setVisibility(View.GONE);
        mNoConnectionMessage.setVisibility(View.INVISIBLE);
        switch (errorType) {
            case 0:
                mNoConnectionMessage.setText(getString(R.string.no_mission_available));
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
    public void onItemClickListener(int missionNumber) {
        Intent missionDetailsIntent = new Intent(getActivity(), MissionDetailsActivity.class);
        missionDetailsIntent.putExtra(MISSION_NUMBER_KEY, missionNumber);
        startActivity(missionDetailsIntent);
    }
}
