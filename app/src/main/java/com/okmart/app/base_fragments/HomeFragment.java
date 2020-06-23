package com.okmart.app.base_fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.activities.CreditCardActivity;
import com.okmart.app.activities.DashboardActivity;
import com.okmart.app.activities.FlashSeeMoreActivity;
import com.okmart.app.activities.ProductDetailsActivity;
import com.okmart.app.activities.ProductDetailsFlashActivity;
import com.okmart.app.activities.ReloadActivity;
import com.okmart.app.activities.SearchActivity;
import com.okmart.app.adapters.Category2Adapter;
import com.okmart.app.adapters.BannerAdapter;
import com.okmart.app.adapters.CategoryAdapter;
import com.okmart.app.adapters.FeaturedAdapter;
import com.okmart.app.adapters.FlashAdapter;
import com.okmart.app.adapters.ProductAdapter;
import com.okmart.app.model.CategoryData;
import com.okmart.app.model.FeaturedModel;
import com.okmart.app.model.ProductModel;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SharedPreferenceUtil;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context = getActivity();
    private FirebaseFunctions mFunctions;
    private String TAG = HomeFragment.class.getSimpleName();
    private FirebaseFirestore db;
    private TextView walletPoint;
    private ViewPager pager_product_images;
    private ViewPager pager_product_category;
    private ArrayList<String> images = new ArrayList<>();
    private DotsIndicator dots_indicator;
    private DotsIndicator dots_indicator_category;
    private RecyclerView rv_flash, rv_Featured,rv_product,rv_category;
    private List<CategoryData> categoryDataList=new ArrayList<>();

    private List<ProductModel> flashDataList=new ArrayList<>();
    private List<FeaturedModel> featuredDataList=new ArrayList<>();
    private List<ProductModel> productDataList=new ArrayList<ProductModel>();
    private String end_datetime;
    private FlashAdapter flashAdapter;
    private ProductAdapter productAdapter;
    private FeaturedAdapter featuredAdapter;
    private String category_ref="all";
    private ArrayList al;
    private ProgressBar progressBar, progressBarCategory,progressBarProduct;
    private NestedScrollView scrollView;
    private float height, height2;
    //    private CollapsingToolbarLayout collapsing_toolbar;
//    private AppBarLayout appbar;
    private ImageView background;
    private SwipeRefreshLayout swifeRefreshLayout;
    private RelativeLayout flash_section;
    private TextView see_more;
    private EditText search_product;
    private ProgressBar progressBarWalletPoints;

    private SharedPreferenceUtil sharedPreferenceUtil;

    private Category2Adapter category2Adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferenceUtil = new SharedPreferenceUtil(getContext());

        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        initXml(view);

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            getContactDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {

                    Log.e(TAG, "onSuccess:userDetails " + hashMap);
                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        HashMap<String,Object> share_url = (HashMap) hashMap.get("data");

                        sharedPreferenceUtil.setString("share_url", share_url.get("share_url").toString());
                        sharedPreferenceUtil.save();
                    }
                    /*else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(getActivity(),message);
                    }
                    else
                    {
                        DialogBoxError.showError(getActivity(),message);
                    }*/

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(getActivity(), e.getMessage());
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
        }



        if (getArguments()!=null)
        {
            if(getArguments().getString("product_ref")!=null)
            {
                if (getArguments().getString("product_ref").equals(""))
                {
                    Log.e("Response", "Normal Flow");
                }
                else
                {


                    ProgressDialog dialog = new ProgressDialog(getContext());
                    dialog.setMessage("Please wait ...");
                    dialog.setCancelable(false);
                    dialog.show();

                    productDetailRetrieve(getArguments().getString("product_ref")).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                        @Override
                        public void onSuccess(HashMap hashMap) {
                            Log.e(TAG, "onSuccess:userDetails " + hashMap);

                            if(dialog.isShowing()) {
                                dialog.cancel();
                            }

                            String response_msg = hashMap.get("response_msg").toString();
                            String message = hashMap.get("message").toString();

                            if (response_msg.equals("success"))
                            {
                                String success = hashMap.get("success").toString();
                                if(success.equals("true"))
                                {
                                    HashMap<String,Object> productDetails = (HashMap) hashMap.get("productDetails");

                                    String is_flash = productDetails.get("is_flash").toString();
                                    if(is_flash.equals("true")) {

                                        Intent intent = new Intent(getContext(), ProductDetailsFlashActivity.class);
                                        intent.putExtra("product_ref", getArguments().getString("product_ref"));
                                        startActivity(intent);

                                    }
                                    else {

                                        Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                                        intent.putExtra("product_ref", getArguments().getString("product_ref"));
                                        startActivity(intent);

                                    }
                                }
                            }
                            else if (response_msg.equals(Constants.unauthorized))
                            {
                                DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                            }
                            else if (response_msg.equals(Constants.under_maintenance))
                            {
                                DialogBoxError.showError(getActivity(),message);
                            }
                            else
                            {
                                DialogBoxError.showError(getActivity(),message);
                            }

                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(dialog.isShowing()) {
                                dialog.cancel();
                            }

                            Log.e(TAG, "onFailure: " + e);
                            DialogBoxError.showError(getActivity(), e.getMessage());
                        }
                    });

                }
            }
        }



        search_product = view.findViewById(R.id.search_product);
        search_product.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    //handle your situation here
                    Intent intent = new Intent(getContext(), SearchActivity.class);

                    ArrayList<ProductModel> new_list = new ArrayList<>();
                    new_list.addAll(productDataList);
                    new_list.addAll(flashDataList);

                    intent.putExtra("productDataList", (Serializable) new_list);
