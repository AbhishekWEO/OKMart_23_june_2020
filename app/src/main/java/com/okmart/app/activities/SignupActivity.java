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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.okmart.app.adapters.StateListAdapter;
import com.okmart.app.model.StateListModel;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DialogBoxState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = SignupActivity.class.getSimpleName();
    //    //private Button back, next;
    private TextView next;
    private ImageView img_back;
    private CardView cardView;
    //    private DialogBoxState dialogBoxState;
    private Context context = SignupActivity.this;

    private EditText phonenumber;
    private String input_phonenumber, txt_phonenumber;
    //    private EditText name, address_line_1, address_line_2, city, state, postal_code, country, phonenumber, email;
//    private String txt_name, txt_address_line_1, txt_address_line_2, txt_city, txt_state, txt_state_ref, txt_postal_code, txt_country, input_phonenumber, txt_phonenumber, txt_email;

    //    private List<StateListModel> stateLists = new ArrayList<>();
//    private String type = "signup";
//
//    private LoadingButton custombtn,custombtnBack;
    private ProgressBar progressBarNext;
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        mFunctions = FirebaseFunctions.getInstance();

//        dialogBoxState = new DialogBoxState();
//
//        name = findViewById(R.id.name);
//        address_line_1 = findViewById(R.id.address_line_1);
//        address_line_2 = findViewById(R.id.address_line_2);
//        city = findViewById(R.id.city);
//        postal_code = findViewById(R.id.postal_code);
//        country = findViewById(R.id.country);
        phonenumber = findViewById(R.id.phonenumber);
//        email = findViewById(R.id.email);
//
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        cardView = findViewById(R.id.cardView);
        cardView.setOnClickListener(this);
//
//        custombtnBack=findViewById(R.id.custombtnBack);
//        custombtnBack.setOnClickListener(this);
//
        next = findViewById(R.id.next);
//
//        custombtn = findViewById(R.id.custombtn);
//        custombtn.setOnClickListener(this);
//
        progressBarNext = findViewById(R.id.progressBarNext);
