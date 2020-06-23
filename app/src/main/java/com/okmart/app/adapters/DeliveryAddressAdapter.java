package com.okmart.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.R;
import com.okmart.app.activities.DeliverNowActivity;
import com.okmart.app.model.DeliveryAddressModel;

import java.util.List;


public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.MyViewHolder> {

    private Context context;
    private List<DeliveryAddressModel> deliveryAddressDataList;
    public String address_ref="";

    public DeliveryAddressAdapter(Context context, List<DeliveryAddressModel> deliveryAddressDataList) {
        this.context = context;
        this.deliveryAddressDataList = deliveryAddressDataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_delivery_address, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String is_default = deliveryAddressDataList.get(position).getIs_default();
        String ref = deliveryAddressDataList.get(position).getAddress_ref();
        String address_line_2 = deliveryAddressDataList.get(position).getAddress_line_2().trim();

        holder.name.setText(deliveryAddressDataList.get(position).getName());
        //holder.address_line_1.setText(deliveryAddressDataList.get(position).getAddress_line_1());

        StringBuilder sb = new StringBuilder();
        sb.append(deliveryAddressDataList.get(position).getAddress_line_1()+", ");

        if(address_line_2.equalsIgnoreCase(""))
        {
            holder.address_line_2.setVisibility(View.GONE);
        }
        else
        {
            holder.address_line_2.setText(address_line_2);
            sb.append(address_line_2+", ");
        }

        holder.city.setText(deliveryAddressDataList.get(position).getCity() + ", ");
        holder.state.setText(deliveryAddressDataList.get(position).getState()+ ", ");
        holder.pincode.setText(deliveryAddressDataList.get(position).getPincode());
        holder.phone_no.setText(deliveryAddressDataList.get(position).getPhone_no());

        sb.append(deliveryAddressDataList.get(position).getCity()+", "+deliveryAddressDataList.get(position).getState()+", "+deliveryAddressDataList.get(position).getPincode());
        holder.address_line_1.setText(sb.toString());

//        if(is_default.equals("true"))
//        {
//            address_ref = deliveryAddressDataList.get(position).getAddress_ref();
//            holder.is_default.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            holder.is_default.setVisibility(View.INVISIBLE);
//        }

        if(address_ref.equals(ref))
        {
            holder.check.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.check.setVisibility(View.INVISIBLE);
        }

        holder.main_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //address_ref = deliveryAddressDataList.get(position).getAddress_ref();
                ((DeliverNowActivity)context).references(deliveryAddressDataList.get(position).getAddress_ref(), "false");
                //notifyDataSetChanged();
            }
        });

        if (position==deliveryAddressDataList.size()-1)
        {
            holder.tv_divider.setVisibility(View.GONE);
        }
        else
        {
            holder.tv_divider.setVisibility(View.VISIBLE);
        }

    }

    public void setAddressRef(String address_ref)
    {
        this.address_ref=address_ref;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return deliveryAddressDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name, address_line_1, address_line_2, city, state, pincode, phone_no;
        private ImageView check;
        private RelativeLayout main_view;
        private TextView tv_divider;

        public MyViewHolder(View view)
        {
            super(view);

            name = view.findViewById(R.id.name);
            address_line_1 = view.findViewById(R.id.address_line_1);
            address_line_2 = view.findViewById(R.id.address_line_2);
            city = view.findViewById(R.id.city);
            state = view.findViewById(R.id.state);
            pincode = view.findViewById(R.id.pincode);
            phone_no = view.findViewById(R.id.phone_no);
            check = view.findViewById(R.id.check);
            main_view = view.findViewById(R.id.main_view);
            tv_divider = view.findViewById(R.id.tv_divider);

            address_line_2.setVisibility(View.GONE);
            city.setVisibility(View.GONE);
            state.setVisibility(View.GONE);
            pincode.setVisibility(View.GONE);

        }
    }
}