package com.about.future.spacex.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.about.future.spacex.databinding.FragmentCoreDetailsBinding;
import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.ui.CoreDetailsActivity;
import com.about.future.spacex.ui.MissionDetailsActivity;
import com.about.future.spacex.ui.adapters.MissionsAdapter;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import static com.about.future.spacex.utils.Constants.ACTIVE;
import static com.about.future.spacex.utils.Constants.DESTROYED;
import static com.about.future.spacex.utils.Constants.LOST;
import static com.about.future.spacex.utils.Constants.MISSION_NUMBER_KEY;
import static com.about.future.spacex.utils.Constants.RETIRED;

public class CoreDetailsFragment extends Fragment implements MissionsAdapter.ListItemClickListener {
    private Core mCore;
    private MissionsViewModel mViewModel;
    private MissionsAdapter mMissionsAdapter;
    private FragmentCoreDetailsBinding binding;

    public CoreDetailsFragment() { }

    private CoreDetailsActivity getActivityCast() { return (CoreDetailsActivity) getActivity(); }
    public void setCore(Core core) { mCore = core; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoreDetailsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        binding.toolbar.setTitle("");
        getActivityCast().setSupportActionBar(binding.toolbar);
        bindViews(mCore);

        mViewModel = ViewModelProviders.of(this).get(MissionsViewModel.class);
        if (mCore != null) getMissions();

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

    private void bindViews(Core core) {
        if (core != null) {
            binding.collapsingToolbarLayout.setTitle(core.getCoreSerial());
            binding.toolbar.setNavigationOnClickListener(view -> getActivityCast().onBackPressed());

            String backdropUrl = ImageUtils.getBackdropPath(getActivityCast(), core.getBlock(), core.getCoreSerial());
            String thumbnailUrl = ImageUtils.getMediumPath(getActivityCast(), core.getBlock(), core.getCoreSerial());

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

            if (core.getBlock() > 0) {
                binding.blockType.setText(String.format(getString(R.string.format_block_type), String.valueOf(core.getBlock())));
            } else {
                binding.blockType.setText(getString(R.string.label_unknown));
            }

            String status;
            switch (core.getStatus()) {
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
            binding.coreStatus.setText(status);

            if (core.getOriginalLaunch() != null && !core.getOriginalLaunch().equals("")) {
                String date = DateUtils.changeDateFormat(core.getOriginalLaunch());

                // Init calendar, set original launch date to it
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(DateUtils.stringDateToLong(date));

                // Calculate Offset (this includes DayTimeSavings, if existent) and add it to UTC date
                int offset = calendar.getTimeZone().getOffset(core.getOriginalLaunchUnix() * 1000);
                // Alternative calculation
                int offset2 = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
                calendar.add(Calendar.MILLISECOND, offset);

                binding.originalLaunchDate.setText(DateUtils.formatDate(getActivityCast(), calendar.getTime()));
            } else {
                binding.originalLaunchDate.setText(getString(R.string.status_unknown));
            }

            if (core.getDetails() != null && !core.getDetails().equals("")) {
                binding.coreDetails.setVisibility(View.VISIBLE);
                binding.coreDetails.setText(core.getDetails());
            } else {
                binding.coreDetails.setVisibility(View.GONE);
            }

            binding.rtlsLandings.setText(String.format(
                    getString(R.string.format_attempts_landings),
                    String.valueOf(core.getRtlsAttempts()),
                    String.valueOf(core.getRtlsLandings()))
            );

            binding.asdsLandings.setText(String.format(
                    getString(R.string.format_attempts_landings),
                    String.valueOf(core.getAsdsAttempts()),
                    String.valueOf(core.getAsdsLandings()))
            );

            if (core.isWaterLanding()) {
                binding.waterLandings.setText(getString(R.string.label_true));
            } else {
                binding.waterLandings.setText(getString(R.string.label_false));
            }

            binding.reuseCount.setText(String.valueOf(core.getReuseCount()));
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
        if (mCore.getMissions() != null && mCore.getMissions().size() > 0) {
            int[] flights = new int[mCore.getMissions().size()];
            for (int i = 0; i < mCore.getMissions().size(); i++) {
                flights[i] = mCore.getMissions().get(i).getFlight();
            }

            setupRecyclerView();

            // Try loading data from DB, if no data was found show empty list
            mViewModel.getMiniMissions(flights).observe(this, missions -> {
                if (missions != null && missions.size() > 0) {
                    binding.missionsRecyclerView.setVisibility(View.VISIBLE);
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
