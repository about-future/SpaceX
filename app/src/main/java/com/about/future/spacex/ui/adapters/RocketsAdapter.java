package com.about.future.spacex.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.about.future.spacex.R;
import com.about.future.spacex.model.rocket.RocketMini;
import com.about.future.spacex.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RocketsAdapter extends RecyclerView.Adapter<RocketsAdapter.ViewHolder> {
    private final Context mContext;
    private List<RocketMini> mRockets = new ArrayList<>();
    private final RocketsAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(String selectedRocket);
    }

    public RocketsAdapter(Context context, RocketsAdapter.ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    public void setRockets(List<RocketMini> rockets) {
        mRockets = rockets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RocketsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_item_rocket, parent, false);
        view.setFocusable(false);
        return new RocketsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RocketsAdapter.ViewHolder holder, int position) {
        holder.bindTo(mRockets.get(position));
    }

    @Override
    public int getItemCount() {
        return mRockets != null ? mRockets.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.card_rocket_image)
        ImageView rocketImageView;
        @BindView(R.id.card_rocket_name)
        TextView rocketNameTextView;
        @BindView(R.id.card_rocket_description)
        TextView rocketDescriptionTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bindTo(RocketMini rocket) {
            if (!TextUtils.isEmpty(rocket.getRocketId())) {
                switch (rocket.getRocketId()) {
                    case "falcon1":
                        rocketImageView.setImageResource(R.drawable.falcon1_small);
                        break;
                    case "falcon9":
                        ImageUtils.setImage(mContext.getString(R.string.falcon9_medium), rocketImageView);
                        //rocketImageView.setImageResource(R.drawable.falcon9_small);
                        break;
                    case "falconheavy":
                        //ImageUtils.setImage(mContext.getString(R.string.falcon_heavy_medium), rocketImageView);
                        rocketImageView.setImageResource(R.drawable.falcon_heavy);
                        break;
                    case "starship":
                        ImageUtils.setImage(mContext.getString(R.string.starship_medium), rocketImageView);
                        //rocketImageView.setImageResource(R.drawable.bfr1);
                        break;
                    default:
                        //rocketImageView.setImageResource(R.drawable.rocket_small);
                        ImageUtils.setImage(mContext.getString(R.string.default_medium), rocketImageView);
                }
            } else {
                //rocketImageView.setImageResource(R.drawable.rocket_small);
                ImageUtils.setImage(mContext.getString(R.string.default_medium), rocketImageView);
            }

            if (!TextUtils.isEmpty(rocket.getName())) {
                rocketNameTextView.setText(rocket.getName());
            } else {
                rocketNameTextView.setText(mContext.getString(R.string.label_unknown));
            }

            if (!TextUtils.isEmpty(rocket.getDescription())) {
                rocketDescriptionTextView.setText(rocket.getDescription());
            } else {
                rocketDescriptionTextView.setText(mContext.getString(R.string.label_unknown));
            }
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(mRockets.get(getAdapterPosition()).getRocketId());
        }
    }
}
