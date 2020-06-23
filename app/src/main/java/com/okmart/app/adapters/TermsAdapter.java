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

public class TermsAdapter extends RecyclerView.Adapter<TermsAdapter.MyViewHolder>  {

    private Context context;
    private List<String> terms;

    private String TAG = TermsAdapter.class.getSimpleName();

    public TermsAdapter(Context context, List<String> terms) {
        this.context = context;
        this.terms = terms;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_terms, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.terms.setText("- " + terms.get(position));

    }

    @Override
    public int getItemCount() {
        return terms.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView terms;

        public MyViewHolder(View view) {
            super(view);
            terms = view.findViewById(R.id.terms);

        }
    }
}