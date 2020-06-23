package com.okmart.app.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

public class BannerAdapter extends PagerAdapter {
    private Context context;
    private List<String> images;

    private String TAG = BannerAdapter.class.getSimpleName();

    public BannerAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_banner, container, false   );

        final ProgressBar progressBarBanner = view.findViewById(R.id.progressBarBanner);

        ImageView image = view.findViewById(R.id.image_slider);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.error);

//        Glide.with(context).load(images.get(position)).into(image);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(images.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (progressBarBanner.getVisibility()==View.VISIBLE) {
                            progressBarBanner.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (progressBarBanner.getVisibility()==View.VISIBLE) {
                            progressBarBanner.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(image);


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