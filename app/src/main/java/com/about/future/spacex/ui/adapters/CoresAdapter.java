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
import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
            String imagePath = ImageUtils.getThumbnailPath(mContext, core.getBlock(), core.getCoreSerial());
            setImage(imagePath, coreThumbnail);

            coreSerialTextView.setText(core.getCoreSerial());
            if (core.getDetails() != null && !core.getDetails().equals("")) {
                coreDetailsTextView.setText(core.getDetails());
            } else {
                coreDetailsTextView.setText(mContext.getString(R.string.no_core_details));
            }
        }

        private String getPath(int block, String coreSerial) {
            switch (block) {
                case 1:
                    switch (coreSerial) {
                        case "B1005": return mContext.getString(R.string.core_b1005);
                        case "B1006": return mContext.getString(R.string.core_b1006);
                        case "B1007": return mContext.getString(R.string.core_b1007);
                        case "B1008": return mContext.getString(R.string.core_b1008);
                        case "B1010": return mContext.getString(R.string.core_b1010);
                        case "B1011": return mContext.getString(R.string.core_b1011);
                        case "B1012": return mContext.getString(R.string.core_b1012);
                        case "B1013": return mContext.getString(R.string.core_b1013);
                        case "B1014": return mContext.getString(R.string.core_b1014);
                        case "B1015": return mContext.getString(R.string.core_b1015);
                        case "B1016": return mContext.getString(R.string.core_b1016);
                        case "B1017": return mContext.getString(R.string.core_b1017);
                        case "B1018": return mContext.getString(R.string.core_b1018);
                        case "B1019": return mContext.getString(R.string.core_b1019);
                        case "B1020": return mContext.getString(R.string.core_b1020);
                        default: return mContext.getString(R.string.core_block1);
                    }
                case 2:
                    switch (coreSerial) {
                        case "B1021": return mContext.getString(R.string.core_b1021);
                        case "B1022": return mContext.getString(R.string.core_b1022);
                        case "B1023": return mContext.getString(R.string.core_b1023);
                        case "B1024": return mContext.getString(R.string.core_b1024);
                        case "B1025": return mContext.getString(R.string.core_b1025);
                        case "B1026": return mContext.getString(R.string.core_b1026);
                    }
                    break;
                case 3:
                    switch (coreSerial) {
                        case "B1028": return mContext.getString(R.string.core_b1028);
                        case "B1029": return mContext.getString(R.string.core_b1029);
                        case "B1030": return mContext.getString(R.string.core_b1030);
                        case "B1031": return mContext.getString(R.string.core_b1031);
                        case "B1032": return mContext.getString(R.string.core_b1032);
                        case "B1033": return mContext.getString(R.string.core_b1033);
                        case "B1034": return mContext.getString(R.string.core_b1034);
                        case "B1035": return mContext.getString(R.string.core_b1035);
                        case "B1036": return mContext.getString(R.string.core_b1036);
                        case "B1037": return mContext.getString(R.string.core_b1037);
                        case "B1038": return mContext.getString(R.string.core_b1038);
                    }
                    break;
                case 4:
                    switch (coreSerial) {
                        case "B1039": return mContext.getString(R.string.core_b1039);
                        case "B1040": return mContext.getString(R.string.core_b1040);
                        case "B1041": return mContext.getString(R.string.core_b1041);
                        case "B1042": return mContext.getString(R.string.core_b1042);
                        case "B1043": return mContext.getString(R.string.core_b1043);
                        case "B1044": return mContext.getString(R.string.core_b1044);
                        case "B1045": return mContext.getString(R.string.core_b1045);
                    }
                    break;
                case 5:
                    switch (coreSerial) {
                        case "B1046": return mContext.getString(R.string.core_b1046);
                        case "B1047": return mContext.getString(R.string.core_b1047);
                        case "B1048": return mContext.getString(R.string.core_b1048);
                        case "B1049": return mContext.getString(R.string.core_b1049);
                        case "B1050": return mContext.getString(R.string.core_b1050);
                        case "B1051": return mContext.getString(R.string.core_b1051);
                        case "B1052": return mContext.getString(R.string.core_b1052);
                        case "B1053": return mContext.getString(R.string.core_b1053);
                        case "B1054": return mContext.getString(R.string.core_b1054);
                        case "B1055": return mContext.getString(R.string.core_b1055);
                        case "B1056": return mContext.getString(R.string.core_b1056);
                        case "B1057": return mContext.getString(R.string.core_b1057);
                        case "B1059": return mContext.getString(R.string.core_b1059);
                        default: return mContext.getString(R.string.core_block5);
                    }
                default: //New type of core
                    return "";
            }

            return "";
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
