package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.HashMap;
import java.util.Map;

public class ReloadActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = ReloadActivity.class.getSimpleName();
    private FirebaseFunctions mFunctions;
    private TextView walletPoint, tv_reloadNow;
    private Context context = ReloadActivity.this;
    private CardView cv_50Rm, cv_100Rm,cv_500Rm, cv_1000Rm;
    private EditText edt_price;
    private ImageView img_back;
    private String amt="";

    private ProgressBar progressBarWalletPoints;
    private SharedPreferenceUtil sharedPreferenceUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload);
        getSupportActionBar().hide();

        sharedPreferenceUtil = new SharedPreferenceUtil(ReloadActivity.this);

        progressBarWalletPoints = findViewById(R.id.progressBarWalletPoints);

        mFunctions = FirebaseFunctions.getInstance();
        walletPoint = findViewById(R.id.walletPoint);
        tv_reloadNow = findViewById(R.id.tv_reloadNow);

        edt_price = findViewById(R.id.edt_price);
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

        cv_50Rm = findViewById(R.id.cv_50Rm);
        cv_100Rm = findViewById(R.id.cv_100Rm);
        cv_500Rm = findViewById(R.id.cv_500Rm);
        cv_1000Rm = findViewById(R.id.cv_1000Rm);

        cv_50Rm.setOnClickListener(this);
        cv_100Rm.setOnClickListener(this);
        cv_500Rm.setOnClickListener(this);
        cv_1000Rm.setOnClickListener(this);
        tv_reloadNow.setOnClickListener(this);
        tv_reloadNow.setClickable(false);

        if (DialogBoxError.checkInternetConnection(ReloadActivity.this))
        {
            getWalletPointOfUser().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);

                    if(progressBarWalletPoints.isShown()) {

                        progressBarWalletPoints.setVisibility(View.GONE);
                    }

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        walletPoint.setText(DoubleDecimal.twoPointsComma(hashMap.get("walletPoint").toString()));
//                    walletPoint.setText(DoubleDecimal.twoPoints(hashMap.get("walletPoint").toString()) + " PT");

                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(ReloadActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(ReloadActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(ReloadActivity.this,message);
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    if(progressBarWalletPoints.isShown()) {

                        progressBarWalletPoints.setVisibility(View.GONE);
                    }

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(ReloadActivity.this);
        }

        edt_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                amt = edt_price.getText().toString().trim();

                int length=edt_price.getText().toString().trim().length();
                if (length==0 || Float.parseFloat(amt) <1)
                {
                    tv_reloadNow.setBackgroundColor(getResources().getColor(R.color.fade));
                    cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    tv_reloadNow.setClickable(false);
                }
                else
                {
                    tv_reloadNow.setClickable(true);
                    tv_reloadNow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }

                if (edt_price.getText().toString().trim().equalsIgnoreCase("50"))
                {
                    cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));

                }
                else if (edt_price.getText().toString().trim().equalsIgnoreCase("100"))
                {
                    cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));

                }
                else if (edt_price.getText().toString().trim().equalsIgnoreCase("500"))
                {
                    cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));

                }
                else if (edt_price.getText().toString().trim().equalsIgnoreCase("1000"))
                {
                    cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                else
                {
                    cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                    cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                }
            }
        });
    }

    private Task<HashMap> getWalletPointOfUser() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getWalletPointOfUser").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.cv_50Rm :

                amt="50";
                cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));

                edt_price.setText("50");
                edt_price.setSelection(edt_price.getText().length());

                tv_reloadNow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;

            case R.id.cv_100Rm :

                amt="100";
                cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));

                edt_price.setText("100");
                edt_price.setSelection(edt_price.getText().length());

                tv_reloadNow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;

            case R.id.cv_500Rm :

                amt="500";
                cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));

                edt_price.setText("500");
                edt_price.setSelection(edt_price.getText().length());

                tv_reloadNow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;

            case R.id.cv_1000Rm :

                amt="1000";
                cv_50Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                cv_100Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                cv_500Rm.setCardBackgroundColor(getResources().getColor(R.color.fade));
                cv_1000Rm.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

                edt_price.setText("1000");
                edt_price.setSelection(edt_price.getText().length());

                tv_reloadNow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                break;

            case R.id.img_back :

                finish();
                break;

            case R.id.tv_reloadNow:

                if (DialogBoxError.checkInternetConnection(ReloadActivity.this))
                {
                    if (validations())
                    {
                        Intent intent=new Intent(this,CreditCardActivity.class);
                        intent.putExtra("amt",amt);
                        startActivity(intent);
                    }
                }
                else
                {
                    DialogBoxError.showInternetDialog(ReloadActivity.this);
                }

                break;

        }
    }

    private boolean validations()
    {
        if (edt_price.getText().toString().trim().isEmpty())
        {
            DialogBoxError.showError(this, "Please Enter Your Preferred Amount");
            return false;
        }
        else if(Float.parseFloat(amt) < 10)
        {
            DialogBoxError.showError(this, "Min reload amount must be RM 10");
            return false;
        }
        return true;
    }

}