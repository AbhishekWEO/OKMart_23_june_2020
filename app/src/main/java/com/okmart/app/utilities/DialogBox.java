package com.okmart.app.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.okmart.app.R;
import com.okmart.app.activities.DashboardActivity;
import com.okmart.app.activities.NewAddressActivity;
import com.okmart.app.activities.SplashActivity;
import com.okmart.app.adapters.MyBiddingAdapter;
import com.okmart.app.model.MyBiddings;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogBox {

    private static Dialog dialogE;

    public static double bid_price_new = 0;



    private static String TAG = DialogBox.class.getSimpleName();
    private static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();


    public static void showEditCancelOfferDialog(Context context, String offer_ref, CountDownTimer timer,
                                                 List<MyBiddings>myBiddingsList,
                                                 /*Map<Integer, CountDownTimer> timerMap,
                                                 int position,*/ MyBiddingAdapter myBiddingAdapter
            ,String wallet_balance,String current_price,String direct_purchase_price,
                                                 TextView bid_price,String bidPrice) {

        dialogE = new Dialog(context);
        dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogE.setCancelable(false);
        dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogE.setContentView(R.layout.dialog_edit_cancel_offer);

        TextView edit_offer = dialogE.findViewById(R.id.edit_offer);
        edit_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogE.cancel();
//                DialogBox.showEditOfferDialog(context, offer_ref/*, timer, activeDataList, timerMap,
//                        position, activeOffersAdapter*/
//                        , Double.parseDouble(current_price),
//                        Double.parseDouble(direct_purchase_price),
//                        wallet_balance,bid_price,bidPrice);

            }
        });

        TextView cancel_offer = dialogE.findViewById(R.id.cancel_offer);
        cancel_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogE.cancel();
                DialogBox.showCancelOfferDialog(context, offer_ref, timer, myBiddingsList, /*timerMap,
                        position,*/ myBiddingAdapter);

            }
        });

        TextView go_back = dialogE.findViewById(R.id.go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.cancel();
            }
        });

        if (dialogE != null && dialogE.isShowing())
            return;
        dialogE.show();

    }

    public static void showEditOfferDialog(Context context, String offer_ref/*, CountDownTimer timer,
                                           List<ActiveOffersModel> activeDataList,
                                           Map<Integer, CountDownTimer> timerMap,
                                           int position, ActiveOffersAdapter activeOffersAdapter*/
            ,double current_product_price,double direct_purchase_price,String points,TextView tv_bid_price,
                                           String bidPrice_)
    {
        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(context);

        dialogE = new Dialog(context);
        dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogE.setCancelable(false);
        dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogE.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //dialogE.setContentView(R.layout.dialog_edit_offer);

        dialogE.setContentView(R.layout.edit_bid_layout);
        dialogE.getWindow().setGravity(Gravity.BOTTOM);

        Window window = dialogE.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


        TextView tv_placeBid = dialogE.findViewById(R.id.tv_placeBid);
        TextView tv_points = dialogE.findViewById(R.id.points);
        TextView tv_bidPrice = dialogE.findViewById(R.id.tv_bidPrice);
        SeekBar seekBar = dialogE.findViewById(R.id.seekBar);
        TextView tv_currentPrice = dialogE.findViewById(R.id.tv_currentPrice);
        TextView tv_directPrice = dialogE.findViewById(R.id.tv_directPrice);
        ProgressBar progressBar= dialogE.findViewById(R.id.progressBar);
        RelativeLayout rectangle = dialogE.findViewById(R.id.rectangle);

        tv_currentPrice.setText(DoubleDecimal.twoPointsComma(""+current_product_price)+" "+context.getResources().getString(R.string.pt));
        tv_directPrice.setText(DoubleDecimal.twoPointsComma(""+direct_purchase_price)+" "+context.getResources().getString(R.string.pt));
        tv_points.setText(DoubleDecimal.twoPointsComma(points));

        String bid_prc=bidPrice_;
        String bidPrc=bidPrice_;

        int step=1;
        seekBar.setMax((int) ((direct_purchase_price-current_product_price)/step));

        if(bid_price_new == 0) {
            tv_bidPrice.setText(DoubleDecimal.twoPointsComma(""+bidPrc)+" "+context.getResources().getString(R.string.pt));
            seekBar.setProgress((int) (Double.parseDouble(bidPrc) - current_product_price));
        }
        else {
            tv_bidPrice.setText(DoubleDecimal.twoPointsComma(""+bid_price_new)+" "+context.getResources().getString(R.string.pt));
            seekBar.setProgress((int) ((Double) (bid_price_new) - current_product_price));
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {


                bid_price_new = current_product_price + (progress * step);

                String values =String.valueOf(current_product_price + (progress * step));
                tv_bidPrice.setText(DoubleDecimal.twoPointsComma(values)+" "+context.getResources().getString(R.string.pt));
                Log.e("values",values);

                if (progressBar.getVisibility()==View.GONE)
                {
                    if (current_product_price + (progress * step)==direct_purchase_price)
                    {
                        tv_placeBid.setText("Buy Now");
                        tv_placeBid.setTextColor(context.getResources().getColor(R.color.white));
                        tv_placeBid.setBackgroundColor(context.getResources().getColor(R.color.orange));
                        //MyBiddingsActivity.isTimerFinish=false;
                        rectangle.setBackgroundResource(R.drawable.rounded_solid_orange);
                    }
                    else
                    {
                        //MyBiddingsActivity.isTimerFinish=true;
                        tv_placeBid.setText("Place new Bid");
                        tv_placeBid.setTextColor(context.getResources().getColor(R.color.white));
                        tv_placeBid.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                        rectangle.setBackgroundResource(R.drawable.rounded_solid_primary_max_radius);
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

                if(bid_price_new == 0) {

                    Toast.makeText(context, "Please change the bid price", Toast.LENGTH_SHORT).show();
                }
                else {

                    tv_placeBid.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    tv_placeBid.setFocusable(false);

                    editOffer(offer_ref, String.valueOf(bid_price_new)).addOnSuccessListener(new OnSuccessListener<HashMap>() {
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
                                tv_bid_price.setText("RM "+DoubleDecimal.twoPointsComma(String.valueOf(bid_price_new)));

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

            }
        });

        if (dialogE != null && dialogE.isShowing())
            return;
        dialogE.show();
    }

    public static void showCancelOfferDialog(Context context, String offer_ref, CountDownTimer timer,
                                             List<MyBiddings>myBiddingsList,
                                             /*Map<Integer, CountDownTimer> timerMap,
                                             int position,*/ MyBiddingAdapter myBiddingAdapter)
    {

        SharedPreferenceUtil sharedPreferenceUtil =new SharedPreferenceUtil(context);

        dialogE = new Dialog(context);
        dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogE.setCancelable(false);
        dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogE.setContentView(R.layout.dialog_cancel_offer);

        TextView cancel_offer = dialogE.findViewById(R.id.cancel_offer);
        ProgressBar progressBar = dialogE.findViewById(R.id.progressBar);
        TextView go_back = dialogE.findViewById(R.id.go_back);
        cancel_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                cancel_offer.setText("");
                go_back.setEnabled(false);

                cancelOffer(offer_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                    @Override
                    public void onSuccess(HashMap hashMap) {

                        progressBar.setVisibility(View.GONE);
                        go_back.setEnabled(true);

                        Log.e(TAG, "onSuccess: " + hashMap);


                        dialogE.cancel();

                        String response_msg = hashMap.get("response_msg").toString();
                        String message = hashMap.get("message").toString();
                        if (response_msg.equals("success"))
                        {

                            /*sharedPreferenceUtil.setString("winning_status","lost");
                            sharedPreferenceUtil.save();
                            ((Activity)context).finish();*/

                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                        progressBar.setVisibility(View.GONE);
                        go_back.setEnabled(true);
                        DialogBoxError.showError(context, e.getMessage());
                    }
                });

            }
        });

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.cancel();
            }
        });

        if (dialogE != null && dialogE.isShowing())
            return;
        dialogE.show();

    }

    public static void showDialog(Activity context, String msg) {
        dialogE = new Dialog(context);
        dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogE.setCancelable(false);
        dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogE.setContentView(R.layout.dialog_error);
        TextView message = dialogE.findViewById(R.id.message);
        message.setText(msg);
        TextView ok = dialogE.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.cancel();
                context.finish();
            }
        });
        if (dialogE != null && dialogE.isShowing())
            return;
        dialogE.show();
    }

    public static void showLogoutDialog(Context context, String token_new, SharedPreferenceUtil sharedPreferenceUtil) {

        dialogE = new Dialog(context);
        dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogE.setCancelable(false);
        dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogE.setContentView(R.layout.dialog_logout);

        ProgressBar progressBar = dialogE.findViewById(R.id.progressBar);

        TextView no = dialogE.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogE.cancel();

            }
        });

        TextView yes = dialogE.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (DialogBoxError.checkInternetConnection(context))
                {
                    yes.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    logout(token_new).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                        @Override
                        public void onSuccess(HashMap hashMap) {

                            sharedPreferenceUtil.clearAll();

                            FirebaseAuth.getInstance().signOut();
                            ((Activity)context).finishAffinity();

//                        FirebaseAuth.getInstance().signOut();
//
//                        ((Activity)context).finishAffinity();
                            Intent intent = new Intent(context, SplashActivity.class);
                            context.startActivity(intent);
                            ((Activity)context).finish();

                            Log.e(TAG, "onSuccess: " + hashMap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            yes.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "onFailure: " + e);
                        }
                    });
                }
                else
                {
                    dialogE.dismiss();
                    DialogBoxError.showInternetDialog(context);
                }

            }
        });

        if (dialogE != null && dialogE.isShowing())
            return;
        dialogE.show();

    }

    private static Task<HashMap> editOffer(String offer_ref, String bid_price) {
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

    private static Task<HashMap> cancelOffer(String offer_ref) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("offer_ref", offer_ref);

        return mFunctions.getHttpsCallable("cancelOffer").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    public static void showDeleteDialog(Context context, ProgressBar progressBarDlt, TextView tv_delete,
                                        TextView tv_save, ImageView img_back) {
        dialogE = new Dialog(context);
        dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogE.setCancelable(false);
        dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogE.setContentView(R.layout.dialog_dlt);
        TextView no = dialogE.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.cancel();
            }
        });
        TextView yes = dialogE.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.cancel();
                tv_save.setEnabled(false);
                img_back.setEnabled(false);
                tv_delete.setText("");
                progressBarDlt.setVisibility(View.VISIBLE);
                ((NewAddressActivity)context).dltAddress();
            }
        });
        if (dialogE != null && dialogE.isShowing())
            return;
        dialogE.show();
    }

    public static void successfullDialog(Activity context,String txt_txn_id)
    {
        Dialog dialog=new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.successfull);
        dialog.show();
        TextView tv_continue=dialog.findViewById(R.id.tv_continue);
        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                DashboardActivity.txn_id=txt_txn_id;
                Intent intent = new Intent(context, DashboardActivity.class);
                intent.putExtra("type","wallet");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                context.finish();
            }
        });
    }

    public static void failedDialog(Activity context)
    {
        Dialog dialog=new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.payment_failed);
        dialog.show();

        TextView tv_retry=dialog.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                context.finish();
            }
        });
    }

    private static String feedback="";

    public static void feedbackDialog(Activity context,String order_ref)
    {
        Dialog dialog=new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.show();

        ImageView feedback1 =dialog.findViewById(R.id.feedback1);
        ImageView feedback2 =dialog.findViewById(R.id.feedback2);
        ImageView feedback3 =dialog.findViewById(R.id.feedback3);
        ImageView feedback4 =dialog.findViewById(R.id.feedback4);
        ImageView feedback5 =dialog.findViewById(R.id.feedback5);
        TextView tv_submit = dialog.findViewById(R.id.tv_submit);
        ProgressBar progressBarSubmit = dialog.findViewById(R.id.progressBarSubmit);



        feedback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback="terrible";
                feedback1.setImageResource(R.drawable.feedback1);
                feedback2.setImageResource(R.drawable.feedbacks2);
                feedback3.setImageResource(R.drawable.feedbacks3);
                feedback4.setImageResource(R.drawable.feedbacks4);
                feedback5.setImageResource(R.drawable.feedbacks5);
            }
        });

        feedback2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback="bad";
                feedback1.setImageResource(R.drawable.feedbacks1);
                feedback2.setImageResource(R.drawable.feedback2);
                feedback3.setImageResource(R.drawable.feedbacks3);
                feedback4.setImageResource(R.drawable.feedbacks4);
                feedback5.setImageResource(R.drawable.feedbacks5);
            }
        });

        feedback3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback="satisfactory";
                feedback1.setImageResource(R.drawable.feedbacks1);
                feedback2.setImageResource(R.drawable.feedbacks2);
                feedback3.setImageResource(R.drawable.feedback3);
                feedback4.setImageResource(R.drawable.feedbacks4);
                feedback5.setImageResource(R.drawable.feedbacks5);
            }
        });

        feedback4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback="great";
                feedback1.setImageResource(R.drawable.feedbacks1);
                feedback2.setImageResource(R.drawable.feedbacks2);
                feedback3.setImageResource(R.drawable.feedbacks3);
                feedback4.setImageResource(R.drawable.feedback4);
                feedback5.setImageResource(R.drawable.feedbacks5);
            }
        });

        feedback5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback="wonderful";
                feedback1.setImageResource(R.drawable.feedbacks1);
                feedback2.setImageResource(R.drawable.feedbacks2);
                feedback3.setImageResource(R.drawable.feedbacks3);
                feedback4.setImageResource(R.drawable.feedbacks4);
                feedback5.setImageResource(R.drawable.feedback5);
            }
        });

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedback.isEmpty())
                {
                    DialogBoxError.showError(context, "Please select your feedback");
                    return;
                }
                tv_submit.setText("");
                tv_submit.setEnabled(false);
                progressBarSubmit.setVisibility(View.VISIBLE);

                if (DialogBoxError.checkInternetConnection(context))
                {
                    tv_submit.setClickable(false);
                    tv_submit.setFocusable(false);
                    tv_submit.setEnabled(false);

                    updateFeedback(feedback,order_ref).addOnSuccessListener(new OnSuccessListener<HashMap>() {
                        @Override
                        public void onSuccess(HashMap hashMap) {

                            Log.e(TAG, "onSuccess: " + hashMap);

                            feedback="";

                            String response_msg = hashMap.get("repsonse_msg").toString();
                            String message = hashMap.get("message").toString();

                            if(response_msg.equals("success"))
                            {
                                dialog.dismiss();

                                DialogBox.feedbackDialog2(context);

//                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                progressBarSubmit.setVisibility(View.GONE);
                                tv_submit.setText("SUBMIT");
                                tv_submit.setClickable(true);
                                tv_submit.setFocusable(true);
                                tv_submit.setEnabled(true);
                                DialogBoxError.showError(context, message);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.e(TAG, "onFailure: " + e);

                            progressBarSubmit.setVisibility(View.GONE);
                            tv_submit.setText("SUBMIT");
                            tv_submit.setClickable(true);
                            tv_submit.setFocusable(true);
                            tv_submit.setEnabled(true);
                            DialogBoxError.showError(context, e.getMessage());

                        }
                    });
                }
                else
                {
                    DialogBoxError.showInternetDialog(context);
                }

                //
            }
        });
    }


    public static void feedbackDialog2(Activity context)
    {
        Dialog dialog=new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_feedback2);
        dialog.show();

        TextView tv_submit = dialog.findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, DashboardActivity.class);
                intent.putExtra("type","MyOffers_Successfull");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                ((Activity)context).finish();


            }
        });
    }


    public static void congratulationsDialog(Activity context, String order_ref, String product_name, String image_thumbnail, String bid_price, String product_ref)
    {
        Dialog dialog=new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_congratulations);
        dialog.show();

        ImageView iv_image_thumbnail =dialog.findViewById(R.id.image_thumbnail);
        TextView tv_product_name =dialog.findViewById(R.id.product_name);
        TextView tv_bid_price =dialog.findViewById(R.id.bid_price);
        TextView share =dialog.findViewById(R.id.share);
        TextView close =dialog.findViewById(R.id.close);

        Glide.with(context).load(image_thumbnail).into(iv_image_thumbnail);
        tv_product_name.setText(product_name);
        tv_bid_price.setText(bid_price);

        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(context);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, I've just won this item at a great price from OK Express, Take a look here, you might stand a chance too! "+sharedPreferenceUtil.getString("share_url","") + "/product?ref=" + product_ref);
                context.startActivity(Intent.createChooser(intent, "Share"));
                
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }


    public static Task<HashMap> logout(String token_new) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("device_token", token_new);

        return mFunctions.getHttpsCallable("logout").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }


    private static Task<HashMap> updateFeedback(String feedback, String order_ref)
    {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("order_ref", order_ref);
        data.put("feedback", feedback);

        return mFunctions.getHttpsCallable("updateFeedback").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }




}