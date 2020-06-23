package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBox;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;


public class ChangeNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_back,img_edit;
    private EditText phonenumber;
    private Button btn_update;
    private LinearLayout ll_main,ll_otp;
    private RelativeLayout rl_resend,rl_bottom;
    private TextView tv_mobNum,tv_update,tv_resend,tv_view_line;
    private ProgressBar progressBar,progressBarSubmit;
    private OtpTextView otp_view;

    private String mobNo="";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean mVerificationInProgress = false;

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private String TAG = ChangeNumberActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;
    private String mVerificationId;
    private String old_phone_no="";
    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number);

        sharedPreferenceUtil = new SharedPreferenceUtil(ChangeNumberActivity.this);

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();


        getSupportActionBar().hide();
        initXml();
        setOnClickListener();

        // [START phone_auth_callbacks]
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

                mVerificationInProgress = false;
                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    DialogBoxError.showError(ChangeNumberActivity.this, "Please enter the phone number in a correct format");
                    // Invalid request
                    // ...



                } else if (e instanceof FirebaseTooManyRequestsException) {

                    DialogBoxError.showError(ChangeNumberActivity.this, "Quota exceeded.");
                    // The SMS quota for the project has been exceeded
                    // ...

                }
                progressBarVisibilitGone();
                phonenumberEditable();

                phonenumber.requestFocus();
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                updateUI(STATE_CODE_SENT);

                Log.d(TAG, "onCodeSent:" + verificationId);

                /*Intent intent = new Intent(ChangeNumberActivity.this, OTPActivity.class);
                intent.putExtra("phonenumber", mobNo);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("token", token);
                startActivity(intent);
                finish();*/

                mVerificationId = verificationId;
                mResendToken = token;

                ll_otp.setVisibility(View.VISIBLE);
                rl_resend.setVisibility(View.VISIBLE);
                img_edit.setVisibility(View.VISIBLE);
                tv_view_line.setVisibility(View.GONE);
                /*phonenumber.setEnabled(false);
                phonenumber.setFocusable(false);*/

                tv_update.setText("submit");
                progressBarSubmit.setVisibility(View.GONE);

                DialogBoxError.showKeyboard(ChangeNumberActivity.this,otp_view);
                otp_view.requestFocusOTP();

            }
        };
        // [END phone_auth_callbacks]

    }

    private void initXml() {

        img_back=findViewById(R.id.img_back);
        phonenumber=findViewById(R.id.phonenumber);
        //btn_update=findViewById(R.id.btn_update);
        ll_main=findViewById(R.id.ll_main);
        ll_otp=findViewById(R.id.ll_otp);
        img_edit=findViewById(R.id.img_edit);
        tv_mobNum=findViewById(R.id.tv_mobNum);
        tv_update=findViewById(R.id.tv_update);
        progressBarSubmit=findViewById(R.id.progressBarSubmit);
        rl_resend=findViewById(R.id.rl_resend);
        tv_resend=findViewById(R.id.tv_resend);
        tv_view_line=findViewById(R.id.tv_view_line);
        otp_view=findViewById(R.id.otp_view);
        progressBar=findViewById(R.id.progressBar);
        rl_bottom=findViewById(R.id.rl_bottom);

        rl_bottom.setVisibility(View.GONE);
        ll_main.setVisibility(View.GONE);

        if (DialogBoxError.checkInternetConnection(ChangeNumberActivity.this))
        {
            getUserDetails();
        }
        else
        {
            DialogBoxError.showInternetDialog(ChangeNumberActivity.this);
        }

        setOnEditorActionListene();

        setOtpListener();

    }

    private void setOnEditorActionListene() {
        phonenumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if (DialogBoxError.checkInternetConnection(ChangeNumberActivity.this))
                    {
                        updateNumber();
                    }
                    else
                    {
                        DialogBoxError.showInternetDialog(ChangeNumberActivity.this);
                    }

                    return true;
                }
                return false;
            }
        });
    }

    private void setOtpListener() {
        otp_view.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            @Override
            public void onOTPComplete(String otp)
            {
                // fired when user has entered the OTP fully.

                if (DialogBoxError.checkInternetConnection(ChangeNumberActivity.this))
                {
                    verifyPhoneNumberWithCode(mVerificationId, otp);
                    DialogBoxError.hideKeyboard(ChangeNumberActivity.this);
                    tv_update.setText("");
                    progressBarSubmit.setVisibility(View.VISIBLE);
                }
                else
                {
                    DialogBoxError.showInternetDialog(ChangeNumberActivity.this);
                }

            }
        });
    }

    private void setOnClickListener() {
        img_back.setOnClickListener(this);
//        btn_update.setOnClickListener(this);

        img_edit.setOnClickListener(this);
        tv_update.setOnClickListener(this);
        tv_resend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_back:
                if (progressBarSubmit.getVisibility()==View.GONE)
                {
                    finish();
                }

                break;
            //case R.id.btn_update:
            case R.id.tv_update:

                if (DialogBoxError.checkInternetConnection(ChangeNumberActivity.this))
                {
                    if (tv_update.getText().toString().equalsIgnoreCase("submit"))
                    {
                        if(otp_view.getOTP().isEmpty())
                        {
                            DialogBoxError.showError(ChangeNumberActivity.this, "Please fill 6 digit OTP");
                            return;
                        }
                        else if(otp_view.getOTP().trim().length()<6)
                        {
                            DialogBoxError.showError(ChangeNumberActivity.this, "Please fill 6 digit OTP");
                            return;
                        }

                        verifyPhoneNumberWithCode(mVerificationId, otp_view.getOTP());
                        tv_update.setText("");
                        progressBarSubmit.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        if (DialogBoxError.checkInternetConnection(ChangeNumberActivity.this))
                        {
                            updateNumber();
                        }
                        else
                        {
                            DialogBoxError.showInternetDialog(ChangeNumberActivity.this);
                        }

                    }
                }
                else
                {
                    DialogBoxError.showInternetDialog(ChangeNumberActivity.this);
                }

                break;

            case R.id.tv_resend:
                if (DialogBoxError.checkInternetConnection(ChangeNumberActivity.this))
                {
                    resendVerificationCode(mobNo, mResendToken);
                    Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DialogBoxError.showInternetDialog(ChangeNumberActivity.this);
                }

                break;

            case R.id.img_edit:
                //phonenumber.setEnabled(true);
                DialogBoxError.hideKeyboard(ChangeNumberActivity.this);
                phonenumber.setFocusable(true);
                phonenumber.setFocusableInTouchMode(true);
                tv_view_line.setVisibility(View.VISIBLE);
                ll_otp.setVisibility(View.GONE);
                rl_resend.setVisibility(View.GONE);
                img_edit.setVisibility(View.GONE);
                tv_update.setText("update");

                DialogBoxError.showKeyboard(ChangeNumberActivity.this,phonenumber);
                phonenumber.requestFocus();
                phonenumber.setSelection(phonenumber.getText().length());
                otp_view.setOTP("");

                break;
        }
    }

    private boolean validations() {
        //String mobile_no = Constants.MOB_CODE + phonenumber.getText().toString();

        String first_char = "";

        if(phonenumber.getText().toString().length() > 0) {

            first_char = phonenumber.getText().toString().substring(0,1);
        }

        if (phonenumber.getText().toString().trim().isEmpty())
        {
            DialogBoxError.showError(this, "Please Enter Mobile Number");
            return false;
        }
        else if (phonenumber.getText().toString().trim().length() < 9)
        {
            DialogBoxError.showError(this, "Please Enter correct Mobile Number");
            return false;
        }
        else if (first_char.equalsIgnoreCase("0")) {
            DialogBoxError.showError(this, "Please enter phone number in this format 11xxxxxxxx without 0");

            return false;
        }

        return true;
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

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                Log.e(TAG, "STATE_INITIALIZED");
//                enableViews(mStartButton, mPhoneNumberField);
//                disableViews(mVerifyButton, mResendButton, mVerificationField);
//                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                Log.e(TAG, "code sent");

//                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
//                disableViews(mStartButton);
//                mDetailText.setText(R.string.status_code_sent);
//
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
//                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
//                        mVerificationField);
//                mDetailText.setText(R.string.status_verification_failed);
//
                Log.e(TAG, "STATE_VERIFY_FAILED");

                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
//                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
//                        mVerificationField);
//                mDetailText.setText(R.string.status_verification_succeeded);
//
//                // Set the verification text based on the credential
//                if (cred != null) {
//                    if (cred.getSmsCode() != null) {
//                        mVerificationField.setText(cred.getSmsCode());
//                    } else {
//                        mVerificationField.setText(R.string.instant_validation);
//                    }
//                }

                Log.e(TAG, "STATE_VERIFY_SUCCESS:");

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
//                mDetailText.setText(R.string.status_sign_in_failed);

                Log.e(TAG, "STATE_SIGNIN_FAILED");
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }

    }

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Log.e(TAG, "phn no: " + mobNo);

                            if (!task.isSuccessful()) {
                                Log.e(TAG, "Exeption " + task.getException().getMessage());
                                Exception e = task.getException();
                                if (e instanceof FirebaseFunctionsException) {
                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                    Object details = ffe.getDetails();
                                }
                            } else {
                                Log.e(TAG, "error Report ");
                            }
