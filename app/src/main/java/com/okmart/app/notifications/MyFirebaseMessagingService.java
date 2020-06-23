package com.okmart.app.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.okmart.app.R;
import com.okmart.app.activities.DashboardActivity;
import com.okmart.app.activities.DeliverNowActivity;
import com.okmart.app.activities.ProductDetailsActivity;
import com.okmart.app.activities.ProductDetailsFlashActivity;
import com.okmart.app.base_fragments.OffersFragment;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.FragmentUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    String type, ref, notification_ref;
    PendingIntent pendingIntent;
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0)
        {

            Log.e(TAG, "Message data payload: " + remoteMessage.getData());

            ArrayMap arrayMap = (ArrayMap) remoteMessage.getData();
            type = arrayMap.get("type").toString();

            if (type.equalsIgnoreCase("test_notification")) { }
            else
            {
                ref = arrayMap.get("ref").toString();
                notification_ref = arrayMap.get("notification_ref").toString();

                if (/* Check if data needs to be processed by long running job */ true) {
                    // For long-running tasks (10 seconds or more) use WorkManager.
                    scheduleJob();
                } else {
                    // Handle message within 10 seconds
                    handleNow();
                }
            }
        }

        if (type.equalsIgnoreCase("test_notification")) { }
        else
        {
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null)
            {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification());

                Uri uri = null;

                if(remoteMessage.getNotification().getImageUrl() != null)
                {
                    uri = remoteMessage.getNotification().getImageUrl();
                }


                sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), uri, type, ref, notification_ref);
            }
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private int getRandomNumber(int min,int max) {
        return (new Random()).nextInt((max - min) + 1) + min;
    }

    /*
     *To get a Bitmap image from the URL received
     * */

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     * @param imageUrl
     */
    private void sendNotification(String messageTitle, String messageBody, Uri imageUrl, String n_type, String n_ref, String notification_ref) {
        Intent intent=null;
        if(n_type.equals("live_product"))
        {
            intent = new Intent(this, ProductDetailsActivity.class);
            intent.putExtra("product_ref", n_ref);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);*/
        }
        else if(n_type.equals("flash_product_live"))
        {
            intent = new Intent(this, ProductDetailsFlashActivity.class);
            intent.putExtra("product_ref", n_ref);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);*/
        }
        else if(n_type.equals("bid_winner"))
        {
            intent = new Intent(this, DeliverNowActivity.class);
            intent.putExtra("checkout_ref", n_ref);
            intent.putExtra("notification_ref",notification_ref);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);*/
        }
        else if(n_type.equals("order_confirmed") || n_type.equals("order_updated"))
        {
            intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("type","MyOffers_Successfull");
            intent.putExtra("status_ref", n_ref);
            intent.putExtra("notification_ref",notification_ref);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);*/
        }
        else if(n_type.equals("admin_msg"))
        {
            intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("notification_ref",notification_ref);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);*/
        }
        else if(n_type.equals("payment_confirmed") || n_type.equalsIgnoreCase("payment_failed"))
        {
            intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("type", "wallet");
            intent.putExtra("my_wallet_screen","recharge");
            intent.putExtra("status_ref", n_ref);
            intent.putExtra("notification_ref",notification_ref);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);*/
        }
        int notification_id=getRandomNumber(1,1000);
        pendingIntent = PendingIntent.getActivity(this, notification_id /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap tempBMP = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        Bitmap bitmap = null;
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        if(imageUrl != null)
        {
            bitmap=getBitmapfromUrl(imageUrl.toString());
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle(messageTitle)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setLargeIcon(bitmap)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                                    .bigLargeIcon(null))
//                            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                            .setContentIntent(pendingIntent);
        }
        else
        {
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle(messageTitle)
                            .setContentText(messageBody)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notification_id/* ID of notification */, notificationBuilder.build());
    }




    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}