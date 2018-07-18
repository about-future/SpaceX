package com.about.future.spacex;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.future.spacex.mission.Core;
import com.about.future.spacex.mission.Payload;
import com.about.future.spacex.data.MissionViewModel;
import com.about.future.spacex.data.MissionViewModelFactory;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.MissionLoader;
import com.about.future.spacex.mission.Mission;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.MissionsFragment.MISSION_NUMBER_KEY;

public class MissionDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Mission>> {

    private static final int MISSION_LOADER_ID = 89207;

    private AppDatabase mDb;
    private Mission mMission;
    private int mMissionNumber;
    private View mRootView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.webcast_preview)
    ImageView mWebcastPreviewImageView;
    @BindView(R.id.launch_date)
    TextView mLaunchDateTextView;
    @BindView(R.id.rocket_type)
    TextView mRocketTypeTextView;
    @BindView(R.id.mission_patch_large)
    ImageView mMissionPatchImageView;
    @BindView(R.id.launch_site_name)
    TextView mLaunchSiteNameTextView;
    @BindView(R.id.mission_details)
    TextView mDetailsTextView;
    // Payload details
    @BindView(R.id.payload_id)
    TextView mPayloadIdTextView;
    @BindView(R.id.payload_type)
    TextView mPayloadTypeTextView;
    @BindView(R.id.payload_mass)
    TextView mPayloadMassTextView;
    @BindView(R.id.payload_orbit)
    TextView mPayloadOrbitTextView;
    @BindView(R.id.payload_orbit_long)
    TextView mPayloadOrbitLongTextView;
    // Second stage details
    @BindView(R.id.second_stage_block)
    TextView mSecondStageBlockTextView;
    // First stage details
    @BindView(R.id.separation_line5)
    View mSeparationLine5View;
    @BindView(R.id.core_serial)
    TextView mCoreSerialTextView;
    @BindView(R.id.core_block)
    TextView mCoreBlockTextView;
    @BindView(R.id.core_reused)
    TextView mCoreReusedTextView;
    @BindView(R.id.core_landing)
    TextView mCoreLandingSuccessTextView;
    @BindView(R.id.core_landing_type)
    TextView mCoreLandingTypeTextView;
    @BindView(R.id.core_landing_vehicle)
    TextView mCoreLandingVehicleTextView;
    @BindView(R.id.core_landing_type_long)
    TextView mCoreLandingTypeLongTextView;
    @BindView(R.id.core_landing_vehicle_long)
    TextView mCoreLandingVehicleLongTextView;

    @BindView(R.id.rocket_core_image)
    ImageView mCoreImageView;
    @BindView(R.id.rocket_payload_image)
    ImageView mPayloadImageView;


    public MissionDetailsFragment() {
    }

    public static MissionDetailsFragment newInstance(int missionNumber) {
        Bundle arguments = new Bundle();
        arguments.putInt(MISSION_NUMBER_KEY, missionNumber);
        MissionDetailsFragment fragment = new MissionDetailsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = AppDatabase.getInstance(getContext());

        if (getArguments() != null && getArguments().containsKey(MISSION_NUMBER_KEY)) {
            mMissionNumber = getArguments().getInt(MISSION_NUMBER_KEY);
        }
        setHasOptionsMenu(true);
    }

    public MissionDetailsActivity getActivityCast() {
        return (MissionDetailsActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mMissionNumber = savedInstanceState.getInt(MISSION_NUMBER_KEY);
        }

        mRootView = inflater.inflate(R.layout.fragment_mission_details, container, false);
        ButterKnife.bind(this, mRootView);

        mToolbar.setTitle("");
        getActivityCast().setSupportActionBar(mToolbar);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Snackbar.make(mSwipeRefreshLayout, getString(R.string.mission_updating), Snackbar.LENGTH_SHORT).show();
                refreshData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        MissionViewModelFactory factory = new MissionViewModelFactory(mDb, mMissionNumber);
        final MissionViewModel viewModel = ViewModelProviders.of(this, factory).get(MissionViewModel.class);
        viewModel.getMissionLiveData().observe(this, new Observer<Mission>() {
            @Override
            public void onChanged(@Nullable Mission mission) {
                //viewModel.getMissionLiveData().removeObserver(this);
                bindViews(mission);
                mMission = mission;
            }
        });

        return mRootView;
    }

    private void refreshData() {
        //Init mission loader
        getLoaderManager().initLoader(MISSION_LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(MISSION_NUMBER_KEY, mMissionNumber);
    }

    private void bindViews(Mission mission) {
        if (mRootView == null) {
            return;
        }

        if (mission != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            mCollapsingToolbarLayout.setTitle(mission.getMissionName());
            Log.v("TITLE SET", mission.getMissionName());
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivityCast().onBackPressed();
                }
            });

            // Webcast preview and link
            if (mission.getLinks() != null && mission.getLinks().getVideoLink() != null) {
                final String missionVideoUrl = mission.getLinks().getVideoLink();
                final String videoKey = missionVideoUrl.substring(missionVideoUrl.indexOf("=") + 1, missionVideoUrl.length());
                final String sdVideoImageUrl = ImageUtils.buildSdVideoThumbnailUrl(videoKey);

                Picasso.get()
                        .load(sdVideoImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mWebcastPreviewImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Yay!
                            }

                            @Override
                            public void onError(Exception e) {
                                // Try again online, if cache loading failed
                                Picasso.get()
                                        .load(sdVideoImageUrl)
                                        .into(mWebcastPreviewImageView, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                // Yay!
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                // Try again online, using a lower resolution image URL
                                                String hqVideoUrl = ImageUtils.buildHqVideoThumbnailUrl(videoKey);
                                                Picasso.get()
                                                        .load(hqVideoUrl)
                                                        .error(R.drawable.video)
                                                        .into(mWebcastPreviewImageView);
                                            }
                                        });
                            }
                        });

                // Set a listener, so we can open each video when the webcast image is clicked
                mWebcastPreviewImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(missionVideoUrl)));
                    }
                });

                // TODO: Set listener on play image too and make a touch selector
            } else {
                mWebcastPreviewImageView.setImageResource(R.drawable.falcon9);
                mRootView.findViewById(R.id.webcast_play).setVisibility(View.GONE);
            }

            // Patch (if it's available)
            if (mission.getLinks() != null && mission.getLinks().getMissionPatchSmall() != null) {
                final String missionPatchImageUrl = mission.getLinks().getMissionPatchSmall();
                Picasso.get()
                        .load(missionPatchImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mMissionPatchImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Yay!
                            }

                            @Override
                            public void onError(Exception e) {
                                // Try again online, if cache loading failed
                                Picasso.get()
                                        .load(missionPatchImageUrl)
                                        .error(R.drawable.dragon)
                                        .into(mMissionPatchImageView);
                            }
                        });
            } else {
                // Otherwise, load placeholder patch
                mMissionPatchImageView.setImageResource(R.drawable.dragon);
            }

            // Launch date
            mLaunchDateTextView.setText(mission.getLaunchDateUtc());
            // Launch site
            mLaunchSiteNameTextView.setText(mission.getLaunchSite().getSiteName());
            // Mission details
            if (mission.getDetails() != null && !TextUtils.isEmpty(mission.getDetails())) {
                mDetailsTextView.setText(mission.getDetails());
            } else {
                mDetailsTextView.setVisibility(View.GONE);
            }

            // Rocket details
            if (mission.getRocket() != null) {
                Payload firstPayload = null;
                Core firstCore = null;

                // Rocket name
                String rocketName = mission.getRocket().getRocketName();
                mRocketTypeTextView.setText(rocketName);

                // Payload details
                if (mission.getRocket().getSecondStage().getPayloads() != null) {
                    firstPayload = mission.getRocket().getSecondStage().getPayloads().get(0);

                    // Set payload Id
                    mPayloadIdTextView.setText(firstPayload.getPayloadId());
                    // Set payload Type
                    mPayloadTypeTextView.setText(firstPayload.getPayloadType());
                    // Append "Reused" annotation
                    if (firstPayload.isReused()) {
                        mPayloadTypeTextView.append(getString(R.string.label_reused_payload));
                    }

                    // Set payload Mass
                    if (firstPayload.getPayloadMassKg() > 0) {
                        mPayloadMassTextView.setText(String.valueOf(firstPayload.getPayloadMassKg()) + " kg");
                        if (firstPayload.getPayloadMassLbs() > 0) {
                            mPayloadMassTextView.append("\n" + String.valueOf(firstPayload.getPayloadMassLbs()) + " lbs");
                        } else {
                            int pounds = (int) (firstPayload.getPayloadMassKg() * 2.20462);
                            mPayloadMassTextView.append("\n" + String.valueOf(pounds) + " lbs");
                        }
                    } else {
                        mPayloadMassTextView.setText(getString(R.string.label_unknown));
                    }

                    // Set Orbit
                    if (!TextUtils.isEmpty(firstPayload.getOrbit())) {
                        if (TextUtils.equals(firstPayload.getOrbit(), getString(R.string.label_es_l1))) {
                            mPayloadOrbitTextView.setText(getString(R.string.label_se_l1));
                        } else {
                            mPayloadOrbitTextView.setText(firstPayload.getOrbit());
                        }
                        switch (firstPayload.getOrbit()) {
                            case "BEO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_beo));
                                break;
                            case "DRO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_dro));
                                break;
                            case "GEO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_geo));
                                break;
                            case "GTO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_gto));
                                break;
                            case "HEO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_heo));
                                break;
                            case "LEO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_leo));
                                break;
                            case "LOI":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_loi));
                                break;
                            case "MEO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_meo));
                                break;
                            case "MOI":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_moi));
                                break;
                            case "SO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_so));
                                break;
                            case "TLI":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_tli));
                                break;
                            case "TMI":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_tmi));
                                break;
                            case "PO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_po));
                                break;
                            case "ISS":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_iss));
                                break;
                            case "SSO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_sso));
                                break;
                            case "HCO":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_hco));
                                break;
                            case "ES-L1":
                                mPayloadOrbitLongTextView.setText(getString(R.string.label_orbit_se_l1));
                                break;
                            default:
                                mPayloadOrbitLongTextView.setText("");
                        }
                    } else {
                        mPayloadOrbitTextView.setText(getString(R.string.label_unknown));
                        mPayloadOrbitLongTextView.setText("");
                    }
                }

                // Second stage details
                if (mission.getRocket().getSecondStage().getBlock() > 0) {
                    mSecondStageBlockTextView.setText(String.valueOf(mission.getRocket().getSecondStage().getBlock()));
                } else {
                    mSecondStageBlockTextView.setText(getString(R.string.label_unknown));
                }

                // Core details
                if (mission.getRocket().getFirstStage().getCores() != null) {
                    firstCore = mission.getRocket().getFirstStage().getCores().get(0);
                    // Core serial
                    if (!TextUtils.isEmpty(firstCore.getCoreSerial())) {
                        mCoreSerialTextView.setText(firstCore.getCoreSerial());
                    } else {
                        mCoreSerialTextView.setText(getString(R.string.label_unknown));
                    }

                    // Core type
                    if (firstCore.getBlock() > 0) {
                        mCoreBlockTextView.setText(String.valueOf(firstCore.getBlock()));
                    } else {
                        mCoreBlockTextView.setText(getString(R.string.label_unknown));
                    }
                    // Was this core used before?
                    if (firstCore.isReused()) {
                        mCoreReusedTextView.setText(getString(R.string.label_yes));
                        // Check how many time this core was used before
                        if (firstCore.getFlight() > 2) {
                            mCoreReusedTextView.append(" (" + String.valueOf(firstCore.getFlight() - 1) + " times)");
                        } else if (firstCore.getFlight() == 2) {
                            mCoreReusedTextView.append(" (" + String.valueOf(firstCore.getFlight() - 1) + " time)");
                        }
                    } else {
                        mCoreReusedTextView.setText(getString(R.string.label_no));
                    }

                    // Landing
                    if (!TextUtils.isEmpty(firstCore.getLandingType()) && !TextUtils.isEmpty(firstCore.getLandingVehicle())) {
                        // Get current time
                        long now = new Date().getTime();
                        // Was the core landing successful?
                        if (firstCore.isLandingSuccess()) {
                            mCoreLandingSuccessTextView.setText(getString(R.string.label_yes));
                        } else {
                            // Otherwise, check if this is an upcoming mission by comparing
                            // the launch time with current time
                            // If it is an upcoming mission, just hide the two views
                            if (mission.getLaunchDateUnix() > now / 1000) {
                                mCoreLandingSuccessTextView.setVisibility(View.GONE);
                                mRootView.findViewById(R.id.core_landing_label).setVisibility(View.GONE);
                            } else {
                                // Otherwise, if it's a past mission, just set text as "No"
                                mCoreLandingSuccessTextView.setText(getString(R.string.label_no));
                            }
                        }

                        // Set landing type
                        mCoreLandingTypeTextView.setText(firstCore.getLandingType());
                        switch (firstCore.getLandingType()) {
                            case "ASDS":
                                mCoreLandingTypeLongTextView.setText(getString(R.string.label_asds));
                                break;
                            case "RTLS":
                                mCoreLandingTypeLongTextView.setText(getString(R.string.label_rtls));
                                break;
                            default:
                                break;
                        }

                        // Set landing vehicle
                        mCoreLandingVehicleTextView.setText(firstCore.getLandingVehicle());
                        switch (firstCore.getLandingVehicle()) {
                            case "OCISLY":
                                mCoreLandingVehicleLongTextView.setText(getString(R.string.label_ocisly));
                                break;
                            case "JRTI":
                                mCoreLandingVehicleLongTextView.setText(getString(R.string.label_jrti));
                                break;
                            case "ASOG":
                                mCoreLandingVehicleLongTextView.setText(getString(R.string.label_asog));
                                break;
                            case "LZ-1":
                                mCoreLandingVehicleLongTextView.setText(getString(R.string.label_lz1));
                                break;
                            case "LZ-2":
                                mCoreLandingVehicleLongTextView.setText(getString(R.string.label_lz2));
                                break;
                            default:
                                break;
                        }
                    } else {
                        mRootView.findViewById(R.id.landing_layout).setVisibility(View.GONE);
                    }
                }

                // Set rocket image (payload and core)
                ConstraintLayout.LayoutParams paramsPayload = (ConstraintLayout.LayoutParams) mPayloadImageView.getLayoutParams();
                ConstraintLayout.LayoutParams paramsCore = (ConstraintLayout.LayoutParams) mCoreImageView.getLayoutParams();
                ConstraintLayout.LayoutParams paramsSeparationLine5 = (ConstraintLayout.LayoutParams) mSeparationLine5View.getLayoutParams();
                float[] screenSize = ScreenUtils.getScreenSize(getActivityCast());

                // Depending on the payload type, we can show a different upper image
                String payloadType = "";
                if (firstPayload != null) {
                    payloadType = mission.getRocket().getSecondStage().getPayloads().get(0).getPayloadType();
                }
                // Depending on the block type, we can show a different lower image
                int blockNumber = 5;
                if (firstCore != null && firstCore.getBlock() > 0) {
                    blockNumber = mission.getRocket().getFirstStage().getCores().get(0).getBlock();
                }

                // Set image, depending on rocket type and payload
                switch (rocketName) {
                    case "Falcon 1":
                        mPayloadImageView.setImageResource(R.drawable.payload_falcon1);
                        mCoreImageView.setImageResource(R.drawable.core_falcon1);
                        paramsPayload.setMarginEnd(48);
                        paramsCore.setMarginEnd(48);
                        break;
                    case "Falcon 9":
                        setPayloadImage(payloadType);

                        switch (blockNumber) {
                            case 5:
                                mCoreImageView.setImageResource(R.drawable.core_block5);
                                break;
                            default:
                                mCoreImageView.setImageResource(R.drawable.core_block4);
                                break;
                        }

                        paramsPayload.setMarginEnd(48);
                        paramsCore.setMarginEnd(48);
                        break;
                    case "Falcon Heavy":
                        setPayloadImage(payloadType);

                        switch (blockNumber) {
                            case 5:
                                mCoreImageView.setImageResource(R.drawable.falcon_heavy_block5);
                                break;
                            default:
                                mCoreImageView.setImageResource(R.drawable.falcon_heavy_block4);
                                break;
                        }

                        if (ScreenUtils.isPortraitMode(getActivityCast())) {
                            paramsPayload.setMarginEnd(69);
                            paramsCore.setMarginEnd(20);
                            paramsSeparationLine5.setMarginEnd(0);
                        } else {
                            paramsPayload.setMarginEnd(90);
                            paramsCore.setMarginEnd(20);
                            paramsSeparationLine5.setMarginEnd(70);
                        }

                        break;
                    case "BFR":
                        mPayloadImageView.setImageResource(R.drawable.payload_bfr);
                        mCoreImageView.setImageResource(R.drawable.core_bfr);
                        paramsPayload.setMarginEnd(24);
                        paramsCore.setMarginEnd(24);
                        break;
                    case "Big Falcon Rocket":
                        mPayloadImageView.setImageResource(R.drawable.payload_bfr);
                        mCoreImageView.setImageResource(R.drawable.core_bfr);
                        paramsPayload.setMarginEnd(24);
                        paramsCore.setMarginEnd(24);
                        break;
                }

                // We will set the payload/core image height to be equal to:
                // {Devices max screen size in px(height or width) - [(StatusBar + ActionBar + TopMargin + 2 x BottomMargin) (in dp) * screen density]} * 30.8%(or 69.2%) of total resulted height
                // Phone: 24 + 56 + 16 + 2x16 = 128dp
                // Tablet: 24 + 64 + 24 + 2x24 = 160dp
                float maxSize = Math.max(screenSize[0], screenSize[1]);
                // If screen is in Landscape mode, show rocket image 33% bigger
                if (!ScreenUtils.isPortraitMode(getActivityCast())) {
                    maxSize = (float) (maxSize * 1.33);
                }
                paramsPayload.height = (int) ((maxSize - getResources().getInteger(R.integer.rocket_height_subtraction) * screenSize[2]) * 0.308);
                paramsCore.height = (int) ((maxSize - getResources().getInteger(R.integer.rocket_height_subtraction) * screenSize[2]) * 0.692);
                paramsPayload.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                paramsCore.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                mPayloadImageView.setLayoutParams(paramsPayload);
                mCoreImageView.setLayoutParams(paramsCore);
                mSeparationLine5View.setLayoutParams(paramsSeparationLine5);
            } else {
                mRootView.findViewById(R.id.rocket_type_label).setVisibility(View.GONE);
                mRocketTypeTextView.setVisibility(View.GONE);
            }
        }
    }

    private void setPayloadImage(String payloadType) {
        switch (payloadType) {
            case "Satellite":
                // Falcon 9 with Fairing
                mPayloadImageView.setImageResource(R.drawable.payload_satellite);
                break;
            case "Dragon Boilerplate":
                // Falcon 9 with Dragon 1
                mPayloadImageView.setImageResource(R.drawable.payload_dragon1);
                break;
            case "Dragon 1.0":
                // Falcon 9 with Dragon 1.0
                mPayloadImageView.setImageResource(R.drawable.payload_dragon1);
                break;
            case "Dragon 1.1":
                // Falcon 9 with Dragon 1.1
                mPayloadImageView.setImageResource(R.drawable.payload_dragon1);
                break;
            default:
                mPayloadImageView.setImageResource(R.drawable.payload_dragon2);
                break;
        }
    }

    @NonNull
    @Override
    public Loader<List<Mission>> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case MISSION_LOADER_ID:
                // If the loaded id matches mission loader, return a new missions loader
                Log.v("MISSION NUMBER", "" + mMission.getFlightNumber());
                return new MissionLoader(getActivityCast(), mMission.getFlightNumber());
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Mission>> loader, List<Mission> missions) {
        if (missions != null) {
            final Mission newMission = missions.get(0);

            String missionAsString1 = new Gson().toJson(newMission);
            String missionAsString2 = new Gson().toJson(mMission);

            // If the content of the two missions is different, update the DB and
            // reattach the fragment, so the new values could be displayed in the UI
            if (!missionAsString1.equals(missionAsString2)) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.missionDao().updateMission(newMission);
                        snakeBarThis(getString(R.string.mission_updated));
                    }
                });
            } else {
                snakeBarThis(getString(R.string.mission_up_to_date));
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Mission>> loader) {
        mMission = null;
    }

    private void snakeBarThis(String message) {
        Snackbar snackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        snackbar.show();
    }

    // TODO: Solve displayed mission data, depending on location
    // TODO: Swipe and Refresh all missions
    // TODO: Mark upcoming missions with something
    // TODO: Create upcoming mission patch

    // TODO: Notifications with Firebase and Google Maps
    // TODO: Launching Sites on Google Maps
    // TODO: Rockets, Launching Sites and Payloads/Capsules

    // TODO: Widget
    // TODO: Video link update from Firebase (maybe)
}
