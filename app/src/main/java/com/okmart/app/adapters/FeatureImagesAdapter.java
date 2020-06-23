package com.okmart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.okmart.app.R;

import java.util.List;

public class FeatureImagesAdapter extends RecyclerView.Adapter<FeatureImagesAdapter.MyViewHolder>  {

    private Context context;
    private List<String> images;

    private String TAG = FeatureImagesAdapter.class.getSimpleName();

    public FeatureImagesAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_feature_images, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        Glide.with(context).load(images.get(position)).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);

        }
    }
}
