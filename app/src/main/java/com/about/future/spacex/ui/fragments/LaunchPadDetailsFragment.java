package com.about.future.spacex.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.databinding.FragmentLaunchPadDetailsBinding;
import com.about.future.spacex.model.pads.LaunchPad;
import com.about.future.spacex.model.rocket.RocketMini;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.TextsUtils;
import com.about.future.spacex.ui.LaunchPadDetailsActivity;
import com.about.future.spacex.viewmodel.RocketsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LaunchPadDetailsFragment extends Fragment {
    private LaunchPad mLaunchPad;
    private FragmentLaunchPadDetailsBinding binding;
    private String mRocketNames = "";

    public LaunchPadDetailsFragment() { }

    private LaunchPadDetailsActivity getActivityCast() { return (LaunchPadDetailsActivity) getActivity(); }
    public void setLaunchPad(LaunchPad launchPad) { mLaunchPad = launchPad; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLaunchPadDetailsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        binding.toolbar.setTitle("");
        getActivityCast().setSupportActionBar(binding.toolbar);
        bindViews(mLaunchPad);

        binding.swipeToRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeToRefreshLayout.setRefreshing(false);
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

    private void bindViews(LaunchPad launchPad) {
        if (launchPad != null) {
            binding.collapsingToolbarLayout.setTitle(launchPad.getFullName());
            binding.toolbar.setNavigationOnClickListener(view -> getActivityCast().onBackPressed());

            if (launchPad.getLatitude() != 0 && launchPad.getLongitude() != 0) {
                double latitude = launchPad.getLatitude();
                double longitude = launchPad.getLongitude();
                final String locationPadSatelliteImageUrl = ImageUtils.buildSatelliteBackdropUrl(latitude, longitude, 14, getActivityCast());
                //Log.v("SATELLITE URL", "IS: " + locationPadSatelliteImageUrl);

                // Set backdrop image as a satellite image of the launch pad
                if (launchPad.getImages() != null
                        && launchPad.getImages().getLargeImages() != null
                        && launchPad.getImages().getLargeImages().length > 0) {
                    for (String image : launchPad.getImages().getLargeImages()) {
                        if (image != null) {
                            Picasso.get()
                                    .load(image) // locationPadSatelliteImageUrl
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(binding.satelliteViewBackdrop, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            // Yay!
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            // Try again online, if cache loading failed
                                            Picasso.get()
                                                    .load(locationPadSatelliteImageUrl)
                                                    .error(R.drawable.staticmap)
                                                    .into(binding.satelliteViewBackdrop);
                                        }
                                    });
                            break;
                        }
                    }
                }

                // Set launch pad map
                final String locationPadMapImageUrl = ImageUtils.buildMapThumbnailUrl(
                        latitude, longitude, 8, "map", getActivityCast());
                //Log.v("MAP URL", "IS: " + locationPadSatelliteImageUrl);

                Picasso.get()
                        .load(locationPadMapImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(binding.launchPadMap, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Yay!
                            }

                            @Override
                            public void onError(Exception e) {
                                // Try again online, if cache loading failed
                                Picasso.get()
                                        .load(locationPadMapImageUrl)
                                        .error(R.drawable.empty_map)
                                        .into(binding.launchPadMap);
                            }
                        });

                // Launch pad location
                if (!TextUtils.isEmpty(launchPad.getName()) && !TextUtils.isEmpty(launchPad.getRegion())) {
                    binding.padLocation.setText(
                            String.format(getString(R.string.launch_pad_location),
                                    launchPad.getName(),
                                    launchPad.getRegion()));
                } else {
                    binding.padLocation.setText(getString(R.string.label_unknown));
                }

            } else {
                binding.satelliteViewBackdrop.setImageResource(R.drawable.staticmap);
                binding.launchPadMap.setImageResource(R.drawable.empty_map);
                binding.padLocation.setText(getString(R.string.label_unknown));
            }

            // Launch pad status
            if (!TextUtils.isEmpty(launchPad.getStatus())) {
                binding.launchPadStatus.setText(TextsUtils.firstLetterUpperCase(launchPad.getStatus()));
            } else {
                binding.launchPadStatus.setText(getString(R.string.label_unknown));
            }

            // Launched vehicles
            if (launchPad.getRockets() != null && launchPad.getRockets().length > 0) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    RocketsViewModel viewModel = new ViewModelProvider(getActivityCast()).get(RocketsViewModel.class);
                    List<RocketMini> rockets = viewModel.getMiniRocketsRaw();

                    if (rockets != null && rockets.size() > 0) {
                        for (String rocketId : launchPad.getRockets()) {
                            for (RocketMini rocket : rockets) {
                                if (rocketId.equals(rocket.getRocketId())) {
                                    mRocketNames = mRocketNames.concat(rocket.getName()).concat(", ");
                                    break;
                                }
                            }
                        }

                        AppExecutors.getInstance().mainThread().execute(() -> {
                            if (mRocketNames.length() > 0) {
                                binding.launchedVehicles.setText(mRocketNames.substring(0, mRocketNames.length() - 2));
                            } else {
                                binding.launchedVehicles.setText(getString(R.string.label_unknown));
                            }
                        });
                    } else {
                        binding.launchedVehicles.setText(getString(R.string.label_unknown));
                    }
                });
            } else {
                binding.launchedVehicles.setText(getString(R.string.label_unknown));
            }

            // Launch pad details
            if (!TextUtils.isEmpty(launchPad.getDetails())) {
                binding.launchPadDetails.setText(launchPad.getDetails());
            } else {
                binding.launchPadDetails.setVisibility(View.GONE);
            }
        }
    }
}
