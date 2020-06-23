package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.utilities.DialogBoxError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = WebViewActivity.class.getSimpleName();
    private Context context = WebViewActivity.this;
    WebView mywebview;

    private static String URL;
    private String id, title;

    private ImageView back_image;
    private FirebaseFunctions mFunctions;
    private TextView heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getSupportActionBar().hide();

        mFunctions = FirebaseFunctions.getInstance();
        heading = findViewById(R.id.heading);

        back_image = findViewById(R.id.back_image);
        back_image.setOnClickListener(this);

        mywebview = (WebView) findViewById(R.id.webView);

        WebSettings settings = mywebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(!(extras == null))
        {
            id = extras.getString("id");
            URL = extras.getString("page_url");
            title = extras.getString("title");

            heading.setText(title);

            mywebview.loadUrl(URL);


            if (DialogBoxError.checkInternetConnection(WebViewActivity.this))
            {
                mywebview.setWebViewClient(new WebViewClient(){

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (Uri.parse(url).getHost().equals(URL)) {
                            // This is your web site, so do not override; let the WebView to load the page
                            return false;
                        }
                        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        handler.proceed();
                    }
                });
            }
            else
            {
                DialogBoxError.showInternetDialog(WebViewActivity.this);
            }




//            if(type.equals("refund_policy"))
//            {
//                heading.setText("Return & Refund Policy");
//            }
//            else if(type.equals("legal"))
//            {
//                heading.setText("Legal");
//            }
//            else if(type.equals("privacy_policy"))
//            {
//                heading.setText("Privacy Policy");
//            }
//            else if(type.equals("terms_and_conditions"))
//            {
//                heading.setText("Terms & Conditions");
//            }
        }

//        getCmsPagesTitle().addOnSuccessListener(new OnSuccessListener<HashMap>() {
//            @Override
//            public void onSuccess(HashMap hashMap) {
//                Log.e(TAG, "onSuccess: " + hashMap);
//
//                ArrayList al = (ArrayList) hashMap.get("data");
//
//                for(int i = 0; i<al.size() ; i++) {
//
//                    HashMap data = (HashMap) al.get(i);
//                    String id = data.get("id").toString();
//                    String title = data.get("title").toString();
//                    String page_url = data.get("page_url").toString();
//
//                    if(type.equals("refund_policy"))
//                    {
//                        if(id.equals("refund-policy")) {
//
//                            URL = page_url;
//                            break;
//                        }
//                    }
//                    else if(type.equals("legal"))
//                    {
//                        if(id.equals("legal")) {
//
//                            URL = page_url;
//                            break;
//                        }
//                    }
//                    else if(type.equals("privacy-policy"))
//                    {
//                        if(id.equals("privacy_policy")) {
//
//                            URL = page_url;
//                            break;
//                        }
//                    }
//                    else if(type.equals("terms_and_conditions"))
//                    {
//                        if(id.equals("terms-and-conditions")) {
//
//                            URL = page_url;
//                            break;
//                        }
//                    }
//                }
//
//                mywebview.loadUrl(URL);
//
//                mywebview.setWebViewClient(new WebViewClient(){
//
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                        if (Uri.parse(url).getHost().equals(URL)) {
//                            // This is your web site, so do not override; let the WebView to load the page
//                            return false;
//                        }
//                        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        startActivity(intent);
//                        return true;
//                    }
//
//                    @Override
//                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                        handler.proceed();
//                    }
//                });
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e(TAG, "onFailure: " + e);
//                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
//            }
//        });



    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_image){
            onBackPressed();
        }
    }

    private Task<HashMap> getCmsPagesTitle() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getCmsPagesTitle").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }
}