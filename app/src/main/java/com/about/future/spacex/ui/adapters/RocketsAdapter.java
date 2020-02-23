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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RocketsAdapter extends RecyclerView.Adapter<RocketsAdapter.ViewHolder> {
    private final Context mContext;
    private List<RocketMini> mRockets = new ArrayList<RocketMini>() {
    };
    private final RocketsAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(int selectedRocket);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.rocket_card_item, parent, false);
        view.setFocusable(false);
        return new RocketsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RocketsAdapter.ViewHolder holder, int position) {

        if (!TextUtils.isEmpty(mRockets.get(position).getRocketId())) {
            switch (mRockets.get(position).getRocketId()) {
                case "falcon1":
                    holder.rocketImageView.setImageResource(R.drawable.falcon1_small);
                    break;
                case "falcon9":
                    holder.rocketImageView.setImageResource(R.drawable.falcon9_small);
                    break;
                case "falconheavy":
                    holder.rocketImageView.setImageResource(R.drawable.falcon_heavy_small);
                    break;
                case "starship":
                    holder.rocketImageView.setImageResource(R.drawable.bfr1);
                    break;
                default:
                    holder.rocketImageView.setImageResource(R.drawable.rocket_small);
            }
        } else {
            holder.rocketImageView.setImageResource(R.drawable.rocket_small);
        }

        if (!TextUtils.isEmpty(mRockets.get(position).getName())) {
            holder.rocketNameTextView.setText(mRockets.get(position).getName());
        } else {
            holder.rocketNameTextView.setText(mContext.getString(R.string.label_unknown));
        }

        if (!TextUtils.isEmpty(mRockets.get(position).getDescription())) {
            holder.rocketDescriptionTextView.setText(mRockets.get(position).getDescription());
        } else {
            holder.rocketDescriptionTextView.setText(mContext.getString(R.string.label_unknown));
        }
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

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClickListener(getAdapterPosition());
        }
    }
}
