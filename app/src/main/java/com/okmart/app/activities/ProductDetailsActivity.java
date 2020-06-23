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
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = ProductDetailsActivity.this;
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
    private List<String>product_image=new ArrayList<>();
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
    private String TAG = ProductDetailsActivity.class.getSimpleName();
    private ProgressBar progressBarSubmit;
    private Boolean flag = false;
    private ProgressBar progressBottom;

    private ProductDetailAdapter viewPagerAdapter;
    private RelativeLayout rl_phoneDetails;

    private RelativeLayout rl_bid;
    private EditText tv_bidPrice;
    private TextView tv_currentPrice,tv_directPrice;
    private SeekBar seekBar;
    private double current_product_price, direct_purchase_price;
    private int step = 1;
    private String place_bid="";
    private double bid_price;
    private boolean isCollapse=true;
    private RelativeLayout rectangle;
    private ImageView arrow_down_bid;

    private SharedPreferenceUtil sharedPreferenceUtil;
    private RelativeLayout rl_shipping;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        mFunctions=FirebaseFunctions.getInstance();
        getSupportActionBar().hide();

        ProductAdapter.isCliclable=false;
        db = FirebaseFirestore.getInstance();

        sharedPreferenceUtil = new SharedPreferenceUtil(this);

        initXml();
        setonClickListener();

        isCollapse=true;

        Glide.with(context).load(R.drawable.button).into(imgarrow1);

        if (DialogBoxError.checkInternetConnection(ProductDetailsActivity.this))
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
            DialogBoxError.showInternetDialog(ProductDetailsActivity.this);
        }

        tv_bidPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    //scrollDown();
