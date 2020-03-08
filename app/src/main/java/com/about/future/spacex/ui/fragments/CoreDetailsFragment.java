package com.about.future.spacex.ui.fragments;

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
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.databinding.FragmentCoreDetailsBinding;
import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.model.mission.MissionMini;
import com.about.future.spacex.ui.CoreDetailsActivity;
import com.about.future.spacex.ui.adapters.MissionsAdapter;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.about.future.spacex.utils.Constants.ACTIVE;
import static com.about.future.spacex.utils.Constants.BLOCK3_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK3_SMALL;
import static com.about.future.spacex.utils.Constants.BLOCK5_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK5_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_SERIAL_KEY;
import static com.about.future.spacex.utils.Constants.DESTROYED;
import static com.about.future.spacex.utils.Constants.LOST;
import static com.about.future.spacex.utils.Constants.RETIRED;

public class CoreDetailsFragment extends Fragment implements MissionsAdapter.ListItemClickListener {
    private Core mCore;
    private String mCoreSerial;
    private View mRootView;

    private MissionsViewModel mViewModel;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private MissionsAdapter mMissionsAdapter;

    private FragmentCoreDetailsBinding binding;

    public CoreDetailsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(CORE_SERIAL_KEY)) {
            mCoreSerial = getArguments().getString(CORE_SERIAL_KEY);
        }
        setHasOptionsMenu(true);
    }

    private CoreDetailsActivity getActivityCast() { return (CoreDetailsActivity) getActivity(); }
    public void setCore(Core core) { mCore = core; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCoreSerial = savedInstanceState.getString(CORE_SERIAL_KEY);
        }

        binding = FragmentCoreDetailsBinding.inflate(inflater, container, false);
        mRootView = binding.getRoot();

        binding.toolbar.setTitle("");
        getActivityCast().setSupportActionBar(binding.toolbar);
        bindViews(mCore);

        mViewModel = ViewModelProviders.of(this).get(MissionsViewModel.class);
        getMissions();

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            refreshData();
        });

        return mRootView;
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(CORE_SERIAL_KEY, mCoreSerial);
    }

    private void bindViews(Core core) {
        if (mRootView == null) {
            return;
        }

        if (core != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            binding.collapsingToolbarLayout.setTitle(core.getCoreSerial());
            binding.toolbar.setNavigationOnClickListener(view -> getActivityCast().onBackPressed());

            String backdropUrl;
            String thumbnailUrl;
            switch (core.getBlock()) {
                case 3:
                    backdropUrl = BLOCK3_MEDIUM;
                    thumbnailUrl = BLOCK3_SMALL;
                    break;
                case 5:
                    backdropUrl = BLOCK5_MEDIUM;
                    thumbnailUrl = BLOCK5_SMALL;
                    break;
                default:
                    backdropUrl = BLOCK5_MEDIUM; //TODO: Create default case
                    thumbnailUrl = BLOCK5_MEDIUM;
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
                binding.originalLaunchDate.setText(core.getOriginalLaunch()); //TODO: FIX DATE
            } else {
                binding.originalLaunchDate.setText(getString(R.string.status_unknown));
            }

            if (core.getDetails() != null && !core.getDetails().equals("")) {
                binding.coreDetails.setVisibility(View.VISIBLE);
                binding.coreDetails.setText(core.getDetails());
            } else {
                binding.coreDetails.setVisibility(View.GONE);
            }

            //TODO: init RV and Adapter, show missions

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
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            binding.missionsRecyclerView.setLayoutManager(mLinearLayoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    binding.missionsRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            binding.missionsRecyclerView.addItemDecoration(mDividerItemDecoration);
        } else {
            int columnCount = getResources().getInteger(R.integer.mission_list_column_count);
            mStaggeredGridLayoutManager =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            binding.missionsRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        }

        binding.missionsRecyclerView.setHasFixedSize(false);  //TODO: Maybe it has to be true
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
            AppExecutors.getInstance().diskIO().execute(() -> {
                List<MissionMini> missions = mViewModel.getMiniMissions(flights);
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
        //TODO: Open selected mission
    }
}
