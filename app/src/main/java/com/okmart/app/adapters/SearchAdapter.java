package com.okmart.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.okmart.app.R;
import com.okmart.app.activities.ProductDetailsActivity;
import com.okmart.app.activities.ProductDetailsFlashActivity;
import com.okmart.app.model.ProductModel;
import com.okmart.app.utilities.DialogBox;
import com.okmart.app.utilities.DialogBoxError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private Context context;
    private List<ProductModel> productDataList;
    private List<ProductModel> productDataListFiltered;

    private String TAG = TermsAdapter.class.getSimpleName();

    public SearchAdapter(Context context, List<ProductModel> productDataList, List<ProductModel> productDataListFiltered) {
        this.context = context;
        this.productDataList = productDataList;
        this.productDataListFiltered = productDataListFiltered;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_search, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.error);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(productDataListFiltered.get(position).getProduct_image())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        if (holder.progressBarProduct.isShown()) {
//                            holder.progressBarProduct.setVisibility(View.GONE);
//                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        if (holder.progressBarProduct.isShown()) {
//                            holder.progressBarProduct.setVisibility(View.GONE);
//                        }
                        return false;
                    }
                })
                .into(holder.product_image);

        holder.product_name.setText(productDataListFiltered.get(position).getProduct_name());


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogBoxError.hideKeyboard((Activity) context);

                String is_flash = productDataListFiltered.get(position).getIs_flash();

                Log.e(TAG, "is_flash: "+is_flash);

                if(is_flash.equalsIgnoreCase("true")) {
                    Intent intent = new Intent(context, ProductDetailsFlashActivity.class);
                    intent.putExtra("product_ref", productDataListFiltered.get(position).getProduct_ref());
                    intent.putExtra("product_status",productDataListFiltered.get(position).getProduct_status());
                    context.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    intent.putExtra("product_ref", productDataListFiltered.get(position).getProduct_ref());
                    intent.putExtra("product_status",productDataListFiltered.get(position).getProduct_status());
                    context.startActivity(intent);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return productDataListFiltered.size();
    }


//    // put below code (method) in Adapter class
//    public void filter(String charText) {
//        charText = charText.toLowerCase(Locale.getDefault());
//        productDataListFiltered.clear();
//        if (charText.length() == 0) {
//            productDataListFiltered.addAll(productDataList);
//        }
//        else
//        {
//            for (ProductModel wp : productDataList) {
//                if (wp.getProduct_name().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    productDataListFiltered.add(wp);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView product_image;
        TextView product_name;
        RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            product_image = view.findViewById(R.id.product_image);
            product_name = view.findViewById(R.id.product_name);
            relativeLayout = view.findViewById(R.id.relativeLayout);

        }
    }

    public void filter(List<ProductModel> tempProductDataListFiltered)
    {
        productDataListFiltered = tempProductDataListFiltered;
        notifyDataSetChanged();
    }
}