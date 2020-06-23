package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.ExampleEphemeralKeyProvider;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.view.AddPaymentMethodActivityStarter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class FpxPaymentActivity extends AppCompatActivity {

    private Context context = FpxPaymentActivity.this;
    private String TAG = FpxPaymentActivity.class.getSimpleName();
    private FirebaseFunctions mFunctions;
    private String client_secret;
    private Stripe stripe;

    private PaymentSession paymentSession;
    private PaymentSession.PaymentSessionListener paymentSessionListener;

    private TextView payment_method_result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpx_payment);

        payment_method_result = findViewById(R.id.payment_method_result);
        mFunctions = FirebaseFunctions.getInstance();

        PaymentConfiguration.init(FpxPaymentActivity.this, Constants.publishablekey);

        CustomerSession.initCustomerSession(
                this,
                new ExampleEphemeralKeyProvider(context)
        );

        stripe = new Stripe(FpxPaymentActivity.this, Constants.publishablekey);


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
                    DialogBoxError.showError(context, e.getMessage());
            }
        });

//        paymentSession = new PaymentSession(
//                this,
//                new PaymentSessionConfig.Builder()
//                        // Optionally specify the `PaymentMethod.Type`
//                        // values to use. Default is `PaymentMethod.Type.Card`.
//                        .setPaymentMethodTypes(Arrays.asList(
//                                PaymentMethod.Type.Fpx,
//                                PaymentMethod.Type.Card
//                        ))
//                        .setShippingMethodsRequired(false)
//                        .build()
//        );
//        paymentSession.init(paymentSessionListener);


        TextView btn_select_payment_method = findViewById(R.id.btn_select_payment_method);
        btn_select_payment_method.setOnClickListener(view ->
                        launchAddPaymentMethod()
                );

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

        final AddPaymentMethodActivityStarter.Result     result =
                AddPaymentMethodActivityStarter.Result.fromIntent(data);
        if (result != null) {
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

                        final PaymentIntent.Status status = result.getIntent().getStatus();
                        if (PaymentIntent.Status.RequiresPaymentMethod == status) {
                            Log.e("Response", "RequiresPaymentMethod");
                            // attempt authentication again or ask for a new Payment Method
                        } else if (PaymentIntent.Status.RequiresConfirmation == status) {
                            Log.e("Response", "RequiresConfirmation");
                            // handle confirming the PaymentIntent again on the server
                        } else if (PaymentIntent.Status.Succeeded == status) {
                            Log.e("Response", "Succeeded");
                            // capture succeeded
                        }
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        // handle error
                        Log.e("Response", e.getMessage());
                    }
                });

    }

    private void onPaymentMethodResult(@NonNull PaymentMethod paymentMethod) {
        final String fpxBankCode = Objects.requireNonNull(paymentMethod.fpx).bank;
        final String resultMessage = "Created Payment Method\n" +
                "\nType: " + paymentMethod.type +
                "\nId: " + paymentMethod.id +
                "\nBank code: " + fpxBankCode;

        payment_method_result.setText(resultMessage);

        stripe.confirmPayment(this,
                ConfirmPaymentIntentParams.createWithPaymentMethodId(
                        paymentMethod.id,
                        client_secret
                ));

    }

    private Task<HashMap> createFpxPaymentIntent() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 1000);

        return mFunctions.getHttpsCallable("createFpxPaymentIntent").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }
}