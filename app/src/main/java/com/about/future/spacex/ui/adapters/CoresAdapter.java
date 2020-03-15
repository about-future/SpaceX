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
import com.about.future.spacex.utils.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.about.future.spacex.utils.Constants.BLOCK1_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK1_SMALL;
import static com.about.future.spacex.utils.Constants.BLOCK2_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK2_SMALL;
import static com.about.future.spacex.utils.Constants.BLOCK3_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK3_SMALL;
import static com.about.future.spacex.utils.Constants.BLOCK4_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK4_SMALL;
import static com.about.future.spacex.utils.Constants.BLOCK5_MEDIUM;
import static com.about.future.spacex.utils.Constants.BLOCK5_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1005_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1006_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1008_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1010_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1011_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1012_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1013_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1014_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1015_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1016_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1017_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1019_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1020_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1021_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1022_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1023_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1024_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1025_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1026_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1029_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1030_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1031_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1032_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1033_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1034_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1035_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1036_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1037_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1038_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1039_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1040_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1041_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1042_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1043_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1044_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1045_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1046_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1047_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1048_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1049_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1050_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1051_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1052_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1053_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1054_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1055_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1056_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1057_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1058_SMALL;
import static com.about.future.spacex.utils.Constants.CORE_B1059_SMALL;

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
                    case 1:
                        switch (core.getCoreSerial()) {
                            case "B1005":
                                imagePath = CORE_B1005_SMALL;
                                break;
                            case "B1006":
                                imagePath = CORE_B1006_SMALL;
                                break;
                            case "B1008":
                                imagePath = CORE_B1008_SMALL;
                                break;
                            case "B1010":
                                imagePath = CORE_B1010_SMALL;
                                break;
                            case "B1011":
                                imagePath = CORE_B1011_SMALL;
                                break;
                            case "B1012":
                                imagePath = CORE_B1012_SMALL;
                                break;
                            case "B1013":
                                imagePath = CORE_B1013_SMALL;
                                break;
                            case "B1014":
                                imagePath = CORE_B1014_SMALL;
                                break;
                            case "B1015":
                                imagePath = CORE_B1015_SMALL;
                                break;
                            case "B1016":
                                imagePath = CORE_B1016_SMALL;
                                break;
                            case "B1017":
                                imagePath = CORE_B1017_SMALL;
                                break;
                            case "B1019":
                                imagePath = CORE_B1019_SMALL;
                                break;
                            case "B1020":
                                imagePath = CORE_B1020_SMALL;
                                break;
                            default:
                                imagePath = BLOCK1_SMALL;
                                break;
                        }
                        break;
                    case 2:
                        switch (core.getCoreSerial()) {
                            case "B1021":
                                imagePath = CORE_B1021_SMALL;
                                break;
                            case "B1022":
                                imagePath = CORE_B1022_SMALL;
                                break;
                            case "B1023":
                                imagePath = CORE_B1023_SMALL;
                                break;
                            case "B1024":
                                imagePath = CORE_B1024_SMALL;
                                break;
                            case "B1025":
                                imagePath = CORE_B1025_SMALL;
                                break;
                            case "B1026":
                                imagePath = CORE_B1026_SMALL;
                                break;
                            default:
                                imagePath = BLOCK2_SMALL;
                                break;
                        }
                        break;
                    case 3:
                        switch (core.getCoreSerial()) {
                            case "B1029":
                                imagePath = CORE_B1029_SMALL;
                                break;
                            case "B1030":
                                imagePath = CORE_B1030_SMALL;
                                break;
                            case "B1031":
                                imagePath = CORE_B1031_SMALL;
                                break;
                            case "B1032":
                                imagePath = CORE_B1032_SMALL;
                                break;
                            case "B1033":
                                imagePath = CORE_B1033_SMALL;
                                break;
                            case "B1034":
                                imagePath = CORE_B1034_SMALL;
                                break;
                            case "B1035":
                                imagePath = CORE_B1035_SMALL;
                                break;
                            case "B1036":
                                imagePath = CORE_B1036_SMALL;
                                break;
                            case "B1037":
                                imagePath = CORE_B1037_SMALL;
                                break;
                            case "B1038":
                                imagePath = CORE_B1038_SMALL;
                                break;
                            default:
                                imagePath = BLOCK3_SMALL;
                        }
                        break;
                    case 4:
                        switch (core.getCoreSerial()) {
                            case "B1039":
                                imagePath = CORE_B1039_SMALL;
                                break;
                            case "B1040":
                                imagePath = CORE_B1040_SMALL;
                                break;
                            case "B1041":
                                imagePath = CORE_B1041_SMALL;
                                break;
                            case "B1042":
                                imagePath = CORE_B1042_SMALL;
                                break;
                            case "B1043":
                                imagePath = CORE_B1043_SMALL;
                                break;
                            case "B1044":
                                imagePath = CORE_B1044_SMALL;
                                break;
                            case "B1045":
                                imagePath = CORE_B1045_SMALL;
                                break;
                            default:
                                imagePath = BLOCK4_SMALL;
                        }
                        break;
                    case 5:
                        switch (core.getCoreSerial()) {
                            case "B1046":
                                imagePath = CORE_B1046_SMALL;
                                break;
                            case "B1047":
                                imagePath = CORE_B1047_SMALL;
                                break;
                            case "B1048":
                                imagePath = CORE_B1048_SMALL;
                                break;
                            case "B1049":
                                imagePath = CORE_B1049_SMALL;
                                break;
                            case "B1050":
                                imagePath = CORE_B1050_SMALL;
                                break;
                            case "B1051":
                                imagePath = CORE_B1051_SMALL;
                                break;
                            case "B1052":
                                imagePath = CORE_B1052_SMALL;
                                break;
                            case "B1053":
                                imagePath = CORE_B1053_SMALL;
                                break;
                            case "B1054":
                                imagePath = CORE_B1054_SMALL;
                                break;
                            case "B1055":
                                imagePath = CORE_B1055_SMALL;
                                break;
                            case "B1056":
                                imagePath = CORE_B1056_SMALL;
                                break;
                            case "B1057":
                                imagePath = CORE_B1057_SMALL;
                                break;
                            /*case "B1058":
                                imagePath = CORE_B1058_SMALL;
                                break;*/
                            case "B1059":
                                imagePath = CORE_B1059_SMALL;
                                break;
                            default:
                                imagePath = BLOCK5_SMALL;
                        }
                        break;
                    default: //New type of core
                        imagePath = "";
                        break;
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
                    case 1:
                        imagePath = BLOCK1_MEDIUM;
                        break;
                    case 2:
                        imagePath = BLOCK2_MEDIUM;
                        break;
                    case 3:
                        imagePath = BLOCK3_MEDIUM;
                        break;
                    case 4:
                        imagePath = BLOCK4_MEDIUM;
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
