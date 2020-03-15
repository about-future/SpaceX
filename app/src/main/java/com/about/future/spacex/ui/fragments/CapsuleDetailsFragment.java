package com.about.future.spacex.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.about.future.spacex.R;
import com.about.future.spacex.databinding.FragmentCapsuleDetailsBinding;
import com.about.future.spacex.model.rocket.Capsule;
import com.about.future.spacex.ui.CapsuleDetailsActivity;
import com.about.future.spacex.ui.MissionDetailsActivity;
import com.about.future.spacex.ui.adapters.MissionsAdapter;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import static com.about.future.spacex.utils.Constants.ACTIVE;
import static com.about.future.spacex.utils.Constants.DESTROYED;
import static com.about.future.spacex.utils.Constants.DRAGON1_BIG;
import static com.about.future.spacex.utils.Constants.DRAGON1_SMALL;
import static com.about.future.spacex.utils.Constants.DRAGON2_BIG;
import static com.about.future.spacex.utils.Constants.DRAGON2_SMALL;
import static com.about.future.spacex.utils.Constants.LOST;
import static com.about.future.spacex.utils.Constants.MISSION_NUMBER_KEY;
import static com.about.future.spacex.utils.Constants.RETIRED;

public class CapsuleDetailsFragment extends Fragment implements MissionsAdapter.ListItemClickListener {
    private Capsule mCapsule;
    private MissionsViewModel mViewModel;
    private MissionsAdapter mMissionsAdapter;
    private FragmentCapsuleDetailsBinding binding;

    public CapsuleDetailsFragment() { }

    private CapsuleDetailsActivity getActivityCast() { return (CapsuleDetailsActivity) getActivity(); }
    public void setCapsule(Capsule capsule) { mCapsule = capsule; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCapsuleDetailsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        binding.toolbar.setTitle("");
        getActivityCast().setSupportActionBar(binding.toolbar);
        bindViews(mCapsule);

        mViewModel = ViewModelProviders.of(this).get(MissionsViewModel.class);
        if (mCapsule != null) getMissions();

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            refreshData();
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void refreshData() {
        // If there is a network connection, refresh data
        if (NetworkUtils.isConnected(getActivityCast())) {
            // Refresh date from Server
            Toast.makeText(getActivityCast(), "Refresh", Toast.LENGTH_SHORT).show();
        } else {
            // Display connection error message
            Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void bindViews(Capsule capsule) {
        if (capsule != null) {
            binding.collapsingToolbarLayout.setTitle(capsule.getCapsuleSerial());
            binding.toolbar.setNavigationOnClickListener(view -> getActivityCast().onBackPressed());

            String backdropUrl;
            String thumbnailUrl;
            switch (capsule.getCapsuleId()) {
                case "dragon1":
                    backdropUrl = DRAGON1_BIG;
                    thumbnailUrl = DRAGON1_SMALL;
                    break;
                case "dragon2":
                    backdropUrl = DRAGON2_BIG;
                    thumbnailUrl = DRAGON2_SMALL;
                    break;
                default:
                    backdropUrl = DRAGON1_BIG; //TODO: Create default case
                    thumbnailUrl = DRAGON1_BIG;
                    break;
            }

            // Backdrop
            Picasso.get()
                    .load(backdropUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(binding.backdropImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Yay!
                        }

                        @Override
                        public void onError(Exception e) {
                            // Try again online, if cache loading failed
                            Picasso.get()
                                    .load(backdropUrl)
                                    .error(R.drawable.staticmap)
                                    .into(binding.backdropImage);
                        }
                    });

            // Thumbnail
            Picasso.get()
                    .load(thumbnailUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(binding.thumbnailImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Yay!
                        }

                        @Override
                        public void onError(Exception e) {
                            // Try again online, if cache loading failed
                            Picasso.get()
                                    .load(thumbnailUrl)
                                    .error(R.drawable.staticmap)
                                    .into(binding.thumbnailImage);
                        }
                    });

            binding.capsuleType.setText(capsule.getType());

            String status;
            switch (capsule.getStatus()) {
                case ACTIVE:
                    status = getString(R.string.status_active);
                    break;
                case RETIRED:
                    status = getString(R.string.status_retired);
                    break;
                case DESTROYED:
                    status = getString(R.string.status_destroyed);
                    break;
                case LOST:
                    status = getString(R.string.status_lost);
                    break;
                default:
                    status = getString(R.string.status_unknown);
                    break;
            }
            binding.capsuleStatus.setText(status);

            if (capsule.getOriginalLaunch() != null && !capsule.getOriginalLaunch().equals("")) {
                binding.originalLaunchDate.setText(DateUtils.changeDateFormat(capsule.getOriginalLaunch())); //TODO: FIX DATE
            } else {
                binding.originalLaunchDate.setText(getString(R.string.status_unknown));
            }

            if (capsule.getDetails() != null && !capsule.getDetails().equals("")) {
                binding.capsuleDetails.setVisibility(View.VISIBLE);
                binding.capsuleDetails.setText(capsule.getDetails());
            } else {
                binding.capsuleDetails.setVisibility(View.GONE);
            }

            binding.landings.setText(String.valueOf(capsule.getLandings()));
            binding.reuseCount.setText(String.valueOf(capsule.getReuseCount()));
        }
    }

    private void setupRecyclerView() {
        if (ScreenUtils.isPortraitMode(getActivityCast())) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            binding.missionsRecyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    binding.missionsRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            binding.missionsRecyclerView.addItemDecoration(mDividerItemDecoration);
        } else {
            int columnCount = getResources().getInteger(R.integer.mission_list_column_count);
            StaggeredGridLayoutManager staggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            binding.missionsRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        }

        binding.missionsRecyclerView.setHasFixedSize(true);
        mMissionsAdapter = new MissionsAdapter(getContext(), this);
        binding.missionsRecyclerView.setAdapter(mMissionsAdapter);
    }

    private void getMissions() {
        if (mCapsule.getMissions() != null && mCapsule.getMissions().size() > 0) {
            int[] flights = new int[mCapsule.getMissions().size()];
            for (int i = 0; i < mCapsule.getMissions().size(); i++) {
                flights[i] = mCapsule.getMissions().get(i).getFlight();
            }

            setupRecyclerView();

            // Try loading data from DB, if no data was found show empty list
            mViewModel.getMiniMissions(flights).observe(this, missions -> {
                Log.v("CAPSULE MISSIONS", "FROM DB!");
                if (missions != null && missions.size() > 0) {
                    //binding.missionsRecyclerView.setVisibility(View.VISIBLE);
                    mMissionsAdapter.setMissions(missions);
                }
            });
        } else {
            binding.missionsLabel.setVisibility(View.GONE);
            binding.separationLine2.setVisibility(View.GONE);
            binding.missionsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClickListener(int selectedMission) {
        Intent missionDetailsIntent = new Intent(getActivity(), MissionDetailsActivity.class);
        missionDetailsIntent.putExtra(MISSION_NUMBER_KEY, selectedMission);
        startActivity(missionDetailsIntent);
    }
}
