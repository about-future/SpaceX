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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.about.future.spacex.R;
import com.about.future.spacex.model.mission.Core;
import com.about.future.spacex.model.mission.Payload;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.utils.NetworkUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.utils.TextsUtils;
import com.about.future.spacex.ui.LaunchPadDetailsActivity;
import com.about.future.spacex.ui.MissionDetailsActivity;
import com.about.future.spacex.ui.RocketDetailsActivity;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.about.future.spacex.viewmodel.LaunchPadsViewModel;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.about.future.spacex.viewmodel.RocketsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.LAUNCH_PAD_ID_KEY;
import static com.about.future.spacex.utils.Constants.MISSION_NUMBER_KEY;
import static com.about.future.spacex.utils.Constants.ROCKET_ID_KEY;

public class MissionDetailsFragment extends Fragment {
    private Mission mMission;
    private int mMissionNumber;
    private View mRootView;

    private MissionsViewModel mMissionsViewModel;
    private LaunchPadsViewModel mLaunchPadsViewModel;
    private RocketsViewModel mRocketsViewModel;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.webcast_preview)
    ImageView mWebcastPreviewImageView;
    @BindView(R.id.webcast_play)
    ImageView mWebcastPlayButton;
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

    @BindView(R.id.rocket_type_linear_layout)
    LinearLayout mRocketTypeLinearLayout;
    @BindView(R.id.launch_date_linear_layout)
    LinearLayout mLaunchDateLinearLayout;
    @BindView(R.id.launch_site_linear_layout)
    LinearLayout mLaunchSiteLinearLayout;

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
    @BindView(R.id.separation_line55)
    View mSeparationLine5View;
    @BindView(R.id.core_serial)
    TextView mCoreSerialTextView;
    @BindView(R.id.core_block)
    TextView mCoreBlockTextView;
    @BindView(R.id.core_reused)
    TextView mCoreReusedTextView;
    @BindView(R.id.core_landing_label)
    TextView mCoreLandingLabel;
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

    // Rocket images
    @BindView(R.id.mission_rocket_core_image)
    ImageView mCoreImageView;
    @BindView(R.id.mission_rocket_payload_image)
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

        if (getArguments() != null && getArguments().containsKey(MISSION_NUMBER_KEY)) {
            mMissionNumber = getArguments().getInt(MISSION_NUMBER_KEY);
        }
        setHasOptionsMenu(true);
    }

    //TODO: Mission youtube launch thumbnail (check all past missions)
    // add loading missions, rockets and pads animation, instead of cloud image
    // fix mission details layouts

    private MissionDetailsActivity getActivityCast() { return (MissionDetailsActivity) getActivity(); }
    public void setMissionId(int missionId) { mMissionNumber = missionId; }

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

        // Init view models
        mLaunchPadsViewModel = ViewModelProviders.of(this).get(LaunchPadsViewModel.class);
        mRocketsViewModel = ViewModelProviders.of(this).get(RocketsViewModel.class);
        mMissionsViewModel = ViewModelProviders.of(this).get(MissionsViewModel.class);
        mMissionsViewModel.getMissionDetails(mMissionNumber).observe(this, mission -> {
            bindViews(mission);
            mMission = mission;
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            refreshData();
        });

        return mRootView;
    }

    private void refreshData() {
        // If there is a network connection, refresh data
        if (NetworkUtils.isConnected(getActivityCast())) {
            //Init mission loader
            Toast.makeText(getActivityCast(), "Mission refresh", Toast.LENGTH_SHORT).show();
        } else {
            // Display connection error message
            Toast.makeText(getActivityCast(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(MISSION_NUMBER_KEY, mMissionNumber);
    }

    private void startWebcast(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void bindViews(final Mission mission) {
        if (mRootView == null) {
            return;
        }

        if (mission != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            mCollapsingToolbarLayout.setTitle(mission.getMissionName());
            mToolbar.setNavigationOnClickListener(view -> getActivityCast().onBackPressed());

            // Webcast preview and link
            if (mission.getLinks() != null && mission.getLinks().getVideoLink() != null) {
                final String missionVideoUrl = mission.getLinks().getVideoLink();
                final String videoKey = missionVideoUrl.substring(missionVideoUrl.indexOf("=") + 1);
                Log.v("VIDEO KEY", "IS: " + videoKey);

                final String sdVideoImageUrl = ImageUtils.buildSdVideoThumbnailUrl(mission.getLinks().getYoutubeId());
                Log.v("VIDEO THUMBNAIL", "IS: " + sdVideoImageUrl);

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
                mWebcastPreviewImageView.setOnClickListener(view -> startWebcast(missionVideoUrl));
                // Set a listener, so we can open each video when the play image is clicked
                mWebcastPlayButton.setVisibility(View.VISIBLE);
                mWebcastPlayButton.setOnClickListener(view -> startWebcast(missionVideoUrl));
            } else {
                // Otherwise, set backdrop image if the is no webcast preview available
                switch (mission.getRocket().getRocketName()) {
                    case "Falcon 9":
                        mWebcastPreviewImageView.setImageResource(R.drawable.falcon9_backdrop);
                        break;
                    case "Falcon Heavy":
                        mWebcastPreviewImageView.setImageResource(R.drawable.falcon_heavy_backdrop);
                        break;
                    case "Big Falcon Rocket":
                        mWebcastPreviewImageView.setImageResource(R.drawable.bfr_backdrop);
                        break;
                    default:
                        mWebcastPreviewImageView.setImageResource(R.drawable.rocket);
                        break;
                }
                mWebcastPlayButton.setVisibility(View.GONE);
            }

            // Patch (if it's available)
            if (mission.getLinks() != null && mission.getLinks().getMissionPatchSmall() != null) {
                final String missionPatchImageUrl = mission.getLinks().getMissionPatchSmall();
                // First, try loading from cache
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
                                        .error(R.drawable.default_patch_f9_small)
                                        .into(mMissionPatchImageView);
                            }
                        });
            } else {
                // Otherwise, load placeholder patch
                try {
                    ImageUtils.setDefaultImage(
                            mMissionPatchImageView,
                            mission.getRocket().getRocketName(),
                            mission.getRocket().getSecondStage().getPayloads().get(0).getPayloadType(),
                            mission.getRocket().getFirstStage().getCores().get(0).getBlock());
                } catch (NullPointerException e) {
                    mMissionPatchImageView.setImageResource(R.drawable.default_patch_f9_small);
                }
            }

            mRocketTypeLinearLayout.setOnClickListener(view -> createRocketIntent());

            mLaunchSiteLinearLayout.setOnClickListener(view -> AppExecutors.getInstance().diskIO().execute(() -> {
                int launchPadId = mLaunchPadsViewModel.getLaunchPadId(mission.getLaunchSite().getSiteId());

                Intent intent = new Intent(getActivityCast(), LaunchPadDetailsActivity.class);
                intent.putExtra(LAUNCH_PAD_ID_KEY, launchPadId);
                startActivity(intent);
            }));

            // Set launch date
            if (mission.getLaunchDateUnix() > 0) {
                // Convert mission Date from seconds in milliseconds
                Date missionDate = new Date(mission.getLaunchDateUnix() * 1000L);
                // Set formatted date in TextView
                mLaunchDateTextView.setText(DateUtils.formatDate(getActivityCast(), missionDate));
            } else {
                // Otherwise, set text as Unknown
                mLaunchDateTextView.setText(getString(R.string.label_unknown));
            }

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
                Core centralCore = null;
                boolean showAcronymsMeaning = SpaceXPreferences.showAcronymMeaning(getActivityCast());

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
                        // Check if the mass should be displayed in metric or imperial system
                        if (SpaceXPreferences.isMetric(getActivityCast())) {
                            mPayloadMassTextView.setText(String.format(getString(R.string.mass_kg), TextsUtils.formatFuel(firstPayload.getPayloadMassKg())));
                        } else {
                            // If we have a payload weight in lbs, display it
                            if (firstPayload.getPayloadMassLbs() > 0) {
                                mPayloadMassTextView.setText(String.format(getString(R.string.mass_lbs), TextsUtils.formatFuel(firstPayload.getPayloadMassLbs())));
                            } else {
                                // Otherwise, convert kgs in lbs and display it
                                int pounds = (int) (firstPayload.getPayloadMassKg() * 2.20462);
                                mPayloadMassTextView.setText(String.format(getString(R.string.mass_lbs), TextsUtils.formatFuel(pounds)));
                            }
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
                        if (showAcronymsMeaning) {
                            mPayloadOrbitLongTextView.setVisibility(View.VISIBLE);
                            showLongOrbit(firstPayload.getOrbit(), mPayloadOrbitLongTextView);
                        } else {
                            mPayloadOrbitLongTextView.setVisibility(View.GONE);
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

                // First Stage
                // Core details
                if (mission.getRocket().getFirstStage().getCores() != null) {
                    // Get current time
                    long now = new Date().getTime();

                    // Get central core
                    centralCore = mission.getRocket().getFirstStage().getCores().get(0);

                    // Core serial
                    TextView coreSerialLabel = mRootView.findViewById(R.id.core_serial_label);
                    if (rocketName.equals("Falcon Heavy")) {
                        coreSerialLabel.setText(getString(R.string.label_core_booster_serial));
                    } else {
                        coreSerialLabel.setText(getString(R.string.label_core_serial));
                    }
                    if (!TextUtils.isEmpty(centralCore.getCoreSerial())) {
                        mCoreSerialTextView.setText(centralCore.getCoreSerial());
                    } else {
                        mCoreSerialTextView.setText(getString(R.string.label_unknown));
                    }

                    // Core type
                    if (centralCore.getBlock() > 0) {
                        mCoreBlockTextView.setText(String.valueOf(centralCore.getBlock()));
                    } else {
                        mCoreBlockTextView.setText(getString(R.string.label_unknown));
                    }
                    // Was this core used before?
                    setReused(centralCore.isReused(), centralCore.getFlight(), mCoreReusedTextView);

                    // Landing details
                    if (!TextUtils.isEmpty(centralCore.getLandingType()) && !TextUtils.isEmpty(centralCore.getLandingVehicle())) {
                        // Was the core landing successful?
                        setSuccessfulLanding(
                                centralCore.isLandingSuccess(),
                                mCoreLandingSuccessTextView,
                                mission.getLaunchDateUnix() > now / 1000,
                                mCoreLandingLabel);

                        // Set landing type
                        mCoreLandingTypeTextView.setText(centralCore.getLandingType());
                        if (showAcronymsMeaning) {
                            mCoreLandingTypeLongTextView.setVisibility(View.VISIBLE);
                            setLongLandingType(centralCore.getLandingType(), mCoreLandingTypeLongTextView);
                        } else {
                            mCoreLandingTypeLongTextView.setVisibility(View.GONE);
                        }

                        // Set landing vehicle
                        mCoreLandingVehicleTextView.setText(centralCore.getLandingVehicle());
                        if (showAcronymsMeaning) {
                            mCoreLandingVehicleLongTextView.setVisibility(View.VISIBLE);
                            setLongLandingVehicle(centralCore.getLandingVehicle(), mCoreLandingVehicleLongTextView);
                        } else {
                            mCoreLandingVehicleLongTextView.setVisibility(View.GONE);
                        }
                    } else {
                        mRootView.findViewById(R.id.central_core_landing_layout).setVisibility(View.GONE);
                    }

                    if (rocketName.equals("Falcon Heavy") && mission.getRocket().getFirstStage().getCores().size() == 3) {
                        // Show side cores layouts
                        mRootView.findViewById(R.id.left_core_layout).setVisibility(View.VISIBLE);
                        mRootView.findViewById(R.id.right_core_layout).setVisibility(View.VISIBLE);

                        // Get Left Core
                        Core leftCore = mission.getRocket().getFirstStage().getCores().get(1);

                        // Left Core Serial
                        TextView leftCoreSerial = mRootView.findViewById(R.id.left_core_serial);
                        if (!TextUtils.isEmpty(leftCore.getCoreSerial())) {
                            leftCoreSerial.setText(leftCore.getCoreSerial());
                        } else {
                            leftCoreSerial.setText(getString(R.string.label_unknown));
                        }

                        // Left Core type
                        TextView leftCoreBlockType = mRootView.findViewById(R.id.left_core_block);
                        if (leftCore.getBlock() > 0) {
                            leftCoreBlockType.setText(String.valueOf(leftCore.getBlock()));
                        } else {
                            leftCoreBlockType.setText(getString(R.string.label_unknown));
                        }
                        // Was left core used before?
                        TextView leftCoreReused = mRootView.findViewById(R.id.left_core_reused);
                        setReused(leftCore.isReused(), leftCore.getFlight(), leftCoreReused);

                        // Left Core landing details
                        TextView leftCoreLandingSuccess = mRootView.findViewById(R.id.left_core_landing);
                        TextView leftCoreLandingLabel = mRootView.findViewById(R.id.left_core_landing_label);
                        if (!TextUtils.isEmpty(leftCore.getLandingType()) && !TextUtils.isEmpty(leftCore.getLandingVehicle())) {
                            setSuccessfulLanding(
                                    leftCore.isLandingSuccess(),
                                    leftCoreLandingSuccess,
                                    mission.getLaunchDateUnix() > now / 1000,
                                    leftCoreLandingLabel);

                            // Set landing type
                            TextView leftCoreLandingType = mRootView.findViewById(R.id.left_core_landing_type);
                            TextView leftCoreLandingTypeLong = mRootView.findViewById(R.id.left_core_landing_type_long);
                            leftCoreLandingType.setText(leftCore.getLandingType());
                            if (showAcronymsMeaning) {
                                leftCoreLandingTypeLong.setVisibility(View.VISIBLE);
                                setLongLandingType(leftCore.getLandingType(), leftCoreLandingTypeLong);
                            } else {
                                leftCoreLandingTypeLong.setVisibility(View.GONE);
                            }

                            // Set landing vehicle
                            TextView leftCoreLandingVehicle = mRootView.findViewById(R.id.left_core_landing_vehicle);
                            TextView leftCoreLandingVehicleLong = mRootView.findViewById(R.id.left_core_landing_vehicle_long);
                            leftCoreLandingVehicle.setText(leftCore.getLandingVehicle());
                            if (showAcronymsMeaning) {
                                leftCoreLandingVehicleLong.setVisibility(View.VISIBLE);
                                setLongLandingVehicle(leftCore.getLandingVehicle(), leftCoreLandingVehicleLong);
                            } else {
                                leftCoreLandingVehicleLong.setVisibility(View.GONE);
                            }
                        } else {
                            mRootView.findViewById(R.id.left_core_landing_layout).setVisibility(View.GONE);
                        }

                        // Get Right Core
                        Core rightCore = mission.getRocket().getFirstStage().getCores().get(2);

                        // Right Core Serial
                        TextView rightCoreSerial = mRootView.findViewById(R.id.right_core_serial);
                        if (!TextUtils.isEmpty(rightCore.getCoreSerial())) {
                            rightCoreSerial.setText(rightCore.getCoreSerial());
                        } else {
                            rightCoreSerial.setText(getString(R.string.label_unknown));
                        }

                        // Right Core type
                        TextView rightCoreBlockType = mRootView.findViewById(R.id.right_core_block);
                        if (rightCore.getBlock() > 0) {
                            rightCoreBlockType.setText(String.valueOf(rightCore.getBlock()));
                        } else {
                            rightCoreBlockType.setText(getString(R.string.label_unknown));
                        }
                        // Was right core used before?
                        TextView rightCoreReused = mRootView.findViewById(R.id.right_core_reused);
                        setReused(rightCore.isReused(), rightCore.getFlight(), rightCoreReused);

                        // Right Core landing details
                        TextView rightCoreLandingSuccess = mRootView.findViewById(R.id.right_core_landing);
                        TextView rightCoreLandingLabel = mRootView.findViewById(R.id.right_core_landing_label);
                        if (!TextUtils.isEmpty(rightCore.getLandingType()) && !TextUtils.isEmpty(rightCore.getLandingVehicle())) {
                            setSuccessfulLanding(
                                    rightCore.isLandingSuccess(),
                                    rightCoreLandingSuccess,
                                    mission.getLaunchDateUnix() > now / 1000,
                                    rightCoreLandingLabel);

                            // Set landing type
                            TextView rightCoreLandingType = mRootView.findViewById(R.id.right_core_landing_type);
                            TextView rightCoreLandingTypeLong = mRootView.findViewById(R.id.right_core_landing_type_long);
                            rightCoreLandingType.setText(rightCore.getLandingType());
                            if (showAcronymsMeaning) {
                                rightCoreLandingTypeLong.setVisibility(View.VISIBLE);
                                setLongLandingType(rightCore.getLandingType(), rightCoreLandingTypeLong);
                            } else {
                                rightCoreLandingTypeLong.setVisibility(View.GONE);
                            }

                            // Set landing vehicle
                            TextView rightCoreLandingVehicle = mRootView.findViewById(R.id.right_core_landing_vehicle);
                            TextView rightCoreLandingVehicleLong = mRootView.findViewById(R.id.right_core_landing_vehicle_long);
                            rightCoreLandingVehicle.setText(rightCore.getLandingVehicle());
                            if (showAcronymsMeaning) {
                                rightCoreLandingVehicleLong.setVisibility(View.VISIBLE);
                                setLongLandingVehicle(rightCore.getLandingVehicle(), rightCoreLandingVehicleLong);
                            } else {
                                rightCoreLandingVehicleLong.setVisibility(View.GONE);
                            }
                        } else {
                            mRootView.findViewById(R.id.right_core_landing_layout).setVisibility(View.GONE);
                        }
                    } else {
                        mRootView.findViewById(R.id.left_core_layout).setVisibility(View.GONE);
                        mRootView.findViewById(R.id.right_core_layout).setVisibility(View.GONE);
                    }
                }

                // Set rocket image (payload and core)
                ConstraintLayout.LayoutParams paramsPayload = (ConstraintLayout.LayoutParams) mPayloadImageView.getLayoutParams();
                ConstraintLayout.LayoutParams paramsCore = (ConstraintLayout.LayoutParams) mCoreImageView.getLayoutParams();
                float[] screenSize = ScreenUtils.getScreenSize(getActivityCast());

                // Depending on the payload type, we can show a different upper image
                String payloadType = "";
                if (firstPayload != null) {
                    payloadType = mission.getRocket().getSecondStage().getPayloads().get(0).getPayloadType();
                }
                // Depending on the block type, we can show a different lower image
                int blockNumber = 5;
                if (centralCore != null && centralCore.getBlock() > 0) {
                    blockNumber = mission.getRocket().getFirstStage().getCores().get(0).getBlock();
                }

                // Set rocket image depending on rocket type and payload
                switch (rocketName) {
                    case "Falcon 1":
                        mPayloadImageView.setImageResource(R.drawable.payload_falcon1);
                        mCoreImageView.setImageResource(R.drawable.core_falcon1);
                        paramsPayload.setMarginEnd(48);
                        paramsCore.setMarginEnd(48);
                        break;
                    case "Falcon 9":
                        setPayloadImage(rocketName, payloadType);

                        switch (blockNumber) {
                            case 6:
                                break;
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
                        setPayloadImage(rocketName, payloadType);

                        switch (blockNumber) {
                            case 6:
                                break;
                            case 5:
                                mCoreImageView.setImageResource(R.drawable.falcon_heavy_block5);
                                break;
                            default:
                                mCoreImageView.setImageResource(R.drawable.falcon_heavy_block4);
                                break;
                        }

                        paramsPayload.setMarginEnd(20);
                        paramsCore.setMarginEnd(20);
                        break;
                    case "Starship":
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

                // Set click listener on payload and core images and create an intent for RocketDetailsActivity
                mPayloadImageView.setOnClickListener(view -> createRocketIntent());
                mCoreImageView.setOnClickListener(view -> createRocketIntent());

            } else {
                mRootView.findViewById(R.id.rocket_type_label).setVisibility(View.GONE);
                mRocketTypeTextView.setVisibility(View.GONE);
            }
        }
    }

    private void setPayloadImage(String rocketName, String payloadType) {
        switch (payloadType) {
            case "Satellite":
                // Falcon 9 and Falcon Heavy with Fairing
                if (TextUtils.equals(rocketName, "Falcon Heavy")) {
                    mPayloadImageView.setImageResource(R.drawable.payload_fh_satellite);
                } else {
                    mPayloadImageView.setImageResource(R.drawable.payload_satellite);
                }
                break;
            case "Dragon Boilerplate":
                // Falcon 9 with Dragon 1
                //mPayloadImageView.setImageResource(R.drawable.payload_dragon1);
                break;
            case "Dragon 1.0":
                // Falcon 9 with Dragon 1.0
                //mPayloadImageView.setImageResource(R.drawable.payload_dragon1);
                break;
            case "Dragon 1.1":
                // Falcon 9 with Dragon 1.1
                mPayloadImageView.setImageResource(R.drawable.payload_dragon1);
                break;
            default:
                if (TextUtils.equals(rocketName, "Falcon Heavy")) {
                    mPayloadImageView.setImageResource(R.drawable.payload_fh_dragon2);
                } else {
                    mPayloadImageView.setImageResource(R.drawable.payload_dragon2);
                }
                break;
        }
    }

    private void showLongOrbit(String orbit, TextView view) {
        switch (orbit) {
            case "BEO":
                view.setText(getString(R.string.label_orbit_beo));
                break;
            case "DRO":
                view.setText(getString(R.string.label_orbit_dro));
                break;
            case "GEO":
                view.setText(getString(R.string.label_orbit_geo));
                break;
            case "GTO":
                view.setText(getString(R.string.label_orbit_gto));
                break;
            case "HEO":
                view.setText(getString(R.string.label_orbit_heo));
                break;
            case "LEO":
                view.setText(getString(R.string.label_orbit_leo));
                break;
            case "LOI":
                view.setText(getString(R.string.label_orbit_loi));
                break;
            case "MEO":
                view.setText(getString(R.string.label_orbit_meo));
                break;
            case "MOI":
                view.setText(getString(R.string.label_orbit_moi));
                break;
            case "SO":
                view.setText(getString(R.string.label_orbit_so));
                break;
            case "TLI":
                view.setText(getString(R.string.label_orbit_tli));
                break;
            case "TMI":
                view.setText(getString(R.string.label_orbit_tmi));
                break;
            case "PO":
                view.setText(getString(R.string.label_orbit_po));
                break;
            case "ISS":
                view.setText(getString(R.string.label_orbit_iss));
                break;
            case "SSO":
                view.setText(getString(R.string.label_orbit_sso));
                break;
            case "HCO":
                view.setText(getString(R.string.label_orbit_hco));
                break;
            case "ES-L1":
                view.setText(getString(R.string.label_orbit_se_l1));
                break;
            default:
                view.setText("");
        }
    }

    private void setSuccessfulLanding(boolean isLandingSuccess, TextView view, boolean isUpcomingMission, TextView landingLabelView) {
        if (isLandingSuccess) {
            view.setText(getString(R.string.label_yes));
        } else {
            // Otherwise, check if this is an upcoming mission by comparing
            // the launch time with current time
            // If it is an upcoming mission, just hide the two views
            if (isUpcomingMission) {
                view.setVisibility(View.GONE);
                landingLabelView.setVisibility(View.GONE);
            } else {
                // Otherwise, if it's a past mission, just set text as "No"
                view.setText(getString(R.string.label_no));
            }
        }
    }

    private void setReused(boolean isReused, int flight, TextView view) {
        if (isReused) {
            view.setText(getString(R.string.label_yes));
            // Check how many time this core was used before
            if (flight > 2) {
                view.append(
                        String.format(getString(R.string.reused_x_times),
                                String.valueOf(flight - 1)));
            } else if (flight == 2) {
                view.append(
                        String.format(getString(R.string.reused_1_time),
                                String.valueOf(flight - 1)));
            }
        } else {
            view.setText(getString(R.string.label_no));
        }
    }

    private void setLongLandingType(String landingType, TextView view) {
        switch (landingType) {
            case "ASDS":
                view.setText(getString(R.string.label_asds));
                break;
            case "RTLS":
                view.setText(getString(R.string.label_rtls));
                break;
            default:
                break;
        }
    }

    private void setLongLandingVehicle(String landingVehicle, TextView view) {
        switch (landingVehicle) {
            case "OCISLY":
                view.setText(getString(R.string.label_ocisly));
                break;
            case "JRTI":
                view.setText(getString(R.string.label_jrti));
                break;
            case "ASOG":
                view.setText(getString(R.string.label_asog));
                break;
            case "LZ-1":
                view.setText(getString(R.string.label_lz1));
                break;
            case "LZ-2":
                view.setText(getString(R.string.label_lz2));
                break;
            default:
                break;
        }
    }

    private void createRocketIntent() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            int rocketId = mRocketsViewModel.getRocketId(mRocketTypeTextView.getText().toString());

            Intent intent = new Intent(getActivityCast(), RocketDetailsActivity.class);
            intent.putExtra(ROCKET_ID_KEY, rocketId);
            startActivity(intent);
        });
    }
}
