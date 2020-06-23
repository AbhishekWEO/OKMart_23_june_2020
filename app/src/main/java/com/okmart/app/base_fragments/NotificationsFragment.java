package com.okmart.app.base_fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.okmart.app.activities.ReloadActivity;
import com.okmart.app.adapters.NotificationsAdapter;
import com.okmart.app.model.NotificationsModel;
import com.okmart.app.pagination.PaginationListener;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FirebaseFunctions mFunctions;
    private String TAG = NotificationsFragment.class.getSimpleName();
    private Context context = getActivity();
    private RecyclerView rv_notifications;
    private List<NotificationsModel> notificationsDataList=new ArrayList<>();
    private TextView walletPoint;
    private SkeletonScreen skeletonScreen;
    private NotificationsAdapter notificationsAdapter;
    private FirebaseFirestore db;
    private String user_ref;
    private int notification_count=1;
    private boolean isObserverInvoke=false;
    private TextView no_notifications;

    //27 jan
    private int page_number = Constants.PAGE_START;
    private boolean isLastPage = false;
    //private int totalPage = 10;
    private boolean isLoading = false;
    private int itemCount = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar progressBarWalletPoints;

    private SharedPreferenceUtil sharedPreferenceUtil;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        sharedPreferenceUtil = new SharedPreferenceUtil(getActivity());

        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBarWalletPoints = view.findViewById(R.id.progressBarWalletPoints);

        no_notifications = view.findViewById(R.id.no_notifications);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        walletPoint = view.findViewById(R.id.walletPoint);
        isObserverInvoke=false;
        notification_count=1;
        rv_notifications = view.findViewById(R.id.rv_notifications);

        RelativeLayout section = view.findViewById(R.id.section);
        section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ReloadActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        //rv_notifications.setLayoutManager(new LinearLayoutManager(context));
        rv_notifications.setLayoutManager(layoutManager);
        //skeletonScreen = Skeleton.bind(this.rv_notifications).adapter(notificationsAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();

        rv_notifications.setHasFixedSize(true);

        notificationsAdapter = new NotificationsAdapter(getActivity(), new ArrayList<>());
        rv_notifications.setAdapter(notificationsAdapter);

        rv_notifications.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            public void loadMoreItems() {
                isLoading = true;
                page_number++;
                getNotification();
            }
            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
            @Override
            public boolean isLoading() {
                return isLoading;
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
//                walletPoint.setText(DoubleDecimal.twoPoints(hashMap.get("walletPoint").toString()) + " PT");
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



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        skeletonScreen = Skeleton.bind(this.rv_notifications).adapter(notificationsAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {

            isLastPage=false;
            page_number=1;
            //rv_notifications.setVisibility(View.GONE);
            getNotification();
        }
        else
        {
            skeletonScreen.hide();
            DialogBoxError.showInternetDialog(getActivity());
        }

    }

    private Task<HashMap> getNotificationList() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("page_number", page_number);
        data.put("limit", Constants.limit);

        return mFunctions.getHttpsCallable("getNotificationList").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
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


    public void getNotification() {

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            getNotificationList().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);

                    if (page_number==1)
                    {
                        skeletonScreen.hide();
                    }

                    swipeRefreshLayout.setRefreshing(false);
                    notificationsDataList.clear();

                    String response_msg = hashMap.get("response_msg").toString();
                    String message = hashMap.get("message").toString();

                    if (response_msg.equals("success"))
                    {
                        ArrayList al = (ArrayList) hashMap.get("data");
                        List<NotificationsModel> notificationsList=new ArrayList<>();

                        if (al.size()>0)
                        {
                            for (int i = 0; i < al.size(); i++) {

                                HashMap data = (HashMap) al.get(i);

                                NotificationsModel notificationsModel = new NotificationsModel();

                                String status = data.get("status").toString();

                                notificationsModel.setIs_read(data.get("is_read").toString());
                                notificationsModel.setIs_clear(data.get("is_clear").toString());
                                notificationsModel.setStatus(status);
                                notificationsModel.setStatus_ref(data.get("status_ref").toString());
                                notificationsModel.setNotification_ref(data.get("notification_ref").toString());
                                notificationsModel.setHeading(data.get("heading").toString());
                                notificationsModel.setMessage(data.get("message").toString());

                                if(status.equals("order")) {

                                    notificationsModel.setOrder_status(data.get("order_status").toString());
                                }

                                user_ref = data.get("user_ref").toString();
                                String is_clear = data.get("is_clear").toString();

                                if(!(is_clear.equals("true")))
                                {
                                    notificationsList.add(notificationsModel);//
                                    notificationsDataList.add(notificationsModel);
                                }

                            }

                        /*notificationsAdapter = new NotificationsAdapter(getActivity(), notificationsDataList);
                        rv_notifications.setAdapter(notificationsAdapter);*/

                    /*if (!isObserverInvoke) {
                        observer(user_ref);
                    }*/
                            /**
                             * manage progress view
                             */
                            if (page_number != Constants.PAGE_START) notificationsAdapter.removeLoading();
                            notificationsAdapter.addItems(notificationsList);



                            notificationsAdapter.addLoading();

                            rv_notifications.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            if (page_number==1)
                            {
                                no_notifications.setText("There are no notifications");
                            }
                            else {
                                notificationsAdapter.emptyData();
                            }
                            page_number--;
                            isLastPage = true;
                        }
                        isLoading = false;
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
                    Log.e(TAG, "onFailure: " + e);
                    DialogBoxError.showError(getActivity(), getString(R.string.something_went_wrong));
                }
            });
        }
        else
        {
            DialogBoxError.showInternetDialog(getActivity());
        }

        //skeletonScreen = Skeleton.bind(this.rv_notifications).adapter(notificationsAdapter).shimmer(true).angle(20).frozen(false).duration(1200).count(10).load(R.layout.item_skeleton).show();

    }

    public void observer(String user_ref) {

        db.collection("notification")
                .whereEqualTo("user_ref", user_ref)//"NGSMzFHL5cnVoXgpUZ1r"
                .whereEqualTo("is_clear", false)
                .limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "listen:error", e);
                            return;
                        }
                        isObserverInvoke=true;

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {

                                case ADDED:

                                    if (notification_count> notificationsDataList.size())
                                    {
                                        Log.e(TAG, "Notification_added: " + String.valueOf(notification_count )+ ", "  + notificationsDataList.size());
                                        getNotification();
                                    }

                                    notification_count++;

                                    break;

                                case MODIFIED:
                                    Log.e(TAG, "Notification_modified: " + String.valueOf(notification_count )+ ", "  + notificationsDataList.size());
                                    break;

                                case REMOVED:
                                    if (notification_count>1)
                                    {
                                        notification_count=notification_count-1;
                                    }

                                    getNotification();

                                    Log.e(TAG, "onEventRemoved: " + dc.getDocument().getData());

                                    break;

                            }
                        }

                    }
                });

    }


    @Override
    public void onRefresh() {

        if (DialogBoxError.checkInternetConnection(getActivity()))
        {
            notificationsAdapter.clear();

            isLastPage = false;

            isLoading = true;
            page_number=1;
            getNotification();
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
            DialogBoxError.showInternetDialog(getActivity());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        notificationsAdapter.clear();
    }

}