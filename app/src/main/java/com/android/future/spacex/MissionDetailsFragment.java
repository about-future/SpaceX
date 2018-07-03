package com.android.future.spacex;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.future.spacex.data.AddMissionViewModel;
import com.android.future.spacex.data.AddMissionViewModelFactory;
import com.android.future.spacex.data.AppDatabase;
import com.android.future.spacex.entity.Mission;
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
    private ImageView mPhotoView;

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

        mPhotoView = mRootView.findViewById(R.id.photo);

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

        final TextView bodyView = mRootView.findViewById(R.id.article_body);

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

            bodyView.setText(mission.getDetails());

            if (mission.getLinks() != null && mission.getLinks().getMissionPatchSmall() != null) {
                final String missionPatchImageUrl = mission.getLinks().getMissionPatchSmall();
                Picasso.get()
                        .load(missionPatchImageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mPhotoView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Yay!
                            }

                            @Override
                            public void onError(Exception e) {
                                // Try again online, if cache loading failed
                                Picasso.get()
                                        .load(missionPatchImageUrl)
                                        .error(R.drawable.bangabandu)
                                        .into(mPhotoView);
                            }
                        });
            } else {
                mPhotoView.setImageResource(R.drawable.falcon9_block5);
            }

            bodyView.append("\n");
            bodyView.append(mission.getLaunchSite().getSiteName() + "\n");
            if (mission.getLinks() != null)
                bodyView.append(mission.getLinks().getVideoLink() + "\n");
            bodyView.append(mission.getReuse().isCore() + "\n");
            if (mission.getRocket() != null) {
                bodyView.append(mission.getRocket().getRocketType() + "\n");
                bodyView.append(mission.getRocket().getRocketName() + "\n");
            }
//        } else {
//            mRootView.setVisibility(View.GONE);
        }

    }
}
