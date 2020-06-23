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
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.R;
import com.okmart.app.model.PurchaseRefundModel;
import com.okmart.app.pagination.BaseViewHolder;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SecondsToDateTime;

import java.util.List;


public class PurchaseRefundAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<PurchaseRefundModel> purchaseRefundListModels;
    private Context context;
    public int expandedPosition = -1;
    private String status_ref;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;


    public PurchaseRefundAdapter(Context context, List<PurchaseRefundModel> purchaseRefundListModels,
                                 String status_ref)
    {
        this.context=context;
        this.purchaseRefundListModels = purchaseRefundListModels;
        this.status_ref=status_ref;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(context).inflate(R.layout.layout_purchase_refund, parent, false);
        return new MyViewHolder(view);*/
        BaseViewHolder viewHolder=null;
        switch (viewType)
        {
            case VIEW_TYPE_NORMAL:
                viewHolder = new PurchaseRefundAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_purchase_refund,parent,false));
                break;

            case VIEW_TYPE_LOADING:
                viewHolder = new PurchaseRefundAdapter.ProgressHolder(LayoutInflater.from(parent.getContext())
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
        //return purchaseRefundListModels.size();

        return purchaseRefundListModels == null ? 0 : purchaseRefundListModels.size();

    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible)
        {
            return position == purchaseRefundListModels.size()-1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        }
        else
        {
            return VIEW_TYPE_NORMAL;
        }
    }

    public class MyViewHolder extends BaseViewHolder {

        private TextView tv_txn_type, tv_seconds, tv_price, tv_product_name, tv_transaction_id, tv_current_price;
        private ImageView more, less;
        private LinearLayout details;
        private RelativeLayout more2, less2;
        private LinearLayout relativeLayout;

        public MyViewHolder(@NonNull View view) {
            super(view);

            tv_txn_type = view.findViewById(R.id.txn_type);
            tv_seconds = view.findViewById(R.id._seconds);
            tv_price = view.findViewById(R.id.price);

            tv_product_name = view.findViewById(R.id.product_name);
            tv_transaction_id = view.findViewById(R.id.transaction_id);
            tv_current_price = view.findViewById(R.id.current_price);

            more = view.findViewById(R.id.more);
            less = view.findViewById(R.id.less);

            more2 = view.findViewById(R.id.more2);
            less2 = view.findViewById(R.id.less2);

            relativeLayout = view.findViewById(R.id.relativeLayout);

            details = view.findViewById(R.id.details);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position)
        {
            String txn_type = purchaseRefundListModels.get(position).getTxn_type();
            String _seconds = purchaseRefundListModels.get(position).get_seconds();
            String price = purchaseRefundListModels.get(position).getPrice();
            String product_name = purchaseRefundListModels.get(position).getProduct_name();
            String transaction_id = purchaseRefundListModels.get(position).getTransaction_id();
            String current_price = purchaseRefundListModels.get(position).getCurrent_price();

            if(txn_type.equalsIgnoreCase("debit")) {
                tv_txn_type.setText("Debit");

                tv_txn_type.setTextColor(context.getResources().getColor(R.color.red));
                tv_seconds.setTextColor(context.getResources().getColor(R.color.red));
                tv_price.setTextColor(context.getResources().getColor(R.color.red));
            }
            else if(txn_type.equalsIgnoreCase("shipping")) {
                tv_txn_type.setText("Shipping");

                tv_txn_type.setTextColor(context.getResources().getColor(R.color.red));
                tv_seconds.setTextColor(context.getResources().getColor(R.color.red));
                tv_price.setTextColor(context.getResources().getColor(R.color.red));
            }
            else if(txn_type.equalsIgnoreCase("refund")) {
                tv_txn_type.setTextColor(context.getResources().getColor(R.color.green));
                tv_seconds.setTextColor(context.getResources().getColor(R.color.green));
                tv_price.setTextColor(context.getResources().getColor(R.color.green));

                tv_txn_type.setText("Refund");
            }

            tv_seconds.setText(SecondsToDateTime.conversionDate(_seconds));
            tv_price.setText("RM " + DoubleDecimal.twoPointsComma(price));
            tv_product_name.setText(CapitalUtils.capitalize(product_name));
            tv_transaction_id.setText(transaction_id);
            tv_current_price.setText("RM " + DoubleDecimal.twoPointsComma(current_price));

            more2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    expandedPosition = position;
                    notifyItemChanged(expandedPosition);
                    notifyDataSetChanged();

                    //status_ref="";

                    details.setVisibility(View.VISIBLE);
                    more.setVisibility(View.GONE);
                    less.setVisibility(View.VISIBLE);

                }
            });

            less2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //status_ref="";

                    details.setVisibility(View.GONE);
                    more.setVisibility(View.VISIBLE);
                    less.setVisibility(View.GONE);
                }
            });

            if (purchaseRefundListModels.get(position).getTransaction_ref().equalsIgnoreCase(status_ref))
            {
                expandedPosition = position;
                status_ref="";

            }

            if (position == expandedPosition) {

                Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                details.startAnimation(anim);


//                LayoutTransition layoutTransition = relativeLayout.getLayoutTransition();
//                layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

                details.setVisibility(View.VISIBLE);
                more.setVisibility(View.GONE);
                less.setVisibility(View.VISIBLE);

//                expandedPosition=-1;

            } else {

//                LayoutTransition layoutTransition = relativeLayout.getLayoutTransition();
//                layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

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

    public void addItems(List<PurchaseRefundModel>purchaseItems)
    {
        purchaseRefundListModels.addAll(purchaseItems);
        notifyDataSetChanged();
    }

    public void addLoading()
    {
        if (purchaseRefundListModels.size()>= Constants.limit)
        {
            isLoaderVisible=true;
            purchaseRefundListModels.add(new PurchaseRefundModel());
            notifyItemInserted(purchaseRefundListModels.size()-1);
        }
    }

    public void removeLoading()
    {
        isLoaderVisible = false;
        int position = purchaseRefundListModels.size()-1;
        try
        {
            PurchaseRefundModel item = getItem(position);
            if (item != null)
            {
                purchaseRefundListModels.remove(position);
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
        purchaseRefundListModels.remove(purchaseRefundListModels.size()-1);
        notifyDataSetChanged();
    }

    PurchaseRefundModel getItem(int position) {
        return purchaseRefundListModels.get(position);
    }

    public void clear()
    {
        purchaseRefundListModels.clear();
        notifyDataSetChanged();
    }
}