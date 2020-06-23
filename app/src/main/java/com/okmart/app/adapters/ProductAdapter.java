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
import androidx.core.content.ContextCompat;
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
import com.okmart.app.model.ProductModel;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.Vibration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>   {

    private Context context;
    private List<ProductModel> productDataList;
    private long current_time_milliseconds, timer_difference;
    private String end_datetime;
    private CountDownTimer timer;
    private FirebaseFunctions mFunctions;
    private static Map<Integer, CountDownTimer> timerMap;
    private String sold_price="";
    private boolean flag = false;

    public static boolean isCliclable=true;

    public ProductAdapter()
    {
        Log.e("Default", "Constructor");
    }

    public ProductAdapter(Context context, List<ProductModel> productDataList)
    {
        this.context=context;
        this.productDataList = productDataList;
        mFunctions=FirebaseFunctions.getInstance();
        timerMap = new HashMap<Integer, CountDownTimer>();
        this.sold_price="";
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_product,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

//        Glide.with(context).load(R.drawable.button).into(holder.imgarrow1);

        holder.product_image.getLayoutParams().height = getScreenWidth()/2;

//        Glide.with(context).load(productDataList.get(position).getProduct_image()).into(holder.product_image);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.error);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(productDataList.get(position).getProduct_image())
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

        holder.product_name.setText(productDataList.get(position).getProduct_name());
        holder.current_price.setText( DoubleDecimal.twoPointsComma(productDataList.get(position).getCurrent_price()));
        holder.s_price.setText("RM " + DoubleDecimal.twoPointsComma(productDataList.get(position).getS_price()));
        holder.s_price.setPaintFlags(holder.s_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String product_status = productDataList.get(position).getProduct_status();


        if(product_status.equals("upcoming"))
        {
            holder.background_two.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_solid_purple_two));
            holder.ic_arrow_down.setVisibility(View.GONE);
            holder.product_status.setText("Starting in");

            holder.tv_quantity.setVisibility(View.GONE);
            holder.tv_quantity.setText("Starting in");

            setLable(holder.tv_quantity,position,holder.rl_price,holder.rl_price,holder.progressBarBottom);

        }
        else if(product_status.equals("live"))
        {
            holder.background_two.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_solid_primary_dark_two));
            holder.ic_arrow_down.setVisibility(View.VISIBLE);
            holder.product_status.setVisibility(View.GONE);
            holder.product_status.setText("Price drop in");
            holder.product_status.setVisibility(View.GONE);
            holder.tv_quantity.setVisibility(View.GONE);

            if (!productDataList.get(position).getIsSold())
            {
                setLable(holder.tv_quantity,position,holder.rl_price,holder.rl_price,holder.progressBarBottom);
            }
            else if(productDataList.get(position).getIsSold())
            {
                holder.rl_price.setVisibility(View.GONE);
                holder.rl_timer.setVisibility(View.GONE);
                holder.progressBarBottom.setVisibility(View.VISIBLE);

                if (!sold_price.isEmpty())
                {
                    holder.tv_quantity.setText("1 sold at RM "+DoubleDecimal.twoPoints(sold_price));
                }

                sold_price="";

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ProductModel obj = new ProductModel();
                        obj.setProduct_name(productDataList.get(position).getProduct_name());
                        obj.setProduct_ref(productDataList.get(position).getProduct_ref());
                        obj.setS_price(productDataList.get(position).getS_price());
                        obj.setCurrent_price(productDataList.get(position).getCurrent_price());
                        obj.set_seconds(productDataList.get(position).get_seconds());
                        obj.setProduct_image(productDataList.get(position).getProduct_image());
                        obj.setFeatures(productDataList.get(position).getFeatures());
                        obj.setProduct_status(productDataList.get(position).getProduct_status());
                        obj.setProduct_status_number(productDataList.get(position).getProduct_status_number());
                        obj.setQuantity(productDataList.get(position).getQuantity());
                        obj.setSold_quantity(0);
                        obj.setIsSold(false);

                        productDataList.remove(position);
                        productDataList.add(position,obj);

                        setLable(holder.tv_quantity,position,holder.rl_price,holder.rl_price,holder.progressBarBottom);

                    }
                },5000);
            }

        } else if(product_status.equals("sold"))
        {

            holder.background_two.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_solid_grey_two));
            holder.ic_arrow_down.setVisibility(View.GONE);
            holder.ll_timer.setVisibility(View.GONE);
            //holder.tv_quantity.setVisibility(View.GONE);
            holder.tv_quantity.setVisibility(View.GONE);
            holder.tv_quantity.setText("0 units left");
            holder.product_status.setText("Sold out");


        } else if(product_status.equals("expired"))
        {

            holder.background_two.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_solid_golden_two));
            holder.ic_arrow_down.setVisibility(View.GONE);
            //holder.product_status.setText("Product expired");
            holder.ll_timer.setVisibility(View.GONE);
            //holder.tv_quantity.setVisibility(View.GONE);

            holder.tv_quantity.setVisibility(View.GONE);
            holder.tv_quantity.setText("Expired");
            holder.product_status.setText("Expired");

        }

        end_datetime = productDataList.get(position).get_seconds();

        if (product_status.equalsIgnoreCase("live")||
                product_status.equalsIgnoreCase("upcoming"))
        {

            holder.countDownTimer(end_datetime, position);

        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Vibration.vibration(context);

                if (isCliclable)
                {
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    intent.putExtra("product_ref", productDataList.get(position).getProduct_ref());
                    intent.putExtra("product_status",productDataList.get(position).getProduct_status());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ic_arrow_down, product_image, imgarrow1;
        public TextView product_name, current_price, s_price, product_status,tv_quantity;
        public TextView tv_hour, tv_min, tv_sec;
        public LinearLayout ll_timer;
        private ProgressBar progressBarProduct,progressBarBottom;
        private RelativeLayout rl_price,rl_timer, background_two;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            ic_arrow_down = view.findViewById(R.id.ic_arrow_down);
            product_image = view.findViewById(R.id.product_image);
            imgarrow1 = view.findViewById(R.id.imgarrow1);
            product_name = view.findViewById(R.id.product_name);
            current_price = view.findViewById(R.id.current_price);
            s_price = view.findViewById(R.id.s_price);
            product_status = view.findViewById(R.id.product_status);
            rl_price = view.findViewById(R.id.rl_price);
            rl_timer = view.findViewById(R.id.rl_timer);

            tv_hour = view.findViewById(R.id.tv_hour);
            tv_min = view.findViewById(R.id.tv_min);
            tv_sec = view.findViewById(R.id.tv_sec);
            background_two = view.findViewById(R.id.background_two);

            ll_timer = view.findViewById(R.id.ll_timer);
            progressBarProduct = view.findViewById(R.id.progressBarProduct);
            progressBarBottom = view.findViewById(R.id.progressBarBottom);

            cardView = view.findViewById(R.id.cardView);
            tv_quantity = view.findViewById(R.id.tv_quantity);

        }

        public void countDownTimer(String end_datetime, int position)
        {
            current_time_milliseconds=System.currentTimeMillis();
            timer_difference=(Long.valueOf(end_datetime) * 1000)-current_time_milliseconds;

            timer = timerMap.get(position);
            timer = new CountDownTimer(timer_difference, 1000) {
                public void onTick(long leftTimeInMilliseconds) {

                    Log.e("Ptimer", String.valueOf(leftTimeInMilliseconds));
                    int seconds = (int) (leftTimeInMilliseconds / 1000) % 60 ;
                    int minutes = (int) ((leftTimeInMilliseconds / (1000*60)) % 60);
                    int hours   = (int) ((leftTimeInMilliseconds / (1000*60*60)) % 24);
                    tv_hour.setText(String.format("%02d", hours));
                    tv_min.setText(String.format("%02d", minutes));
                    tv_sec.setText(String.format("%02d", seconds));

                    if(productDataList.size()>0 && position!=-1)
                    {
                        try {
                            if (!productDataList.get(position).getIsSold())
                            {
                                rl_price.setVisibility(View.VISIBLE);
                                rl_timer.setVisibility(View.VISIBLE);
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
                        rl_timer.setVisibility(View.GONE);
                        progressBarBottom.setVisibility(View.VISIBLE);

                        if (productDataList.get(position).getProduct_status().equalsIgnoreCase("live"))
                        {
                            Log.e("TimerFinish","product_ref:- "+productDataList.get(getAdapterPosition()).getProduct_ref()+" ,product_name: "+productDataList.get(getAdapterPosition()).getProduct_name());

                            if (DialogBoxError.checkInternetConnection(context))
                            {
                                getDownTimePrc(productDataList.get(getAdapterPosition()).getProduct_ref(), getAdapterPosition());
                            }
                            else
                            {
                                DialogBoxError.showInternetDialog(context);
                            }

                        }

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            timerMap.put(position, timer);
        }

//        private void getProductDetail(String product_ref, int position)
//        {
//            productDetailRetrieve(product_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
//                @Override
//                public void onSuccess(HashMap hashMap) {
//                    Log.e("ProductAdapter", "onSuccessProductDetails: " + hashMap);
//                    HashMap<String,Object> productDetails = (HashMap) hashMap.get("productDetails");
//                    HashMap end_selling_time = (HashMap) productDetails.get("end_selling_time");
//                    String end_datetime = end_selling_time.get("_seconds").toString();
//
//                    Log.e("TimerFinish","product_ref:- "+productDetails.get("product_ref")+" ,end_datetime: "+end_datetime);
//
//                    String currentPrice=productDetails.get("current_price").toString();
//                    String s_price=productDetails.get("s_price").toString();
//
//                    Log.e("current_price",currentPrice);
//
//                    String is_featured=productDetails.get("is_featured").toString();
//                    if (is_featured.equalsIgnoreCase("false"))
//                    {
////                        current_price.setText("RM "+DoubleDecimal.twoPoints(currentPrice));
//                        product_status.setText("Price drop in");
//                        countDownTimer(end_datetime, position);
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.e("ProductAdapter", "onFailure: " + e);
//                    DialogBoxError.showError(context, context.getString(R.string.something_went_wrong));
//                }
//            });
//        }

        private Task<HashMap>productDetailRetrieve(String product_ref)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("product_ref",product_ref);

            return mFunctions.getHttpsCallable("productDetailsRetrieve").call(map)
                    .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                        @Override
                        public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                            return (HashMap)task.getResult().getData();
                        }
                    });
        }

        //22 jan
        private void getDownTimePrc(String product_ref, int position)
        {
            getDownTimePrice(product_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e("ProductAdapter","getDownTimePrc: " + hashMap);

                    HashMap<String,Object> productDowmTime = (HashMap) hashMap.get("data");

                    if (productDowmTime.size()>0)
                    {
                        String downtime_ref = productDowmTime.get("downtime_ref").toString();

                        Log.e("ProductDetailsActivity","downtime_ref: " + downtime_ref);

                        HashMap end_datetime = (HashMap) productDowmTime.get("end_datetime");
                        String endDatetime = end_datetime.get("_seconds").toString();

                        String currentPrice = productDowmTime.get("current_price").toString();

                        if (Double.parseDouble(currentPrice)>0)
                        {
                            current_price.setText("RM "+DoubleDecimal.twoPoints(currentPrice));

                            if (productDataList.size()>0)
                            {

                                try {
                                    ProductModel obj = new ProductModel();
                                    obj.setProduct_name(productDataList.get(position).getProduct_name());
                                    obj.setProduct_ref(productDataList.get(position).getProduct_ref());
                                    obj.setCurrent_price(productDataList.get(position).getCurrent_price());
                                    obj.setS_price(productDataList.get(position).getS_price());
                                    obj.setQuantity(productDataList.get(position).getQuantity());
                                    obj.set_seconds(endDatetime);
                                    obj.setProduct_image(productDataList.get(position).getProduct_image());
                                    obj.setFeatures(productDataList.get(position).getFeatures());
                                    obj.setProduct_status(productDataList.get(position).getProduct_status());
                                    obj.setProduct_status_number(productDataList.get(position).getProduct_status_number());
                                    //obj.setIsQuantity(productDataList.get(position).getIsQuantity());
                                    obj.setIsQuantity(false);
                                    obj.setSold_quantity(productDataList.get(position).getSold_quantity());
                                    obj.setIsSold(productDataList.get(position).getIsSold());

                                    obj.setIs_flash(productDataList.get(position).getIs_flash());

                                    productDataList.remove(position);
                                    productDataList.add(position,obj);

                                    notifyItemChanged(position,productDataList);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                            }

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
                    Log.e("ProductAdapter", "onFailure: " + e);
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
        if(timerMap!=null)
        {
            for(CountDownTimer timer : timerMap.values()){
                timer.cancel();
            }
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public void soldProduct(int position,String bid_price)
    {
        this.sold_price=bid_price;
        //this.soldProductPosition=position;

        if (productDataList.size()>0)
        {
            ProductModel obj = new ProductModel();
            obj.setProduct_name(productDataList.get(position).getProduct_name());
            obj.setProduct_ref(productDataList.get(position).getProduct_ref());
            obj.setS_price(productDataList.get(position).getS_price());
            obj.setCurrent_price(productDataList.get(position).getCurrent_price());
            obj.set_seconds(productDataList.get(position).get_seconds());
            obj.setProduct_image(productDataList.get(position).getProduct_image());
            obj.setFeatures(productDataList.get(position).getFeatures());
            obj.setProduct_status(productDataList.get(position).getProduct_status());
            obj.setProduct_status_number(productDataList.get(position).getProduct_status_number());
            obj.setQuantity(productDataList.get(position).getQuantity());
            obj.setSold_quantity(1);
            obj.setIsSold(true);
            //obj.setIsQuantity(productDataList.get(position).getIsQuantity());
            obj.setIsQuantity(false);
            obj.setIs_flash(productDataList.get(position).getIs_flash());

            productDataList.remove(position);
            productDataList.add(position,obj);

            //notifyDataSetChanged();
            notifyItemChanged(position,productDataList);
        }

    }

    private void setLable(TextView tv_quantity,int position,
                          RelativeLayout rl_price, RelativeLayout rl_timer, ProgressBar progressBarBottom)
    {
        if (productDataList.get(position).getQuantity()==1)
        {
            tv_quantity.setText("Last unit!");
        }
        else if (productDataList.get(position).getQuantity()==2)
        {
            tv_quantity.setText("Last 2 units!");
        }
        else if (productDataList.get(position).getQuantity()==3)
        {
            tv_quantity.setText("Last 3 units!");
        }
        else
        {
            tv_quantity.setText(""+productDataList.get(position).getQuantity()+" units left");
        }

        rl_price.setVisibility(View.VISIBLE);
        rl_timer.setVisibility(View.VISIBLE);
        progressBarBottom.setVisibility(View.GONE);

    }

    public void productQuantity(int position,int quantity)
    {
        if (productDataList.size()>0)
        {
            ProductModel obj = new ProductModel();
            obj.setProduct_name(productDataList.get(position).getProduct_name());
            obj.setProduct_ref(productDataList.get(position).getProduct_ref());
            obj.setS_price(productDataList.get(position).getS_price());
            obj.setCurrent_price(productDataList.get(position).getCurrent_price());
            obj.set_seconds(productDataList.get(position).get_seconds());
            obj.setProduct_image(productDataList.get(position).getProduct_image());
            obj.setFeatures(productDataList.get(position).getFeatures());
            obj.setProduct_status(productDataList.get(position).getProduct_status());
            obj.setProduct_status_number(productDataList.get(position).getProduct_status_number());
            obj.setQuantity(quantity);
            obj.setIsQuantity(true);
            obj.setSold_quantity(productDataList.get(position).getSold_quantity());
            obj.setIsSold(productDataList.get(position).getIsSold());

            obj.setIs_flash(productDataList.get(position).getIs_flash());

            productDataList.remove(position);
            productDataList.add(position,obj);

            //notifyDataSetChanged();
            notifyItemChanged(position,productDataList);
        }

    }

    public void currentPrice(int position, String current_price)
    {
        Log.e("PCurrentPrice",current_price+" ,Position"+position);
        if (productDataList.size()>0)
        {

            ProductModel obj = new ProductModel();
            obj.setProduct_name(productDataList.get(position).getProduct_name());
            obj.setProduct_ref(productDataList.get(position).getProduct_ref());
            obj.setS_price(productDataList.get(position).getS_price());
            obj.setQuantity(productDataList.get(position).getQuantity());
            obj.setCurrent_price(current_price);
            obj.set_seconds(productDataList.get(position).get_seconds());
            obj.setProduct_image(productDataList.get(position).getProduct_image());
            obj.setFeatures(productDataList.get(position).getFeatures());
            obj.setProduct_status(productDataList.get(position).getProduct_status());
            obj.setProduct_status_number(productDataList.get(position).getProduct_status_number());
            obj.setSold_quantity(productDataList.get(position).getSold_quantity());
            obj.setIsSold(productDataList.get(position).getIsSold());
            obj.setIsQuantity(false);

            obj.setIs_flash(productDataList.get(position).getIs_flash());

            productDataList.remove(position);
            productDataList.add(position,obj);

            notifyItemChanged(position,productDataList);
            //notifyDataSetChanged();
        }

    }

}