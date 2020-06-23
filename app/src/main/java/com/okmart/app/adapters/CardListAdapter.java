package com.okmart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.R;
import com.okmart.app.model.FetchCardListModel;

import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    List<FetchCardListModel.DataBean> getCardList;
    private Context context;
    private ItemClickListener itemClickListener;
    private int lastCheckedPosition = -1;

    public CardListAdapter(Context context, List<FetchCardListModel.DataBean> getCardList, ItemClickListener itemClickListener) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.getCardList = getCardList;
        this.itemClickListener = itemClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.credit_card_list, parent, false);//card_individual_list
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        SpannableString spannableString = new SpannableString(" XXXX "+getCardList.get(position).getLast4());
//
//        spannableString.setSpan(new RelativeSizeSpan(1.2f),15,spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//        holder.carddetails.setText(spannableString);
        holder.carddetails.setText("**** **** **** "+getCardList.get(position).getLast4());

        String exp_month = ""+getCardList.get(position).getExp_month();
        String exp_year = ""+getCardList.get(position).getExp_year();
        String exp_yearlast2digit = exp_year.substring(2,4);
        holder.tv_expiredDate.setText(context.getResources().getString(R.string.expires)+" "+exp_month+"/"+exp_yearlast2digit);

        holder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.deleteCard(getCardList.get(position).getId());
            }
        });

        holder.checkbox.setChecked(position == lastCheckedPosition);

        if (position == lastCheckedPosition)
        {

            itemClickListener.clearEditTexts(getCardList.get(lastCheckedPosition).getId(),lastCheckedPosition);

        }



        /*if (position == 0){

            itemClickListener.clearEditTexts(getCardList.get(lastCheckedPosition).getId(),lastCheckedPosition);

        }*/

        if (getCardList.get(position).getBrand().equalsIgnoreCase("visa")){
            holder.imgcard.setImageResource(R.drawable.card_visa);

        } else if (getCardList.get(position).getBrand().equalsIgnoreCase("amex")){
            holder.imgcard.setImageResource(R.drawable.card_amex);

        } else if (getCardList.get(position).getBrand().equalsIgnoreCase("diners")){
            holder.imgcard.setImageResource(R.drawable.card_diners_club);

        } else if (getCardList.get(position).getBrand().equalsIgnoreCase("discover")){
            holder.imgcard.setImageResource(R.drawable.card_discover);

        } else if (getCardList.get(position).getBrand().equalsIgnoreCase("jcb")){
            holder.imgcard.setImageResource(R.drawable.card_jcb);

        } else if (getCardList.get(position).getBrand().equalsIgnoreCase("maestro")){
            holder.imgcard.setImageResource(R.drawable.card_maestro);

        } else if (getCardList.get(position).getBrand().equalsIgnoreCase("mastercard")){
            holder.imgcard.setImageResource(R.drawable.card_mastercard);

        } else if (getCardList.get(position).getBrand().equalsIgnoreCase("unionpay")){
            holder.imgcard.setImageResource(R.drawable.card_unionpay);

        } else {
            holder.imgcard.setImageResource(R.drawable.card_undefined);

        }

    }

    @Override
    public int getItemCount() {
        return getCardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView carddetails;
        private ImageView imgcard;
        private CheckBox checkbox;
        private ImageView cross;
        private TextView tv_expiredDate;


        ViewHolder(View itemView) {
            super(itemView);
            carddetails = itemView.findViewById(R.id.carddetails);
            imgcard = itemView.findViewById(R.id.imgcard);
            checkbox = itemView.findViewById(R.id.checkbox);

            tv_expiredDate = itemView.findViewById(R.id.tv_expiredDate);

            cross = itemView.findViewById(R.id.cross);

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastCheckedPosition = getAdapterPosition();

//                    itemClickListener.clearEditTexts(getCardList.get(lastCheckedPosition).getId(),lastCheckedPosition);

                    //because of this blinking problem occurs so
                    //i have a suggestion to add notifyDataSetChanged();
                    //   notifyItemRangeChanged(0, list.length);//blink list problem
                    notifyDataSetChanged();

                }
            });

        }

    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void clearEditTexts(String str, int position);
        void deleteCard(String cardid);

    }
}