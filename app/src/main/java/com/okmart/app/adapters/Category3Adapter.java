package com.okmart.app.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.okmart.app.base_fragments.HomeFragment;
import com.okmart.app.model.CategoryData;
import com.okmart.app.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class Category3Adapter extends RecyclerView.Adapter<Category3Adapter.MyViewHolder> {

    private Context context;
    private List<CategoryData> categoryDataList;
    private HomeFragment homeFragment;
    private int pager_position;
    int i1 = 0, i2 = 0;
    int  viewpager_position;
    static int viewPager_position=0;
    //static String isClick="";
    //static String category_ref="";


    public Category3Adapter(Context context,List<CategoryData> categoryDataList, HomeFragment homeFragment)
    {
        this.context=context;
        this.categoryDataList=categoryDataList;
        this.homeFragment = homeFragment;
        this.pager_position = pager_position;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.category3,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        holder.tv_catName.setText(categoryDataList.get(position).getName());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.error);

        if (categoryDataList.get(position).isCheck()==true)
        {
            //Glide.with(context).load(categoryDataList.get(position).getCat_selected_icon()).into(holder.img_cat);
            holder.progressBarCategory.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(categoryDataList.get(position).getCat_selected_icon())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (holder.progressBarCategory.getVisibility()==View.VISIBLE) {
                                holder.progressBarCategory.setVisibility(View.GONE);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (holder.progressBarCategory.getVisibility()==View.VISIBLE) {
                                holder.progressBarCategory.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    })
                    .into(holder.img_cat);

            holder.tv_catName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else
        {

            if(position == 0) {

                holder.progressBarCategory.setVisibility(View.VISIBLE);
            }

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(categoryDataList.get(position).getCat_icon())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (holder.progressBarCategory.getVisibility()==View.VISIBLE) {
                                holder.progressBarCategory.setVisibility(View.GONE);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (holder.progressBarCategory.getVisibility()==View.VISIBLE) {
                                holder.progressBarCategory.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    })
                    .into(holder.img_cat);

            holder.tv_catName.setTextColor(context.getResources().getColor(R.color.darkGrey2));

        }


        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (categoryDataList.get(position).isCheck()==false)
                {
                    //viewPager_position = viewpager_position;
                    Constants.isClick="true";
                    Constants.category_ref=categoryDataList.get(position).getCategory_ref();

                    homeFragment.stopCountdown();

                    homeFragment.featured(categoryDataList.get(position).getCategory_ref());
                    homeFragment.products(categoryDataList.get(position).getCategory_ref());

                    for (int i = 0; i < categoryDataList.size(); i++) {
                        String cat_name = categoryDataList.get(i).getName();
                        //String cat_image = categoryDataList.get(i).getCat_icon();
                        String cat_ref = categoryDataList.get(i).getCategory_ref();
                        String cat_icon = categoryDataList.get(i).getCat_icon();
                        String cat_selected_icon = categoryDataList.get(i).getCat_selected_icon();

                        CategoryData obj = new CategoryData();
                        obj.setName(cat_name);
                        //obj.setCat_icon(cat_image);
                        obj.setCategory_ref(cat_ref);
                        obj.setCat_icon(cat_icon);
                        obj.setCat_selected_icon(cat_selected_icon);

                        if (i == position) {
                            obj.setCheck(true);
                        } else {
                            obj.setCheck(false);
                        }
                        categoryDataList.remove(i);
                        categoryDataList.add(i, obj);

                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryDataList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_main;
        private ImageView img_cat;
        private TextView tv_catName;
        private ProgressBar progressBarCategory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rl_main=itemView.findViewById(R.id.rl_main);
            img_cat=itemView.findViewById(R.id.img_cat);
            tv_catName=itemView.findViewById(R.id.tv_catName);

            progressBarCategory = itemView.findViewById(R.id.progressBarCategory);
        }
    }

    /*public void getViewPagerPosition(int position)
    {
        viewpager_position = position;


    }*/

}