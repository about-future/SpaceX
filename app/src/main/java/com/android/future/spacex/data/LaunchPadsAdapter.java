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

import com.android.future.spacex.R;
import com.android.future.spacex.launch_pad_entity.LaunchPad;
import com.android.future.spacex.utils.ImageUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchPadsAdapter extends RecyclerView.Adapter<LaunchPadsAdapter.ViewHolder> {
    private final Context mContext;
    private List<LaunchPad> mLaunchPads = new ArrayList<LaunchPad>() {
    };
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(String launchPadSelected);
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

        if (mLaunchPads.get(position).getPadLocation() != null) {
            double latitude = mLaunchPads.get(position).getPadLocation().getLatitude();
            double longitude = mLaunchPads.get(position).getPadLocation().getLongitude();
            launchPadThumbnailPath = ImageUtils.buildMapThumbnailUrl(latitude, longitude);
        }

        // If we have a valid image path, try loading it from cache or from web with Picasso
        if (!TextUtils.isEmpty(launchPadThumbnailPath)) {
            final String launchPadThumbnailUrl = launchPadThumbnailPath;
            // Try loading image from device memory or cache
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
                            // Try again online, if cache loading failed
                            Picasso.get()
                                    .load(launchPadThumbnailUrl)
                                    .error(R.drawable.staticmap)
                                    .into(holder.launchPadThumbnailImageView);
                        }
                    });
        } else {
            // Otherwise, don't bother using Picasso and set default image for launchPadThumbnailImageView
            holder.launchPadThumbnailImageView.setImageResource(R.drawable.staticmap);
        }

        if (!TextUtils.isEmpty(mLaunchPads.get(position).getFullName())) {
            holder.launchPadFullNameTextView.setText(mLaunchPads.get(position).getFullName());
        } else {
            holder.launchPadFullNameTextView.setText(mContext.getString(R.string.label_unknown));
        }

        if (!TextUtils.isEmpty(mLaunchPads.get(position).getPadLocation().getName()) &&
                !TextUtils.isEmpty(mLaunchPads.get(position).getPadLocation().getRegion())) {
            holder.launchPadLocationTextView.setText(
                    mLaunchPads.get(position).getPadLocation().getName()
                            + ", " +
                            mLaunchPads.get(position).getPadLocation().getRegion());
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
            mOnClickListener.onItemClickListener(mLaunchPads.get(getAdapterPosition()).getId());
        }
    }

    public void setLaunchPads(List<LaunchPad> launchPads) {
        mLaunchPads = launchPads;
        notifyDataSetChanged();
    }

    public List<LaunchPad> getLaunchPads() {
        return mLaunchPads;
    }
}
