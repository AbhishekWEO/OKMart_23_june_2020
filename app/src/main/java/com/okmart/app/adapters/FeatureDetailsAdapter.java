package com.okmart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.R;

import java.util.List;

public class FeatureDetailsAdapter extends RecyclerView.Adapter<FeatureDetailsAdapter.MyViewHolder>  {

    private Context context;
    private List<String> details;

    private String TAG = FeatureImagesAdapter.class.getSimpleName();

    public FeatureDetailsAdapter(Context context, List<String> details) {
        this.context = context;
        this.details = details;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_feature_details, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.details.setText("- " + details.get(position));

    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView details;

        public MyViewHolder(View view) {
            super(view);
            details = view.findViewById(R.id.details);

        }
    }
}