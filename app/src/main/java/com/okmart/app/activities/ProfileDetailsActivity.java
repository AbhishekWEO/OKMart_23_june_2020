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
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.okmart.app.BuildConfig;
import com.okmart.app.R;
import com.okmart.app.utilities.CapitalUtils;
import com.okmart.app.utilities.Constants;
import com.okmart.app.utilities.DialogBoxError;
import com.okmart.app.utilities.SharedPreferenceUtil;
import com.okmart.app.utilities.UtilCameraPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetailsActivity extends AppCompatActivity {

    private Context context = ProfileDetailsActivity.this;
    private FirebaseFunctions mFunctions;
    private String TAG = ProfileDetailsActivity.class.getSimpleName();
    private TextView tv_save, dob;
    private EditText user_name, phone_no, email;
    private String txt_user_name, txt_gender, txt_dob, txt_email, txt_profile_image;
    private Calendar mcalendar = Calendar.getInstance();
    private int day,month,year;
    private Spinner spinner;
    private List<String> list;
    private ImageView img_back;
    private CircleImageView profile_img;

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

    private FirebaseStorage storage;
    String downloadURL = "";
    String uploadURL = "";

    private ProgressBar progressBar, progressBarSave;
    private boolean isUpdateProfile=false;
    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        getSupportActionBar().hide();

        sharedPreferenceUtil = new SharedPreferenceUtil(ProfileDetailsActivity.this);
        mFunctions = FirebaseFunctions.getInstance();

        img_back = findViewById(R.id.img_back);
        user_name = findViewById(R.id.user_name);
        dob = findViewById(R.id.dob);
        email = findViewById(R.id.email);
        tv_save = findViewById(R.id.tv_save);

        progressBar = findViewById(R.id.progressBarMedium);
        progressBarSave = findViewById(R.id.progressBarSave);
        profile_img = findViewById(R.id.profile_img);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        user_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_NEXT)
                {
                    spinner.performClick();
                    DialogBoxError.hideKeyboard(ProfileDetailsActivity.this);
                    return true;
                }
                return false;
            }
        });

        storage = FirebaseStorage.getInstance();

        String[] PERMISSIONS = {CAMERA_PERMISSION,READ_EXTERNAL_STORAGE_PERMISSION,
                WRITE_EXTERNAL_STORAGE_PERMISSION};
        if (!hasPermissions(ProfileDetailsActivity.this, PERMISSIONS))
        {
            ActivityCompat.requestPermissions((Activity)ProfileDetailsActivity.this, PERMISSIONS,REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }

        spinner = (Spinner) findViewById(R.id.spinner);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        list = new ArrayList<String>();
        list.add("Male");
        list.add("Female");

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.spinner, list);
        adp.setDropDownViewResource(R.layout.spinner_item);
