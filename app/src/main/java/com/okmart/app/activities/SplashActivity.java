package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends AppCompatActivity {

    Context context = SplashActivity.this;
    private String TAG = SplashActivity.class.getSimpleName();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFunctions mFunctions;

    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView img = (ImageView)findViewById(R.id.ic_logo);
                Animation aniSlide = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
                img.startAnimation(aniSlide);
            }
        }, 2500);




        sharedPreferenceUtil = new SharedPreferenceUtil(SplashActivity.this);
        sharedPreferenceUtil.setString("winning_status","");
        sharedPreferenceUtil.save();

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        mFunctions = FirebaseFunctions.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.e(TAG, "inside the method ");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (sharedPreferenceUtil.getString("isLogin","").equalsIgnoreCase("true"))
                {
                    setShortcuts();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            goToDashboard();

                            /*Intent intent = new Intent(context, DashboardActivity.class);
                            startActivity(intent);
                            finish();*/
                        }
                    }, 3000);
                }
                else
                {
                    setLogoutShorcut();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(mAuthListener);
    }

    private void goToDashboard()
    {
        if (getIntent().getExtras()!=null)
        {
            String type = getIntent().getExtras().getString("type");
            String ref = getIntent().getExtras().getString("ref");
            String notification_ref = getIntent().getExtras().getString("notification_ref");
            if(type!=null)
            {
                if (!(notification_ref.equalsIgnoreCase("")))
                {
                    markNotificationAsRead(notification_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                        @Override
                        public void onSuccess(HashMap hashMap) {
                            Log.e(TAG, "onSuccess: " + hashMap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: " + e);
                            DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                        }
                    });
                }
                Log.e("pushNotificaton",type);
                if(type.equals("live_product"))
                {
                    Intent intent = new Intent(this, ProductDetailsActivity.class);
                    intent.putExtra("product_ref", ref);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else if(type.equals("flash_product_live"))
                {
                    Intent intent = new Intent(this, ProductDetailsFlashActivity.class);
                    intent.putExtra("product_ref", ref);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else if(type.equals("bid_winner"))
                {
                    Intent intent = new Intent(this, DeliverNowActivity.class);
                    intent.putExtra("checkout_ref", ref);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else if(type.equals("order_confirmed") || type.equals("order_updated"))
                {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.putExtra("type","MyOffers_Successfull");
                    intent.putExtra("status_ref", ref);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else if(type.equals("admin_msg"))
                {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else if(type.equals("payment_confirmed") || type.equalsIgnoreCase("payment_failed"))
                {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.putExtra("type", "wallet");
                    intent.putExtra("my_wallet_screen","recharge");
                    intent.putExtra("status_ref", ref);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
            else
            {
                Intent extraIntent = getIntent();

                if (extraIntent.getData()!=null)
                {
                    Uri data = extraIntent.getData();


                    String redirection_type = data.toString().substring(16, 23);

                    if(redirection_type.equals("product")) {
                        String product_ref = data.toString().substring(28);
                        Intent intent = new Intent(context, DashboardActivity.class);
                        intent.putExtra("product_ref", product_ref);
                        startActivity(intent);
                        finish();
                    }
                    else {

                        String order_ref = data.toString().substring(33);

                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.putExtra("type","MyOffers_SuccessfullDeep");
                        intent.putExtra("status_ref", order_ref);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                }
                else
                {
                    Intent intent = new Intent(context, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        }
        else
        {
            Intent intent = new Intent(context, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private Task<HashMap> markNotificationAsRead(String notification_ref) {
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

    private void setShortcuts()
    {
        final ShortcutManager shortcutManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
        {
            shortcutManager = getSystemService(ShortcutManager.class);

            //Define the intent, which in this instance is launching Activity//
            Intent dynamicIntent1 = new Intent(this,DashboardActivity.class);
            dynamicIntent1.setAction(Intent.ACTION_VIEW);

            //Create the ShortcutInfo object//

            ShortcutInfo dynamicShortcut1 = new ShortcutInfo.Builder(getApplicationContext(), "home")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_home))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_home))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_home_hover))
                    .setIntent(dynamicIntent1)
                    .build();

            //2

            Intent dynamicIntent2 = new Intent(this,DashboardActivity.class);
            dynamicIntent2.setAction(Intent.ACTION_VIEW);
            dynamicIntent2.putExtra("type","MyOffers");
            ShortcutInfo dynamicShortcut2 = new ShortcutInfo.Builder(getApplicationContext(), "biddings")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_biddings))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_biddings))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_offers_hover))
                    .setIntent(dynamicIntent2)
                    .build();
            //shortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut2, dynamicShortcut1));

            //3
            Intent dynamicIntent3 = new Intent(this,DashboardActivity.class);
            dynamicIntent3.setAction(Intent.ACTION_VIEW);
            dynamicIntent3.putExtra("type","notifications");
            ShortcutInfo dynamicShortcut3 = new ShortcutInfo.Builder(this, "notifications")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_notifications))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_notifications))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_notifications_hover))
                    .setIntent(dynamicIntent3)
                    .build();


            //4
            Intent dynamicIntent4 = new Intent(this,DashboardActivity.class);
            dynamicIntent4.setAction(Intent.ACTION_VIEW);
            dynamicIntent4.putExtra("type","wallet");
            ShortcutInfo dynamicShortcut4 = new ShortcutInfo.Builder(this, "wallet")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_wallet))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_wallet))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_wallet_hover))
                    .setIntent(dynamicIntent4)
                    .build();
            /*//5
            Intent dynamicIntent5 = new Intent(this,DashboardActivity.class);
            dynamicIntent5.setAction(Intent.ACTION_VIEW);
            ShortcutInfo dynamicShortcut5 = new ShortcutInfo.Builder(this, "settings")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_settings))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_settings))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_settings_hover))
                    .setIntent(dynamicIntent5)
                    .build();*/
            shortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut1, dynamicShortcut2, dynamicShortcut3, dynamicShortcut4));


        }


    }

    private void setLogoutShorcut()
    {

        final ShortcutManager shortcutManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
        {
            shortcutManager = getSystemService(ShortcutManager.class);

            Intent dynamicIntent1 = new Intent(this,LoginActivity.class);
            dynamicIntent1.setAction(Intent.ACTION_VIEW);

            ShortcutInfo dynamicShortcut1 = new ShortcutInfo.Builder(getApplicationContext(), "login")

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_login))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_login))
                    .setIcon(Icon.createWithResource(this, R.drawable.login))
                    .setIntent(dynamicIntent1)
                    .build();

            //2
            Intent dynamicIntent2 = new Intent(this,SignupActivity.class);
            dynamicIntent2.setAction(Intent.ACTION_VIEW);
            ShortcutInfo dynamicShortcut2 = new ShortcutInfo.Builder(getApplicationContext(), "signup")

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_signup))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_signup))
                    .setIcon(Icon.createWithResource(this, R.drawable.signup))
                    .setIntent(dynamicIntent2)
                    .build();

            shortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut1, dynamicShortcut2));


        }

        /*final ShortcutManager shortcutManager;

        ShortcutInfo dynamicShortcut = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1)
        {
            shortcutManager = getSystemService(ShortcutManager.class);
            dynamicShortcut = new ShortcutInfo.Builder(SplashActivity.this, "dynamic_shortcut")
                    .setShortLabel("Label changed")
                    .build();

            shortcutManager.updateShortcuts(Arrays.asList(dynamicShortcut));
        }*/


    }
}