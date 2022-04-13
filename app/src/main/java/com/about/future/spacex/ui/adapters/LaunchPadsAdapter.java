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
    private List<LaunchPad> mLaunchPads = new ArrayList<>();
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
        holder.bindTo(mLaunchPads.get(position));
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

        private void bindTo(LaunchPad launchPad) {
            String launchPadThumbnailPath = "";

            /*if (launchPad.getLatitude() != 0 && launchPad.getLongitude() != 0) {
                double latitude = launchPad.getLatitude();
                double longitude = launchPad.getLongitude();

                if (ScreenUtils.isPortraitMode(mContext)) {
                    launchPadThumbnailPath = ImageUtils.buildMapThumbnailUrl(latitude, longitude, 14, "satellite", mContext);
                } else {
                    launchPadThumbnailPath = ImageUtils.buildSatelliteBackdropUrl(latitude, longitude, 14, mContext);
                }
            }*/

            if (launchPad.getImages() != null
                    && launchPad.getImages().getLargeImages() != null
                    && launchPad.getImages().getLargeImages().length > 0) {
                for(String image : launchPad.getImages().getLargeImages()) {
                    if (image != null) {
                        launchPadThumbnailPath = image;
                        break;
                    }
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
                            .into(launchPadThumbnailImageView);
                    // Reset savedDate, only when last position is reached
                    if (getAbsoluteAdapterPosition() == mLaunchPads.size() - 1)
                        SpaceXPreferences.setLaunchPadsThumbnailsSavingDate(mContext, new Date().getTime());
                } else {
                    Log.v("TRY FETCHING", "FROM CACHE");
                    // Otherwise, try loading cached images
                    Picasso.get()
                            .load(launchPadThumbnailPath)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(launchPadThumbnailImageView, new Callback() {
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
                                            .into(launchPadThumbnailImageView);
                                    // Reset savedDate, only when last position is reached
                                    if (getAbsoluteAdapterPosition() == mLaunchPads.size() - 1)
                                        SpaceXPreferences.setLaunchPadsThumbnailsSavingDate(mContext, new Date().getTime());
                                }
                            });
                }
            } else {
                // Otherwise, don't bother using Picasso and set default image for launchPadThumbnailImageView
                launchPadThumbnailImageView.setImageResource(R.drawable.empty_map);
            }

            if (!TextUtils.isEmpty(launchPad.getFullName())) {
                launchPadFullNameTextView.setText(launchPad.getFullName());
            } else {
                launchPadFullNameTextView.setText(mContext.getString(R.string.label_unknown));
            }

            if (!TextUtils.isEmpty(launchPad.getLocality()) && !TextUtils.isEmpty(launchPad.getRegion())) {
                launchPadLocationTextView.setText(
                        String.format(
                                mContext.getString(R.string.launch_pad_location),
                                launchPad.getLocality(),
                                launchPad.getRegion()
                        )
                );
            } else {
                launchPadFullNameTextView.setText(mContext.getString(R.string.label_unknown));
            }
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(mLaunchPads.get(getAbsoluteAdapterPosition()).getId());
        }
    }
}
