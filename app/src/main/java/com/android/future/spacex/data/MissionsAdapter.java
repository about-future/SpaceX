package com.android.future.spacex.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.future.spacex.R;
import com.android.future.spacex.entity.Mission;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MissionsAdapter extends RecyclerView.Adapter<MissionsAdapter.ViewHolder> {
    private final Context mContext;
    private List<Mission> mMissions = new ArrayList<Mission>() {};
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(int missionSelected);
    }

    public MissionsAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mission_list_item, parent, false);
        view.setFocusable(false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String missionPatchImagePath = "";
        // TODO
        if (mMissions.get(position).getLinks() != null && mMissions.get(position).getLinks().getMissionPatchSmall() != null)
            missionPatchImagePath = mMissions.get(position).getLinks().getMissionPatchSmall();

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
                                    .error(R.drawable.dragon)
                                    .into(holder.missionPatchImageView);
                        }
                    });
        } else {
            // Otherwise, don't bother using Picasso and set default_mission_patch image for missionPatchImageView
            holder.missionPatchImageView.setImageResource(R.drawable.dragon); // default_mission_patch
        }

        holder.missionNameTextView.setText(mMissions.get(position).getMissionName());
        holder.launchDateTextView.setText(mMissions.get(position).getLaunchDateUtc());
    }

    @Override
    public int getItemCount() {
        if (mMissions == null) {
            return 0;
        }
        return mMissions.size();
        //return mMissions != null ? mMissions.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.mission_patch)
        ImageView missionPatchImageView;
        @BindView(R.id.mission_name)
        TextView missionNameTextView;
        @BindView(R.id.launch_date)
        TextView launchDateTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(mMissions.get(getAdapterPosition()).getFlightNumber());
        }
    }

    public void setMissions(List<Mission> missions) {
        mMissions = missions;
        notifyDataSetChanged();
    }

    public List<Mission> getMissions() {
        return mMissions;
    }
}
