package com.okmart.app.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.okmart.app.R;
import com.okmart.app.model.FailedOffersModel;
import com.okmart.app.pagination.BaseViewHolder;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DoubleDecimal;

import java.util.List;

public class FailedOffersAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<FailedOffersModel> failedDataList;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public FailedOffersAdapter(Context context, List<FailedOffersModel> failedDataList) {
        this.context = context;
        this.failedDataList = failedDataList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(context).inflate(R.layout.layout_failed, parent, false);
        return new MyViewHolder(view);*/

        BaseViewHolder viewHolder=null;
        switch (viewType)
        {
            case VIEW_TYPE_NORMAL:
                viewHolder = new FailedOffersAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_failed,parent,false));
                break;

            case VIEW_TYPE_LOADING:
                viewHolder = new FailedOffersAdapter.ProgressHolder(LayoutInflater.from(parent.getContext())
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
        //return failedDataList.size();
        return failedDataList == null ? 0 : failedDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible)
        {
            return position == failedDataList.size()-1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        }
        else
        {
            return VIEW_TYPE_NORMAL;
        }
    }

    public class MyViewHolder extends BaseViewHolder{

        private ImageView image_thumbnail;
        private TextView _seconds, product_name, bid_price, current_price, actual_price;

        public MyViewHolder(View view)
        {
            super(view);

            image_thumbnail = view.findViewById(R.id.image_thumbnail);
            _seconds = view.findViewById(R.id._seconds);
            product_name = view.findViewById(R.id.product_name);
            bid_price = view.findViewById(R.id.bid_price);
            current_price = view.findViewById(R.id.current_price);
            actual_price = view.findViewById(R.id.actual_price);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position)
        {
            Glide.with(context).load(failedDataList.get(position).getImage_thumbnail()).into(image_thumbnail);

            _seconds.setText(failedDataList.get(position).get_seconds());
            product_name.setText(failedDataList.get(position).getProduct_name());
            bid_price.setText("RM " + DoubleDecimal.twoPointsComma(failedDataList.get(position).getBid_price()));

            actual_price.setPaintFlags(actual_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            actual_price.setText("RM " + DoubleDecimal.twoPointsComma(failedDataList.get(position).getActual_price()));
            current_price.setText("RM " + DoubleDecimal.twoPointsComma(failedDataList.get(position).getCurrent_price()));

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

    public void addItems(List<FailedOffersModel>failedItems)
    {
        failedDataList.addAll(failedItems);
        notifyDataSetChanged();
    }

    public void addLoading()
    {
        if (failedDataList.size()>= Constants.limit)
        {
            isLoaderVisible=true;
            failedDataList.add(new FailedOffersModel());
            notifyItemInserted(failedDataList.size()-1);
        }
    }

    public void removeLoading()
    {
        isLoaderVisible = false;
        int position = failedDataList.size()-1;
        try
        {
            FailedOffersModel item = getItem(position);
            if (item!=null)
            {
                failedDataList.remove(position);
                notifyItemRemoved(position);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    FailedOffersModel getItem(int position) {
        return failedDataList.get(position);
    }

    public void emptyData()
    {
        isLoaderVisible = false;
        failedDataList.remove(failedDataList.size()-1);
        notifyDataSetChanged();
    }


    public void clear()
    {
        failedDataList.clear();
        notifyDataSetChanged();
    }

}