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

import com.about.future.spacex.R;
import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.utils.ImageUtils;
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

import static com.about.future.spacex.utils.Constants.JRTI_BIG;
import static com.about.future.spacex.utils.Constants.JRTI_SMALL;
import static com.about.future.spacex.utils.Constants.OCISLY_BIG;
import static com.about.future.spacex.utils.Constants.OCISLY_SMALL;

public class LandingPadsAdapter extends RecyclerView.Adapter<LandingPadsAdapter.ViewHolder> {
    private final Context mContext;
    private List<LandingPad> mLandingPads = new ArrayList<LandingPad>() {};
    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(String selectedLaunchPad);
    }

    public LandingPadsAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    public void setLandingPads(List<LandingPad> landingPads) {
        mLandingPads = landingPads;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LandingPadsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (ScreenUtils.isPortraitMode(mContext)) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_pad, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.card_item_pad, parent, false);
        }
        view.setFocusable(false);
        return new LandingPadsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LandingPadsAdapter.ViewHolder holder, int position) {
        String landingPadThumbnailPath = "";
        final int thisPosition = position;

        if (mLandingPads.get(position).getLocation() != null) {
            double latitude = mLandingPads.get(position).getLocation().getLatitude();
            double longitude = mLandingPads.get(position).getLocation().getLongitude();

            if (ScreenUtils.isPortraitMode(mContext)) {
                switch (mLandingPads.get(position).getId()) {
                    case "OCISLY":
                        landingPadThumbnailPath = OCISLY_SMALL;
                        break;
                    case "JRTI":
                        landingPadThumbnailPath = JRTI_SMALL;
                        break;
                    default:
                        landingPadThumbnailPath = ImageUtils.buildMapThumbnailUrl(latitude, longitude, 15, "satellite", mContext);
                }
            } else {
                switch (mLandingPads.get(position).getId()) {
                    case "OCISLY":
                        landingPadThumbnailPath = OCISLY_BIG;
                        break;
                    case "JRTI":
                        landingPadThumbnailPath = JRTI_BIG;
                        break;
                    default:
                        landingPadThumbnailPath = ImageUtils.buildSatelliteBackdropUrl(latitude, longitude, 17, mContext);
                }
            }
        }

        // If we have a valid image path, try loading it
        if (!TextUtils.isEmpty(landingPadThumbnailPath)) {
            final String landingPadThumbnailUrl = landingPadThumbnailPath;

            // If the time difference between NOW and the last time images were loaded is equal or
            // grater than 30 days, reload images from web and reset savedDate value
            if (ImageUtils.doWeNeedToFetchImagesOnline(mContext)) {
                Log.v("FETCHING", "FROM WEB");
                // Fetch images
                Picasso.get()
                        .load(landingPadThumbnailUrl)
                        .error(R.drawable.empty_map)
                        .into(holder.padThumbnailImageView);
                // Reset savedDate, only when last position is reached
                if (position == mLandingPads.size() - 1)
                    SpaceXPreferences.setLandingPadsThumbnailsSavingDate(mContext, new Date().getTime());
            } else {
                Log.v("TRY FETCHING", "FROM CACHE");
                // Otherwise, try loading cached images
                Picasso.get()
                        .load(landingPadThumbnailPath)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.padThumbnailImageView, new Callback() {
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
                                        .load(landingPadThumbnailUrl)
                                        .error(R.drawable.empty_map)
                                        .into(holder.padThumbnailImageView);
                                // Reset savedDate, only when last position is reached
                                if (thisPosition == mLandingPads.size() - 1)
                                    SpaceXPreferences.setLandingPadsThumbnailsSavingDate(mContext, new Date().getTime());
                            }
                        });
            }
        } else {
            // Otherwise, don't bother using Picasso and set default image for launchPadThumbnailImageView
            holder.padThumbnailImageView.setImageResource(R.drawable.empty_map);
        }

        if (!TextUtils.isEmpty(mLandingPads.get(position).getFullName())) {
            holder.padFullNameTextView.setText(mLandingPads.get(position).getFullName());
        } else {
            holder.padFullNameTextView.setText(mContext.getString(R.string.label_unknown));
        }

        if (!TextUtils.isEmpty(mLandingPads.get(position).getLocation().getName()) &&
                !TextUtils.isEmpty(mLandingPads.get(position).getLocation().getRegion())) {
            holder.padLocationTextView.setText(
                    String.format(
                            mContext.getString(R.string.launch_pad_location),
                            mLandingPads.get(position).getLocation().getName(),
                            mLandingPads.get(position).getLocation().getRegion()));
        } else {
            holder.padFullNameTextView.setText(mContext.getString(R.string.label_unknown));
        }
    }

    @Override
    public int getItemCount() {
        return mLandingPads != null ? mLandingPads.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.pad_thumbnail)
        ImageView padThumbnailImageView;
        @BindView(R.id.pad_full_name)
        TextView padFullNameTextView;
        @BindView(R.id.pad_location)
        TextView padLocationTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(mLandingPads.get(getAdapterPosition()).getId());
        }
    }
}
