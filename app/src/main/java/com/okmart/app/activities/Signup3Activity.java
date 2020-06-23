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

public class Signup3Activity extends AppCompatActivity {

    private EditText name;
    private String txt_phonenumber, txt_email, txt_name;
    private ImageView img_back;
    private TextView next;
    private Context context = Signup3Activity.this;
    private String TAG = Signup3Activity.class.getSimpleName();
    private ProgressBar progressBarNext;

    private FirebaseFunctions mFunctions;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        getSupportActionBar().hide();

        mFunctions = FirebaseFunctions.getInstance();
        progressBarNext = findViewById(R.id.progressBarNext);

        Bundle extras = getIntent().getExtras();
        if (!(extras == null)) {

            txt_phonenumber = getIntent().getStringExtra("phonenumber");
            txt_email = getIntent().getStringExtra("email");

        }

        name = findViewById(R.id.name);

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

                txt_name = name.getText().toString();

                if (TextUtils.isEmpty(txt_name)) {
                    DialogBoxError.showError(context, "Please enter name");
                    return;
                }


//                progressBarNext.setVisibility(View.VISIBLE);
//                next.setText("");

                Intent intent = new Intent(Signup3Activity.this, Signup4Activity.class);
                intent.putExtra("phonenumber", txt_phonenumber);
                intent.putExtra("email", txt_email);
                intent.putExtra("name", txt_name);
                startActivity(intent);

//                startPhoneNumberVerification(txt_phonenumber);


            }
        });

        next = findViewById(R.id.next);


//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                // This callback will be invoked in two situations:
//                // 1 - Instant verification. In some cases the phone number can be instantly
//                //     verified without needing to send or enter a verification code.
//                // 2 - Auto-retrieval. On some devices Google Play services can automatically
//                //     detect the incoming verification SMS and perform verification without
//                //     user action.
//                Log.d(TAG, "onVerificationCompleted:" + credential);
//
//                // signInWithPhoneAuthCredential(credential);
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                // This callback is invoked in an invalid request for verification is made,
//                // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e);
//
//
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//
//                    DialogBoxError.showError(context, "Please enter the phone number in a correct format");
//                    // Invalid request
//                    // ...
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//
//                    DialogBoxError.showError(context, "Quota exceeded.");
//                    // The SMS quota for the project has been exceeded
//                    // ...
//                }
//
//                progressBarNext.setVisibility(View.GONE);
//                next.setText("NEXT");
//                // Show a message and update the UI
//                // ...
//            }
//
//            @Override
//            public void onCodeSent(@NonNull String verificationId,
//                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                // The SMS verification code has been sent to the provided phone number, we
//                // now need to ask the user to enter the code and then construct a credential
//                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:" + verificationId);
//
//
//                progressBarNext.setVisibility(View.GONE);
//                next.setText("NEXT");
//
//
//                Intent intent = new Intent(context, OTPSignupActivity.class);
//                intent.putExtra("name", txt_name);
//                intent.putExtra("address_line_1", "");
//                intent.putExtra("address_line_2", "");
//                intent.putExtra("city", "");
//                intent.putExtra("state", "");
//                intent.putExtra("state_ref", "");
//                intent.putExtra("postal_code", "");
//                intent.putExtra("country", "");
//                intent.putExtra("phonenumber", txt_phonenumber);
//                intent.putExtra("email", txt_email);
//                intent.putExtra("verificationId", verificationId);
//                intent.putExtra("token", token);
//                startActivity(intent);
////                finish();
//
//
//            }
//        };
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        progressBarNext.setVisibility(View.GONE);
//        next.setText("NEXT");
//    }
//
//    private void startPhoneNumberVerification(String phoneNumber) {
//        // [START start_phone_auth]
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacks
//        // [END start_phone_auth]
//
//        mVerificationInProgress = true;
//    }

}
