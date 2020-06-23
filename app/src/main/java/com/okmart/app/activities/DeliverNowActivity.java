package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.droidbond.loadingbutton.LoadingButton;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.adapters.DeliveryAddressAdapter;
import com.okmart.app.adapters.SelfPickupAddressAdapter;
import com.okmart.app.model.DeliveryAddressModel;
import com.okmart.app.model.SelfPickupAddressModel;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliverNowActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseFunctions mFunctions;
    private String TAG = DeliverNowActivity.class.getSimpleName();
    private Context context = DeliverNowActivity.this;
    private TextView tv_delivery, tv_self_pickup, walletPoint, tv_confirm, points, product_name, bid_price,tv_noOutlet;
    private ImageView  delivery_dot_image, self_pickup_dot_image;
    private RecyclerView rv_delivery_address, rv_self_pickup_address;
    private List<DeliveryAddressModel> deliveryAddressDataList=new ArrayList<>();
    private List<SelfPickupAddressModel> selfPickupAddressDataList=new ArrayList<>();
    private String product_ref, checkout_ref, address_ref="", is_outlet, shipping_price, txt_product_name, txt_bid_price;
    private RelativeLayout rl_product_details, rl_shipping_fee, rl_address_select;
    private ProgressBar progressBarDeliver, progressBarTop;
    private LoadingButton custombtnConfirm;
    private ImageView img_back;

    private DeliveryAddressAdapter deliveryAddressAdapter;
    private SelfPickupAddressAdapter selfPickupAddressAdapter;
    private String self_pickup="delivery";
    private ProgressBar progressBarSubmit;
    private RelativeLayout rl_bottom;
    private LinearLayout add_address;
    private boolean flag = false;
    private ProgressBar progressBarWalletPoints;
    private TextView tv_selectAddresTitle;
    private SharedPreferenceUtil sharedPreferenceUtil;
