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
    private List<MissionMini> mMissions = new ArrayList<MissionMini>() {};
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
            view = LayoutInflater.from(mContext).inflate(R.layout.mission_list_item, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.mission_card_item, parent, false);
        }
        view.setFocusable(false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String missionPatchImagePath = "";
        if (!TextUtils.isEmpty(mMissions.get(position).getMissionPatch()))
            missionPatchImagePath = mMissions.get(position).getMissionPatch();

        // If we have a valid image path, try loading it from cache or from web with Picasso
        if (!TextUtils.isEmpty(missionPatchImagePath)) {
            final String missionPatchImageUrl = missionPatchImagePath;
            // Try loading image from device memory or cache
            Picasso.get()
                    .load(missionPatchImageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.missionPatchImageView, new Callback() {
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
                                    .into(holder.missionPatchImageView);
                        }
                    });
        } else {
            // Otherwise, don't bother using Picasso and set default_mission_patch image for missionPatchImageView
            try {
                ImageUtils.setDefaultImage(
                        holder.missionPatchImageView,
                        mMissions.get(position).getRocketName(),
                        mMissions.get(position).getPayloads().get(0).getPayloadType(),
                        mMissions.get(position).getBlock());
            } catch (NullPointerException e) {
                holder.missionPatchImageView.setImageResource(R.drawable.default_patch_f9_small);
            }
        }

        holder.missionNameTextView.setText(mMissions.get(position).getMissionName());

        // Set mission date and time, if it's available
        if (mMissions.get(position).getLaunchDateUnix() > 0) {
            // Convert mission Date from seconds in milliseconds
            Date missionDate = new Date(mMissions.get(position).getLaunchDateUnix() * 1000L);
            // Set formatted date in TextView
            holder.launchDateTextView.setText(DateUtils.formatDate(mContext, missionDate));

            // Set green color if mission time is bigger than present time (meaning it's an upcoming mission)
            if (missionDate.getTime() > new Date().getTime()) {
                holder.launchDateTextView.setTextColor(mContext.getResources().getColor(R.color.colorGreen));

                // Set time left until launch
                holder.timeLeftTextView.setVisibility(View.VISIBLE);
                holder.timeLeftTextView.setText(DateUtils.formatTimeLeft(mContext, mMissions.get(position).getLaunchDateUnix()));
            } else {
                // Otherwise, set the default color
                if (ScreenUtils.isPortraitMode(mContext)) {
                    holder.launchDateTextView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                } else {
                    holder.launchDateTextView.setTextColor(mContext.getResources().getColor(R.color.colorCardDescription));
                }
                holder.timeLeftTextView.setVisibility(View.GONE);
            }
        } else {
            // Otherwise, set text as Unknown
            holder.launchDateTextView.setText(mContext.getString(R.string.label_unknown));
        }
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

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(getAdapterPosition());
        }
    }
}
