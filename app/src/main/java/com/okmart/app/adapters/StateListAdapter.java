package com.okmart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.R;
import com.okmart.app.activities.NewAddressActivity;
import com.okmart.app.activities.SignupActivity;
import com.okmart.app.model.StateListModel;
import com.okmart.app.utilities.DialogBoxState;

import java.util.List;

public class StateListAdapter extends RecyclerView.Adapter<StateListAdapter.MyViewHolder> {

    private List<StateListModel> stateListModels;
    private Context context;
    private TextView stateName;
    private String type;

    public StateListAdapter(Context context, List<StateListModel> stateListModels, TextView stateName, String type) {
        this.context = context;
        this.stateListModels = stateListModels;
        this.stateName = stateName;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_state_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        String name = stateListModels.get(position).getName();

        holder.radio.setText(name);
        holder.radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (type.equalsIgnoreCase("signup")) {
                    ((SignupActivity) context).stateRef(stateListModels.get(position).getName(), stateListModels.get(position).getState_ref());
                } else {
                    ((NewAddressActivity) context).stateRef(stateListModels.get(position).getName(), stateListModels.get(position).getState_ref());
                }

                stateName.setText(stateListModels.get(position).getName());
                DialogBoxState.hideDialog(context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stateListModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RadioButton radio;

        public MyViewHolder(View view) {
            super(view);
            radio = view.findViewById(R.id.radio);

        }
    }
}