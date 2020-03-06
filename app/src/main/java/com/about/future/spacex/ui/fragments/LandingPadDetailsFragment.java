package com.about.future.spacex.ui.fragments;

import android.content.Intent;
import android.net.Uri;
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
import com.about.future.spacex.databinding.FragmentLandingPadDetailsBinding;
import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.ui.LandingPadDetailsActivity;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.utils.TextsUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static com.about.future.spacex.utils.Constants.JRTI_BIG;
import static com.about.future.spacex.utils.Constants.LANDING_PAD_ID_KEY;
import static com.about.future.spacex.utils.Constants.OCISLY_BIG;

public class LandingPadDetailsFragment extends Fragment {
    private LandingPad mLandingPad;
    private int mLandingPadId;
    private View mRootView;

    private FragmentLandingPadDetailsBinding binding;

    public LandingPadDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(LANDING_PAD_ID_KEY)) {
            mLandingPadId = getArguments().getInt(LANDING_PAD_ID_KEY);
        }
        setHasOptionsMenu(true);
    }

    private LandingPadDetailsActivity getActivityCast() { return (LandingPadDetailsActivity) getActivity(); }
    public void setLandingPad(LandingPad landingPad) { mLandingPad = landingPad; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLandingPadId = savedInstanceState.getInt(LANDING_PAD_ID_KEY);
        }

        binding = FragmentLandingPadDetailsBinding.inflate(inflater, container, false);
        mRootView = binding.getRoot();

        binding.toolbar.setTitle("");
        getActivityCast().setSupportActionBar(binding.toolbar);
        bindViews(mLandingPad);

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
        outState.putInt(LANDING_PAD_ID_KEY, mLandingPadId);
    }

    private void bindViews(LandingPad landingPad) {
        if (mRootView == null) {
            return;
        }

        if (landingPad != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            binding.collapsingToolbarLayout.setTitle(landingPad.getFullName());
            binding.toolbar.setNavigationOnClickListener(view -> getActivityCast().onBackPressed());

            if (landingPad.getLocation() != null) {
                double latitude = landingPad.getLocation().getLatitude();
                double longitude = landingPad.getLocation().getLongitude();
                String locationPadSatelliteImageUrl; //= ImageUtils.buildSatelliteBackdropUrl(latitude, longitude, 14, getActivityCast());

                // Set backdrop image as a satellite image of the landing pad or an official SpaceX image
                switch (landingPad.getId()) {
                    case "OCISLY":
                        locationPadSatelliteImageUrl = OCISLY_BIG;
                        binding.googleText.setVisibility(View.GONE);
                        binding.googleTextShadow.setVisibility(View.GONE);
                        break;
                    case "JRTI":
                        locationPadSatelliteImageUrl = JRTI_BIG;
                        binding.googleText.setVisibility(View.GONE);
                        binding.googleTextShadow.setVisibility(View.GONE);
                        break;
                    default:
                        locationPadSatelliteImageUrl = ImageUtils.buildSatelliteBackdropUrl(latitude, longitude, 16, getActivityCast());
                        binding.googleText.setVisibility(View.VISIBLE);
                        binding.googleTextShadow.setVisibility(View.VISIBLE);
                }

                Log.v("SATELLITE URL", "IS: " + locationPadSatelliteImageUrl);

                Picasso.get()
                        .load(locationPadSatelliteImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(binding.landingPadBackdrop, new Callback() {
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
                                        .into(binding.landingPadBackdrop);
                            }
                        });

                // Set landing pad map
                final String locationPadMapImageUrl = ImageUtils.buildMapThumbnailUrl(
                        latitude, longitude, 8, "map", getActivityCast());
                Log.v("MAP URL", "IS: " + locationPadSatelliteImageUrl);

                Picasso.get()
                        .load(locationPadMapImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(binding.landingPadMap, new Callback() {
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
                                        .into(binding.landingPadMap);
                            }
                        });

                // Landing pad location
                if (!TextUtils.isEmpty(landingPad.getLocation().getName()) &&
                        !TextUtils.isEmpty(landingPad.getLocation().getRegion())) {
                    binding.padLocation.setText(
                            String.format(getString(R.string.landing_pad_location),
                                    landingPad.getLocation().getName(),
                                    landingPad.getLocation().getRegion()));
                } else {
                    binding.padLocation.setText(getString(R.string.label_unknown));
                }

            } else {
                binding.landingPadBackdrop.setImageResource(R.drawable.staticmap);
                binding.landingPadMap.setImageResource(R.drawable.empty_map);
                binding.padLocation.setText(getString(R.string.label_unknown));
            }

            // Landing pad status
            if (!TextUtils.isEmpty(landingPad.getStatus())) {
                binding.landingPadStatus.setText(TextsUtils.firstLetterUpperCase(landingPad.getStatus()));
            } else {
                binding.landingPadStatus.setText(getString(R.string.label_unknown));
            }

            // Landing Type
            if (SpaceXPreferences.getAcronymsStatus(getActivityCast())) {
                if (landingPad.getLandingType().equals("ASDS")) {
                    binding.landingType.setText(getString(R.string.asds));
                } else {
                    binding.landingType.setText(getString(R.string.rtls));
                }
            } else {
                binding.landingType.setText(landingPad.getLandingType().toUpperCase());
            }

            // Landing Stats
            binding.attemptedLandings.setText(String.valueOf(landingPad.getAttemptedLandings()));
            binding.successfulLandings.setText(String.valueOf(landingPad.getSuccessfulLandings()));

            // Landing pad details
            if (!TextUtils.isEmpty(landingPad.getDetails())) {
                binding.landingPadDetails.setText(landingPad.getDetails());
            } else {
                binding.landingPadDetails.setVisibility(View.GONE);
            }

            // Wiki Page
            if (!TextUtils.isEmpty(landingPad.getWikipedia())) {
                binding.landingPadWiki.setVisibility(View.VISIBLE);
                binding.landingPadWiki.setOnClickListener(view -> {
                    Uri webpage = Uri.parse(landingPad.getWikipedia());
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getActivityCast().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                });
            } else {
                binding.landingPadWiki.setVisibility(View.GONE);
            }
        }
    }
}
