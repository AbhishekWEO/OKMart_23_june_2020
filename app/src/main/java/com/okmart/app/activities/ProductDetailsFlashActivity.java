package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.adapters.FeatureDetailsAdapter;
import com.okmart.app.adapters.FeatureImagesAdapter;
import com.okmart.app.adapters.FeaturedAdapter;
import com.okmart.app.adapters.FlashAdapter;
import com.okmart.app.adapters.FlashSeeMoreAdapter;
import com.okmart.app.adapters.ProductAdapter;
import com.okmart.app.adapters.ProductDetailAdapter;
import com.okmart.app.adapters.TermsAdapter;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SharedPreferenceUtil;
import com.okmart.app.utilities.Vibration;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsFlashActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = ProductDetailsFlashActivity.this;
    private ViewPager pager_product_images;
    private DotsIndicator dots_indicator;
    private CardView cv_buyNow;//cardView_seeMore
    private RelativeLayout cardView_seeMore,cardView_less;
    private LinearLayout ll_see_more, ll_features, ll_buy, ll_timer, ll_less;
    private ImageView img_back, img_share, imgarrow1;
    private RelativeLayout rl_viewpager,rl_transparentbg,rl_features;
    private TextView no_of_views,tv_productName,tv_price,tv_strike_price,tv_descriptionTitle, tv_description, tv_featuresTitle, tv_termsTitle,
            tv_buyNow,tv_shippingFee,points,tv_hour,tv_min,tv_sec,tv_nextPrice;
    //private LockableNestedScrollView features_scrollView;
    private ScrollView features_scrollView;
    private List<String> product_image=new ArrayList<>();
    private String product_ref="",downtime_ref,current_price;
    private FirebaseFunctions mFunctions;
    private EditText edt_price;
    private Double wallet_bal;
    private long current_time_milliseconds, timer_difference;
    private CountDownTimer timer;
    private boolean isTimerFinish=false;
    private String product_status="";
    private RecyclerView rv_feature_details, rv_feature_images, rv_terms_conditions;
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> details = new ArrayList<>();
    private ArrayList<String> terms = new ArrayList<>();
    private ProgressBar progressBarBanner, progressBarDescription;
    private FirebaseFirestore db;
    private String TAG = ProductDetailsFlashActivity.class.getSimpleName();
    private ProgressBar progressBarSubmit;
    private Boolean flag = false;
    private ProgressBar progressBottom;

    private ProductDetailAdapter viewPagerAdapter;
    private RelativeLayout rl_phoneDetails;

    private RelativeLayout rl_bid;
    private TextView tv_bidPrice,tv_currentPrice,tv_directPrice;
    private SeekBar seekBar;
    private int current_product_price,direct_purchase_price, step=1;
    private String place_bid="";
    private double bid_price;
    private boolean isCollapse=true;
    private RelativeLayout rectangle;

    private String flash_down_second;
    private String down_per_price;
    private String min_price;
    private String flash_sale_order_time;
    private long flash_down_second_milli;

    private SharedPreferenceUtil sharedPreferenceUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_flash);
        mFunctions=FirebaseFunctions.getInstance();
        getSupportActionBar().hide();

        FlashAdapter.isCliclableflash=false;
        FlashSeeMoreAdapter.isCliclable=false;
        db = FirebaseFirestore.getInstance();

        sharedPreferenceUtil = new SharedPreferenceUtil(this);

        initXml();
        setonClickListener();

        isCollapse=true;

        Glide.with(context).load(R.drawable.button).into(imgarrow1);

        if (DialogBoxError.checkInternetConnection(ProductDetailsFlashActivity.this))
        {
            db.collection("products")
                    .whereEqualTo("is_active", true)
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

                                        Log.e(TAG, "ModificationsProduct: " + dc.getDocument().getData());
                                        Log.e(TAG, "ModificationsProduct2: " + dc.getDocument().getId());

                                        String documentId = dc.getDocument().getId();
                                        if (documentId.equals(product_ref)) {
                                            HashMap hm = (HashMap) dc.getDocument().getData();
                                            no_of_views.setText(hm.get("views").toString());
                                        }

                                        break;

                                    case MODIFIED:

                                        Log.e(TAG, "ModificationsProduct: " + dc.getDocument().getData());
                                        Log.e(TAG, "ModificationsProduct2: " + dc.getDocument().getId());

                                        String document_id = dc.getDocument().getId();
                                        if (document_id.equals(product_ref)) {
                                            HashMap hm = (HashMap) dc.getDocument().getData();
                                            no_of_views.setText(hm.get("views").toString());
                                        }

                                        break;

                                }
                            }
                        }
                    });

            setViewsOfProduct(true).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e);
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(ProductDetailsFlashActivity.this);
        }

    }

    private void initXml()
    {
        imgarrow1 = findViewById(R.id.imgarrow1);
        progressBarSubmit = findViewById(R.id.progressBarSubmit);
        progressBarBanner = findViewById(R.id.progressBarBanner);
        progressBarDescription = findViewById(R.id.progressBarDescription);
        pager_product_images = findViewById(R.id.pager_product_images);
        dots_indicator = findViewById(R.id.dots_indicator);
        no_of_views = findViewById(R.id.no_of_views);
        tv_productName = findViewById(R.id.tv_productName);
        tv_price = findViewById(R.id.tv_price);
        tv_strike_price = findViewById(R.id.tv_strike_price);
        tv_descriptionTitle = findViewById(R.id.tv_descriptionTitle);
        tv_description = findViewById(R.id.tv_description);
        tv_featuresTitle = findViewById(R.id.tv_featuresTitle);
        tv_termsTitle = findViewById(R.id.tv_termsTitle);
        cardView_seeMore = findViewById(R.id.cardView_seeMore);
        ll_see_more = findViewById(R.id.ll_see_more);
        tv_shippingFee = findViewById(R.id.tv_shippingFee);
        img_back = findViewById(R.id.img_back);
        img_share = findViewById(R.id.img_share);
        rl_viewpager = findViewById(R.id.rl_viewpager);
        rl_features = findViewById(R.id.rl_features);
        rv_feature_images = findViewById(R.id.rv_feature_images);
        rv_feature_images.setLayoutManager(new LinearLayoutManager(context));
        rv_feature_details = findViewById(R.id.rv_feature_details);
        rv_feature_details.setLayoutManager(new LinearLayoutManager(context));
        rv_terms_conditions = findViewById(R.id.rv_terms_conditions);
        rv_terms_conditions.setLayoutManager(new LinearLayoutManager(context));
        cv_buyNow = findViewById(R.id.cv_buyNow);
        ll_features = findViewById(R.id.ll_features);
        rl_transparentbg = findViewById(R.id.rl_transparentbg);
        ll_buy = findViewById(R.id.ll_buy);
        tv_buyNow = findViewById(R.id.tv_buyNow);
        features_scrollView = findViewById(R.id.features_scrollView);
        points= findViewById(R.id.points);
        edt_price= findViewById(R.id.edt_price);
        tv_hour= findViewById(R.id.tv_hour);
        tv_min= findViewById(R.id.tv_min);
        tv_sec= findViewById(R.id.tv_sec);
        ll_timer= findViewById(R.id.ll_timer);
        ll_timer.setVisibility(View.GONE);
        tv_nextPrice= findViewById(R.id.tv_nextPrice);
        ll_less= findViewById(R.id.ll_less);
        cardView_less= findViewById(R.id.cardView_less);

        progressBottom= findViewById(R.id.progressBottom);
        rl_phoneDetails = findViewById(R.id.rl_phoneDetails);
        rectangle = findViewById(R.id.rectangle);

        rl_bid = findViewById(R.id.rl_bid);
        tv_bidPrice = findViewById(R.id.tv_bidPrice);
        tv_currentPrice = findViewById(R.id.tv_currentPrice);
        tv_directPrice = findViewById(R.id.tv_directPrice);
        seekBar = findViewById(R.id.seekBar);

        /*features_scrollView.setScrollingEnabled(false);
        rv_feature_details.setNestedScrollingEnabled(false);
        rv_feature_images.setNestedScrollingEnabled(false);
        rv_terms_conditions.setNestedScrollingEnabled(false);*/

        tv_nextPrice.setVisibility(View.INVISIBLE);
        cv_buyNow.setVisibility(View.VISIBLE);
        ll_see_more.setVisibility(View.GONE);
        progressBottom.setVisibility(View.GONE);
        cardView_seeMore.setVisibility(View.GONE);

        if (DialogBoxError.checkInternetConnection(ProductDetailsFlashActivity.this))
        {
            getSetData();
        }
        else
        {
            DialogBoxError.showInternetDialog(ProductDetailsFlashActivity.this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            features_scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
                {
                    Log.e("DownscrollY", ""+scrollY+", oldScrollY"+oldScrollY);

                    if (scrollY > oldScrollY)
                    {
                        int height= rl_viewpager.getHeight();
                        Log.e("height",""+height);
                        if (height>=540)
                        {
                            // Log.e("DownscrollY", ""+scrollY+", oldScrollY"+oldScrollY);
                            scrollDown();
                        }

                        //Toast.makeText(ProductDetailsFlashActivity.this, "Scroll DOWN", Toast.LENGTH_SHORT).show();

                    }
                    if (scrollY < oldScrollY)
                    {
                        //scrollUp();
                        Log.e("UpscrollY", ""+scrollY+", oldScrollY"+oldScrollY);
                        //Toast.makeText(ProductDetailsFlashActivity.this, "Scroll UP", Toast.LENGTH_SHORT).show();
                    }

                    if (scrollY == 0)
                    {
                        Log.e("changed2", "TOP SCROLL");
                        collaspseData();

                        //Toast.makeText(ProductDetailsFlashActivity.this, "TOP SCROLL", Toast.LENGTH_SHORT).show();
                    }

                    if (scrollY == 5)
                    {

                    }

                    if (scrollY == v.getMeasuredHeight())
                    {
                        Log.e("changed2", "BOTTOM SCROLL");
                        //Toast.makeText(ProductDetailsFlashActivity.this, "BOTTOM SCROLL", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }

    }


    private void getSetData()
    {
        product_image.clear();

        getWalletPointOfUser().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e("ProductDetailsFlash","onSuccess: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {

                    wallet_bal=Double.parseDouble(hashMap.get("walletPoint").toString());

                    points.setText(DoubleDecimal.twoPoints(hashMap.get("walletPoint").toString()));
                }
                else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(ProductDetailsFlashActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(ProductDetailsFlashActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(ProductDetailsFlashActivity.this,message);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ProductDetailsFlash", "onFailure: " + e);
//                DialogBoxError.showError(ProductDetailsFlashActivity.this, getString(R.string.something_went_wrong));
            }
        });

        product_ref=getIntent().getStringExtra("product_ref");

        if (DialogBoxError.checkInternetConnection(ProductDetailsFlashActivity.this))
        {
            getPrductDetails(product_ref);
        }
        else
        {
            DialogBoxError.showInternetDialog(ProductDetailsFlashActivity.this);
        }

        if (getIntent().getStringExtra("notification_ref")!=null)
        {
            if (DialogBoxError.checkInternetConnection(ProductDetailsFlashActivity.this))
            {
                readNotification(getIntent().getStringExtra("notification_ref"));
            }
            else
            {
                DialogBoxError.showInternetDialog(ProductDetailsFlashActivity.this);
            }
        }
    }

    private void setAdapter(String isScrollDown)
    {
        pager_product_images.setAdapter(null);
        viewPagerAdapter = new ProductDetailAdapter(this,product_image,isScrollDown);
        //pager_product_images.setAdapter(new ProductDetailAdapter(this,product_image));
        pager_product_images.setAdapter(viewPagerAdapter);
        dots_indicator.setViewPager(pager_product_images);
    }

    private void setonClickListener()
    {
        ll_see_more.setOnClickListener(this);
        cv_buyNow.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_share.setOnClickListener(this);
        ll_less.setOnClickListener(this);
        rl_phoneDetails.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ll_see_more:
                // rl_features.setBackgroundColor(getResources().getColor(R.color.green));
                //viewPagerAdapter.showMore("show_more");

//                image.setVisibility(View.VISIBLE);
                ll_see_more.setVisibility(View.GONE);
                cardView_seeMore.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //23 jan
                        //rl_viewpager.getLayoutTransition().setDuration(400);
                        rl_viewpager.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

                        rl_viewpager.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,300));
//                        pager_product_images.setLayoutParams(new RelativeLayout.LayoutParams(500, ViewGroup.LayoutParams.MATCH_PARENT));

                        Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                        //Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.top_anim);
                        pager_product_images.startAnimation(fadeInAnimation);

                        /*ValueAnimator va = ValueAnimator.ofInt(100, 300);
                        va.setDuration(400);
                        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                Integer value = (Integer) animation.getAnimatedValue();
                                pager_product_images.getLayoutParams().height = value.intValue();
                                pager_product_images.requestLayout();
                            }
                        });
                        va.start();*/

                        int colorFrom = getResources().getColor(R.color.grey1_);
                        int colorTo = getResources().getColor(R.color.grey_);
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(250); // milliseconds
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {

                                //rl_viewpager.setAlpha(0.3f);
                                rl_viewpager.setBackgroundColor((int) animator.getAnimatedValue());
                            }

                        });
                        colorAnimation.start();

                    }
                },200);

                RelativeLayout.LayoutParams featuresParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                featuresParams.topMargin=240;
                //rl_features.getLayoutTransition().setDuration(500);
                rl_features.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                rl_features.setLayoutParams(featuresParams);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) features_scrollView.getLayoutParams();
                params.rightMargin = 10;
                features_scrollView.setLayoutParams(params);
                //features_scrollView.setScrollingEnabled(true);
                rv_feature_details.setNestedScrollingEnabled(true);
                rv_feature_images.setNestedScrollingEnabled(true);

                ll_less.setVisibility(View.VISIBLE);
                cardView_less.setVisibility(View.VISIBLE);

                break;

            case R.id.cv_buyNow:
                collaspseData();

                cv_buyNow.setEnabled(false);

                cardView_seeMore.setVisibility(View.GONE);
                String text=tv_buyNow.getText().toString();

                tv_buyNow.setText("");
                progressBarSubmit.setVisibility(View.VISIBLE);

                if (text.equalsIgnoreCase("Buy Now")) {
                    if (DialogBoxError.checkInternetConnection(ProductDetailsFlashActivity.this))
                    {
                        flashSale();
                    }
                    else
                    {
                        DialogBoxError.showInternetDialog(ProductDetailsFlashActivity.this);
                    }
                }


                break;

            case R.id.img_back:

                if (progressBarSubmit.getVisibility()==View.GONE)
                {
                    if (tv_buyNow.getText().toString().equalsIgnoreCase("Place Bid"))
                    {
                        tv_buyNow.setText("bid now");
                        rl_transparentbg.setVisibility(View.GONE);
                        features_scrollView.fullScroll(View.FOCUS_UP);
                        features_scrollView.smoothScrollTo(0,0);
                        features_scrollView.setVisibility(View.VISIBLE);
                        ll_buy.setVisibility(View.GONE);
                    }
                    else
                    {
                        finish();
                    }
                }

                break;

            case R.id.img_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this great deal, let’s purchase it at OK Express! "+sharedPreferenceUtil.getString("share_url","") + "/product?ref=" + product_ref);
                startActivity(Intent.createChooser(intent, "Share"));
                break;

            case R.id.ll_less:

                //viewPagerAdapter.showMore("show_less");

                features_scrollView.fullScroll(View.FOCUS_UP);
                features_scrollView.smoothScrollTo(0,0);

                ll_see_more.setVisibility(View.VISIBLE);
                cardView_seeMore.setVisibility(View.VISIBLE);

                ll_less.setVisibility(View.GONE);
                cardView_less.setVisibility(View.GONE);

                int rl_viewpager_height=rl_viewpager.getHeight();
                Log.e("rl_viewpager_height",""+rl_viewpager_height);

                //23 jan
                rl_viewpager.getLayoutTransition().setDuration(700);
                rl_viewpager.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                rl_viewpager.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,540));

                Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                pager_product_images.startAnimation(fadeInAnimation);

                /*ValueAnimator va = ValueAnimator.ofInt(540, 540);
                va.setDuration(400);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        pager_product_images.getLayoutParams().height = value.intValue();
                        pager_product_images.requestLayout();
                    }
                });
                va.start();*/

                //rl_viewpager.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,540));

                RelativeLayout.LayoutParams features_params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                features_params.topMargin=480;
                rl_features.setLayoutParams(features_params);

                //features_scrollView.setScrollingEnabled(false);
                rv_feature_details.setNestedScrollingEnabled(false);
                rv_feature_images.setNestedScrollingEnabled(false);

                break;
            case R.id.rl_phoneDetails:
                collaspseData();
                break;
        }
    }

    // productDetailsRetrieve
    private void getPrductDetails(String product_ref)
    {

        productDetailRetrieve(product_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap)
            {
                progressBarBanner.setVisibility(View.GONE);
                progressBarDescription.setVisibility(View.GONE);

                Log.e("ProductDetailsFlash","onSuccess: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    String success = hashMap.get("success").toString();
                    if (success.equalsIgnoreCase("true"))
                    {
                        HashMap<String,Object> productDetails = (HashMap) hashMap.get("productDetails");

                        Log.e("ProductDetailsFlash",""+productDetails);

                        product_status= productDetails.get("product_status").toString();
                        current_price=productDetails.get("current_price").toString();

                        tv_price.setText(getResources().getString(R.string.rm)+" "+DoubleDecimal.twoPoints(productDetails.get("current_price").toString()));
//                    tv_currentPrice.setText(DoubleDecimal.twoPoints(productDetails.get("current_price").toString())+" "+getResources().getString(R.string.rm));

                        String direct_purchasPrice = productDetails.get("direct_purchase_price").toString();
                        tv_directPrice.setText(DoubleDecimal.twoPoints(direct_purchasPrice)+" "+getResources().getString(R.string.rm));

                        //seekBar.setProgress(Integer.parseInt(DoubleDecimal.twoPoints(productDetails.get("current_price").toString())));
                        //seekBar.setProgress(Integer.parseInt(productDetails.get("current_price").toString()));
                        //seekBar.setMax(Integer.parseInt(DoubleDecimal.twoPoints(direct_purchasPrice)));

                        flash_down_second = productDetails.get("flash_down_second").toString();
                        down_per_price =productDetails.get("down_per_price").toString();
                        min_price = productDetails.get("min_price").toString();
                        HashMap hashmap_flash_sale_order_time = (HashMap) productDetails.get("flash_sale_order_time");
                        flash_sale_order_time = hashmap_flash_sale_order_time.get("_seconds").toString();


                        current_time_milliseconds=System.currentTimeMillis();
                        long end_datetime_milli = Long.parseLong(flash_sale_order_time) * 1000;
                        Log.e("Response", "Response");
                        timer_difference=current_time_milliseconds-end_datetime_milli;
                        flash_down_second_milli = Long.parseLong(flash_down_second)*1000;
                        long n = (timer_difference) / flash_down_second_milli;
                        current_price = String.valueOf(Double.parseDouble(current_price) - (n*Double.parseDouble(down_per_price)));
                        if(Double.parseDouble(current_price) < (Double.parseDouble(min_price))) {
                            current_price = min_price;
                        }

                        tv_price.setText("RM " + DoubleDecimal.twoPointsComma(current_price));
                        countDownTimer();

                        current_product_price = Integer.parseInt(productDetails.get("current_price").toString());

                        bid_price = Double.parseDouble(productDetails.get("current_price").toString());
                        tv_bidPrice.setText((DoubleDecimal.twoPointsComma(productDetails.get("current_price").toString())+" "+getResources().getString(R.string.rm)));

                        direct_purchase_price=Integer.parseInt((direct_purchasPrice));

                        seekBar.setMax((direct_purchase_price-current_product_price)/step);

                        String s_price=getResources().getString(R.string.rm)+" "+DoubleDecimal.twoPointsComma(productDetails.get("s_price").toString());
                        SpannableString spannableStr = new SpannableString(s_price);
                        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
                        spannableStr.setSpan(strikethroughSpan, 0, s_price.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        tv_strike_price.setText(spannableStr);

                        if (!isTimerFinish)
                        {
                            //no_of_views.setText(productDetails.get("views").toString());
                            tv_productName.setText(CapitalUtils.capitalize(productDetails.get("product_name").toString()));

                            if(tv_description.equals(""))
                            {
                                tv_descriptionTitle.setVisibility(View.GONE);
                            }
                            else
                            {
                                tv_descriptionTitle.setVisibility(View.VISIBLE);
                                tv_description.setText(productDetails.get("desc").toString());
                            }

                            ArrayList feature_details = (ArrayList) productDetails.get("feature_details");

                            if(feature_details.size() == 0)
                            {
                                tv_featuresTitle.setVisibility(View.GONE);
                            }
                            else
                            {
                                tv_featuresTitle.setVisibility(View.VISIBLE);

                                for (int i = 0; i < feature_details.size(); i++) {

                                    String detail = (String) feature_details.get(i);

                                    details.add(detail);
                                }

                                rv_feature_details.setAdapter(new FeatureDetailsAdapter(context, details));
                            }

                            ArrayList al = (ArrayList) productDetails.get("feature_images");
                            images.clear();

                            for (int i = 0; i < al.size(); i++) {

                                String image = (String) al.get(i);

                                images.add(image);
                            }

                            rv_feature_images.setAdapter(new FeatureImagesAdapter(context, images));

                            ArrayList term_condition = (ArrayList) productDetails.get("term_condition");

                            if(term_condition.size() == 0)
                            {
                                tv_termsTitle.setVisibility(View.GONE);
                            }
                            else
                            {
                                tv_termsTitle.setVisibility(View.VISIBLE);

                                for (int i = 0; i < term_condition.size(); i++) {

                                    String term = (String) term_condition.get(i);

                                    terms.add(term);
                                }

                                rv_terms_conditions.setAdapter(new TermsAdapter(context, terms));
                            }

                            tv_shippingFee.setText(getResources().getString(R.string.rm)+" "+DoubleDecimal.twoPoints(productDetails.get("shipping_price").toString()));

                            //product images
                            product_image = (ArrayList) productDetails.get("image_gallery");
                            if (product_image.size()>0)
                            {
                                setAdapter("false");
                            }
                        }

                        //tv_nextPrice.setVisibility(View.VISIBLE);
                        ll_see_more.setVisibility(View.VISIBLE);

                        if (product_status.equals("live"))
                        {
                        /*ll_timer.setVisibility(View.VISIBLE);
                        tv_nextPrice.setText("Next price drop in");
                        cv_buyNow.setVisibility(View.VISIBLE);*/

//                        getDownTimePrc(product_ref);

                        /*HashMap end_selling_time = (HashMap) productDetails.get("end_selling_time");
                        String end_datetime = end_selling_time.get("_seconds").toString();

                        Log.e("ProductDetailsFlashActivity","end_datetime: "+end_datetime);

                        countDownTimer(end_datetime);*/

                        }
                        else if (product_status.equals("upcoming"))
                        {
                            ll_timer.setVisibility(View.VISIBLE);
                            tv_nextPrice.setVisibility(View.VISIBLE);
                            tv_nextPrice.setText("Product coming soon");
                            cv_buyNow.setVisibility(View.INVISIBLE);

                            HashMap start_selling_time = (HashMap) productDetails.get("start_selling_time");
                            String start_datetime = start_selling_time.get("_seconds").toString();
                            Log.e("ProductDetailsFlash","end_datetime: "+start_datetime);
//                        countDownTimer(start_datetime);
                        }
                        else if (product_status.equalsIgnoreCase("expired"))
                        {
                            tv_nextPrice.setVisibility(View.VISIBLE);
                            tv_nextPrice.setText("Product expired");
                            ll_timer.setVisibility(View.GONE);
                            cv_buyNow.setVisibility(View.INVISIBLE);
                        }
                        else if (product_status.equalsIgnoreCase("sold"))
                        {
                            tv_nextPrice.setVisibility(View.VISIBLE);
                            tv_nextPrice.setText("Sold out");
                            ll_timer.setVisibility(View.GONE);
                            cv_buyNow.setVisibility(View.INVISIBLE);
                        }

                        if(flag == false) {
                            if (DialogBoxError.checkInternetConnection(ProductDetailsFlashActivity.this))
                            {
                                observers();
                            }
                            else
                            {
                                DialogBoxError.showInternetDialog(ProductDetailsFlashActivity.this);
                            }

                            flag = true;
                        }

                        if (tv_nextPrice.getVisibility()==View.VISIBLE)
                        {
                            progressBottom.setVisibility(View.GONE);
                        }

                        // showMore();
                    }
                }
                /*else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(ProductDetailsFlashActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(ProductDetailsFlashActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(ProductDetailsFlashActivity.this,message);
                }*/



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBarBanner.setVisibility(View.GONE);
                progressBarDescription.setVisibility(View.GONE);
                Log.e("ProductDetailsFlash", "onFailure: " + e);
//                DialogBoxError.showError(ProductDetailsFlashActivity.this, getString(R.string.something_went_wrong));
            }
        });
    }


    public void countDownTimer()
    {

        timer = new CountDownTimer(flash_down_second_milli, 1000) {
            public void onTick(long leftTimeInMilliseconds) {

            }
            public void onFinish()
            {
                current_price = String.valueOf(Double.parseDouble(current_price) - Double.parseDouble(down_per_price));

                if(Double.parseDouble(current_price) < (Double.parseDouble(min_price)))
                {
                    current_price = min_price;
                    updateFlashProductResetOrderTime().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                        @Override
                        public void onSuccess(HashMap hashMap) {
                            Log.e("TAG", "onSuccess:userDetails2" + hashMap);

                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.e("TAG", "onFailure: " + e);
                            DialogBoxError.showError(context, e.getMessage());
                        }
                    });
                }
                else
                {
                    tv_price.setText("RM " + DoubleDecimal.twoPointsComma(current_price));

                    countDownTimer();
                }

            }

        }.start();
    }

    private Task<HashMap> productDetailRetrieve(String product_ref)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("product_ref",product_ref);

        return mFunctions.getHttpsCallable("productDetailsRetrieve").call(map)
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (HashMap)task.getResult().getData();
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

    private boolean validations()
    {
        /*if (TextUtils.isEmpty(edt_price.getText().toString().trim())) {
            DialogBoxError.showError(ProductDetailsFlashActivity.this, "Please Enter Your Offer Price");
            return false;
        }
        else if (Double.parseDouble(edt_price.getText().toString().trim())>wallet_bal)
        {
            DialogBoxError.showError(ProductDetailsFlashActivity.this, "Insufficient wallet balance");
            return false;
        }
        else if (Integer.parseInt(edt_price.getText().toString().trim())<1)
        {
            DialogBoxError.showError(ProductDetailsFlashActivity.this, "Bid amount should be greater than zero");
            return false;
        }*/

        if (bid_price>wallet_bal)
        {
            DialogBoxError.showError(ProductDetailsFlashActivity.this, "Insufficient wallet balance");
            return false;
        }

        return true;
    }

    private void flashSale()
    {
        buyFlashSaleProduct().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e("ProductDetailsFlash","onSuccess: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if(progressBarSubmit.isShown()) {
                    progressBarSubmit.setVisibility(View.GONE);
                }


                if(response_msg.equals("success"))
                {

                    Vibration.vibration(ProductDetailsFlashActivity.this);

                    Constants.playSuccesSoundEffect(ProductDetailsFlashActivity.this, Constants.click);


                    setViewsOfProduct(false).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                        @Override
                        public void onSuccess(HashMap hashMap) {
                            Log.e(TAG, "onSuccess: " + hashMap);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: " + e);
                        }
                    });

                    ProductAdapter adapter=new ProductAdapter();
                    FeaturedAdapter featuredAdapter=new FeaturedAdapter();
                    adapter.closeTimer();
                    featuredAdapter.closeTimer();

                    Intent intent=new Intent(ProductDetailsFlashActivity.this, DashboardActivity.class);
                    intent.putExtra("type","MyOffers_Successfull");
                    startActivity(intent);
                    finish();

