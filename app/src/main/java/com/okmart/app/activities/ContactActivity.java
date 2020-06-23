package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.base_fragments.OffersFragment;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBox;
import com.okmart.app.utilities.DialogBoxContactMessage;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private FirebaseFunctions mFunctions;
    private String TAG = ContactActivity.class.getSimpleName();
    private Context context = ContactActivity.this;
    private TextView company_name, address, phone_numbers, emails, send;
    private ImageView img_back;
    private EditText name, email, message;
    private String txt_name, txt_email, txt_message;
    private ProgressBar progressBarSend, progressBarContact, progressBarForm;
    private LinearLayout details, form;
    String txt_phone_numbers = null;
    String txt_emails = null;
    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        getSupportActionBar().hide();

        sharedPreferenceUtil = new SharedPreferenceUtil(ContactActivity.this);
        mFunctions = FirebaseFunctions.getInstance();

        progressBarSend = findViewById(R.id.progressBarSend);
        progressBarContact = findViewById(R.id.progressBarContact);
        progressBarForm = findViewById(R.id.progressBarForm);
        company_name = findViewById(R.id.company_name);
        address = findViewById(R.id.address);
        phone_numbers = findViewById(R.id.phone_numbers);
        emails = findViewById(R.id.emails);
        img_back = findViewById(R.id.img_back);
        details = findViewById(R.id.details);
        form = findViewById(R.id.form);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (DialogBoxError.checkInternetConnection(ContactActivity.this))
        {
            userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess:userDetails " + hashMap);

                    if(progressBarForm.isShown())
                    {
                        progressBarForm.setVisibility(View.GONE);
                    }

                    form.setVisibility(View.VISIBLE);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        HashMap userData = (HashMap) hashMap.get("userData");

                        String i_name = userData.get("user_name").toString();

                        name.setText(CapitalUtils.capitalize(i_name));
                        name.setSelection(name.length());
                        email.setText(userData.get("email").toString());

                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(ContactActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(ContactActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(ContactActivity.this,message);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if(progressBarForm.isShown())
                    {
                        progressBarForm.setVisibility(View.GONE);
                    }

                    form.setVisibility(View.VISIBLE);

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(ContactActivity.this);
        }



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_name = name.getText().toString();
                txt_email = email.getText().toString();
                txt_message = message.getText().toString();

                if (!validate()) {
                    return;
                }

                if (DialogBoxError.checkInternetConnection(ContactActivity.this))
                {
                    progressBarSend.setVisibility(View.VISIBLE);
                    send.setText("");
                    send.setClickable(false);

                    contactUs().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                        @Override
                        public void onSuccess(HashMap hashMap) {
                            Log.e(TAG, "onSuccess: " +hashMap);

                            if(progressBarSend.isShown()) {
                                progressBarSend.setVisibility(View.GONE);
                            }

                            String response_msg = hashMap.get("response_msg").toString();
                            String msg = hashMap.get("message").toString();

                            if (response_msg.equals("success"))
                            {
                                send.setText("SEND");
                                send.setClickable(true);

                                name.setSelection(0);

                                DialogBoxContactMessage.showContactMessage(context, name, email, message);
                            }
                            else if (response_msg.equals(Constants.unauthorized))
                            {
                                DialogBoxError.showDialogBlockUser(ContactActivity.this,msg,sharedPreferenceUtil);
                            }
                            else if (response_msg.equals(Constants.under_maintenance))
                            {
                                DialogBoxError.showError(ContactActivity.this,msg);
                            }
                            else
                            {
                                DialogBoxError.showError(ContactActivity.this,msg);
                            }



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if(progressBarSend.isShown()) {
                                progressBarSend.setVisibility(View.GONE);
                            }
                            send.setText("SEND");
                            send.setClickable(true);

                            Log.e(TAG, "onFailure: "+e );
                        }
                    });
                }
                else
                {
                    DialogBoxError.showInternetDialog(ContactActivity.this);
                }

            }
        });

        if (DialogBoxError.checkInternetConnection(ContactActivity.this))
        {
            getContactDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " +hashMap);

                    details.setVisibility(View.VISIBLE);


                    if(progressBarContact.isShown()) {
                        progressBarContact.setVisibility(View.GONE);
                    }

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        HashMap hm = (HashMap) hashMap.get("data");
                        String txt_company_name = (String) hm.get("company_name");
                        company_name.setText(txt_company_name);
                        company_name.setTextColor(getResources().getColor(R.color.black));

                        String txt_address = (String) hm.get("address");
                        address.setText(txt_address);

                        ArrayList listEmailsData = (ArrayList) hm.get("emails");
                        StringBuilder sb_email = new StringBuilder();
                        for (int i = 0; i < listEmailsData.size(); i++) {

                            sb_email.append(listEmailsData.get(i).toString());
                            sb_email.append(", ");
                    /*if(i+1 == listEmailsData.size())
                    {
                        txt_emails = listEmailsData.get(i).toString();
                    }
                    else
                    {
                        txt_emails = listEmailsData.get(i).toString();
                    }*/

                        }
                        if (sb_email!=null)
                        {
                            sb_email.deleteCharAt(sb_email.length()-2);
                        }

                        emails.setText(sb_email.toString());

                        ArrayList listPhoneata = (ArrayList) hm.get("phone_numbers");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < listPhoneata.size(); i++) {

                            sb.append(listPhoneata.get(i).toString());
                            sb.append(", ");
                    /*if(i+1 == listPhoneata.size())
                    {
                        txt_phone_numbers = listPhoneata.get(i).toString();
                    }
                    else
                    {
                        txt_phone_numbers = listPhoneata.get(i).toString();
                    }*/

                        }
                        if (sb!=null)
                        {
                            sb.deleteCharAt(sb.length()-2);
                        }
                        phone_numbers.setText(sb.toString());
                    }
                    /*else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(ContactActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(ContactActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(ContactActivity.this,message);
                    }*/


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if(progressBarContact.isShown()) {
                        progressBarContact.setVisibility(View.GONE);
                    }

                    Log.e(TAG, "onFailure: "+e );
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(ContactActivity.this);
        }




        phone_numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", txt_phone_numbers, null));
                startActivity(intent);
            }
        });


        emails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ txt_emails});

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));

            }
        });

    }

    private boolean validate() {

        if (TextUtils.isEmpty(txt_name)) {
            DialogBoxError.showError(context, "Please enter name");
            return false;
        }
        else if (TextUtils.isEmpty(txt_email)) {
            DialogBoxError.showError(context, "Please enter email");
            return false;
        }
        else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(txt_email).matches())) {
            DialogBoxError.showError(context, "Please enter correct email");
            return false;
        }
        else if (TextUtils.isEmpty(txt_message)) {
            DialogBoxError.showError(context, "Please enter message");
            return false;
        }

        return true;
    }

    private Task<HashMap> contactUs() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("name", txt_name);
        data.put("email", txt_email);
        data.put("message", txt_message);

        return mFunctions.getHttpsCallable("contactUs").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> getContactDetails() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getContactDetails").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
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
}