//
//        state = findViewById(R.id.state);
//        state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    dialogBoxState.state(context, state, stateLists, type);
//                }
//            }
//        });
//        state.setOnClickListener(this);
//
//        getSetData();
//
//        getStateList().addOnSuccessListener(new OnSuccessListener<HashMap>() {
//            @Override
//            public void onSuccess(HashMap hashMap) {
//                Log.e(TAG, "onSuccess: " + hashMap);
//                ArrayList arrayList = (ArrayList) hashMap.get("stateList");
//
//                stateLists.clear();
//
//                for(int i = 0 ; i < arrayList.size() ; i++)
//                {
//                    StateListModel stateListModel = new StateListModel();
//                    HashMap stateList = (HashMap) arrayList.get(i);
//                    stateListModel.setName(stateList.get("name").toString());
//                    stateListModel.setState_ref(stateList.get("state_ref").toString());
//                    stateListModel.setCode(stateList.get("code").toString());
//                    stateListModel.setState_ref(stateList.get("state_ref").toString());
//
//                    stateLists.add(stateListModel);
//
//                    Log.e(TAG, "onSuccess:stateList " + stateList.get("name").toString());
//                }
//
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e(TAG, "onFailure: "+e );
//                DialogBoxError.showError(context, context.getString(R.string.something_went_wrong));
//            }
//        });


    }


    @Override
    protected void onResume() {
        super.onResume();

        progressBarNext.setVisibility(View.GONE);
        next.setText("NEXT");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if (view.getId() == R.id.cardView) {
            next.setText("");
            progressBarNext.setVisibility(View.VISIBLE);

            checkRegisteration();

        }
//        else if (view.getId() == R.id.state)
//        {
//            dialogBoxState.state(context, state, stateLists, type);
//        }
//        else if (view.getId() == R.id.custombtn)
//        {
//            if (!custombtn.isLoading())
//            {
//                custombtn.showLoading();
//                checkRegisteration();
//            }
//        }
//        else if (view.getId()==R.id.custombtnBack)
//        {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }
//
//
//    private boolean validate() {
//    private boolean validate() {
//
////        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
////
////        if (TextUtils.isEmpty(txt_name)) {
////            DialogBoxError.showError(context, "Please Enter Name");
////            return false;
////        }
////        else if (TextUtils.isEmpty(txt_address_line_1)) {
////            DialogBoxError.showError(context, "Please Enter Address");
////            return false;
////        }
////        else if (TextUtils.isEmpty(txt_city)) {
////            DialogBoxError.showError(context, "Please enter city");
////            return false;
////        }
////        else if (TextUtils.isEmpty(txt_state)) {
////            DialogBoxError.showError(context, "Please enter state");
////            return false;
////        }
////        else if (TextUtils.isEmpty(txt_postal_code)) {
////            DialogBoxError.showError(context, "Please enter postcode");
////            return false;
////        }
////        else if (TextUtils.isEmpty(input_phonenumber)) {
////            DialogBoxError.showError(context, "Please enter mobile number");
////            return false;
////        }
////        else if (input_phonenumber.length() < 9) {
////            DialogBoxError.showError(context, "Please enter correct mobile number");
////            return false;
////        }
////        else if (TextUtils.isEmpty(txt_email)) {
////            DialogBoxError.showError(context, "Please enter email");
////            return false;
////        }
////        else if (!(txt_email.matches(emailPattern))) {
////            DialogBoxError.showError(context, "Please enter correct email");
////            return false;
////        }
//
//        return true;
//    }

    private void checkRegisteration() {

        cardView.setEnabled(false);
        cardView.setFocusable(false);
        cardView.setClickable(false);

        input_phonenumber = phonenumber.getText().toString();

        String first_char = "";

        if(input_phonenumber.length() > 0) {

            first_char = input_phonenumber.substring(0,1);
        }

        if (TextUtils.isEmpty(input_phonenumber)) {
            DialogBoxError.showError(context, "Please enter mobile number");

            progressBarNext.setVisibility(View.GONE);
            next.setText("NEXT");

            return;
        }
        else if (first_char.equalsIgnoreCase("0")) {
            DialogBoxError.showError(context, "Please enter phone number in this format 11xxxxxxxx without 0");

            progressBarNext.setVisibility(View.GONE);
            next.setText("NEXT");

            return;
        }
        else if (input_phonenumber.length()<9) {
            DialogBoxError.showError(context, "Please enter correct phone number");

            progressBarNext.setVisibility(View.GONE);
            next.setText("NEXT");

            return;
        }

        txt_phonenumber = Constants.MOB_CODE + input_phonenumber;

        checkRegisterationStatusOfUser().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess: " + hashMap);

                cardView.setEnabled(true);
                cardView.setFocusable(true);
                cardView.setClickable(true);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                progressBarNext.setVisibility(View.GONE);
                next.setText("NEXT");

                if (response_msg.equals("user blocked")) {

                    DialogBoxError.showError(context, message);


                } else if (response_msg.equals("user not registered")) {

                    Intent intent = new Intent(SignupActivity.this, Signup2Activity.class);
                    intent.putExtra("phonenumber", txt_phonenumber);
                    startActivity(intent);

//                    startPhoneNumberVerification(txt_phonenumber);

                } else if (response_msg.equals("registered user")) {

                    DialogBoxError.showError(context, message);

                } else {

                    DialogBoxError.showError(context, message);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e);

                cardView.setEnabled(true);
                cardView.setFocusable(true);
                cardView.setClickable(true);

                progressBarNext.setVisibility(View.GONE);
                next.setText("NEXT");
                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
            }
        });

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

    //    private Task<HashMap> getStateList() {
//        // Create the arguments to the callable function.
//        Map<String, Object> data = new HashMap<>();
//
//        return mFunctions.getHttpsCallable("getStateList").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
//            @Override
//            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
//                return (HashMap) task.getResult().getData();
//            }
//        });
//    }
//
//
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
//
//
    public void stateRef (String state_name, String state_ref)
    {
        String txt_state_ref = state_ref;
    }
//
//
//    private void getSetData()
//    {
//        if (getIntent().getStringExtra("name")!=null)
//        {
//            name.setText(getIntent().getStringExtra("name"));
//            address_line_1.setText(getIntent().getStringExtra("address_line_1"));
//            address_line_2.setText(getIntent().getStringExtra("address_line_2"));
//            city.setText(getIntent().getStringExtra("city"));
//            state.setText(getIntent().getStringExtra("state"));
//            txt_state_ref=getIntent().getStringExtra("state_ref");
//            postal_code.setText(getIntent().getStringExtra("postal_code"));
//            country.setText(getIntent().getStringExtra("country"));
//            phonenumber.setText(getIntent().getStringExtra("ph_no"));
//            email.setText(getIntent().getStringExtra("email"));
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//            phonenumber.requestFocus();
//        }
//    }

}