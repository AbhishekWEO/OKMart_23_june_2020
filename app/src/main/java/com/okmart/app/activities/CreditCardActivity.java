package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.adapters.CardListAdapter;
import com.okmart.app.interfaces.RetrofitInterface;
import com.okmart.app.model.ChargeModel;
import com.okmart.app.model.CreateCustomerCardModel;
import com.okmart.app.model.CreateCustomerModel;
import com.okmart.app.model.DeleteCard;
import com.okmart.app.model.FetchCardListModel;
import com.okmart.app.retrofit.RetrofitClient;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBox;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.ExampleEphemeralKeyProvider;
import com.okmart.app.utilities.SharedPreferenceUtil;
import com.okmart.app.utilities.Vibration;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.PaymentSession;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.Token;
import com.stripe.android.view.AddPaymentMethodActivityStarter;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class CreditCardActivity extends AppCompatActivity implements View.OnClickListener, CardListAdapter.ItemClickListener {

    //
    private String TAG = CreditCardActivity.class.getSimpleName();
    //
    private EditText edt_holderName,edt_cardNumber,edt_cvv,edt_expiredDate;
    private TextView tv_creditDebit,tv_payNow,tv_add_new_card,tv_walletPoint;//edt_expiredDate;
    private ProgressBar progressBarWalletPoints;
    private RelativeLayout oldcards;
    private LinearLayout ll_main,ll_newcards;
    private RecyclerView oldcardlist;
    private ImageView img_back;

    private String cardholder_name,stripe_customer_id="",cardID="",price="";
    private boolean isNewCard=true;

    private ProgressDialog dialog;
    private CardInputWidget card_input_widget,card_input;
    private int count,expirdate_count=0;

    private FirebaseFunctions mFunctions;
    private String txt_txn_id, txt_payment_status, txt_price, user_name, txt_card_brand, txt_card_last4, name;

    private List<FetchCardListModel.DataBean> getCardList = new ArrayList<>();
    private CardListAdapter cardListAdapter;

    //fpx

    private TextView fpx;
    private RadioButton radioBtn_cards,radioBtn_fpx;
    private ProgressBar progressPayNow;
    private String client_secret;
    private Stripe stripe;

    private PaymentSession paymentSession;
    private PaymentSession.PaymentSessionListener paymentSessionListener;

    private ImageView img_addNewCard;
    private TextView payment_method_result,tv_onlineBanking,tv_otherPaymentMthd;
    private RelativeLayout rl_onlineBanking;
    private double fpx_payment_amt,original_amt;
    int fpx_payment;
    private SharedPreferenceUtil sharedPreferenceUtil;


    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        getSupportActionBar().hide();

        sharedPreferenceUtil = new SharedPreferenceUtil(CreditCardActivity.this);

        mFunctions = FirebaseFunctions.getInstance();


        initXml();
        setOnClickListener();

    }

    private void initXml()
    {
        img_back=findViewById(R.id.img_back);
        edt_holderName=findViewById(R.id.edt_holderName);
        edt_cardNumber=findViewById(R.id.edt_cardNumber);
        edt_expiredDate=findViewById(R.id.edt_expiredDate);
        edt_cvv=findViewById(R.id.edt_cvv);
        card_input_widget=findViewById(R.id.card_input_widget);
        tv_payNow=findViewById(R.id.tv_payNow);
        oldcards=findViewById(R.id.oldcards);
        ll_main=findViewById(R.id.ll_main);
        ll_newcards=findViewById(R.id.ll_newcards);
        tv_creditDebit=findViewById(R.id.tv_creditDebit);
        tv_add_new_card=findViewById(R.id.tv_add_new_card);
        oldcardlist=findViewById(R.id.oldcardlist);
        card_input=findViewById(R.id.card_input);
        radioBtn_cards=findViewById(R.id.radioBtn_cards);
        radioBtn_fpx=findViewById(R.id.radioBtn_fpx);
        progressPayNow=findViewById(R.id.progressPayNow);
        tv_walletPoint = findViewById(R.id.tv_walletPoint);
        progressBarWalletPoints = findViewById(R.id.progressBarWalletPoints);
        tv_onlineBanking = findViewById(R.id.tv_onlineBanking);
        tv_otherPaymentMthd = findViewById(R.id.tv_otherPaymentMthd);
        rl_onlineBanking = findViewById(R.id.rl_onlineBanking);
        img_addNewCard = findViewById(R.id.img_addNewCard);


        ll_main.setVisibility(View.GONE);
        oldcardlist.setLayoutManager(new LinearLayoutManager(CreditCardActivity.this));

        Bundle extras = getIntent().getExtras();
        if (extras == null)
        {
            Log.e("extras", "null");
        }
        else
        {
            price = extras.getString("amt");
            //tv_payNow.setText("PAY NOW RM " + DoubleDecimal.twoPoints(price));

            double fpx_paymentamt = Double.parseDouble(extras.getString("amt"))*100;

            /*DecimalFormat df2 = new DecimalFormat("#.##");
            double dd = Double.parseDouble(df2.format(fpx_paymentamt));*/

            fpx_payment_amt = Double.parseDouble(String.format("%.2f", fpx_paymentamt));

            original_amt = Double.parseDouble(extras.getString("amt"));
        }

        //02 june
        if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
        {
            getUserWallet();


            dialog = new ProgressDialog(CreditCardActivity.this);
            dialog.setMessage("Please wait ...");
            dialog.setCancelable(false);
            dialog.show();

            getPublicSecretKey().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {

                    Log.e("Response",""+hashMap);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        HashMap data = (HashMap) hashMap.get("data");
                        String secret_key = data.get("secret_key").toString();
                        String public_key = data.get("public_key").toString();

                        //Constants.publishablekey = public_key;
                        //Constants.authant = "Bearer "+secret_key;

                        Constants.publishablekey =  "pk_test_GmeU9E8JWi34A1lB3pJE1UjW";
                        Constants.authant = "Bearer sk_test_hROAOKQRiZn4SjWPCarP0IMl00Le9CKx5V";


                        if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
                        {
                            getUserData();
                        }
                        else
                        {
                            DialogBoxError.showInternetDialog(CreditCardActivity.this);
                        }

                        //
                        PaymentConfiguration.init(CreditCardActivity.this, Constants.publishablekey);

                        CustomerSession.initCustomerSession(CreditCardActivity.this, new ExampleEphemeralKeyProvider(CreditCardActivity.this));

                        stripe = new Stripe(CreditCardActivity.this, Constants.publishablekey);

                        createFpxPaymentIntent().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap) {
                                Log.e(TAG, "onSuccess:userDetails " + hashMap);

                                String response_msg = hashMap.get("response_msg").toString();
                                String message = hashMap.get("message").toString();

                                if (response_msg.equals("Intent created successfully"))
                                {

                                    HashMap data = (HashMap) hashMap.get("data");

                                    client_secret = data.get("client_secret").toString();
                                }
                                else if (response_msg.equals(Constants.unauthorized))
                                {
                                    DialogBoxError.showDialogBlockUser(CreditCardActivity.this,message,sharedPreferenceUtil);
                                }
                                else if (response_msg.equals(Constants.under_maintenance))
                                {
                                    DialogBoxError.showError(CreditCardActivity.this,message);
                                }
                                else
                                {
                                    DialogBoxError.showError(CreditCardActivity.this,message);
                                }


                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Error: Amount must be no more than RM999,999.99
                                Log.e(TAG, "onFailure: " + e);
                                DialogBoxError.showDialog(CreditCardActivity.this, e.getMessage());

                            }
                        });
                    }
                    /*else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(CreditCardActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(CreditCardActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(CreditCardActivity.this,message);
                    }*/








                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ErrorMsg",e.getMessage());
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(CreditCardActivity.this);
        }


        /*if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
        {
            getUserData();
        }
        else
        {
            DialogBoxError.showInternetDialog(CreditCardActivity.this);
        }

        //
        PaymentConfiguration.init(CreditCardActivity.this, Constants.publishablekey);

        CustomerSession.initCustomerSession(this, new ExampleEphemeralKeyProvider(CreditCardActivity.this));

        stripe = new Stripe(CreditCardActivity.this, Constants.publishablekey);

        createFpxPaymentIntent().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess:userDetails " + hashMap);

                HashMap data = (HashMap) hashMap.get("data");

                client_secret = data.get("client_secret").toString();

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showDialog(CreditCardActivity.this, e.getMessage());
            }
        });*/

        //

        fpx = findViewById(R.id.fpx);
        tv_onlineBanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(CreditCardActivity.this, FpxPaymentActivity.class);
                intent.putExtra("amt",price);
                startActivity(intent);*/


                DialogBoxError.hideKeyboard(CreditCardActivity.this);

                if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
                {
                    launchAddPaymentMethod();
                }
                else
                {
                    DialogBoxError.showInternetDialog(CreditCardActivity.this);
                }
            }
        });

        addTextChangedListener();
    }

    private void getUserData()
    {
        /*dialog = new ProgressDialog(CreditCardActivity.this);
        dialog.setMessage("Please wait ...");
        dialog.setCancelable(false);
        dialog.show();*/

        userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e("CreditCardActivity", "onSuccess: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    HashMap hm = (HashMap) hashMap.get("userData");
                    user_name = CapitalUtils.capitalize((String) hm.get("user_name").toString());
                    progressBarWalletPoints.setVisibility(View.GONE);
                    if (hm.get("stripe_customer_id")!=null)
                    {
                        stripe_customer_id = (String) hm.get("stripe_customer_id").toString();
                    }
                    //stripe_customer_id = (String) hm.get("stripe_customer_id").toString();
                    edt_holderName.setText(user_name);
                    edt_holderName.setSelection(edt_holderName.getText().length());

                    if (!(stripe_customer_id.equals(""))) {

                        if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
                        {
                            requestToFetchCardList();
                        }
                        else
                        {
                            DialogBoxError.showInternetDialog(CreditCardActivity.this);
                        }


                    }
                    else
                    {
                        if (dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        // background.setVisibility(View.GONE);

                        tv_creditDebit.setText(getResources().getString(R.string.credit_card_debit_card));
                        oldcards.setVisibility(View.GONE);
                        ll_newcards.setVisibility(View.VISIBLE);
                        ll_main.setVisibility(View.VISIBLE);
                        DialogBoxError.showKeyboard(CreditCardActivity.this,edt_cardNumber);

                    }
                }
                else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(CreditCardActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(CreditCardActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(CreditCardActivity.this,message);
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }

                Log.e("CreditCardActivity", "onFailure: " + e);
                DialogBoxError.showError(CreditCardActivity.this,e.getMessage());
            }
        });
    }

    private void addTextChangedListener()
    {
        edt_holderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (validation())
                {
                    doPayNow();
                    /*String prc=DoubleDecimal.twoPoints(price);
                    String str="<b>PAY NOW </b> RM <b>"+prc+"</b>";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tv_payNow.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        tv_payNow.setText(Html.fromHtml(str));
                    }
                    tv_payNow.setBackgroundColor(getResources().getColor(R.color.green));*/
                }
                else
                {
                    payNow();
                }
            }
        });

        /*edt_cardNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.e("KeyEvent", "onKey: "+keyEvent );
                if(i == KeyEvent.KEYCODE_DEL) {
                    //perform erase when the text field is empty
                    Toast.makeText(CreditCardActivity.this, "del", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });*/

        edt_cardNumber.addTextChangedListener(new TextWatcher() {
            //29 jan

            //end
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) { /*Empty*/}

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s) {

                /*String str =edt_cardNumber.getText().toString().trim();
                String str2= str.replace(" ","");
                card_input_widget.setCardNumber(str2);*/

                //29 jan


                //end

                int inputlength = edt_cardNumber.getText().toString().length();

                /*
                if (count <= inputlength && (inputlength == 4 ||
                        inputlength == 9 || inputlength == 14))
                 */

                if (count <= inputlength && (inputlength == 5 ||
                        inputlength == 10 || inputlength == 15))
                {
                    String str=edt_cardNumber.getText().toString().trim();
                    StringBuilder sb=new StringBuilder(str);
                    sb.insert(inputlength-1," ");

                    String str1=sb.toString();

                    //edt_cardNumber.setText(edt_cardNumber.getText().toString()+" ");
                    edt_cardNumber.setText(str1);

                    //int pos = edt_cardNumber.getText().length();
                }

                count = edt_cardNumber.getText().toString().length();
                edt_cardNumber.setSelection(count);


                if (validation())
                {
                    doPayNow();
                }
                else
                {
                    payNow();
                }

                if (inputlength == 4)
                {
                    String card_num=edt_cardNumber.getText().toString().trim();
                    //card_num=card_num.replace(" ","");

                    card_input.setCardNumber(card_num);

                }
                else if (inputlength == 0)
                {
                    card_input.setCardNumber("");
                }
            }
        });

        edt_expiredDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                int length = edt_expiredDate.getText().toString().length();
                //if (expirdate_count<=length && length==4)
                /*if (expirdate_count<=length && length==5)
                {
                    StringBuilder sb = new StringBuilder(edt_expiredDate.getText().toString());
                    sb.insert(length-1,"-");
                    edt_expiredDate.setText(sb.toString());
                    //edt_expiredDate.setText(edt_expiredDate.getText().toString()+"-");
                }*/
                if (expirdate_count<=length && length==3)
                {
                    StringBuilder sb = new StringBuilder(edt_expiredDate.getText().toString());
                    sb.insert(length-1,"/");
                    edt_expiredDate.setText(sb.toString());
                    //edt_expiredDate.setText(edt_expiredDate.getText().toString()+"/");
                }
                expirdate_count = edt_expiredDate.getText().toString().length();
                edt_expiredDate.setSelection(expirdate_count);
                if (validation())
                {
                    doPayNow();
                }
                else
                {
                    payNow();
                }
            }
        });

        edt_cvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (validation())
                {
                    doPayNow();
                }
                else
                {
                    payNow();
                }
            }
        });
    }

    private void setOnClickListener()
    {

        img_back.setOnClickListener(this);
        tv_payNow.setOnClickListener(this);
        tv_add_new_card.setOnClickListener(this);
        radioBtn_cards.setOnClickListener(this);
        radioBtn_fpx.setOnClickListener(this);
        img_addNewCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_back:
                if (progressPayNow.isShown())
                {
                    Log.e(TAG, "progressPayNow: "+progressPayNow.isShown() );
                }
                else
                {
                    DialogBoxError.hideKeyboard(CreditCardActivity.this);
                    finish();
                }

                break;
            case R.id.tv_payNow:
                if (progressPayNow.isShown())
                {
                    Log.e(TAG, "progressPayNow: "+progressPayNow.isShown() );
                }
                else
                {
                    if (isNewCard)
                    {
                        if (validation2())
                        {
                            DialogBoxError.hideKeyboard(CreditCardActivity.this);
                            cardholder_name=edt_holderName.getText().toString().trim();
                            //card number
                            String card_num=edt_cardNumber.getText().toString().trim();
                            card_num=card_num.replace(" ","");
                            Log.e("card_num",card_num);
                            card_input_widget.setCardNumber(card_num);
                            //expiry date
                        /*String exp_date=edt_expiredDate.getText().toString().trim();
                        String splt[]=exp_date.split("-");
                        String yyyy=splt[0].substring(2,4);
                        String mm=splt[1];*/
                            String exp_date=edt_expiredDate.getText().toString().trim();
                            String splt[]=exp_date.split("/");
                            String yyyy=splt[1].substring(2,4);
                            String mm=splt[0];
                            int year=Integer.parseInt(yyyy);
                            int month=Integer.parseInt(mm);
                            card_input_widget.setExpiryDate(month,year);
                            Log.e("year",yyyy);
                            //cvv
                            card_input_widget.setCvcCode(edt_cvv.getText().toString().trim());
                            Card card = card_input_widget.getCard();
                            Log.e("CreditCardActivity", "stripe_functionality: " + card);

                            if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
                            {
                                DialogBoxError.hideKeyboard(CreditCardActivity.this);
                                stripe_functionality();
                            }
                            else
                            {
                                DialogBoxError.showInternetDialog(CreditCardActivity.this);
                            }
                        }
                    }
                    else
                    {
                        if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
                        {
                            DialogBoxError.hideKeyboard(CreditCardActivity.this);
                            stripe_functionality();
                        }
                        else
                        {
                            DialogBoxError.showInternetDialog(CreditCardActivity.this);
                        }
                    }
                }

                break;

            case R.id.tv_add_new_card:
                tv_creditDebit.setText(getResources().getString(R.string.credit_card_debit_card));
                oldcards.setVisibility(View.GONE);
                ll_newcards.setVisibility(View.VISIBLE);
                radioBtn_fpx.setVisibility(View.GONE);
                fpx.setVisibility(View.GONE);

                tv_otherPaymentMthd.setVisibility(View.GONE);
                rl_onlineBanking.setVisibility(View.GONE);

                isNewCard = true;

                DialogBoxError.showKeyboard(CreditCardActivity.this,edt_cardNumber);

                String str="<b>PAY NOW </b>";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tv_payNow.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tv_payNow.setText(Html.fromHtml(str));
                }
                tv_payNow.setBackgroundColor(getResources().getColor(R.color.mediumGrey));

                tv_payNow.setEnabled(false);
                tv_payNow.setFocusable(false);
                tv_payNow.setClickable(false);

                break;

            case R.id.radioBtn_cards:

                if (radioBtn_fpx.isChecked())
                {
                    radioBtn_cards.setChecked(true);
                    radioBtn_fpx.setChecked(false);
                    fpx.setVisibility(View.GONE);
                    if (isNewCard)
                    {
                        oldcards.setVisibility(View.GONE);
                        ll_newcards.setVisibility(View.VISIBLE);
                        tv_payNow.setBackgroundColor(getResources().getColor(R.color.mediumGrey));
                    }
                    else
                    {
                        oldcards.setVisibility(View.VISIBLE);
                        ll_newcards.setVisibility(View.GONE);
                        tv_payNow.setBackgroundColor(getResources().getColor(R.color.green));
                    }

                    tv_payNow.setEnabled(true);
                    tv_payNow.setFocusable(true);
                    tv_payNow.setClickable(true);
                }
                break;

            case R.id.radioBtn_fpx:

                if (radioBtn_cards.isChecked())
                {
                    radioBtn_fpx.setChecked(true);
                    radioBtn_cards.setChecked(false);

                    fpx.setVisibility(View.VISIBLE);

                    oldcards.setVisibility(View.GONE);
                    ll_newcards.setVisibility(View.GONE);

                    tv_payNow.setBackgroundColor(getResources().getColor(R.color.green));
                    tv_payNow.setEnabled(false);
                    tv_payNow.setFocusable(false);
                    tv_payNow.setClickable(false);
                }

                break;

            case R.id.img_addNewCard:
                tv_creditDebit.setText(getResources().getString(R.string.credit_card_debit_card));
                oldcards.setVisibility(View.GONE);
                ll_newcards.setVisibility(View.VISIBLE);
                radioBtn_fpx.setVisibility(View.GONE);
                fpx.setVisibility(View.GONE);

                tv_otherPaymentMthd.setVisibility(View.GONE);
                rl_onlineBanking.setVisibility(View.GONE);

                isNewCard = true;

                DialogBoxError.showKeyboard(CreditCardActivity.this,edt_cardNumber);

                /*String str="<b>PAY NOW </b>";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tv_payNow.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tv_payNow.setText(Html.fromHtml(str));
                }*/
                tv_payNow.setBackgroundColor(getResources().getColor(R.color.mediumGrey));
                tv_payNow.setFocusable(false);
                break;

        }
    }

    private boolean validation()
    {
        String splt[] = new String[0];
        String exp_date=edt_expiredDate.getText().toString().trim();
        /*if (!exp_date.isEmpty())
        {
            splt = exp_date.split("-");
            String yyyy=splt[0];
            int yy=Integer.parseInt(splt[0]);
        }*/
        if (edt_expiredDate.getText().toString().trim().length()==7)
        {
            splt = exp_date.split("/");
            String yyyy=splt[1];
            int yy=Integer.parseInt(splt[1]);
        }
        int year= Calendar.getInstance().get(Calendar.YEAR);
        int mth = Calendar.getInstance().get(Calendar.MONTH);
        int month=mth+1;
        Log.e("year",""+year+", month"+month);
        if (edt_holderName.getText().toString().trim().isEmpty())
        {
            //DialogBoxError.showError(this, "Please Enter Card Holder Name");
            return false;
        }
        else if (edt_cardNumber.getText().toString().trim().isEmpty())
        {
            //DialogBoxError.showError(this, "Please Enter Card Number");
            return false;
        }
        else if (edt_cardNumber.getText().toString().trim().length()<19)
        {
            //DialogBoxError.showError(this, "Please Enter Valid Card Number");
            return false;
        }
        else if (edt_expiredDate.getText().toString().trim().isEmpty())
        {
            //DialogBoxError.showError(this, "Please Enter Expiry Date");
            return false;
        }
        else if (edt_expiredDate.getText().toString().trim().length()<7)
        {
            //DialogBoxError.showError(this, "Invalid Expiry Date");
            return false;
        }
        else if (splt[0].length()<2)
        {
            //DialogBoxError.showError(this, "Inalid Expiry Date. Please use MM/YYYY format");
            return false;
        }
        else if (Integer.parseInt(splt[0])<1 || Integer.parseInt(splt[0])>12)
        {
            //DialogBoxError.showError(this, "Please Enter Valid Month Of Expiry Date");
            return false;
        }
        else if (splt[1].length()<4 || splt[1].length()>5)
        {
            //DialogBoxError.showError(this, "Inalid Expiry Date. Please use MM/YYYY format");
            return false;
        }
        else if (Integer.parseInt(splt[1])<year)
        {
            //DialogBoxError.showError(this, "Please Enter Valid Year Expiry Date");
            return false;
        }
        else if (edt_cvv.getText().toString().trim().isEmpty())
        {
            //DialogBoxError.showError(this, "Please Enter CVV");
            return false;
        }
        else if (edt_cvv.getText().toString().trim().length()<3)
        {
            //DialogBoxError.showError(this, "Please Enter valid CVV");
            return false;
        }
        return true;
    }

    private boolean validation2()
    {
        String splt[] = new String[0];
        String exp_date=edt_expiredDate.getText().toString().trim();
        /*if (!exp_date.isEmpty())
        {
            splt = exp_date.split("-");
            String yyyy=splt[0];
            int yy=Integer.parseInt(splt[0]);
        }*/
        if (edt_expiredDate.getText().toString().trim().length()==7)
        {
            splt = exp_date.split("/");
            String yyyy=splt[1];
            int yy=Integer.parseInt(splt[1]);
        }
        int year= Calendar.getInstance().get(Calendar.YEAR);
        int mth = Calendar.getInstance().get(Calendar.MONTH);
        int month=mth+1;
        Log.e("year",""+year+", month"+month);
        if (edt_holderName.getText().toString().trim().isEmpty())
        {
            DialogBoxError.showError(this, "Please enter card holder name");
            return false;
        }
        else if (edt_cardNumber.getText().toString().trim().isEmpty())
        {
            DialogBoxError.showError(this, "Please enter card number");
            return false;
        }
        else if (edt_cardNumber.getText().toString().trim().length()<19)
        {
            DialogBoxError.showError(this, "Please enter valid card number");
            return false;
        }
        else if (edt_expiredDate.getText().toString().trim().isEmpty())
        {
            DialogBoxError.showError(this, "Please enter expiry date");
            return false;
        }
        else if (edt_expiredDate.getText().toString().trim().length()<7)
        {
            DialogBoxError.showError(this, "Invalid expiry date. Please use MM/YYYY format");
            return false;
        }
        else if (splt[0].length()<2)
        {
            DialogBoxError.showError(this, "Invalid expiry date. Please use MM/YYYY format");
            return false;
        }
        else if (Integer.parseInt(splt[0])<1 || Integer.parseInt(splt[0])>12)
        {
            DialogBoxError.showError(this, "Please enter valid month Of expiry date");
            return false;
        }
        else if (splt[1].length()<4 || splt[1].length()>5)
        {
            DialogBoxError.showError(this, "Invalid expiry date. Please use MM/YYYY format");
            return false;
        }
        else if (Integer.parseInt(splt[1])<year)
        {
            DialogBoxError.showError(this, "Please enter valid year expiry date");
            return false;
        }
        else if (edt_cvv.getText().toString().trim().isEmpty())
        {
            DialogBoxError.showError(this, "Please enter CVV");
            return false;
        }
        else if (edt_cvv.getText().toString().trim().length()<3)
        {
            DialogBoxError.showError(this, "Please enter valid CVV");
            return false;
        }
        return true;
    }

    public void stripe_functionality()
    {
        dialog = new ProgressDialog(CreditCardActivity.this);
        dialog.setMessage("Please wait ...");
        dialog.setCancelable(false);
        dialog.show();

        if (stripe_customer_id.equals("")) {

            isNewCard = true;

        }

        if (isNewCard)
        {
            Card card = card_input_widget.getCard();

            Stripe stripe = new Stripe(CreditCardActivity.this, Constants.publishablekey);

            Log.e("CreditCardActivity", "stripe_functionality: " + card);

            if (card!=null)
            {

                card = new Card.Builder(card.getNumber(), card.getExpMonth(), card.getExpYear(), card.getCvc())
                        .name(cardholder_name)
                        .build();


//                card.setName(cardholder_name);

                stripe.createToken(card, new ApiResultCallback<Token>() {
                    @Override
                    public void onSuccess(Token token) {

                        if (stripe_customer_id.equals("")) {

                            requestToCreateCustomer(token.getId());

                        } else {

                            requestToCreateCard(stripe_customer_id, token.getId());

                        }
                    }

                    @Override
                    public void onError(@NotNull Exception e) {
                        if (dialog.isShowing())
                        {
                            dialog.dismiss();
                        }

                        DialogBoxError.showError(CreditCardActivity.this,e.getMessage());
                    }
                });

//                stripe.createToken(card, new TokenCallback()
//                        {
//                            public void onSuccess(Token token)
//                            {
//
//                                if (stripe_customer_id.equals("")) {
//
//                                    requestToCreateCustomer(token.getId());
//
//                                } else {
//
//                                    requestToCreateCard(stripe_customer_id, token.getId());
//
//                                }
//
//                            }
//
//                            public void onError(Exception e) {
//                                // Show localized error message
//                                if (dialog.isShowing())
//                                {
//                                    dialog.dismiss();
//                                }
//
//                                DialogBoxError.showError(CreditCardActivity.this,e.getMessage());
//
//                            }
//                        }
//                );
            }
            else
            {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                DialogBoxError.showError(CreditCardActivity.this,"You have entered wrong card number");
            }
        }
        else
        {
            requestToCharge(stripe_customer_id, cardID);
        }

    }

    private void requestToCreateCustomer(String tokenid) {
        WeakHashMap<String, String> param = new WeakHashMap<>();
        param.put("name", cardholder_name);
        param.put("description", "Customer added from Android App ");
        param.put("source", tokenid);

        Log.e("Sending data", String.valueOf(param));
        RetrofitInterface apiService = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<CreateCustomerModel> call = apiService.attemptToCreateCustomer(Constants.authant, param);
        call.enqueue(new Callback<CreateCustomerModel>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<CreateCustomerModel> call, retrofit2.Response<CreateCustomerModel> response) {
                if (response != null) {
                    if (response.isSuccessful())
                    {
                        //  Toast.makeText(CreditCardScreen.this, "successCustomer", Toast.LENGTH_SHORT).show();

                        Log.e("tag", "customerid: " + response.body().getId());

                        String customerid = response.body().getId();
                        stripe_customer_id = customerid;


                        Log.e("CreditCardActivity", "onSuccess:stripe Customer Created : " + response.body().getId());

                        updateCustomerId(customerid).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap) {
                                Log.e("CreditCardActivity", "onSuccess: " + hashMap);

                                requestToCharge(response.body().getId(), "");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("CreditCardActivity", "onFailure: " + e);
                                DialogBoxError.showError(CreditCardActivity.this,e.getMessage());
                            }
                        });

                    } else {

                        try {
                            Log.e("tag", "onResponse create customer: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Toast.makeText(Login.this,response.message(), Toast.LENGTH_SHORT).show();
                        // DialogBox.showError(Login.this,response.message());

                    }
                }
                else
                {
                    if (dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    DialogBoxError.showError(CreditCardActivity.this,response.message());

                    // Toast.makeText(CreditCardActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateCustomerModel> call, Throwable t) {
                ///  Log.e(TAG, "onFailure: "+t.getMessage() );
                DialogBoxError.showError(CreditCardActivity.this,t.getMessage());
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }
                //Toast.makeText(CreditCardActivity.this, "Something went wrong" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Task<HashMap> updateCustomerId(String customerid) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("stripe_customer_id", customerid);

        return mFunctions.getHttpsCallable("updateCustomerId").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private void requestToCreateCard(String id, String tokenid) {
        WeakHashMap<String, String> param = new WeakHashMap<>();
        param.put("source", tokenid);

        Log.e("Sending data", String.valueOf(param));
        RetrofitInterface apiService = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<CreateCustomerCardModel> call = apiService.attemptToCreateCard(Constants.authant, id, param);
        call.enqueue(new Callback<CreateCustomerCardModel>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<CreateCustomerCardModel> call, retrofit2.Response<CreateCustomerCardModel> response) {
                // progress.cancel();
                if (response != null)
                {
                    if (response.isSuccessful())
                    {

                        Card card = card_input_widget.getCard();

                        if (card == null) {
                            Toast.makeText(CreditCardActivity.this, "Please enter your card no.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Stripe stripe = new Stripe(CreditCardActivity.this, Constants.publishablekey);
                        stripe.createToken(
                                card,
                                new ApiResultCallback<Token>() {
                                    public void onSuccess(Token token) {

                                        Log.e("CreditCardActivity", "onSuccess:stripe Card Created : " + response.body().getName());

                                        cardID = response.body().getId();

                                        requestToCharge(stripe_customer_id, cardID);

                                    }

                                    public void onError(Exception error) {
                                        // Show localized error message
//                                        if (progress.isShowing())
//                                        progress.cancel();
                                        //     showRemoveLoader();
                                        DialogBoxError.showError(CreditCardActivity.this,error.getMessage());
                                        /*Toast.makeText(CreditCardActivity.this,
                                                error.getLocalizedMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();*/
                                    }
                                }
                        );

                    }
                    else
                    {

                        try
                        {
                            if (dialog.isShowing())
                            {
                                dialog.dismiss();
                            }

                            String res = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(res);
                            String error = jsonObject.get("error").toString();
                            JSONObject jsonObject1 = new JSONObject(error);
                            String message = jsonObject1.get("message").toString();

                            DialogBoxError.showError(CreditCardActivity.this, message);
                            //Toast.makeText(CreditCardActivity.this, message, Toast.LENGTH_SHORT).show();

                            Log.e("tag", "onResponse create card: " + message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
                else
                {
                    if (dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    DialogBoxError.showError(CreditCardActivity.this, response.message());
                    //Toast.makeText(CreditCardActivity.this, "Response null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateCustomerCardModel> call, Throwable t) {
                ///  Log.e(TAG, "onFailure: "+t.getMessage() );
                DialogBoxError.showError(CreditCardActivity.this,t.getMessage());
                //Toast.makeText(CreditCardActivity.this, "Something went wrong" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void requestToCharge(String cus_id, String id) {

        Double price_ = Double.valueOf(price) * 100;

        Integer dPrice = Integer.valueOf(price_.intValue());

        WeakHashMap<String, Object> param = new WeakHashMap<>();
        // param.put("source",tokenid);
        param.put("amount", String.valueOf(dPrice));
        param.put("currency", "MYR");
        if (cus_id.length() != 0)
            param.put("customer", cus_id);
        if (id.length() != 0)
            param.put("source", id);
        param.put("description", "Payment from Android App");

        Log.e("Sending data", String.valueOf(param));

        RetrofitInterface apiService = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<ChargeModel> call = apiService.attemptToCharge(Constants.authant, param);
        call.enqueue(new Callback<ChargeModel>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<ChargeModel> call, retrofit2.Response<ChargeModel> response) {

                if (response.isSuccessful())
                {
                    Log.e("CreditCardActivity", "onResponse:charge success");

                    txt_payment_status = "success";

                    txt_txn_id = response.body().getBalance_transaction();
                    txt_card_brand = response.body().getPayment_method_details().getCard().getBrand();
                    txt_card_last4 = response.body().getPayment_method_details().getCard().getLast4();
                    name = response.body().getSource().getName().toString();

                    txt_price = price;

                    if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
                    {
                        createStripePayment().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap) {
                                Log.e("CreditCardActivity", "onSuccess: " + hashMap);

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }

                                String response_msg = hashMap.get("response_msg").toString();
                                String message = hashMap.get("message").toString();

                                if (response_msg.equals("success"))
                                {
                                    if (response.body().getStatus().equals("succeeded"))
                                    {

                                        Vibration.vibration(CreditCardActivity.this);

                                        Constants.playSuccesSoundEffect(CreditCardActivity.this, Constants.click);

                                        DialogBox.successfullDialog(CreditCardActivity.this, txt_txn_id);

                                    }
                                    else
                                    {

                                        Vibration.vibration(CreditCardActivity.this);

                                        Constants.playSuccesSoundEffect(CreditCardActivity.this, Constants.click);

                                        DialogBox.failedDialog(CreditCardActivity.this);

                                    }
                                }
                                else if (response_msg.equals(Constants.unauthorized))
                                {
                                    DialogBoxError.showDialogBlockUser(CreditCardActivity.this,message,sharedPreferenceUtil);
                                }
                                else if (response_msg.equals(Constants.under_maintenance))
                                {
                                    DialogBoxError.showError(CreditCardActivity.this,message);
                                }
                                else
                                {
                                    DialogBoxError.showError(CreditCardActivity.this,message);
                                }




                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("CreditCardActivity", "onFailure: " + e);
                                DialogBoxError.showError(CreditCardActivity.this,e.getMessage());
                            }
                        });
                    }
                    else
                    {
                        DialogBoxError.showInternetDialog(CreditCardActivity.this);
                    }


                } else {
                    Log.e("CreditCardActivity", "onResponse:charge failure");


                    try {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        String res = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(res);
                        String error = jsonObject.get("error").toString();
                        JSONObject jsonObject1 = new JSONObject(error);
                        String message = jsonObject1.get("message").toString();

                        //Toast.makeText(CreditCardActivity.this, message, Toast.LENGTH_SHORT).show();
                        DialogBoxError.showError(CreditCardActivity.this,message);
                        Log.e("tag", "onResponse charge: " + message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChargeModel> call, Throwable t) {
                Log.e("CreditCardActivity", "onFailure: " + t.getMessage());
                DialogBoxError.showError(CreditCardActivity.this,t.getMessage());
                //Toast.makeText(CreditCardActivity.this, "Something went wrong" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private Task<HashMap> createStripePayment() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("txn_id", txt_txn_id);
        data.put("payment_status", txt_payment_status);
        data.put("price", Double.valueOf(txt_price));
        data.put("customer_id", "");
        data.put("currency", "");
        data.put("receipt_email", "");
        data.put("card_last4", txt_card_last4);
        data.put("card_id", "");
        data.put("card_funding", "");
        data.put("card_holder_name", name);
        data.put("pay_datetime", "");
        data.put("card_funding", "");
        data.put("card_brand", txt_card_brand);
        data.put("failure_code", "");
        data.put("failure_message", "");


        return mFunctions.getHttpsCallable("createStripePayment").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });

    }

    //fetch all card list
    private void requestToFetchCardList()
    {
        RetrofitInterface apiService = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<FetchCardListModel> call = apiService.attemptToFetchCards(Constants.authant, stripe_customer_id);
        call.enqueue(new Callback<FetchCardListModel>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<FetchCardListModel> call, retrofit2.Response<FetchCardListModel> response) {
//                progress.cancel();
//                showRemoveLoader();

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                Log.e("CreditCardActivity", "onResponseresponse: " + String.valueOf(response));
//                Log.e("CreditCardActivity", "onResponseresponse: " + String.valueOf(response.body().getData()));

                if (response != null)
                {
                    ll_main.setVisibility(View.VISIBLE);

                    if (response.isSuccessful())
                    {

                        if (response.body().getData().size() == 0)
                        {
                            isNewCard = true;

                            oldcards.setVisibility(View.GONE);
                            ll_newcards.setVisibility(View.VISIBLE);
                            Log.e("CreditCardActivity", "onResponse:empty card ");

                            DialogBoxError.showKeyboard(CreditCardActivity.this,edt_cardNumber);

                            tv_payNow.setBackgroundColor(getResources().getColor(R.color.mediumGrey));
                            tv_payNow.setBackgroundColor(getResources().getColor(R.color.mediumGrey));
                            tv_payNow.setEnabled(false);
                            tv_payNow.setFocusable(false);
                            tv_payNow.setClickable(false);
                        }
                        else
                        {

                            if (response.body().getData().size() > 0)
                            {
                                String prc= DoubleDecimal.twoPoints(price);
                                /*String str="<b>PAY NOW </b> RM <b>"+DoubleDecimal.twoPointsComma(prc)+"</b>";

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    tv_payNow.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    tv_payNow.setText(Html.fromHtml(str));
                                }*/

                                //tv_payNow.setBackgroundColor(getResources().getColor(R.color.green));
                                tv_payNow.setBackgroundColor(getResources().getColor(R.color.mediumGrey));
                                tv_payNow.setEnabled(false);
                                tv_payNow.setFocusable(false);
                                tv_payNow.setClickable(false);

                                //tv_payNow.setText("PAY NOW RM " + DoubleDecimal.twoPoints(price));
                                isNewCard = false;
                                //hide keyboard
                                DialogBoxError.hideKeyboard(CreditCardActivity.this);

                                getCardList = response.body().getData();
                                //tv_creditDebit.setText("Saved Cards");
                                oldcards.setVisibility(View.VISIBLE);
                                ll_newcards.setVisibility(View.GONE);

                                if (getCardList.size() == 0) {
                                    ViewGroup.LayoutParams params = oldcardlist.getLayoutParams();
                                    params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                                    oldcardlist.setLayoutParams(params);

                                    Toast.makeText(CreditCardActivity.this, "No Cards", Toast.LENGTH_SHORT).show();

                                    Log.e("CreditCardActivity", "onResponse:empty card ");

                                } else {
                                    ViewGroup.LayoutParams params = oldcardlist.getLayoutParams();
                                    params.height = RelativeLayout.LayoutParams.WRAP_CONTENT * getCardList.size();
                                    oldcardlist.setLayoutParams(params);
                                }

                                Log.e("CreditCardActivity", "onResponse:getCardList " + getCardList);

                                cardListAdapter = new CardListAdapter(CreditCardActivity.this, getCardList, CreditCardActivity.this);
                                oldcardlist.setAdapter(cardListAdapter);

                            } else {

                                tv_creditDebit.setText(getResources().getString(R.string.credit_card_debit_card));
                                oldcards.setVisibility(View.GONE);
                                ll_newcards.setVisibility(View.VISIBLE);

                            }

                        }

                    }
                    else
                    {
                        tv_creditDebit.setText(getResources().getString(R.string.credit_card_debit_card));
                        oldcards.setVisibility(View.GONE);
                        ll_newcards.setVisibility(View.VISIBLE);
                        Log.e("CreditCardActivity", "onResponse: " + response.message());
                        // Toast.makeText(Login.this,response.message(), Toast.LENGTH_SHORT).show();
                        // DialogBox.showError(Login.this,response.message());

                    }

                }
                else
                {
                    DialogBoxError.showError(CreditCardActivity.this,response.message());
                    //Toast.makeText(CreditCardActivity.this, "Response null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FetchCardListModel> call, Throwable t) {
                ///  Log.e(TAG, "onFailure: "+t.getMessage() );
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                DialogBoxError.showError(CreditCardActivity.this,t.getMessage());
                //Toast.makeText(CreditCardActivity.this, "Something went wrong" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public static ArrayList<String> listOfPattern()
    {
        ArrayList<String> listOfPattern=new ArrayList<String>();

        String ptVisa = "^4[0-9]$";

        listOfPattern.add(ptVisa);

        String ptMasterCard = "^5[1-5]$";

        listOfPattern.add(ptMasterCard);

        return listOfPattern;
    }

    @Override
    public void clearEditTexts(String str, int position) {
        Log.e("tag", "clearEditTexts: " + str);

        if (progressPayNow.isShown())
        {
            tv_payNow.setBackgroundColor(getResources().getColor(R.color.green));
            tv_payNow.setEnabled(false);
            tv_payNow.setFocusable(false);
            tv_payNow.setClickable(false);
        }
        else
        {
            cardID = str;
            tv_payNow.setBackgroundColor(getResources().getColor(R.color.green));
            tv_payNow.setEnabled(true);
            tv_payNow.setFocusable(true);
            tv_payNow.setClickable(true);
        }


    }

    @Override
    public void deleteCard(String cardid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreditCardActivity.this);
        builder.setTitle("Message");
        builder.setMessage("Are you sure you want to delete card. ");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
                {
                    requestToDeleteCard(cardid);
                }
                else
                {
                    DialogBoxError.showInternetDialog((CreditCardActivity.this));
                }




            }
        });
        builder.show();
    }

    private void requestToDeleteCard(String cardId) {

        RetrofitInterface apiService = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<DeleteCard> call = apiService.attemptToDelete(Constants.authant, stripe_customer_id, cardId);
        call.enqueue(new Callback<DeleteCard>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<DeleteCard> call, retrofit2.Response<DeleteCard> response) {

                if (response != null) {
                    if (response.isSuccessful()) {
                        //  Toast.makeText(CreditCardScreen.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        getCardList.clear();

                        if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
                        {
                            requestToFetchCardList();
                        }
                        else
                        {
                            DialogBoxError.showInternetDialog(CreditCardActivity.this);
                        }


                    } else {
                        Log.e("tag", "onResponse delete card: " + response.message());
                    }

                } else {
                    Toast.makeText(CreditCardActivity.this, "Response null", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<DeleteCard> call, Throwable t) {
                ///  Log.e(TAG, "onFailure: "+t.getMessage() );
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                DialogBoxError.showError(CreditCardActivity.this,t.getMessage());
                //Toast.makeText(CreditCardActivity.this, "Something went wrong" + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void doPayNow()
    {
        String prc=DoubleDecimal.twoPoints(price);
        /*String str="<b>PAY NOW </b> RM <b>"+DoubleDecimal.twoPointsComma(prc)+"</b>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_payNow.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv_payNow.setText(Html.fromHtml(str));
        }*/
        tv_payNow.setBackgroundColor(getResources().getColor(R.color.green));
        tv_payNow.setEnabled(true);
        tv_payNow.setFocusable(true);
        tv_payNow.setClickable(true);
    }

    private void payNow()
    {
        String str="<b>PAY NOW </b>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_payNow.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv_payNow.setText(Html.fromHtml(str));
        }
        tv_payNow.setBackgroundColor(getResources().getColor(R.color.mediumGrey));
    }

    ///fpx payments
    private Task<HashMap> createFpxPaymentIntent() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("amount", fpx_payment_amt);//
        data.put("is_test",Constants.is_test);

        return mFunctions.getHttpsCallable("createFpxPaymentIntent").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private void launchAddPaymentMethod() {

        new AddPaymentMethodActivityStarter(this)
                .startForResult(new AddPaymentMethodActivityStarter.Args.Builder()
                        .setPaymentMethodType(PaymentMethod.Type.Fpx)
                        .build()
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final AddPaymentMethodActivityStarter.Result result = AddPaymentMethodActivityStarter.Result.fromIntent(data);
        if (result != null)
        {
            onPaymentMethodResult(result.getPaymentMethod());
        }


        stripe.onPaymentResult(requestCode, data,
                new ApiResultCallback<PaymentIntentResult>() {
                    @Override
                    public void onSuccess(@NonNull PaymentIntentResult result) {
                        // If authentication succeeded, the PaymentIntent will have
                        // user actions resolved; otherwise, handle the PaymentIntent
                        // status as appropriate (e.g. the customer may need to choose
                        // a new payment method)

                        Log.e("Response",""+result.getIntent());

                        long amount = result.getIntent().getAmount();
                        long canceledAt = result.getIntent().getCanceledAt();

                        //PaymentIntent.CancellationReason cancellationReason = result.getIntent().getCancellationReason(); //always null

                        String captureMethod = result.getIntent().getCaptureMethod();
                        String clientSecret = result.getIntent().getClientSecret();
                        String confirmationMethod = result.getIntent().getConfirmationMethod();
                        long created = result.getIntent().getCreated();
                        String currency = result.getIntent().getCurrency();

                        //String description = result.getIntent().getDescription();//always null

                        String id = result.getIntent().getId();

                        boolean isLiveMode = result.getIntent().isLiveMode();

                        String failure_code="",failure_msg="";
                        PaymentIntent.Error lastPaymentError = result.getIntent().getLastPaymentError(); // null in case of success
                        if (lastPaymentError!=null)
                        {
                            failure_code = lastPaymentError.getCode();
                            failure_msg = lastPaymentError.getMessage();
                        }

                        //String nextAction = ""+result.getIntent().getNextAction();//need to conever map //always null
                        //String NextActionData//always null
                        //String NextActionType = ""+result.getIntent().getNextActionType();//remove//always null
                        //String objectType = //not fetching
                        //PaymentMethod paymentMethod = result.getIntent().getPaymentMethod();//null in case of fail
                        //String paymentMethodId = result.getIntent().getPaymentMethodId();//null in case of fail

                        String payment_method=result.getIntent().getPaymentMethodTypes().get(0);
                        List<String> paymentMethodTypes = result.getIntent().getPaymentMethodTypes();

                        //String receiptEmail = result.getIntent().getReceiptEmail();////always null
                        //String setupFutureUsage = //not fetching and always null
                        //PaymentIntent.Shipping shipping = result.getIntent().getShipping();////always null in case of fail

                        //StripeIntent.Status status_ = result.getIntent().getStatus();//code="requires_payment_method" name="RequiresPaymentMethod" in case of fail, and  code="Succeeded" name="Succeeded" in case of success

                        // Uri redirectUrl = result.getIntent().getRedirectUrl();//always null in case of fail


                        JSONObject jsonObject = new JSONObject();
                        try
                        {
                            jsonObject.put("amount",amount);
                            jsonObject.put("canceledAt",canceledAt);
                            jsonObject.put("captureMethod",captureMethod);
                            jsonObject.put("clientSecret",clientSecret);
                            jsonObject.put("confirmationMethod",confirmationMethod);
                            jsonObject.put("created",created);
                            jsonObject.put("currency",currency);
                            jsonObject.put("id",id);
                            jsonObject.put("isLiveMode",isLiveMode);

                            JSONArray jsonArray = new JSONArray();
                            for (int i=0; i<paymentMethodTypes.size(); i++)
                            {
                                jsonArray.put(i,paymentMethodTypes.get(i));
                            }

                            jsonObject.put("paymentMethodTypes",jsonArray);
                            jsonObject.put("status",result.getIntent().getStatus());
                            //jsonObject.put("lastPaymentError",lastPaymentError);

                            Log.e("jsonObject",""+jsonObject);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.e("ErrorMsg", e.getMessage());
                        }


                        final PaymentIntent.Status status = result.getIntent().getStatus();

                        if (PaymentIntent.Status.RequiresPaymentMethod == status)
                        {
                            Log.e("Response", "RequiresPaymentMethod");
                            Vibration.vibration(CreditCardActivity.this);

                            Constants.playSuccesSoundEffect(CreditCardActivity.this, Constants.click);

                            DialogBox.failedDialog(CreditCardActivity.this);

                            /*createFpxPayment(id,"success",payment_method,currency,
                                    ""+created,failure_code,failure_msg,jsonObject);*/
                            // attempt authentication again or ask for a new Payment Method
                        }
                        else if (PaymentIntent.Status.RequiresConfirmation == status)
                        {
                            Log.e("Response", "RequiresConfirmation");
                            // handle confirming the PaymentIntent again on the server
                        }
                        else if (PaymentIntent.Status.Succeeded == status)
                        {
                            Log.e("Response", "Succeeded");

                            if (DialogBoxError.checkInternetConnection(CreditCardActivity.this))
                            {

                                createFpxPayment(id,"success",payment_method,currency,
                                        ""+created,failure_code,failure_msg,jsonObject);
                            }
                            else
                            {
                                DialogBoxError.showInternetDialog(CreditCardActivity.this);
                            }



                        }
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        // handle error
                        Log.e("Response", e.getMessage());
                        DialogBoxError.showDialog(CreditCardActivity.this, e.getMessage());
                        progressPayNow.setVisibility(View.GONE);
                        tv_payNow.setText(getResources().getString(R.string.pay_now));

                    }
                });

    }

    private void onPaymentMethodResult(@NonNull PaymentMethod paymentMethod) {
        final String fpxBankCode = Objects.requireNonNull(paymentMethod.fpx).bank;
        final String resultMessage = "Created Payment Method\n" +
                "\nType: " + paymentMethod.type +
                "\nId: " + paymentMethod.id +
                "\nBank code: " + fpxBankCode;

        fpx.setVisibility(View.VISIBLE);
        fpx.setText(fpxBankCode.toUpperCase());
        progressPayNow.setVisibility(View.VISIBLE);
        tv_payNow.setText("");
        tv_payNow.setBackgroundColor(getResources().getColor(R.color.green));


        stripe.confirmPayment(this,
                ConfirmPaymentIntentParams.createWithPaymentMethodId(
                        paymentMethod.id,
                        client_secret,"okmart-payments://stripe-redirect"
                ));


    }
    private void createFpxPayment(String id, String payment_status,String payment_method,
                                  String currency,
                                  String pay_datetime,
                                  String failure_code,
                                  String failure_message,
                                  JSONObject jsonObject)
    {

        createStripePayment(id,"success",payment_method,currency,""+pay_datetime,failure_code,failure_message,jsonObject).addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e("CreditCardActivity", "onSuccess: " + hashMap);


                if (hashMap.get("response_msg").toString().equals("success"))
                {

                    Vibration.vibration(CreditCardActivity.this);

                    Constants.playSuccesSoundEffect(CreditCardActivity.this, Constants.click);

                    DialogBox.successfullDialog(CreditCardActivity.this, id);

                }
                else
                {

                    Vibration.vibration(CreditCardActivity.this);

                    Constants.playSuccesSoundEffect(CreditCardActivity.this, Constants.click);

                    DialogBox.failedDialog(CreditCardActivity.this);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("CreditCardActivity", "onFailure: " + e);
                DialogBoxError.showError(CreditCardActivity.this,e.getMessage());
            }
        });
    }

    private Task<HashMap> createStripePayment(String id, String payment_status,String payment_method,
                                              String currency,
                                              String pay_datetime,
                                              String failure_code,
                                              String failure_message,
                                              JSONObject jsonObject) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("txn_id", id);
        data.put("payment_status", payment_status);
        data.put("payment_method", "fpx");
        data.put("price", original_amt);
        data.put("customer_id", "");
        data.put("currency", currency);
        data.put("receipt_email", "");
        data.put("card_last4", "");
        data.put("card_id", "");
        data.put("card_funding", "");
        data.put("card_holder_name", "");
        data.put("pay_datetime", pay_datetime);
        data.put("card_funding", "");
        data.put("card_brand", "");
        data.put("failure_code", failure_code);
        data.put("failure_message", failure_message);
        data.put("payment_response",jsonObject);


        return mFunctions.getHttpsCallable("createStripePayment").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });

    }


    private Task<HashMap> getPublicSecretKey()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("contact",Constants.fpxdetails);

        return mFunctions.getHttpsCallable("getContactDetails").call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (HashMap)task.getResult().getData();
                    }
                });
    }

    private void getUserWallet()
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
                    tv_walletPoint.setText(DoubleDecimal.twoPointsComma(hashMap.get("walletPoint").toString()));
//                    walletPoint.setText(DoubleDecimal.twoPoints(hashMap.get("walletPoint").toString()) + " PT");

                }
                else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(CreditCardActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(CreditCardActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(CreditCardActivity.this,message);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(progressBarWalletPoints.isShown()) {

                    progressBarWalletPoints.setVisibility(View.GONE);
                }

                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(CreditCardActivity.this, getString(R.string.something_went_wrong));
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
    public void onBackPressed() {
        if (progressPayNow.isShown())
        {
            Log.e(TAG, "progressPayNow: "+progressPayNow.isShown() );
        }
        else
        {
            DialogBoxError.hideKeyboard(CreditCardActivity.this);
            finish();
        }
    }
}