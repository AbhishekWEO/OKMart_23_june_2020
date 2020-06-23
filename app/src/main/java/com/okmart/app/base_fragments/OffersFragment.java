package com.okmart.app.base_fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
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
import com.okmart.app.activities.DeliverNowActivity;
import com.okmart.app.activities.ReloadActivity;
import com.okmart.app.adapters.ActiveOffersAdapter;
import com.okmart.app.adapters.FailedOffersAdapter;
import com.okmart.app.adapters.SuccessfulOffersAdapter;
import com.okmart.app.model.ActiveOffersModel;
import com.okmart.app.model.FailedOffersModel;
import com.okmart.app.model.SuccessfulOffersModel;
import com.okmart.app.pagination.PaginationListener;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SecondsToDateTime;
import com.okmart.app.utilities.SharedPreferenceUtil;
import com.okmart.app.utilities.Vibration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OffersFragment extends Fragment implements View.OnClickListener {

    private FirebaseFunctions mFunctions;
    private String TAG = OffersFragment.class.getSimpleName();
    private Context context = getActivity();
    private FirebaseFirestore db;

    private TextView walletPoint,tv_active,tv_successful,tv_failed;
    private LinearLayout ll_active,ll_failed;
    public static LinearLayout ll_successful;
    private ImageView active_dot_image,successful_dot_image,failed_dot_image;
    private TextView no_active_offers, no_successful_offers, no_failed_offers;

    private RecyclerView rv_active_offers, rv_successful_offers, rv_failed_offers;

    public List<ActiveOffersModel> activeDataList=new ArrayList<>();
    private List<SuccessfulOffersModel> successfulDataList=new ArrayList<>();
    private List<FailedOffersModel> failedDataList=new ArrayList<>();
    private ActiveOffersAdapter activeOffersAdapter;
    private SuccessfulOffersAdapter successfulOffersAdapter;
    private FailedOffersAdapter failedOffersAdapter;
    private ProgressBar progressBarOfferActive, progressBarOfferSuccessful, progressBarOfferFailed;
    private SkeletonScreen skeletonScreen1, skeletonScreen2, skeletonScreen3;

    private String user_ref="";
    private int failed_count=1,successfull_count=1;
    private int flag1=0, flag2=0, flag3=0;

    private SwipeRefreshLayout swipeRefresh_active;

    //successfull pagination
    private SwipeRefreshLayout swipeRefresh_successful;
    public String checkout_ref="";
    private LinearLayoutManager lm_successful;
    private int successful_page_number = Constants.PAGE_START;
    private boolean successful_isLastPage = false;
    private boolean successful_isLoading = false;

    //failed pagination
    private SwipeRefreshLayout swipeRefresh_failed;
    private LinearLayoutManager lm_failed;
    private int failed_page_number=Constants.PAGE_START;
    private boolean failed_isLastPage = false;
    private boolean failed_isLoading = false;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private String currentTab="";
    private ProgressBar progressBarWalletPoints;


    public OffersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_offers, container, false);
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();


        sharedPreferenceUtil = new SharedPreferenceUtil(getActivity());

        // getUserDetails();

        initXml(view);
        /*if (getActivity()!=null)
        {
            if (DialogBoxError.checkInternetConnection(getActivity()))
            {
                activeOffers();
            }
            else
            {
                DialogBoxError.showInternetDialog(getActivity());
            }
        }*/



