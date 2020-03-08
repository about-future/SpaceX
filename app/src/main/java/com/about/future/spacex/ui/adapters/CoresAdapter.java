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
import com.about.future.spacex.databinding.CardItemCoreBinding;
import com.about.future.spacex.databinding.ListItemCoreBinding;
import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.utils.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.BLOCK3_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK3_SMALL;
import static com.about.future.spacex.utils.Constants.BLOCK5_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK5_SMALL;

public class CoresAdapter extends RecyclerView.Adapter<CoresAdapter.ViewHolder> {
    private final Context mContext;
    private List<Core> mCores = new ArrayList<Core>() {};
    private final ListItemClickListener mOnClickListener;

    //private ListItemCoreBinding listBinding;
    //private CardItemCoreBinding cardBinding;

    public interface ListItemClickListener {
        void onItemClickListener(String selectedLaunchPad);
    }

    public CoresAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    public void setCores(List<Core> cores) {
        mCores = cores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CoresAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (ScreenUtils.isPortraitMode(mContext)) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_core, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.card_item_core, parent, false);
        }
        view.setFocusable(false);
        return new ViewHolder(view);

        /*View view;
        if (ScreenUtils.isPortraitMode(mContext)) {
            listBinding = ListItemCoreBinding.inflate(LayoutInflater.from(parent.getContext()));
            view = listBinding.getRoot();
        } else {
            cardBinding = CardItemCoreBinding.inflate(LayoutInflater.from(parent.getContext()));
            view = cardBinding.getRoot();
        }
        view.setFocusable(false);

        return new ViewHolder(view);*/
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(mCores.get(position));
    }

    @Override
    public int getItemCount() {
        return mCores != null ? mCores.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.core_thumbnail)
        ImageView coreThumbnail;
        @BindView(R.id.core_serial)
        TextView coreSerialTextView;
        @BindView(R.id.core_details)
        TextView coreDetailsTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bindTo(Core core) {
            String imagePath;

            if (ScreenUtils.isPortraitMode(mContext)) {
                switch (core.getBlock()) {
                    case 3:
                        imagePath = BLOCK3_SMALL;
                        break;
                    case 5:
                        imagePath = BLOCK5_SMALL;
                        break;
                    default:
                        imagePath = ""; //ImageUtils.buildMapThumbnailUrl(latitude, longitude, 15, "satellite", mContext);
                }

                setImage(imagePath, coreThumbnail);

                coreSerialTextView.setText(core.getCoreSerial());
                if (core.getDetails() != null && !core.getDetails().equals("")) {
                    coreDetailsTextView.setText(core.getDetails());
                } else {
                    coreDetailsTextView.setText(mContext.getString(R.string.no_core_details));
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

                setImage(imagePath, coreThumbnail);

                coreSerialTextView.setText(core.getCoreSerial());
                if (core.getDetails() != null && !core.getDetails().equals("")) {
                    coreDetailsTextView.setText(core.getDetails());
                } else {
                    coreDetailsTextView.setText(mContext.getString(R.string.no_core_details));
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
            mOnClickListener.onItemClickListener(mCores.get(getAdapterPosition()).getCoreSerial());
        }
    }
}
