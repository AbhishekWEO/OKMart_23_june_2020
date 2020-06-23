package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droidbond.loadingbutton.LoadingButton;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.okmart.app.R;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class OTPLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseFunctions mFunctions;
    private String TAG = OTPLoginActivity.class.getSimpleName();
    private Context context = OTPLoginActivity.this;
    private OtpTextView otp_view;
    private String txt_phonenumber, verificationId;
    private TextView phonenumber, change_number, resend;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private String token_new;
    private String OTP = "";
    //private Button verify;
    private TextView verify;
    private ProgressBar progressBarVerify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    //private LoadingButton custombtn;
    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);

        getSupportActionBar().hide();

        mFunctions = FirebaseFunctions.getInstance();

        sharedPreferenceUtil = new SharedPreferenceUtil(OTPLoginActivity.this);

        otp_view = findViewById(R.id.otp_view);
        verify = findViewById(R.id.verify);
        // verify.setVisibility(View.GONE);

        //custombtn = findViewById(R.id.custombtn);
        progressBarVerify = findViewById(R.id.progressBarVerify);

        phonenumber = findViewById(R.id.phonenumber);

        change_number = findViewById(R.id.change_number);
        change_number.setOnClickListener(this);

        resend = findViewById(R.id.resend);
        resend.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        DialogBoxError.showKeyboard(OTPLoginActivity.this, otp_view);


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

                signInWithPhoneAuthCredential(credential);
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
                /*if (custombtn.isLoading())
                {
                    custombtn.hideLoading();
                }*/

                progressBarVerify.setVisibility(View.GONE);
                verify.setText("VERIFY");

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);


