package com.okmart.app.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.okmart.app.R;
import com.okmart.app.activities.ReloadActivity;
import com.okmart.app.activities.SplashActivity;
import com.okmart.app.base_fragments.OffersFragment;

import java.util.HashMap;


public class DialogBoxError {

    private static Dialog dialogE;


    private String TAG = DialogBoxError.class.getSimpleName();

    public static void showError(Context context, String msg) {

        if (dialogE != null && dialogE.isShowing())
            return;

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
                dialogE.dismiss();
            }
        });

        dialogE.show();
    }

    public static void showErrorBalance(Context context, String msg) {

        if (dialogE != null && dialogE.isShowing())
            return;

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
                dialogE.dismiss();

                Intent intent = new Intent(context, ReloadActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

        dialogE.show();
    }


    public static void showDailog(Context context, String msg) {

        if (dialogE != null && dialogE.isShowing())
            return;

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
                dialogE.dismiss();
                //((Activity)context).finish();
                OffersFragment.ll_successful.performClick();
            }
        });

        dialogE.show();

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity, View editText)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view==null)
        {
            view = new View(activity);
        }
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        editText.requestFocus();
    }


    public static boolean checkInternetConnection(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public static void showInternetDialog(Context context)
    {
        if (dialogE != null && dialogE.isShowing())
            return;

        dialogE = new Dialog(context);
        dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogE.setCancelable(false);
        dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogE.setContentView(R.layout.dialog_error);
        TextView message = dialogE.findViewById(R.id.message);
        message.setText("Internet not available, Cross check your internet connectivity and try again");
        TextView ok = dialogE.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.cancel();
            }
        });

        dialogE.show();

    }

    public static void showDialog(Context context, String msg)
    {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_error);

        TextView message = dialog.findViewById(R.id.message);
        message.setText(msg);

        TextView ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((Activity)context).finish();
            }
        });

        if (dialog.isShowing())
        {
            return;
        }

        dialog.show();
    }

    //for block user
    public static void showDialogBlockUser(Context context, String msg,
                                           SharedPreferenceUtil sharedPreferenceUtil)
    {
        if (dialogE != null && dialogE.isShowing())
        {
            return;
        }
        else
        {
            //Dialog dialog = new Dialog(context);
            dialogE = new Dialog(context);
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogE.setCancelable(false);
            dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogE.setContentView(R.layout.dialog_error);
            dialogE.show();

            TextView message = dialogE.findViewById(R.id.message);
            message.setText(msg);

            ProgressBar progressBar = dialogE.findViewById(R.id.progressBar);

            TextView ok = dialogE.findViewById(R.id.ok);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogE.dismiss();
                    //((Activity)context).finish();

                    if (DialogBoxError.checkInternetConnection(context))
                    {
                        ok.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);

                        DialogBox.logout(sharedPreferenceUtil.getString("user_token",""))
                                .addOnSuccessListener(new OnSuccessListener<HashMap>() {
                            @Override
                            public void onSuccess(HashMap hashMap)
                            {

                                Log.e("onSuccess: ", "" + hashMap);

                                sharedPreferenceUtil.clearAll();

                                FirebaseAuth.getInstance().signOut();
                                ((Activity)context).finishAffinity();

//                        FirebaseAuth.getInstance().signOut();
//
//                        ((Activity)context).finishAffinity();
                                Intent intent = new Intent(context, SplashActivity.class);
                                context.startActivity(intent);
                                ((Activity)context).finish();



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                ok.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.e("onFailure:", "" + e);
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

        }


        /*if (dialogE.isShowing())
        {
            return;
        }

        dialogE.show();*/
    }

}