package com.okmart.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.okmart.app.model.FeaturedModel;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.Vibration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.MyViewHolder> {

    private Context context;
    private List<FeaturedModel> featuredDataList;
    private long current_time_milliseconds, timer_difference;
    private String end_datetime;
    private CountDownTimer timer;
    private FirebaseFunctions mFunctions;
    private static Map<Integer, CountDownTimer> featuredTimerMap;

    private String sold_price="";
    int soldProductPosition=-1;

    public FeaturedAdapter()
    {
        Log.e("Default", "Constructor");
    }

    public FeaturedAdapter(Context context, List<FeaturedModel> featuredDataList) {
        this.context = context;
        this.featuredDataList = featuredDataList;
        mFunctions=FirebaseFunctions.getInstance();
        featuredTimerMap = new HashMap<Integer, CountDownTimer>();

        this.sold_price="";

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_featured, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        Glide.with(context).load(R.drawable.button).into(holder.imgarrow1);

        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        holder.cardView.getLayoutParams().height= (width/2) - 40;

//        Glide.with(context).load(featuredDataList.get(position).getProduct_image()).into(holder.product_image);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.error);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(featuredDataList.get(position).getProduct_image())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (holder.progressBar.isShown()) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (holder.progressBar.isShown()) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(holder.product_image);


        holder.product_name.setText(featuredDataList.get(position).getProduct_name());
        holder.current_price.setText("RM " + DoubleDecimal.twoPoints(featuredDataList.get(position).getCurrent_price()));
        holder.s_price.setText("RM " + DoubleDecimal.twoPoints(featuredDataList.get(position).getS_price()));

        holder.s_price.setPaintFlags(holder.s_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String product_status = featuredDataList.get(position).getProduct_status();

        if(product_status.equals("upcoming"))
        {
            holder.product_status.setText("Product coming soon");
            holder.tv_quantity.setVisibility(View.GONE);

        }
        else if(product_status.equals("live"))
        {

            holder.product_status.setText("Price drop in");
            holder.tv_quantity.setVisibility(View.VISIBLE);

            if (!featuredDataList.get(position).getIsSold())
            {
                if (featuredDataList.get(position).getQuantity()==1)
                {
                    holder.tv_quantity.setText("Last unit!");
                }
                else if (featuredDataList.get(position).getQuantity()==2)
                {
                    holder.tv_quantity.setText("Last 2 units!");
                }
                else if (featuredDataList.get(position).getQuantity()==3)
                {
                    holder.tv_quantity.setText("Last 3 units!");
                }
                else
                {
                    holder.tv_quantity.setText(""+featuredDataList.get(position).getQuantity()+" units left");
                }
            }
            else if(featuredDataList.get(position).getIsSold() && soldProductPosition==position)
            {
                holder.rl_price.setVisibility(View.GONE);
                holder.ll_timer.setVisibility(View.GONE);
                holder.product_status.setVisibility(View.GONE);
                holder.progressBarBottom.setVisibility(View.VISIBLE);

                holder.tv_quantity.setText("1 sold at RM "+DoubleDecimal.twoPoints(sold_price));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sold_price="";
                        soldProductPosition=-1;

                        for (int i=0;i<featuredDataList.size();i++)
                        {
                            FeaturedModel obj = new FeaturedModel();
                            obj.setProduct_name(featuredDataList.get(i).getProduct_name());
                            obj.setProduct_ref(featuredDataList.get(i).getProduct_ref());
                            obj.setS_price(featuredDataList.get(i).getS_price());
                            obj.setCurrent_price(featuredDataList.get(i).getCurrent_price());
                            obj.set_seconds(featuredDataList.get(i).get_seconds());
                            obj.setProduct_image(featuredDataList.get(i).getProduct_image());
                            obj.setProduct_status(featuredDataList.get(i).getProduct_status());
                            obj.setQuantity(featuredDataList.get(i).getQuantity());
                            if (i==position)
                            {
                                obj.setSold_quantity(0);
                                obj.setIsSold(false);
                            }
                            else
                            {
                                obj.setSold_quantity(featuredDataList.get(i).getSold_quantity());
                                obj.setIsSold(featuredDataList.get(i).getIsSold());
                            }
                            featuredDataList.remove(i);
                            featuredDataList.add(i,obj);
                        }

                        //notifyDataSetChanged();

                        setLable(holder.tv_quantity,position,holder.rl_price,holder.ll_timer,holder.product_status,holder.progressBarBottom);

                    }
                },5000);

            }

        }

        end_datetime = featuredDataList.get(position).get_seconds();

        holder.countDownTimer(end_datetime, position);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Vibration.vibration(context);

                //product_ref
