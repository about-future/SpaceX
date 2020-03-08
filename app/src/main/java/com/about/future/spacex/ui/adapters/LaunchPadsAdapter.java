package com.about.future.spacex.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.about.future.spacex.model.pads.LaunchPad;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.R;
import com.about.future.spacex.utils.ScreenUtils;
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
    private List<LaunchPad> mLaunchPads = new ArrayList<LaunchPad>() {};
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(String selectedLaunchPad);
    }

    public LaunchPadsAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    public void setLaunchPads(List<LaunchPad> launchPads) {
        mLaunchPads = launchPads;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LaunchPadsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (ScreenUtils.isPortraitMode(mContext)) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_pad, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.card_item_pad, parent, false);
        }
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
            if (ScreenUtils.isPortraitMode(mContext)) {
                launchPadThumbnailPath = ImageUtils.buildMapThumbnailUrl(latitude, longitude, 14, "satellite", mContext);
            } else {
                launchPadThumbnailPath = ImageUtils.buildSatelliteBackdropUrl(latitude, longitude, 14, mContext);
            }
        }

        // If we have a valid image path, try loading it
        if (!TextUtils.isEmpty(launchPadThumbnailPath)) {
            final String launchPadThumbnailUrl = launchPadThumbnailPath;

            // If the time difference between NOW and the last time images were loaded is equal or
            // grater than 30 days, reload images from web and reset savedDate value
            if (ImageUtils.doWeNeedToFetchImagesOnline(mContext)) {
                Log.v("FETCHING", "FROM WEB");
                // Fetch images
                Picasso.get()
                        .load(launchPadThumbnailUrl)
                        .error(R.drawable.empty_map)
                        .into(holder.launchPadThumbnailImageView);
                // Reset savedDate, only when last position is reached
                if (position == mLaunchPads.size() - 1)
                    SpaceXPreferences.setLaunchPadsThumbnailsSavingDate(mContext, new Date().getTime());
            } else {
                Log.v("TRY FETCHING", "FROM CACHE");
                // Otherwise, try loading cached images
                Picasso.get()
                        .load(launchPadThumbnailPath)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.launchPadThumbnailImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Yay!
                                Log.v("FETCHING", "FROM CACHE");
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.v("FETCHING", "FROM WEB");
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
                    String.format(
                            mContext.getString(R.string.launch_pad_location),
                            mLaunchPads.get(position).getLocation().getName(),
                            mLaunchPads.get(position).getLocation().getRegion()));
        } else {
            holder.launchPadFullNameTextView.setText(mContext.getString(R.string.label_unknown));
        }
    }

    @Override
    public int getItemCount() {
        return mLaunchPads != null ? mLaunchPads.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.pad_thumbnail)
        ImageView launchPadThumbnailImageView;
        @BindView(R.id.pad_full_name)
        TextView launchPadFullNameTextView;
        @BindView(R.id.pad_location)
        TextView launchPadLocationTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(mLaunchPads.get(getAdapterPosition()).getSiteId());
        }
    }
}
