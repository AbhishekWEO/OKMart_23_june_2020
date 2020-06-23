package com.okmart.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.R;
import com.okmart.app.model.MyBiddings;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.DoubleDecimal;

import java.util.List;

public class MyBiddingAdapter extends RecyclerView.Adapter<MyBiddingAdapter.MyViewHolder>
{
    private Context context;
    private List<MyBiddings> myBiddingsList;
    private String user_ref_new;

    public MyBiddingAdapter(Context context,List<MyBiddings> myBiddingsList, String user_ref_new)
    {
        this.context=context;
        this.myBiddingsList=myBiddingsList;
        this.user_ref_new=user_ref_new;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_bidding_adpater,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        String is_edited = myBiddingsList.get(position).getIs_edited();
        Log.e("is_edited2", is_edited);

        Log.e("ndvjnvjdf2", "testing");

//        if(is_edited.equalsIgnoreCase("false") &&
//                myBiddingsList.get(position).getIs_direct_checkout().equalsIgnoreCase("false") &&
//                myBiddingsList.get(position).getUser_ref().equalsIgnoreCase(user_ref_new))
//        {
//            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
//        }
//        else {
//            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
//        }
//        else if(is_edited.equalsIgnoreCase("false") &&
//                myBiddingsList.get(position).getIs_direct_checkout().equalsIgnoreCase("false"))
//        {
//            holder.is_edited.setImageResource(R.drawable.bid_active);
//        }
//        else if (myBiddingsList.get(position).getIs_direct_checkout().equalsIgnoreCase("true"))
//        {
//            holder.is_edited.setImageResource(R.drawable.direct_checkout);
//        }
//        else
//        {
//            holder.is_edited.setImageResource(R.drawable.bid_cancelled);
//        }

        if (myBiddingsList.get(position).getIs_winner().equals("true"))
        {
            Drawable mDrawable = ContextCompat.getDrawable(context,R.drawable.biddings_bg);
            mDrawable.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
            holder.cardView.setBackground(mDrawable);

            holder.is_edited.setVisibility(View.GONE);

            //holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

            holder.tv_biddingTime.setText(myBiddingsList.get(position).getBid_time());
            holder.tv_biddingTime.setTextColor(context.getResources().getColor(R.color.white));

            String user_name = CapitalUtils.capitalize(firstWord(myBiddingsList.get(position).getUser_name()));
            String bid_price = DoubleDecimal.twoPointsComma(myBiddingsList.get(position).getBid_price());

            String text_view_str = " Yayy! <b>"+ user_name+"</b> won the item at <b> RM "+bid_price+"!</b>";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                holder.tv_name.setText(Html.fromHtml(text_view_str, Html.FROM_HTML_MODE_LEGACY));
            }
            else
            {
                holder.tv_name.setText(Html.fromHtml(text_view_str));
            }

            holder.tv_name.setTextColor(context.getResources().getColor(R.color.white));
        }
        else if(is_edited.equalsIgnoreCase("false") &&
                myBiddingsList.get(position).getIs_direct_checkout().equalsIgnoreCase("false") &&
                myBiddingsList.get(position).getUser_ref().equalsIgnoreCase(user_ref_new))
        {
            holder.tv_biddingTime.setText(myBiddingsList.get(position).getBid_time());

            Drawable mDrawable = ContextCompat.getDrawable(context,R.drawable.biddings_bg);
            mDrawable.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.baby_pink), PorterDuff.Mode.SRC_IN));
            holder.cardView.setBackground(mDrawable);
            //holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.baby_pink));

            SpannableStringBuilder builder = new SpannableStringBuilder();

            String white = CapitalUtils.capitalize(firstWord(myBiddingsList.get(position).getUser_name())) +" called for ";
            SpannableString whiteSpannable= new SpannableString(white);
            whiteSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, white.length(), 0);
            builder.append(whiteSpannable);

            StyleSpan styleSpanBold  = new StyleSpan(Typeface.BOLD);
            String red = "RM "+ DoubleDecimal.twoPointsComma(myBiddingsList.get(position).getBid_price());
            SpannableString redSpannable= new SpannableString(red);
            redSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, red.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            redSpannable.setSpan(styleSpanBold, 0, red.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            builder.append(redSpannable);

            holder.tv_name.setText(builder, TextView.BufferType.SPANNABLE);


        }
        else
        {

            holder.tv_biddingTime.setText(myBiddingsList.get(position).getBid_time());

            /*Drawable mDrawable = ContextCompat.getDrawable(context,R.drawable.biddings_bg);
            mDrawable.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN));
            holder.cardView.setBackground(mDrawable);*/
            holder.cardView.setBackgroundResource(R.drawable.bidding_white_bg);

            //holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));

            SpannableStringBuilder builder = new SpannableStringBuilder();

            String white = CapitalUtils.capitalize(firstWord(myBiddingsList.get(position).getUser_name())) +" called for ";
            SpannableString whiteSpannable= new SpannableString(white);
            whiteSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, white.length(), 0);
            builder.append(whiteSpannable);

            StyleSpan styleSpanBold  = new StyleSpan(Typeface.BOLD);
            String red = "RM "+ DoubleDecimal.twoPointsComma(myBiddingsList.get(position).getBid_price());
            SpannableString redSpannable= new SpannableString(red);
            redSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, red.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            redSpannable.setSpan(styleSpanBold, 0, red.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            builder.append(redSpannable);

            holder.tv_name.setText(builder, TextView.BufferType.SPANNABLE);
        }

    }

    @Override
    public int getItemCount() {
        return myBiddingsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tv_biddingTime,tv_name,tv_bidPrice;
        //private CardView cardView;
        private RelativeLayout cardView;
        private ImageView is_edited;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_biddingTime = itemView.findViewById(R.id.tv_biddingTime);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_bidPrice = itemView.findViewById(R.id.tv_bidPrice);
            cardView  = itemView.findViewById(R.id.cardView);
            is_edited  = itemView.findViewById(R.id.is_edited);
        }
    }

    public String firstWord(String firstWord) {
        String[] strings =firstWord.split("\\s");
        String fname=strings[0];

        return fname;//firstWord;
    }
}