//                    Toast.makeText(ProductDetailsFlashActivity.this, message, Toast.LENGTH_SHORT).show();

                }
                else if(response_msg.equals("low wallet balance")) {

                    DialogBoxError.showErrorBalance(context, message);
                    cv_buyNow.setEnabled(true);
                    tv_buyNow.setText("buy now");

                }
                else if(response_msg.equals("product price less than min price or greater than current_starting_price")) {

                    DialogBoxError.showError(context, message);
                    cv_buyNow.setEnabled(true);
                    tv_buyNow.setText("buy now");

                }
                else if(response_msg.equals("product sold out")) {

                    DialogBoxError.showError(context, message);
                    cv_buyNow.setEnabled(true);
                    tv_buyNow.setText("buy now");
                }
                else if(response_msg.equals("product does not exist")) {

                    DialogBoxError.showError(context, message);
                    cv_buyNow.setEnabled(true);
                    tv_buyNow.setText("buy now");

                }
                else
                {
                    DialogBoxError.showError(context, message);
                    cv_buyNow.setEnabled(true);
                    tv_buyNow.setText("buy now");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                cv_buyNow.setEnabled(true);
                tv_buyNow.setText("buy now");

                if(progressBarSubmit.isShown()) {
                    progressBarSubmit.setVisibility(View.GONE);
                }
                Log.e("ProductDetailsFlash", "onFailure: " + e);

//                DialogBoxError.showError(ProductDetailsFlashActivity.this, getString(R.string.something_went_wrong));

            }
        });
    }

    private Task<HashMap> buyFlashSaleProduct()
    {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM d yyyy hh:mm:ss");
        String strDate = formatter.format(date);


        Map<String,Object> map = new HashMap<>();
        map.put("product_ref",product_ref);
        map.put("current_price",Double.parseDouble(current_price));
        map.put("order_time",strDate);

        return mFunctions.getHttpsCallable("buyFlashSaleProduct").call(map)
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Log.e("ffff",""+(HashMap)task.getResult().getData());
                        return (HashMap)task.getResult().getData();
                    }
                });

    }

    private Task<HashMap> setViewsOfProduct(boolean flag) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("product_ref", product_ref);
        data.put("is_view", flag);
        return mFunctions.getHttpsCallable("setViewsOfProduct").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    //countdowntimer

