package com.okmart.app.adapters;

import android.content.Context;
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
import com.okmart.app.model.SelfPickupAddressModel;

import java.util.List;


public class SelfPickupAddressAdapter extends RecyclerView.Adapter<SelfPickupAddressAdapter.MyViewHolder> {

    private Context context;
    private List<SelfPickupAddressModel> selfPickupAddressDataList;
    public String address_ref="";

    public SelfPickupAddressAdapter(Context context, List<SelfPickupAddressModel> selfPickupAddressDataList) {
        this.context = context;
        this.selfPickupAddressDataList = selfPickupAddressDataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_self_pickup_address, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String ref = selfPickupAddressDataList.get(position).getAddress_ref();

        holder.outlet_name.setText(selfPickupAddressDataList.get(position).getOutlet_name());
        /*holder.address_line_1.setText(selfPickupAddressDataList.get(position).getAddress_line_1());
        holder.address_line_2.setText(selfPickupAddressDataList.get(position).getAddress_line_2());
        holder.city.setText(selfPickupAddressDataList.get(position).getCity() + ", ");
        holder.state.setText(selfPickupAddressDataList.get(position).getState()+ ", ");
        holder.pincode.setText(selfPickupAddressDataList.get(position).getPincode());*/
        holder.phone_no.setText(selfPickupAddressDataList.get(position).getPhone_no());

        StringBuilder sb = new StringBuilder();
        sb.append(selfPickupAddressDataList.get(position).getAddress_line_1()+", ");
        if (selfPickupAddressDataList.get(position).getAddress_line_2().equalsIgnoreCase(""))
        {

        }
        else
        {
            sb.append(selfPickupAddressDataList.get(position).getAddress_line_2()+", ");
        }

        sb.append(selfPickupAddressDataList.get(position).getCity()+", "+selfPickupAddressDataList.get(position).getState()+", "+selfPickupAddressDataList.get(position).getPincode());
        holder.address_line_1.setText(sb.toString());


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
                // address_ref = selfPickupAddressDataList.get(position).getAddress_ref();
                ((DeliverNowActivity)context).references(selfPickupAddressDataList.get(position).getAddress_ref(), "true");
                // notifyDataSetChanged();
            }
        });

    }

    public void setAddressRef(String address_ref)
    {
        this.address_ref=address_ref;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return selfPickupAddressDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView outlet_name, address_line_1, address_line_2, city, state, pincode, phone_no;
        private ImageView check;
        private RelativeLayout main_view;

        public MyViewHolder(View view)
        {
            super(view);

            outlet_name = view.findViewById(R.id.outlet_name);
            address_line_1 = view.findViewById(R.id.address_line_1);
            address_line_2 = view.findViewById(R.id.address_line_2);
            city = view.findViewById(R.id.city);
            state = view.findViewById(R.id.state);
            pincode = view.findViewById(R.id.pincode);
            phone_no = view.findViewById(R.id.phone_no);
            check = view.findViewById(R.id.check);
            main_view = view.findViewById(R.id.main_view);

            address_line_2.setVisibility(View.GONE);
            city.setVisibility(View.GONE);
            state.setVisibility(View.GONE);
            pincode.setVisibility(View.GONE);

        }
    }
}