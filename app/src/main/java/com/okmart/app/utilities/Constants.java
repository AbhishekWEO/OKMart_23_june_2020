package com.okmart.app.utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class Constants {

    public static String isClick="";
    public static String category_ref="";

    public static final String MOB_CODE = "+91";
    //public static final String MOB_CODE = "+60";

    public static final String createcustomer = "https://api.stripe.com/v1/";
    public static final String customers = "customers";

    /*public static final String publishablekey = "pk_test_GmeU9E8JWi34A1lB3pJE1UjW";
    public static final String authant = "Bearer sk_test_hROAOKQRiZn4SjWPCarP0IMl00Le9CKx5V";*/

    public static String publishablekey = "";
    public static String authant = "";

    public static final String charges = "charges";
    public static String amt = "amt";
    public static String txn_id = "txn_id";
    public static String payment_via = "payment_via";
    public static String fpxdetails = "fpxdetails";
    public static String is_test = "true";

    //    pagination
    public static int limit = 50;
    public static final int PAGE_START = 1;
    //    public static final int PAGE_SIZE = 6;

    public static String click = "click";
    public static String orderconfirm = "orderconfirm";

    public static String success = "success";
    public static String unauthorized = "Unauthorized";
    public static String under_maintenance = "Under maintenance";

    static MediaPlayer mp;
    public static void  playSuccesSoundEffect(Context context, String text){
        String fileName = "android.resource://"+  context.getPackageName() + "/raw/"+text;
        Uri sound = Uri.parse(fileName);
        mp = MediaPlayer.create(context, sound);

        if (mp!=null){
            mp.setVolume(0.9f,0.9f);
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer unused) {
                if (mp!=null)
                    mp.release();

                mp = null;
            }
        });
        mp.start();
    }

}