//                    Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });




        tv_bidPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                try{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(s.length() != 0) {
                                double a = Double.parseDouble(tv_bidPrice.getText().toString()) - Double.parseDouble(current_price);
                                seekBar.setProgress((int) (a *100));
                            }

                        }
                    }, 2000);
                }
                catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
            }
        });

    }

    private void initXml()
    {
        rl_shipping = findViewById(R.id.rl_shipping);
        arrow_down_bid = findViewById(R.id.arrow_down_bid);
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
        cv_buyNow.setVisibility(View.INVISIBLE);
        ll_see_more.setVisibility(View.GONE);
        progressBottom.setVisibility(View.VISIBLE);
        cardView_seeMore.setVisibility(View.GONE);
        arrow_down_bid.setVisibility(View.GONE);
        rl_shipping.setVisibility(View.GONE);
        ll_buy.setVisibility(View.GONE);

        if (DialogBoxError.checkInternetConnection(ProductDetailsActivity.this))
        {
            getSetData();
        }
        else
        {
            DialogBoxError.showInternetDialog(ProductDetailsActivity.this);
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
                        //if (height>=540)
                        if (height>=480)
                        {
                            // Log.e("DownscrollY", ""+scrollY+", oldScrollY"+oldScrollY);
                            scrollDown();
                        }

                    }
                    if (scrollY < oldScrollY)
                    {
//                        scrollUp();
                        Log.e("UpscrollY", ""+scrollY+", oldScrollY"+oldScrollY);
                    }

                    if (scrollY == 0)
                    {
                        Log.e("changed2", "TOP SCROLL");
                        collaspseData();
                    }

                    if (scrollY == 5)
                    {

                    }

                    if (scrollY == v.getMeasuredHeight())
                    {
                        Log.e("changed2", "BOTTOM SCROLL");
                    }
                }

            });
        }

        setOnSeekBarChangeListener();
    }

    private void setOnSeekBarChangeListener()
    {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                bid_price = current_product_price + ((float) progress/100 * step);

//                String values =String.valueOf(current_product_price + (progress * step));
//                tv_bidPrice.setText(DoubleDecimal.twoPoints(values));

                tv_bidPrice.setText(DoubleDecimal.twoPoints(String.valueOf(bid_price)));
                tv_bidPrice.setSelection(tv_bidPrice.length());

                if(!progressBarSubmit.isShown())
                {
                    if (ll_buy.getVisibility()==View.VISIBLE)
                    {
                        if (current_product_price + (progress/100 * step)==direct_purchase_price)
                        {
                            tv_buyNow.setText("Buy Now");
                        }
                        else
                        {
                            tv_buyNow.setText("Place Bid");
                        }
                    }
                }

                Log.e("seekbar",""+bid_price);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSetData()
    {
        product_image.clear();

        getWalletPointOfUser().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e("ProductDetailsActivity","onSuccess: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {

                    wallet_bal=Double.parseDouble(hashMap.get("walletPoint").toString());

                    points.setText(DoubleDecimal.twoPointsComma(hashMap.get("walletPoint").toString()));
                }
                else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(ProductDetailsActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(ProductDetailsActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(ProductDetailsActivity.this,message);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ProductDetailsActivity", "onFailure: " + e);
            }
        });

        product_ref=getIntent().getStringExtra("product_ref");

        if (DialogBoxError.checkInternetConnection(ProductDetailsActivity.this))
        {
            getPrductDetails(product_ref);
        }
        else
        {
            DialogBoxError.showInternetDialog(ProductDetailsActivity.this);
        }

        if (getIntent().getStringExtra("notification_ref")!=null)
        {
            if (DialogBoxError.checkInternetConnection(ProductDetailsActivity.this))
            {
                readNotification(getIntent().getStringExtra("notification_ref"));
            }
            else
            {
                DialogBoxError.showInternetDialog(ProductDetailsActivity.this);
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
                cardView_seeMore.setVisibility(View.GONE);
                String text=tv_buyNow.getText().toString();
                if (text.equalsIgnoreCase("bid now"))
                {
                    tv_buyNow.setText("Place Bid");

                    rl_transparentbg.setVisibility(View.VISIBLE);
                    features_scrollView.setVisibility(View.GONE);
                    ll_buy.setVisibility(View.VISIBLE);

                }
                else
                {
                    cv_buyNow.setEnabled(false);
                    img_back.setEnabled(false);

                    place_bid=tv_buyNow.getText().toString();

                    tv_buyNow.setText("");
                    progressBarSubmit.setVisibility(View.VISIBLE);

                    if (DialogBoxError.checkInternetConnection(ProductDetailsActivity.this))
                    {
                        addOffers();
                    }
                    else
                    {
                        DialogBoxError.showInternetDialog(ProductDetailsActivity.this);
                    }

                }
                break;

            case R.id.img_back:

                finish();

//                if (progressBarSubmit.getVisibility()==View.GONE)
//                {
//                    if (tv_buyNow.getText().toString().equalsIgnoreCase("Place Bid"))
//                    {
//                        tv_buyNow.setText("bid now");
//                        rl_transparentbg.setVisibility(View.GONE);
//                        features_scrollView.fullScroll(View.FOCUS_UP);
//                        features_scrollView.smoothScrollTo(0,0);
//                        features_scrollView.setVisibility(View.VISIBLE);
//                        ll_buy.setVisibility(View.GONE);
//                    }
//                    else
//                    {
//                        finish();
//                    }
//                }

                break;

            case R.id.img_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this great deal, let’s bid it at OK Express! "+sharedPreferenceUtil.getString("share_url","") + "/product?ref=" + product_ref);
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

                Log.e("ProductDetailsActivity","onSuccess: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    String success = hashMap.get("success").toString();
                    if (success.equalsIgnoreCase("true"))
                    {
                        HashMap<String,Object> productDetails = (HashMap) hashMap.get("productDetails");

                        Log.e("ProductDetailsActivity",""+productDetails);

                        product_status= productDetails.get("product_status").toString();
                        current_price=productDetails.get("current_price").toString();

                        tv_price.setText(getResources().getString(R.string.rm)+" "+DoubleDecimal.twoPointsComma(productDetails.get("current_price").toString()));
                        tv_currentPrice.setText(DoubleDecimal.twoPointsComma(productDetails.get("current_price").toString()));

                        String direct_purchasPrice = productDetails.get("direct_purchase_price").toString();
                        tv_directPrice.setText(DoubleDecimal.twoPointsComma(direct_purchasPrice));

                        current_product_price = Double.parseDouble(productDetails.get("current_price").toString());

                        bid_price = Double.parseDouble(productDetails.get("current_price").toString());
                        tv_bidPrice.setText((DoubleDecimal.twoPoints(productDetails.get("current_price").toString())));

                        direct_purchase_price= Double.parseDouble((direct_purchasPrice));

                        double a = Double.parseDouble(productDetails.get("direct_purchase_price").toString());
                        double b = Double.parseDouble(productDetails.get("current_price").toString());

                        seekBar.setMax((int) ((a-b)*100)/(step));

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

                            rl_shipping.setVisibility(View.VISIBLE);
                            tv_shippingFee.setText(getResources().getString(R.string.rm)+" "+DoubleDecimal.twoPoints(productDetails.get("shipping_price").toString()));

                            ll_buy.setVisibility(View.VISIBLE);

                            //product images
                            product_image = (ArrayList) productDetails.get("image_gallery");
                            if (product_image.size()>0)
                            {
                                setAdapter("false");
                            }
                        }

                        ll_see_more.setVisibility(View.VISIBLE);

                        if (product_status.equals("live"))
                        {

                            getDownTimePrc(product_ref);

                        }
                        else if (product_status.equals("upcoming"))
                        {
                            ll_timer.setVisibility(View.VISIBLE);
                            tv_nextPrice.setVisibility(View.VISIBLE);
                            tv_nextPrice.setText("Product coming soon");
                            cv_buyNow.setVisibility(View.INVISIBLE);
                            arrow_down_bid.setVisibility(View.GONE);
                            ll_buy.setVisibility(View.GONE);

                            HashMap start_selling_time = (HashMap) productDetails.get("start_selling_time");
                            String start_datetime = start_selling_time.get("_seconds").toString();
                            Log.e("ProductDetailsActivity","end_datetime: "+start_datetime);
                            countDownTimer(start_datetime);
                        }
                        else if (product_status.equalsIgnoreCase("expired"))
                        {
                            tv_nextPrice.setVisibility(View.VISIBLE);
                            tv_nextPrice.setText("Product expired");
                            ll_timer.setVisibility(View.GONE);
                            cv_buyNow.setVisibility(View.INVISIBLE);
                            arrow_down_bid.setVisibility(View.GONE);
                            ll_buy.setVisibility(View.GONE);
                        }
                        else if (product_status.equalsIgnoreCase("sold"))
                        {
                            tv_nextPrice.setVisibility(View.VISIBLE);
                            tv_nextPrice.setText("Sold out");
                            ll_timer.setVisibility(View.GONE);
                            cv_buyNow.setVisibility(View.INVISIBLE);
                            arrow_down_bid.setVisibility(View.GONE);
                            ll_buy.setVisibility(View.GONE);
                        }

                        if(flag == false) {
                            if (DialogBoxError.checkInternetConnection(ProductDetailsActivity.this))
                            {
                                observers();
                            }
                            else
                            {
                                DialogBoxError.showInternetDialog(ProductDetailsActivity.this);
                            }

                            flag = true;
                        }

                        if (tv_nextPrice.getVisibility()==View.VISIBLE)
                        {
                            progressBottom.setVisibility(View.GONE);
                        }

                    }
                }
                /*else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(ProductDetailsActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(ProductDetailsActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(ProductDetailsActivity.this,message);
                }*/



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBarBanner.setVisibility(View.GONE);
                progressBarDescription.setVisibility(View.GONE);
                Log.e("ProductDetailsActivity", "onFailure: " + e);
//                DialogBoxError.showError(ProductDetailsActivity.this, getString(R.string.something_went_wrong));
            }
        });
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

    private void getDownTimePrc(String product_ref)
    {
        if (DialogBoxError.checkInternetConnection(ProductDetailsActivity.this))
        {
            getDownTimePrice(product_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e("ProductDetailsActivity","product_ref: "+product_ref+", getDownTimePrc: " + hashMap);

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        try
                        {
                            HashMap<String,Object> productDowmTime = (HashMap) hashMap.get("data");

                            if (productDowmTime.size()>0)
                            {
                                downtime_ref = productDowmTime.get("downtime_ref").toString();

                                Log.e("ProductDetailsActivity","downtime_ref: " + downtime_ref);

                                double a = Double.parseDouble(productDowmTime.get("current_price").toString());

                                if (a>0)
                                {

                                    tv_price.setText("RM "+DoubleDecimal.twoPointsComma(productDowmTime.get("current_price").toString()));
                                    tv_currentPrice.setText(DoubleDecimal.twoPointsComma(productDowmTime.get("current_price").toString()));

                                    current_price=productDowmTime.get("current_price").toString();
                                    current_product_price = Double.parseDouble(productDowmTime.get("current_price").toString());

                                    seekBar.setMax((int) ((direct_purchase_price-current_product_price)*100)/step);

                                    HashMap end_datetime = (HashMap) productDowmTime.get("end_datetime");
                                    String endDatetime = end_datetime.get("_seconds").toString();

                                    countDownTimer(endDatetime);
                                }
                                else
                                {
                                    getDownTimePrc(product_ref);
                                }
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    /*else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(ProductDetailsActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(ProductDetailsActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(ProductDetailsActivity.this,message);
                    }*/


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ProductDetailsActivity", "onFailure: " + e);
//                DialogBoxError.showError(ProductDetailsActivity.this, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(ProductDetailsActivity.this);
        }

    }

    private Task<HashMap>getDownTimePrice(String product_ref)
    {
        Map<String,Object> map = new HashMap<>();
        map.put("product_ref",product_ref);

        return mFunctions.getHttpsCallable("getDownPrice ").call(map)
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (HashMap)task.getResult().getData();
                    }
                });
    }

    private boolean validations()
    {
        /*if (TextUtils.isEmpty(edt_price.getText().toString().trim())) {
            DialogBoxError.showError(ProductDetailsActivity.this, "Please Enter Your Offer Price");
            return false;
        }
        else if (Double.parseDouble(edt_price.getText().toString().trim())>wallet_bal)
        {
            DialogBoxError.showError(ProductDetailsActivity.this, "Insufficient wallet balance");
            return false;
        }
        else if (Integer.parseInt(edt_price.getText().toString().trim())<1)
        {
            DialogBoxError.showError(ProductDetailsActivity.this, "Bid amount should be greater than zero");
            return false;
        }*/

        if (bid_price>wallet_bal)
        {
            DialogBoxError.showError(ProductDetailsActivity.this, "Insufficient wallet balance");
            return false;
        }

        return true;
    }

    private void addOffers()
    {
        addProductOffer().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e("ProductDetailsActivity","onSuccess: " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if(response_msg.equals("offer added successfully")) {
                    Log.e("message", "offer added successfully");
                }
                else if(response_msg.equals("offer has been moved to checkout"))
                {
                    Log.e("message", "offer has been moved to checkout");
                }
                else
                {
                    //tv_buyNow.setText(place_bid);
                    if(progressBarSubmit.isShown()) {
                        progressBarSubmit.setVisibility(View.GONE);
                    }
                }

                if(response_msg.equals("offer already added"))
                {
                    DialogBoxError.showError(context, message);
                    cv_buyNow.setEnabled(true);
                    img_back.setEnabled(true);
                    tv_buyNow.setText(place_bid);
                }
                else if(response_msg.equals("offer added successfully"))
                {

                    Vibration.vibration(ProductDetailsActivity.this);

                    Constants.playSuccesSoundEffect(ProductDetailsActivity.this, Constants.click);

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


                    Intent intent=new Intent(ProductDetailsActivity.this, DashboardActivity.class);
                    intent.putExtra("type","MyOffers");
                    startActivity(intent);
                    finish();

                    Toast.makeText(ProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();

                }
                else if(response_msg.equals("downtime does not exist"))
                {
                    cv_buyNow.setEnabled(true);
                    img_back.setEnabled(true);
                    tv_buyNow.setText(place_bid);
                    DialogBoxError.showError(context, "Please try again");
                }
                else if(response_msg.equals("low wallet balance"))
                {
                    cv_buyNow.setEnabled(true);
                    img_back.setEnabled(true);
                    tv_buyNow.setText(place_bid);

                    DialogBoxError.showErrorBalance(context, message);
                }
                else if(response_msg.equals("product price changed"))
                {
                    cv_buyNow.setEnabled(true);
                    img_back.setEnabled(true);
                    tv_buyNow.setText(place_bid);
                    DialogBoxError.showError(context, message);
                }
                else if(response_msg.equals("product sold out"))
                {
                    cv_buyNow.setEnabled(true);
                    img_back.setEnabled(true);
                    tv_buyNow.setText(place_bid);

                    DialogBoxError.showError(context, message);
                }
                else if(response_msg.equals("product does not exist"))
                {
                    cv_buyNow.setEnabled(true);
                    img_back.setEnabled(true);
                    tv_buyNow.setText(place_bid);

                    DialogBoxError.showError(context, message);
                }
                else if(response_msg.equals("downtime expired"))
                {
                    cv_buyNow.setEnabled(true);
                    img_back.setEnabled(true);
                    tv_buyNow.setText(place_bid);

                    DialogBoxError.showError(context, message);
                }
                else if(response_msg.equals("offer has been moved to checkout"))
                {

                    Vibration.vibration(ProductDetailsActivity.this);

                    Constants.playSuccesSoundEffect(ProductDetailsActivity.this, Constants.click);

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

                    Intent intent=new Intent(ProductDetailsActivity.this, DashboardActivity.class);
                    intent.putExtra("type","MyOffers_Successfull");
                    startActivity(intent);
                    finish();

                    Toast.makeText(ProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    cv_buyNow.setEnabled(true);
                    img_back.setEnabled(true);
                    tv_buyNow.setText(place_bid);

                    DialogBoxError.showError(context, message);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cv_buyNow.setEnabled(true);
                img_back.setEnabled(true);
                tv_buyNow.setText(place_bid);
                if(progressBarSubmit.isShown()) {
                    progressBarSubmit.setVisibility(View.GONE);
                }
                Log.e("ProductDetailsActivity", "onFailure: " + e);

//                DialogBoxError.showError(ProductDetailsActivity.this, getString(R.string.something_went_wrong));

            }
        });
    }

    private Task<HashMap> addProductOffer()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("product_ref",product_ref);
        map.put("bid_price",Double.parseDouble(tv_bidPrice.getText().toString()));
        map.put("current_price",Double.parseDouble(current_price));
        map.put("downtime_ref",downtime_ref);

        return mFunctions.getHttpsCallable("addOffer").call(map)
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

    private void countDownTimer(String end_datetime)
    {
        current_time_milliseconds=System.currentTimeMillis();
        timer_difference=(Long.valueOf(end_datetime) * 1000)-current_time_milliseconds;

        timer = new CountDownTimer(timer_difference, 1000) {
            public void onTick(long leftTimeInMilliseconds) {
                Log.e("ProductDetailActivity","TimingProductDetailActivity: "+ String.valueOf(leftTimeInMilliseconds));
                int seconds = (int) (leftTimeInMilliseconds / 1000) % 60 ;
                int minutes = (int) ((leftTimeInMilliseconds / (1000*60)) % 60);
                int hours   = (int) ((leftTimeInMilliseconds / (1000*60*60)) % 24);

                tv_hour.setText(String.format("%02d", hours));
                tv_min.setText(String.format("%02d", minutes));
                tv_sec.setText(String.format("%02d", seconds));

                if (product_status.equals("live"))
                {
                    ll_timer.setVisibility(View.VISIBLE);
                    tv_nextPrice.setVisibility(View.GONE);
                    tv_nextPrice.setText("Price drop in");
                    cv_buyNow.setVisibility(View.VISIBLE);
                    arrow_down_bid.setVisibility(View.VISIBLE);
                    ll_buy.setVisibility(View.VISIBLE);
                }
                progressBottom.setVisibility(View.GONE);

            }

            public void onFinish() {
//                holder.tv_timer.setText("00:00:00");

                if (progressBottom.getVisibility()==View.GONE)
                {
                    progressBottom.setVisibility(View.VISIBLE);
                }

                tv_nextPrice.setVisibility(View.INVISIBLE);
                cv_buyNow.setVisibility(View.INVISIBLE);
                ll_timer.setVisibility(View.INVISIBLE);
                arrow_down_bid.setVisibility(View.INVISIBLE);

                isTimerFinish=true;

                if (product_status.equals("live"))
                {
                    getDownTimePrc(product_ref);
                }

                //getPrductDetails(product_ref);
            }
        }.start();
    }

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

        ProductAdapter.isCliclable=true;

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
                                    String product_ref_new = documentChange.getDocument().getId();

                                    Log.e(TAG, "product_ref_new: " + product_ref_new);

                                    if (product_ref.equals(product_ref_new))
                                    {



                                        if (!(product_status_new.equals(product_status)))
                                        {
                                            cancelTimer();


                                            if (DialogBoxError.checkInternetConnection(ProductDetailsActivity.this))
                                            {
                                                getPrductDetails(product_ref_new);
                                            }
                                            else
                                            {
                                                DialogBoxError.showInternetDialog(ProductDetailsActivity.this);
                                            }
                                        }
                                        /*if (!(current_price.equals(DoubleDecimal.twoPoints(current_price_new))))
                                        {
                                            tv_price.setText("RM "+DoubleDecimal.twoPoints(current_price_new));

                                        }*/
                                    }

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
//                    DialogBoxError.showError(ProductDetailsActivity.this, e.getMessage());
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

                //rl_viewpager.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,300));
                rl_viewpager.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,250));

                /*pager_product_images.setAdapter(null);
                viewPagerAdapter = new ProductDetailAdapter(ProductDetailsActivity.this,product_image,"true");
                pager_product_images.setAdapter(viewPagerAdapter);*/
                setAdapter("true");
            }
        },200);

        RelativeLayout.LayoutParams featuresParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        //featuresParams.topMargin=240;
        featuresParams.topMargin=210;

        rl_features.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        rl_features.setLayoutParams(featuresParams);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) features_scrollView.getLayoutParams();
        params.rightMargin = 10;
        params.bottomMargin = 110;
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
        if (height==250)
        {
            features_scrollView.fullScroll(View.FOCUS_UP);
            features_scrollView.smoothScrollTo(0,0);

            int rl_viewpager_height=rl_viewpager.getHeight();
            Log.e("rl_viewpager_height",""+rl_viewpager_height);

            //rl_viewpager.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,540));
            rl_viewpager.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,480));
            setAdapter("false");
            /*pager_product_images.setAdapter(null);
            viewPagerAdapter = new ProductDetailAdapter(ProductDetailsActivity.this,product_image,"false");
            pager_product_images.setAdapter(viewPagerAdapter);*/

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    RelativeLayout.LayoutParams features_params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                    //features_params.topMargin=480;
                    features_params.topMargin=420;
                    rl_features.setLayoutParams(features_params);

                    features_scrollView.fullScroll(View.FOCUS_UP);
                    features_scrollView.smoothScrollTo(0,0);

                }
            },10);
        }
    }
}