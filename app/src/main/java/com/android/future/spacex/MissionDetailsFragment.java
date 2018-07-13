package com.android.future.spacex;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import com.android.future.spacex.data.AddMissionViewModel;
import com.android.future.spacex.data.AddMissionViewModelFactory;
import com.android.future.spacex.data.AppDatabase;
import com.android.future.spacex.data.MissionLoader;
import com.android.future.spacex.entity.Core;
import com.android.future.spacex.entity.Mission;
import com.android.future.spacex.entity.Payload;
import com.android.future.spacex.utils.ImageUtils;
import com.android.future.spacex.utils.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.future.spacex.SpaceXActivity.MISSION_NUMBER_KEY;

public class MissionDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Mission>> {

    private static final int MISSION_LOADER_ID = 89207;

    private AppDatabase mDb;
    private int mMissionNumber;
    private View mRootView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
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
    // Second stage details
    @BindView(R.id.second_stage_block)
    TextView mSecondStageBlockTextView;
    // First stage details
    @BindView(R.id.core_serial)
    TextView mCoreSerialTextView;
    @BindView(R.id.core_block)
    TextView mCoreBlockTextView;
    @BindView(R.id.core_flight)
    TextView mCoreFlightTextView;
    @BindView(R.id.core_landing)
    TextView mCoreLandingSuccessTextView;
    @BindView(R.id.core_landing_type)
    TextView mCoreLandingTypeTextView;
    @BindView(R.id.core_landing_vehicle)
    TextView mCoreLandingVehicleTextView;

    @BindView(R.id.rocket_core_image)
    ImageView mCoreImageView;
    @BindView(R.id.rocket_payload_image)
    ImageView mPayloadImageView;

    private Mission mMission;

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
                Snackbar.make(mSwipeRefreshLayout, getString(R.string.mission_updating), Snackbar.LENGTH_LONG).show();

                refreshData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        AddMissionViewModelFactory factory = new AddMissionViewModelFactory(mDb, mMissionNumber);
        final AddMissionViewModel viewModel = ViewModelProviders.of(this, factory).get(AddMissionViewModel.class);
        viewModel.getMissionLiveData().observe(this, new Observer<Mission>() {
            @Override
            public void onChanged(@Nullable Mission mission) {
                mMission = mission;
                viewModel.getMissionLiveData().removeObserver(this);
                bindViews(mission);
            }
        });

        return mRootView;
    }

    private void refreshData() {
        //Init mission loader
        Log.v("LOADER" ,"INIT");
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

            // TODO: check toolbar
            Toolbar toolbar = mRootView.findViewById(R.id.toolbar);
            toolbar.setTitle(mission.getMissionName());
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivityCast().onBackPressed();
                }
            });

            // Webcast preview and link
            if (mission.getLinks() != null && mission.getLinks().getVideoLink() != null) {
                final String missionVideoUrl = mission.getLinks().getVideoLink();
                final String videoKey = missionVideoUrl.substring(missionVideoUrl.indexOf("=") + 1, missionVideoUrl.length());
                final String sdVideoImageUrl = ImageUtils.buildSdVideoThumbnailUrl(getContext(), videoKey);

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
                                                String hqVideoUrl = ImageUtils.buildHqVideoThumbnailUrl(getContext(), videoKey);
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

                    mPayloadIdTextView.setText(firstPayload.getPayloadId());
                    mPayloadTypeTextView.setText(firstPayload.getPayloadType());
                    if (firstPayload.isReused()) {
                        mPayloadTypeTextView.append(" (Reused)");
                    }
                    mPayloadMassTextView.setText(String.valueOf(firstPayload.getPayloadMassKg()) + " kg\n");
                    mPayloadMassTextView.append(String.valueOf(firstPayload.getPayloadMassLbs()) + " lbs");
                    mPayloadOrbitTextView.setText(firstPayload.getOrbit());
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

                    mCoreSerialTextView.setText(firstCore.getCoreSerial());
                    if (firstCore.getBlock() > 0) {
                        mCoreBlockTextView.setText(String.valueOf(firstCore.getBlock()));
                    } else {
                        mCoreBlockTextView.setText(getString(R.string.label_unknown));
                    }
                    if (firstCore.isReused()) {
                        mCoreFlightTextView.setText(String.valueOf(firstCore.getFlight()));
                    }
                    mCoreLandingSuccessTextView.setText(String.valueOf(firstCore.isLandingSuccess()));
                    mCoreLandingTypeTextView.setText(firstCore.getLandingType());
                    mCoreLandingVehicleTextView.setText(firstCore.getLandingVehicle());
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

                        paramsPayload.setMarginEnd(69);
                        paramsCore.setMarginEnd(20);
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
                paramsPayload.height = (int) ((maxSize - getResources().getInteger(R.integer.rocket_height_subtraction) * screenSize[2]) * 0.308);
                paramsCore.height = (int) ((maxSize - getResources().getInteger(R.integer.rocket_height_subtraction) * screenSize[2]) * 0.692);
                paramsPayload.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                paramsCore.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                mPayloadImageView.setLayoutParams(paramsPayload);
                mCoreImageView.setLayoutParams(paramsCore);
            } else {
                //mRootView.findViewById(R.id.rocket_type_label).setVisibility(View.GONE);
                mRocketTypeTextView.setVisibility(View.GONE);
            }
        } else {
            mToolbar.setTitle("");
            mWebcastPreviewImageView.setImageResource(R.drawable.video);
            mLaunchDateTextView.setText("");
            mRocketTypeTextView.setText("");
            mMissionPatchImageView.setImageResource(R.drawable.dragon);
            mLaunchSiteNameTextView.setText("");
            mDetailsTextView.setText("");
            mPayloadIdTextView.setText("");
            mPayloadTypeTextView.setText("");
            mPayloadMassTextView.setText("");
            mPayloadOrbitTextView.setText("");
            mSecondStageBlockTextView.setText("");
            mCoreSerialTextView.setText("");
            //TODO: add the rest of core texts
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
    public void onLoadFinished(@NonNull Loader<List<Mission>> loader, List<Mission> data) {
        if (data != null) {
            Log.v("MISSION REFRESHED", data.get(0).getLaunchDateUtc());
            bindViews(data.get(0));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Mission>> loader) {
        mMission = null;
    }

    // TODO 2: Slide to refresh data and save it in DB
    // TODO 4: Create layout-land version
}
