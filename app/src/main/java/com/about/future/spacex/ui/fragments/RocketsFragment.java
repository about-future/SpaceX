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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.about.future.spacex.R;
import com.about.future.spacex.databinding.FragmentRocketsBinding;
import com.about.future.spacex.ui.adapters.RocketsAdapter;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ResultDisplay;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.ui.RocketDetailsActivity;
import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.viewmodel.RocketsViewModel;

import java.util.List;

import static com.about.future.spacex.utils.Constants.ROCKET_ID_KEY;

public class RocketsFragment extends Fragment implements RocketsAdapter.ListItemClickListener {
    private RocketsAdapter mRocketsAdapter;
    private RocketsViewModel mViewModel;
    private FragmentRocketsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRocketsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Setup RecyclerView and Adaptor
        setupRecyclerView();
        // Init view model
        mViewModel = new ViewModelProvider(this).get(RocketsViewModel.class);

        binding.swipeToRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeToRefreshLayout.setRefreshing(false);
            SpaceXPreferences.setRocketsStatus(getContext(), true);
            getRockets();
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        int columnCount = getResources().getInteger(R.integer.rocket_list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(sglm);
        binding.recyclerView.setHasFixedSize(false);
        mRocketsAdapter = new RocketsAdapter(getContext(), this);
        binding.recyclerView.setAdapter(mRocketsAdapter);
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
        mViewModel.getRocketsFromServer().observe(this, checkResultDisplay -> {
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
                        if (SpaceXPreferences.getRocketsFirstLoad(getActivityCast())) {
                            errorStateUi(1);
                        } else {
                            Toast.makeText(getActivityCast(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ResultDisplay.STATE_SUCCESS:
                        binding.swipeToRefreshLayout.setRefreshing(false);

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
                binding.errorMessage.setText(getString(R.string.no_rocket_available));
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
    public void onItemClickListener(String selectedRocket) {
        Intent rocketDetailsIntent = new Intent(getActivity(), RocketDetailsActivity.class);
        rocketDetailsIntent.putExtra(ROCKET_ID_KEY, selectedRocket);
        startActivity(rocketDetailsIntent);
    }
}
