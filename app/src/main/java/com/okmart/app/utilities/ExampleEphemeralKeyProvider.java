package com.okmart.app.utilities;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.work.impl.Schedulers;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExampleEphemeralKeyProvider implements EphemeralKeyProvider {
//    private final BackendApi backendApi =
//            RetrofitFactory.instance.create(BackendApi.class);
//    private final CompositeDisposable compositeDisposable =
//            new CompositeDisposable();

    private Context context;
    private FirebaseFunctions mFunctions;
    private String stripe_customer_id;


    public ExampleEphemeralKeyProvider(Context context) {
        this.context = context;
    }

    @Override
    public void createEphemeralKey(
            @NonNull @Size(min = 4) String apiVersion,
            @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {

        mFunctions = FirebaseFunctions.getInstance();


        userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e("CreditCardActivity", "onSuccess: " + hashMap);

                HashMap hm = (HashMap) hashMap.get("userData");
                if (hm.get("stripe_customer_id")!=null)
                {
                    stripe_customer_id = (String) hm.get("stripe_customer_id").toString();
                }


                createFpxEphemeralKey().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e("Response", "onSuccess: " + hashMap);

                        HashMap data = (HashMap) hashMap.get("data");
                        final String rawKey = data.get("id").toString();
                        keyUpdateListener.onKeyUpdate(rawKey);

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("Response", "onFailure: " + e);
                        //DialogBoxError.showError(context, e.getMessage());
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("CreditCardActivity", "onFailure: " + e);
                DialogBoxError.showError(context,e.getMessage());
            }
        });

    }

    private Task<HashMap> createFpxEphemeralKey() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("customerid", stripe_customer_id);
        data.put("is_test",Constants.is_test);


        return mFunctions.getHttpsCallable("createFpxEphemeralKey").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
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