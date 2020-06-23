package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.okmart.app.BaseActivity;
import com.okmart.app.R;
import com.okmart.app.base_fragments.HomeFragment;
import com.okmart.app.base_fragments.NotificationsFragment;
import com.okmart.app.base_fragments.OffersFragment;
import com.okmart.app.base_fragments.SettingsFragment;
import com.okmart.app.base_fragments.WalletFragment;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBox;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.FragmentUtils;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private Context context = DashboardActivity.this;
    private String active_fragment = "";
    private String type = "home";
    private String my_offer_screen="active";
    public static BottomNavigationView navigation;
    public static LinearLayout shadow;
    private FirebaseFunctions mFunctions;
    private String TAG = DashboardActivity.class.getSimpleName();
    private String total_count;
    private FirebaseFirestore db;
    private int notification_count=1;
    private static boolean isGetUserDetail=false;
    public static String txn_id="";
    private String status_ref="",my_wallet_screen="";
    private ImageView border1, border2, border3, border4, border5;
    private SharedPreferenceUtil sharedPreferenceUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().hide();

        sharedPreferenceUtil = new SharedPreferenceUtil(DashboardActivity.this);

        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        border1 = findViewById(R.id.border1);
        border2 = findViewById(R.id.border2);
        border3 = findViewById(R.id.border3);
        border4 = findViewById(R.id.border4);
        border5 = findViewById(R.id.border5);

        shadow = findViewById(R.id.shadow);
        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setItemIconTintList(null);

