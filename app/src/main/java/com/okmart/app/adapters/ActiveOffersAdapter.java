package com.okmart.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.okmart.app.R;
import com.okmart.app.activities.MyBiddingsActivity;
import com.okmart.app.base_fragments.OffersFragment;
import com.okmart.app.model.ActiveOffersModel;
import com.okmart.app.utilities.DoubleDecimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveOffersAdapter extends RecyclerView.Adapter<ActiveOffersAdapter.MyViewHolder> {

    private String TAG = ActiveOffersAdapter.class.getSimpleName();
    private Context context;
    public static List<ActiveOffersModel> activeDataList;
    private long current_time_milliseconds, timer_difference, timer_difference2;
    private String end_datetime, start_datetime;
    private CountDownTimer timer;
    private int i = 0;
    private Map<Integer, CountDownTimer> timerMap;

    private OffersFragment offersFragment;

    public ActiveOffersAdapter(Context context, List<ActiveOffersModel> activeDataList,
                               OffersFragment offersFragment) {
        this.context = context;
        this.activeDataList = activeDataList;
        timerMap = new HashMap<>();
        this.offersFragment=offersFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_active, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {

            Glide.with(context).load(activeDataList.get(position).getImage_thumbnail()).into(holder.image_thumbnail);
            String offer_ref = activeDataList.get(position).getOffer_ref();

            /*holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogBox.showEditCancelOfferDialog(context, offer_ref, timer, activeDataList, timerMap, position,
                            ActiveOffersAdapter.this);
                }
            });*/

            holder.product_name.setText(activeDataList.get(position).getProduct_name());
            holder.bid_price.setText("RM " + DoubleDecimal.twoPointsComma(activeDataList.get(position).getBid_price()));

            holder.actual_price.setPaintFlags(holder.actual_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.actual_price.setText("RM " + DoubleDecimal.twoPointsComma(activeDataList.get(position).getActual_price()));
            holder.current_price.setText("RM " + DoubleDecimal.twoPointsComma(activeDataList.get(position).getCurrent_price()));

            end_datetime = activeDataList.get(position).get_seconds();
            start_datetime = activeDataList.get(position).getStart_seconds();
            current_time_milliseconds = System.currentTimeMillis();

            timer_difference = (Long.valueOf(end_datetime) * 1000) - (Long.valueOf(start_datetime) * 1000);
            timer_difference2 = (Long.valueOf(end_datetime) * 1000) - current_time_milliseconds;

            holder.mProgressBar.setMax((int) timer_difference / 1000);
            holder.mProgressBar.setProgress((int) timer_difference / 1000);

            timer = timerMap.get(position);

            timer = new CountDownTimer(timer_difference2, 1000) {
                public void onTick(long leftTimeInMilliseconds) {
                    Log.e("ActiveTimer", String.valueOf(leftTimeInMilliseconds));

                    int seconds = (int) (leftTimeInMilliseconds / 1000) % 60;
                    int minutes = (int) ((leftTimeInMilliseconds / (1000 * 60)) % 60);
                    int hours = (int) ((leftTimeInMilliseconds / (1000 * 60 * 60)) % 24);

                    holder._seconds.setText(String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds));

                    i++;
                    holder.mProgressBar.setProgress((int) (leftTimeInMilliseconds / 1000));

                }

                public void onFinish()
                {
                /*activeDataList.remove(position);
                notifyDataSetChanged();
                holder.mProgressBar.setProgress(0);*/


                    Log.e("ActiveOfferAdapter", "offer_ref: " + offer_ref);
                    for (int i = 0; i < activeDataList.size(); i++)
                    {
                        if (activeDataList.get(i).getOffer_ref().equalsIgnoreCase(offer_ref)) {
                            activeDataList.remove(i);
                            if (activeDataList.size()==0)
                            {
                                offersFragment.showNoActiveOffers();
                            }
                            else
                            {
                                notifyDataSetChanged();
                            }

                            holder.mProgressBar.setProgress(0);

                            break;
                        }
                    }

                    /*if (activeDataList.size()==0)
                    {
                        no_active_offers.setVisibility(View.VISIBLE);
                    }*/

                }
            }.start();


            holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ll_main.setFocusable(false);
                    Intent intent = new Intent(context,MyBiddingsActivity.class);
                    intent.putExtra("image_thumbnail",activeDataList.get(position).getImage_thumbnail());
                    intent.putExtra("product_name",activeDataList.get(position).getProduct_name());
                    intent.putExtra("bid_price",activeDataList.get(position).getBid_price());
                    intent.putExtra("end_datetime",activeDataList.get(position).get_seconds());
                    intent.putExtra("start_datetime",activeDataList.get(position).getStart_seconds());
                    intent.putExtra("offer_ref",activeDataList.get(position).getOffer_ref());
                    intent.putExtra("downtime_ref",activeDataList.get(position).getDowntime_ref());
                    intent.putExtra("direct_purchase_price",activeDataList.get(position).getDirect_purchase_price());
                    intent.putExtra("current_price",activeDataList.get(position).getCurrent_price());
                    intent.putExtra("product_ref",activeDataList.get(position).getProduct_ref());
                    intent.putExtra("user_ref",activeDataList.get(position).getUser_ref());
                    context.startActivity(intent);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: ");
        }

        timerMap.put(position, timer);
    }

    @Override
    public int getItemCount() {
        return activeDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_thumbnail, more;
        private TextView product_name, bid_price, current_price, actual_price, _seconds;
        private ProgressBar mProgressBar;
        private LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);

            more = view.findViewById(R.id.more);
            image_thumbnail = view.findViewById(R.id.image_thumbnail);
            product_name = view.findViewById(R.id.product_name);
            bid_price = view.findViewById(R.id.bid_price);
            current_price = view.findViewById(R.id.current_price);
            actual_price = view.findViewById(R.id.actual_price);
            _seconds = view.findViewById(R.id._seconds);
            mProgressBar = view.findViewById(R.id.progressBar);
            ll_main = view.findViewById(R.id.ll_main);
        }

    }

    public void closeTimer() {
        if (timerMap != null) {
            for (CountDownTimer timer : timerMap.values()) {
                if(timer != null){
                    timer.cancel();
                }
            }
        }
    }
}