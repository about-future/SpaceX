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
import com.about.future.spacex.databinding.FragmentCapsuleDetailsBinding;
import com.about.future.spacex.model.mission.MissionMini;
import com.about.future.spacex.model.rocket.Capsule;
import com.about.future.spacex.ui.CapsuleDetailsActivity;
import com.about.future.spacex.ui.adapters.MissionsAdapter;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.about.future.spacex.utils.Constants.ACTIVE;
import static com.about.future.spacex.utils.Constants.CAPSULE_SERIAL_KEY;
import static com.about.future.spacex.utils.Constants.DESTROYED;
import static com.about.future.spacex.utils.Constants.DRAGON1_MEDIUM;
import static com.about.future.spacex.utils.Constants.DRAGON1_SMALL;
import static com.about.future.spacex.utils.Constants.DRAGON2_MEDIUM;
import static com.about.future.spacex.utils.Constants.DRAGON2_SMALL;
import static com.about.future.spacex.utils.Constants.LOST;
import static com.about.future.spacex.utils.Constants.RETIRED;

public class CapsuleDetailsFragment extends Fragment implements MissionsAdapter.ListItemClickListener {
    private Capsule mCapsule;
    private String mCapsuleSerial;
    private View mRootView;

    private MissionsViewModel mViewModel;
    private int[] mStaggeredPosition;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private MissionsAdapter mMissionsAdapter;

    private FragmentCapsuleDetailsBinding binding;

    public CapsuleDetailsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(CAPSULE_SERIAL_KEY)) {
            mCapsuleSerial = getArguments().getString(CAPSULE_SERIAL_KEY);
        }
        setHasOptionsMenu(true);
    }

    private CapsuleDetailsActivity getActivityCast() { return (CapsuleDetailsActivity) getActivity(); }
    public void setCapsule(Capsule capsule) { mCapsule = capsule; }

    //TODO: get capsule from ViewModel using it's serial number

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCapsuleSerial = savedInstanceState.getString(CAPSULE_SERIAL_KEY);
        }

        binding = FragmentCapsuleDetailsBinding.inflate(inflater, container, false);
        mRootView = binding.getRoot();

        binding.toolbar.setTitle("");
        getActivityCast().setSupportActionBar(binding.toolbar);
        bindViews(mCapsule);

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
        outState.putString(CAPSULE_SERIAL_KEY, mCapsuleSerial);
    }

    private void bindViews(Capsule capsule) {
        if (mRootView == null) {
            return;
        }

        if (capsule != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            binding.collapsingToolbarLayout.setTitle(capsule.getCapsuleSerial());
            binding.toolbar.setNavigationOnClickListener(view -> getActivityCast().onBackPressed());

            String backdropUrl;
            String thumbnailUrl;
            switch (capsule.getCapsuleId()) {
                case "dragon1":
                    backdropUrl = DRAGON1_MEDIUM;
                    thumbnailUrl = DRAGON1_SMALL;
                    break;
                case "dragon2":
                    backdropUrl = DRAGON2_MEDIUM;
                    thumbnailUrl = DRAGON2_SMALL;
                    break;
                default:
                    backdropUrl = DRAGON1_MEDIUM; //TODO: Create default case
                    thumbnailUrl = DRAGON1_MEDIUM;
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
                binding.originalLaunchDate.setText(capsule.getOriginalLaunch()); //TODO: FIX DATE
            } else {
                binding.originalLaunchDate.setText(getString(R.string.status_unknown));
            }

            if (capsule.getDetails() != null && !capsule.getDetails().equals("")) {
                binding.capsuleDetails.setVisibility(View.VISIBLE);
                binding.capsuleDetails.setText(capsule.getDetails());
            } else {
                binding.capsuleDetails.setVisibility(View.GONE);
            }

            //TODO: init RV and Adapter, show missions

            binding.landings.setText(String.valueOf(capsule.getLandings()));
            binding.reuseCount.setText(String.valueOf(capsule.getReuseCount()));
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
        if (mCapsule.getMissions() != null && mCapsule.getMissions().size() > 0) {
            int[] flights = new int[mCapsule.getMissions().size()];
            for (int i = 0; i < mCapsule.getMissions().size(); i++) {
                flights[i] = mCapsule.getMissions().get(i).getFlight();
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