//        navigation.getOrCreateBadge(R.id.action_notification).setNumber();

        //DialogBox.feedbackDialog(DashboardActivity.this);

        if (DialogBoxError.checkInternetConnection(DashboardActivity.this))
        {
            getUserToken();

            getBadgeCount();

            getUserDeatilsCongratulations();

//            getOrdersForFdbk();
        }
        else
        {
            DialogBoxError.showInternetDialog(DashboardActivity.this);
        }


        Bundle extras = getIntent().getExtras();

        if(!(extras == null))
        {
            if(getIntent().getStringExtra("product_ref")!=null) {
                Fragment fragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("product_ref",getIntent().getStringExtra("product_ref"));
                fragment.setArguments(bundle);
                FragmentUtils.addFragmentsInBarContainer(fragment, getSupportFragmentManager());

                active_fragment = "home";
            }
            else if (getIntent().getStringExtra("type").equals("MyOffers"))
            {
                my_offer_screen="active";
                navigation.setSelectedItemId(R.id.action_my_offers);
                /*Fragment fragment = new OffersFragment();
                Bundle bundle = new Bundle();
                bundle.putString("my_offer_screen",my_offer_screen);
                fragment.setArguments(bundle);
                FragmentUtils.addFragmentsInBarContainer(fragment, getSupportFragmentManager());
                active_fragment = "offers";*/
            }
            else if (getIntent().getStringExtra("type").equals("MyOffers_Successfull"))
            {
                if (getIntent().getStringExtra("status_ref")!=null)
                {
                    status_ref=getIntent().getStringExtra("status_ref");
                }

                if (getIntent().getStringExtra("notification_ref")!=null)
                {
                    readNotification(getIntent().getStringExtra("notification_ref"));
                }

                my_offer_screen="successfull";
                navigation.setSelectedItemId(R.id.action_my_offers);
            }
            else if (getIntent().getStringExtra("type").equals("MyOffers_SuccessfullDeep"))
            {
                if (getIntent().getStringExtra("status_ref")!=null)
                {
                    status_ref=getIntent().getStringExtra("status_ref");
                }

                my_offer_screen="successfullDeep";
                navigation.setSelectedItemId(R.id.action_my_offers);
            }
            else if (getIntent().getStringExtra("type").equals("wallet"))
            {
                if (getIntent().getStringExtra("my_wallet_screen")!=null)
                {
                    my_wallet_screen=getIntent().getStringExtra("my_wallet_screen");
                    status_ref=getIntent().getStringExtra("status_ref");
                }

                if (getIntent().getStringExtra("notification_ref")!=null)
                {
                    readNotification(getIntent().getStringExtra("notification_ref"));
                }
                navigation.setSelectedItemId(R.id.action_wallet);
            }
            else if (getIntent().getStringExtra("type").equals("notifications"))
            {
                navigation.setSelectedItemId(R.id.action_notification);
            }
        }
        else
        {
//            FragmentUtils.addFragmentsInBarContainer(new HomeFragment(), getSupportFragmentManager());

            Fragment fragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("product_ref","");
            fragment.setArguments(bundle);
            FragmentUtils.addFragmentsInBarContainer(fragment, getSupportFragmentManager());

            active_fragment = "home";
        }

    }


    boolean doubleBackToExitPressedOnce = false;


    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.action_home:
                if(!(active_fragment.equals("home"))) {

                    FragmentUtils.addFragmentsInBarContainer(new HomeFragment(), getSupportFragmentManager());

                }
                active_fragment = "home";
                border1.setVisibility(View.VISIBLE);
                border2.setVisibility(View.INVISIBLE);
                border3.setVisibility(View.INVISIBLE);
                border4.setVisibility(View.INVISIBLE);
                border5.setVisibility(View.INVISIBLE);
                break;

            case R.id.action_my_offers:
                if(!(active_fragment.equals("offers")))
                {
                    Fragment fragment = new OffersFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("my_offer_screen",my_offer_screen);
                    bundle.putString("status_ref",status_ref);
                    fragment.setArguments(bundle);
                    FragmentUtils.addFragmentsInBarContainer(fragment, getSupportFragmentManager());
                }
                my_offer_screen="active";
                active_fragment = "offers";
                status_ref="";
                border1.setVisibility(View.INVISIBLE);
                border2.setVisibility(View.VISIBLE);
                border3.setVisibility(View.INVISIBLE);
                border4.setVisibility(View.INVISIBLE);
                border5.setVisibility(View.INVISIBLE);
                break;

            case R.id.action_notification:
                if(!(active_fragment.equals("notification"))) {

                    FragmentUtils.addFragmentsInBarContainer(new NotificationsFragment(), getSupportFragmentManager());

                }
                active_fragment = "notification";

                border1.setVisibility(View.INVISIBLE);
                border2.setVisibility(View.INVISIBLE);
                border3.setVisibility(View.VISIBLE);
                border4.setVisibility(View.INVISIBLE);
                border5.setVisibility(View.INVISIBLE);

                break;

            case R.id.action_wallet:
                if(!(active_fragment.equals("wallet")))
                {
                    if (!my_wallet_screen.isEmpty())
                    {
                        Fragment fragment = new WalletFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("my_wallet_screen", my_wallet_screen);
                        bundle.putString("status_ref",status_ref);
                        fragment.setArguments(bundle);
                        FragmentUtils.addFragmentsInBarContainer(fragment,  ((AppCompatActivity) context).getSupportFragmentManager());
                    }
                    else
                    {
                        FragmentUtils.addFragmentsInBarContainer(new WalletFragment(), getSupportFragmentManager());
                    }
                }
                active_fragment = "wallet";
                status_ref="";
                my_wallet_screen="";
                border1.setVisibility(View.INVISIBLE);
                border2.setVisibility(View.INVISIBLE);
                border3.setVisibility(View.INVISIBLE);
                border4.setVisibility(View.VISIBLE);
                border5.setVisibility(View.INVISIBLE);
                break;

            case R.id.action_settings:
                if(!(active_fragment.equals("settings"))) {

                    FragmentUtils.addFragmentsInBarContainer(new SettingsFragment(), getSupportFragmentManager());

                }
                active_fragment = "settings";
                border1.setVisibility(View.INVISIBLE);
                border2.setVisibility(View.INVISIBLE);
                border3.setVisibility(View.INVISIBLE);
                border4.setVisibility(View.INVISIBLE);
                border5.setVisibility(View.VISIBLE);
                break;
        }

        return true;
    }


    public void getUserDeatilsCongratulations() {
        userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    String success = hashMap.get("success").toString();

                    if (success.equals("false"))
                    {
                        String msg = hashMap.get("message").toString();
                        DialogBoxError.showError(DashboardActivity.this,msg);
                    }
                    else
                    {
                        HashMap userData = (HashMap) hashMap.get("userData");
                        String user_ref = userData.get("user_ref").toString();

                        observerCongratulations(user_ref);
                    }
                }

                else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(DashboardActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(DashboardActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(DashboardActivity.this,message);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
            }
        });

    }


    public void getUserDeatils() {
        userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();

                if (response_msg.equals("success"))
                {
                    HashMap userData = (HashMap) hashMap.get("userData");
                    String user_ref = userData.get("user_ref").toString();

                    notification_count=1;
                    observer(user_ref);
//                observerCongratulations(user_ref);

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
            }
        });

    }


    public void observer(String user_ref) {

        db.collection("notification")
                .whereEqualTo("user_ref", user_ref)//"NGSMzFHL5cnVoXgpUZ1r"
                .whereEqualTo("is_clear", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {

                                case ADDED:
                                    isGetUserDetail=true;
                                    //Log.e(TAG, "Dashboard_notification_ac: " + dc.getDocument().getData());
                                    if (notification_count> Integer.parseInt(total_count))
                                    {
                                        Log.e(TAG, "Dashboard_notification_ac: " + String.valueOf(notification_count )+ ", "  + total_count);
                                        getBadgeCount();

                                    }

                                    notification_count++;

                                    break;

                                case MODIFIED:
                                    Log.e(TAG, "Dashboard_notification: " + String.valueOf(notification_count )+ ", "  + total_count);
                                    getBadgeCount();

                                    break;

                                case REMOVED:

                                    Log.e(TAG, "onEvent: " + dc.getDocument().getData());

                                    break;
                            }
                        }
                    }
                });

    }


    public void observerCongratulations(String user_ref) {

        db.collection("my_orders")
                .whereEqualTo("user_ref", user_ref)//"NGSMzFHL5cnVoXgpUZ1r"
                .whereEqualTo("is_winner", true)
                .whereEqualTo("is_won_message_close", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {

                                case ADDED:

                                    HashMap hashMap = (HashMap) dc.getDocument().getData();
                                    Log.e(TAG, "onEvent: " + hashMap);
                                    String order_ref = dc.getDocument().getId();

                                    HashMap product_details = (HashMap)hashMap.get("product_details");

                                    String product_name = CapitalUtils.capitalize(product_details.get("product_name").toString());
                                    String image_thumbnail = product_details.get("image_thumbnail").toString();
                                    String bid_price = DoubleDecimal.twoPointsComma(hashMap.get("bid_price").toString());
                                    String product_ref = hashMap.get("product_ref").toString();

                                    Constants.playSuccesSoundEffect(DashboardActivity.this, Constants.click);

                                    DialogBox.congratulationsDialog(DashboardActivity.this,order_ref, product_name, image_thumbnail, bid_price, product_ref);


                                    updateFeedback(order_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                                        @Override
                                        public void onSuccess(HashMap hashMap)
                                        {
                                            Log.e(TAG, "onSuccess: " + hashMap);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: " + e);
                                            DialogBoxError.showError(DashboardActivity.this, e.getMessage());
                                        }
                                    });

                                    break;

                                case MODIFIED:
                                    Log.e(TAG, "onEvent: " + dc.getDocument().getData());

                                    break;

                                case REMOVED:

                                    Log.e(TAG, "onEvent: " + dc.getDocument().getData());

                                    break;
                            }
                        }
                    }
                });

    }


    public void getBadgeCount() {

        if (DialogBoxError.checkInternetConnection(DashboardActivity.this))
        {
            getNotificationCount().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {

                    Log.e(TAG, "onSuccess123: " + hashMap );

                    String response_msg = hashMap.get("response_msg").toString();

                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        String unread_count = hashMap.get("unread_count").toString();
                        total_count = hashMap.get("total_count").toString();

                        if(Integer.parseInt(unread_count ) > 0) {

                            BadgeDrawable badge = navigation.getOrCreateBadge(R.id.action_notification);
                            badge.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                            navigation.getOrCreateBadge(R.id.action_notification).setNumber(Integer.parseInt(unread_count));
                            navigation.getOrCreateBadge(R.id.action_notification).setVerticalOffset(5);
                            navigation.getOrCreateBadge(R.id.action_notification).setHorizontalOffset(5);
                            navigation.getOrCreateBadge(R.id.action_notification).setMaxCharacterCount(3);
                        }
                        else
                        {
                            navigation.removeBadge(R.id.action_notification);
                        }
                        if (!isGetUserDetail)
                        {
                            if (DialogBoxError.checkInternetConnection(DashboardActivity.this))
                            {
                                getUserDeatils();
                            }
                            else
                            {
                                DialogBoxError.showInternetDialog(DashboardActivity.this);
                            }

                        }

                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(DashboardActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(DashboardActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(DashboardActivity.this,message);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, e.getMessage());
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(DashboardActivity.this);
        }

    }


    private Task<HashMap> getNotificationCount() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getNotificationCount").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
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


    private void readNotification(String notification_ref)
    {
        if (DialogBoxError.checkInternetConnection(DashboardActivity.this))
        {
            if(!(notification_ref.equalsIgnoreCase(""))) {
                markNotificationAsRead(notification_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e(TAG, "onSuccess: " + hashMap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e);
                        DialogBoxError.showError(DashboardActivity.this, e.getMessage());
                    }
                });
            }
        }
        else
        {
            DialogBoxError.showInternetDialog(DashboardActivity.this);
        }

    }


    private Task<HashMap> markNotificationAsRead(String notification_ref)
    {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("notification_ref", notification_ref);
        return mFunctions.getHttpsCallable("markNotificationAsRead").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }


    private void getOrdersForFdbk()
    {
        if (DialogBoxError.checkInternetConnection(DashboardActivity.this))
        {

            getOrdersForCongratulations().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap)
                {
                    Log.e(TAG, "onSuccess: " + hashMap);

                    String response_msg = hashMap.get("response_msg").toString();

                    ArrayList data = (ArrayList) hashMap.get("data");


                    String order_ref = "";

                    if (data.size()>0)
                    {
                        HashMap dataList = (HashMap) data.get(0);
                        order_ref = dataList.get("order_ref").toString();
                        String product_name = CapitalUtils.capitalize(dataList.get("product_name").toString());
                        String image_thumbnail = dataList.get("image_thumbnail").toString();
                        String bid_price = DoubleDecimal.twoPointsComma(dataList.get("bid_price").toString());
                        String product_ref = (Objects.requireNonNull(dataList.get("product_ref")).toString());

                        DialogBox.congratulationsDialog(DashboardActivity.this,order_ref, product_name, image_thumbnail, bid_price, product_ref);

                        updateFeedback(order_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap)
                            {
                                Log.e(TAG, "onSuccess: " + hashMap);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e);
                                DialogBoxError.showError(DashboardActivity.this, e.getMessage());
                            }
                        });

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(DashboardActivity.this, e.getMessage());
                }
            });

