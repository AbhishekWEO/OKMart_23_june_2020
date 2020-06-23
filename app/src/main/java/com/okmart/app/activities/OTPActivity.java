package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.okmart.app.utilities.DialogBox;
import com.okmart.app.utilities.DialogBoxError;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFunctions mFunctions;
    private String TAG = OTPLoginActivity.class.getSimpleName();
    private Context context = OTPActivity.this;
    private OtpTextView otp_view;
    private String new_phone_no, verificationId;
    private TextView phonenumber, change_number, resend;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private String token_new, otp = "";
    private Button verify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private String old_phone_no;//,new_phone_no;//txt_phonenumber

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mFunctions = FirebaseFunctions.getInstance();
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();
        initXml();
        setOnClickListener();


        Bundle extras = getIntent().getExtras();
        if (!(extras == null)) {

            new_phone_no = extras.getString("phonenumber");

            verificationId = extras.getString("verificationId");
            mResendToken = (PhoneAuthProvider.ForceResendingToken) extras.get("token");

            phonenumber.setText(new_phone_no);
        }

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

                    DialogBoxError.showError(context, "Please enter the phone number in a correct format");
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {

                    DialogBoxError.showError(context, "Quota exceeded.");
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
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
                Log.d(TAG, "onCodeSent:" + verificationId);

                mResendToken = token;

                // Save verification ID and resending token so we can use them later
                verificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]

            }
        };


        otp_view.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.

                verifyPhoneNumberWithCode(verificationId, otp);
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(otp.equals(""))
                {
                    DialogBoxError.showError(context, "Please fill 6 digit OTP");
                    return;
                }

                verifyPhoneNumberWithCode(verificationId, otp);
            }
        });

    }

    private void initXml()
    {
        otp_view = findViewById(R.id.otp_view);
        verify = findViewById(R.id.verify);
        phonenumber = findViewById(R.id.phonenumber);
        change_number = findViewById(R.id.change_number);
        resend = findViewById(R.id.resend);

    }

    private void setOnClickListener()
    {
        change_number.setOnClickListener(this);
        resend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.resend:
                resendVerificationCode(new_phone_no, mResendToken);
                break;
        }
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

                                DialogBoxError.showError(OTPActivity.this, "Invalid code.");
                                // [END_EXCLUDE]
                            }

                        }
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
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]

        FirebaseUser user = mAuth.getCurrentUser();

        user.updatePhoneNumber(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {

                            userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                                @Override
                                public void onSuccess(HashMap hashMap) {
                                    Log.e(TAG, "onSuccess:userDetails " + hashMap);

                                    HashMap userData = (HashMap) hashMap.get("userData");


                                    old_phone_no = (String) userData.get("phone_no");


                                    updateMobileNumber().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                                        @Override
                                        public void onSuccess(HashMap hashMap) {

                                            Log.e(TAG, "onSuccess: " + hashMap);

                                            String msg=hashMap.get("message").toString();

                                            DialogBox.showDialog(OTPActivity.this, msg);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onSuccess: error");
                                            DialogBoxError.showError(OTPActivity.this,e.getMessage());

                                        }
                                    });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: " + e);
                                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                                }
                            });

                        } else {


                            String a = task.getException().getMessage();
                            if(a.equals("This credential is already associated with a different user account."))
                            {
                                Toast.makeText(context, "This credential is already associated with a different user account.", Toast.LENGTH_SHORT).show();
                            }

                            Log.e(TAG, "onSuccesserror" + task.getException().getMessage().toString());
                            DialogBoxError.showError(OTPActivity.this,task.getException().getMessage().toString());
                        }
                    }
                });

    }

    private Task<HashMap> updateMobileNumber() {
        Map<String, Object> data = new HashMap<>();
        data.put("prev_mobile_no", old_phone_no);
        data.put("new_mobile_no", new_phone_no);
        return mFunctions.getHttpsCallable("updateMobileNumber").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
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