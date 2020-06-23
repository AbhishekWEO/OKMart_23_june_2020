package com.okmart.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.okmart.app.R;

import java.util.Arrays;

public class MobileVerifiedActivity extends AppCompatActivity {

    private Context context = MobileVerifiedActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verified);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getIntent().getStringExtra("fromSignUp")!=null)
                {
                    if (getIntent().getStringExtra("fromSignUp").equalsIgnoreCase("true"))
                    {
                        Intent intent = new Intent(context, ProfileImgActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        setShortcuts();

                        /*Intent intent = new Intent(context, DashboardActivity.class);
                        startActivity(intent);
                        finish();*/
                    }
                }
            }
        }, 1500);
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

        Intent intent = new Intent(context, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}