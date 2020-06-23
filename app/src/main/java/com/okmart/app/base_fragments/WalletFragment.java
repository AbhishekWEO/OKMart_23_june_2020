package com.okmart.app.base_fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.activities.DashboardActivity;
import com.okmart.app.activities.ReferEarnActivity;
import com.okmart.app.activities.ReloadActivity;
import com.okmart.app.adapters.PurchaseRefundAdapter;
import com.okmart.app.adapters.RechargeHistoryAdapter;
import com.okmart.app.model.PurchaseRefundModel;
import com.okmart.app.model.RechargeHistoryModel;
import com.okmart.app.pagination.PaginationListener;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SharedPreferenceUtil;
import com.okmart.app.utilities.Vibration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment implements View.OnClickListener {

    private String TAG = WalletFragment.class.getSimpleName();
    private FirebaseFunctions mFunctions;
    private Context context = getActivity();
    private LinearLayout ll_rechargeHistory,ll_refund;
    private TextView tv_recharge,tv_refund,tv_reload, user_name, wallet_balance;
    private ImageView recharge_dot_image,refund_dot_image, profile_image, refer_earn;
    private RecyclerView rv_rechargeHistory, rv_purchaseRefund;
    private ProgressBar progressBar;
    private String txt_profile_image;
    private List<PurchaseRefundModel> purchaseRefundDataList=new ArrayList<>();
    private List<RechargeHistoryModel> rechargeHistoryDataList=new ArrayList<>();
    private PurchaseRefundAdapter purchaseRefundAdapter;
    private RechargeHistoryAdapter rechargeHistoryAdapter;
    private TextView no_purchaserefund, no_rechargehistory;
    private SkeletonScreen skeletonScreen1, skeletonScreen2;
    private int flag1=0, flag2=0;

    private LinearLayoutManager linearLayoutManager,rechargeLayoutManger;
    private String status_ref="",payment_ref="";
    private SwipeRefreshLayout swipeRefresh_recharge,swipeRefresh_purchase;

    //recharge history pagination
    private int recharge_page_number = Constants.PAGE_START;
    private boolean recharge_isLastPage = false;
    private boolean recharge_isLoading = false;

    //purchase refund pagination
    private int purchase_page_number = Constants.PAGE_START;
    private boolean purchase_isLastPage = false;
    private boolean purchase_isLoading = false;

    private String txt_referred_code, txt_referral_url, txt_amount_for_new_user, txt_amt_for_refrence_user, txt_min_txn_amount;;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private ProgressBar progressBarWalletBal;

    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_wallet, container, false);
        sharedPreferenceUtil = new SharedPreferenceUtil(getActivity());
        initXml(view);
        setOnClickListener();

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            getUserDetails();
            getRechargeHistoryList();
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
        }



