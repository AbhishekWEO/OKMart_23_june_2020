package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.adapters.FlashAdapter;
import com.okmart.app.adapters.FlashSeeMoreAdapter;
import com.okmart.app.model.ProductModel;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlashSeeMoreActivity extends AppCompatActivity {

    private RecyclerView rv_flash;
    private List<ProductModel> flashDataList=new ArrayList<>();
    private FlashSeeMoreAdapter flashAdapter;//flashSeeMoreAdapter;
    //private FlashAdapter flashAdapter;
    private FirebaseFirestore db;
    private String TAG = FlashSeeMoreActivity.class.getSimpleName();
    private String end_datetime;
    private ImageView img_back;
    private SwipeRefreshLayout swifeRefreshLayout;
    private ProgressBar progressBar;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_see_more);

        getSupportActionBar().hide();

        sharedPreferenceUtil = new SharedPreferenceUtil(FlashSeeMoreActivity.this);

        progressBar = findViewById(R.id.progressBar);

        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        swifeRefreshLayout = findViewById(R.id.swifeRefreshLayout);
        swifeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                swifeRefreshLayout.setRefreshing(false);
                rv_flash.setVisibility(View.GONE);

                if (this!=null)
                {
                    if (DialogBoxError.checkInternetConnection(FlashSeeMoreActivity.this))
                    {
                        if (flashAdapter!=null)
                        {
                            flashAdapter.closeTimer();
                        }

                        flashDataList.clear();
                        flash();
                    }
                    else
                    {
                        DialogBoxError.showInternetDialog(FlashSeeMoreActivity.this);
                    }
                }
            }
        });

        rv_flash = findViewById(R.id.rv_flash);
        rv_flash.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        flash();
    }


    public void flash() {
        if (this!=null)
        {
            if (DialogBoxError.checkInternetConnection(this))
            {

                productListRetrieveFlash().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e(TAG, "onSuccess: " + hashMap);

                        progressBar.setVisibility(View.GONE);

                        rv_flash.setVisibility(View.VISIBLE);

                        if (swifeRefreshLayout.isRefreshing())
                        {
                            swifeRefreshLayout.setRefreshing(false);
                        }

                        flashDataList.clear();

                        String response_msg = hashMap.get("response_msg").toString();
                        String message = hashMap.get("message").toString();

                        if (response_msg.equals("success"))
                        {
                            HashMap hashmap_current_time = (HashMap) hashMap.get("current_time");

                            String current_time = hashmap_current_time.get("_seconds").toString();

                            ArrayList arrayList = (ArrayList) hashMap.get("productData");
                            int size = arrayList.size();

                            for(int i = 0 ; i<size ; i++)
                            {
                                HashMap data = (HashMap) arrayList.get(i);
                                ProductModel flashModel = new ProductModel();

                                flashModel.setProduct_name(CapitalUtils.capitalize(data.get("product_name").toString()));
                                flashModel.setProduct_ref(data.get("product_ref").toString());
                                flashModel.setS_price(data.get("s_price").toString());
                                flashModel.setCurrent_price(data.get("current_price").toString());
                                flashModel.setProduct_image(data.get("product_image").toString());
                                flashModel.setFeatures(data.get("features").toString());
                                flashModel.setProduct_status(data.get("product_status").toString());
                                flashModel.setQuantity(Integer.parseInt(data.get("quantity").toString()));

                                flashModel.setFlash_down_second(data.get("flash_down_second").toString());
                                flashModel.setDown_per_price(data.get("down_per_price").toString());
                                flashModel.setMin_price(data.get("min_price").toString());

                                HashMap hashmap_flash_sale_order_time = (HashMap) data.get("flash_sale_order_time");
                                flashModel.setFlash_sale_order_time(hashmap_flash_sale_order_time.get("_seconds").toString());

                                HashMap end_selling_time = (HashMap) data.get("end_selling_time");
                                end_datetime = end_selling_time.get("_seconds").toString();
                                flashModel.set_seconds(end_datetime);
                                flashModel.setProduct_status_number(4);

                                flashModel.setIs_resetOrderTime("false");

                                flashDataList.add(flashModel);

                            }

//                        Comparator<FlashModel> comparator = Comparator.comparing(e -> e.getProduct_status_number());
//                        productDataList.sort(comparator.reversed());

                            //flashAdapter = new FlashAdapter(FlashSeeMoreActivity.this, flashDataList, current_time);
                            flashAdapter = new FlashSeeMoreAdapter(FlashSeeMoreActivity.this, flashDataList, current_time);

                            rv_flash.setAdapter(flashAdapter);
                            rv_flash.setVisibility(View.VISIBLE);

                            if (this!=null)
                            {
                                if (DialogBoxError.checkInternetConnection(FlashSeeMoreActivity.this))
                                {
                                    flashObservers();
                                }
                                else
                                {
                                    DialogBoxError.showInternetDialog(FlashSeeMoreActivity.this);
                                }
                            }
                        }
                        else if (response_msg.equals(Constants.unauthorized))
                        {
                            DialogBoxError.showDialogBlockUser(FlashSeeMoreActivity.this,message,sharedPreferenceUtil);
                        }
                        else if (response_msg.equals(Constants.under_maintenance))
                        {
                            DialogBoxError.showError(FlashSeeMoreActivity.this,message);
                        }
                        else
                        {
                            DialogBoxError.showError(FlashSeeMoreActivity.this,message);
                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "onFailure: " + e);
                    }
                });
            }
            else
            {
                DialogBoxError.showInternetDialog(this);
            }
        }
    }

    ListenerRegistration registration;

    private void flashObservers()
    {

        db = null;
        db = FirebaseFirestore.getInstance();

        Query query = db.collection("products").whereEqualTo("is_active",true);

        registration =  query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null)
                {
                    Log.e(TAG, "listen:error", e);
                    return;
                }
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges())
                {
                    switch (documentChange.getType())
                    {
                        case MODIFIED:
                            Log.e(TAG, "onEventObserver: " + documentChange.getDocument().getData());

                            HashMap hashMap = (HashMap) documentChange.getDocument().getData();
                            String product_status_new = hashMap.get("product_status").toString();
                            String current_price_new = hashMap.get("current_price").toString();
                            String product_ref_new = documentChange.getDocument().getId();
                            Log.e(TAG, "product_ref_new: " + product_ref_new);

                            String flash_sale_order_time_new= hashMap.get("flash_sale_order_time").toString();

                            String is_flash = hashMap.get("is_flash").toString();
                            String is_featured = hashMap.get("is_featured").toString();
                            String new_quantity = hashMap.get("quantity").toString();

                            Log.e(TAG, "new_quantity: " + new_quantity);

                            if (is_flash.equalsIgnoreCase("true"))
                            {
                                for (int i = 0; i < flashDataList.size(); i++)
                                {

                                    String product_ref = flashDataList.get(i).getProduct_ref();
                                    String product_status = flashDataList.get(i).getProduct_status();
                                    String current_price = flashDataList.get(i).getCurrent_price();
                                    String flash_sale_order_time = flashDataList.get(i).getFlash_sale_order_time();

                                    String quantity=""+flashDataList.get(i).getQuantity();

                                    Log.e(TAG, "onEventproductModel: " + product_ref);
                                    if (product_ref_new.equals(product_ref))
                                    {
                                        Log.e(TAG, "onEvent:Matched");

                                        //
                                        //Timestamp(seconds=1592289323, nanoseconds=415000000)
                                        String rep1=flash_sale_order_time_new.replace("Timestamp(seconds=","");

                                        String[] split=rep1.split(",");
                                        String new_flash_sale_order_time=split[0];
                                        //

                                        if (!(new_flash_sale_order_time.equals(flash_sale_order_time)))
                                        {
                                            if (flashAdapter!=null)
                                            {
                                                flashAdapter.closeTimer();
                                            }
                                            flash();

                                            break;
                                        }

                                        if (!(product_status_new.equals(product_status)))
                                        {

                                            if (is_flash.equalsIgnoreCase("true"))
                                            {
                                                if (flashAdapter!=null)
                                                {
                                                    flashAdapter.closeTimer();
                                                }
                                                flash();
                                            }

//                                                productDataList.clear();
//
//                                                if (productAdapter!=null)
//                                                {
//                                                    productAdapter.closeTimer();
//                                                }
//
//                                                products(category_ref);

//                                                if (is_featured.equalsIgnoreCase("true"))
//                                                {
//                                                    if (featuredAdapter!=null)
//                                                    {
//                                                        featuredAdapter.closeTimer();
//                                                    }
//                                                    featured(category_ref);
//                                                }

                                            break;
                                        }
//                                            else if (!(new_quantity.equals(quantity)))
//                                            {
//                                                int newQuantity = Integer.parseInt(new_quantity);
//                                                int position=i;
//
////                                                Log.e("ProductSold","product_ref:- "+product_ref_new+" ,newQuantity:- "+newQuantity);
//
//                                                productAdapter.productQuantity(position,newQuantity);
//
//                                            }
//
//                                            if (!(current_price.equals(current_price_new)))
//                                            {
//
//                                                int position = i;
//                                                productAdapter.currentPrice(position, current_price_new);
//
//                                            }
//                                            break;
                                    }


                                }
                            }

                            break;
                    }
                }
            }
        });
    }


    private void closeListner() {
        registration.remove();
    }


    private Task<HashMap> productListRetrieveFlash() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("product_type", "flash_sale");

        return mFunctions.getHttpsCallable("productListRetrieve").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

}
