package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droidbond.loadingbutton.LoadingButton;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.model.PoliciesModel;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = LoginActivity.class.getSimpleName();
    private Context context = LoginActivity.this;
    private FirebaseFunctions mFunctions;
    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private EditText phonenumber;
    private String input_phonenumber, txt_phonenumber;
    private TextView signup, login, termsandconditionstatement;
    private CheckBox checkbox;

    private LoadingButton custombtn;
    private ProgressBar progressBarSubmit;

    private String id, page_url, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        phonenumber = findViewById(R.id.phonenumber);
        mFunctions = FirebaseFunctions.getInstance();

        if (DialogBoxError.checkInternetConnection(LoginActivity.this))
        {
            getCmsPagesTitle().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);

                    ArrayList arrayList = (ArrayList) hashMap.get("data");

                    for (int i = 0; i < arrayList.size(); i++) {

                        HashMap data = (HashMap) arrayList.get(i);

                        id = data.get("id").toString();
                        page_url = data.get("page_url").toString();
                        title = CapitalUtils.capitalize(data.get("title").toString());

                        if(id.equals("terms-and-conditions")){
                            break;
                        }

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
            DialogBoxError.showInternetDialog(LoginActivity.this);
        }



        checkbox = findViewById(R.id.checkbox);
        termsandconditionstatement = findViewById(R.id.termsandconditionstatement);
        termsandconditionstatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("page_url", page_url);
                intent.putExtra("title", title);
                ((Activity)context).startActivity(intent);
            }
        });

        signup = findViewById(R.id.signup);
        signup.setOnClickListener(this);

        login = findViewById(R.id.login);
        login.setOnClickListener(this);

        custombtn=findViewById(R.id.custombtn);
        progressBarSubmit = findViewById(R.id.progressBarSubmit);

        custombtn.setOnClickListener(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                // signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    DialogBoxError.showError(context, "Please enter the phone number in a correct format");
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {

                    DialogBoxError.showError(context, "Quota exceeded.");
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
                if (custombtn.isLoading())
                {
                    custombtn.hideLoading();
                }

                progressBarSubmit.setVisibility(View.GONE);
                login.setText("LOGIN");

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                Intent intent = new Intent(context, OTPLoginActivity.class);
                intent.putExtra("phonenumber", input_phonenumber);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("token", token);
                startActivity(intent);
                finish();

            }
        };
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signup)
        {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        }
        else if(view.getId() == R.id.login)
        {
            login.setText("");
            progressBarSubmit.setVisibility(View.VISIBLE);

            checkRegisteration();
        }
        else if (view.getId()==R.id.custombtn)
        {
            if (!custombtn.isLoading())
            {
                custombtn.showLoading();
                checkRegisteration();
            }
        }
    }

    private boolean validate() {

        if (TextUtils.isEmpty(input_phonenumber)) {
            DialogBoxError.showError(context, "Please enter mobile number");
            return false;
        }
        else if (input_phonenumber.length() < 9) {
            DialogBoxError.showError(context, "Please enter correct mobile number");
            return false;
        }
        else if(!(checkbox.isChecked()))
        {
            DialogBoxError.showError(context, "Please accept Terms & Conditions");
            return false;
        }

        return true;
    }

    private void checkRegisteration() {

        input_phonenumber = phonenumber.getText().toString();
        txt_phonenumber = Constants.MOB_CODE + phonenumber.getText().toString();

        if (!validate()) {

            if (custombtn.isLoading())
            {
                custombtn.hideLoading();
            }
            progressBarSubmit.setVisibility(View.GONE);
            login.setText("LOGIN");
            return;
        }

        if (DialogBoxError.checkInternetConnection(LoginActivity.this))
        {
            checkRegisterationStatusOfUser().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("user blocked")) {

                        DialogBoxError.showError(context, message);
                        if (custombtn.isLoading())
                        {
                            custombtn.hideLoading();
                        }
                        progressBarSubmit.setVisibility(View.GONE);
                        login.setText("LOGIN");

                    } else if (response_msg.equals("user not registered")) {

                        DialogBoxError.showError(context, message);
                        if (custombtn.isLoading())
                        {
                            custombtn.hideLoading();
                        }
                        progressBarSubmit.setVisibility(View.GONE);
                        login.setText("LOGIN");

                    } else if (response_msg.equals("registered user")) {

                        startPhoneNumberVerification(txt_phonenumber);

                    } else {

                        DialogBoxError.showError(context, message);
                        if (custombtn.isLoading())
                        {
                            custombtn.hideLoading();
                        }
                        progressBarSubmit.setVisibility(View.GONE);
                        login.setText("LOGIN");

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e);
                    if (custombtn.isLoading())
                    {
                        custombtn.hideLoading();
                    }
                    progressBarSubmit.setVisibility(View.GONE);
                    login.setText("LOGIN");
                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(LoginActivity.this);
        }


    }

    private Task<HashMap> checkRegisterationStatusOfUser() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("phonenumber", txt_phonenumber);

        return mFunctions.getHttpsCallable("checkRegisterationStatusOfUser").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
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