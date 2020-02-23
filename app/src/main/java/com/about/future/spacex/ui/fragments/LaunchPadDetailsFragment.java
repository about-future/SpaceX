package com.about.future.spacex.ui.fragments;

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
//import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.about.future.spacex.R;
import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.TextsUtils;
import com.about.future.spacex.ui.LaunchPadDetailsActivity;
//import com.about.future.spacex.viewmodel.LaunchPadsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.LAUNCH_PAD_ID_KEY;

public class LaunchPadDetailsFragment extends Fragment {
    private LaunchPad mLaunchPad;
    private int mLaunchPadId;
    private View mRootView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.launch_pad_satellite_view)
    ImageView mLaunchPadSatelliteImageView;
    @BindView(R.id.launch_pad_location)
    TextView mLaunchPadLocationTextView;
    @BindView(R.id.launch_pad_status)
    TextView mLaunchPadStatusTextView;
    @BindView(R.id.launch_pad_map)
    ImageView mLaunchPadMapImageView;
    @BindView(R.id.launched_vehicles)
    TextView mLaunchedVehiclesTextView;
    @BindView(R.id.launch_pad_details)
    TextView mLaunchPadDetailsTextView;

    public LaunchPadDetailsFragment() {
        // Required empty public constructor
    }

    public static LaunchPadDetailsFragment newInstance(int launchPadId) {
        LaunchPadDetailsFragment fragment = new LaunchPadDetailsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(LAUNCH_PAD_ID_KEY, launchPadId);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(LAUNCH_PAD_ID_KEY)) {
            mLaunchPadId = getArguments().getInt(LAUNCH_PAD_ID_KEY);
        }
        setHasOptionsMenu(true);
    }

    private LaunchPadDetailsActivity getActivityCast() { return (LaunchPadDetailsActivity) getActivity(); }
    public void setLaunchPad(LaunchPad launchPad) { mLaunchPad = launchPad; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLaunchPadId = savedInstanceState.getInt(LAUNCH_PAD_ID_KEY);
        }

        mRootView = inflater.inflate(R.layout.fragment_launch_pad_details, container, false);
        ButterKnife.bind(this, mRootView);

        mToolbar.setTitle("");
        getActivityCast().setSupportActionBar(mToolbar);
        bindViews(mLaunchPad);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            refreshData();
        });

        return mRootView;
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
        outState.putInt(LAUNCH_PAD_ID_KEY, mLaunchPadId);
    }

    private void bindViews(LaunchPad launchPad) {
        if (mRootView == null) {
            return;
        }

        if (launchPad != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            mCollapsingToolbarLayout.setTitle(launchPad.getFullName());
            mToolbar.setNavigationOnClickListener(view -> getActivityCast().onBackPressed());

            if (launchPad.getLocation() != null) {
                double latitude = launchPad.getLocation().getLatitude();
                double longitude = launchPad.getLocation().getLongitude();
                final String locationPadSatelliteImageUrl = ImageUtils.buildSatelliteBackdropUrl(latitude, longitude, getActivityCast());
                Log.v("SATELLITE URL", "IS: " + locationPadSatelliteImageUrl);

                // Set backdrop image as a satellite image of the launch pad
                Picasso.get()
                        .load(locationPadSatelliteImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mLaunchPadSatelliteImageView, new Callback() {
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
                                        .into(mLaunchPadSatelliteImageView);
                            }
                        });

                // Set launch pad map
                final String locationPadMapImageUrl = ImageUtils.buildMapThumbnailUrl(
                        latitude, longitude, 8, "map", getActivityCast());
                Log.v("MAP URL", "IS: " + locationPadSatelliteImageUrl);

                Picasso.get()
                        .load(locationPadMapImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mLaunchPadMapImageView, new Callback() {
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
                                        .into(mLaunchPadMapImageView);
                            }
                        });

                // Launch pad location
                if (!TextUtils.isEmpty(launchPad.getLocation().getName()) &&
                        !TextUtils.isEmpty(launchPad.getLocation().getRegion())) {
                    mLaunchPadLocationTextView.setText(
                            String.format(getString(R.string.launch_pad_location),
                                    launchPad.getLocation().getName(),
                                    launchPad.getLocation().getRegion()));
                } else {
                    mLaunchPadLocationTextView.setText(getString(R.string.label_unknown));
                }

            } else {
                mLaunchPadSatelliteImageView.setImageResource(R.drawable.staticmap);
                mLaunchPadMapImageView.setImageResource(R.drawable.empty_map);
                mLaunchPadLocationTextView.setText(getString(R.string.label_unknown));
            }

            // Launch pad status
            if (!TextUtils.isEmpty(launchPad.getStatus())) {
                mLaunchPadStatusTextView.setText(TextsUtils.firstLetterUpperCase(launchPad.getStatus()));
            } else {
                mLaunchPadStatusTextView.setText(getString(R.string.label_unknown));
            }

            // Launched vehicles
            if (launchPad.getVehiclesLaunched().length != 0) {
                mLaunchedVehiclesTextView.setText(TextUtils.join(", ", launchPad.getVehiclesLaunched()));
            } else {
                mLaunchPadStatusTextView.setText(getString(R.string.label_unknown));
            }

            // Launch pad details
            if (!TextUtils.isEmpty(launchPad.getDetails())) {
                mLaunchPadDetailsTextView.setText(launchPad.getDetails());
            } else {
                mLaunchPadDetailsTextView.setVisibility(View.GONE);
            }
        }
    }
}
