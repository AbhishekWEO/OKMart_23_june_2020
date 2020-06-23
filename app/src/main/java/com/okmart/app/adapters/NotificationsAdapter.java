package com.okmart.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.activities.DashboardActivity;
import com.okmart.app.activities.DeliverNowActivity;
import com.okmart.app.base_fragments.OffersFragment;
import com.okmart.app.base_fragments.WalletFragment;
import com.okmart.app.model.NotificationsModel;
import com.okmart.app.pagination.BaseViewHolder;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.FragmentUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsAdapter extends RecyclerView.Adapter<BaseViewHolder>   {

    private List<NotificationsModel> notificationsListModels;
    private Context context;
    private FirebaseFunctions mFunctions;
    private String TAG = NotificationsAdapter.class.getSimpleName();

    //30jan
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    //end

    public NotificationsAdapter(Context context, List<NotificationsModel> notificationsListModels) {
        this.context = context;
        this.notificationsListModels = notificationsListModels;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_notifications, parent, false);

        return new MyViewHolder(item`View);*/

        BaseViewHolder viewHolder = null;
        switch (viewType) {

            case VIEW_TYPE_NORMAL:
                viewHolder = new NotificationsAdapter.MyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_notifications, parent, false));
                break;

            case VIEW_TYPE_LOADING:
                viewHolder = new NotificationsAdapter.ProgressHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_loading, parent, false));
                break;

            default:
                return null;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position)
    {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible)
        {
            return position == notificationsListModels.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        }
        else
        {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        //return notificationsListModels.size();
        return notificationsListModels == null ? 0 : notificationsListModels.size();
    }

    public class MyViewHolder extends BaseViewHolder {

        private TextView heading, message, clear;
        private ProgressBar progressBarClear;
        private LinearLayout card_view;

        public MyViewHolder(View view) {
            super(view);
            heading = view.findViewById(R.id.heading);
            message = view.findViewById(R.id.message);
            clear = view.findViewById(R.id.clear);
            progressBarClear = view.findViewById(R.id.progressBarClear);
            card_view = view.findViewById(R.id.card_view);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position)
        {
            mFunctions = FirebaseFunctions.getInstance();

            String is_read = notificationsListModels.get(position).getIs_read();
            String is_clear = notificationsListModels.get(position).getIs_clear();
            String status = notificationsListModels.get(position).getStatus();
            String status_ref = notificationsListModels.get(position).getStatus_ref();
            String heading_ = notificationsListModels.get(position).getHeading();
            String msg = notificationsListModels.get(position).getMessage();
            String notification_ref = notificationsListModels.get(position).getNotification_ref();
            String order_status = notificationsListModels.get(position).getOrder_status();

            heading.setText(heading_);
            message.setText(msg);

            if(!(is_read.equals("true"))) {
                card_view.setBackgroundColor(context.getResources().getColor(R.color.notification_grey));
            }
            else
            {
                card_view.setBackgroundColor(context.getResources().getColor(R.color.white));
            }

            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(status.equals("order")) {
                        if(order_status.equals("pending")) {
                            Intent intent = new Intent(context, DeliverNowActivity.class);
                            intent.putExtra("checkout_ref", status_ref);
                            context.startActivity(intent);
                        }
                        else
                        {
                            DashboardActivity.navigation.setSelectedItemId(R.id.action_my_offers);
                            Fragment fragment = new OffersFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("my_offer_screen", "successfull");
                            bundle.putString("status_ref",notificationsListModels.get(position).getStatus_ref());
                            fragment.setArguments(bundle);
                            FragmentUtils.addFragmentsInBarContainer(fragment,  ((AppCompatActivity) context).getSupportFragmentManager());
                        }

                    }
                    else if(status.equals("recharge")) {
                        DashboardActivity.navigation.setSelectedItemId(R.id.action_wallet);
                        Fragment fragment = new WalletFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("my_wallet_screen", "recharge");
                        bundle.putString("status_ref",notificationsListModels.get(position).getStatus_ref());
                        fragment.setArguments(bundle);
                        FragmentUtils.addFragmentsInBarContainer(fragment,  ((AppCompatActivity) context).getSupportFragmentManager());
                    }
                    if(status.equals("wallet")) {
                        DashboardActivity.navigation.setSelectedItemId(R.id.action_wallet);
                        Fragment fragment = new WalletFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("my_wallet_screen", "purchase");
                        bundle.putString("ref", status_ref);
                        bundle.putString("status_ref",notificationsListModels.get(position).getStatus_ref());
                        fragment.setArguments(bundle);
                        FragmentUtils.addFragmentsInBarContainer(fragment,  ((AppCompatActivity) context).getSupportFragmentManager());
                    }

                    markNotificationAsRead(notification_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                        @Override
                        public void onSuccess(HashMap hashMap) {
                            Log.e(TAG, "onSuccess: " + hashMap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.e(TAG, "onFailure: " + e);
                            DialogBoxError.showError(context, context.getString(R.string.something_went_wrong));
                        }
                    });

                }
            });

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    clear.setVisibility(View.GONE);
//                    progressBarClear.setVisibility(View.VISIBLE);

//                    06-03-2020

                    notificationsListModels.remove(position);
                    notifyDataSetChanged();


                    if(is_read.equals("false")) {

                        markNotificationAsRead(notification_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap) {
                                Log.e(TAG, "onSuccess: " + hashMap);

                                clearNotification(notification_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                                    @Override
                                    public void onSuccess(HashMap hashMap) {
                                        Log.e(TAG, "onSuccess: " + hashMap);

                                        clear.setVisibility(View.VISIBLE);
                                        progressBarClear.setVisibility(View.GONE);

//                                        notificationsListModels.remove(position);
//                                        notifyDataSetChanged();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        clear.setVisibility(View.VISIBLE);
                                        progressBarClear.setVisibility(View.GONE);

                                        Log.e(TAG, "onFailure: " + e);
                                        DialogBoxError.showError(context, context.getString(R.string.something_went_wrong));
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.e(TAG, "onFailure: " + e);
                                DialogBoxError.showError(context, context.getString(R.string.something_went_wrong));
                            }
                        });
                    }
                    else
                    {
                        clearNotification(notification_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap) {
                                Log.e(TAG, "onSuccess: " + hashMap);

                                clear.setVisibility(View.VISIBLE);
                                progressBarClear.setVisibility(View.GONE);

//                                notificationsListModels.remove(position);
//                                notifyDataSetChanged();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                clear.setVisibility(View.VISIBLE);
                                progressBarClear.setVisibility(View.GONE);

                                Log.e(TAG, "onFailure: " + e);
                                DialogBoxError.showError(context, context.getString(R.string.something_went_wrong));
                            }
                        });
                    }
                }
            });
        }
    }

    private Task<HashMap> clearNotification(String notification_ref) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("notification_ref", notification_ref);

        return mFunctions.getHttpsCallable("clearNotification").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> markNotificationAsRead(String notification_ref) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("notification_ref", notification_ref);

        return mFunctions.getHttpsCallable("markNotificationAsRead").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
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

    public void addItems(List<NotificationsModel> notificationItems)
    {
        notificationsListModels.addAll(notificationItems);
        notifyDataSetChanged();
    }
    public void addLoading()
    {
        if (notificationsListModels.size()>= Constants.limit)
        {
            isLoaderVisible = true;
            notificationsListModels.add(new NotificationsModel());
            notifyItemInserted(notificationsListModels.size()-1);
        }

    }

    public void removeLoading()
    {
        isLoaderVisible = false;
        int position = notificationsListModels.size()-1;
        try{
            NotificationsModel item = getItem(position);
            if (item != null)
            {
                notificationsListModels.remove(position);
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
        notificationsListModels.remove(notificationsListModels.size()-1);
        notifyDataSetChanged();
    }

    NotificationsModel getItem(int position) {
        return notificationsListModels.get(position);
    }

    public void clear()
    {
        notificationsListModels.clear();
        notifyDataSetChanged();
    }
}