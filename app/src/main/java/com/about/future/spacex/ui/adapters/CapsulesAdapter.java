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
import com.about.future.spacex.model.rocket.Capsule;
import com.about.future.spacex.utils.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.DRAGON1_BIG;
import static com.about.future.spacex.utils.Constants.DRAGON1_SMALL;
import static com.about.future.spacex.utils.Constants.DRAGON2_BIG;
import static com.about.future.spacex.utils.Constants.DRAGON2_SMALL;

public class CapsulesAdapter extends RecyclerView.Adapter<CapsulesAdapter.ViewHolder> {
    private final Context mContext;
    private List<Capsule> mCapsules = new ArrayList<Capsule>() {};
    private final ListItemClickListener mOnClickListener;

    //private ListItemCapsuleBinding listBinding;
    //private CardItemCapsuleBinding cardBinding;

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
        /*if (ScreenUtils.isPortraitMode(mContext)) {
            listBinding = ListItemCapsuleBinding.inflate(LayoutInflater.from(parent.getContext()));
            view = listBinding.getRoot();
        } else {
            cardBinding = CardItemCapsuleBinding.inflate(LayoutInflater.from(parent.getContext()));
            view = cardBinding.getRoot();
        }
        view.setFocusable(false);*/

        if (ScreenUtils.isPortraitMode(mContext)) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_capsule, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.card_item_capsule, parent, false);
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
        @BindView(R.id.capsule_thumbnail)
        ImageView capsuleThumbnail;
        @BindView(R.id.capsule_serial)
        TextView capsuleSerialTextView;
        @BindView(R.id.capsule_details)
        TextView capsuleDetailsTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bindTo(Capsule capsule) {
            String imagePath;

            if (ScreenUtils.isPortraitMode(mContext)) {
                switch (capsule.getCapsuleId()) {
                    case "dragon1":
                        imagePath = DRAGON1_SMALL;
                        break;
                    case "dragon2":
                        imagePath = DRAGON2_SMALL;
                        break;
                    default:
                        imagePath = ""; //ImageUtils.buildMapThumbnailUrl(latitude, longitude, 15, "satellite", mContext);
                }

                setImage(imagePath, capsuleThumbnail);

                capsuleSerialTextView.setText(capsule.getCapsuleSerial());
                if (capsule.getDetails() != null && !capsule.getDetails().equals("")) {
                    capsuleDetailsTextView.setText(capsule.getDetails());
                } else {
                    capsuleDetailsTextView.setText(mContext.getString(R.string.no_capsule_details));
                }
            } else {
                switch (capsule.getCapsuleId()) {
                    case "dragon1":
                        imagePath = DRAGON1_BIG;
                        break;
                    case "dragon2":
                        imagePath = DRAGON2_BIG;
                        break;
                    default:
                        imagePath = "";
                }

                setImage(imagePath, capsuleThumbnail);

                capsuleSerialTextView.setText(capsule.getCapsuleSerial());
                if (capsule.getDetails() != null && !capsule.getDetails().equals("")) {
                    capsuleDetailsTextView.setText(capsule.getDetails());
                } else {
                    capsuleDetailsTextView.setText(mContext.getString(R.string.no_capsule_details));
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
            mOnClickListener.onItemClickListener(mCapsules.get(getAdapterPosition()).getCapsuleSerial());
        }
    }
}
