package com.okmart.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import com.okmart.app.activities.DeliverNowActivity;
import com.okmart.app.base_fragments.OffersFragment;
import com.okmart.app.model.SuccessfulOffersModel;
import com.okmart.app.pagination.BaseViewHolder;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.List;

public class SuccessfulOffersAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<SuccessfulOffersModel> successfulDataList;
    public int expandedPosition = -1;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private OffersFragment offersFragment;


    public SuccessfulOffersAdapter(Context context, List<SuccessfulOffersModel> successfulDataList,
                                   OffersFragment offersFragment)
    {
        this.context = context;
        this.successfulDataList = successfulDataList;
        this.offersFragment=offersFragment;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(context).inflate(R.layout.layout_successful, parent, false);
        return new MyViewHolder(view);*/

        BaseViewHolder viewHolder=null;
        switch (viewType)
        {
            case VIEW_TYPE_NORMAL:
                viewHolder = new SuccessfulOffersAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_successful,parent,false));
                break;

            case VIEW_TYPE_LOADING:
                viewHolder = new SuccessfulOffersAdapter.ProgressHolder(LayoutInflater.from(parent.getContext())
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
        //return successfulDataList.size();
        return  successfulDataList == null ? 0 : successfulDataList.size();

    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible)
        {
            return position == successfulDataList.size()-1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        }
        else
        {
            return VIEW_TYPE_NORMAL;
        }
    }

    public class MyViewHolder extends BaseViewHolder {

        private ImageView image_thumbnail, more, less;
        private TextView tv_seconds, tv_product_name, tv_bid_price, tv_current_price, tv_actual_price,
                tv_progress, tv_deliver_now, tv_delivery_status, tv_information;
        private LinearLayout shipping_details, address_details, ic_share;
        private TextView tv_name, tv_address_line_1, tv_address_line_2, tv_city, tv_state, tv_pincode, tv_phone_no;
        private SharedPreferenceUtil sharedPreferenceUtil;

        public MyViewHolder(View view) {
            super(view);

            sharedPreferenceUtil = new SharedPreferenceUtil(context);

            ic_share = view.findViewById(R.id.ic_share);
            image_thumbnail = view.findViewById(R.id.image_thumbnail);
            more = view.findViewById(R.id.more);
            less = view.findViewById(R.id.less);
            tv_seconds = view.findViewById(R.id._seconds);
            tv_product_name = view.findViewById(R.id.product_name);
            tv_bid_price = view.findViewById(R.id.bid_price);
            tv_current_price = view.findViewById(R.id.current_price);
            tv_actual_price = view.findViewById(R.id.actual_price);
            tv_progress = view.findViewById(R.id.progress);
            tv_deliver_now = view.findViewById(R.id.deliver_now);
            tv_delivery_status = view.findViewById(R.id.delivery_status);
            tv_information = view.findViewById(R.id.information);
            shipping_details = view.findViewById(R.id.shipping_details);
            address_details = view.findViewById(R.id.address_details);

            tv_name = view.findViewById(R.id.name);
            tv_address_line_1 = view.findViewById(R.id.address_line_1);
            tv_address_line_2 = view.findViewById(R.id.address_line_2);
            tv_city = view.findViewById(R.id.city);
            tv_state = view.findViewById(R.id.state);
            tv_pincode = view.findViewById(R.id.pincode);
            tv_phone_no = view.findViewById(R.id.phone_no);

            tv_address_line_2.setVisibility(View.GONE);
            tv_city.setVisibility(View.GONE);
            tv_state.setVisibility(View.GONE);
            tv_pincode.setVisibility(View.GONE);

        }

        @Override
        protected void clear() {

        }

        public void onBind(int position)
        {
            Log.e("transaction_id2", successfulDataList.get(position).getTransaction_id());

            String product_ref = successfulDataList.get(position).getProduct_ref();
            String checkout_ref = successfulDataList.get(position).getCheckout_ref();
            String order_status = successfulDataList.get(position).getOrder_status();
            String is_outlet = successfulDataList.get(position).getIs_outlet();
            String is_checkout = successfulDataList.get(position).getIs_checkout();
            String is_clicked = successfulDataList.get(position).getIs_clicked();

            Glide.with(context).load(successfulDataList.get(position).getImage_thumbnail()).into(image_thumbnail);

            tv_seconds.setText(successfulDataList.get(position).get_seconds());
            tv_product_name.setText(successfulDataList.get(position).getProduct_name());
            tv_bid_price.setText("RM " + DoubleDecimal.twoPointsComma(successfulDataList.get(position).getBid_price()));

            tv_actual_price.setPaintFlags(tv_actual_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv_actual_price.setText("RM " + DoubleDecimal.twoPointsComma(successfulDataList.get(position).getActual_price()));
            tv_current_price.setText("RM " + DoubleDecimal.twoPointsComma(successfulDataList.get(position).getCurrent_price()));

            if (is_checkout.equals("true")) {
                tv_deliver_now.setVisibility(View.GONE);
                tv_progress.setVisibility(View.VISIBLE);
            } else {
                tv_deliver_now.setVisibility(View.VISIBLE);
                tv_progress.setVisibility(View.GONE);
            }

            tv_deliver_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DeliverNowActivity.class);
                    intent.putExtra("checkout_ref", successfulDataList.get(position).getCheckout_ref());
                    context.startActivity(intent);
                }
            });

            if (order_status.equals("pending")) {
                shipping_details.setVisibility(View.GONE);
            } else {

                StringBuilder sb = new StringBuilder();
                sb.append(successfulDataList.get(position).getAddress_line_1()+", ");

                String address_line_2 = "";
                address_line_2 = successfulDataList.get(position).getAddress_line_2();

                tv_name.setText(successfulDataList.get(position).getName());

                if (address_line_2.equalsIgnoreCase("")) {
                    tv_address_line_2.setVisibility(View.GONE);
                } else {
                    sb.append(address_line_2+", ");
                    tv_address_line_2.setText(address_line_2);
                }

                sb.append(successfulDataList.get(position).getCity()+", "+successfulDataList.get(position).getState()+", "+successfulDataList.get(position).getPincode());
                tv_address_line_1.setText(sb.toString());

                tv_city.setText(successfulDataList.get(position).getCity() + ", ");
                tv_state.setText(successfulDataList.get(position).getState() + ", ");
                tv_pincode.setText(successfulDataList.get(position).getPincode());
                tv_phone_no.setText(successfulDataList.get(position).getPhone_no());

                shipping_details.setVisibility(View.VISIBLE);
            }

            if (is_outlet.equals("true")) {
                tv_information.setText("Outlet Information");
            }
            else {
                tv_information.setText("Delivery Information");
            }

            if (is_outlet.equals("true") && !(order_status.equalsIgnoreCase("delivered")))
            {
                if(order_status.equals("shipped")) {
                    tv_delivery_status.setText("Ready to pickup");
                }
                else {
                    tv_delivery_status.setText(CapitalUtils.capitalize(order_status));
//                    tv_delivery_status.setText("Outlet");
                }

            } else {
                tv_delivery_status.setText(CapitalUtils.capitalize(order_status));//"Delivery"
            }

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    expandedPosition = position;
                    notifyItemChanged(expandedPosition);
                    notifyDataSetChanged();

                    address_details.setVisibility(View.VISIBLE);
                    more.setVisibility(View.GONE);
                    less.setVisibility(View.VISIBLE);

                }
            });

            less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    address_details.setVisibility(View.GONE);
                    more.setVisibility(View.VISIBLE);
                    less.setVisibility(View.GONE);
                }
            });

            if (offersFragment.checkout_ref.equalsIgnoreCase(successfulDataList.get(position).getCheckout_ref()))
            {
                expandedPosition = position;
                offersFragment.checkout_ref="";
            }

            if (position == expandedPosition) {

                address_details.setVisibility(View.VISIBLE);
                more.setVisibility(View.GONE);
                less.setVisibility(View.VISIBLE);
            } else {

                address_details.setVisibility(View.GONE);
                more.setVisibility(View.VISIBLE);
                less.setVisibility(View.GONE);
            }

            ic_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this great deal, let’s bid it at OK Express! "+sharedPreferenceUtil.getString("share_url","") + "/product?ref=" + product_ref);
                    ((Activity) context).startActivity(Intent.createChooser(intent, "Share"));
                }
            });
        }
    }

    public class ProgressHolder extends BaseViewHolder
    {
        ProgressBar progressBar;
        public ProgressHolder(View itemView)
        {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progressBar);
        }

        @Override
        protected void clear() {

        }
    }

    public void addItems(List<SuccessfulOffersModel>successItems)
    {
        successfulDataList.addAll(successItems);
        notifyDataSetChanged();
    }

    public void addLoading()
    {
        if (successfulDataList.size()>= Constants.limit)
        {
            isLoaderVisible=true;
            successfulDataList.add(new SuccessfulOffersModel());
            notifyItemInserted(successfulDataList.size()-1);
        }
    }

    public void removeLoading()
    {
        isLoaderVisible = false;
        int position = successfulDataList.size()-1;
        try
        {
            SuccessfulOffersModel item = getItem(position);
            if (item!=null)
            {
                successfulDataList.remove(position);
                notifyItemRemoved(position);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    SuccessfulOffersModel getItem(int position) {
        return successfulDataList.get(position);
    }

    public void emptyData()
    {
        isLoaderVisible = false;
        successfulDataList.remove(successfulDataList.size()-1);
        notifyDataSetChanged();
    }

    public void clear()
    {
        successfulDataList.clear();
        notifyDataSetChanged();
    }
}