//            getOrdersForFeedback().addOnSuccessListener(new OnSuccessListener<HashMap>() {
//                @Override
//                public void onSuccess(HashMap hashMap)
//                {
//                    Log.e(TAG, "onSuccess: " + hashMap);
//
//                    String response_msg = hashMap.get("response_msg").toString();
//
//                    ArrayList data = (ArrayList) hashMap.get("data");
//
//                    if (data.size()>0)
//                    {
//                        HashMap dataList = (HashMap) data.get(0);
//                        String order_ref = dataList.get("order_ref").toString();
//
//                        DialogBox.feedbackDialog(DashboardActivity.this,order_ref);
//
//                    }
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.e(TAG, "onFailure: " + e);
//                    DialogBoxError.showError(DashboardActivity.this, e.getMessage());
//                }
//            });
        }
        else
        {
            DialogBoxError.showInternetDialog(DashboardActivity.this);
        }

    }


    private Task<HashMap> getOrdersForFeedback() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getOrdersForFeedback").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }


    private Task<HashMap> getOrdersForCongratulations() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("is_win", true);

        return mFunctions.getHttpsCallable("getOrdersForFeedback").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }


    private Task<HashMap> updateFeedback(String order_ref) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("order_ref", order_ref);
        data.put("is_win", true);

        return mFunctions.getHttpsCallable("updateFeedback").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private void getUserToken()
    {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {


                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String user_token = task.getResult().getToken();
                        Log.e("onCompletetoken", "" + user_token);

                        sharedPreferenceUtil.setString("user_token",user_token);
                        sharedPreferenceUtil.save();
                    }
                });
    }

}