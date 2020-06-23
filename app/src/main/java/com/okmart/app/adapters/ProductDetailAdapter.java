package com.okmart.app.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.okmart.app.R;

import java.util.List;

public class ProductDetailAdapter extends PagerAdapter {
    private Context context;
    private List<String> images;
    private String isScrollDown;

    public ProductDetailAdapter(Context context, List<String> images,String isScrollDown)
    {
        this.context=context;
        this.images=images;
        this.isScrollDown=isScrollDown;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = LayoutInflater.from(context).inflate(R.layout.product_images, container, false   );

        ImageView product_images = view.findViewById(R.id.product_images);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        if (isScrollDown.equals("true"))
        {
            product_images.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        else
        {
            product_images.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }


//        Glide.with(context).load(images.get(position)).into(product_images);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.error);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(images.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (progressBar.getVisibility()==View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (progressBar.getVisibility()==View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(product_images);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
