package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.model.StateListModel;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBox;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DialogBoxState;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAddressActivity extends AppCompatActivity {

    private String TAG = NewAddressActivity.class.getSimpleName();
    private Context context = NewAddressActivity.this;
    private FirebaseFunctions mFunctions;
    private EditText name, address_line1, address_line2, city, pincode, country, phone_no;
    private String txt_name, txt_address_line1, txt_address_line2, txt_city, txt_state, txt_state_ref, txt_pincode, txt_country, txt_phone_no, txt_address_ref, primary_phone, txt_is_default;
    private TextView tv_save,tv_delete, default_address,tv_fillDetails, state;
    private ImageView img_back;
    private DialogBoxState dialogBoxState;
    private List<StateListModel> stateLists = new ArrayList<>();
    private String type = "new";
    private String choice = "new";
    private ProgressBar progressBarSave,progressBarDlt;
    private RelativeLayout rl_dlt;
    private Switch mark_as_default;
    private RelativeLayout rl_defaultAddress;
    private String update_defaultAddress="";
    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        getSupportActionBar().hide();

        sharedPreferenceUtil = new SharedPreferenceUtil(NewAddressActivity.this);

        mFunctions = FirebaseFunctions.getInstance();
        dialogBoxState = new DialogBoxState();

        progressBarSave = findViewById(R.id.progressBarSave);

        name = findViewById(R.id.name);
        address_line1 = findViewById(R.id.address_line1);
        address_line2 = findViewById(R.id.address_line2);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        pincode = findViewById(R.id.pincode);
        country = findViewById(R.id.country);
        phone_no = findViewById(R.id.phone_no);
        tv_save = findViewById(R.id.tv_save);
        rl_dlt = findViewById(R.id.rl_dlt);
        tv_delete = findViewById(R.id.tv_delete);
        mark_as_default = findViewById(R.id.mark_as_default);
        //default_address = findViewById(R.id.default_address);
        progressBarDlt = findViewById(R.id.progressBarDlt);
        rl_defaultAddress = findViewById(R.id.rl_defaultAddress);
        tv_fillDetails = findViewById(R.id.tv_fillDetails);

        if (DialogBoxError.checkInternetConnection(NewAddressActivity.this))
        {
            userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess:userDetails " + hashMap);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        HashMap userData = (HashMap) hashMap.get("userData");

                        primary_phone = (userData.get("phone_no").toString()).substring(3);
                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(NewAddressActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(NewAddressActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(NewAddressActivity.this,message);
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
            DialogBoxError.showInternetDialog(NewAddressActivity.this);
        }



        state = findViewById(R.id.state);
        /*state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    dialogBoxState.state(context, state, stateLists, type);
                }
            }
        });*/
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxState.state(context, state, stateLists, type);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (!(extras == null)) {
            choice = "edit";

            tv_fillDetails.setText("Edit your address");

            name.setText(extras.getString("name"));
            name.setSelection(name.getText().length());
            address_line1.setText(extras.getString("address_line_1"));
            address_line2.setText(extras.getString("address_line_2"));
            city.setText(extras.getString("city"));
            state.setText(extras.getString("state"));
            pincode.setText(extras.getString("pincode"));
            phone_no.setText((extras.getString("phone_no").substring(3)));
            txt_address_ref = extras.getString("address_ref");
            txt_is_default = extras.getString("is_default");

            if(txt_is_default.equalsIgnoreCase("true"))
            {
                //default_address.setVisibility(View.VISIBLE);
                rl_defaultAddress.setVisibility(View.GONE);
                mark_as_default.setChecked(true);
            }
            else {

                //default_address.setVisibility(View.GONE);
                rl_defaultAddress.setVisibility(View.VISIBLE);
                mark_as_default.setChecked(false);
            }

        }


        city.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i==EditorInfo.IME_ACTION_NEXT)
                {
                    DialogBoxError.hideKeyboard(NewAddressActivity.this);
                    dialogBoxState.state(context, state, stateLists, type);
                }
                return false;
            }
        });


        if (choice.equalsIgnoreCase("new"))
        {
            rl_dlt.setVisibility(View.GONE);
            rl_defaultAddress.setVisibility(View.GONE);
            DialogBoxError.showKeyboard(NewAddressActivity.this,name);
            name.requestFocus();
        }
        else
        {
            rl_dlt.setVisibility(View.VISIBLE);
            DialogBoxError.hideKeyboard(NewAddressActivity.this);
        }

        if (DialogBoxError.checkInternetConnection(NewAddressActivity.this))
        {
            getStateList().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        ArrayList arrayList = (ArrayList) hashMap.get("stateList");

                        stateLists.clear();

                        for(int i = 0 ; i < arrayList.size() ; i++)
                        {
                            StateListModel stateListModel = new StateListModel();
                            HashMap stateList = (HashMap) arrayList.get(i);
                            stateListModel.setName(stateList.get("name").toString());
                            stateListModel.setState_ref(stateList.get("state_ref").toString());
                            stateListModel.setCode(stateList.get("code").toString());
                            stateListModel.setState_ref(stateList.get("state_ref").toString());

                            stateLists.add(stateListModel);

                            Log.e(TAG, "onSuccess:stateList " + stateList.get("name").toString());
                        }
                    }
                    /*else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(NewAddressActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(NewAddressActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(NewAddressActivity.this,message);
                    }*/


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: "+e );
                    DialogBoxError.showError(context, context.getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(NewAddressActivity.this);
        }


        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*DialogBoxError.hideKeyboard(NewAddressActivity.this);
                finish();*/

                if (progressBarSave.getVisibility()==View.GONE || progressBarDlt.getVisibility()==View.GONE)
                {
                    DialogBoxError.hideKeyboard(NewAddressActivity.this);
                    finish();
                }

            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                txt_name = name.getText().toString();
                txt_address_line1 = address_line1.getText().toString();
                txt_address_line2 = address_line2.getText().toString();
                txt_city = city.getText().toString();
                txt_state = state.getText().toString();
                txt_pincode = pincode.getText().toString();
                txt_country = country.getText().toString();
                txt_phone_no = phone_no.getText().toString();

                if (!validate())
                {
                    return;
                }
                tv_delete.setEnabled(false);
                img_back.setEnabled(false);

                progressBarSave.setVisibility(View.VISIBLE);
                tv_save.setText("");
                tv_save.setClickable(false);

                if(choice.equalsIgnoreCase("new"))
                {
                    if (DialogBoxError.checkInternetConnection(NewAddressActivity.this))
                    {
                        addAddress().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap) {
                                Log.e(TAG, "onSuccess123: " + hashMap);

                                String response_msg = hashMap.get("response_msg").toString();
                                String message = hashMap.get("message").toString();

                                if (response_msg.equals("success"))
                                {
                                    tv_delete.setEnabled(true);
                                    finish();
                                }
                                else if (response_msg.equals(Constants.unauthorized))
                                {
                                    DialogBoxError.showDialogBlockUser(NewAddressActivity.this,message,sharedPreferenceUtil);
                                }
                                else if (response_msg.equals(Constants.under_maintenance))
                                {
                                    DialogBoxError.showError(NewAddressActivity.this,message);
                                }
                                else
                                {
                                    DialogBoxError.showError(NewAddressActivity.this,message);
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                tv_delete.setEnabled(true);
                                img_back.setEnabled(true);
                                progressBarSave.setVisibility(View.GONE);
                                tv_save.setText("Save");
                                tv_save.setClickable(true);

                                Log.e(TAG, "onFailure123: " + e);
                                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                            }
                        });
                    }
                    else
                    {
                        DialogBoxError.showInternetDialog(NewAddressActivity.this);
                    }

                }
                else if(choice.equalsIgnoreCase("edit"))
                {
                    if (txt_is_default.equalsIgnoreCase("false"))
                    {
                        if (mark_as_default.isChecked())
                        {
                            if (DialogBoxError.checkInternetConnection(NewAddressActivity.this))
                            {
                                setDefaultAddress().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                                    @Override
                                    public void onSuccess(HashMap hashMap)
                                    {
                                        Log.e(TAG, "onSuccess: " + hashMap);

                                        String response_msg = hashMap.get("response_msg").toString();
                                        String message = hashMap.get("message").toString();

                                        if (response_msg.equals("success"))
                                        {
                                        }
                                        else if (response_msg.equals(Constants.unauthorized))
                                        {
                                            update_defaultAddress="failed";
                                            DialogBoxError.showDialogBlockUser(NewAddressActivity.this,message,sharedPreferenceUtil);
                                        }
                                        else if (response_msg.equals(Constants.under_maintenance))
                                        {
                                            update_defaultAddress="failed";
                                            DialogBoxError.showError(NewAddressActivity.this,message);
                                        }
                                        else
                                        {
                                            update_defaultAddress="failed";
                                            DialogBoxError.showError(NewAddressActivity.this,message);
                                        }

                                        /*String response_msg = hashMap.get("response_msg").toString();
                                        if (response_msg.equalsIgnoreCase("success"))
                                        {
                                            //updateAddressDetail();
                                        }
                                        else
                                        {
                                            update_defaultAddress="failed";
                                            DialogBoxError.showError(context, hashMap.get("message").toString());
                                        }*/
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: "+e );
                                        DialogBoxError.showError(context, e.getMessage());
                                    }
                                });
                            }
                            else
                            {
                                DialogBoxError.showInternetDialog(NewAddressActivity.this);
                            }

                        }
                    }
                    if (!update_defaultAddress.equalsIgnoreCase("failed"))
                    {
                        if (DialogBoxError.checkInternetConnection(NewAddressActivity.this))
                        {
                            updateAddressDetail();
                        }
                        else
                        {
                            DialogBoxError.showInternetDialog(NewAddressActivity.this);
                        }
                    }
                }
            }
        });

        tv_delete.setOnClickListener(view -> {

            DialogBox.showDeleteDialog(NewAddressActivity.this,progressBarDlt,tv_delete,tv_save,img_back);
            //dltAddress();
        });

    }

    private boolean validate() {

        String first_char = "";

        if(txt_phone_no.length() > 0) {

            first_char = txt_phone_no.substring(0,1);
        }

        if (TextUtils.isEmpty(txt_name)) {
            DialogBoxError.showError(context, "Please enter name");
            return false;
        }
        else if (TextUtils.isEmpty(txt_address_line1)) {
            DialogBoxError.showError(context, "Please enter address");
            return false;
        }
//        else if (TextUtils.isEmpty(txt_address_line2)) {
//            DialogBoxError.showError(context, "Please Enter Address");
//            return false;
//        }
        else if (TextUtils.isEmpty(txt_city)) {
            DialogBoxError.showError(context, "Please enter city");
            return false;
        }
        else if (TextUtils.isEmpty(txt_state)) {
            DialogBoxError.showError(context, "Please enter state");
            return false;
        }
        else if (TextUtils.isEmpty(txt_pincode)) {
            DialogBoxError.showError(context, "Please enter postcode");
            return false;
        }

//        else if (TextUtils.isEmpty(txt_country)) {
//            DialogBoxError.showError(context, "Please enter mobile number");
//            return false;
//        }
        if(txt_phone_no.length()>0)
        {
            if (txt_phone_no.length() < 9) {
                DialogBoxError.showError(context, "Please enter correct mobile number");

                return false;
            }
        }
        else
        {
            txt_phone_no = primary_phone;
        }
        if (first_char.equalsIgnoreCase("0")) {
            DialogBoxError.showError(context, "Please enter phone number in this format 11xxxxxxxx without 0");

            return false;
        }

        return true;
    }

    private Task<HashMap> addAddress() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("name", txt_name);
        data.put("address_line1", txt_address_line1);
        data.put("address_line2", txt_address_line2);
        data.put("city", txt_city);
        data.put("state", txt_state);
        data.put("state_ref", txt_state_ref);
        data.put("pincode", txt_pincode);
        data.put("country", txt_country);
        data.put("phone_no", Constants.MOB_CODE + txt_phone_no);

        return mFunctions.getHttpsCallable("addAddress").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> setDefaultAddress() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("address_ref", txt_address_ref);

        return mFunctions.getHttpsCallable("setDefaultAddress").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> updateAddress() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("address_ref", txt_address_ref);
        data.put("name", txt_name);
        data.put("address_line1", txt_address_line1);
        data.put("address_line2", txt_address_line2);
        data.put("city", txt_city);
        data.put("state", txt_state);
        data.put("state_ref", txt_state_ref);
        data.put("pincode", txt_pincode);
        data.put("country", txt_country);
        data.put("phone_no", Constants.MOB_CODE + txt_phone_no);

        return mFunctions.getHttpsCallable("updateAddress").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> getStateList() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getStateList").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
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

    public void stateRef(String state_name,String state_ref)
    {
        txt_state_ref = state_ref;
        if (choice.equalsIgnoreCase("new"))
        {
            DialogBoxError.showKeyboard(NewAddressActivity.this,pincode);
        }

    }

    public void dltAddress()
    {
        if (DialogBoxError.checkInternetConnection(NewAddressActivity.this))
        {
            deleteFun().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess:userDetails " + hashMap);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        String success=hashMap.get("success").toString();
                        if (success.equalsIgnoreCase("true"))
                        {
                            tv_save.setEnabled(true);
                            img_back.setEnabled(true);

                            Toast.makeText(context, hashMap.get("message").toString(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            if (hashMap.get("response_msg").toString().equalsIgnoreCase("address could not be deleted"))
                            {
                                DialogBoxError.showError(context, "Primary address could not be deleted");
                            }
                            else
                            {
                                DialogBoxError.showError(context, hashMap.get("message").toString());
                            }
                            tv_delete.setText("Delete Address");
                            progressBarDlt.setVisibility(View.GONE);

                            tv_save.setEnabled(true);
                            img_back.setEnabled(true);
                        }
                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(NewAddressActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        tv_delete.setText("Delete Address");
                        progressBarDlt.setVisibility(View.GONE);

                        tv_save.setEnabled(true);
                        img_back.setEnabled(true);

                        DialogBoxError.showError(NewAddressActivity.this,message);
                    }
                    else
                    {
                        tv_delete.setText("Delete Address");
                        progressBarDlt.setVisibility(View.GONE);

                        tv_save.setEnabled(true);
                        img_back.setEnabled(true);
                        DialogBoxError.showError(NewAddressActivity.this,message);
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    tv_save.setEnabled(true);
                    img_back.setEnabled(true);

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, e.getMessage());
                    tv_delete.setText("delete");
                    progressBarDlt.setVisibility(View.GONE);
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(NewAddressActivity.this);
        }



    }

    private Task<HashMap> deleteFun() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("address_ref", txt_address_ref);

        return mFunctions.getHttpsCallable("deleteAddress").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private void updateAddressDetail()
    {
        updateAddress().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap)
            {
                Log.e(TAG, "onSuccess123: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    Toast.makeText(context, hashMap.get("message").toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(NewAddressActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(NewAddressActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(NewAddressActivity.this, message);
                }

                /*String response_msg = hashMap.get("response_msg").toString();
                if (response_msg.equalsIgnoreCase("success"))
                {
                    Toast.makeText(context, hashMap.get("message").toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    DialogBoxError.showError(context, hashMap.get("message").toString());
                }*/
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBarSave.setVisibility(View.GONE);
                tv_save.setText("Save");
                tv_save.setClickable(true);

                Log.e(TAG, "onFailure123: " + e);
                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
            }
        });
    }
}