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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.about.future.spacex.R;
import com.about.future.spacex.databinding.FragmentLandingPadDetailsBinding;
import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.ui.LandingPadDetailsActivity;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.utils.TextsUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.JRTI_BIG;
import static com.about.future.spacex.utils.Constants.LANDING_PAD_ID_KEY;
import static com.about.future.spacex.utils.Constants.OCISLY_BIG;

public class LandingPadDetailsFragment extends Fragment {
    private LandingPad mLandingPad;
    private int mLandingPadId;
    private View mRootView;

    /*@BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.landing_pad_view)
    ImageView mLandingPadSatelliteImageView;
    @BindView(R.id.landing_pad_map)
    ImageView mLandingPadMapImageView;
    @BindView(R.id.pad_location)
    TextView mLandingPadLocationTextView;
    @BindView(R.id.landing_pad_status)
    TextView mLandingPadStatusTextView;
    @BindView(R.id.landing_type)
    TextView mLandingTypeTextView;
    @BindView(R.id.landing_pad_details)
    TextView mLandingPadDetailsTextView;
    @BindView(R.id.landing_pad_wiki)
    TextView mWikiPageTextView;
    @BindView(R.id.attempted_landings)
    TextView mAttemptedLandingsTextView;
    @BindView(R.id.successful_landings)
    TextView mSuccessfulLandingsTextView;*/

    FragmentLandingPadDetailsBinding binding;

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

        //mRootView = inflater.inflate(R.layout.fragment_landing_pad_details, container, false);
        //ButterKnife.bind(this, mRootView);

        /*mToolbar.setTitle("");
        getActivityCast().setSupportActionBar(mToolbar);
        bindViews(mLandingPad);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            refreshData();
        });*/

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
                        break;
                    case "JRTI":
                        locationPadSatelliteImageUrl = JRTI_BIG;
                        break;
                    default:
                        locationPadSatelliteImageUrl = ImageUtils.buildSatelliteBackdropUrl(latitude, longitude, 16, getActivityCast());

                }

                Log.v("SATELLITE URL", "IS: " + locationPadSatelliteImageUrl);

                Picasso.get()
                        .load(locationPadSatelliteImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mLandingPadSatelliteImageView, new Callback() {
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
                                        .into(mLandingPadSatelliteImageView);
                            }
                        });

                // Set landing pad map
                final String locationPadMapImageUrl = ImageUtils.buildMapThumbnailUrl(
                        latitude, longitude, 8, "map", getActivityCast());
                Log.v("MAP URL", "IS: " + locationPadSatelliteImageUrl);

                Picasso.get()
                        .load(locationPadMapImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mLandingPadMapImageView, new Callback() {
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
                                        .into(mLandingPadMapImageView);
                            }
                        });

                // Landing pad location
                if (!TextUtils.isEmpty(landingPad.getLocation().getName()) &&
                        !TextUtils.isEmpty(landingPad.getLocation().getRegion())) {
                    mLandingPadLocationTextView.setText(
                            String.format(getString(R.string.landing_pad_location),
                                    landingPad.getLocation().getName(),
                                    landingPad.getLocation().getRegion()));
                } else {
                    mLandingPadLocationTextView.setText(getString(R.string.label_unknown));
                }

            } else {
                mLandingPadSatelliteImageView.setImageResource(R.drawable.staticmap);
                mLandingPadMapImageView.setImageResource(R.drawable.empty_map);
                mLandingPadLocationTextView.setText(getString(R.string.label_unknown));
            }

            // Landing pad status
            if (!TextUtils.isEmpty(landingPad.getStatus())) {
                mLandingPadStatusTextView.setText(TextsUtils.firstLetterUpperCase(landingPad.getStatus()));
            } else {
                mLandingPadStatusTextView.setText(getString(R.string.label_unknown));
            }

            // Landing Type
            if (SpaceXPreferences.getAcronymsStatus(getActivityCast())) {
                if (landingPad.getLandingType().equals("ASDS")) {
                    mLandingTypeTextView.setText(getString(R.string.asds));
                } else {
                    mLandingTypeTextView.setText(getString(R.string.rtls));
                }
            } else {
                mLandingTypeTextView.setText(landingPad.getLandingType().toUpperCase());
            }

            // Landing Stats
            mAttemptedLandingsTextView.setText(String.valueOf(landingPad.getAttemptedLandings()));
            mSuccessfulLandingsTextView.setText(String.valueOf(landingPad.getSuccessfulLandings()));

            // Landing pad details
            if (!TextUtils.isEmpty(landingPad.getDetails())) {
                mLandingPadDetailsTextView.setText(landingPad.getDetails());
            } else {
                mLandingPadDetailsTextView.setVisibility(View.GONE);
            }

            // Wiki Page
            if (!TextUtils.isEmpty(landingPad.getWikipedia())) {
                mWikiPageTextView.setVisibility(View.VISIBLE);
                mWikiPageTextView.setOnClickListener(view -> {
                    Uri webpage = Uri.parse(landingPad.getWikipedia());
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getActivityCast().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                });
            } else {
                mWikiPageTextView.setVisibility(View.GONE);
            }
        }
    }
}
