package com.okmart.app.adapters;


import android.content.Context;
import android.content.Intent;
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
import com.okmart.app.activities.NewAddressActivity;
import com.okmart.app.model.AddressModel;

import java.util.List;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private Context context;
    private List<AddressModel> addressDataList;

    public AddressAdapter(Context context, List<AddressModel> addressDataList) {
        this.context = context;
        this.addressDataList = addressDataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_address, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String is_default = addressDataList.get(position).getIs_default();
        String address_ref = addressDataList.get(position).getAddress_ref();

        String name = addressDataList.get(position).getName();
        String address_line_1 = addressDataList.get(position).getAddress_line_1();
        String address_line_2 = addressDataList.get(position).getAddress_line_2();
        String city = addressDataList.get(position).getCity();
        String state = addressDataList.get(position).getState();
        String pincode = addressDataList.get(position).getPincode();
        String phone_no = addressDataList.get(position).getPhone_no();

        holder.name.setText(name);

        StringBuilder sb = new StringBuilder();
        sb.append(address_line_1+", ");

        //holder.address_line_1.setText(address_line_1);
        if(address_line_2.equalsIgnoreCase(""))
        {
            holder.address_line_2.setVisibility(View.GONE);
        }
        else
        {
            holder.address_line_2.setText(address_line_2);
            sb.append(address_line_2+", ");
        }

        holder.city.setText(city + ", ");
        holder.state.setText(state + ", ");
        holder.pincode.setText(pincode);
        holder.phone_no.setText(phone_no);

        sb.append(city+", "+state+", "+pincode);


        Log.e("Address",sb.toString());
        holder.address_line_1.setText(sb.toString());

        holder.main_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewAddressActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("address_line_1", address_line_1);
                intent.putExtra("address_line_2", address_line_2);
                intent.putExtra("city", city);
                intent.putExtra("state", state);
                intent.putExtra("pincode", pincode);
                intent.putExtra("phone_no", phone_no);
                intent.putExtra("address_ref", address_ref);
                intent.putExtra("is_default", is_default);
                context.startActivity(intent);
            }
        });

        if(is_default.equalsIgnoreCase("true")) {

            holder.is_default.setVisibility(View.VISIBLE);
        }
        else {

            holder.is_default.setVisibility(View.GONE);
        }

//        if(is_default.equals("true"))
//        {
//            address_ref = deliveryAddressDataList.get(position).getAddress_ref();
//            holder.is_default.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            holder.is_default.setVisibility(View.INVISIBLE);
//        }

//        if(address_ref.equals(ref))
//        {
//            holder.check.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            holder.check.setVisibility(View.INVISIBLE);
//        }

//        holder.main_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                address_ref = deliveryAddressDataList.get(position).getAddress_ref();
//                ((DeliverNowActivity)context).references(address_ref, "false");
//                notifyDataSetChanged();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return addressDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView is_default, name, address_line_1, address_line_2, city, state, pincode, phone_no;
        private ImageView edit, check;
        private RelativeLayout main_view;

        public MyViewHolder(View view)
        {
            super(view);

            is_default = view.findViewById(R.id.is_default);
            name = view.findViewById(R.id.name);
            address_line_1 = view.findViewById(R.id.address_line_1);
            address_line_2 = view.findViewById(R.id.address_line_2);
            city = view.findViewById(R.id.city);
            state = view.findViewById(R.id.state);
            pincode = view.findViewById(R.id.pincode);
            phone_no = view.findViewById(R.id.phone_no);
            edit = view.findViewById(R.id.edit);
            check = view.findViewById(R.id.check);
            main_view = view.findViewById(R.id.main_view);

            address_line_2.setVisibility(View.GONE);
            city.setVisibility(View.GONE);
            state.setVisibility(View.GONE);
            pincode.setVisibility(View.GONE);

        }
    }
}