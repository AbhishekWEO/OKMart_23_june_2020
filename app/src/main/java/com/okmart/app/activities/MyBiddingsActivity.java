package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.okmart.app.adapters.MyBiddingAdapter;
import com.okmart.app.model.MyBiddings;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBox;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.DoubleDecimal;
import com.okmart.app.utilities.SecondsToDateTime;
import com.okmart.app.utilities.SharedPreferenceUtil;
import com.okmart.app.utilities.Vibration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBiddingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_back, img_product_thumbnail, more, img_speaker, img_sold_out;
    private TextView product_name, bid_price, tv_seconds, tv_name;
    private ProgressBar progressBar2;
    private String offer_ref = "", downtime_ref = "";
    private CountDownTimer timer;
    private RecyclerView rv_bidRecords;
    private CardView cardViewWinner, cv_sold;
    private FirebaseFirestore db;
    private List<MyBiddings> myBiddingsList = new ArrayList<>();
    private MyBiddingAdapter myBiddingAdapter;
    private boolean isTimerFinish = false;
    private RelativeLayout section;

    private TextView walletPoint, points;
    private String TAG = MyBiddingsActivity.class.getSimpleName();
    private String wallet_balance;
    private Context context = MyBiddingsActivity.this;
    private FirebaseFunctions mFunctions;
    private String phone_no = "", user_ref = "", direct_purchase_price = "", current_price = "", bidPrice = "", product_ref = "", user_ref_new ;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private boolean isActive = false, product_status = false, isProductExpired = false;
    private boolean isCurrentUserWinner = false;

    private EditText tv_bidPrice;
    private TextView tv_currentPrice, tv_directPrice, tv_placeBid;
    private ProgressBar progressBar;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DialogBox.bid_price_new = 0;

        getSupportActionBar().hide();

        setContentView(R.layout.activity_my_biddings);
        db = FirebaseFirestore.getInstance();
        mFunctions = FirebaseFunctions.getInstance();

        isTimerFinish = false;
        isActive = true;
        isCurrentUserWinner = false;
        product_status = false;
        isProductExpired = false;

        sharedPreferenceUtil = new SharedPreferenceUtil(MyBiddingsActivity.this);

        initXml();
        setOnClickListener();
        observers();
    }

    private void initXml() {
        tv_currentPrice = findViewById(R.id.tv_currentPrice);
        tv_directPrice = findViewById(R.id.tv_directPrice);
        tv_bidPrice = findViewById(R.id.tv_bidPrice);
        tv_placeBid = findViewById(R.id.tv_placeBid);

        progressBar= findViewById(R.id.progressBar);
        seekBar = findViewById(R.id.seekBar);

        img_back = findViewById(R.id.img_back);
        walletPoint = findViewById(R.id.walletPoint);
        points = findViewById(R.id.points);
        img_product_thumbnail = findViewById(R.id.img_product_thumbnail);
        more = findViewById(R.id.more);
        product_name = findViewById(R.id.product_name);
        bid_price = findViewById(R.id.bid_price);
        progressBar2 = findViewById(R.id.progressBar);
        tv_seconds = findViewById(R.id._seconds);
        rv_bidRecords = findViewById(R.id.rv_bidRecords);
        tv_name = findViewById(R.id.tv_name);
        cardViewWinner = findViewById(R.id.cardViewWinner);
        cv_sold = findViewById(R.id.cv_sold);
        img_speaker = findViewById(R.id.img_speaker);
        img_sold_out = findViewById(R.id.img_sold_out);
        section = findViewById(R.id.section);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        rv_bidRecords.setLayoutManager(lm);

        getSetData();
    }

    private void getSetData() {
        if (DialogBoxError.checkInternetConnection(MyBiddingsActivity.this)) {
            getUserBal();
        } else {
            DialogBoxError.showInternetDialog(MyBiddingsActivity.this);
        }

        if (getIntent().getExtras() != null) {
            user_ref_new = getIntent().getStringExtra("user_ref");
            product_ref = getIntent().getStringExtra("product_ref");
            offer_ref = getIntent().getStringExtra("offer_ref");
            direct_purchase_price = getIntent().getStringExtra("direct_purchase_price");
            current_price = getIntent().getStringExtra("current_price");

            downtime_ref = getIntent().getStringExtra("downtime_ref");

            Glide.with(MyBiddingsActivity.this)
                    .load(getIntent().getStringExtra("image_thumbnail"))
                    .into(img_product_thumbnail);
            product_name.setText(getIntent().getStringExtra("product_name"));

            bid_price.setText("RM " + DoubleDecimal.twoPointsComma(getIntent().getStringExtra("bid_price")));

            bidPrice = getIntent().getStringExtra("bid_price");

            tv_bidPrice.setText(DoubleDecimal.twoPoints(bidPrice));
            tv_currentPrice.setText(DoubleDecimal.twoPointsComma(""+current_price));
            tv_directPrice.setText(DoubleDecimal.twoPointsComma(""+direct_purchase_price));

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


            int step=1;
            seekBar.setMax((int) (((Double.parseDouble(direct_purchase_price) - Double.parseDouble(current_price))*100)/step));
            seekBar.setProgress((int) ((Double.parseDouble(bidPrice) - Double.parseDouble(current_price))*100));

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {


                    bidPrice = String.valueOf(Double.parseDouble(current_price) + + ((float) progress/100 * step));

                    tv_bidPrice.setText(DoubleDecimal.twoPoints(bidPrice));
                    tv_bidPrice.setSelection(tv_bidPrice.length());

                    if (progressBar.getVisibility()==View.GONE)
                    {
                        if (Double.parseDouble(current_price) + (progress/100 * step) == Double.parseDouble(direct_purchase_price))
                        {
                            tv_placeBid.setText("Buy Now");
                        }
                        else
                        {
                            tv_placeBid.setText("Place new Bid");
                        }
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            tv_placeBid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    if(Double.parseDouble(bidPrice) == 0)
                    {

                        Toast.makeText(context, "Please change the bid price", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        if (DialogBoxError.checkInternetConnection(MyBiddingsActivity.this))
                        {
                            tv_placeBid.setText("");
                            progressBar.setVisibility(View.VISIBLE);
                            tv_placeBid.setFocusable(false);

                            editOffer(offer_ref, String.valueOf(bidPrice)).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                                @Override
                                public void onSuccess(HashMap hashMap) {
                                    Log.e(TAG, "EditProduct: " + hashMap);
//                            dialogE.cancel();
                                    String response_msg = hashMap.get("response_msg").toString();
                                    String message = hashMap.get("message").toString();
                                    //
                                    tv_placeBid.setText("Place new Bid");
                                    progressBar.setVisibility(View.GONE);
                                    tv_placeBid.setEnabled(true);

                                    if(response_msg.equals("bid has been moved to checkout"))
                                    {
                                        DialogBoxError.showError(context, message);
                                    }
                                    else if(response_msg.equals("success"))
                                    {
                                        tv_bidPrice.setText(DoubleDecimal.twoPoints(String.valueOf(bidPrice)));

                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    }
                                    else if(response_msg.equals("bid not found"))
                                    {
                                        DialogBoxError.showError(context, message);
                                    }
                                    else if(response_msg.equals("low wallet balance"))
                                    {
                                        DialogBoxError.showError(context, message);
                                    }
                                    else if(response_msg.equals("product sold out"))
                                    {
                                        DialogBoxError.showError(context, message);
                                    }
                                    else if(response_msg.equals("product does not exist"))
                                    {
                                        DialogBoxError.showError(context, message);
                                    }
                                    else if(response_msg.equals("downtime expired"))
                                    {
                                        DialogBoxError.showError(context, message);
                                    }
                                    else if(response_msg.equals("downtime does not exist"))
                                    {
                                        DialogBoxError.showError(context, message);
                                    }
                                    else if(response_msg.equals("offer has been moved to checkout"))
                                    {
                                        //DialogBoxError.showDailog(context, message);

                                    }
                                    else if (response_msg.equals(Constants.unauthorized))
                                    {
                                        DialogBoxError.showDialogBlockUser(MyBiddingsActivity.this,message,sharedPreferenceUtil);
                                    }
                                    else if (response_msg.equals(Constants.under_maintenance))
                                    {
                                        DialogBoxError.showError(MyBiddingsActivity.this,message);
                                    }
                                    else
                                    {
                                        DialogBoxError.showError(context, message);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: " + e);
                                    DialogBoxError.showError(context, e.getMessage());
                                    tv_placeBid.setText("Place new Bid");
                                    progressBar.setVisibility(View.GONE);
                                    tv_placeBid.setEnabled(true);
                                }
                            });
                        }
                        else
                        {
                            DialogBoxError.showInternetDialog(MyBiddingsActivity.this);
                        }

                    }

                }
            });

            String end_datetime = getIntent().getStringExtra("end_datetime");
            String start_datetime = getIntent().getStringExtra("start_datetime");

            long current_time_milliseconds = System.currentTimeMillis();

            long timer_difference = (Long.valueOf(end_datetime) * 1000) - (Long.valueOf(start_datetime) * 1000);
            long timer_difference2 = (Long.valueOf(end_datetime) * 1000) - current_time_milliseconds;

            progressBar2.setMax((int) timer_difference / 1000);
            progressBar2.setProgress((int) timer_difference / 1000);

            timer = new CountDownTimer(timer_difference2, 1000) {
                @Override
                public void onTick(long leftTimeInMilliseconds) {
                    Log.e("MyBiddingsActivityTimer", String.valueOf(leftTimeInMilliseconds));

                    int seconds = (int) (leftTimeInMilliseconds / 1000) % 60;
                    int minutes = (int) ((leftTimeInMilliseconds / (1000 * 60)) % 60);
                    int hours = (int) ((leftTimeInMilliseconds / (1000 * 60 * 60)) % 24);

                    tv_seconds.setText(String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds));
                    progressBar2.setProgress((int) (leftTimeInMilliseconds / 1000));

                }

                @Override
                public void onFinish() {
                    //isTimerFinish=true;
                }
            }.start();

        }

        setAdapter();

    }

    private void setAdapter() {
        myBiddingAdapter = new MyBiddingAdapter(MyBiddingsActivity.this, myBiddingsList, user_ref_new);
        rv_bidRecords.setAdapter(myBiddingAdapter);

        myBiddingsList.clear();

        if (DialogBoxError.checkInternetConnection(MyBiddingsActivity.this)) {
            myBiddingObserver();
        } else {
            DialogBoxError.showInternetDialog(MyBiddingsActivity.this);
        }

    }

    private void setOnClickListener() {
        img_back.setOnClickListener(this);
        more.setOnClickListener(this);
        section.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;

            case R.id.more:

                DialogBox.showEditCancelOfferDialog(MyBiddingsActivity.this, offer_ref,
                        timer, myBiddingsList,
                        myBiddingAdapter, wallet_balance, current_price, direct_purchase_price, bid_price,
                        bidPrice);
                break;

            case R.id.section:
                Intent intent = new Intent(MyBiddingsActivity.this, ReloadActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isActive = false;
        closeTimer();

    }

    private void closeTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void myBiddingObserver() {
        db = null;
        db = FirebaseFirestore.getInstance();

        db.collection("biddings")
                .whereEqualTo("downtime_ref", downtime_ref)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Error", e.getMessage());
                            return;
                        }

                        if (isActive)
                        {
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges())
                            {
                                switch (dc.getType())
                                {
                                    case ADDED:
                                        Log.e("ADDED", "" + dc.getDocument().getData());

                                        HashMap hashMap = (HashMap) dc.getDocument().getData();

                                        Log.e("hashMap", "" + hashMap);

                                        boolean isDirecCheckOut=false;

                                        if (hashMap.get("user_ref").toString().equals(user_ref))
                                        {
                                            if (hashMap.get("is_direct_checkout").toString().equals("true"))
                                            {

                                                Vibration.vibration(MyBiddingsActivity.this);

                                                Constants.playSuccesSoundEffect(MyBiddingsActivity.this, Constants.click);

                                                closeTimer();
                                                isDirecCheckOut=true;
                                                sharedPreferenceUtil.setString("winning_status", "won");
                                                sharedPreferenceUtil.save();
                                                finish();
                                                break;
                                                //DialogBoxError.showDialog(context, "Your bid has been accepted, Please proceed your bid in won tab under biddings.");
                                            }
                                        }


                                        ///
                                        /*if (!isDirecCheckOut)
                                        {

                                        }*/

                                        MyBiddings myBiddingsObj = new MyBiddings();
                                        myBiddingsObj.setUser_name(hashMap.get("user_name").toString());
                                        myBiddingsObj.setUser_ref(hashMap.get("user_ref").toString());
                                        myBiddingsObj.setBid_price(hashMap.get("bid_price").toString());
                                        myBiddingsObj.setIs_bid_cancelled(hashMap.get("is_bid_cancelled").toString());
                                        myBiddingsObj.setIs_winner(hashMap.get("is_winner").toString());
                                        myBiddingsObj.setIs_edited(hashMap.get("is_edited").toString());

                                        myBiddingsObj.setIs_direct_checkout(hashMap.get("is_direct_checkout").toString());

                                        if (user_ref.equalsIgnoreCase(hashMap.get("user_ref").toString())) {

                                            offer_ref = dc.getDocument().getId();

                                        }

                                        //bid_datetime=Timestamp(seconds=1582622033, nanoseconds=629000000)
                                        String bid_datetime = hashMap.get("bid_datetime").toString();

                                        String sec = seconds(bid_datetime);
                                        String bid_dateTime = SecondsToDateTime.millisecToDateTime(sec);
                                        Log.e("dateTime: ", bid_dateTime);
                                        //myBiddingsObj.setBid_datetime(bid_dateTime);
                                        myBiddingsObj.setBid_datetime(sec);

                                        //bid time
                                        String[] timeSplit = bid_dateTime.split(",");
                                        String bid_time = timeSplit[1];
                                        Log.e("dateTime: ", bid_time);

                                        myBiddingsObj.setBid_time(bid_time);

                                        myBiddingsList.add(myBiddingsObj);

                                        Comparator<MyBiddings> comparator = null;
                                        comparator = Comparator.comparing(ex -> ex.getBid_datetime());
                                        myBiddingsList.sort(comparator.reversed());
                                        //

                                        myBiddingAdapter.notifyDataSetChanged();



                                        break;

                                    case MODIFIED:
                                        Log.e("MODIFIED2", "" + dc.getDocument().getData());

                                        int position = 0;

                                        HashMap hashMap_modified = (HashMap) dc.getDocument().getData();


                                        /*if (hashMap_modified.get("user_ref").toString().equals(user_ref))
                                        {
                                            if (hashMap_modified.get("is_direct_checkout").toString().equals("true"))
                                            {

                                                Vibration.vibration(MyBiddingsActivity.this);

                                                Constants.playSuccesSoundEffect(MyBiddingsActivity.this, Constants.click);

                                                closeTimer();
                                                // isDirecCheckOut=true;
                                                sharedPreferenceUtil.setString("winning_status", "won");
                                                sharedPreferenceUtil.save();
                                                finish();
                                                //DialogBoxError.showDialog(context, "Your bid has been accepted, Please proceed your bid in won tab under biddings.");
                                            }
                                        }*/


                                        if (hashMap_modified.get("user_ref").toString().equals(user_ref))
                                        {
                                            if (hashMap_modified.get("is_bid_cancelled").toString().equals("true"))
                                            {

                                                Vibration.vibration(MyBiddingsActivity.this);

                                                Constants.playSuccesSoundEffect(MyBiddingsActivity.this, Constants.click);

                                                closeTimer();
                                                // isDirecCheckOut=true;
                                                sharedPreferenceUtil.setString("winning_status", "lost");
                                                sharedPreferenceUtil.save();
//                                                finish();
                                                DialogBoxError.showDialog(context, "Your bid has been cancelled.");
                                            }
                                        }


                                        String user_ref2 = hashMap_modified.get("user_ref").toString();

                                        for (int i = 0; i < myBiddingsList.size(); i++)
                                        {
                                            if (hashMap_modified.get("user_ref").toString().equals(myBiddingsList.get(i).getUser_ref()))
                                            {

                                                MyBiddings myBiddingsObj3 = new MyBiddings();
                                                myBiddingsObj3.setUser_name(hashMap_modified.get("user_name").toString());
                                                myBiddingsObj3.setUser_ref(hashMap_modified.get("user_ref").toString());
                                                myBiddingsObj3.setBid_price(hashMap_modified.get("bid_price").toString());
                                                myBiddingsObj3.setIs_bid_cancelled(hashMap_modified.get("is_bid_cancelled").toString());
                                                myBiddingsObj3.setIs_winner(hashMap_modified.get("is_winner").toString());
                                                myBiddingsObj3.setIs_edited(hashMap_modified.get("is_edited").toString());

                                                myBiddingsObj3.setIs_direct_checkout(hashMap_modified.get("is_direct_checkout").toString());

                                                String bid_datetime3 = hashMap_modified.get("bid_datetime").toString();

                                                String sec3 = seconds(bid_datetime3);
                                                String bid_dateTime3 = SecondsToDateTime.millisecToDateTime(sec3);
                                                Log.e("dateTime: ", bid_dateTime3);
                                                //myBiddingsObj3.setBid_datetime(bid_dateTime3);
                                                myBiddingsObj3.setBid_datetime(sec3);

                                                //bid time
                                                String[] timeSplit3 = bid_dateTime3.split(",");
                                                String bid_time3 = timeSplit3[1];
                                                Log.e("dateTime: ", bid_time3);

                                                myBiddingsObj3.setBid_time(bid_time3);

                                                myBiddingsList.remove(i);
                                                myBiddingsList.add(i, myBiddingsObj3);

                                                //myBiddingAdapter.notifyDataSetChanged();

                                                Log.e("ndvjnvjdf", hashMap_modified.get("is_edited").toString());

//                                                    myBiddingsList.remove(i);
//                                                    myBiddingsList.add(0, myBiddingsObj3);
//
//                                                    isCurrentUserWinner = true;
//
//                                                    Vibration.vibration(MyBiddingsActivity.this);
//
//                                                    Constants.playSuccesSoundEffect(MyBiddingsActivity.this, Constants.orderconfirm);

                                                break;

                                            }

                                        }


                                        String is_winner = hashMap_modified.get("is_winner").toString();
                                        if (is_winner.equals("true"))
                                        {

                                            isTimerFinish = true;

                                            cv_sold.setVisibility(View.VISIBLE);
                                           // img_speaker.setVisibility(View.VISIBLE);
                                            img_sold_out.setVisibility(View.VISIBLE);

                                            tv_seconds.setVisibility(View.GONE);
                                            progressBar2.setVisibility(View.GONE);
                                            more.setVisibility(View.GONE);

                                            if (hashMap_modified.get("user_ref").toString().equals(user_ref))
                                            {
                                                for (int i = 0; i < myBiddingsList.size(); i++)
                                                {
                                                    if (hashMap_modified.get("user_ref").toString().equals(myBiddingsList.get(i).getUser_ref()))
                                                    {
                                                        MyBiddings myBiddingsObj3 = new MyBiddings();
                                                        myBiddingsObj3.setUser_name(hashMap_modified.get("user_name").toString());
                                                        myBiddingsObj3.setUser_ref(hashMap_modified.get("user_ref").toString());
                                                        myBiddingsObj3.setBid_price(hashMap_modified.get("bid_price").toString());
                                                        myBiddingsObj3.setIs_bid_cancelled(hashMap_modified.get("is_bid_cancelled").toString());
                                                        myBiddingsObj3.setIs_winner(hashMap_modified.get("is_winner").toString());
                                                        myBiddingsObj3.setIs_edited(hashMap_modified.get("is_edited").toString());

                                                        myBiddingsObj3.setIs_direct_checkout(hashMap_modified.get("is_direct_checkout").toString());


                                                        String bid_datetime3 = hashMap_modified.get("bid_datetime").toString();

                                                        String sec3 = seconds(bid_datetime3);
                                                        String bid_dateTime3 = SecondsToDateTime.millisecToDateTime(sec3);
                                                        Log.e("dateTime: ", bid_dateTime3);
                                                        myBiddingsObj3.setBid_datetime(bid_dateTime3);

                                                        //bid time
                                                        String[] timeSplit3 = bid_dateTime3.split(",");
                                                        String bid_time3 = timeSplit3[1];
                                                        Log.e("dateTime: ", bid_time3);

                                                        myBiddingsObj3.setBid_time(bid_time3);

                                                        myBiddingsList.remove(i);
                                                        myBiddingsList.add(0, myBiddingsObj3);

                                                        isCurrentUserWinner = true;

                                                        Vibration.vibration(MyBiddingsActivity.this);

                                                        Constants.playSuccesSoundEffect(MyBiddingsActivity.this, Constants.orderconfirm);

                                                        break;
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                for (int i = 0; i < myBiddingsList.size(); i++)
                                                {
                                                    /*if (hashMap_modified.get("user_ref").toString().equals(user_ref))
                                                    {

                                                        MyBiddings myBiddingsObj3 = new MyBiddings();
                                                        myBiddingsObj3.setUser_name(firstWord(hashMap_modified.get("user_name").toString()));
                                                        myBiddingsObj3.setUser_ref(hashMap_modified.get("user_ref").toString());
                                                        myBiddingsObj3.setBid_price(hashMap_modified.get("bid_price").toString());
                                                        myBiddingsObj3.setIs_bid_cancelled(hashMap_modified.get("is_bid_cancelled").toString());
                                                        myBiddingsObj3.setIs_winner(hashMap_modified.get("is_winner").toString());
                                                        myBiddingsObj3.setIs_edited(hashMap_modified.get("is_edited").toString());

                                                        myBiddingsObj3.setIs_direct_checkout(hashMap_modified.get("is_direct_checkout").toString());


                                                        String bid_datetime3 = hashMap_modified.get("bid_datetime").toString();

                                                        String sec3 = seconds(bid_datetime3);
                                                        String bid_dateTime3 = SecondsToDateTime.millisecToDateTime(sec3);
                                                        Log.e("dateTime: ", bid_dateTime3);
                                                        myBiddingsObj3.setBid_datetime(bid_dateTime3);

                                                        //bid time
                                                        String[] timeSplit3 = bid_dateTime3.split(",");
                                                        String bid_time3 = timeSplit3[1];
                                                        Log.e("dateTime: ", bid_time3);

                                                        myBiddingsObj3.setBid_time(bid_time3);

                                                        myBiddingsList.remove(i);
                                                        myBiddingsList.add(0, myBiddingsObj3);

                                                        isCurrentUserWinner = true;

                                                        Vibration.vibration(MyBiddingsActivity.this);

                                                        Constants.playSuccesSoundEffect(MyBiddingsActivity.this, Constants.orderconfirm);

                                                        break;

                                                    }
                                                    else*/ if (hashMap_modified.get("user_ref").toString().equals(myBiddingsList.get(i).getUser_ref()))
                                                    {
                                                        MyBiddings myBiddingsObj3 = new MyBiddings();
                                                        myBiddingsObj3.setUser_name(hashMap_modified.get("user_name").toString());
                                                        myBiddingsObj3.setUser_ref(hashMap_modified.get("user_ref").toString());
                                                        myBiddingsObj3.setBid_price(hashMap_modified.get("bid_price").toString());
                                                        myBiddingsObj3.setIs_bid_cancelled(hashMap_modified.get("is_bid_cancelled").toString());
                                                        myBiddingsObj3.setIs_winner(hashMap_modified.get("is_winner").toString());
                                                        myBiddingsObj3.setIs_edited(hashMap_modified.get("is_edited").toString());
                                                        myBiddingsObj3.setIs_direct_checkout(hashMap_modified.get("is_direct_checkout").toString());

                                                        String bid_datetime3 = hashMap_modified.get("bid_datetime").toString();

                                                        String sec3 = seconds(bid_datetime3);
                                                        String bid_dateTime3 = SecondsToDateTime.millisecToDateTime(sec3);
                                                        Log.e("dateTime: ", bid_dateTime3);
                                                        myBiddingsObj3.setBid_datetime(bid_dateTime3);

                                                        //bid time
                                                        String[] timeSplit3 = bid_dateTime3.split(",");
                                                        String bid_time3 = timeSplit3[1];
                                                        Log.e("dateTime: ", bid_time3);

                                                        myBiddingsObj3.setBid_time(bid_time3);

                                                        myBiddingsList.remove(i);
                                                        myBiddingsList.add(0, myBiddingsObj3);

                                                        isCurrentUserWinner = false;
                                                        break;

                                                    }
                                                }
                                            }

                                            myBiddingAdapter.notifyDataSetChanged();

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (isCurrentUserWinner) {

//                                                        Constants.playSuccesSoundEffect(MyBiddingsActivity.this, Constants.orderconfirm);

                                                        sharedPreferenceUtil.setString("winning_status", "won");
                                                        sharedPreferenceUtil.save();
                                                        if (isProductExpired) {
                                                            DialogBoxError.showDialog(MyBiddingsActivity.this, "This product has been expired. You can bid again to win a product.");
                                                        } else {
                                                            DialogBoxError.showDialog(MyBiddingsActivity.this, "Product has been sold out for this time slot. You can bid again to win this product");
                                                        }

                                                    } else {
                                                        sharedPreferenceUtil.setString("winning_status", "lost");
                                                        sharedPreferenceUtil.save();
                                                        if (isProductExpired) {
                                                            DialogBoxError.showDialog(MyBiddingsActivity.this, "This product has been expired. You can bid again to win a product.");
                                                        } else {
                                                            DialogBoxError.showDialog(MyBiddingsActivity.this, "Product has been sold out for this time slot. You can bid again to win this product");
                                                        }
                                                    }
                                                }
                                            }, 5000);
                                        }
//                                        else
//                                        {
//                                            if (myBiddingsList.size()>0)
//                                            {
//                                                boolean isDirecCheckOut=false;
//
//                                                for (int i=0;i<myBiddingsList.size();i++)
//                                                {
//                                                    if (hashMap_modified.get("user_ref").toString().equals(myBiddingsList.get(i).getUser_ref()))
//                                                    {
//                                                        String bidPrc=hashMap_modified.get("bid_price").toString();
//
//                                                        if (!(bidPrc.equals(myBiddingsList.get(i).getBid_price())))
//                                                        {
//                                                            MyBiddings myBiddingsObj2 = new MyBiddings();
//                                                            myBiddingsObj2.setUser_name(firstWord(hashMap_modified.get("user_name").toString()));
//                                                            myBiddingsObj2.setUser_ref(hashMap_modified.get("user_ref").toString());
//                                                            myBiddingsObj2.setBid_price(hashMap_modified.get("bid_price").toString());
//                                                            myBiddingsObj2.setIs_bid_cancelled(hashMap_modified.get("is_bid_cancelled").toString());
//                                                            myBiddingsObj2.setIs_winner(hashMap_modified.get("is_winner").toString());
//                                                            myBiddingsObj2.setIs_edited(hashMap_modified.get("is_edited").toString());
//
//                                                            //
//                                                            //bid_datetime=Timestamp(seconds=1582622033, nanoseconds=629000000)
//                                                            //updated=Timestamp(seconds=1582637358, nanoseconds=99000000)
//                                                            String bid_datetime2=hashMap_modified.get("bid_datetime").toString();
//
//                                                            String sec2=seconds(bid_datetime2);
//                                                            String bid_dateTime2=SecondsToDateTime.millisecToDateTime(sec2);
//                                                            Log.e("dateTime: ", bid_dateTime2);
//                                                            myBiddingsObj2.setBid_datetime(bid_dateTime2);
//
//                                                            //bid time
//                                                            String[] timeSplit2=bid_dateTime2.split(",");
//                                                            String bid_time2=timeSplit2[1];
//                                                            Log.e("dateTime: ", bid_time2);
//
//                                                            myBiddingsObj2.setBid_time(bid_time2);
//
//                                                            myBiddingsList.remove(i);
//                                                            myBiddingsList.add(i,myBiddingsObj2);
//
//                                                            Comparator<MyBiddings> comparator2 = null;
//                                                            comparator2 = Comparator.comparing(ex -> ex.getBid_datetime());
//                                                            myBiddingsList.sort(comparator2.reversed());
//                                                            break;
//
//                                                        }
//
//                                                    }
//
//                                                }
//
//                                                if (!isDirecCheckOut)
//                                                {
//                                                    myBiddingAdapter.notifyDataSetChanged();
//
//                                                }
//
//                                            }
//
//                                        }

                                        break;

                                    case REMOVED:
                                        Log.e("REMOVED", "" + dc.getDocument().getData());

                                        boolean isLoginuser = false;

                                        HashMap hashMap_removed = (HashMap) dc.getDocument().getData();

                                        if (!product_status) {
                                            if (!isTimerFinish) {
                                                if (myBiddingsList.size() > 0) {
                                                    for (int i = 0; i < myBiddingsList.size(); i++) {
                                                        if (hashMap_removed.get("user_ref").toString().equals(user_ref)) {
                                                            if (hashMap_removed.get("is_direct_checkout").toString().equals("true")) {

                                                                Vibration.vibration(MyBiddingsActivity.this);

                                                                Constants.playSuccesSoundEffect(MyBiddingsActivity.this, Constants.click);

                                                                closeTimer();
                                                                // isDirecCheckOut=true;
                                                                sharedPreferenceUtil.setString("winning_status", "won");
                                                                sharedPreferenceUtil.save();
                                                                finish();
                                                                //DialogBoxError.showDialog(context, "Your bid has been accepted, Please proceed your bid in won tab under biddings.");
                                                            } else if (hashMap_removed.get("is_direct_checkout").toString().equals("false"))
                                                            //else if (hashMap_removed.get("is_bid_cancelled").toString().equals("true"))
                                                            {
                                                                closeTimer();
                                                                sharedPreferenceUtil.setString("winning_status", "lost");
                                                                sharedPreferenceUtil.save();
                                                                DialogBoxError.showDialog(MyBiddingsActivity.this, "Your bid has been cancelled.");
                                                                //isLoginuser=true;
                                                            }
                                                            break;
                                                        } else if (hashMap_removed.get("user_ref").toString().equals(myBiddingsList.get(i).getUser_ref())) {
                                                            isLoginuser = false;
                                                            myBiddingsList.remove(i);
                                                            break;
                                                        }

                                                    }

                                                    if (isLoginuser) {
                                                        closeTimer();
                                                        finish();
                                                    } else {
                                                        myBiddingAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                });
    }


    private void getUserBal() {
        userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
            @Override
            public void onSuccess(HashMap hashMap) {
                Log.e(TAG, "onSuccess:userDetails " + hashMap);

                String response_msg = hashMap.get("response_msg").toString();
                String message = hashMap.get("message").toString();

                if (response_msg.equals("success"))
                {
                    HashMap userData = (HashMap) hashMap.get("userData");

                    wallet_balance = DoubleDecimal.twoPoints(userData.get("wallet_balance").toString());
                    phone_no = userData.get("phone_no").toString();

                    user_ref = userData.get("user_ref").toString();

                    walletPoint.setText(DoubleDecimal.twoPointsComma(wallet_balance));
                    points.setText(DoubleDecimal.twoPointsComma(wallet_balance));

                    if (DialogBoxError.checkInternetConnection(MyBiddingsActivity.this)) {
                        realtimeUpdationsWallet();
                    } else {
                        DialogBoxError.showInternetDialog(MyBiddingsActivity.this);
                    }

//                DialogBox.showEditOfferDialog(context, offer_ref, Double.parseDouble(current_price),
//                        Double.parseDouble(direct_purchase_price), wallet_balance,bid_price,bidPrice);

                }
                /*else if (response_msg.equals(Constants.unauthorized))
                {
                    DialogBoxError.showDialogBlockUser(MyBiddingsActivity.this,message,sharedPreferenceUtil);
                }
                else if (response_msg.equals(Constants.under_maintenance))
                {
                    DialogBoxError.showError(MyBiddingsActivity.this,message);
                }
                else
                {
                    DialogBoxError.showError(MyBiddingsActivity.this,message);
                }*/


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "onFailure: " + e);
                DialogBoxError.showError(context, e.getMessage());
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

    private void realtimeUpdationsWallet() {

        db.collection("users")
                .whereEqualTo("phone_no", phone_no)
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

                                case MODIFIED:

                                    Log.e(TAG, "onEvent:Modified " + dc.getDocument().getData());

                                    HashMap hashMap = (HashMap) dc.getDocument().getData();

                                    walletPoint.setText(DoubleDecimal.twoPointsComma(hashMap.get("wallet_balance").toString()));
                                    points.setText(DoubleDecimal.twoPointsComma(hashMap.get("wallet_balance").toString()));

                                    break;

                            }
                        }

                    }
                });
    }

    private String seconds(String bid_datetime) {
        String rep1 = bid_datetime.replace("Timestamp(", "");
        String rep2 = rep1.replace(")", "");
        String[] split = rep2.split(",");
        String seconds = split[0];
        String sec = seconds.replace("seconds=", "");

        return sec;
    }

    private void observers() {
        db = null;
        db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereEqualTo("is_active", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "listen:error", e);
                            return;
                        }
                        if (isActive) {
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                switch (documentChange.getType()) {
                                    case MODIFIED:
                                        Log.e(TAG, "onEventObserver: " + documentChange.getDocument().getData());
                                        ///
                                        HashMap hashMap = (HashMap) documentChange.getDocument().getData();
                                        String product_status_new = hashMap.get("product_status").toString();
                                        String current_price_new = hashMap.get("current_price").toString();
                                        String product_ref_new = documentChange.getDocument().getId();
                                        Log.e(TAG, "product_ref_new: " + product_ref_new);
                                        if (product_ref_new.equals(product_ref)) {
                                            Log.e(TAG, "onEvent:Matched");
                                            if ((product_status_new.equals("sold"))) {
                                                product_status = true;
                                                closeTimer();

                                                cv_sold.setVisibility(View.VISIBLE);
                                                //img_speaker.setVisibility(View.VISIBLE);
                                                img_sold_out.setVisibility(View.VISIBLE);

                                                tv_seconds.setVisibility(View.GONE);
                                                progressBar2.setVisibility(View.GONE);
                                                //more.setVisibility(View.GONE);
                                                sharedPreferenceUtil.setString("winning_status", "lost");
                                                sharedPreferenceUtil.save();
                                                DialogBoxError.showDialog(context, "This Product has been sold out. You can bid again to win a product");
                                                //Toast.makeText(context, "This product has been sold out. You can bid again to win this product.", Toast.LENGTH_SHORT).show();
                                            } else if ((product_status_new.equals("expired"))) {
                                                isProductExpired = true;
                                                //closeTimer();

                                                //DialogBoxError.showDialog(context, "This product has been expired. You can bid again to win a product.");

                                            }
                                        }

                                        break;
                                }
                            }
                        }

                    }
                });
    }


    public String firstWord(String firstWord)
    {
        if(firstWord.contains(" ")){
            firstWord = firstWord.substring(0, firstWord.indexOf(" "));
        }
        return firstWord;
    }


    private Task<HashMap> editOffer(String offer_ref, String bid_price) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("offer_ref", offer_ref);
        data.put("bid_price", Double.parseDouble(bid_price));

        return mFunctions.getHttpsCallable("editOffer").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

}