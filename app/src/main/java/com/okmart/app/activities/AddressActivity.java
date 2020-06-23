package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.adapters.AddressAdapter;
import com.okmart.app.model.AddressModel;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressActivity extends AppCompatActivity {

    private String TAG = AddressActivity.class.getSimpleName();
    private FirebaseFunctions mFunctions;
    private Context context = AddressActivity.this;
    private RelativeLayout rl_new_address;
    private ImageView img_back;
    private RecyclerView rv_address_list;
    private List<AddressModel> addressDataList=new ArrayList<>();
    //    private ProgressBar progressBarAddress;
    private SkeletonScreen skeletonScreen;
    private AddressAdapter addressAdapter;
    private TextView no_address;
    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        getSupportActionBar().hide();

        sharedPreferenceUtil = new SharedPreferenceUtil(AddressActivity.this);
        mFunctions = FirebaseFunctions.getInstance();

        rv_address_list = findViewById(R.id.rv_address_list);
        rv_address_list.setLayoutManager(new LinearLayoutManager(context));

        no_address = findViewById(R.id.no_address);

        skeletonScreen = Skeleton.bind(this.rv_address_list).adapter(addressAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();
//        progressBarAddress = findViewById(R.id.progressBarAddress);
//        progressBarAddress.setVisibility(View.VISIBLE);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rl_new_address = findViewById(R.id.rl_new_address);
        rl_new_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DialogBoxError.checkInternetConnection(AddressActivity.this))
        {
            getAddress().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);

//                if(progressBarAddress.isShown()) {
//                    progressBarAddress.setVisibility(View.GONE);
//                }

                    skeletonScreen.hide();

                    addressDataList.clear();

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        ArrayList al = (ArrayList) hashMap.get("data");

                        if(al.size() == 0) {

                            no_address.setText("There are no address.");
                        }
                        else
                        {

                            no_address.setText("");

                            for (int i = 0; i < al.size(); i++) {

                                HashMap data = (HashMap) al.get(i);

                                AddressModel addressModel = new AddressModel();
                                addressModel.setName(CapitalUtils.capitalize(data.get("name").toString()));
                                addressModel.setAddress_line_1(data.get("address_line1").toString());
                                addressModel.setAddress_line_2(data.get("address_line2").toString());
                                addressModel.setCity(CapitalUtils.capitalize(data.get("city").toString()));
                                addressModel.setState(CapitalUtils.capitalize(data.get("state").toString()));
                                addressModel.setPincode(data.get("pincode").toString());
                                addressModel.setPhone_no(data.get("phone_no").toString());
                                addressModel.setAddress_ref(data.get("address_ref").toString());
                                addressModel.setIs_default(data.get("is_default").toString());

                                addressDataList.add(addressModel);

                            }

                            Comparator<AddressModel> comparator = Comparator.comparing(e -> e.getIs_default());
                            addressDataList.sort(comparator.reversed());

                            addressAdapter = new AddressAdapter(context, addressDataList);
                            rv_address_list.setAdapter(addressAdapter);

                        }
                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(AddressActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(AddressActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(AddressActivity.this, message);
                    }



                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

//                if(progressBarAddress.isShown()) {
//                    progressBarAddress.setVisibility(View.GONE);
//                }

                    skeletonScreen.hide();

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(AddressActivity.this);
        }

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
}