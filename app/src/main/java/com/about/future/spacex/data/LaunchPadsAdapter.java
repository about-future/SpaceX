package com.about.future.spacex.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.R;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchPadsAdapter extends RecyclerView.Adapter<LaunchPadsAdapter.ViewHolder> {
    private final Context mContext;
    private List<LaunchPad> mLaunchPads = new ArrayList<LaunchPad>() {
    };
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(int launchPadSelected);
    }

    public LaunchPadsAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public LaunchPadsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.launch_pad_list_item, parent, false);
        view.setFocusable(false);
        return new LaunchPadsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LaunchPadsAdapter.ViewHolder holder, int position) {
        String launchPadThumbnailPath = "";
        final int thisPosition = position;

        if (mLaunchPads.get(position).getLocation() != null) {
            double latitude = mLaunchPads.get(position).getLocation().getLatitude();
            double longitude = mLaunchPads.get(position).getLocation().getLongitude();
            launchPadThumbnailPath = ImageUtils.buildMapThumbnailUrl(latitude, longitude, 14, "satellite");
        }

        // If we have a valid image path, try loading it
        if (!TextUtils.isEmpty(launchPadThumbnailPath)) {
            final String launchPadThumbnailUrl = launchPadThumbnailPath;

            // If the time difference between NOW and the last time images were loaded is equal or
            // grater than 30 days, reload images from web and reset savedDate value
            if (ImageUtils.doWeNeedToFetchImagesOnline(mContext)) {
                // Fetch images
                Picasso.get()
                        .load(launchPadThumbnailUrl)
                        .error(R.drawable.empty_map)
                        .into(holder.launchPadThumbnailImageView);
                // Reset savedDate, only when last position is reached
                if (position == mLaunchPads.size() - 1)
                    SpaceXPreferences.setLaunchPadsThumbnailsSavingDate(mContext, new Date().getTime());
            } else {
                // Otherwise, try loading cached images
                Picasso.get()
                        .load(launchPadThumbnailPath)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.launchPadThumbnailImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Yay!
                            }

                            @Override
                            public void onError(Exception e) {
                                // Try again online, if cache loading failed and reset savedDate value
                                Picasso.get()
                                        .load(launchPadThumbnailUrl)
                                        .error(R.drawable.empty_map)
                                        .into(holder.launchPadThumbnailImageView);
                                // Reset savedDate, only when last position is reached
                                if (thisPosition == mLaunchPads.size() - 1)
                                    SpaceXPreferences.setLaunchPadsThumbnailsSavingDate(mContext, new Date().getTime());
                            }
                        });
            }
        } else {
            // Otherwise, don't bother using Picasso and set default image for launchPadThumbnailImageView
            holder.launchPadThumbnailImageView.setImageResource(R.drawable.empty_map);
        }

        if (!TextUtils.isEmpty(mLaunchPads.get(position).getFullName())) {
            holder.launchPadFullNameTextView.setText(mLaunchPads.get(position).getFullName());
        } else {
            holder.launchPadFullNameTextView.setText(mContext.getString(R.string.label_unknown));
        }

        if (!TextUtils.isEmpty(mLaunchPads.get(position).getLocation().getName()) &&
                !TextUtils.isEmpty(mLaunchPads.get(position).getLocation().getRegion())) {
            holder.launchPadLocationTextView.setText(
                    mLaunchPads.get(position).getLocation().getName()
                            + ", " +
                            mLaunchPads.get(position).getLocation().getRegion());
            // TODO: fix above string concat
        } else {
            holder.launchPadFullNameTextView.setText(mContext.getString(R.string.label_unknown));
        }
    }

    @Override
    public int getItemCount() {
        return mLaunchPads != null ? mLaunchPads.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.launch_pad_thumbnail)
        ImageView launchPadThumbnailImageView;
        @BindView(R.id.launch_pad_full_name)
        TextView launchPadFullNameTextView;
        @BindView(R.id.launch_pad_location)
        TextView launchPadLocationTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(mLaunchPads.get(getAdapterPosition()).getPadId());
        }
    }

    public void setLaunchPads(List<LaunchPad> launchPads) {
        mLaunchPads = launchPads;
        notifyDataSetChanged();
    }
}