//                Intent intent = new Intent(context, ProductDetailsActivity.class);
//                intent.putExtra("product_ref",featuredDataList.get(position).getProduct_ref());
//                intent.putExtra("product_status",featuredDataList.get(position).getProduct_status());
//                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return featuredDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView product_image, imgarrow1;
        public TextView product_status, product_name, current_price, s_price,tv_quantity;
        public TextView tv_hour, tv_min, tv_sec;
        private ProgressBar progressBar,progressBarBottom;
        LinearLayout ll_timer;
        private RelativeLayout rl_price;
        private CardView cardView;

        public MyViewHolder(@NonNull View view) {
            super(view);

            product_image = view.findViewById(R.id.product_image);
            imgarrow1 = view.findViewById(R.id.imgarrow1);
            product_status = view.findViewById(R.id.product_status);
            product_name = view.findViewById(R.id.product_name);
            current_price = view.findViewById(R.id.current_price);
            s_price = view.findViewById(R.id.s_price);

            tv_hour = view.findViewById(R.id.tv_hour);
            tv_min = view.findViewById(R.id.tv_min);
            tv_sec = view.findViewById(R.id.tv_sec);

            progressBar = view.findViewById(R.id.progressBar);

            progressBarBottom = view.findViewById(R.id.progressBarBottom);
            ll_timer = view.findViewById(R.id.ll_timer);
            rl_price = view.findViewById(R.id.rl_price);

            cardView = view.findViewById(R.id.cardView);
            tv_quantity = view.findViewById(R.id.tv_quantity);
        }

        public void countDownTimer(String end_datetime, int position)
        {
            current_time_milliseconds=System.currentTimeMillis();
            timer_difference=(Long.valueOf(end_datetime) * 1000)-current_time_milliseconds;

            timer=featuredTimerMap.get(position);
            timer = new CountDownTimer(timer_difference, 1000) {
                public void onTick(long leftTimeInMilliseconds) {
                    Log.e("Ftimer", String.valueOf(leftTimeInMilliseconds));

                    int seconds = (int) (leftTimeInMilliseconds / 1000) % 60 ;
                    int minutes = (int) ((leftTimeInMilliseconds / (1000*60)) % 60);
                    int hours   = (int) ((leftTimeInMilliseconds / (1000*60*60)) % 24);

                    tv_hour.setText(String.format("%02d", hours));
                    tv_min.setText(String.format("%02d", minutes));
                    tv_sec.setText(String.format("%02d", seconds));

                    if(featuredDataList.size()>0 && position!=-1)
                    {
                        try {
                            if (!featuredDataList.get(position).getIsSold())
                            {
                                rl_price.setVisibility(View.VISIBLE);
                                ll_timer.setVisibility(View.VISIBLE);
                                product_status.setVisibility(View.VISIBLE);
                                progressBarBottom.setVisibility(View.GONE);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }

                }

                public void onFinish() {
                    try
                    {
                        rl_price.setVisibility(View.GONE);
                        ll_timer.setVisibility(View.GONE);
                        product_status.setVisibility(View.GONE);
                        progressBarBottom.setVisibility(View.VISIBLE);

                        if (featuredDataList.get(position).getProduct_status().equalsIgnoreCase("live"))
                        {
                            Log.e("TimerFinish","product_ref:- "+featuredDataList.get(getAdapterPosition()).getProduct_ref()+" ,product_name: "+featuredDataList.get(getAdapterPosition()).getProduct_name());

                            if (DialogBoxError.checkInternetConnection(context))
                            {
                                getDownTimePrc(featuredDataList.get(getAdapterPosition()).getProduct_ref(),getAdapterPosition());
                            }
                            else
                            {
                                DialogBoxError.showInternetDialog(context);
                            }

                            //getProductDetail(featuredDataList.get(getAdapterPosition()).getProduct_ref(),getAdapterPosition());

                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                }
            }.start();
            featuredTimerMap.put(position,timer);
        }


        //22 jan
        private void getDownTimePrc(String product_ref, int position)
        {
            getDownTimePrice(product_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e("FeaturedAdapter","getDownTimePrc: " + hashMap);

                    HashMap<String,Object> productDowmTime = (HashMap) hashMap.get("data");

                    if (productDowmTime.size()>0)
                    {
                        String downtime_ref = productDowmTime.get("downtime_ref").toString();

                        Log.e("ProductDetailsActivity","downtime_ref: " + downtime_ref);

                        HashMap end_datetime = (HashMap) productDowmTime.get("end_datetime");
                        String endDatetime = end_datetime.get("_seconds").toString();

                        String currentPrice = productDowmTime.get("current_price").toString();

                        if (Integer.parseInt(currentPrice)>0)
                        {
                            current_price.setText("RM "+DoubleDecimal.twoPoints(currentPrice));
                            //

                            if (featuredDataList.size()>0)
                            {
                                /*for (int i=0;i<featuredDataList.size();i++)
                                {
                                    FeaturedModel obj = new FeaturedModel();
                                    obj.setProduct_name(featuredDataList.get(i).getProduct_name());
                                    obj.setProduct_ref(featuredDataList.get(i).getProduct_ref());
                                    obj.setS_price(featuredDataList.get(i).getS_price());
                                    if (i==position)
                                    {
                                        obj.setCurrent_price(currentPrice);
                                        obj.set_seconds(endDatetime);
                                    }
                                    else
                                    {
                                        obj.setCurrent_price(featuredDataList.get(i).getCurrent_price());
                                        obj.set_seconds(featuredDataList.get(i).get_seconds());
                                    }
                                    obj.setProduct_image(featuredDataList.get(i).getProduct_image());
                                    obj.setProduct_status(featuredDataList.get(i).getProduct_status());
                                    obj.setQuantity(featuredDataList.get(i).getQuantity());
                                    obj.setIsQuantity(featuredDataList.get(i).getIsQuantity());
                                    obj.setSold_quantity(featuredDataList.get(i).getSold_quantity());
                                    obj.setIsSold(featuredDataList.get(i).getIsSold());

                                    featuredDataList.remove(i);
                                    featuredDataList.add(i,obj);
                                }*/

                                try {
                                    FeaturedModel obj = new FeaturedModel();
                                    obj.setProduct_name(featuredDataList.get(position).getProduct_name());
                                    obj.setProduct_ref(featuredDataList.get(position).getProduct_ref());
                                    obj.setS_price(featuredDataList.get(position).getS_price());
                                    obj.setCurrent_price(currentPrice);
                                    obj.set_seconds(endDatetime);
                                    obj.setProduct_image(featuredDataList.get(position).getProduct_image());
                                    obj.setProduct_status(featuredDataList.get(position).getProduct_status());
                                    obj.setQuantity(featuredDataList.get(position).getQuantity());
                                    obj.setIsQuantity(featuredDataList.get(position).getIsQuantity());
                                    obj.setSold_quantity(featuredDataList.get(position).getSold_quantity());
                                    obj.setIsSold(featuredDataList.get(position).getIsSold());

                                    featuredDataList.remove(position);
                                    featuredDataList.add(position,obj);

                                    notifyItemChanged(position,featuredDataList);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            //countDownTimer(endDatetime, position);
                        }
                        else
                        {
                            getDownTimePrc(product_ref,position);

                        }

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FeaturedAdapter", "onFailure: " + e);
                    DialogBoxError.showError(context, e.getMessage());
                }
            });
        }

        private Task<HashMap>getDownTimePrice(String product_ref)
        {
            Map<String,Object> map = new HashMap<>();
            map.put("product_ref",product_ref);

            return mFunctions.getHttpsCallable("getDownPrice ").call(map)
                    .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                        @Override
                        public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                            return (HashMap)task.getResult().getData();
                        }
                    });
        }

    }

    public void closeTimer()
    {
        if (featuredTimerMap!=null)
        {
            for (CountDownTimer timer : featuredTimerMap.values())
            {
                timer.cancel();

            }
        }

    }

    private void setLable(TextView tv_quantity,int position,
                          RelativeLayout rl_price, LinearLayout ll_timer, TextView product_status, ProgressBar progressBarBottom)
    {
        if (featuredDataList.get(position).getQuantity()==1)
        {
            tv_quantity.setText("Last unit!");
        }
        else if (featuredDataList.get(position).getQuantity()==2)
        {
            tv_quantity.setText("Last 2 units!");
        }
        else if (featuredDataList.get(position).getQuantity()==3)
        {
            tv_quantity.setText("Last 3 units!");
        }
        else
        {
            tv_quantity.setText(""+featuredDataList.get(position).getQuantity()+" units left");
        }

        rl_price.setVisibility(View.VISIBLE);
        ll_timer.setVisibility(View.VISIBLE);
        product_status.setVisibility(View.VISIBLE);
        progressBarBottom.setVisibility(View.GONE);

    }

    public void soldProduct(int position,String bid_price)
    {
        this.sold_price=bid_price;
        this.soldProductPosition=position;

        if (featuredDataList.size()>0)
        {
            /*for (int i=0;i<featuredDataList.size();i++)
            {
                FeaturedModel obj = new FeaturedModel();
                obj.setProduct_name(featuredDataList.get(i).getProduct_name());
                obj.setProduct_ref(featuredDataList.get(i).getProduct_ref());
                obj.setS_price(featuredDataList.get(i).getS_price());
                obj.setCurrent_price(featuredDataList.get(i).getCurrent_price());
                obj.set_seconds(featuredDataList.get(i).get_seconds());
                obj.setProduct_image(featuredDataList.get(i).getProduct_image());
                obj.setProduct_status(featuredDataList.get(i).getProduct_status());
                obj.setQuantity(featuredDataList.get(i).getQuantity());
                if (i==position)
                {
                    obj.setSold_quantity(1);
                    obj.setIsSold(true);
                }
                else
                {
                    obj.setSold_quantity(featuredDataList.get(i).getSold_quantity());
                    obj.setIsSold(featuredDataList.get(i).getIsSold());
                }
                featuredDataList.remove(i);
                featuredDataList.add(i,obj);
            }*/

            FeaturedModel obj = new FeaturedModel();
            obj.setProduct_name(featuredDataList.get(position).getProduct_name());
            obj.setProduct_ref(featuredDataList.get(position).getProduct_ref());
            obj.setS_price(featuredDataList.get(position).getS_price());
            obj.setCurrent_price(featuredDataList.get(position).getCurrent_price());
            obj.set_seconds(featuredDataList.get(position).get_seconds());
            obj.setProduct_image(featuredDataList.get(position).getProduct_image());
            obj.setProduct_status(featuredDataList.get(position).getProduct_status());
            obj.setQuantity(featuredDataList.get(position).getQuantity());
            obj.setSold_quantity(1);
            obj.setIsSold(true);
            featuredDataList.remove(position);
            featuredDataList.add(position,obj);

            //notifyDataSetChanged();
            notifyItemChanged(position,featuredDataList);
        }


    }

    public void productQuantity(int position,int quantity)
    {
        if (featuredDataList.size()>0)
        {
            /*for (int i=0;i<featuredDataList.size();i++)
            {
                FeaturedModel obj = new FeaturedModel();
                obj.setProduct_name(featuredDataList.get(i).getProduct_name());
                obj.setProduct_ref(featuredDataList.get(i).getProduct_ref());
                obj.setS_price(featuredDataList.get(i).getS_price());
                obj.setCurrent_price(featuredDataList.get(i).getCurrent_price());
                obj.set_seconds(featuredDataList.get(i).get_seconds());
                obj.setProduct_image(featuredDataList.get(i).getProduct_image());
                obj.setProduct_status(featuredDataList.get(i).getProduct_status());
                if (i==position)
                {
                    obj.setQuantity(quantity);
                    obj.setIsQuantity(true);
                }
                else
                {
                    obj.setQuantity(featuredDataList.get(i).getQuantity());
                    obj.setIsQuantity(featuredDataList.get(i).getIsQuantity());
                }

                obj.setSold_quantity(featuredDataList.get(i).getSold_quantity());
                obj.setIsSold(featuredDataList.get(i).getIsSold());

                featuredDataList.remove(i);
                featuredDataList.add(i,obj);
            }*/

            FeaturedModel obj = new FeaturedModel();
            obj.setProduct_name(featuredDataList.get(position).getProduct_name());
            obj.setProduct_ref(featuredDataList.get(position).getProduct_ref());
            obj.setS_price(featuredDataList.get(position).getS_price());
            obj.setCurrent_price(featuredDataList.get(position).getCurrent_price());
            obj.set_seconds(featuredDataList.get(position).get_seconds());
            obj.setProduct_image(featuredDataList.get(position).getProduct_image());
            obj.setProduct_status(featuredDataList.get(position).getProduct_status());
            obj.setQuantity(quantity);
            obj.setIsQuantity(true);

            obj.setSold_quantity(featuredDataList.get(position).getSold_quantity());
            obj.setIsSold(featuredDataList.get(position).getIsSold());

            featuredDataList.remove(position);
            featuredDataList.add(position,obj);

            notifyItemChanged(position,featuredDataList);
        }

    }

}