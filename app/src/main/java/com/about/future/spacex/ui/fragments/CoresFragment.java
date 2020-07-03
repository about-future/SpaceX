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
import com.about.future.spacex.databinding.FragmentCoresBinding;
import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.ui.CoreDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.ui.adapters.CoresAdapter;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.viewmodel.CoresViewModel;

import java.util.List;

import static com.about.future.spacex.utils.Constants.CORE_RECYCLER_POSITION_KEY;
import static com.about.future.spacex.utils.Constants.CORE_SERIAL_KEY;

public class CoresFragment extends Fragment implements CoresAdapter.ListItemClickListener {
    private int mCoresPosition = RecyclerView.NO_POSITION;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private CoresAdapter mAdapter;
    private CoresViewModel mViewModel;
    private FragmentCoresBinding binding;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(CORE_RECYCLER_POSITION_KEY)) {
            mCoresPosition = savedInstanceState.getInt(CORE_RECYCLER_POSITION_KEY);
        }

        binding = FragmentCoresBinding.inflate(inflater, container, false);
        mRootView = binding.getRoot();

        // Setup RecyclerView and Adaptor
        setupRecyclerView();
        // Init view model
        mViewModel = new ViewModelProvider(this).get(CoresViewModel.class);

        binding.swipeToRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeToRefreshLayout.setRefreshing(false);
            SpaceXPreferences.setCoresStatus(getContext(), true);
            getCores();
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
        mAdapter = new CoresAdapter(getContext(), this);
        binding.recyclerView.setAdapter(mAdapter);
    }

    private SpaceXActivity getActivityCast() {
        return (SpaceXActivity) getActivity();
    }

    private void restorePosition() {
        if (mCoresPosition == RecyclerView.NO_POSITION) mCoresPosition = 0;
        // Scroll the RecyclerView to mPosition
        binding.recyclerView.scrollToPosition(mCoresPosition);
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
        getCores();
    }

    // If cores were already loaded once, just query the DB and display them,
    // otherwise get them from server
    private void getCores() {
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
        mViewModel.getCoresFromServer().observe(this, checkResultDisplay -> {
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
                        if (SpaceXPreferences.getCoresFirstLoad(getActivityCast())) {
                            errorStateUi(1);
                        } else {
                            Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        binding.swipeToRefreshLayout.setRefreshing(false);

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
                                Toast.makeText(getActivityCast(), getString(R.string.no_cores_available), Toast.LENGTH_SHORT).show();
                            }
                        }

                        break;
                }
            }
        });
    }

    private void getCoresFromDB() {
        // Try loading data from DB, if no data was found show empty list
        mViewModel.getCoresFromDb().observe(this, cores -> {
            if (cores != null && cores.size() > 0) {
                // Update UI
                successStateUi();
                // Show data
                mAdapter.setCores(cores);
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
                binding.errorMessage.setText(getString(R.string.no_cores_available));
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
    public void onItemClickListener(String selectedCore) {
        Intent intent = new Intent(getActivity(), CoreDetailsActivity.class);
        intent.putExtra(CORE_SERIAL_KEY, selectedCore);
        startActivity(intent);
    }
}
