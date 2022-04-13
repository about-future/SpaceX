package com.about.future.spacex.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.about.future.spacex.R;
import com.about.future.spacex.model.mission.MissionMini;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MissionsAdapter extends RecyclerView.Adapter<MissionsAdapter.ViewHolder> {
    private final Context mContext;
    private List<MissionMini> mMissions = new ArrayList<>();
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(int selectedMission);
    }

    public MissionsAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    public void setMissions(List<MissionMini> missions) {
        mMissions = missions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (ScreenUtils.isPortraitMode(mContext)) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_mission, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.card_item_mission, parent, false);
        }
        view.setFocusable(false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bindTo(mMissions.get(position));
    }

    @Override
    public int getItemCount() {
        return mMissions != null ? mMissions.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.mission_patch)
        ImageView missionPatchImageView;
        @BindView(R.id.mission_name)
        TextView missionNameTextView;
        @BindView(R.id.launch_date)
        TextView launchDateTextView;
        @BindView(R.id.mission_time_left)
        TextView timeLeftTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bindTo(MissionMini mission) {
            String missionPatchImagePath = "";

            if (!TextUtils.isEmpty(mission.getSmallPatch()))
                missionPatchImagePath = mission.getSmallPatch();

            // If we have a valid image path, try loading it from cache or from web with Picasso
            if (!TextUtils.isEmpty(missionPatchImagePath)) {
                final String missionPatchImageUrl = missionPatchImagePath;
                // Try loading image from device memory or cache
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
                                        .error(R.drawable.default_patch_f9_small)
                                        .into(missionPatchImageView);
                            }
                        });
            } else {
                // Otherwise, don't bother using Picasso and set default_mission_patch image for missionPatchImageView
                try {
                    ImageUtils.setDefaultImage(
                            missionPatchImageView,
                            mission.getRocketName(),
                            "",
                            5);
                } catch (NullPointerException e) {
                    missionPatchImageView.setImageResource(R.drawable.default_patch_f9_small);
                }
            }

            missionNameTextView.setText(mission.getMissionName());

            // Set mission date and time, if it's available
            if (mission.getLaunchDateUnix() > 0) {
                // Convert mission Date from seconds in milliseconds
                Date missionDate = new Date(mission.getLaunchDateUnix() * 1000L);
                // Set formatted date in TextView
                launchDateTextView.setText(DateUtils.formatDate(mContext, missionDate));

                // Set green color if mission time is bigger than present time (meaning it's an upcoming mission)
                if (missionDate.getTime() > new Date().getTime()) {
                    launchDateTextView.setTextColor(mContext.getResources().getColor(R.color.colorGreen));

                    // Set time left until launch
                    timeLeftTextView.setVisibility(View.VISIBLE);
                    timeLeftTextView.setText(DateUtils.formatTimeLeft(mContext, mission.getLaunchDateUnix()));
                } else {
                    // Otherwise, set the default color
                    if (ScreenUtils.isPortraitMode(mContext)) {
                        launchDateTextView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                    } else {
                        launchDateTextView.setTextColor(mContext.getResources().getColor(R.color.colorCardDescription));
                    }
                    timeLeftTextView.setVisibility(View.GONE);
                }
            } else {
                // Otherwise, set text as Unknown
                launchDateTextView.setText(mContext.getString(R.string.label_unknown));
            }
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(mMissions.get(getAdapterPosition()).getFlightNumber());
        }
    }
}