//                Intent intent = new Intent(context, OTPSignupActivity.class);
//                intent.putExtra("name", txt_name);
//                intent.putExtra("address_line_1", txt_address_line_1);
//                intent.putExtra("address_line_2", txt_address_line_2);
//                intent.putExtra("city", txt_city);
//                intent.putExtra("state", txt_state);
//                intent.putExtra("state_ref", txt_state_ref);
//                intent.putExtra("postal_code", txt_postal_code);
//                intent.putExtra("country", txt_country);
//                intent.putExtra("phonenumber", txt_phonenumber);
//                intent.putExtra("email", txt_email);
//                intent.putExtra("verificationId", verificationId);
//                intent.putExtra("token", token);
//                startActivity(intent);
//                finish();



            }
        };


        Bundle extras = getIntent().getExtras();
        if (!(extras == null)) {

            txt_phonenumber = extras.getString("phonenumber");

            verificationId = extras.getString("verificationId");
            mResendToken = (PhoneAuthProvider.ForceResendingToken) extras.get("token");

            phonenumber.setText(Constants.MOB_CODE + txt_phonenumber);
        }

        otp_view.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.

                /*if (!custombtn.isLoading())
                {
                    DialogBoxError.hideKeyboard(OTPLoginActivity.this);

                    custombtn.showLoading();
                    verifyPhoneNumberWithCode(verificationId, otp);
                }*/

                DialogBoxError.hideKeyboard(OTPLoginActivity.this);

                OTP=otp;

                verify.setText("");
                progressBarVerify.setVisibility(View.VISIBLE);
                verifyPhoneNumberWithCode(verificationId, otp);
            }
        });


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(OTP.equals(""))
                {
                    DialogBoxError.showError(context, "Please fill 6 digit OTP");
                    return;
                }

                DialogBoxError.hideKeyboard(OTPLoginActivity.this);

                if (DialogBoxError.checkInternetConnection(OTPLoginActivity.this))
                {

                    verify.setText("");
                    progressBarVerify.setVisibility(View.VISIBLE);

                    verifyPhoneNumberWithCode(verificationId, OTP);
                }
                else
                {
                    DialogBoxError.showInternetDialog(OTPLoginActivity.this);
                }


            }
        });

        /*custombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otp.equals(""))
                {
                    DialogBoxError.showError(context, "Please fill 6 digit OTP");
                    return;
                }
                if (!custombtn.isLoading())
                {
                    DialogBoxError.hideKeyboard(OTPLoginActivity.this);

                    custombtn.showLoading();
                    verifyPhoneNumberWithCode(verificationId, otp);
                }


            }
        });*/
    }

    @Override
    public void onClick(View view) {

         if (view.getId() == R.id.change_number)
         {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if (view.getId() == R.id.resend)
        {

            if (DialogBoxError.checkInternetConnection(OTPLoginActivity.this))
            {

                Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT).show();

                resendVerificationCode(Constants.MOB_CODE + txt_phonenumber, mResendToken);
            }
            else
            {
                DialogBoxError.showInternetDialog(OTPLoginActivity.this);
            }



        }
    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        if (DialogBoxError.checkInternetConnection(OTPLoginActivity.this))
        {
            mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();

                        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.e(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                token_new = task.getResult().getToken();

                                login().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                                    @Override
                                    public void onSuccess(HashMap hashMap) {
                                        Log.e(TAG, "onSuccess: " + hashMap);

                                        String response_msg = hashMap.get("response_msg").toString();
                                        String message = hashMap.get("message").toString();



                                        if (response_msg.equals("success"))
                                        {
                                            //HashMap<String,Object> productDowmTime = (HashMap) hashMap.get("data");

                                            HashMap<String, Object>userData = (HashMap)hashMap.get("userData");
                                            Log.e(TAG, "userData: "+userData );

                                            sharedPreferenceUtil.setString("isLogin","true");
                                            sharedPreferenceUtil.save();

                                            Intent intent = new Intent(context, MobileVerifiedActivity.class);
                                            intent.putExtra("fromSignUp","false");
                                            startActivity(intent);
                                            finish();

                                        } else if (response_msg.equals("user blocked")) {

                                            DialogBoxError.showError(context, message);
                                        /*if (custombtn.isLoading())
                                        {
                                            custombtn.hideLoading();
                                        }*/
                                            progressBarVerify.setVisibility(View.GONE);
                                            verify.setText("VERIFY");

                                        } else if (response_msg.equals("'user not registered")) {

                                            DialogBoxError.showError(context, message);
                                        /*if (custombtn.isLoading())
                                        {
                                            custombtn.hideLoading();
                                        }*/

                                            progressBarVerify.setVisibility(View.GONE);
                                            verify.setText("VERIFY");

                                        }
                                        else
                                        {
                                            DialogBoxError.showError(context, message);
                                        /*if (custombtn.isLoading())
                                        {
                                            custombtn.hideLoading();
                                        }*/

                                            progressBarVerify.setVisibility(View.GONE);
                                            verify.setText("VERIFY");
                                        }

                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: " + e);
                                    /*if (custombtn.isLoading())
                                    {
                                        custombtn.hideLoading();
                                    }*/

                                        progressBarVerify.setVisibility(View.GONE);
                                        verify.setText("VERIFY");

                                        DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                                    }
                                });

                            }
                        });


                    } else {

                    /*if (custombtn.isLoading())
                    {
                        custombtn.hideLoading();
                    }*/

                        progressBarVerify.setVisibility(View.GONE);
                        verify.setText("VERIFY");

                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.e(TAG, "Invalid code.");

                            DialogBoxError.showError(context, "Invalid code");

//                        Snackbar.make(findViewById(android.R.id.content), "Invalid code.",
//                                Snackbar.LENGTH_SHORT).show();
                            // [END_EXCLUDE]
                        }
                    }
                }
            });

        }
        else
        {
            DialogBoxError.showInternetDialog(OTPLoginActivity.this);
        }


    }


    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    private Task<HashMap> login() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("phonenumber", Constants.MOB_CODE + txt_phonenumber);
        data.put("device_token", token_new);

        return mFunctions.getHttpsCallable("login").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }
}