//        successfulOffers();
//        failedOffers();

        RelativeLayout section = view.findViewById(R.id.section);
        section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ReloadActivity.class);
                startActivity(intent);
            }
        });

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
//                    walletPoint.setText(DoubleDecimal.twoPoints(hashMap.get("walletPoint").toString()) + " PT");
                        walletPoint.setText(DoubleDecimal.twoPointsComma(hashMap.get("walletPoint").toString()));

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
                    DialogBoxError.showError(getActivity(), getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
        }



        setOnClickListener();
        return view;
    }

    private void initXml(View view)
    {

        progressBarWalletPoints = view.findViewById(R.id.progressBarWalletPoints);
        progressBarOfferActive= view.findViewById(R.id.progressBarOfferActive);
        progressBarOfferSuccessful= view.findViewById(R.id.progressBarOfferSuccessful);
        progressBarOfferFailed= view.findViewById(R.id.progressBarOfferFailed);

        walletPoint=(TextView) view.findViewById(R.id.walletPoint);

        ll_active=(LinearLayout)view.findViewById(R.id.ll_active);
        tv_active=(TextView) view.findViewById(R.id.tv_active);
        active_dot_image=(ImageView) view.findViewById(R.id.active_dot_image);
        rv_active_offers = view.findViewById(R.id.rv_active_offers);
        rv_active_offers.setLayoutManager(new LinearLayoutManager(context));
        no_active_offers = view.findViewById(R.id.no_active_offers);

        ll_successful=(LinearLayout)view.findViewById(R.id.ll_successful);
        tv_successful=(TextView) view.findViewById(R.id.tv_successful);
        successful_dot_image=(ImageView) view.findViewById(R.id.successful_dot_image);
        rv_successful_offers = view.findViewById(R.id.rv_successful_offers);
        //rv_successful_offers.setLayoutManager(new LinearLayoutManager(context));
        lm_successful=new LinearLayoutManager(context);
        rv_successful_offers.setLayoutManager(lm_successful);
        rv_successful_offers.setHasFixedSize(true);
        successfulOffersAdapter = new SuccessfulOffersAdapter(getActivity(), new ArrayList<>(),OffersFragment.this);
        rv_successful_offers.setAdapter(successfulOffersAdapter);
        no_successful_offers = view.findViewById(R.id.no_successful_offers);

        ll_failed=(LinearLayout)view.findViewById(R.id.ll_failed);
        tv_failed=(TextView) view.findViewById(R.id.tv_failed);
        failed_dot_image=(ImageView) view.findViewById(R.id.failed_dot_image);
        rv_failed_offers = view.findViewById(R.id.rv_failed_offers);
        //rv_failed_offers.setLayoutManager(new LinearLayoutManager(context));
        lm_failed = new LinearLayoutManager(context);
        rv_failed_offers.setLayoutManager(lm_failed);
        rv_failed_offers.setHasFixedSize(true);
        failedOffersAdapter = new FailedOffersAdapter(getActivity(), new ArrayList<>());
        rv_failed_offers.setAdapter(failedOffersAdapter);

        no_failed_offers = view.findViewById(R.id.no_failed_offers);

        swipeRefresh_active = view.findViewById(R.id.swipeRefresh_active);
        swipeRefresh_successful = view.findViewById(R.id.swipeRefresh_successful);
        swipeRefresh_failed = view.findViewById(R.id.swipeRefresh_failed);

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            pagination();
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
        }


        setOnRefreshListener();
        getsetData();
    }

    private void pagination()
    {
        rv_successful_offers.addOnScrollListener(new PaginationListener(lm_successful) {
            @Override
            public void loadMoreItems() {
                successful_isLoading=true;
                successful_page_number++;
                successfulOffers();
            }

            @Override
            public boolean isLastPage() {
                return successful_isLastPage;
            }

            @Override
            public boolean isLoading() {
                return successful_isLoading;
            }
        });

        //failed
        rv_failed_offers.addOnScrollListener(new PaginationListener(lm_failed) {
            @Override
            public void loadMoreItems() {
                failed_isLoading=true;
                failed_page_number++;
                failedOffers();
            }

            @Override
            public boolean isLastPage() {
                return failed_isLastPage;
            }

            @Override
            public boolean isLoading() {
                return failed_isLoading;
            }
        });
    }

    private void setOnRefreshListener()
    {

        swipeRefresh_active.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (DialogBoxError.checkInternetConnection(getActivity()))
                {
                    stopCountdown();

                    flag1 = 0;
                    activeOffers();
                }
                else
                {
                    swipeRefresh_active.setRefreshing(false);
                    DialogBoxError.showInternetDialog(getActivity());
                }

            }
        });

        swipeRefresh_successful.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (DialogBoxError.checkInternetConnection(getActivity()))
                {
                    successful_isLastPage=false;
                    successfulOffersAdapter.expandedPosition=-1;
                    successfulOffersAdapter.clear();
                    checkout_ref="";
                    successful_page_number=Constants.PAGE_START;
                    flag2=0;
                    successfulOffers();
                }
                else
                {
                    swipeRefresh_successful.setRefreshing(false);
                    DialogBoxError.showInternetDialog(getActivity());
                }

            }
        });

        swipeRefresh_failed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (DialogBoxError.checkInternetConnection(getActivity()))
                {
                    failed_isLastPage=false;
                    failedOffersAdapter.clear();
                    failed_page_number = Constants.PAGE_START;
                    flag3=0;
                    failedOffers();
                }
                else
                {
                    swipeRefresh_failed.setRefreshing(false);
                    DialogBoxError.showInternetDialog(getActivity());
                }

            }
        });
    }
    private void setOnClickListener()
    {
        ll_active.setOnClickListener(this);
        ll_successful.setOnClickListener(this);
        ll_failed.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Typeface rubik_regular=null,rubik_medium = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            rubik_medium = getActivity().getResources().getFont(R.font.rubik_medium);
            rubik_regular = getActivity().getResources().getFont(R.font.rubik_regular);
        }
        else
        {
            rubik_medium = ResourcesCompat.getFont(getActivity(), R.font.rubik_medium);
            rubik_regular = ResourcesCompat.getFont(getActivity(), R.font.rubik_regular);
        }

        switch (view.getId())
        {
            case R.id.ll_active:

                currentTab="active";

                Vibration.vibration(getActivity());

                flag1 = 0;

                no_active_offers.setVisibility(View.GONE);
                no_successful_offers.setVisibility(View.GONE);
                no_failed_offers.setVisibility(View.GONE);

                rv_active_offers.setVisibility(View.VISIBLE);
                //rv_successful_offers.setVisibility(View.GONE);
                swipeRefresh_active.setVisibility(View.VISIBLE);
                swipeRefresh_successful.setVisibility(View.GONE);
                //rv_failed_offers.setVisibility(View.GONE);
                swipeRefresh_failed.setVisibility(View.GONE);

                active_dot_image.setVisibility(View.VISIBLE);
                successful_dot_image.setVisibility(View.INVISIBLE);
                failed_dot_image.setVisibility(View.INVISIBLE);

                tv_active.setTypeface(rubik_medium);
                tv_successful.setTypeface(rubik_regular);
                tv_failed.setTypeface(rubik_regular);

                activeOffers();

                break;

            case R.id.ll_successful:

                currentTab="successful";

                Vibration.vibration(getActivity());

                successful_isLastPage=false;
                successfulOffersAdapter.expandedPosition=-1;
                successfulOffersAdapter.clear();
                checkout_ref="";
                successful_page_number=Constants.PAGE_START;
                flag2=0;



                no_active_offers.setVisibility(View.GONE);
                no_successful_offers.setVisibility(View.GONE);
                no_failed_offers.setVisibility(View.GONE);

                rv_active_offers.setVisibility(View.GONE);
                //rv_successful_offers.setVisibility(View.VISIBLE);
                swipeRefresh_active.setVisibility(View.GONE);
                swipeRefresh_successful.setVisibility(View.VISIBLE);
                //rv_failed_offers.setVisibility(View.GONE);
                swipeRefresh_failed.setVisibility(View.GONE);

                active_dot_image.setVisibility(View.INVISIBLE);
                successful_dot_image.setVisibility(View.VISIBLE);
                failed_dot_image.setVisibility(View.INVISIBLE);

                tv_active.setTypeface(rubik_regular);
                tv_successful.setTypeface(rubik_medium);
                tv_failed.setTypeface(rubik_regular);

                successfulOffers();

                break;

            case R.id.ll_failed:

                currentTab="failed";

                Vibration.vibration(getActivity());

                failed_isLastPage=false;
                failedOffersAdapter.clear();
                failed_page_number = Constants.PAGE_START;
                flag3=0;



                no_active_offers.setVisibility(View.GONE);
                no_successful_offers.setVisibility(View.GONE);
                no_failed_offers.setVisibility(View.GONE);

                rv_active_offers.setVisibility(View.GONE);
                //rv_successful_offers.setVisibility(View.GONE);
                swipeRefresh_active.setVisibility(View.GONE);
                swipeRefresh_successful.setVisibility(View.GONE);
                //rv_failed_offers.setVisibility(View.VISIBLE);
                swipeRefresh_failed.setVisibility(View.VISIBLE);

                active_dot_image.setVisibility(View.INVISIBLE);
                successful_dot_image.setVisibility(View.INVISIBLE);
                failed_dot_image.setVisibility(View.VISIBLE);

                tv_active.setTypeface(rubik_regular);
                tv_successful.setTypeface(rubik_regular);
                tv_failed.setTypeface(rubik_medium);

                failedOffers();

                break;
        }
    }


    public void realtimeupdations()
    {

        db.collection("my_orders")
                .whereEqualTo("user_ref", user_ref)//"NGSMzFHL5cnVoXgpUZ1r"
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
                                    if (successfull_count>successfulDataList.size())
                                    {
                                        Log.e(TAG, "onEventw: " + dc.getDocument().getData());
                                        successfulOffers();
                                    }
                                    successfull_count++;

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


    public void realtimeupdations2() {

        db.collection("past_biddings")
                .whereEqualTo("user_ref",user_ref)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e)
                    {
                        if (e!=null)
                        {
                            Log.e(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges())
                        {
                            switch (dc.getType())
                            {
                                case ADDED:
                                    if (failed_count>failedDataList.size())
                                    {
                                        Log.e(TAG, "onAdded: " + dc.getDocument().getData());
                                        failedOffers();
                                    }
                                    failed_count++;

                                    break;

                                case MODIFIED:

                                    Log.e(TAG, "onModified: " + dc.getDocument().getData());

                                    break;

                                case REMOVED:

                                    Log.e(TAG, "onRemoved: " + dc.getDocument().getData());

                                    break;
                            }
                        }
                    }
                });
    }


    private void activeOffers()
    {

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            if(flag1 == 0) {
//            progressBarOfferActive.setVisibility(View.VISIBLE);
                skeletonScreen1 = Skeleton.bind(this.rv_active_offers).adapter(activeOffersAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();
            }

            progressBarOfferSuccessful.setVisibility(View.GONE);
            progressBarOfferFailed.setVisibility(View.GONE);

            getActiveOffers().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "activeOffers: " + hashMap);

                    flag1 = 1;

//                if(progressBarOfferActive.isShown()) {
//                    progressBarOfferActive.setVisibility(View.GONE);
//                }

                    swipeRefresh_active.setRefreshing(false);

                    skeletonScreen1.hide();

                    activeDataList.clear();

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        ArrayList al = (ArrayList) hashMap.get("data");


                        if(al.size() == 0)
                        {
                            no_active_offers.setText("There are no active biddings.");

                            if (currentTab.equalsIgnoreCase("active"))
                            {
                                no_successful_offers.setVisibility(View.GONE);
                                no_failed_offers.setVisibility(View.GONE);
                                no_active_offers.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                no_active_offers.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            for (int i = 0; i < al.size(); i++) {

                                HashMap data = (HashMap) al.get(i);

                                ActiveOffersModel activeOffersModel = new ActiveOffersModel();
                                activeOffersModel.setDirect_purchase_price(DoubleDecimal.twoPoints(data.get("direct_purchase_price").toString()));
                                activeOffersModel.setDowntime_ref(data.get("downtime_ref").toString());
                                activeOffersModel.setOffer_ref(data.get("offer_ref").toString());
                                activeOffersModel.setActual_price(DoubleDecimal.twoPoints(data.get("actual_price").toString()));
                                activeOffersModel.setCurrent_price((data.get("current_price").toString()));
                                activeOffersModel.setBid_price(DoubleDecimal.twoPoints(data.get("bid_price").toString()));
                                activeOffersModel.setProduct_ref(data.get("product_ref").toString());
                                activeOffersModel.setUser_ref(data.get("user_ref").toString());

                                HashMap product_details = (HashMap) data.get("product_details");
                                activeOffersModel.setProduct_name(CapitalUtils.capitalize(product_details.get("product_name").toString()));
                                activeOffersModel.setImage_thumbnail(product_details.get("image_thumbnail").toString());

                                HashMap downtime_details = (HashMap) data.get("downtime_details");
                                HashMap end_selling_time = (HashMap) downtime_details.get("end_datetime");
                                String end_datetime = end_selling_time.get("_seconds").toString();
                                activeOffersModel.set_seconds(end_datetime);

                                HashMap start_selling_time = (HashMap) downtime_details.get("start_datetime");
                                String start_datetime = start_selling_time.get("_seconds").toString();
                                activeOffersModel.setStart_seconds(start_datetime);

                                if(data.get("is_edited").toString().equalsIgnoreCase("false") &&
                                        data.get("is_direct_checkout").toString().equalsIgnoreCase("false"))
                                {
                                    activeDataList.add(activeOffersModel);
                                }
                            }

                            if (activeDataList.size()>0)
                            {
                                activeOffersAdapter = new ActiveOffersAdapter(getActivity(), activeDataList,OffersFragment.this);
                                rv_active_offers.setAdapter(activeOffersAdapter);
                            }
                            else
                            {
                                no_active_offers.setText("There are no active biddings.");
                                no_active_offers.setVisibility(View.VISIBLE);
                            }

                            if (getActivity()!=null)
                            {
                                if (DialogBoxError.checkInternetConnection(getActivity()))
                                {
                                    getUserDetails();
                                }
                                else
                                {
                                    DialogBoxError.showInternetDialog(getActivity());
                                }
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

//                if(progressBarOfferActive.isShown()) {
//                    progressBarOfferActive.setVisibility(View.GONE);
//                }

                    skeletonScreen1.hide();

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
        }

    }

    private void successfulOffers()
    {

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            if(flag2 == 0) {
//            progressBarOfferSuccessful.setVisibility(View.VISIBLE);
                skeletonScreen2 = Skeleton.bind(this.rv_successful_offers).adapter(successfulOffersAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();
            }

//        progressBarOfferActive.setVisibility(View.GONE);
//        progressBarOfferFailed.setVisibility(View.GONE);

//        skeletonScreen1.hide();
//        skeletonScreen3.hide();

            getSuccessfulOffers().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {

                    flag2 = 1;
                    Log.e(TAG, "onSuccess123: " + hashMap);

//                if(progressBarOfferSuccessful.isShown()) {
//                    progressBarOfferSuccessful.setVisibility(View.GONE);
//                }
                    swipeRefresh_successful.setRefreshing(false);
                    if (successful_page_number==1)
                    {
                        skeletonScreen2.hide();
                    }


                    successfulDataList.clear();
                    successfull_count=1;

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        ArrayList al = (ArrayList) hashMap.get("data");

                        if(al.size() == 0)
                        {
                            if (successful_page_number==1)
                            {
                                //no_successful_offers.setVisibility(View.VISIBLE);
                                no_successful_offers.setText("There are no won biddings.");
                                swipeRefresh_successful.setVisibility(View.GONE);


                                if (currentTab.equalsIgnoreCase("successful"))
                                {
                                    no_active_offers.setVisibility(View.GONE);
                                    no_failed_offers.setVisibility(View.GONE);
                                    no_successful_offers.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    no_successful_offers.setVisibility(View.GONE);
                                }

                            }
                            else
                            {
                                successfulOffersAdapter.emptyData();
                                successful_page_number--;
                            }
                            successful_isLastPage = true;
                        }
                        else
                        {

                            for (int i = 0; i < al.size(); i++) {

                                HashMap data = (HashMap) al.get(i);

                                SuccessfulOffersModel successfulOffersModel = new SuccessfulOffersModel();

                                successfulOffersModel.setCurrent_price((data.get("current_price").toString()));
                                successfulOffersModel.setBid_price((data.get("bid_price").toString()));
                                successfulOffersModel.setIs_checkout(data.get("is_checkout").toString());
                                successfulOffersModel.setIs_outlet(data.get("is_outlet").toString());
                                successfulOffersModel.setProduct_ref(data.get("product_ref").toString());
                                successfulOffersModel.setCheckout_ref(data.get("checkout_ref").toString());
                                successfulOffersModel.setTransaction_id(data.get("transaction_id").toString());
                                successfulOffersModel.setShipping_price((data.get("shipping_price").toString()));
                                successfulOffersModel.setOrder_status(data.get("order_status").toString());
                                String order_status = data.get("order_status").toString();

                                HashMap product_details = (HashMap) data.get("product_details");
                                successfulOffersModel.setProduct_name(CapitalUtils.capitalize(product_details.get("product_name").toString()));
                                successfulOffersModel.setActual_price((product_details.get("s_price").toString()));
                                successfulOffersModel.setImage_thumbnail(product_details.get("image_thumbnail").toString());

                                HashMap bid_datetime = (HashMap) data.get("bid_datetime");
                                successfulOffersModel.set_seconds(SecondsToDateTime.conversion(bid_datetime.get("_seconds").toString()));

                                if((order_status.equals("processing")) || (order_status.equals("shipped")) || (order_status.equals("delivered")))
                                {
                                    HashMap address_details = (HashMap) data.get("address_details");

                                    if((data.get("is_outlet").toString()).equals("true"))
                                    {
                                        successfulOffersModel.setName(CapitalUtils.capitalize(address_details.get("outlet_name").toString()));
                                    }
                                    else
                                    {
                                        successfulOffersModel.setName(CapitalUtils.capitalize(address_details.get("name").toString()));
                                    }
                                    successfulOffersModel.setAddress_line_1(address_details.get("address_line1").toString());
                                    successfulOffersModel.setAddress_line_2(address_details.get("address_line2").toString());
                                    successfulOffersModel.setCity(CapitalUtils.capitalize(address_details.get("city").toString()));
                                    successfulOffersModel.setState(CapitalUtils.capitalize(address_details.get("state").toString()));
                                    successfulOffersModel.setPincode(address_details.get("pincode").toString());
                                    successfulOffersModel.setPhone_no(address_details.get("phone_no").toString());
                                    successfulOffersModel.setIs_clicked("false");

                                }

                                successfulDataList.add(successfulOffersModel);

                            }

                    /*successfulOffersAdapter = new SuccessfulOffersAdapter(getActivity(), successfulDataList,OffersFragment.this);
                    rv_successful_offers.setAdapter(successfulOffersAdapter);*/

                            if (successful_page_number!=Constants.PAGE_START)
                            {
                                successfulOffersAdapter.removeLoading();
                            }

                            successfulOffersAdapter.addItems(successfulDataList);
                            successfulOffersAdapter.addLoading();

                            if (!checkout_ref.isEmpty())
                            {
                                for (int i=0;i<successfulDataList.size();i++)
                                {
                                    if (checkout_ref.equalsIgnoreCase(successfulDataList.get(i).getCheckout_ref()))
                                    {
                                        lm_successful.scrollToPositionWithOffset(i,0);
                                        break;
                                    }
                                }
                            }

                        }
                        successful_isLoading=false;
                        //realtimeupdations();

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

//                if(progressBarOfferSuccessful.isShown()) {
//                    progressBarOfferSuccessful.setVisibility(View.GONE);
//                }

                    skeletonScreen2.hide();

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
        }


    }


    private void failedOffers()
    {

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            if(flag3 == 0) {
//            progressBarOfferFailed.setVisibility(View.VISIBLE);
                skeletonScreen3 = Skeleton.bind(this.rv_failed_offers).adapter(failedOffersAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();
            }

//        progressBarOfferActive.setVisibility(View.GONE);
//        progressBarOfferSuccessful.setVisibility(View.GONE);

//        skeletonScreen1.hide();
//        skeletonScreen2.hide();

            getFailedOffers().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {

                    swipeRefresh_failed.setRefreshing(false);
                    Log.e(TAG, "onSuccess: " + hashMap);

                    flag3 = 1;

//                if(progressBarOfferFailed.isShown()) {
//                    progressBarOfferFailed.setVisibility(View.GONE);
//                }
                    if (failed_page_number==1)
                    {
                        skeletonScreen3.hide();
                    }

                    failedDataList.clear();
                    failed_count=1;

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        ArrayList al = (ArrayList) hashMap.get("data");

                        if(al.size() == 0)
                        {
                            if (failed_page_number==1)
                            {
                                swipeRefresh_failed.setVisibility(View.GONE);
                                no_failed_offers.setText("There are no lost biddings.");

                                no_active_offers.setVisibility(View.GONE);

                                if (currentTab.equalsIgnoreCase("failed"))
                                {
                                    no_active_offers.setVisibility(View.GONE);
                                    no_successful_offers.setVisibility(View.GONE);
                                    no_failed_offers.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    no_failed_offers.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                failedOffersAdapter.emptyData();
                                failed_page_number--;
                            }
                            failed_isLastPage=true;

                        }
                        else
                        {
                            for (int i = 0; i < al.size(); i++) {

                                HashMap data = (HashMap) al.get(i);

                                FailedOffersModel failedOffersModel = new FailedOffersModel();

                                failedOffersModel.setCurrent_price((data.get("current_price").toString()));
                                failedOffersModel.setBid_price((data.get("bid_price").toString()));

                                HashMap product_details = (HashMap) data.get("product_details");
                                failedOffersModel.setProduct_name(CapitalUtils.capitalize(product_details.get("product_name").toString()));
                                failedOffersModel.setActual_price((product_details.get("s_price").toString()));
                                failedOffersModel.setImage_thumbnail(product_details.get("image_thumbnail").toString());

                                HashMap bid_datetime = (HashMap) data.get("bid_datetime");
                                failedOffersModel.set_seconds(SecondsToDateTime.conversion(bid_datetime.get("_seconds").toString()));

                                failedDataList.add(failedOffersModel);

                            }

                    /*failedOffersAdapter = new FailedOffersAdapter(getActivity(), failedDataList);
                    rv_failed_offers.setAdapter(failedOffersAdapter);*/
                            if (failed_page_number!=Constants.PAGE_START)
                            {
                                failedOffersAdapter.removeLoading();
                            }
                            failedOffersAdapter.addItems(failedDataList);
                            failedOffersAdapter.addLoading();

                        }
                        failed_isLoading=false;

                        //realtimeupdations2();
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

//                if(progressBarOfferFailed.isShown()) {
//                    progressBarOfferFailed.setVisibility(View.GONE);
//                }

                    skeletonScreen3.hide();

                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
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


    private Task<HashMap> getActiveOffers() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        return mFunctions.getHttpsCallable("getActiveOffers").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }


    private Task<HashMap> getSuccessfulOffers() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("page_number", successful_page_number);//0
        data.put("limit", Constants.limit);

        return mFunctions.getHttpsCallable("getSuccessfulOffers").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }


    private Task<HashMap> getFailedOffers() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("page_number", failed_page_number);
        data.put("limit", Constants.limit);

        return mFunctions.getHttpsCallable("getFailedOffers").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }


    private void getsetData()
    {
        Typeface rubik_regular=null,rubik_medium = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            rubik_medium = getActivity().getResources().getFont(R.font.rubik_medium);
            rubik_regular = getActivity().getResources().getFont(R.font.rubik_regular);
        }
        else
        {
            rubik_medium = ResourcesCompat.getFont(getActivity(), R.font.rubik_medium);
            rubik_regular = ResourcesCompat.getFont(getActivity(), R.font.rubik_regular);
        }
        if (getArguments()!=null) {
            if (getArguments().getString("my_offer_screen").equalsIgnoreCase("successfull")) {
                //
                if (getArguments().getString("status_ref") != null) {
                    checkout_ref = getArguments().getString("status_ref");
                } else {
                    checkout_ref = "";
                }

                successfulOffersAdapter.expandedPosition = -1;
                successfulOffersAdapter.clear();
                successful_page_number = Constants.PAGE_START;
                flag2 = 0;
                //
                currentTab = "successful";
                successfulOffers();

                no_active_offers.setVisibility(View.GONE);
                no_successful_offers.setVisibility(View.VISIBLE);
                no_failed_offers.setVisibility(View.GONE);

                active_dot_image.setVisibility(View.INVISIBLE);
                successful_dot_image.setVisibility(View.VISIBLE);
                failed_dot_image.setVisibility(View.INVISIBLE);

                tv_active.setTypeface(rubik_regular);
                tv_successful.setTypeface(rubik_medium);
                tv_failed.setTypeface(rubik_regular);

                rv_active_offers.setVisibility(View.GONE);
                //rv_successful_offers.setVisibility(View.VISIBLE);
                swipeRefresh_active.setVisibility(View.GONE);
                swipeRefresh_successful.setVisibility(View.VISIBLE);
                //rv_failed_offers.setVisibility(View.GONE);
                swipeRefresh_failed.setVisibility(View.GONE);

            }

            if (getArguments().getString("my_offer_screen").equalsIgnoreCase("successfullDeep")) {

                ProgressDialog dialog = new ProgressDialog(getContext());
                dialog.setMessage("Please wait ...");
                dialog.setCancelable(false);
                dialog.show();

//                Toast.makeText(getContext(), "Hello", Toast.LENGTH_LONG).show();
                //
                if (getArguments().getString("status_ref") != null) {
                    checkout_ref = getArguments().getString("status_ref");
                } else {
                    checkout_ref = "";
                }

                successfulOffersAdapter.expandedPosition = -1;
                successfulOffersAdapter.clear();
                successful_page_number = Constants.PAGE_START;
                flag2 = 0;
                //
                currentTab = "successful";
                successfulOffers();

                no_active_offers.setVisibility(View.GONE);
                no_successful_offers.setVisibility(View.VISIBLE);
                no_failed_offers.setVisibility(View.GONE);

                active_dot_image.setVisibility(View.INVISIBLE);
                successful_dot_image.setVisibility(View.VISIBLE);
                failed_dot_image.setVisibility(View.INVISIBLE);

                tv_active.setTypeface(rubik_regular);
                tv_successful.setTypeface(rubik_medium);
                tv_failed.setTypeface(rubik_regular);

                rv_active_offers.setVisibility(View.GONE);
                //rv_successful_offers.setVisibility(View.VISIBLE);
                swipeRefresh_active.setVisibility(View.GONE);
                swipeRefresh_successful.setVisibility(View.VISIBLE);
                //rv_failed_offers.setVisibility(View.GONE);
                swipeRefresh_failed.setVisibility(View.GONE);

                if (DialogBoxError.checkInternetConnection(getActivity()))
                {
                    checkoutDetails(checkout_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
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
                                HashMap userData = (HashMap) hashMap.get("data");

                                String is_checkout = userData.get("is_checkout").toString();

                                if(is_checkout.equals("false")) {
                                    Intent intent = new Intent(getContext(), DeliverNowActivity.class);
                                    intent.putExtra("checkout_ref", checkout_ref);
                                    startActivity(intent);
                                }
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

                            if(dialog.isShowing()) {
                                dialog.cancel();
                            }

                            Log.e(TAG, "onFailure: " + e);
                            DialogBoxError.showError(getActivity(), e.getMessage());
                        }
                    });
                }
                else
                {
                    DialogBoxError.showInternetDialog(getActivity());
                }

            }
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        stopCountdown();
    }


    public void stopCountdown() {

        if (activeOffersAdapter!=null)
        {
            activeOffersAdapter.closeTimer();
        }
    }

    //get user details

    private void getUserDetails()
    {
        userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess:userDetails " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {

                    HashMap userData = (HashMap) hashMap.get("userData");

                    user_ref= userData.get("user_ref").toString();
                }
                /*else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(getActivity(),message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(getActivity(),message);
                }*/


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
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

    @Override
    public void onResume() {
        super.onResume();

        if (sharedPreferenceUtil.getString("winning_status","").equals("lost"))
        {
            ll_failed.performClick();
            sharedPreferenceUtil.setString("winning_status","");
            sharedPreferenceUtil.save();
        }
        else if (sharedPreferenceUtil.getString("winning_status","").equals("won"))
        {
            ll_successful.performClick();
            sharedPreferenceUtil.setString("winning_status","");
            sharedPreferenceUtil.save();
        }
        else
        {
            if (getActivity()!=null)
            {
                if (getArguments()!=null)
                {
                    if (successful_dot_image.getVisibility()==View.INVISIBLE
                            && failed_dot_image.getVisibility()==View.INVISIBLE)
                    {
                        if (getArguments().getString("my_offer_screen").equalsIgnoreCase("active"))
                        {
                            if (DialogBoxError.checkInternetConnection(getActivity()))
                            {
                                currentTab="active";
                                no_active_offers.setVisibility(View.GONE);
                                flag1 = 0;
                                activeOffers();
                            }
                            else
                            {
                                DialogBoxError.showInternetDialog(getActivity());
                            }
                        }
                    }

                }
                /*else
                {
                    if (DialogBoxError.checkInternetConnection(getActivity()))
                    {
                        flag1 = 0;
                        activeOffers();
                    }
                    else
                    {
                        DialogBoxError.showInternetDialog(getActivity());
                    }
                }*/

            }
        }

    }

    public void showNoActiveOffers()
    {
        if (currentTab.equalsIgnoreCase("active"))
        {
            no_active_offers.setVisibility(View.VISIBLE);
            no_active_offers.setText("There are no active biddings.");
        }
        rv_active_offers.setVisibility(View.GONE);
    }



    private Task<HashMap> checkoutDetails(String checkout_ref) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("checkout_ref", checkout_ref);

        return mFunctions.getHttpsCallable("checkoutDetails").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }
}