//                    intent.putExtra("productDataListFiltered", (Serializable) new_list);
//                    intent.putExtra("flashDataList", (Serializable) flashDataList);
//                    intent.putExtra("flashDataListFiltered", (Serializable) flashDataList);
                    startActivity(intent);
                }
            }
        });


        see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FlashSeeMoreActivity.class);
                startActivity(intent);
            }
        });

        height = DashboardActivity.navigation.getHeight();
        height2 = DashboardActivity.shadow.getHeight();
        RelativeLayout section = view.findViewById(R.id.section);
        section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ReloadActivity.class);
                startActivity(intent);
            }
        });

        scrollView = view.findViewById(R.id.scrollView);

        pager_product_images = view.findViewById(R.id.pager_product_images);
        pager_product_category = view.findViewById(R.id.pager_product_category);
        pager_product_category.setOffscreenPageLimit(1);

        getSetData();


        pager_product_category.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("position", String.valueOf(position));
                Log.e("position", String.valueOf(position));
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("position", String.valueOf(position));
                if (position==0)
                {
                    pager_product_category.setAdapter(null);
                    pager_product_category.setAdapter(category2Adapter);
                }
                else
                {
                    category2Adapter.getViewPagerPosition(position);
                }
                //category2Adapter.getViewPagerPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                Log.e("position", String.valueOf(state));

            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        search_product.clearFocus();
    }

    private void initXml(View view)
    {
//        collapsing_toolbar = view.findViewById(R.id.collapsing_toolbar);
//        appbar = view.findViewById(R.id.appbar);
        progressBarWalletPoints = view.findViewById(R.id.progressBarWalletPoints);
        see_more = view.findViewById(R.id.see_more);
        flash_section = view.findViewById(R.id.flash_section);
        progressBar = view.findViewById(R.id.progressBar);
        progressBarCategory = view.findViewById(R.id.progressBarCategory);
        progressBarProduct = view.findViewById(R.id.progressBarProduct);
        walletPoint = view.findViewById(R.id.walletPoint);
        dots_indicator = view.findViewById(R.id.dots_indicator);
        dots_indicator_category = view.findViewById(R.id.dots_indicator_category);
        rv_flash = view.findViewById(R.id.rv_flash);
        rv_Featured = view.findViewById(R.id.rv_Featured);
        rv_product = view.findViewById(R.id.rv_product);
        rv_category = view.findViewById(R.id.rv_category);
        swifeRefreshLayout = view.findViewById(R.id.swifeRefreshLayout);
        swifeRefreshLayout.setOnRefreshListener(this);

//        rv_flash.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));
        rv_flash.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        rv_Featured.setLayoutManager(new LinearLayoutManager(context));
        rv_product.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        rv_category.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));


        setAdapter();
    }

    private void getSetData()
    {
        if (getActivity()!=null)
        {
            if (DialogBoxError.checkInternetConnection(getActivity()))
            {
                getWalletPointOfUser().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e(TAG, "onSuccess: " + hashMap);

                        if(progressBarWalletPoints.isShown()) {

                            progressBarWalletPoints.setVisibility(View.GONE);
                        }
                        String response_msg = hashMap.get("response_msg").toString();
                        String message = hashMap.get("message").toString();

                        if (response_msg.equals("success"))
                        {
                            walletPoint.setText(DoubleDecimal.twoPointsComma(hashMap.get("walletPoint").toString()));
//                        walletPoint.setText(DoubleDecimal.twoPoints(hashMap.get("walletPoint").toString()));

                        }
                        /*else if (response_msg.equals(Constants.unauthorized))
                        {
                            DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                        }
                        else if (response_msg.equals(Constants.under_maintenance))
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }
                        else
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }*/


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(progressBarWalletPoints.isShown()) {

                            progressBarWalletPoints.setVisibility(View.GONE);
                        }
                        Log.e(TAG, "onFailure: " + e);

                    }
                });

                getBannerList().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e(TAG, "onSuccess: " + hashMap);

                        progressBar.setVisibility(View.GONE);
                        images.clear();

                        String response_msg = hashMap.get("response_msg").toString();
                        String message = hashMap.get("message").toString();

                        if (response_msg.equals("success"))
                        {
                            ArrayList al = (ArrayList) hashMap.get("data");

                            for (int i = 0; i < al.size(); i++) {

                                HashMap data = (HashMap) al.get(i);
                                String image = data.get("image").toString();

                                images.add(image);

                            }

                            pager_product_images.setAdapter(new BannerAdapter(getActivity(), images));
                            dots_indicator.setViewPager(pager_product_images);

                            pager_product_images.setVisibility(View.VISIBLE);
                            dots_indicator.setVisibility(View.VISIBLE);
                        }
                        /*else if (response_msg.equals(Constants.unauthorized))
                        {
                            DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                        }
                        else if (response_msg.equals(Constants.under_maintenance))
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }
                        else
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }*/



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
                DialogBoxError.showInternetDialog(getActivity());
            }
        }
    }

    private void setAdapter()
    {
        if (getActivity()!=null)
        {
            if (DialogBoxError.checkInternetConnection(getActivity()))
            {
                flash();
                featured(category_ref);
                category();
                products(category_ref);
                getSoldProduct();

                //flashObservers();
            }
            else
            {
                DialogBoxError.showInternetDialog(getActivity());
            }
        }
    }

    public void flash() {
        if (getActivity()!=null)
        {
            if (DialogBoxError.checkInternetConnection(getActivity()))
            {
                productListRetrieveFlash().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e(TAG, "onSuccess: " + hashMap);

                        flash_section.setVisibility(View.VISIBLE);

//                        if (swifeRefreshLayout.isRefreshing())
//                        {
//                            swifeRefreshLayout.setRefreshing(false);
//                        }

                        flashDataList.clear();
//                        progressBarProduct.setVisibility(View.GONE);

                        String response_msg = hashMap.get("response_msg").toString();
                        String message = hashMap.get("message").toString();

                        if (response_msg.equals("success"))
                        {
                            HashMap hashmap_current_time = (HashMap) hashMap.get("current_time");

                            String current_time = hashmap_current_time.get("_seconds").toString();

                            ArrayList arrayList = (ArrayList) hashMap.get("productData");
                            int size = arrayList.size();
                            if(size == 0) {
                                flash_section.setVisibility(View.GONE);
                            }
                            if(size>3) {
                                size = 3;
                            }
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
                                flashModel.setIs_flash(data.get("is_flash").toString());
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

//                            String flash_down_second = data.get("flash_down_second").toString();
//                            String down_per_price = data.get("down_per_price").toString();
//                            String min_price = data.get("min_price").toString();
//                            String flash_sale_order_time = hashmap_flash_sale_order_time.get("_seconds").toString();
//                            String current_price_ = data.get("current_price").toString();
//
//                            long current_time_milliseconds=System.currentTimeMillis();
//                            long end_datetime_milli = Long.parseLong(flash_sale_order_time) * 1000;
//                            long timer_difference=current_time_milliseconds-end_datetime_milli;
//                            long flash_down_second_milli = Long.parseLong(flash_down_second)*1000;
//                            long n = (timer_difference) / flash_down_second_milli;
//                            current_price_ = String.valueOf(Long.parseLong(current_price_) - (n*Long.parseLong(down_per_price)));
//                            flashModel.setCurrent_price(current_price_);
                                flashModel.setIs_resetOrderTime("false");
                                flashDataList.add(flashModel);

                            }

//                        Comparator<FlashModel> comparator = Comparator.comparing(e -> e.getProduct_status_number());
//                        productDataList.sort(comparator.reversed());

                            flashAdapter = new FlashAdapter(getActivity(), flashDataList, current_time);
                            rv_flash.setAdapter(flashAdapter);
                            rv_flash.setVisibility(View.VISIBLE);

                            if (getActivity()!=null)
                            {
                                if (DialogBoxError.checkInternetConnection(getActivity()))
                                {
                                    flashObservers();
                                }
                                else
                                {
                                    DialogBoxError.showInternetDialog(getActivity());
                                }
                            }
                        }
                        else if (response_msg.equals(Constants.unauthorized))
                        {
                            DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                        }
                        else if (response_msg.equals(Constants.under_maintenance))
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }
                        else
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        progressBarProduct.setVisibility(View.GONE);
                        Log.e(TAG, "onFailure: " + e);
                    }
                });
            }
            else
            {
                DialogBoxError.showInternetDialog(getActivity());
            }
        }
    }

    public void featured(String cat_ref) {
        if (getActivity()!=null)
        {
            if (DialogBoxError.checkInternetConnection(getActivity()))
            {
                category_ref=cat_ref;
                productListRetrieveFeatured(cat_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e(TAG, "onSuccess: " + hashMap);

                        if (swifeRefreshLayout.isRefreshing())
                        {
                            swifeRefreshLayout.setRefreshing(false);
                        }

                        featuredDataList.clear();

                        String response_msg = hashMap.get("response_msg").toString();
                        String message = hashMap.get("message").toString();

                        if (response_msg.equals("success"))
                        {
                            ArrayList al = (ArrayList) hashMap.get("productData");
                            for(int i = 0 ; i<al.size() ; i++)
                            {
                                HashMap data = (HashMap) al.get(i);
                                FeaturedModel featuredModel = new FeaturedModel();

                                featuredModel.setProduct_name(CapitalUtils.capitalize(data.get("product_name").toString()));
                                featuredModel.setProduct_ref(data.get("product_ref").toString());
                                featuredModel.setS_price(data.get("s_price").toString());
                                featuredModel.setCurrent_price(data.get("current_price").toString());
                                featuredModel.setProduct_image(data.get("product_image").toString());
                                featuredModel.setProduct_status(data.get("product_status").toString());
                                featuredModel.setQuantity(Integer.parseInt(data.get("quantity").toString()));

                                HashMap end_selling_time = (HashMap) data.get("end_selling_time");
                                end_datetime = end_selling_time.get("_seconds").toString();
                                featuredModel.set_seconds(end_datetime);

                                featuredDataList.add(featuredModel);

                            }

                            featuredAdapter=new FeaturedAdapter(getActivity(), featuredDataList);
                            rv_Featured.setAdapter(featuredAdapter);
                            rv_Featured.setVisibility(View.VISIBLE);

                            if (getActivity()!=null)
                            {
                                if (DialogBoxError.checkInternetConnection(getActivity()))
                                {
                                    featuredObservers();
                                }
                                else
                                {
                                    DialogBoxError.showInternetDialog(getActivity());
                                }
                            }
                        }
                        else if (response_msg.equals(Constants.unauthorized))
                        {
                            DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                        }
                        else if (response_msg.equals(Constants.under_maintenance))
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }

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
                DialogBoxError.showInternetDialog(getActivity());
            }
        }
    }

    private void category() {

        if (getActivity()!=null)
        {
            if (DialogBoxError.checkInternetConnection(getActivity()))
            {
                categoryRetrieve().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e(TAG, "onSuccess: " + hashMap);

                        categoryDataList.clear();
                        progressBarCategory.setVisibility(View.GONE);

                        String response_msg = hashMap.get("response_msg").toString();
                        String message = hashMap.get("message").toString();

                        if (response_msg.equals("success"))
                        {
                            ArrayList al = (ArrayList) hashMap.get("categoryData");
                            for (int i = 0 ; i < al.size() ; i++) {
                                HashMap data = (HashMap) al.get(i);

                                CategoryData categoryData = new CategoryData();

                                categoryData.setName(CapitalUtils.capitalize(data.get("name").toString()));
                                categoryData.setCat_icon(data.get("cat_icon").toString());
                                categoryData.setCat_selected_icon(data.get("cat_selected_icon").toString());
                                categoryData.setCategory_ref(data.get("category_ref").toString());

                                if(category_ref.equalsIgnoreCase(data.get("category_ref").toString()))//i == 0
                                {
                                    categoryData.setCheck(true);
                                }
                                else
                                {
                                    categoryData.setCheck(false);
                                }

                                categoryDataList.add(categoryData);
                            }

                            rv_category.setVisibility(View.VISIBLE);
                            pager_product_category.setVisibility(View.VISIBLE);
                            dots_indicator_category.setVisibility(View.VISIBLE);

                            rv_category.setAdapter(new CategoryAdapter(getActivity(),categoryDataList,HomeFragment.this));
                            Constants.isClick="";
                            Constants.category_ref="";
                            category2Adapter = new Category2Adapter(getActivity(), categoryDataList, HomeFragment.this);
                            pager_product_category.setAdapter(category2Adapter);
                            dots_indicator_category.setViewPager(pager_product_category);

//                        SnapHelper snapHelper = new PagerSnapHelper();
//                        snapHelper.attachToRecyclerView(rv_category);

//                        PagerSnapHelper snapHelper = new PagerSnapHelper();
//                        snapHelper.attachToRecyclerView(rv_category);
//
//                        rv_category.addItemDecoration(new LinePagerIndicatorDecoration());
                        }
                        else if (response_msg.equals(Constants.unauthorized))
                        {
                            DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                        }
                        else if (response_msg.equals(Constants.under_maintenance))
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }
                        else
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarCategory.setVisibility(View.GONE);
                        Log.e(TAG, "onFailure: " + e);
                    }
                });
            }
            else
            {
                DialogBoxError.showInternetDialog(getActivity());
            }
        }
    }

    public void products(String cat_ref) {
        if (getActivity()!=null)
        {
            if (DialogBoxError.checkInternetConnection(getActivity()))
            {
                category_ref=cat_ref;

                productListRetrieve(cat_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(HashMap hashMap) {
                        Log.e(TAG, "onSuccess: " + hashMap);

                        if (swifeRefreshLayout.isRefreshing())
                        {
                            swifeRefreshLayout.setRefreshing(false);
                        }

                        productDataList.clear();
                        progressBarProduct.setVisibility(View.GONE);

                        String response_msg = hashMap.get("response_msg").toString();
                        String message = hashMap.get("message").toString();

                        if (response_msg.equals("success"))
                        {
                            al = (ArrayList) hashMap.get("productData");
                            for(int i = 0 ; i<al.size() ; i++)
                            {
                                HashMap data = (HashMap) al.get(i);
                                ProductModel productModel = new ProductModel();

                                productModel.setProduct_name(CapitalUtils.capitalize(data.get("product_name").toString()));
                                productModel.setProduct_ref(data.get("product_ref").toString());
                                productModel.setS_price(data.get("s_price").toString());
                                productModel.setCurrent_price(data.get("current_price").toString());
                                productModel.setProduct_image(data.get("product_image").toString());
                                productModel.setFeatures(data.get("features").toString());
                                productModel.setProduct_status(data.get("product_status").toString());
                                productModel.setIs_flash(data.get("is_flash").toString());
                                productModel.setQuantity(Integer.parseInt(data.get("quantity").toString()));

                                if (data.get("product_status").toString().equals("live")) {

                                    HashMap end_selling_time = (HashMap) data.get("end_selling_time");
                                    end_datetime = end_selling_time.get("_seconds").toString();
                                    productModel.set_seconds(end_datetime);
                                    productModel.setProduct_status_number(4);

                                } else if (data.get("product_status").toString().equals("upcoming")) {

                                    HashMap end_selling_time = (HashMap) data.get("start_selling_time");
                                    end_datetime = end_selling_time.get("_seconds").toString();
                                    productModel.set_seconds(end_datetime);
                                    productModel.setProduct_status_number(3);

                                } else if (data.get("product_status").toString().equals("sold")) {

                                    HashMap end_selling_time = (HashMap) data.get("end_selling_time");
                                    end_datetime = end_selling_time.get("_seconds").toString();
                                    productModel.set_seconds(end_datetime);
                                    productModel.setProduct_status_number(2);

                                } else if (data.get("product_status").toString().equals("expired")) {

                                    HashMap end_selling_time = (HashMap) data.get("end_selling_time");
                                    end_datetime = end_selling_time.get("_seconds").toString();
                                    productModel.set_seconds(end_datetime);
                                    productModel.setProduct_status_number(1);

                                }

                                productDataList.add(productModel);

                            }

                            Comparator<ProductModel> comparator = Comparator.comparing(e -> e.getProduct_status_number());
                            productDataList.sort(comparator.reversed());

                            productAdapter=new ProductAdapter(getActivity(), productDataList);
                            rv_product.setAdapter(productAdapter);
                            rv_product.setVisibility(View.VISIBLE);

                            if (getActivity()!=null)
                            {
                                if (DialogBoxError.checkInternetConnection(getActivity()))
                                {
                                    observers();
                                }
                                else
                                {
                                    DialogBoxError.showInternetDialog(getActivity());
                                }
                            }
                        }
                        else if (response_msg.equals(Constants.unauthorized))
                        {
                            DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                        }
                        else if (response_msg.equals(Constants.under_maintenance))
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }
                        else
                        {
                            DialogBoxError.showError(getActivity(),message);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarProduct.setVisibility(View.GONE);
                        Log.e(TAG, "onFailure: " + e);
                    }
                });
            }
            else
            {
                DialogBoxError.showInternetDialog(getActivity());
            }
        }
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

    private Task<HashMap> getBannerList() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getBannerList").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
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

    private Task<HashMap> productListRetrieveFeatured(String cat_ref) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("catref", cat_ref);
        data.put("product_type", "featured");

        return mFunctions.getHttpsCallable("productListRetrieve").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> categoryRetrieve() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("categoryRetrieve").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> productListRetrieve(String cat_ref) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("catref", cat_ref);

        return mFunctions.getHttpsCallable("productListRetrieve").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopCountdown();

    }

    public void stopCountdown() {
        if (productAdapter!=null)
        {
            productAdapter.closeTimer();
        }
        if (featuredAdapter!=null)
        {
            featuredAdapter.closeTimer();
        }
    }

    ListenerRegistration registration;

    private void flashObservers()
    {

        db = null;
        db = FirebaseFirestore.getInstance();


        Query query = db.collection("products").whereEqualTo("is_active",true);

        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            Log.e(TAG, "onEventObserverFlash: " + documentChange.getDocument().getData());

                            HashMap hashMap = (HashMap) documentChange.getDocument().getData();
                            String product_status_new = hashMap.get("product_status").toString();
                            String current_price_new = hashMap.get("current_price").toString();
                            String flash_sale_order_time_new= hashMap.get("flash_sale_order_time").toString();
                            String product_ref_new = documentChange.getDocument().getId();
                            Log.e(TAG, "product_ref_new: " + product_ref_new);

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
                                        Log.e(TAG, "onEvent:Matched4444");

                                        //Timestamp(seconds=1592289323, nanoseconds=415000000)
                                        String rep1=flash_sale_order_time_new.replace("Timestamp(seconds=","");

                                        String[] split=rep1.split(",");
                                        String new_flash_sale_order_time=split[0];


                                        if (!(new_flash_sale_order_time.equals(flash_sale_order_time)))
                                        {

                                            if (is_flash.equalsIgnoreCase("true"))
                                            {
                                                if (flashAdapter!=null)
                                                {
                                                    flashAdapter.closeTimer();
                                                }
                                                flash();
                                                closeListner();
                                            }


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


//        db.collection("products")
//                .whereEqualTo("is_active",true)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        if (e != null)
//                        {
//                            Log.e(TAG, "listen:error", e);
//                            return;
//                        }
//                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges())
//                        {
//                            switch (documentChange.getType())
//                            {
//                                case MODIFIED:
////                                    Log.e(TAG, "onEventObserver: " + documentChange.getDocument().getData());
//
//                                    HashMap hashMap = (HashMap) documentChange.getDocument().getData();
//                                    String product_status_new = hashMap.get("product_status").toString();
//                                    String current_price_new = hashMap.get("current_price").toString();
//                                    String flash_sale_order_time_new = hashMap.get("flash_sale_order_time").toString();
//                                    String product_ref_new = documentChange.getDocument().getId();
//                                    Log.e(TAG, "product_ref_new: " + product_ref_new);
//
//                                    String is_flash = hashMap.get("is_flash").toString();
//                                    String is_featured = hashMap.get("is_featured").toString();
//                                    String new_quantity = hashMap.get("quantity").toString();
//
//                                    Log.e(TAG, "new_quantity: " + new_quantity);
//
//                                    for (int i = 0; i < flashDataList.size(); i++)
//                                    {
//
//                                        String product_ref = flashDataList.get(i).getProduct_ref();
//                                        String product_status = flashDataList.get(i).getProduct_status();
//                                        String current_price = flashDataList.get(i).getCurrent_price();
//                                        String flash_sale_order_time = flashDataList.get(i).getFlash_sale_order_time();
//
//                                        String quantity=""+flashDataList.get(i).getQuantity();
//
//                                        Log.e(TAG, "onEventproductModel: " + product_ref);
//
//                                        if (product_ref_new.equals(product_ref))
//                                        {
//                                            Log.e(TAG, "onEvent:Matched4444");
//
//                                            if (!(flash_sale_order_time_new.equals(flash_sale_order_time)))
//                                            {
//
//                                                if (is_flash.equalsIgnoreCase("true"))
//                                                {
//                                                    if (flashAdapter!=null)
//                                                    {
//                                                        flashAdapter.closeTimer();
//                                                    }
//                                                    flash();
//                                                }
//
//                                                break;
//                                            }
//
//                                            if (!(product_status_new.equals(product_status)))
//                                            {
//
//                                                if (is_flash.equalsIgnoreCase("true"))
//                                                {
//                                                    if (flashAdapter!=null)
//                                                    {
//                                                        flashAdapter.closeTimer();
//                                                    }
//                                                    flash();
//                                                }
//
//                                                break;
//                                            }
//
////                                            else if (!(new_quantity.equals(quantity)))
////                                            {
////                                                int newQuantity = Integer.parseInt(new_quantity);
////                                                int position=i;
////
//////                                                Log.e("ProductSold","product_ref:- "+product_ref_new+" ,newQuantity:- "+newQuantity);
////
////                                                productAdapter.productQuantity(position,newQuantity);
////
////                                            }
////
////                                            if (!(current_price.equals(current_price_new)))
////                                            {
////
////                                                int position = i;
////                                                productAdapter.currentPrice(position, current_price_new);
////
////                                            }
////                                            break;
//                                        }
//                                    }
//
//                                    break;
//                            }
//                        }
//                    }
//                });
    }


    private void closeListner() {
        registration.remove();
    }

    private void observers()
    {

        db = null;
        db = FirebaseFirestore.getInstance();

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
                                    Log.e(TAG, "onEventObserver: " + documentChange.getDocument().getData());

                                    HashMap hashMap = (HashMap) documentChange.getDocument().getData();
                                    String product_status_new = hashMap.get("product_status").toString();
                                    String current_price_new = hashMap.get("current_price").toString();
                                    String product_ref_new = documentChange.getDocument().getId();
                                    Log.e(TAG, "product_ref_new: " + product_ref_new);

                                    String is_featured = hashMap.get("is_featured").toString();
                                    String new_quantity = hashMap.get("quantity").toString();

                                    Log.e(TAG, "new_quantity: " + new_quantity);

                                    for (int i = 0; i < productDataList.size(); i++)
                                    {

                                        String product_ref = productDataList.get(i).getProduct_ref();
                                        String product_status = productDataList.get(i).getProduct_status();
                                        String current_price = productDataList.get(i).getCurrent_price();

                                        String quantity=""+productDataList.get(i).getQuantity();

                                        Log.e(TAG, "onEventproductModel: " + product_ref);
                                        if (product_ref_new.equals(product_ref))
                                        {
                                            Log.e(TAG, "onEvent:Matched");

                                            if (!(product_status_new.equals(product_status)))
                                            {
                                                productDataList.clear();

                                                if (productAdapter!=null)
                                                {
                                                    productAdapter.closeTimer();
                                                }

                                                products(category_ref);

                                                if (is_featured.equalsIgnoreCase("true"))
                                                {
                                                    if (featuredAdapter!=null)
                                                    {
                                                        featuredAdapter.closeTimer();
                                                    }
                                                    featured(category_ref);
                                                }

                                                break;
                                            }
                                            else if (!(new_quantity.equals(quantity)))
                                            {
                                                int newQuantity = Integer.parseInt(new_quantity);
                                                int position=i;

//                                                Log.e("ProductSold","product_ref:- "+product_ref_new+" ,newQuantity:- "+newQuantity);

                                                productAdapter.productQuantity(position,newQuantity);

                                            }

                                            if (!(current_price.equals(current_price_new)))
                                            {

                                                int position = i;
                                                productAdapter.currentPrice(position, current_price_new);

                                            }
                                            break;
                                        }
                                    }

                                    break;
                            }
                        }
                    }
                });
    }

    //    featured observer
    private void featuredObservers()
    {
        db.collection("products")
                .whereEqualTo("is_featured",true)
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
                                    Log.e(TAG, "FeaturedObserverData: " + documentChange.getDocument().getData());

                                    HashMap hashMap = (HashMap) documentChange.getDocument().getData();
                                    String product_status_new = hashMap.get("product_status").toString();
                                    String current_price_new = hashMap.get("current_price").toString();
                                    String product_ref_new = documentChange.getDocument().getId();

                                    String new_quantity = hashMap.get("quantity").toString();

                                    Log.e(TAG, "product_ref_new: " + product_ref_new);

                                    for (int i = 0; i < featuredDataList.size(); i++)
                                    {

                                        String product_ref = featuredDataList.get(i).getProduct_ref();
                                        String product_status = featuredDataList.get(i).getProduct_status();
                                        String current_price = featuredDataList.get(i).getCurrent_price();

                                        String quantity  = ""+featuredDataList.get(i).getQuantity();

                                        Log.e(TAG, "product_ref: " + product_ref);

                                        if (product_ref_new.equals(product_ref))
                                        {
                                            if (!(product_status_new.equals(product_status)))
                                            {
                                                if (featuredAdapter!=null)
                                                {
                                                    featuredAdapter.closeTimer();
                                                }
                                                featured(category_ref);

                                                if (productAdapter!=null)
                                                {
                                                    productAdapter.closeTimer();
                                                }

                                                products(category_ref);

                                                break;
                                            }
                                            else if (!(new_quantity.equals(quantity)))
                                            {

                                                int newQuantity = Integer.parseInt(new_quantity);
                                                int position=i;

//                                                Log.e("ProductSold","product_ref:- "+product_ref_new+" ,newQuantity:- "+newQuantity);

                                                featuredAdapter.productQuantity(position,newQuantity);

                                            }
                                            break;
                                        }
                                    }

                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    public void onRefresh()
    {
        swifeRefreshLayout.setRefreshing(false);
        pager_product_images.setVisibility(View.GONE);
        dots_indicator.setVisibility(View.GONE);
        flash_section.setVisibility(View.INVISIBLE);
        rv_flash.setVisibility(View.GONE);
        rv_category.setVisibility(View.GONE);
        rv_Featured.setVisibility(View.GONE);
        rv_product.setVisibility(View.GONE);
        pager_product_category.setVisibility(View.GONE);
        dots_indicator_category.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
        progressBarProduct.setVisibility(View.VISIBLE);

        if (getActivity()!=null)
        {
            if (DialogBoxError.checkInternetConnection(getActivity()))
            {
                getSetData();
                flash();
                featured(category_ref);
                category();
                products(category_ref);
            }
            else
            {
                DialogBoxError.showInternetDialog(getActivity());
            }
        }
    }

    private void getSoldProduct()
    {

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        db.collection("my_orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e!=null)
                        {
                            Log.e("Error",e.getMessage());
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges())
                        {
                            switch (dc.getType())
                            {
                                case ADDED:

//                                    Log.e("ProductSold ",""+dc.getDocument().getData());

                                    HashMap hashMap = (HashMap) dc.getDocument().getData();

                                    HashMap product_details = (HashMap)hashMap.get("product_details");

//                                    try {
                                    String product_ref =hashMap.get("product_ref").toString();

                                    String created= hashMap.get("created").toString();

                                    //created=Timestamp(seconds=1581936182, nanoseconds=206000000)
                                    String rep1=created.replace("Timestamp(","");
                                    String rep2=rep1.replace(")","");
                                    String[] split=rep2.split(",");
                                    String seconds=split[0];
                                    String sec=seconds.replace("seconds=","");

                                    boolean isCreateDate=getDate(Long.parseLong(sec));
                                    if (isCreateDate)
                                    {
                                        try
                                        {
                                            String is_featured = product_details.get("is_featured").toString();
//                                            Log.e("is_featured",is_featured);
                                            if (is_featured.equalsIgnoreCase("true"))
                                            {
                                                for (int i=0;i<featuredDataList.size();i++)
                                                {
                                                    if (featuredDataList.get(i).getProduct_ref().equalsIgnoreCase(product_ref))
                                                    {
                                                        featuredAdapter.soldProduct(i,hashMap.get("bid_price").toString());
                                                        break;
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                for (int i=0;i<productDataList.size();i++)
                                                {
                                                    if (productDataList.get(i).getProduct_ref().equalsIgnoreCase(product_ref))
                                                    {
                                                        productAdapter.soldProduct(i,hashMap.get("bid_price").toString());
                                                        break;
                                                    }
                                                }

                                            }
                                        }
                                        catch (Exception ex)
                                        {
                                            ex.printStackTrace();
                                        }
                                    }

//                                    `}
//                                    catch (Exception e1) {
//                                        Log.e("Response", e1.getMessage());
//                                    }`

                                    break;

                            }
                        }
                    }
                });
    }

    private boolean getDate(long time)
    {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        //String date1 = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        String date1 = DateFormat.format("dd-MM-yyyy hh:mm aa", cal).toString();

        Log.e("dateTime: ", date1);

        long current_time_milliseconds=System.currentTimeMillis();

        Calendar caln = Calendar.getInstance(Locale.ENGLISH);
        caln.setTimeInMillis(current_time_milliseconds);
        String c_date = DateFormat.format("dd-MM-yyyy hh:mm aa", caln).toString();

        Log.e("c_date: ", c_date);

        if ( date1.compareTo(c_date) >= 0 )
        {

            //  0 comes when two date are same,
            //  1 comes when date1 is higher then date2
            // -1 comes when date1 is lower then date2

            return true;

        }
        else
        {
            return false;
        }

    }




    private Task<HashMap>productDetailRetrieve(String product_ref)
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


    private Task<HashMap>getContactDetails() {
        Map<String, Object> map = new HashMap<>();
        map.put("contact", "socialshareurl");

        return mFunctions.getHttpsCallable("getContactDetails").call(map)
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (HashMap)task.getResult().getData();
                    }
                });
    }

}