//        DashboardActivity.navigation.setLayoutDirection(BottomNavigationView);

        return view;
    }

    private void initXml(View view)
    {
        progressBarWalletBal = view.findViewById(R.id.progressBarWalletBal);
        mFunctions = FirebaseFunctions.getInstance();
        no_purchaserefund = view.findViewById(R.id.no_purchaserefund);
        no_rechargehistory = view.findViewById(R.id.no_rechargehistory);
        progressBar = view.findViewById(R.id.progressBar);
        user_name = view.findViewById(R.id.user_name);
        wallet_balance = view.findViewById(R.id.wallet_balance);
        profile_image = view.findViewById(R.id.profile_image);
        ll_rechargeHistory=view.findViewById(R.id.ll_rechargeHistory);
        tv_recharge=view.findViewById(R.id.tv_recharge);
        recharge_dot_image=view.findViewById(R.id.recharge_dot_image);
        ll_refund=view.findViewById(R.id.ll_refund);
        tv_refund= view.findViewById(R.id.tv_refund);
        tv_reload= view.findViewById(R.id.tv_reload);
        refund_dot_image=view.findViewById(R.id.refund_dot_image);
        rv_rechargeHistory=view.findViewById(R.id.rv_rechargeHistory);
        rv_purchaseRefund=view.findViewById(R.id.rv_purchaseRefund);
        swipeRefresh_recharge=view.findViewById(R.id.swipeRefresh_recharge);
        swipeRefresh_purchase=view.findViewById(R.id.swipeRefresh_purchase);
        refer_earn=view.findViewById(R.id.refer_earn);
        refer_earn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_amount_for_new_user!=null)
                {
                    Intent intent5 = new Intent(getContext(), ReferEarnActivity.class);
                    intent5.putExtra("referred_code", txt_referred_code);
                    intent5.putExtra("referral_url", txt_referral_url);
                    intent5.putExtra("amount_for_new_user", txt_amount_for_new_user);
                    intent5.putExtra("amt_for_refrence_user", txt_amt_for_refrence_user);
                    intent5.putExtra("min_txn_amount", txt_min_txn_amount);
                    startActivity(intent5);
                }

            }
        });

        user_name.setVisibility(View.INVISIBLE);
        status_ref="";
        payment_ref="";

        getsetData();

        setOnRefreshListener();

        //setAdapter();
    }

    private void setOnRefreshListener()
    {
        swipeRefresh_recharge.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (DialogBoxError.checkInternetConnection(getActivity()))
                {
                    recharge_isLastPage=false;
                    rechargeHistoryAdapter.expandedPosition=-1;
                    rechargeHistoryAdapter.clear();
                    payment_ref="";
                    recharge_page_number=Constants.PAGE_START;
                    getRechargeHistoryList();
                }
                else
                {
                    swipeRefresh_recharge.setRefreshing(false);
                    DialogBoxError.showInternetDialog(getActivity());
                }


            }
        });

        swipeRefresh_purchase.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (DialogBoxError.checkInternetConnection(getActivity()))
                {
                    purchase_isLastPage=false;
                    purchaseRefundAdapter.expandedPosition=-1;
                    purchaseRefundAdapter.clear();
                    status_ref="";
                    purchase_page_number=Constants.PAGE_START;
                    getWalletTransactionsList();
                }
                else
                {
                    swipeRefresh_purchase.setRefreshing(false);
                    DialogBoxError.showInternetDialog(getActivity());
                }

            }
        });
    }

    private void setAdapter()
    {
        //rechagre
        rechargeLayoutManger=new LinearLayoutManager(getActivity());
        //rv_rechargeHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

        rv_rechargeHistory.setLayoutManager(rechargeLayoutManger);

        rv_rechargeHistory.setHasFixedSize(true);
        rechargeHistoryAdapter = new RechargeHistoryAdapter(getActivity(), new ArrayList<>(),payment_ref);
        rv_rechargeHistory.setAdapter(rechargeHistoryAdapter);

        rechargeHistoryAdapter.clear();

        rv_rechargeHistory.addOnScrollListener(new PaginationListener(rechargeLayoutManger) {
            @Override
            public void loadMoreItems() {
                recharge_isLoading = true;
                recharge_page_number++;
                getRechargeHistoryList();
            }
            @Override
            public boolean isLastPage() {
                return recharge_isLastPage;
            }
            @Override
            public boolean isLoading() {
                return recharge_isLoading;
            }
        });

        ////////////////////////
        //purchaseRefund
        if (status_ref.isEmpty())
        {
            linearLayoutManager=new LinearLayoutManager(getActivity());
            //rv_purchaseRefund.setLayoutManager(new LinearLayoutManager(getActivity()));

            rv_purchaseRefund.setLayoutManager(linearLayoutManager);
            rv_purchaseRefund.setHasFixedSize(true);
            purchaseRefundAdapter = new PurchaseRefundAdapter(getActivity(), new ArrayList<>(),status_ref);
            rv_purchaseRefund.setAdapter(purchaseRefundAdapter);

            purchaseRefundAdapter.clear();

            rv_purchaseRefund.addOnScrollListener(new PaginationListener(linearLayoutManager) {
                @Override
                public void loadMoreItems() {
                    purchase_isLoading = true;
                    purchase_page_number++;
                    getWalletTransactionsList();
                }
                @Override
                public boolean isLastPage() {
                    return purchase_isLastPage;
                }
                @Override
                public boolean isLoading() {
                    return purchase_isLoading;
                }
            });
        }

    }

    private void setOnClickListener()
    {
        ll_rechargeHistory.setOnClickListener(this);
        ll_refund.setOnClickListener(this);
        tv_reload.setOnClickListener(this);
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
            case R.id.ll_rechargeHistory:

                Vibration.vibration(getActivity());

                if (DialogBoxError.checkInternetConnection(getActivity()))
                {
                    recharge_isLastPage=false;
                    payment_ref="";
                    swipeRefresh_recharge.setVisibility(View.VISIBLE);
                    //rv_rechargeHistory.setVisibility(View.VISIBLE);


                    swipeRefresh_purchase.setVisibility(View.GONE);
                    //rv_purchaseRefund.setVisibility(View.GONE);

                    recharge_dot_image.setVisibility(View.VISIBLE);
                    refund_dot_image.setVisibility(View.INVISIBLE);

                    tv_recharge.setTypeface(rubik_medium);
                    tv_refund.setTypeface(rubik_regular);

                    rechargeHistoryAdapter.expandedPosition=-1;
                    rechargeHistoryAdapter.clear();
                    payment_ref="";
                    recharge_page_number=Constants.PAGE_START;
                    skeletonScreen1 = Skeleton.bind(this.rv_rechargeHistory).adapter(rechargeHistoryAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();
                    getRechargeHistoryList();
                }
                else
                {
                    DialogBoxError.showInternetDialog(getActivity());
                }

                break;

            case R.id.ll_refund:

                Vibration.vibration(getActivity());

                if (DialogBoxError.checkInternetConnection(getActivity()))
                {
                    purchase_isLastPage=false;
                    status_ref="";
                    swipeRefresh_recharge.setVisibility(View.GONE);
                    //rv_rechargeHistory.setVisibility(View.GONE);

                    swipeRefresh_purchase.setVisibility(View.VISIBLE);
                    rv_purchaseRefund.setVisibility(View.VISIBLE);

                    //rechargeHistoryAdapter.clear();
                    purchaseRefundAdapter.expandedPosition=-1;
                    purchaseRefundAdapter.clear();
                    skeletonScreen2 = Skeleton.bind(this.rv_purchaseRefund).adapter(purchaseRefundAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();
                    purchase_page_number = Constants.PAGE_START;

                    getWalletTransactionsList();

                    recharge_dot_image.setVisibility(View.INVISIBLE);
                    refund_dot_image.setVisibility(View.VISIBLE);

                    tv_recharge.setTypeface(rubik_regular);
                    tv_refund.setTypeface(rubik_medium);
                }
                else
                {
                    DialogBoxError.showInternetDialog(getActivity());
                }

                break;

            case R.id.tv_reload:
                startActivity(new Intent(getActivity(), ReloadActivity.class));
                break;

        }
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
        if (getArguments()!=null)
        {
            if (getArguments().getString("my_wallet_screen").equalsIgnoreCase("purchase"))
            {
                status_ref = getArguments().getString("status_ref");

                swipeRefresh_recharge.setVisibility(View.GONE);
                //rv_rechargeHistory.setVisibility(View.GONE);

                swipeRefresh_purchase.setVisibility(View.VISIBLE);
                //rv_purchaseRefund.setVisibility(View.VISIBLE);

                linearLayoutManager=new LinearLayoutManager(getActivity());
                //rv_purchaseRefund.setLayoutManager(new LinearLayoutManager(getActivity()));

                rv_purchaseRefund.setLayoutManager(linearLayoutManager);
                rv_purchaseRefund.setHasFixedSize(true);
                purchaseRefundAdapter = new PurchaseRefundAdapter(getActivity(), new ArrayList<>(),status_ref);
                rv_purchaseRefund.setAdapter(purchaseRefundAdapter);

                purchaseRefundAdapter.clear();

                rv_purchaseRefund.addOnScrollListener(new PaginationListener(linearLayoutManager) {
                    @Override
                    public void loadMoreItems() {
                        purchase_isLoading = true;
                        purchase_page_number++;
                        getWalletTransactionsList();
                    }
                    @Override
                    public boolean isLastPage() {
                        return purchase_isLastPage;
                    }
                    @Override
                    public boolean isLoading() {
                        return purchase_isLoading;
                    }
                });

                recharge_dot_image.setVisibility(View.INVISIBLE);
                refund_dot_image.setVisibility(View.VISIBLE);

                tv_recharge.setTypeface(rubik_regular);
                tv_refund.setTypeface(rubik_medium);

                getWalletTransactionsList();

            }
            else if (getArguments().getString("my_wallet_screen").equalsIgnoreCase("recharge"))
            {
                payment_ref = getArguments().getString("status_ref");

            }

        }

        setAdapter();
    }


    public void getUserDetails() {

        userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess:userDetails " + hashMap);

                if(progressBar.isShown())
                {
                    progressBar.setVisibility(View.GONE);
                }

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    HashMap userData = (HashMap) hashMap.get("userData");

                    user_name.setText(CapitalUtils.capitalize(userData.get("user_name").toString()));
                    user_name.setVisibility(View.VISIBLE);
//                wallet_balance.setText(DoubleDecimal.twoPoints(userData.get("wallet_balance").toString()));
                    wallet_balance.setText(DoubleDecimal.twoPointsComma(userData.get("wallet_balance").toString()));
                    progressBarWalletBal.setVisibility(View.GONE);
                    txt_profile_image = (String) userData.get("profile_image");

                    txt_referred_code = userData.get("referral_code").toString();
                    txt_referral_url = userData.get("referral_url").toString();

                    txt_amount_for_new_user = (String) userData.get("amount_for_new_user").toString();
                    txt_amt_for_refrence_user = (String) userData.get("amt_for_refrence_user").toString();
                    txt_min_txn_amount = (String) userData.get("min_txn_amount").toString();

                    if (!(txt_profile_image.length() == 0)) {

                        try {

                            //Handle whatever you're going to do with the URL here
                            Glide.with(WalletFragment.this).load(txt_profile_image).placeholder(R.color.white).into(profile_image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        profile_image.setImageResource(R.drawable.avatar);
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

                if(progressBar.isShown())
                {
                    progressBar.setVisibility(View.GONE);
                }

                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(context, getString(R.string.something_went_wrong));
            }
        });
    }

    public void getRechargeHistoryList() {

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            no_rechargehistory.setVisibility(View.GONE);
            no_purchaserefund.setVisibility(View.GONE);
            if(flag1 == 0) {
                skeletonScreen1 = Skeleton.bind(this.rv_rechargeHistory).adapter(rechargeHistoryAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();
            }

            getRechargeHistory().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {

                    swipeRefresh_recharge.setRefreshing(false);

                    Log.e(TAG, "onSuccess: " + hashMap);

                    flag1 = 1;

                    if (recharge_page_number==1)
                    {
                        skeletonScreen1.hide();
                    }

                    rechargeHistoryDataList.clear();

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        ArrayList al = (ArrayList) hashMap.get("data");
                        List<RechargeHistoryModel> rechargeHistoryModelList=new ArrayList<>();

                        if(al.size() == 0)
                        {
                            //no_rechargehistory.setText("There are no transactions");

                            if (recharge_page_number==1)
                            {
                                no_rechargehistory.setVisibility(View.VISIBLE);
                                swipeRefresh_recharge.setVisibility(View.GONE);
                                no_rechargehistory.setText("There are no transactions");
                            }
                            else
                            {
                                rechargeHistoryAdapter.emptyData();
                                recharge_page_number--;
                            }

                            recharge_isLastPage = true;
                        }
                        else
                        {

                            for (int i = 0; i < al.size(); i++)
                            {

                                HashMap data = (HashMap) al.get(i);

                                RechargeHistoryModel rechargeHistoryModel = new RechargeHistoryModel();

                                rechargeHistoryModel.setCard_holder_name(data.get("card_holder_name").toString());
                                rechargeHistoryModel.setPayment_status(data.get("payment_status").toString());
                                rechargeHistoryModel.setCard_last4(data.get("card_last4").toString());
                                rechargeHistoryModel.setPayment_ref(data.get("payment_ref").toString());
                                rechargeHistoryModel.setTxn_id(data.get("txn_id").toString());

                                HashMap created = (HashMap) data.get("created");
                                rechargeHistoryModel.set_seconds(created.get("_seconds").toString());

                                rechargeHistoryModel.setPayment_id(data.get("payment_id").toString());
                                rechargeHistoryModel.setPrice(data.get("price").toString());
                                rechargeHistoryModel.setCard_brand(data.get("card_brand").toString());

                                rechargeHistoryModel.setPayment_method(data.get("payment_method").toString());

                                if(data.get("payment_method").toString().equals("referral")) {
                                    rechargeHistoryModel.setIs_new_user(data.get("is_new_user").toString());
                                    rechargeHistoryModel.setReferral_user_name(CapitalUtils.capitalize(data.get("referral_user_name").toString()));
                                }

                                rechargeHistoryDataList.add(rechargeHistoryModel);
                                rechargeHistoryModelList.add(rechargeHistoryModel);

                            }
                            //commented on 3 feb
                    /*rechargeHistoryAdapter = new RechargeHistoryAdapter(getActivity(), rechargeHistoryDataList,payment_ref);
                    rv_rechargeHistory.setAdapter(rechargeHistoryAdapter);*/

                            if (recharge_page_number != Constants.PAGE_START)
                            {
                                rechargeHistoryAdapter.removeLoading();
                            }
                            rechargeHistoryAdapter.addItems(rechargeHistoryModelList);

                            rechargeHistoryAdapter.addLoading();

                            if (!DashboardActivity.txn_id.isEmpty())
                            {
                                for (int i = 0; i<rechargeHistoryDataList.size();i++)
                                {
                                    if (rechargeHistoryDataList.get(i).getTxn_id().equalsIgnoreCase(DashboardActivity.txn_id))
                                    {
                                        rechargeLayoutManger.scrollToPositionWithOffset(i,0);
                                        break;
                                    }
                                }
                            }
                            else if (!payment_ref.isEmpty())
                            {
                                for (int i = 0; i<rechargeHistoryDataList.size();i++)
                                {
                                    if (rechargeHistoryDataList.get(i).getPayment_ref().equalsIgnoreCase(payment_ref))
                                    {
                                        rechargeLayoutManger.scrollToPositionWithOffset(i,0);
                                        break;
                                    }
                                }
                            }

                        }
                        recharge_isLoading = false;
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
                    swipeRefresh_recharge.setRefreshing(false);
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

    public void getWalletTransactionsList() {

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {

            no_rechargehistory.setVisibility(View.GONE);
            no_purchaserefund.setVisibility(View.GONE);
            if(flag2 == 0) {
                skeletonScreen2 = Skeleton.bind(this.rv_purchaseRefund).adapter(purchaseRefundAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();
            }

            getWalletTransactions().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    swipeRefresh_purchase.setRefreshing(false);
                    Log.e(TAG, "onSuccess:wallet " + hashMap);

                    flag2 = 1;

                    if (purchase_page_number==1)
                    {
                        skeletonScreen2.hide();
                    }

                    purchaseRefundDataList.clear();

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        ArrayList al = (ArrayList) hashMap.get("data");

                        if(al.size() == 0)
                        {
                            if (purchase_page_number==1)
                            {
                                no_purchaserefund.setVisibility(View.VISIBLE);
                                swipeRefresh_purchase.setVisibility(View.GONE);
                                no_purchaserefund.setText("There are no transactions");
                            }
                            else
                            {
                                purchaseRefundAdapter.emptyData();
                                purchase_page_number--;
                            }

                            purchase_isLastPage = true;
                        }
                        else
                        {

                            for (int i = 0; i < al.size(); i++)
                            {

                                HashMap data = (HashMap) al.get(i);

                                PurchaseRefundModel purchaseRefundModel= new PurchaseRefundModel();

                                purchaseRefundModel.setTransaction_id(data.get("transaction_id").toString());
                                purchaseRefundModel.setPhone_no(data.get("phone_no").toString());
                                purchaseRefundModel.setTxn_type(data.get("txn_type").toString());
                                purchaseRefundModel.setUser_name(data.get("user_name").toString());
                                purchaseRefundModel.setPrice(data.get("price").toString());

                                HashMap created = (HashMap) data.get("created");
                                purchaseRefundModel.set_seconds(created.get("_seconds").toString());

                                HashMap product_details = (HashMap) data.get("product_details");
                                purchaseRefundModel.setProduct_name(product_details.get("product_name").toString());
                                purchaseRefundModel.setImage_thumbnail(product_details.get("image_thumbnail").toString());
                                purchaseRefundModel.setCurrent_price(product_details.get("current_price").toString());

                                purchaseRefundModel.setTransaction_ref(data.get("transaction_ref").toString());
                                purchaseRefundModel.setTransaction_id(data.get("transaction_id").toString());

                                purchaseRefundDataList.add(purchaseRefundModel);

                            }

                    /*purchaseRefundAdapter = new PurchaseRefundAdapter(getActivity(), purchaseRefundDataList,status_ref);
                    rv_purchaseRefund.setAdapter(purchaseRefundAdapter);*/

                            if (purchase_page_number != Constants.PAGE_START)
                            {
                                purchaseRefundAdapter.removeLoading();
                            }
                            purchaseRefundAdapter.addItems(purchaseRefundDataList);
                            purchaseRefundAdapter.addLoading();


                            if (!status_ref.isEmpty())
                            {
                                for (int i = 0; i<purchaseRefundDataList.size();i++)
                                {
                                    if (purchaseRefundDataList.get(i).getTransaction_ref().equalsIgnoreCase(status_ref))
                                    {
                                        linearLayoutManager.scrollToPositionWithOffset(i,0);
                                        break;
                                    }
                                }
                            }

                        }
                        purchase_isLoading = false;
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

                    swipeRefresh_purchase.setRefreshing(false);

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

    private Task<HashMap> getRechargeHistory() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("page_number", recharge_page_number);
        data.put("limit", Constants.limit);

        return mFunctions.getHttpsCallable("getRechargeHistory").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private Task<HashMap> getWalletTransactions() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("page_number", purchase_page_number);
        data.put("limit", Constants.limit);

        return mFunctions.getHttpsCallable("getWalletTransactions").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }
}