package com.okmart.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.okmart.app.BuildConfig;
import com.okmart.app.R;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.UtilCameraPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImgActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView profile_img;
    private ImageView img_takeImage;
    private RelativeLayout rl_takeImage;
    private TextView tv_skip;
    //image
    private String userChoosenTask;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    public final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    String READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    String WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private static final int REQUEST_CAMERA = 421;

    private FirebaseFunctions mFunctions;
    private FirebaseStorage storage;
    private String TAG = ProfileImgActivity.class.getSimpleName();
    String downloadURL = "";
    String uploadURL = "";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_img);
        getSupportActionBar().hide();

        mFunctions = FirebaseFunctions.getInstance();
        storage = FirebaseStorage.getInstance();

        String[] PERMISSIONS = {CAMERA_PERMISSION,READ_EXTERNAL_STORAGE_PERMISSION,
                WRITE_EXTERNAL_STORAGE_PERMISSION};
        if (!hasPermissions(ProfileImgActivity.this, PERMISSIONS))
        {
            ActivityCompat.requestPermissions((Activity)ProfileImgActivity.this,PERMISSIONS,REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }

        initXml();
        setOnClickListener();
    }

    private void initXml()
    {
        progressBar = findViewById(R.id.progressBar);
        profile_img=findViewById(R.id.profile_img);
        img_takeImage=findViewById(R.id.img_takeImage);
        tv_skip=findViewById(R.id.tv_skip);
    }

    private void setOnClickListener()
    {
        img_takeImage.setOnClickListener(this);
        tv_skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_takeImage:
                selectImage();
                break;

            case R.id.tv_skip:
                setShortcuts();
                /*Intent intent = new Intent(ProfileImgActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();*/
                break;
        }
    }

    private void selectImage()
    {
        final CharSequence[] items = {getResources().getString(R.string.take_photo),
                getResources().getString(R.string.choose_fromlib),getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                boolean result = UtilCameraPermission.checkPermission(ProfileImgActivity.this);
                if (items[item].equals(getResources().getString(R.string.take_photo)))
                {
                    userChoosenTask = getResources().getString(R.string.take_photo);
                    if (result)
                    {
                        Uri outputFileUri=null;
                        try {
                            outputFileUri = getCaptureImageOutputUri();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);// MediaStore.ACTION_IMAGE_CAPTURE

                        if (cameraIntent.resolveActivity(getPackageManager())!=null)
                        {
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                }
                else if (items[item].equals(getResources().getString(R.string.choose_fromlib)))
                {
                    userChoosenTask = getResources().getString(R.string.choose_fromlib);
                    if (result)
                    {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    }

                }
                else if (items[item].equals(getResources().getString(R.string.cancel)))
                {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //check for camera and storage access permissions
    @TargetApi(Build.VERSION_CODES.M)
    private void checkMultiplePermissions(int permissionCode, Context context) {

        String[] PERMISSIONS = {CAMERA_PERMISSION, READ_EXTERNAL_STORAGE_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION};
        if (!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, permissionCode);
        } else {
            // Open your camera here.
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            filePath = data.getData();

            try {
                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_img.setImageBitmap(bitmap2);
                uploadImage();

                progressBar.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Log.e(TAG, "onActivityResult:asdfghj3" + mCurrentPhotoPath);
                filePath = Uri.parse(mCurrentPhotoPath);
                Bitmap mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                profile_img.setImageBitmap(mImageBitmap);
                uploadImage();

                progressBar.setVisibility(View.VISIBLE);
                tv_skip.setVisibility(View.GONE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Uri getCaptureImageOutputUri() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp +"_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return FileProvider.getUriForFile(ProfileImgActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                image);
    }

    private void uploadImage() {

        if (filePath != null)
        {
            String name = UUID.randomUUID().toString();
            uploadURL = "images/profiles/" + name + ".png";

            StorageReference storageReference = storage.getReference();
            StorageReference ref = storageReference.child(uploadURL);
            Log.e("ref", name);
            Log.e("ref", String.valueOf(ref));

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {

                            storageReference.child(uploadURL).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri)
                                {

                                    Log.e(TAG, "uploadedimgUri:- "+uri.toString());

                                    downloadURL = uri.toString();

                                    updateProfileImage().addOnSuccessListener(new OnSuccessListener<HashMap>()
                                    {
                                        @Override
                                        public void onSuccess(HashMap hashMap) {

                                            if(progressBar.isShown())
                                            {
                                                progressBar.setVisibility(View.GONE);
                                            }

                                            Log.e(TAG, "onSuccess: " + hashMap);
                                            //DialogBoxError.showError(ProfileImgActivity.this, hashMap.get("message").toString());
                                            Toast.makeText(ProfileImgActivity.this, hashMap.get("message").toString(), Toast.LENGTH_SHORT).show();
                                            if (hashMap.get("success").toString().equalsIgnoreCase("true"))
                                            {
                                                setShortcuts();
                                                /*Intent intent = new Intent(ProfileImgActivity.this, DashboardActivity.class);
                                                startActivity(intent);
                                                finish();*/
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "onFailure: " + e.getMessage());
                                            DialogBoxError.showError(ProfileImgActivity.this, e.getMessage());
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressBar.setVisibility(View.GONE);
                                    Log.e(TAG, "onFailure: " + e.getMessage());
                                    DialogBoxError.showError(ProfileImgActivity.this, e.getMessage());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: " + e.getMessage());
                            DialogBoxError.showError(ProfileImgActivity.this, e.getMessage());

                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private Task<HashMap> updateProfileImage() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();

        data.put("image_url", downloadURL);

        return mFunctions.getHttpsCallable("updateProfileImage").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    private void setShortcuts()
    {
        final ShortcutManager shortcutManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
        {
            shortcutManager = getSystemService(ShortcutManager.class);

            //Define the intent, which in this instance is launching Activity//
            Intent dynamicIntent1 = new Intent(this,DashboardActivity.class);
            dynamicIntent1.setAction(Intent.ACTION_VIEW);

            //Create the ShortcutInfo object//

            ShortcutInfo dynamicShortcut1 = new ShortcutInfo.Builder(getApplicationContext(), "home")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_home))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_home))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_home_hover))
                    .setIntent(dynamicIntent1)
                    .build();

            //2

            Intent dynamicIntent2 = new Intent(this,DashboardActivity.class);
            dynamicIntent2.setAction(Intent.ACTION_VIEW);
            dynamicIntent2.putExtra("type","MyOffers");
            ShortcutInfo dynamicShortcut2 = new ShortcutInfo.Builder(getApplicationContext(), "biddings")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_biddings))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_biddings))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_offers_hover))
                    .setIntent(dynamicIntent2)
                    .build();
            //shortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut2, dynamicShortcut1));

            //3
            Intent dynamicIntent3 = new Intent(this,DashboardActivity.class);
            dynamicIntent3.setAction(Intent.ACTION_VIEW);
            dynamicIntent3.putExtra("type","notifications");
            ShortcutInfo dynamicShortcut3 = new ShortcutInfo.Builder(this, "notifications")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_notifications))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_notifications))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_notifications_hover))
                    .setIntent(dynamicIntent3)
                    .build();


            //4
            Intent dynamicIntent4 = new Intent(this,DashboardActivity.class);
            dynamicIntent4.setAction(Intent.ACTION_VIEW);
            dynamicIntent4.putExtra("type","wallet");
            ShortcutInfo dynamicShortcut4 = new ShortcutInfo.Builder(this, "wallet")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_wallet))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_wallet))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_wallet_hover))
                    .setIntent(dynamicIntent4)
                    .build();
            /*//5
            Intent dynamicIntent5 = new Intent(this,DashboardActivity.class);
            dynamicIntent5.setAction(Intent.ACTION_VIEW);
            ShortcutInfo dynamicShortcut5 = new ShortcutInfo.Builder(this, "settings")
                    //Define all the shortcut’s characteristics//

                    .setShortLabel(getResources().getString(R.string.shortcut_short_label_settings))
                    .setLongLabel(getResources().getString(R.string.shortcut_long_label_settings))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_settings_hover))
                    .setIntent(dynamicIntent5)
                    .build();*/
            shortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut1, dynamicShortcut2, dynamicShortcut3, dynamicShortcut4));


        }

        Intent intent = new Intent(ProfileImgActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}