//7009124287


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_now);

        self_pickup="delivery";

        getSupportActionBar().hide();

        sharedPreferenceUtil = new SharedPreferenceUtil(DeliverNowActivity.this);
        mFunctions = FirebaseFunctions.getInstance();

        tv_noOutlet = findViewById(R.id.tv_noOutlet);

        progressBarWalletPoints = findViewById(R.id.progressBarWalletPoints);

        tv_selectAddresTitle = findViewById(R.id.tv_selectAddresTitle);

        bid_price = findViewById(R.id.bid_price);
        product_name = findViewById(R.id.product_name);

        product_name = findViewById(R.id.product_name);
        product_name = findViewById(R.id.product_name);

        custombtnConfirm = findViewById(R.id.custombtnConfirm);
        custombtnConfirm.setOnClickListener(this);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_address = findViewById(R.id.add_address);
        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliverNowActivity.this, NewAddressActivity.class);
                startActivity(intent);
            }
        });

        progressBarDeliver = findViewById(R.id.progressBarDeliver);
        progressBarTop = findViewById(R.id.progressBarTop);

        RelativeLayout section = findViewById(R.id.section);
        section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReloadActivity.class);
                startActivity(intent);
            }
        });

        Bundle extras = getIntent().getExtras();
        if(!(extras == null))
        {
            if (getIntent().getStringExtra("notification_ref")!=null)
            {
                readNotification(getIntent().getStringExtra("notification_ref"));
            }

            checkout_ref = extras.getString("checkout_ref");
//            product_ref = extras.getString("product_ref");
//            shipping_price = extras.getString("shipping_price");
        }

        points=findViewById(R.id.points);

        rl_product_details = findViewById(R.id.rl_product_details);
        rl_shipping_fee = findViewById(R.id.rl_shipping_fee);
        rl_address_select = findViewById(R.id.rl_address_select);
        walletPoint = findViewById(R.id.walletPoint);

        tv_confirm = findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);

        tv_delivery = findViewById(R.id.tv_delivery);
        tv_delivery.setOnClickListener(this);

        tv_self_pickup = findViewById(R.id.tv_self_pickup);
        tv_self_pickup.setOnClickListener(this);

        delivery_dot_image = findViewById(R.id.delivery_dot_image);
        self_pickup_dot_image = findViewById(R.id.self_pickup_dot_image);
        rv_delivery_address = findViewById(R.id.rv_delivery_address);
        rv_delivery_address.setLayoutManager(new LinearLayoutManager(context));
        rv_delivery_address.setNestedScrollingEnabled(false);

        rv_self_pickup_address = findViewById(R.id.rv_self_pickup_address);
        rv_self_pickup_address.setLayoutManager(new LinearLayoutManager(context));

        custombtnConfirm.setVisibility(View.GONE);
        progressBarSubmit= findViewById(R.id.progressBarSubmit);
        rl_bottom = findViewById(R.id.rl_bottom);
        rl_bottom.setVisibility(View.GONE);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (DialogBoxError.checkInternetConnection(DeliverNowActivity.this))
        {
            walletPoints();
            deliveryAddress();
            outletAddress();
        }
        else
        {
            DialogBoxError.showInternetDialog(DeliverNowActivity.this);
        }
    }

    public void walletPoints() {
        getWalletPointOfUser().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess: " + hashMap);

                if(progressBarWalletPoints.isShown()) {

                    progressBarWalletPoints.setVisibility(View.GONE);
                }


                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    walletPoint.setText(DoubleDecimal.twoPointsComma(hashMap.get("walletPoint").toString()));

                }
                else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(DeliverNowActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(DeliverNowActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(DeliverNowActivity.this,message);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(progressBarWalletPoints.isShown()) {

                    progressBarWalletPoints.setVisibility(View.GONE);
                }

                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
            }
        });
    }


    public void outletAddress() {

        getOutletAddressAndProductDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess: " + hashMap);

                selfPickupAddressDataList.clear();


                progressBarTop.setVisibility(View.GONE);
                rl_product_details.setVisibility(View.VISIBLE);
                rl_shipping_fee.setVisibility(View.VISIBLE);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    HashMap data2 = (HashMap) hashMap.get("data");
                    shipping_price = data2.get("shipping_price").toString();
                    txt_bid_price = data2.get("bid_price").toString();
                    txt_product_name = data2.get("product_name").toString();

                    bid_price.setText(DoubleDecimal.twoPointsComma(txt_bid_price)+" PT" );
                    product_name.setText(CapitalUtils.capitalize(txt_product_name));
                    points.setText(DoubleDecimal.twoPointsComma(shipping_price)+" PT");

                    ArrayList al = (ArrayList) data2.get("outlet_address");

                    for (int i = 0; i < al.size(); i++)
                    {

                        HashMap data = (HashMap) al.get(i);

                        SelfPickupAddressModel selfPickupAddressModel= new SelfPickupAddressModel();
                        selfPickupAddressModel.setOutlet_name(CapitalUtils.capitalize(data.get("outlet_name").toString()));
                        selfPickupAddressModel.setAddress_line_1(data.get("address_line1").toString());
                        selfPickupAddressModel.setAddress_line_2(data.get("address_line2").toString());
                        selfPickupAddressModel.setCity(CapitalUtils.capitalize(data.get("city").toString()));
                        selfPickupAddressModel.setState(CapitalUtils.capitalize(data.get("state").toString()));
                        selfPickupAddressModel.setPincode(data.get("pincode").toString());
                        selfPickupAddressModel.setPhone_no(data.get("phone_no").toString());
                        selfPickupAddressModel.setAddress_ref(data.get("address_ref").toString());

                        selfPickupAddressDataList.add(selfPickupAddressModel);

                    }

                    //tv_noOutlet.setVisibility(View.GONE);
                    //rv_self_pickup_address.setVisibility(View.VISIBLE);

                    //rv_self_pickup_address.setAdapter(new SelfPickupAddressAdapter(context, selfPickupAddressDataList));
                    selfPickupAddressAdapter = new SelfPickupAddressAdapter(context, selfPickupAddressDataList);
                    rv_self_pickup_address.setAdapter(selfPickupAddressAdapter);

                    //
                    if (self_pickup.equalsIgnoreCase("selfpickup"))
                    {
                        if (selfPickupAddressDataList.size()>0)
                        {
                            rv_self_pickup_address.setVisibility(View.VISIBLE);
                            tv_confirm.setVisibility(View.VISIBLE);
                            tv_selectAddresTitle.setVisibility(View.VISIBLE);
                            tv_selectAddresTitle.setText(getResources().getString(R.string.please_select_a_nearest_outlet_for_product_pickup));
                            tv_noOutlet.setVisibility(View.GONE);
                        }
                        else
                        {
                            tv_noOutlet.setVisibility(View.VISIBLE);
                            rv_self_pickup_address.setVisibility(View.GONE);
                            tv_selectAddresTitle.setVisibility(View.GONE);
                            tv_confirm.setVisibility(View.GONE);
                        }

                        add_address.setVisibility(View.GONE);
                        rv_delivery_address.setVisibility(View.GONE);
                        rl_shipping_fee.setVisibility(View.GONE);
                        delivery_dot_image.setVisibility(View.INVISIBLE);
                        self_pickup_dot_image.setVisibility(View.VISIBLE);

                        if(progressBarDeliver.isShown())
                        {
                            progressBarDeliver.setVisibility(View.GONE);
                        }
                    }


                }
                /*else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(DeliverNowActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(DeliverNowActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(DeliverNowActivity.this,message);
                }*/



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBarTop.setVisibility(View.GONE);
                rl_product_details.setVisibility(View.VISIBLE);
                rl_shipping_fee.setVisibility(View.VISIBLE);

                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
            }
        });

    }


    public void deliveryAddress() {

        getAddress().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess: " + hashMap);

                if(progressBarDeliver.isShown())
                {
                    progressBarDeliver.setVisibility(View.GONE);
                }

                rl_address_select.setVisibility(View.VISIBLE);
                //custombtnConfirm.setVisibility(View.VISIBLE);

                rl_bottom.setVisibility(View.VISIBLE);

                deliveryAddressDataList.clear();

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    ArrayList al = (ArrayList) hashMap.get("data");

                    if(al.size()==0)
                    {

                        flag = true;

                        if (self_pickup.equalsIgnoreCase("selfpickup"))
                        {
                            if (selfPickupAddressDataList.size()==0 && selfPickupAddressAdapter!=null)
                            {
                                add_address.setVisibility(View.GONE);
                            }

                        }
                        else
                        {
                            add_address.setVisibility(View.VISIBLE);
                            tv_confirm.setVisibility(View.GONE);
                            tv_selectAddresTitle.setVisibility(View.GONE);
                        }

                        //add_address.setVisibility(View.VISIBLE);

                    }
                    else {

                        flag = false;

                        //add_address.setVisibility(View.VISIBLE);

                        for (int i = 0; i < al.size(); i++) {

                            HashMap data = (HashMap) al.get(i);

                            DeliveryAddressModel deliveryAddressModel = new DeliveryAddressModel();
                            deliveryAddressModel.setName(CapitalUtils.capitalize(data.get("name").toString()));
                            deliveryAddressModel.setAddress_line_1(data.get("address_line1").toString());
                            deliveryAddressModel.setAddress_line_2(data.get("address_line2").toString());
                            deliveryAddressModel.setCity(CapitalUtils.capitalize(data.get("city").toString()));
                            deliveryAddressModel.setState(CapitalUtils.capitalize(data.get("state").toString()));
                            deliveryAddressModel.setPincode(data.get("pincode").toString());
                            deliveryAddressModel.setPhone_no(data.get("phone_no").toString());
                            deliveryAddressModel.setAddress_ref(data.get("address_ref").toString());
                            deliveryAddressModel.setIs_default(data.get("is_default").toString());

                            deliveryAddressDataList.add(deliveryAddressModel);

                        }

                        //rv_delivery_address.setAdapter(new DeliveryAddressAdapter(context, deliveryAddressDataList));
                        deliveryAddressAdapter = new DeliveryAddressAdapter(context, deliveryAddressDataList);
                        rv_delivery_address.setAdapter(deliveryAddressAdapter);

                        if (self_pickup.equalsIgnoreCase("delivery"))//self_pickup="delivery";
                        {
                            if (deliveryAddressDataList.size()>0)
                            {
                                rv_delivery_address.setVisibility(View.VISIBLE);
                                tv_confirm.setVisibility(View.VISIBLE);
                                tv_selectAddresTitle.setVisibility(View.VISIBLE);
                                tv_selectAddresTitle.setText(getResources().getString(R.string.please_select_your_address_for_product_delivery));
                            }
                            else
                            {
                                rv_delivery_address.setVisibility(View.GONE);
                                tv_confirm.setVisibility(View.GONE);
                                tv_selectAddresTitle.setVisibility(View.GONE);
                            }

                            add_address.setVisibility(View.VISIBLE);
                            rl_shipping_fee.setVisibility(View.VISIBLE);
                            delivery_dot_image.setVisibility(View.VISIBLE);

                            rv_self_pickup_address.setVisibility(View.GONE);
                            tv_noOutlet.setVisibility(View.GONE);
                            self_pickup_dot_image.setVisibility(View.INVISIBLE);
                        }
                        else if (self_pickup.equalsIgnoreCase("selfpickup"))
                        {
                            if (selfPickupAddressDataList.size()==0 && selfPickupAddressAdapter!=null)
                            {
                                tv_noOutlet.setVisibility(View.VISIBLE);
                            }

                        }

                    }
                }
                /*else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(DeliverNowActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(DeliverNowActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(DeliverNowActivity.this,message);
                }*/



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(progressBarDeliver.isShown())
                {
                    progressBarDeliver.setVisibility(View.GONE);
                }

                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
            }
        });

    }

    public void orderPlace()
    {
        if (DialogBoxError.checkInternetConnection(DeliverNowActivity.this))
        {
            placeOrder().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {

                    progressBarSubmit.setVisibility(View.GONE);
                    tv_confirm.setText("CONFIRM");
                    img_back.setEnabled(true);
                    Log.e(TAG, "onSuccess: " + hashMap);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if(!(response_msg.equals("success")))
                    {
                    /*if (custombtnConfirm.isLoading())
                    {
                        custombtnConfirm.hideLoading();
                    }*/

                    }

                    if(response_msg.equals("address does not exist"))
                    {
                        DialogBoxError.showError(context, message);
                    }
                    else if(response_msg.equals("low wallet balance"))
                    {
                        DialogBoxError.showError(context, message);
                    }
                    else if(response_msg.equals("success"))
                    {

                        Intent intent=new Intent(context, DashboardActivity.class);
                        intent.putExtra("type","MyOffers_Successfull");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        ((Activity)context).finish();


//                        DialogBox.feedbackDialog(DeliverNowActivity.this,checkout_ref);
                        //checkout_ref is order_ref

                        /*Intent intent=new Intent(context, DashboardActivity.class);
                        intent.putExtra("type","MyOffers_Successfull");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();*/

                    }
                    else if(response_msg.equals("order status changed"))
                    {
                        DialogBoxError.showError(context, message);
                    }
                    else if(response_msg.equals("order data not found"))
                    {
                        DialogBoxError.showError(context, message);
                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(DeliverNowActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(DeliverNowActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(DeliverNowActivity.this,message);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBarSubmit.setVisibility(View.GONE);
                    tv_confirm.setText("CONFIRM");
                    img_back.setEnabled(true);

                /*if (custombtnConfirm.isLoading())
                {
                    custombtnConfirm.hideLoading();
                }*/

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(DeliverNowActivity.this);
        }

    }

    @Override
    public void onClick(View view) {

        Typeface rubik_regular=null,rubik_medium = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            rubik_medium = getResources().getFont(R.font.rubik_medium);
            rubik_regular = getResources().getFont(R.font.rubik_regular);
        }
        else
        {
            rubik_medium = ResourcesCompat.getFont(context, R.font.rubik_medium);
            rubik_regular = ResourcesCompat.getFont(context, R.font.rubik_regular);
        }

        if(view.getId() == R.id.tv_delivery) {

            if (deliveryAddressDataList.size()>0)
            {
                tv_confirm.setVisibility(View.VISIBLE);
                tv_selectAddresTitle.setVisibility(View.VISIBLE);
                tv_selectAddresTitle.setText(getResources().getString(R.string.please_select_your_address_for_product_delivery));
            }
            else
            {
                tv_confirm.setVisibility(View.GONE);
                tv_selectAddresTitle.setVisibility(View.GONE);
            }


            //add_address.setVisibility(View.VISIBLE);

//            if(flag) {
//
//                add_address.setVisibility(View.VISIBLE);
//
//            }
//            else {
//
//                add_address.setVisibility(View.GONE);
//
//            }
            if (deliveryAddressAdapter!=null)
            {
                if(!flag) {
                    deliveryAddressAdapter.setAddressRef("");
                }
            }
            if (selfPickupAddressAdapter!=null)
            {
                selfPickupAddressAdapter.setAddressRef("");
            }

            address_ref = "";


            self_pickup="delivery";
            rv_delivery_address.setVisibility(View.VISIBLE);
            rv_self_pickup_address.setVisibility(View.GONE);
            if (!progressBarTop.isShown())
            {
                rl_shipping_fee.setVisibility(View.VISIBLE);
            }

            if(!progressBarDeliver.isShown())
            {
                add_address.setVisibility(View.VISIBLE);
            }




            tv_noOutlet.setVisibility(View.GONE);


            delivery_dot_image.setVisibility(View.VISIBLE);
            self_pickup_dot_image.setVisibility(View.INVISIBLE);

            tv_delivery.setTypeface(rubik_medium);
            tv_self_pickup.setTypeface(rubik_regular);
        }
        else if(view.getId() == R.id.tv_self_pickup)
        {

            if (selfPickupAddressDataList.size()>0)
            {
                if(!flag && deliveryAddressAdapter!=null)
                {
                    deliveryAddressAdapter.setAddressRef("");
                }
                tv_confirm.setVisibility(View.VISIBLE);
                tv_selectAddresTitle.setVisibility(View.VISIBLE);
                tv_selectAddresTitle.setText(getResources().getString(R.string.please_select_a_nearest_outlet_for_product_pickup));

                if (selfPickupAddressAdapter!=null)
                {
                    //            deliveryAddressAdapter.setAddressRef("");
                    selfPickupAddressAdapter.setAddressRef("");
                    address_ref = "";


                }
                tv_noOutlet.setVisibility(View.GONE);
            }
            else
            {
                //tv_noOutlet.setVisibility(View.VISIBLE);
                rv_self_pickup_address.setVisibility(View.GONE);
                tv_selectAddresTitle.setVisibility(View.GONE);
                tv_confirm.setVisibility(View.GONE);

                if(progressBarDeliver.getVisibility()==View.GONE)
                {
                    tv_noOutlet.setVisibility(View.VISIBLE);
                }

            }

            add_address.setVisibility(View.GONE);

            self_pickup="selfpickup";
            rv_delivery_address.setVisibility(View.GONE);
            rv_self_pickup_address.setVisibility(View.VISIBLE);
            rl_shipping_fee.setVisibility(View.GONE);

            delivery_dot_image.setVisibility(View.INVISIBLE);
            self_pickup_dot_image.setVisibility(View.VISIBLE);



            tv_delivery.setTypeface(rubik_regular);
            tv_self_pickup.setTypeface(rubik_medium);


        }
        else if(view.getId() == R.id.tv_confirm)
        {
            if (DialogBoxError.checkInternetConnection(DeliverNowActivity.this))
            {
                if(address_ref.equals(""))
                {
                    DialogBoxError.showError(context, "Please select address");
                }
                else
                {
                    img_back.setEnabled(false);
                    progressBarSubmit.setVisibility(View.VISIBLE);
                    tv_confirm.setText("");
                    orderPlace();
                }
            }
            else
            {
                DialogBoxError.showInternetDialog(DeliverNowActivity.this);
            }
        }
        else if (view.getId() == R.id.custombtnConfirm)
        {
            if(address_ref.equals(""))
            {
                DialogBoxError.showError(context, "Please select address");
            }
            else
            {
                if (!custombtnConfirm.isLoading())
                {
                    custombtnConfirm.showLoading();
                    img_back.setEnabled(false);
                    orderPlace();
                }
            }
        }
    }

    private Task<HashMap> getWalletPointOfUser() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getWalletPointOfUser").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> getAddress() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getAddress").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> getOutletAddressAndProductDetails() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("checkout_ref", checkout_ref);

        return mFunctions.getHttpsCallable("getOutletAddressAndProductDetails").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> placeOrder() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("checkout_ref", checkout_ref);
        data.put("address_ref", address_ref);
        data.put("is_outlet", Boolean.parseBoolean(is_outlet));

        return mFunctions.getHttpsCallable("placeOrder").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    public void references(String address_ref, String is_outlet)
    {
        this.address_ref = address_ref;
        this.is_outlet = is_outlet;

        tv_confirm.setBackgroundResource(R.color.green);

//        tv_confirm.setFocusable(true);
//        tv_confirm.setEnabled(true);
//        tv_confirm.setClickable(true);

        Log.e("address_ref2", address_ref + " " + is_outlet);

        //self_pickup="selfpickup";
        if (self_pickup.equalsIgnoreCase("selfpickup"))
        {

            if(!flag) {
                deliveryAddressAdapter.setAddressRef("");
            }
            selfPickupAddressAdapter.setAddressRef(address_ref);

        }
        else
        {
            deliveryAddressAdapter.setAddressRef(address_ref);
            selfPickupAddressAdapter.setAddressRef("");
        }

    }

    private void readNotification(String notification_ref)
    {
        if(!(notification_ref.equalsIgnoreCase(""))) {
            markNotificationAsRead(notification_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(DeliverNowActivity.this, e.getMessage());
                }
            });
        }
    }

    private Task<HashMap> markNotificationAsRead(String notification_ref)
    {
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

}