package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.okmart.app.utilities.DialogBoxError;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Signup4Activity extends AppCompatActivity {

    private EditText referred_code;
    private String txt_phonenumber, txt_email, txt_name, txt_referred_code="";
    private ImageView img_back;
    private TextView skip, next;
    private Context context = Signup4Activity.this;
    private String TAG = Signup4Activity.class.getSimpleName();
    private ProgressBar progressBarNext;

    private FirebaseFunctions mFunctions;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);

        getSupportActionBar().hide();

        mFunctions = FirebaseFunctions.getInstance();
        progressBarNext = findViewById(R.id.progressBarNext);

        Bundle extras = getIntent().getExtras();
        if (!(extras == null)) {

            txt_phonenumber = getIntent().getStringExtra("phonenumber");
            txt_email = getIntent().getStringExtra("email");
            txt_name = getIntent().getStringExtra("name");

        }

        referred_code = findViewById(R.id.referred_code);

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cardView = findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txt_referred_code = referred_code.getText().toString();

                if (TextUtils.isEmpty(txt_referred_code)) {
                    DialogBoxError.showError(context, "Please enter referral code");
                    return;
                }
                else if (txt_referred_code.length()<6 || txt_referred_code.length()>8  ) {
                    DialogBoxError.showError(context, "Please enter valid referral code");
                    return;
                }


                progressBarNext.setVisibility(View.VISIBLE);
                next.setText("");


                verifyReferredCode().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e(TAG, "onSuccess: " + hashMap);

                        progressBarNext.setVisibility(View.GONE);
                        next.setText("NEXT");


                        String is_verified = hashMap.get("is_verified").toString();

                        if (is_verified.equals("true")) {

                            startPhoneNumberVerification(txt_phonenumber);

                        }
                        else {

                            DialogBoxError.showError(context, "Please enter valid referral code");
                        }
                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e);

                        progressBarNext.setVisibility(View.GONE);
                        next.setText("NEXT");

                        DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                    }
                });
            }
        });

        skip = findViewById(R.id.skip);
        next = findViewById(R.id.next);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPhoneNumberVerification(txt_phonenumber);
            }
        });

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

                progressBarNext.setVisibility(View.GONE);
                next.setText("NEXT");
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

                progressBarNext.setVisibility(View.GONE);
                next.setText("NEXT");

                Intent intent = new Intent(context, OTPSignupActivity.class);
                intent.putExtra("name", txt_name);
                intent.putExtra("address_line_1", "");
                intent.putExtra("address_line_2", "");
                intent.putExtra("city", "");
                intent.putExtra("state", "");
                intent.putExtra("state_ref", "");
                intent.putExtra("postal_code", "");
                intent.putExtra("country", "");
                intent.putExtra("phonenumber", txt_phonenumber);
                intent.putExtra("email", txt_email);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("token", token);
                intent.putExtra("referred_code", txt_referred_code);
                startActivity(intent);
                finish();

            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBarNext.setVisibility(View.GONE);
        next.setText("NEXT");
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


    private Task<HashMap> verifyReferredCode() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("referred_code", txt_referred_code);

        return mFunctions.getHttpsCallable("verifyReferredCode").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }
}