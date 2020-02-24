package com.about.future.spacex.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.about.future.spacex.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private List<String> mGallery = new ArrayList<>();
    private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(int buttonId, String imageUrl, int position);
    }

    public GalleryAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    public void setGallery(List<String> gallery) {
        mGallery = gallery;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(mGallery.get(position));
    }

    @Override
    public int getItemCount() {
        return mGallery == null ? 0 : mGallery.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.gallery_iv)
        ImageView galleryImageView;
        @BindView(R.id.previous_iv)
        ImageView previousImageView;
        @BindView(R.id.next_iv)
        ImageView nextImageView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            galleryImageView.setOnClickListener(this);
            previousImageView.setOnClickListener(this);
            nextImageView.setOnClickListener(this);
        }

        private void bindTo(String imageUrl) {
            Picasso.get()
                    .load(imageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(galleryImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Yay!
                            //mVatCardView.setVisibility(View.VISIBLE);
                            //mVatProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            // Try again online, if cache loading failed
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(galleryImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            // Yay!
                                            //mVatCardView.setVisibility(View.VISIBLE);
                                            //mVatProgressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            // Try again online
                                            Picasso.get()
                                                    .load(imageUrl)
                                                    .error(R.drawable.falcon1_gallery)
                                                    .into(galleryImageView);

                                            //mVatCardView.setVisibility(View.VISIBLE);
                                            //mVatProgressBar.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mOnClickListener.onItemClickListener(view.getId(), mGallery.get(position), position);
        }
    }
}
