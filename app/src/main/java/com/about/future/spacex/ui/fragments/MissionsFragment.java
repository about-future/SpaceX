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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.about.future.spacex.R;
import com.about.future.spacex.fcm.SpaceXWorker;
import com.about.future.spacex.utils.DateUtils;
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

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @BindView(R.id.loading_layout)
    LinearLayout mLoadingLayout;
    @BindView(R.id.special_error_layout)
    LinearLayout mErrorLayout;
    @BindView(R.id.special_error_message)
    TextView mErrorMessage;

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

        Constraints constraints = new Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                //.setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .build();

        /*PeriodicWorkRequest downloadLaunchesWorkRequest = new PeriodicWorkRequest
                .Builder(SpaceXWorker.class, 1, TimeUnit.HOURS)
                //.setInitialDelay(1, TimeUnit.HOURS)
                .addTag("missions")
                //.setConstraints(constraints)
                .build();

        WorkManager.getInstance(getActivityCast()).enqueueUniquePeriodicWork(
                "missions",
                ExistingPeriodicWorkPolicy.KEEP ,
                downloadLaunchesWorkRequest);*/

        if (SpaceXPreferences.getLaunchesFirstLoad(getActivityCast())) {
            OneTimeWorkRequest downloadMissions = new OneTimeWorkRequest
                    .Builder(SpaceXWorker.class)
                    .setConstraints(constraints)
                    .setInitialDelay(5, TimeUnit.MINUTES)
                    .build();

            WorkManager.getInstance(getActivityCast()).enqueueUniqueWork(
                    "missionsWork",
                    ExistingWorkPolicy.KEEP,
                    downloadMissions
            );
        }

        /*String date = SpaceXPreferences.getDownloadDate(getActivityCast());
        String now = DateUtils.getFullDate(new Date().getTime());
        if (date.equals("")) {
            SpaceXPreferences.setDownloadDate(getActivityCast(), now);
        } else {
            SpaceXPreferences.setDownloadDate(getActivityCast(), date.concat("\n").concat(now));
        }*/

        //SpaceXPreferences.setDownloadDate(getActivityCast(), "");
        Log.v("DATE", "IS: " + SpaceXPreferences.getDownloadDate(getActivityCast()));

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

    //TODO:
    // 1. resume position upon screen rotation to list and detail layouts
    // 2. remake Settings layout and activity
    // 3. add another language or 2
    // 4. fix mission and rocket details layouts
    // 5. add gallery to missions and rockets
    // 6. add more endpoints and maybe change main layout to bottom navigation with 4-5 options
    // and for launches create 2 lists: upcoming launches and previous launches
    // 7. fix links to rocket and launch pad called from mission details

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

        /*OneTimeWorkRequest showMess = new OneTimeWorkRequest.Builder(SpaceXWorker.class).build();
        WorkManager.getInstance(getActivityCast()).enqueue(showMess);

        WorkManager.getInstance(getActivityCast()).getWorkInfosByTagLiveData(showMess.getId().toString()).observe(this, workInfos -> {
            for (WorkInfo workInfo : workInfos) {
                String status = workInfo.getState().name();
                Log.v("WORK MANAGER", "STATE IS: " + status);
            }
        });*/
    }

    // If missions were already loaded once, just query the DB and display them,
    // otherwise get them from server
    private void getMissions() {
        // If a forced download is requested, getMissionsStatus flag is true and we download all missions from server
        if (SpaceXPreferences.getMissionsStatus(getActivityCast())) {
            // If there is a network connection
            if (NetworkUtils.isConnected(getActivityCast())) {
                //loadingStateUi();
                getMissionsFromServer();
            } else {
                // Show connection error
                if (SpaceXPreferences.getLaunchesFirstLoad(getActivityCast())) {
                    errorStateUi(2);
                } else {
                    Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
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
                        if (mSwipeToRefreshLayout != null)
                            mSwipeToRefreshLayout.setRefreshing(false);

                        // Show error message
                        if (SpaceXPreferences.getLaunchesFirstLoad(getActivityCast())) {
                            errorStateUi(1);
                        } else {
                            Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        if (mSwipeToRefreshLayout != null)
                            mSwipeToRefreshLayout.setRefreshing(false);

                        List<Mission> missions = checkResultDisplay.data;

                        if (missions != null && missions.size() > 0) {
                            SpaceXPreferences.setMissionsStatus(getContext(), false);
                            SpaceXPreferences.setLaunchesFirstLoad(getActivityCast(), false);
                            getMissionsFromDB();
                        } else {
                            // Update UI
                            if (SpaceXPreferences.getLaunchesFirstLoad(getActivityCast())) {
                                errorStateUi(0);
                            } else {
                                Toast.makeText(getActivityCast(), getString(R.string.no_launches_available), Toast.LENGTH_SHORT).show();
                            }
                        }

                        break;
                }
            }
        });
    }

    private void getMissionsFromDB() {
        Log.v("GET MISSIONS", "FROM DB");

        // Try loading data from DB, if no data was found show empty list
        mViewModel.getMiniMissionsFromDb().observe(this, missions -> {
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
                mErrorMessage.setText(getString(R.string.no_launches_available));
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
    public void onItemClickListener(int selectedMission) {
        Intent missionDetailsIntent = new Intent(getActivity(), MissionDetailsActivity.class);
        missionDetailsIntent.putExtra(MISSION_NUMBER_KEY, selectedMission);
        startActivity(missionDetailsIntent);
    }
}
