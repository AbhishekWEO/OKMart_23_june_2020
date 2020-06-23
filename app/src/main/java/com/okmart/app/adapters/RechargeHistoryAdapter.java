package com.okmart.app.adapters;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.R;
import com.okmart.app.activities.DashboardActivity;
import com.okmart.app.model.RechargeHistoryModel;
import com.okmart.app.pagination.BaseViewHolder;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SecondsToDateTime;

import java.util.List;

public class RechargeHistoryAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<RechargeHistoryModel> rechargeHistoryListModels;
    private Context context;
    public int expandedPosition = -1;
    private int flag = 0;
    private String payment_ref;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public RechargeHistoryAdapter(Context context, List<RechargeHistoryModel> rechargeHistoryListModels,
                                  String payment_ref)
    {
        this.context=context;
        this.rechargeHistoryListModels = rechargeHistoryListModels;
        this.payment_ref=payment_ref;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(context).inflate(R.layout.layout_recharge_history, parent, false);
        return new MyViewHolder(view);*/

        BaseViewHolder viewHolder = null;
        switch (viewType) {

            case VIEW_TYPE_NORMAL:
                viewHolder = new RechargeHistoryAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_recharge_history, parent, false));
                break;

            case VIEW_TYPE_LOADING:
                viewHolder = new RechargeHistoryAdapter.ProgressHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_loading, parent, false));
                break;

            default:
                return null;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        //return rechargeHistoryListModels.size();

        return rechargeHistoryListModels == null ? 0 : rechargeHistoryListModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible)
        {
            return position == rechargeHistoryListModels.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        }
        else
        {
            return VIEW_TYPE_NORMAL;
        }
    }

    public class MyViewHolder extends BaseViewHolder {

        private TextView tv_payment_status,tv_seconds, tv_price, tv_card_holder_name, tv_card_holder_name_label,
                tv_txn_id, tv_approval_id, tv_cardno, tv_cardtype, tv_approval_id_label_label;
        private ImageView more, less, imgCard;
        private LinearLayout details;
        private LinearLayout cardView;
        private RelativeLayout more2, less2, relativeLayout, rl_approval_id, cardtypeview, rl_card_name;

        public MyViewHolder(@NonNull View view) {
            super(view);

            tv_payment_status = view.findViewById(R.id.payment_status);
            tv_seconds = view.findViewById(R.id._seconds);
            tv_price = view.findViewById(R.id.price);

            tv_card_holder_name = view.findViewById(R.id.card_holder_name);
            tv_card_holder_name_label = view.findViewById(R.id.tv_card_holder_name_label);
            tv_approval_id_label_label = view.findViewById(R.id.tv_approval_id_label_label);
            tv_txn_id = view.findViewById(R.id.txn_id);
            tv_approval_id = view.findViewById(R.id.approval_id);
            tv_cardno = view.findViewById(R.id.cardno);
            tv_cardtype = view.findViewById(R.id.cardtype);
            imgCard = view.findViewById(R.id.imgcard);

            more = view.findViewById(R.id.more);
            less = view.findViewById(R.id.less);

            more2 = view.findViewById(R.id.more2);
            less2 = view.findViewById(R.id.less2);

            relativeLayout = view.findViewById(R.id.relativeLayout);

            details = view.findViewById(R.id.details);
            cardView = view.findViewById(R.id.cardView);
            cardtypeview = view.findViewById(R.id.cardtypeview);

            rl_approval_id = view.findViewById(R.id.rl_approval_id);
            rl_card_name = view.findViewById(R.id.rl_card_name);
        }

        @Override
        protected void clear() {

        }
        public void onBind(int position)
        {
            String payment_status = rechargeHistoryListModels.get(position).getPayment_status();
            String _seconds = rechargeHistoryListModels.get(position).get_seconds();
            String price = rechargeHistoryListModels.get(position).getPrice();
            String card_holder_name = rechargeHistoryListModels.get(position).getCard_holder_name();
            String txn_id = rechargeHistoryListModels.get(position).getTxn_id();
            String payment_id = rechargeHistoryListModels.get(position).getPayment_id();
            String cardno = rechargeHistoryListModels.get(position).getCard_last4();
            String txt_card_brand = rechargeHistoryListModels.get(position).getCard_brand();
            String payment_method = rechargeHistoryListModels.get(position).getPayment_method();

            if(payment_status.equalsIgnoreCase("success")) {

                if(payment_method.equals("admin")) {

                    tv_payment_status.setText("Admin recharged");

                }
                else if(payment_method.equals("referral")) {

                    if(rechargeHistoryListModels.get(position).getIs_new_user().equals("true")) {
                        tv_payment_status.setText("Welcome Credit");
                    }
                    else {
                        tv_payment_status.setText("Referral Credit");
                    }

                }
                else {

                    tv_payment_status.setText("Successful recharged");
                }

                rl_approval_id.setVisibility(View.GONE);
                tv_payment_status.setTextColor(context.getResources().getColor(R.color.green));
                tv_seconds.setTextColor(context.getResources().getColor(R.color.green));
                tv_price.setTextColor(context.getResources().getColor(R.color.green));

            }
            else {

                rl_approval_id.setVisibility(View.GONE);
                tv_payment_status.setText("Failed recharged");

                tv_payment_status.setTextColor(context.getResources().getColor(R.color.red));
                tv_seconds.setTextColor(context.getResources().getColor(R.color.red));
                tv_price.setTextColor(context.getResources().getColor(R.color.red));

            }

            tv_seconds.setText(SecondsToDateTime.conversionDate(_seconds));
            tv_price.setText("RM " + DoubleDecimal.twoPointsComma(price));
            tv_txn_id.setText(payment_id);

            if(payment_method.equals("admin")) {

                cardtypeview.setVisibility(View.GONE);
                rl_approval_id.setVisibility(View.GONE);
                rl_card_name.setVisibility(View.GONE);

            }
            else if(payment_method.equals("referral")) {

                tv_card_holder_name_label.setText("User Name");
                tv_card_holder_name.setText(rechargeHistoryListModels.get(position).getReferral_user_name());
                cardtypeview.setVisibility(View.GONE);
                rl_approval_id.setVisibility(View.GONE);
                rl_card_name.setVisibility(View.GONE);

            }
            else
            {
                imgCard.setVisibility(View.VISIBLE);
                tv_cardtype.setVisibility(View.VISIBLE);
                tv_cardno.setVisibility(View.VISIBLE);

                cardtypeview.setVisibility(View.VISIBLE);
                rl_approval_id.setVisibility(View.GONE);
                rl_card_name.setVisibility(View.GONE);

                tv_card_holder_name.setText(card_holder_name);
                tv_approval_id.setText(txn_id);
                tv_cardno.setText(" XXXX " + cardno);

                tv_cardtype.setText("Card Type");

                if (txt_card_brand.equalsIgnoreCase("visa")){
                    imgCard.setImageResource(R.drawable.card_visa);
//                    tv_cardtype.setText("Card Type : "+CapitalUtils.capitalize(txt_card_brand));

                } else if (txt_card_brand.equalsIgnoreCase("amex")){
                    imgCard.setImageResource(R.drawable.card_amex);
//                    tv_cardtype.setText("Card Type : "+CapitalUtils.capitalize(txt_card_brand));

                } else if (txt_card_brand.equalsIgnoreCase("diners")){
                    imgCard.setImageResource(R.drawable.card_diners_club);
//                    tv_cardtype.setText("Card Type : "+CapitalUtils.capitalize(txt_card_brand));

                } else if (txt_card_brand.equalsIgnoreCase("discover")){
                    imgCard.setImageResource(R.drawable.card_discover);
//                    tv_cardtype.setText("Card Type : "+CapitalUtils.capitalize(txt_card_brand));

                } else if (txt_card_brand.equalsIgnoreCase("jcb")){
                    imgCard.setImageResource(R.drawable.card_jcb);
//                    tv_cardtype.setText("Card Type : "+CapitalUtils.capitalize(txt_card_brand));

                } else if (txt_card_brand.equalsIgnoreCase("maestro")){
                    imgCard.setImageResource(R.drawable.card_maestro);
//                    tv_cardtype.setText("Card Type : "+CapitalUtils.capitalize(txt_card_brand));

                } else if (txt_card_brand.equalsIgnoreCase("mastercard")){
                    imgCard.setImageResource(R.drawable.card_mastercard);
//                    tv_cardtype.setText("Card Type : "+CapitalUtils.capitalize(txt_card_brand));

                } else if (txt_card_brand.equalsIgnoreCase("unionpay")){
                    imgCard.setImageResource(R.drawable.card_unionpay);
//                    tv_cardtype.setText("Card Type : "+CapitalUtils.capitalize(txt_card_brand));

                }
                else {
                    imgCard.setVisibility(View.GONE);
                    tv_cardtype.setVisibility(View.GONE);
                    tv_cardno.setVisibility(View.GONE);
                    imgCard.setImageResource(R.drawable.card_undefined);
//                    tv_cardtype.setText("Card Type : "+CapitalUtils.capitalize(txt_card_brand));

                }
            }

            more2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    expandedPosition = position;
                    notifyItemChanged(expandedPosition);
                    notifyDataSetChanged();

                    details.setVisibility(View.VISIBLE);
                    more.setVisibility(View.GONE);
                    less.setVisibility(View.VISIBLE);

                }
            });

            less2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    details.setVisibility(View.GONE);
                    more.setVisibility(View.VISIBLE);
                    less.setVisibility(View.GONE);
                }
            });

            if (!rechargeHistoryListModels.get(position).getTxn_id().equals("") && rechargeHistoryListModels.get(position).getTxn_id().equalsIgnoreCase(DashboardActivity.txn_id))
            {
                expandedPosition = position;

                DashboardActivity.txn_id="";
            }
            else if (rechargeHistoryListModels.get(position).getPayment_ref().equalsIgnoreCase(payment_ref))
            {
                expandedPosition = position;
                payment_ref="";
            }

             /*if (rechargeHistoryListModels.get(position).getPayment_ref().equalsIgnoreCase(payment_ref))
            {
                expandedPosition = position;
                payment_ref="";
            }*/

            if (position == expandedPosition) {

                Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                details.startAnimation(anim);

                details.setVisibility(View.VISIBLE);
                more.setVisibility(View.GONE);
                less.setVisibility(View.VISIBLE);


            } else {

                details.setVisibility(View.GONE);
                more.setVisibility(View.VISIBLE);
                less.setVisibility(View.GONE);
            }

        }
    }

    public class ProgressHolder extends BaseViewHolder
    {
        ProgressBar progressBar;
        ProgressHolder(View itemView)
        {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progressBar);

        }
        @Override
        protected void clear() {
        }
    }

    public void addItems(List<RechargeHistoryModel> rechargeItems)
    {
        rechargeHistoryListModels.addAll(rechargeItems);
        notifyDataSetChanged();
    }
    public void addLoading()
    {
        if (rechargeHistoryListModels.size()>= Constants.limit)
        {
            isLoaderVisible = true;
            rechargeHistoryListModels.add(new RechargeHistoryModel());
            notifyItemInserted(rechargeHistoryListModels.size()-1);
        }

    }

    public void removeLoading()
    {
        isLoaderVisible = false;
        int position = rechargeHistoryListModels.size()-1;
        try
        {
            RechargeHistoryModel item = getItem(position);
            if (item != null)
            {
                rechargeHistoryListModels.remove(position);
                notifyItemRemoved(position);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void emptyData()
    {
        isLoaderVisible = false;
        rechargeHistoryListModels.remove(rechargeHistoryListModels.size()-1);
        notifyDataSetChanged();
    }

    RechargeHistoryModel getItem(int position) {
        return rechargeHistoryListModels.get(position);
    }

    public void clear()
    {
        rechargeHistoryListModels.clear();
        notifyDataSetChanged();
    }
}