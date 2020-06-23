package com.okmart.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.activities.ProductDetailsActivity;
import com.okmart.app.activities.ProductDetailsFlashActivity;
import com.okmart.app.base_fragments.SettingsFragment;
import com.okmart.app.model.FlashModel;
import com.okmart.app.model.ProductModel;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.Vibration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.okmart.app.adapters.ProductAdapter.getScreenWidth;

public class FlashAdapter extends RecyclerView.Adapter<FlashAdapter.MyViewHolder>   {

    private Context context;
    private List<ProductModel> flashDataList;
    private long current_time_milliseconds, timer_difference;
    private String end_datetime;
    private CountDownTimer timer;
    private FirebaseFunctions mFunctions;
    private static Map<Integer, CountDownTimer> flashtimerMap;
    private String sold_price="";
    private boolean flag = false;
    private String current_time;

    private String flash_down_second;
    private String down_per_price;
    private String min_price;
    private String flash_sale_order_time;
    private String current_price_;
    private long flash_down_second_milli;

    private String product_ref ;
    private boolean flag_function = true;

    public static boolean isCliclableflash=true;

    public FlashAdapter() {
        Log.e("Default", "Constructor");
    }

    public FlashAdapter(Context context, List<ProductModel> flashDataList, String current_time)
    {
        this.context=context;
        this.flashDataList = flashDataList;
        mFunctions=FirebaseFunctions.getInstance();
        flashtimerMap = new HashMap<Integer, CountDownTimer>();
        this.sold_price="";
        this.current_time = current_time;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_flash,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.product_image.getLayoutParams().height = getScreenWidth()/3;

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.error);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(flashDataList.get(position).getProduct_image())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (holder.progressBarProduct.isShown()) {
                            holder.progressBarProduct.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (holder.progressBarProduct.isShown()) {
                            holder.progressBarProduct.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(holder.product_image);

        product_ref = flashDataList.get(position).getProduct_ref();

        holder.product_name.setText(flashDataList.get(position).getProduct_name());

        flash_down_second = flashDataList.get(position).getFlash_down_second();
        down_per_price = flashDataList.get(position).getDown_per_price();
        min_price = flashDataList.get(position).getMin_price();
        flash_sale_order_time = flashDataList.get(position).getFlash_sale_order_time();
        current_price_ = flashDataList.get(position).getCurrent_price();

        if(Integer.parseInt(flash_down_second) == 1) {
            holder.price_drop_message.setText("Price drops every second");
        }
        else {
            holder.price_drop_message.setText("Price drops every " + flash_down_second + " seconds");
        }

        current_time_milliseconds=System.currentTimeMillis();
        long end_datetime_milli = Long.parseLong(flash_sale_order_time) * 1000;

        timer_difference=current_time_milliseconds-end_datetime_milli;
        flash_down_second_milli = Long.parseLong(flash_down_second)*1000;

        long n = (timer_difference) / flash_down_second_milli;

        current_price_ = String.valueOf(Double.parseDouble(current_price_) - (n*Double.parseDouble(down_per_price)));

        if(Double.parseDouble(current_price_) < (Double.parseDouble(min_price)))
        {
            current_price_ = min_price;

            Log.e("kvnknkdv", "kndvkdv");

            //if(flag_function)
            if (flashDataList.get(position).getIs_resetOrderTime().equals("false"))
            {

                updateFlashProductResetOrderTime(position,flashDataList.get(position).getProduct_ref()).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e("TAG", "onSuccess:userDetails2" + hashMap);

                        //flag_function = true;



                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("TAG", "onFailure: " + e);
                        DialogBoxError.showError(context, e.getMessage());
                    }
                });

                //flag_function = false;

            }

        }

        holder.current_price.setText( DoubleDecimal.twoPointsComma(current_price_));

        holder.countDownTimer(position);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Vibration.vibration(context);

                if (isCliclableflash) {
                    Intent intent = new Intent(context, ProductDetailsFlashActivity.class);
                    intent.putExtra("product_ref", flashDataList.get(position).getProduct_ref());
                    intent.putExtra("product_status", flashDataList.get(position).getProduct_status());
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return flashDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView product_image;
        public TextView product_name, current_price, price_drop_message;
        public CardView cardView;
        public ProgressBar progressBarProduct;


        public MyViewHolder(View view) {
            super(view);

            product_image = view.findViewById(R.id.product_image);
            product_name = view.findViewById(R.id.product_name);
            current_price = view.findViewById(R.id.current_price);
            price_drop_message = view.findViewById(R.id.price_drop_message);
            cardView = view.findViewById(R.id.cardView);
            progressBarProduct = view.findViewById(R.id.progressBarProduct);

        }

        public void countDownTimer(int position)
        {

            timer = flashtimerMap.get(position);
            timer = new CountDownTimer(flash_down_second_milli, 1000) {
                public void onTick(long leftTimeInMilliseconds) {

                }
                public void onFinish() {

                    change(position);
                }

            }.start();
            flashtimerMap.put(position, timer);
        }

        private void change(int position) {

            current_price_ = String.valueOf(Double.parseDouble(current_price_) - Double.parseDouble(down_per_price));
            if(Double.parseDouble(current_price_) < (Double.parseDouble(min_price))) {
                current_price_ = min_price;

            }
            notifyDataSetChanged();

        }
    }

    public void closeTimer()
    {
        if(flashtimerMap!=null)
        {
            for(CountDownTimer timer : flashtimerMap.values()){
                timer.cancel();
            }
        }
    }

    private Task<HashMap> updateFlashProductResetOrderTime(int position, String product_ref) {

        ProductModel flashModel = new ProductModel();

        flashModel.setProduct_name(flashDataList.get(position).getProduct_name());
        flashModel.setProduct_ref(flashDataList.get(position).getProduct_ref());
        flashModel.setS_price(flashDataList.get(position).getS_price());
        flashModel.setCurrent_price(current_price_);
        flashModel.setProduct_image(flashDataList.get(position).getProduct_image());
        flashModel.setFeatures(flashDataList.get(position).getFeatures());
        flashModel.setProduct_status(flashDataList.get(position).getProduct_status());
        flashModel.setIs_flash(flashDataList.get(position).getIs_flash());
        flashModel.setQuantity(flashDataList.get(position).getQuantity());

        flashModel.setFlash_down_second(flashDataList.get(position).getFlash_down_second());
        flashModel.setDown_per_price(flashDataList.get(position).getDown_per_price());
        flashModel.setMin_price(flashDataList.get(position).getMin_price());

        flashModel.setFlash_sale_order_time(flashDataList.get(position).getFlash_sale_order_time());

        flashModel.set_seconds(flashDataList.get(position).get_seconds());
        flashModel.setProduct_status_number(flashDataList.get(position).getProduct_status_number());
        flashModel.setIs_resetOrderTime("true");
        flashDataList.remove(position);
        flashDataList.add(position,flashModel);

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("product_ref", product_ref);

        return mFunctions.getHttpsCallable("updateFlashProductResetOrderTime").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }



}