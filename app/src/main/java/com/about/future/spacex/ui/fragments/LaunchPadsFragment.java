package com.about.future.spacex.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.about.future.spacex.R;
import com.about.future.spacex.databinding.FragmentLaunchPadsBinding;
import com.about.future.spacex.ui.adapters.LaunchPadsAdapter;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.ui.LaunchPadDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.viewmodel.LaunchPadsViewModel;
import com.about.future.spacex.model.pads.LaunchPad;
import com.about.future.spacex.utils.SpaceXPreferences;

import java.util.List;

import static com.about.future.spacex.utils.Constants.LAUNCH_PADS_RECYCLER_POSITION_KEY;
import static com.about.future.spacex.utils.Constants.LAUNCH_PAD_ID_KEY;

public class LaunchPadsFragment extends Fragment implements LaunchPadsAdapter.ListItemClickListener {
    private int mLaunchPadsPosition = RecyclerView.NO_POSITION;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private LaunchPadsAdapter mLaunchPadsAdapter;
    private LaunchPadsViewModel mViewModel;
    private FragmentLaunchPadsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(LAUNCH_PADS_RECYCLER_POSITION_KEY)) {
            mLaunchPadsPosition = savedInstanceState.getInt(LAUNCH_PADS_RECYCLER_POSITION_KEY);
        }

        binding = FragmentLaunchPadsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        setupRecyclerView();
        mViewModel = new ViewModelProvider(this).get(LaunchPadsViewModel.class);

        binding.swipeToRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeToRefreshLayout.setRefreshing(false);
            SpaceXPreferences.setLaunchPadsStatus(getContext(), true);
            getLaunchPads();
        });

        getLaunchPadsFromDB();

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

        binding.recyclerView.setHasFixedSize(false);
        mLaunchPadsAdapter = new LaunchPadsAdapter(getContext(), this);
        binding.recyclerView.setAdapter(mLaunchPadsAdapter);
    }

    private SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    private void restorePosition() {
        if (mLaunchPadsPosition == RecyclerView.NO_POSITION) mLaunchPadsPosition = 0;
        // Scroll the RecyclerView to mPosition
        binding.recyclerView.scrollToPosition(mLaunchPadsPosition);
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
                getLaunchPadsFromServer();
            } else {
                // Show connection error
                if (SpaceXPreferences.getLaunchPadsFirstLoad(getActivityCast())) {
                    errorStateUi(2);
                } else {
                    Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getLaunchPadsFromServer() {
        mViewModel.getLaunchPadsFromServer().observe(getViewLifecycleOwner(), checkResultDisplay -> {
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
                        if (SpaceXPreferences.getLaunchPadsFirstLoad(getActivityCast())) {
                            errorStateUi(1);
                        } else {
                            Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        binding.swipeToRefreshLayout.setRefreshing(false);

                        List<LaunchPad> launchPads = checkResultDisplay.data;

                        if (launchPads != null && launchPads.size() > 0) {
                            SpaceXPreferences.setLaunchPadsStatus(getContext(), false);
                            SpaceXPreferences.setLaunchPadsFirstLoad(getActivityCast(), false);
                        } else {
                            if (SpaceXPreferences.getLaunchPadsFirstLoad(getActivityCast())) {
                                errorStateUi(0);
                            } else {
                                Toast.makeText(getActivityCast(), getString(R.string.no_launch_pads_available), Toast.LENGTH_SHORT).show();
                            }
                        }

                        break;
                }
            }
        });
    }

    private void getLaunchPadsFromDB() {
        // Try loading data from DB, if no data was found show empty list
        mViewModel.getLaunchPadsFromDb().observe(getViewLifecycleOwner(), launchPads -> {
            if (launchPads != null && launchPads.size() > 0) {
                // Update UI
                successStateUi();
                // Show data
                mLaunchPadsAdapter.setLaunchPads(launchPads);
                restorePosition();
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
                binding.errorMessage.setText(getString(R.string.no_launch_pads_available));
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
    public void onItemClickListener(String selectedLaunchPad) {
        Intent launchPadDetailsIntent = new Intent(getActivity(), LaunchPadDetailsActivity.class);
        launchPadDetailsIntent.putExtra(LAUNCH_PAD_ID_KEY, selectedLaunchPad);
        startActivity(launchPadDetailsIntent);
    }
}
