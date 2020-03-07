package com.about.future.spacex.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.about.future.spacex.R;
import com.about.future.spacex.databinding.CardItemCapsuleBinding;
import com.about.future.spacex.databinding.CardItemCoreBinding;
import com.about.future.spacex.databinding.ListItemCapsuleBinding;
import com.about.future.spacex.databinding.ListItemCoreBinding;
import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.model.rocket.Capsule;
import com.about.future.spacex.utils.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.about.future.spacex.utils.Constants.BLOCK3_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK3_SMALL;
import static com.about.future.spacex.utils.Constants.BLOCK5_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK5_SMALL;

public class CapsulesAdapter extends RecyclerView.Adapter<CapsulesAdapter.ViewHolder> {
    private final Context mContext;
    private List<Capsule> mCapsules = new ArrayList<Capsule>() {};
    private final ListItemClickListener mOnClickListener;

    private ListItemCapsuleBinding listBinding;
    private CardItemCapsuleBinding cardBinding;

    public interface ListItemClickListener {
        void onItemClickListener(String selectedLaunchPad);
    }

    public CapsulesAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    public void setCapsules(List<Capsule> capsules) {
        mCapsules = capsules;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CapsulesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (ScreenUtils.isPortraitMode(mContext)) {
            listBinding = ListItemCapsuleBinding.inflate(LayoutInflater.from(parent.getContext()));
            view = listBinding.getRoot();
        } else {
            cardBinding = CardItemCapsuleBinding.inflate(LayoutInflater.from(parent.getContext()));
            view = cardBinding.getRoot();
        }
        view.setFocusable(false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(mCapsules.get(position));
    }

    @Override
    public int getItemCount() {
        return mCapsules != null ? mCapsules.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        private void bindTo(Capsule capsule) {
            String imagePath;

            if (ScreenUtils.isPortraitMode(mContext)) {
                switch (capsule.getCapsuleId()) {
                    case "dragon1":
                        imagePath = BLOCK3_SMALL;
                        break;
                    case "dragon2":
                        imagePath = BLOCK5_SMALL;
                        break;
                    default:
                        imagePath = ""; //ImageUtils.buildMapThumbnailUrl(latitude, longitude, 15, "satellite", mContext);
                }

                setImage(imagePath, listBinding.coreThumbnail);

                listBinding.coreSerial.setText(core.getCoreSerial());
                if (core.getDetails() != null && !core.getDetails().equals("")) {
                    listBinding.coreDetails.setText(core.getDetails());
                } else {
                    listBinding.coreDetails.setText(mContext.getString(R.string.no_core_details));
                }
            } else {
                switch (core.getBlock()) {
                    case 3:
                        imagePath = BLOCK3_MEDIUM;
                        break;
                    case 5:
                        imagePath = BLOCK5_MEDIUM;
                        break;
                    default:
                        imagePath = "";
                }

                setImage(imagePath, cardBinding.coreThumbnail);

                cardBinding.coreSerial.setText(core.getCoreSerial());
                if (core.getDetails() != null && !core.getDetails().equals("")) {
                    cardBinding.coreDetails.setText(core.getDetails());
                } else {
                    cardBinding.coreDetails.setText(mContext.getString(R.string.no_core_details));
                }
            }
        }

        private void setImage(final String imagePath, ImageView imageView) {
            if (!TextUtils.isEmpty(imagePath)) {

                Picasso.get()
                        .load(imagePath)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(imageView, new Callback() {
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
                                        .load(imagePath)
                                        .error(R.drawable.empty_map)
                                        .into(imageView);
                            }
                        });
            } else {
                // Otherwise, don't bother using Picasso and set default image for launchPadThumbnailImageView
                imageView.setImageResource(R.drawable.empty_map);
            }
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(mCapsules.get(getAdapterPosition()).getCoreSerial());
        }
    }
}