//                                }
//                            });

                            // [START_EXCLUDE]
//                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI

                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.e(TAG, "Invalid code.");

                                DialogBoxError.showError(ChangeNumberActivity.this,"Invalid code.");;
                                progressBarVisibilitGone();
                                phonenumberEditable();
                                // [END_EXCLUDE]
                            }

                        }
                    }
                });
    }

    private Task<HashMap> checkRegisterationStatusOfUser() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("phonenumber", mobNo);

        return mFunctions.getHttpsCallable("checkRegisterationStatusOfUser").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private void progressBarVisibilitGone() {
        tv_update.setText("update");
        progressBarSubmit.setVisibility(View.GONE);
    }

    private void progressBarWitSubmit() {
        tv_update.setText("submit");
        progressBarSubmit.setVisibility(View.GONE);
    }

    private void phonenumberEditable() {
        phonenumber.setFocusable(true);
        phonenumber.setFocusableInTouchMode(true);
    }

    private void getUserDetails() {
        userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess:userDetails " + hashMap);


                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    HashMap userData = (HashMap) hashMap.get("userData");

                    old_phone_no = (String) userData.get("phone_no");
                    tv_mobNum.setText(old_phone_no);

                    ll_main.setVisibility(View.VISIBLE);
                    rl_bottom.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    //DialogBoxError.showKeyboard(ChangeNumberActivity.this,phonenumber);
                    phonenumber.requestFocus();
                }
                else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(ChangeNumberActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(ChangeNumberActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(ChangeNumberActivity.this,message);
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(ChangeNumberActivity.this, e.getMessage());
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

    // [START resend_verification]
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

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        if (DialogBoxError.checkInternetConnection(ChangeNumberActivity.this))
        {
            // [START verify_with_code]
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            // [END verify_with_code]

            FirebaseUser user = mAuth.getCurrentUser();

            user.updatePhoneNumber(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                updateMobileNumber().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                                    @Override
                                    public void onSuccess(HashMap hashMap) {

                                        Log.e(TAG, "onSuccess: " + hashMap);

                                        String msg=hashMap.get("message").toString();

                                        DialogBox.showDialog(ChangeNumberActivity.this, msg);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onSuccess: error");
                                        DialogBoxError.showError(ChangeNumberActivity.this,e.getMessage());
                                        progressBarWitSubmit();
                                    }
                                });
                            }
                            else
                            {
                                String a = task.getException().getMessage();
                                if(a.equals("This credential is already associated with a different user account."))
                                {
                                    Toast.makeText(ChangeNumberActivity.this, "This credential is already associated with a different user account.", Toast.LENGTH_SHORT).show();
                                }

                                Log.e(TAG, "onSuccesserror" + task.getException().getMessage().toString());
                                DialogBoxError.showError(ChangeNumberActivity.this,task.getException().getMessage().toString());
                                progressBarWitSubmit();
                            }
                        }
                    });
        }
        else
        {
            DialogBoxError.showInternetDialog(ChangeNumberActivity.this);
        }

    }

    private Task<HashMap> updateMobileNumber() {
        Map<String, Object> data = new HashMap<>();
        data.put("prev_mobile_no", old_phone_no);
        data.put("new_mobile_no", mobNo);
        return mFunctions.getHttpsCallable("updateMobileNumber").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private void updateNumber() {
        mobNo = Constants.MOB_CODE + phonenumber.getText().toString();
        if (validations())
        {
            tv_update.setText("");
            progressBarSubmit.setVisibility(View.VISIBLE);
            DialogBoxError.hideKeyboard(ChangeNumberActivity.this);

            checkRegisterationStatusOfUser().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("user blocked")) {

                        DialogBoxError.showError(ChangeNumberActivity.this, message);
                        progressBarVisibilitGone();
                        phonenumberEditable();
                        phonenumber.requestFocus();

                    } else if (response_msg.equals("user not registered")) {

                        startPhoneNumberVerification(mobNo);
                        // phonenumber.setEnabled(false);
                        phonenumber.setFocusable(false);
                        //startPhoneNumberVerification(Constants.MOB_CODE + phonenumber.getText().toString());

                    } else if (response_msg.equals("registered user")) {

                        DialogBoxError.showError(ChangeNumberActivity.this, "This mobile number is already registered");
                        progressBarVisibilitGone();
                        phonenumberEditable();
                        phonenumber.requestFocus();

                    } else {

                        DialogBoxError.showError(ChangeNumberActivity.this, message);
                        progressBarVisibilitGone();
                        phonenumberEditable();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e);
                    progressBarVisibilitGone();
                    phonenumberEditable();
                    /*DialogBoxError.showKeyboard(ChangeNumberActivity.this,phonenumber);
                    phonenumber.requestFocus();*/
                    DialogBoxError.showError(ChangeNumberActivity.this, getString(R.string.something_went_wrong));
                }
            });

        }
    }
}