//    private void countDownTimer(String end_datetime)
//    {
//        current_time_milliseconds=System.currentTimeMillis();
//        timer_difference=(Long.valueOf(end_datetime) * 1000)-current_time_milliseconds;
//
//        timer = new CountDownTimer(timer_difference, 1000) {
//            public void onTick(long leftTimeInMilliseconds) {
//                Log.e("ProductDetailActivity","TimingProductDetailActivity: "+ String.valueOf(leftTimeInMilliseconds));
//                int seconds = (int) (leftTimeInMilliseconds / 1000) % 60 ;
//                int minutes = (int) ((leftTimeInMilliseconds / (1000*60)) % 60);
//                int hours   = (int) ((leftTimeInMilliseconds / (1000*60*60)) % 24);
//
//                tv_hour.setText(String.format("%02d", hours));
//                tv_min.setText(String.format("%02d", minutes));
//                tv_sec.setText(String.format("%02d", seconds));
//
//                if (product_status.equals("live"))
//                {
//                    ll_timer.setVisibility(View.VISIBLE);
//                    tv_nextPrice.setVisibility(View.VISIBLE);
//                    tv_nextPrice.setText("Price drop in");
//                    cv_buyNow.setVisibility(View.VISIBLE);
//                    ll_timer.setVisibility(View.VISIBLE);
//                }
//                progressBottom.setVisibility(View.GONE);
//
//                /*if (tv_nextPrice.getVisibility()==View.VISIBLE)
//                {
//                    progressBottom.setVisibility(View.GONE);
//                }*/
//
//
//                //ll_timer.setVisibility(View.VISIBLE);
//
//            }
//
//            public void onFinish() {
////                holder.tv_timer.setText("00:00:00");
//
//                if (progressBottom.getVisibility()==View.GONE)
//                {
//                    progressBottom.setVisibility(View.GONE);
//                }
//
//                tv_nextPrice.setVisibility(View.INVISIBLE);
//                cv_buyNow.setVisibility(View.INVISIBLE);
//                ll_timer.setVisibility(View.INVISIBLE);
//
//                isTimerFinish=true;
//
//
//                if (product_status.equals("live"))
//                {
//                    getDownTimePrc(product_ref);
//                }
//
//                //getPrductDetails(product_ref);
//            }
//        }.start();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (timer!=null)
        {
            timer.cancel();
        }*/
        //cancelTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (timer!=null)
        {
            timer.cancel();
        }*/

        FlashAdapter.isCliclableflash=true;
        FlashSeeMoreAdapter.isCliclable=true;

        cancelTimer();

        setViewsOfProduct(false).addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onDestroyproduct:success " + hashMap);

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onDestroyproduct:failure " + e);
            }
        });

    }

    private void cancelTimer()
    {
        if (timer!=null)
        {
            timer.cancel();
        }
    }

    private void observers()
    {
        db.collection("products")
                .whereEqualTo("is_active",true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                    Log.e(TAG, "ProductDetailsObserver: " + documentChange.getDocument().getData());
                                    ///
                                    HashMap hashMap = (HashMap) documentChange.getDocument().getData();
                                    String product_status_new = hashMap.get("product_status").toString();
                                    String current_price_new = hashMap.get("current_price").toString();
                                    String flash_sale_order_time_new = hashMap.get("flash_sale_order_time").toString();
                                    String product_ref_new = documentChange.getDocument().getId();

                                    Log.e(TAG, "product_ref_new: " + product_ref_new);

                                    String is_flash = hashMap.get("is_flash").toString();

                                    if (is_flash.equalsIgnoreCase("true"))
                                    {
                                        if (product_ref.equals(product_ref_new))
                                        {
                                            //Timestamp(seconds=1591066800, nanoseconds=0)
                                            String rep1=flash_sale_order_time_new.replace("Timestamp(seconds=","");

                                            String[] split=rep1.split(",");
                                            String new_flash_sale_order_time=split[0];
                                            //

                                            if (!(new_flash_sale_order_time.equals(flash_sale_order_time)))
                                            {
                                                flash_sale_order_time = new_flash_sale_order_time;
                                                Log.e("ObserveFlash", "jdvsjjsdv");

                                                cancelTimer();

                                                if (DialogBoxError.checkInternetConnection(ProductDetailsFlashActivity.this))
                                                {
                                                    getPrductDetails(product_ref_new);
                                                }
                                                else
                                                {
                                                    DialogBoxError.showInternetDialog(ProductDetailsFlashActivity.this);
                                                }
                                            }

                                            if (!(product_status_new.equals(product_status)))
                                            {
                                                cancelTimer();

                                                if (DialogBoxError.checkInternetConnection(ProductDetailsFlashActivity.this))
                                                {
                                                    getPrductDetails(product_ref_new);
                                                }
                                                else
                                                {
                                                    DialogBoxError.showInternetDialog(ProductDetailsFlashActivity.this);
                                                }
                                            }
                                        /*if (!(current_price.equals(DoubleDecimal.twoPoints(current_price_new))))
                                        {
                                            tv_price.setText("RM "+DoubleDecimal.twoPoints(current_price_new));

                                        }*/
                                        }
                                    }



//                                    if (product_status_new.equals("live"))
//                                    {
//                                        cancelTimer();
//
//                                        ll_timer.setVisibility(View.VISIBLE);
//                                        tv_nextPrice.setText("Next price drop in");
//                                        cv_buyNow.setVisibility(View.VISIBLE);
//
//                                        getPrductDetails(product_ref);
//
//                                        String end_selling_time=hashMap.get("end_selling_time").toString();
//                                        Log.e("end_selling_time",end_selling_time);
//
//                                        /*HashMap end_selling_time = (HashMap) hashMap.get("end_selling_time");
//                                        String end_datetime = end_selling_time.get("seconds").toString();
//
//                                        Log.e("ProductDetailsFlashActivity","end_datetime: "+end_datetime);
//
//                                        countDownTimer(end_datetime);*/
//
//                                    }
//                                    else if (product_status_new.equals("upcoming"))
//                                    {
//                                        tv_nextPrice.setText("Product coming soon");
//                                        cv_buyNow.setVisibility(View.INVISIBLE);
//
//
//                                        String start_selling_time=hashMap.get("start_selling_time").toString();
//                                        Log.e("start_selling_time",start_selling_time);
//
//                                        /*HashMap start_selling_time = (HashMap) hashMap.get("start_selling_time");
//                                        String start_datetime = start_selling_time.get("seconds").toString();
//                                        Log.e("ProductDetailsFlashActivity","end_datetime: "+start_datetime);
//                                        countDownTimer(start_datetime);*/
//
//                                        getPrductDetails(product_ref_new);
//                                    }
//                                    else if (product_status_new.equalsIgnoreCase("expired"))
//                                    {
//                                        cancelTimer();
//
//                                        tv_nextPrice.setText("Product expired");
//                                        ll_timer.setVisibility(View.GONE);
//                                        cv_buyNow.setVisibility(View.INVISIBLE);
//
//
//                                    }
//                                    else if (product_status_new.equalsIgnoreCase("sold"))
//                                    {
//                                        cancelTimer();
//
//                                        tv_nextPrice.setText("Sold out");
//                                        ll_timer.setVisibility(View.GONE);
//                                        cv_buyNow.setVisibility(View.INVISIBLE);
//
//                                    }

                                    break;
                            }
                        }
                    }
                });
    }

    private void readNotification(String notification_ref)
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
//                    DialogBoxError.showError(ProductDetailsFlashActivity.this, e.getMessage());
                }
            });
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

    /*public void showMore()
    {
        int rl_features_height=rl_features.getHeight();
        Log.e(TAG, "onClick: "+rl_features_height);

        int desc_height=tv_description.getHeight();
        int tv_feature_height=tv_featuresTitle.getHeight();
        int feature_height=rv_feature_details.getHeight();
        int featureImg_height=rv_feature_images.getHeight();
        int total_height=desc_height+tv_feature_height+feature_height+featureImg_height;

        Log.e(TAG, "total_height: "+total_height);

        if (total_height>400)
        {
            ll_see_more.setVisibility(View.VISIBLE);
        }

    }*/

    private void scrollDown()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                rl_viewpager.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

                rl_viewpager.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,300));
                setAdapter("true");

                /*Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                pager_product_images.startAnimation(fadeInAnimation);

                int colorFrom = getResources().getColor(R.color.grey1_);
                int colorTo = getResources().getColor(R.color.grey_);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(250); // milliseconds
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {

                        //rl_viewpager.setBackgroundColor((int) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();*/

            }
        },200);

        RelativeLayout.LayoutParams featuresParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        featuresParams.topMargin=240;

        rl_features.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        rl_features.setLayoutParams(featuresParams);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) features_scrollView.getLayoutParams();
        params.rightMargin = 10;
        features_scrollView.setLayoutParams(params);
        //features_scrollView.setScrollingEnabled(true);
        rv_feature_details.setNestedScrollingEnabled(true);
        rv_feature_images.setNestedScrollingEnabled(true);

        isCollapse=false;
    }

    private void collaspseData()
    {
        int height= rl_viewpager.getHeight();
        Log.e("height",""+height);

        //dfds
        if (height==300)
        {
            features_scrollView.fullScroll(View.FOCUS_UP);
            features_scrollView.smoothScrollTo(0,0);

            int rl_viewpager_height=rl_viewpager.getHeight();
            Log.e("rl_viewpager_height",""+rl_viewpager_height);

            rl_viewpager.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,540));
            setAdapter("false");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    RelativeLayout.LayoutParams features_params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                    features_params.topMargin=480;
                    rl_features.setLayoutParams(features_params);

                    features_scrollView.fullScroll(View.FOCUS_UP);
                    features_scrollView.smoothScrollTo(0,0);

                }
            },10);
        }
    }

    private Task<HashMap> updateFlashProductResetOrderTime()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("product_ref", product_ref);

        return mFunctions.getHttpsCallable("updateFlashProductResetOrderTime").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }
}