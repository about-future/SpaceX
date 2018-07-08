package com.android.future.spacex;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
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
import com.android.future.spacex.entity.Core;
import com.android.future.spacex.entity.Mission;
import com.android.future.spacex.entity.Payload;
import com.android.future.spacex.utils.ImageUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;

import static com.android.future.spacex.SpaceXActivity.MISSION_NUMBER_KEY;

public class MissionDetailsFragment extends Fragment {

    private AppDatabase mDb;
    private int mMissionNumber;
    private View mRootView;
    private Toolbar mToolbar;

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

        mToolbar = mRootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        getActivityCast().setSupportActionBar(mToolbar);

        AddMissionViewModelFactory factory = new AddMissionViewModelFactory(mDb, mMissionNumber);
        final AddMissionViewModel viewModel = ViewModelProviders.of(this, factory).get(AddMissionViewModel.class);
        viewModel.getMissionLiveData().observe(this, new Observer<Mission>() {
            @Override
            public void onChanged(@Nullable Mission mission) {
                viewModel.getMissionLiveData().removeObserver(this);
                bindViews(mission);
            }
        });

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(MISSION_NUMBER_KEY, mMissionNumber);
    }

    private void bindViews(Mission mission) {
        if (mRootView == null) {
            return;
        }

        ImageView mPhotoImageView = mRootView.findViewById(R.id.photo);
        TextView launchDateTextView = mRootView.findViewById(R.id.launch_date);
        TextView rocketTypeTextView = mRootView.findViewById(R.id.rocket_type);
        final ImageView missionPatchImageView = mRootView.findViewById(R.id.mission_patch_large);
        TextView launchSiteNameTextView = mRootView.findViewById(R.id.launch_site_name);
        //TextView launchSiteNameLongTextView = mRootView.findViewById(R.id.launch_site_name_long);
        TextView detailsTextView = mRootView.findViewById(R.id.mission_details);
        final ImageView webcastPreviewImageView = mRootView.findViewById(R.id.webcast_preview);

        TextView coreSerialTextView = mRootView.findViewById(R.id.core_serial);

        if (mission != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);

            Toolbar toolbar = mRootView.findViewById(R.id.toolbar);
            toolbar.setTitle(mission.getMissionName());
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivityCast().onBackPressed();
                }
            });

            // Set backdrop image and rocket type
            if (mission.getRocket() != null && mission.getRocket().getRocketName() != null) {
                String rocketName = mission.getRocket().getRocketName();
                rocketTypeTextView.setText(rocketName);

                // Set backdrop image, depending on rocket type and payload
                switch (rocketName) {
                    case "Falcon 1":
                        mPhotoImageView.setImageResource(R.drawable.falcon1);
                        break;
                    case "Falcon 9":
                        // Depending on the payload type, we can show a different falcon 9 image
//                        String payloadType = mission.getRocket().getSecondStage().getPayloads().get(0).getPayloadType();
//                        switch (payloadType) {
//                            case "Satellite":
//                                // Falcon 9 with Fairing
//                                mPhotoView.setImageResource(R.drawable.falcon93);
//                                break;
//                            case "Dragon 1.1":
//                                // Falcon 9 with Dragon
//                                mPhotoView.setImageResource(R.drawable.falcon92);
//                                break;
//                            default:
//                                mPhotoView.setImageResource(R.drawable.falcon9);
//                                break;
//                        }
                        mPhotoImageView.setImageResource(R.drawable.falcon93);
                        break;
                    case "Falcon Heavy":
                        mPhotoImageView.setImageResource(R.drawable.falconheavy2);
                        break;
                    case "BFR":
                        mPhotoImageView.setImageResource(R.drawable.bfr);
                        break;
                    case "Big Falcon Rocket":
                        mPhotoImageView.setImageResource(R.drawable.bfr);
                        break;
                }
            }

            // Get rocket details
            if (mission.getRocket() != null) {
                rocketTypeTextView.setText(mission.getRocket().getRocketName());

                // Get core details
                if (mission.getRocket().getFirstStage().getCores() != null) {

                    Core firstCore = mission.getRocket().getFirstStage().getCores().get(0);
                    coreSerialTextView.append(firstCore.getCoreSerial() + "\n");
                    coreSerialTextView.append("" + firstCore.getBlock() + "\n");
                    coreSerialTextView.append(firstCore.getLandingVehicle() + "\n");

                    //Log.v("CORE SERIAL", coreSerial);
                } else {
                    Log.v("CORE", "IS EMPTY");
                }

                // Get payload details
                if (mission.getRocket().getSecondStage().getPayloads() != null) {
                    Payload firstPayload = mission.getRocket().getSecondStage().getPayloads().get(0);

                    coreSerialTextView.append(firstPayload.getPayloadId() + "\n");
                    coreSerialTextView.append(firstPayload.getPayloadType() + "\n");
                    coreSerialTextView.append("" + firstPayload.getPayloadMassKg() + "\n");

                } else {
                    Log.v("PAYLOAD", "IS EMPTY");
                }

            } else {
                mPhotoImageView.findViewById(R.id.rocket_type_label).setVisibility(View.GONE);
                rocketTypeTextView.setVisibility(View.GONE);
            }

            // Set mission patch if it's available
            if (mission.getLinks() != null && mission.getLinks().getMissionPatchSmall() != null) {
                final String missionPatchImageUrl = mission.getLinks().getMissionPatchSmall();
                Picasso.get()
                        .load(missionPatchImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(missionPatchImageView, new Callback() {
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
                                        .into(missionPatchImageView);
                            }
                        });
            } else {
                missionPatchImageView.setImageResource(R.drawable.dragon);
            }

            if (mission.getLinks() != null && mission.getLinks().getVideoLink() != null) {
                final String missionVideoUrl = mission.getLinks().getVideoLink();
                final String videoKey = missionVideoUrl.substring(missionVideoUrl.indexOf("=") + 1, missionVideoUrl.length());
                final String sdVideoImageUrl = ImageUtils.buildSdVideoThumbnailUrl(getContext(), videoKey);

                Picasso.get()
                        .load(sdVideoImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(webcastPreviewImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Yay!
                            }

                            @Override
                            public void onError(Exception e) {
                                // Try again online, if cache loading failed
                                Picasso.get()
                                        .load(sdVideoImageUrl)
                                        .into(webcastPreviewImageView, new Callback() {
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
                                                        .into(webcastPreviewImageView);
                                            }
                                        });
                            }
                        });

                // Set a listener, so we can open each video when the webcast image is clicked
                webcastPreviewImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(missionVideoUrl)));
                    }
                });

                // TODO: Set listener on play image too and make a touch selector
            } else {
                // Hide the video layout if no video link available
                mRootView.findViewById(R.id.webcast_layout).setVisibility(View.GONE);
            }

            launchDateTextView.setText(mission.getLaunchDateUtc());

            launchSiteNameTextView.setText(mission.getLaunchSite().getSiteName());
            //launchSiteNameLongTextView.setText(mission.getLaunchSite().getSiteNameLong());

            if (mission.getDetails() != null && !TextUtils.isEmpty(mission.getDetails())) {
                detailsTextView.setText(mission.getDetails());
            } else {
                detailsTextView.setVisibility(View.GONE);
            }
        }
    }

    // TODO 2: Slide to refresh data and save it in DB
    // TODO 3: Add more data in layout about rocket and payload
    // TODO 4: Create layout-land version
}
