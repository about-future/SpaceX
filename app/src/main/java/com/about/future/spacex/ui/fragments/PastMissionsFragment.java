package com.about.future.spacex.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.about.future.spacex.R;
import com.about.future.spacex.databinding.FragmentMissionsBinding;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.ui.MissionDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.ui.adapters.MissionsAdapter;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.about.future.spacex.widget.UpdateIntentService;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.about.future.spacex.utils.Constants.MISSIONS_RECYCLER_POSITION_KEY;
import static com.about.future.spacex.utils.Constants.MISSION_NUMBER_KEY;

public class PastMissionsFragment extends Fragment implements MissionsAdapter.ListItemClickListener {
    private int mMissionsPosition = RecyclerView.NO_POSITION;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private MissionsAdapter mMissionsAdapter;
    private MissionsViewModel mViewModel;
    private FragmentMissionsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(MISSIONS_RECYCLER_POSITION_KEY)) {
            mMissionsPosition = savedInstanceState.getInt(MISSIONS_RECYCLER_POSITION_KEY);
        }

        binding = FragmentMissionsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Setup RecyclerView and Adaptor
        setupRecyclerView();

        // Init view model
        mViewModel = ViewModelProviders.of(this).get(MissionsViewModel.class);

        binding.swipeToRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeToRefreshLayout.setRefreshing(false);
            SpaceXPreferences.setMissionsStatus(getContext(), true);
            getPastMissions();
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        if (ScreenUtils.isPortraitMode(getActivityCast())) {
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            binding.recyclerView.setLayoutManager(mLinearLayoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    binding.recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            binding.recyclerView.addItemDecoration(mDividerItemDecoration);
        } else {
            int columnCount = getResources().getInteger(R.integer.mission_list_column_count);
            mStaggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            binding.recyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        }

        binding.recyclerView.setHasFixedSize(true);
        mMissionsAdapter = new MissionsAdapter(getContext(), this);
        binding.recyclerView.setAdapter(mMissionsAdapter);
    }

    private SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    private void restorePosition() {
        if (mMissionsPosition == RecyclerView.NO_POSITION) mMissionsPosition = 0;
        // Scroll the RecyclerView to mPosition
        binding.recyclerView.scrollToPosition(mMissionsPosition);
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
        getPastMissions();
    }

    // If missions were already loaded once, just query the DB and display them,
    // otherwise get them from server
    private void getPastMissions() {
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
        mViewModel.getMissionsFromServer().observe(this, checkResultDisplay -> {
            if (checkResultDisplay != null) {
                switch (checkResultDisplay.state) {
                    case ResultDisplay.STATE_LOADING:
                        // Update UI
                        loadingStateUi();
                        break;
                    case ResultDisplay.STATE_ERROR:
                        // Update UI
                        binding.swipeToRefreshLayout.setRefreshing(false);

                        // Show error message
                        if (SpaceXPreferences.getLaunchesFirstLoad(getActivityCast())) {
                            errorStateUi(1);
                        } else {
                            Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        binding.swipeToRefreshLayout.setRefreshing(false);

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
        TimeZone tz = TimeZone.getDefault();
        Log.v("TIME ZONE", "IS: " + tz.getDisplayName());
        Log.v("DIFFERENCE", "IS: " + tz.getRawOffset());
        Log.v("DIFFERENCE 2", "IS: " + tz.getOffset(new Date().getTime()));

        long timeZoneDifference = tz.getOffset(new Date().getTime());
        long now = new Date().getTime() - timeZoneDifference;

        Log.v("NOW", "IS: " + now);
        Log.v("NOW CONVERTED", "IS: " + DateUtils.getFullDate(now));


        // Try loading data from DB, if no data was found show empty list
        mViewModel.getPastMissionsFromDb(now / 1000).observe(this, missions -> {
            if (missions != null && missions.size() > 0) {
                // Update UI
                successStateUi();
                // Show data
                mMissionsAdapter.setMissions(missions);
                restorePosition();

                // Update widget
                UpdateIntentService.startActionUpdateMissionWidget(getActivityCast());
            } else {
                // Update UI
                errorStateUi(0);
            }
        });
    }

    private void loadingStateUi() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.noConnectionMessage.setVisibility(View.GONE);
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.errorLayout.setVisibility(View.GONE);
    }

    private void errorStateUi(int errorType) {
        binding.recyclerView.setVisibility(View.GONE);
        binding.loadingLayout.setVisibility(View.GONE);

        switch (errorType) {
            case 0:
                binding.errorLayout.setVisibility(View.VISIBLE);
                binding.errorMessage.setText(getString(R.string.no_launches_available));
                binding.noConnectionMessage.setVisibility(View.GONE);
                break;
            case 1:
                binding.errorLayout.setVisibility(View.VISIBLE);
                binding.errorMessage.setText(getString(R.string.unknown_error));
                binding.noConnectionMessage.setVisibility(View.GONE);
                break;
            default:
                binding.errorLayout.setVisibility(View.GONE);
                binding.noConnectionMessage.setVisibility(View.VISIBLE);
                binding.noConnectionMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_cloud_off, 0, 0);
                binding.noConnectionMessage.setText(getString(R.string.no_connection));
                break;
        }
    }

    private void successStateUi() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        binding.noConnectionMessage.setVisibility(View.GONE);
        binding.loadingLayout.setVisibility(View.GONE);
        binding.errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClickListener(int selectedMission) {
        Intent missionDetailsIntent = new Intent(getActivity(), MissionDetailsActivity.class);
        missionDetailsIntent.putExtra(MISSION_NUMBER_KEY, selectedMission);
        startActivity(missionDetailsIntent);
    }
}
