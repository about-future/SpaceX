package com.about.future.spacex.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.LaunchPadLoader;
import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.utils.TextsUtils;
import com.about.future.spacex.viewmodel.LaunchPadViewModel;
import com.about.future.spacex.viewmodel.LaunchPadViewModelFactory;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.view.LaunchPadsFragment.LAUNCH_PAD_ID_KEY;


public class LaunchPadDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<LaunchPad> {

    private static final int LAUNCH_PAD_LOADER_ID = 917746;

    private AppDatabase mDb;
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
        mDb = AppDatabase.getInstance(getContext());

        if (getArguments() != null && getArguments().containsKey(LAUNCH_PAD_ID_KEY)) {
            mLaunchPadId = getArguments().getInt(LAUNCH_PAD_ID_KEY);
        }
        setHasOptionsMenu(true);
    }

    public LaunchPadDetailsActivity getActivityCast() {
        return (LaunchPadDetailsActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLaunchPadId = savedInstanceState.getInt(LAUNCH_PAD_ID_KEY);
        }

        mRootView = inflater.inflate(R.layout.fragment_launch_pad_details, container, false);
        ButterKnife.bind(this, mRootView);

        mToolbar.setTitle("");
        getActivityCast().setSupportActionBar(mToolbar);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        LaunchPadViewModelFactory factory = new LaunchPadViewModelFactory(mDb, mLaunchPadId);
        final LaunchPadViewModel viewModel = ViewModelProviders.of(this, factory).get(LaunchPadViewModel.class);
        viewModel.getLaunchPadLiveData().observe(this, new Observer<LaunchPad>() {
            @Override
            public void onChanged(@Nullable LaunchPad launchPad) {
                bindViews(launchPad);
                mLaunchPad = launchPad;
            }
        });

        return mRootView;
    }

    private void refreshData() {
        // If there is a network connection, refresh data
        if (NetworkUtils.isConnected(getActivityCast())) {
            //Init launch pad loader
            getLoaderManager().initLoader(LAUNCH_PAD_LOADER_ID, null, this);
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
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivityCast().onBackPressed();
                }
            });

            if (launchPad.getLocation() != null) {
                double latitude = launchPad.getLocation().getLatitude();
                double longitude = launchPad.getLocation().getLongitude();
                final String locationPadSatelliteImageUrl = ImageUtils.buildSatelliteBackdropUrl(latitude, longitude);

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
                        latitude, longitude, 8, "map");
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

    @NonNull
    @Override
    public Loader<LaunchPad> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case LAUNCH_PAD_LOADER_ID:
                // If the loaded id matches launch pad loader, return a new launch pad loader
                return new LaunchPadLoader(getActivityCast(), mLaunchPad.getId());
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<LaunchPad> loader, final LaunchPad launchPad) {
        if (launchPad != null) {

            String launchPadAsString1 = new Gson().toJson(launchPad);
            String launchPadAsString2 = new Gson().toJson(mLaunchPad);

            // If the content of the two launch pads is different, update the DB
            if (!launchPadAsString1.equals(launchPadAsString2)) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.launchPadDao().updateLaunchPad(launchPad);
                    }
                });
                ScreenUtils.snakBarThis(mRootView, getString(R.string.launch_pad_updated));
            } else {
                ScreenUtils.snakBarThis(mRootView, getString(R.string.launch_pad_up_to_date));
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<LaunchPad> loader) {
        mLaunchPad = null;
    }
}
