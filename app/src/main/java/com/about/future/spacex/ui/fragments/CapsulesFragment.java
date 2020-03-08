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
import com.about.future.spacex.databinding.FragmentCapsulesBinding;
import com.about.future.spacex.model.rocket.Capsule;
import com.about.future.spacex.ui.CapsuleDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.ui.adapters.CapsulesAdapter;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.viewmodel.CapsulesViewModel;

import java.util.List;

import static com.about.future.spacex.utils.Constants.CAPSULE_RECYCLER_POSITION_KEY;
import static com.about.future.spacex.utils.Constants.CAPSULE_SERIAL_KEY;

public class CapsulesFragment extends Fragment implements CapsulesAdapter.ListItemClickListener {
    private int mCapsulesPosition = RecyclerView.NO_POSITION;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private CapsulesAdapter mAdapter;
    private CapsulesViewModel mViewModel;
    private FragmentCapsulesBinding binding;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(CAPSULE_RECYCLER_POSITION_KEY)) {
            mCapsulesPosition = savedInstanceState.getInt(CAPSULE_RECYCLER_POSITION_KEY);
        }

        binding = FragmentCapsulesBinding.inflate(inflater, container, false);
        mRootView = binding.getRoot();

        // Setup RecyclerView and Adaptor
        setupRecyclerView();
        // Init view model
        mViewModel = ViewModelProviders.of(this).get(CapsulesViewModel.class);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            SpaceXPreferences.setCapsulesStatus(getContext(), true);
            getCapsules();
        });

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        if (ScreenUtils.isPortraitMode(getActivityCast())) {
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            binding.capsulesRecyclerView.setLayoutManager(mLinearLayoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    binding.capsulesRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            binding.capsulesRecyclerView.addItemDecoration(mDividerItemDecoration);
        } else {
            int columnCount = getResources().getInteger(R.integer.mission_list_column_count);
            mStaggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            binding.capsulesRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        }

        binding.capsulesRecyclerView.setHasFixedSize(false);
        mAdapter = new CapsulesAdapter(getContext(), this);
        binding.capsulesRecyclerView.setAdapter(mAdapter);
    }

    private SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    private void restorePosition() {
        if (mCapsulesPosition == RecyclerView.NO_POSITION) mCapsulesPosition = 0;
        // Scroll the RecyclerView to mPosition
        binding.capsulesRecyclerView.scrollToPosition(mCapsulesPosition);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Save RecyclerView state
        if (mLinearLayoutManager != null) {
            mCapsulesPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        } else if (mStaggeredGridLayoutManager != null) {
            mCapsulesPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(mStaggeredPosition)[0];
        }

        outState.putInt(CAPSULE_RECYCLER_POSITION_KEY, mCapsulesPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCapsules();
    }

    // If capsules were already loaded once, just query the DB and display them,
    // otherwise get them from server
    private void getCapsules() {
        // If a forced download is requested, getCoresStatus flag is true
        // and we download all landing pads from server
        if (SpaceXPreferences.getCapsulesStatus(getActivityCast())) {
            // If there is a network connection
            if (NetworkUtils.isConnected(getActivityCast())) {
                //loadingStateUi();
                getCapsulesFromServer();
            } else {
                // Show connection error
                if (SpaceXPreferences.getCapsulesFirstLoad(getActivityCast())) {
                    errorStateUi(2);
                } else {
                    Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Otherwise, get them from DB
            getCapsulesFromDB();
        }
    }

    private void getCapsulesFromServer() {
        mViewModel.getCapsulesFromServer().observe(this, checkResultDisplay -> {
            if (checkResultDisplay != null) {
                switch (checkResultDisplay.state) {
                    case ResultDisplay.STATE_LOADING:
                        // Update UI
                        loadingStateUi();
                        break;
                    case ResultDisplay.STATE_ERROR:
                        // Update UI
                        binding.swipeRefreshLayout.setRefreshing(false);

                        // Show error message
                        if (SpaceXPreferences.getCoresFirstLoad(getActivityCast())) {
                            errorStateUi(1);
                        } else {
                            Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        binding.swipeRefreshLayout.setRefreshing(false);

                        List<Capsule> capsules = checkResultDisplay.data;

                        if (capsules != null && capsules.size() > 0) {
                            SpaceXPreferences.setCapsulesStatus(getContext(), false);
                            SpaceXPreferences.setCapsulesFirstLoad(getActivityCast(), false);
                            getCapsulesFromDB();
                        } else {
                            // Update UI
                            if (SpaceXPreferences.getCapsulesFirstLoad(getActivityCast())) {
                                errorStateUi(0);
                            } else {
                                Toast.makeText(getActivityCast(), getString(R.string.no_capsules_available), Toast.LENGTH_SHORT).show();
                            }
                        }

                        break;
                }
            }
        });
    }

    private void getCapsulesFromDB() {
        // Try loading data from DB, if no data was found show empty list
        mViewModel.getCapsulesFromDb().observe(this, capsules -> {
            if (capsules != null && capsules.size() > 0) {
                // Update UI
                successStateUi();
                // Show data
                mAdapter.setCapsules(capsules);
                restorePosition();
            } else {
                // Update UI
                errorStateUi(0);
            }
        });
    }

    private void loadingStateUi() {
        binding.capsulesRecyclerView.setVisibility(View.GONE);
        binding.noConnectionMessage.setVisibility(View.GONE);
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.specialErrorLayout.setVisibility(View.GONE);
    }

    private void errorStateUi(int errorType) {
        binding.capsulesRecyclerView.setVisibility(View.GONE);
        binding.loadingLayout.setVisibility(View.GONE);

        switch (errorType) {
            case 0:
                binding.specialErrorLayout.setVisibility(View.VISIBLE);
                binding.specialErrorMessage.setText(getString(R.string.no_capsules_available));
                binding.noConnectionMessage.setVisibility(View.GONE);
                break;
            case 1:
                binding.specialErrorLayout.setVisibility(View.VISIBLE);
                binding.specialErrorMessage.setText(getString(R.string.unknown_error));
                binding.noConnectionMessage.setVisibility(View.GONE);
                break;
            default:
                binding.specialErrorLayout.setVisibility(View.GONE);
                binding.noConnectionMessage.setVisibility(View.VISIBLE);
                binding.noConnectionMessage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_cloud_off, 0, 0);
                binding.noConnectionMessage.setText(getString(R.string.no_connection));
                break;
        }
    }

    private void successStateUi() {
        binding.capsulesRecyclerView.setVisibility(View.VISIBLE);
        binding.noConnectionMessage.setVisibility(View.GONE);
        binding.loadingLayout.setVisibility(View.GONE);
        binding.specialErrorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClickListener(String selectedCapsule) {
        Intent intent = new Intent(getActivity(), CapsuleDetailsActivity.class);
        intent.putExtra(CAPSULE_SERIAL_KEY, selectedCapsule);
        startActivity(intent);
    }
}
