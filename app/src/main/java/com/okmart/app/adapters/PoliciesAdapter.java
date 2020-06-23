package com.okmart.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.R;
import com.okmart.app.activities.WebViewActivity;
import com.okmart.app.model.PoliciesModel;

import java.util.HashMap;
import java.util.List;

public class PoliciesAdapter extends RecyclerView.Adapter<PoliciesAdapter.MyViewHolder>  {

    private Context context;
    private List<PoliciesModel> policies;

    private String TAG = TermsAdapter.class.getSimpleName();

    public PoliciesAdapter(Context context, List<PoliciesModel> policies) {
        this.context = context;
        this.policies = policies;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_policies, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.title.setText(policies.get(position).getTitle());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("id", policies.get(position).getId());
                intent.putExtra("page_url", policies.get(position).getPage_url());
                intent.putExtra("title", policies.get(position).getTitle());
                ((Activity)context).startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return policies.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            relativeLayout = view.findViewById(R.id.relativeLayout);

        }
    }
}