//        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                switch(arg2) {

                    case 0 :
                        txt_gender = "Male";
                        break;
                    case 1 :
                        txt_gender = "Female";
                        break;
                    default :
                        txt_gender = "Nothing";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });


        /*dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if(hasFocus){
                    //this if condition is true when edittext lost focus...
                    //check here for number is larger than 10 or not
                    date();
                }
            }
        });*/

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date();
            }
        });

        day=mcalendar.get(Calendar.DAY_OF_MONTH);
        year=mcalendar.get(Calendar.YEAR);
        month=mcalendar.get(Calendar.MONTH);

        if (DialogBoxError.checkInternetConnection(ProfileDetailsActivity.this))
        {
            userDetails().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                @Override
                public void onSuccess(HashMap hashMap) {
                    Log.e(TAG, "onSuccess: " + hashMap);

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
                        user_name.setSelection(user_name.getText().length());

                        spinner.setVisibility(View.VISIBLE);

                        if (userData.get("gender").toString().equalsIgnoreCase("male")) {
                            spinner.setSelection(0);
                        }
                        else if (userData.get("gender").toString().equalsIgnoreCase("female")) {
                            spinner.setSelection(1);
                        }
                        else
                        {
                            spinner.setSelection(0);
                        }

                        dob.setText(userData.get("dob").toString());
//                phone_no.setText(userData.get("phone_no").toString());
                        email.setText(userData.get("email").toString());

                        txt_profile_image = (String) userData.get("profile_image");

                        if (!(txt_profile_image.length() == 0)) {

                            try {

                                //Handle whatever you're going to do with the URL here
                                Glide.with(context).load(txt_profile_image).placeholder(R.color.white).into(profile_img);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        else
                        {
                            profile_img.setImageResource(R.drawable.avatar);
                        }
                    }
                    else if (response_msg.equals(Constants.unauthorized))
                    {
                        DialogBoxError.showDialogBlockUser(ProfileDetailsActivity.this,message,sharedPreferenceUtil);
                    }
                    else if (response_msg.equals(Constants.under_maintenance))
                    {
                        DialogBoxError.showError(ProfileDetailsActivity.this,message);
                    }
                    else
                    {
                        DialogBoxError.showError(ProfileDetailsActivity.this,message);
                    }


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
        else
        {
            DialogBoxError.showInternetDialog(ProfileDetailsActivity.this);
        }

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (DialogBoxError.checkInternetConnection(ProfileDetailsActivity.this))
                {
                    txt_user_name = user_name.getText().toString();

                    txt_dob = dob.getText().toString();
                    txt_email = email.getText().toString();

                    if (!validate()) {
                        return;
                    }

                    progressBarSave.setVisibility(View.VISIBLE);
                    tv_save.setText("");
                    tv_save.setClickable(false);
                    img_back.setEnabled(false);

                    isUpdateProfile=true;


                    updateProfile().addOnSuccessListener(new OnSuccessListener<HashMap>() {
                        @Override
                        public void onSuccess(HashMap hashMap) {
                            img_back.setEnabled(true);
                            isUpdateProfile=false;
                            Log.e(TAG, "onSuccess: " + hashMap);

                            tv_save.setText("SAVE");
                            tv_save.setClickable(true);

                            if(progressBarSave.isShown()) {

                                progressBarSave.setVisibility(View.GONE);

                            }

                            String response_msg = hashMap.get("response_msg").toString();
                            String message = hashMap.get("message").toString();

                            if (response_msg.equals("success"))
                            {
                                DialogBoxError.showError(context, "Profile updated successfully");
                            }
                            else if (response_msg.equals(Constants.unauthorized))
                            {
                                DialogBoxError.showDialogBlockUser(ProfileDetailsActivity.this,message,sharedPreferenceUtil);
                            }
                            else if (response_msg.equals(Constants.under_maintenance))
                            {
                                DialogBoxError.showError(ProfileDetailsActivity.this,message);
                            }
                            else
                            {
                                DialogBoxError.showError(ProfileDetailsActivity.this,message);
                            }



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isUpdateProfile=false;
                            img_back.setEnabled(true);
                            tv_save.setText("SAVE");
                            tv_save.setClickable(true);

                            if(progressBarSave.isShown()) {

                                progressBarSave.setVisibility(View.GONE);

                            }

                            Log.e(TAG, "onFailure: " + e);
                            DialogBoxError.showError(context, getString(R.string.something_went_wrong));
                        }
                    });
                }
                else
                {
                    DialogBoxError.showInternetDialog(ProfileDetailsActivity.this);
                }

            }
        });
    }


    private boolean validate() {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z.]+";

        if (!(txt_email.equals(""))) {

            if (!(txt_email.matches(emailPattern))) {
                DialogBoxError.showError(context, "Please enter correct email");

                return false;
            }

        }

        return true;
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
                boolean result = UtilCameraPermission.checkPermission(ProfileDetailsActivity.this);
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
        return FileProvider.getUriForFile(ProfileDetailsActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                image);
    }

    private void uploadImage() {

        if (DialogBoxError.checkInternetConnection(ProfileDetailsActivity.this))
        {
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

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "onFailure: " + e.getMessage());
                                                DialogBoxError.showError(ProfileDetailsActivity.this, e.getMessage());
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: " + e.getMessage());
                                        DialogBoxError.showError(ProfileDetailsActivity.this, e.getMessage());
                                    }
                                });


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onFailure: " + e.getMessage());
                                DialogBoxError.showError(ProfileDetailsActivity.this, e.getMessage());
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });

            }
        }
        else
        {
            DialogBoxError.showInternetDialog(ProfileDetailsActivity.this);
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

    private Task<HashMap> updateProfile() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("email", txt_email);
        data.put("dob", txt_dob);
        data.put("gender", txt_gender);
        data.put("user_name", txt_user_name);

        return mFunctions.getHttpsCallable("updateProfile").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap>() {
            @Override
            public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return (HashMap) task.getResult().getData();
            }
        });
    }

    public void date()
    {
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                dob.setText(String.format("%02d" , dayOfMonth) + "-" + String.format("%02d" , monthOfYear+1) + "-" + year);
                //dob.setSelection(dob.getText().length());
            }};//R.style.DatePickerDialogTheme
        DatePickerDialog dpDialog=new DatePickerDialog(context,R.style.DatePickerDialogTheme, listener, year, month, day);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dpDialog.show();

        dpDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dpDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.TRANSPARENT);
        dpDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dpDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        if (!isUpdateProfile)
        {
            super.onBackPressed();
        }

    }

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}