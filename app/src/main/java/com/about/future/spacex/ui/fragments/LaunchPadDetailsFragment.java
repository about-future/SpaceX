package com.about.future.spacex.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.about.future.spacex.R;
import com.about.future.spacex.databinding.FragmentLaunchPadDetailsBinding;
import com.about.future.spacex.model.pads.LaunchPad;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.TextsUtils;
import com.about.future.spacex.ui.LaunchPadDetailsActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class LaunchPadDetailsFragment extends Fragment {
    private LaunchPad mLaunchPad;
    private FragmentLaunchPadDetailsBinding binding;

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

            if (launchPad.getLocation() != null) {
                double latitude = launchPad.getLocation().getLatitude();
                double longitude = launchPad.getLocation().getLongitude();
                final String locationPadSatelliteImageUrl = ImageUtils.buildSatelliteBackdropUrl(latitude, longitude, 14, getActivityCast());
                Log.v("SATELLITE URL", "IS: " + locationPadSatelliteImageUrl);

                // Set backdrop image as a satellite image of the launch pad
                Picasso.get()
                        .load(locationPadSatelliteImageUrl)
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

                // Set launch pad map
                final String locationPadMapImageUrl = ImageUtils.buildMapThumbnailUrl(
                        latitude, longitude, 8, "map", getActivityCast());
                Log.v("MAP URL", "IS: " + locationPadSatelliteImageUrl);

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
                if (!TextUtils.isEmpty(launchPad.getLocation().getName()) &&
                        !TextUtils.isEmpty(launchPad.getLocation().getRegion())) {
                    binding.padLocation.setText(
                            String.format(getString(R.string.launch_pad_location),
                                    launchPad.getLocation().getName(),
                                    launchPad.getLocation().getRegion()));
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
            if (launchPad.getVehiclesLaunched().length != 0) {
                binding.launchedVehicles.setText(TextUtils.join(", ", launchPad.getVehiclesLaunched()));
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
