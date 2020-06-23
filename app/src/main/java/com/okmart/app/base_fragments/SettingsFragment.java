package com.okmart.app.base_fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.okmart.app.R;
import com.okmart.app.activities.AddressActivity;
import com.okmart.app.activities.ChangeNumberActivity;
import com.okmart.app.activities.ContactActivity;
import com.okmart.app.activities.PoliciesActivity;
import com.okmart.app.activities.ProfileDetailsActivity;
import com.okmart.app.activities.ReferEarnActivity;
import com.okmart.app.model.PoliciesModel;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBox;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SharedPreferenceConstant;
import com.okmart.app.utilities.SharedPreferenceUtil;
import com.okmart.app.utilities.Vibration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{

    private Context context = getActivity();
    private String TAG = SettingsFragment.class.getSimpleName();
    private FirebaseFunctions mFunctions;
    private TextView user_name, wallet_balance, logout;
    private RelativeLayout tv_profileDetails, tv_changeNumber, tv_addressBook, tv_refer, tv_contactUs, tv_policies, tv_rateUs;
    private ImageView profile_image;
    private Switch switches;
    private SharedPreferenceUtil preferenceUtil;
    private String txt_profile_image, txt_phone_no, is_notification, txt_referred_code, txt_referral_url, txt_amount_for_new_user, txt_amt_for_refrence_user, txt_min_txn_amount;
    private ProgressBar progressBar,progressBarWalletBal;
    private String token_new;
    private ArrayList<PoliciesModel> al=new ArrayList<>();

    private SharedPreferenceUtil sharedPreferenceUtil;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        preferenceUtil = new SharedPreferenceUtil(getActivity());
        mFunctions = FirebaseFunctions.getInstance();

        sharedPreferenceUtil = new SharedPreferenceUtil(getActivity());

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.e(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            token_new = task.getResult().getToken();
                            Log.e(TAG, "onCompletetoken: " + token_new);
                        }
                    });

            //getCmsPagesTitle

            getCmsPagesTitle().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        ArrayList arrayList = (ArrayList) hashMap.get("data");

                        for (int i = 0; i < arrayList.size(); i++) {

                            HashMap data = (HashMap) arrayList.get(i);

                            PoliciesModel policiesModel = new PoliciesModel();
                            policiesModel.setId(data.get("id").toString());
                            policiesModel.setPage_url(data.get("page_url").toString());
                            policiesModel.setTitle(CapitalUtils.capitalize(data.get("title").toString()));

                            al.add(policiesModel);
                        }
                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(getActivity(),message);
                    }
                    else
                    {
                        DialogBoxError.showError(getActivity(),message);
                    }



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
        }


        progressBarWalletBal = view.findViewById(R.id.progressBarWalletBal);

        progressBar = view.findViewById(R.id.progressBarMedium);

        user_name = view.findViewById(R.id.user_name);
        wallet_balance = view.findViewById(R.id.wallet_balance);
        profile_image = view.findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);

        tv_profileDetails = view.findViewById(R.id.tv_profileDetails);
        tv_profileDetails.setOnClickListener(this);

        tv_changeNumber = view.findViewById(R.id.tv_changeNumber);
        tv_changeNumber.setOnClickListener(this);

        tv_addressBook = view.findViewById(R.id.tv_addressBook);
        tv_addressBook.setOnClickListener(this);

        tv_refer = view.findViewById(R.id.tv_refer);
        tv_refer.setOnClickListener(this);

        tv_contactUs = view.findViewById(R.id.tv_contactUs);
        tv_contactUs.setOnClickListener(this);

        tv_policies = view.findViewById(R.id.tv_policies);
        tv_policies.setOnClickListener(this);

        tv_rateUs = view.findViewById(R.id.tv_rateUs);
        tv_rateUs.setOnClickListener(this);

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(this);

        switches = view.findViewById(R.id.switches);

        if(preferenceUtil.getNotification(SharedPreferenceConstant.notification, "").equalsIgnoreCase("true"))
        {
            switches.setChecked(true);
        }
        else
        {
            switches.setChecked(false);
        }

        switches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {

                    if (DialogBoxError.checkInternetConnection(getActivity()))
                    {
                        updateNotificationStatus(true).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap) {

                                Log.e(TAG, "onSuccess: " + hashMap );

                                String response_msg = hashMap.get("response_msg").toString();
                                String message = hashMap.get("message").toString();

                                if (response_msg.equals("success"))
                                {
                                    preferenceUtil.setNotification(SharedPreferenceConstant.notification, "true");
                                }
                                else if (response_msg.equals(Constants.unauthorized))
                                {
                                    DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                                }
                                else if (response_msg.equals(Constants.under_maintenance))
                                {
                                    DialogBoxError.showError(getActivity(),message);
                                }
                                else
                                {
                                    DialogBoxError.showError(getActivity(),message);
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e);
                            }
                        });

                        //preferenceUtil.setNotification(SharedPreferenceConstant.notification, "true");

                    }
                    else
                    {
                        DialogBoxError.showInternetDialog(getActivity());
                    }


                } else {

                    if (DialogBoxError.checkInternetConnection(getActivity()))
                    {
                        updateNotificationStatus(false).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap) {

                                Log.e(TAG, "onSuccess: " + hashMap);

                                String response_msg = hashMap.get("response_msg").toString();
                                String message = hashMap.get("message").toString();

                                if (response_msg.equals("success"))
                                {
                                    preferenceUtil.setNotification(SharedPreferenceConstant.notification, "false");
                                }
                                else if (response_msg.equals(Constants.unauthorized))
                                {
                                    DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                                }
                                else if (response_msg.equals(Constants.under_maintenance))
                                {
                                    DialogBoxError.showError(getActivity(),message);
                                }
                                else
                                {
                                    DialogBoxError.showError(getActivity(),message);
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e);
                            }
                        });

                        //preferenceUtil.setNotification(SharedPreferenceConstant.notification, "false");

                    }
                    else
                    {
                        DialogBoxError.showInternetDialog(getActivity());
                    }

                }

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess:userDetails " + hashMap);

                    if(progressBar.isShown())
                    {
                        progressBar.setVisibility(View.GONE);
                    }

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        HashMap userData = (HashMap) hashMap.get("userData");

                        is_notification = userData.get("is_notification").toString();
                        user_name.setText(CapitalUtils.capitalize(userData.get("user_name").toString()));
                        wallet_balance.setText(DoubleDecimal.twoPointsComma(userData.get("wallet_balance").toString()));
                        wallet_balance.setVisibility(View.VISIBLE);
                        progressBarWalletBal.setVisibility(View.GONE);
                        txt_referred_code = userData.get("referral_code").toString();
                        txt_referral_url = userData.get("referral_url").toString();

                        txt_profile_image = (String) userData.get("profile_image");
                        txt_phone_no = (String) userData.get("phone_no");

                        txt_amount_for_new_user = (String) userData.get("amount_for_new_user").toString();
                        txt_amt_for_refrence_user = (String) userData.get("amt_for_refrence_user").toString();
                        txt_min_txn_amount = (String) userData.get("min_txn_amount").toString();

                        if(is_notification.equalsIgnoreCase("true"))
                        {
                            switches.setChecked(true);
                        }
                        else
                        {
                            switches.setChecked(false);
                        }

                        if (!(txt_profile_image.length() == 0)) {

                            try {

                                //Handle whatever you're going to do with the URL here
                                Glide.with(SettingsFragment.this).load(txt_profile_image).placeholder(R.color.white).into(profile_image);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        else
                        {
                            profile_image.setImageResource(R.drawable.avatar);
                        }
                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(getActivity(),message);
                    }
                    else
                    {
                        DialogBoxError.showError(getActivity(),message);
                    }


                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if(progressBar.isShown())
                    {
                        progressBar.setVisibility(View.GONE);
                    }

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(getActivity(), e.getMessage());
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.tv_profileDetails :

            case R.id.profile_image :
                Intent intent = new Intent(getContext(), ProfileDetailsActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_changeNumber :
                Intent intent4 = new Intent(getContext(), ChangeNumberActivity.class);
                startActivity(intent4);
                break;

            case R.id.tv_addressBook :

                Intent intent0 = new Intent(getContext(), AddressActivity.class);
                startActivity(intent0);
                break;

            case R.id.tv_refer :

                Intent intent5 = new Intent(getContext(), ReferEarnActivity.class);
                intent5.putExtra("referred_code", txt_referred_code);
                intent5.putExtra("referral_url", txt_referral_url);
                intent5.putExtra("amount_for_new_user", txt_amount_for_new_user);
                intent5.putExtra("amt_for_refrence_user", txt_amt_for_refrence_user);
                intent5.putExtra("min_txn_amount", txt_min_txn_amount);

                startActivity(intent5);
                break;

            case R.id.tv_contactUs :

                Intent intent1 = new Intent(getContext(), ContactActivity.class);
                startActivity(intent1);
                break;

            case R.id.tv_policies :

                Intent intent2 = new Intent(getContext(), PoliciesActivity.class);
                intent2.putExtra("arrayList", (Serializable) al);
                startActivity(intent2);
                break;

            case R.id.tv_rateUs :

                if (DialogBoxError.checkInternetConnection(getActivity()))
                {
                    try{
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
                    }
                    catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    }
                }
                else
                {
                    DialogBoxError.showInternetDialog(getActivity());
                }

                break;

            case R.id.logout :

                Vibration.vibration(getActivity());

                DialogBox.showLogoutDialog(getActivity(), token_new,sharedPreferenceUtil);

                break;
        }
    }

    private Task<HashMap> userDetails() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("userDetails").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> updateNotificationStatus(boolean flag) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("is_notification", flag);

        return mFunctions.getHttpsCallable("updateNotificationStatus").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }


    private Task<HashMap> getCmsPagesTitle() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getCmsPagesTitle").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

}