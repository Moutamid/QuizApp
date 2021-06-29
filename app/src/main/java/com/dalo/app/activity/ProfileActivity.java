package com.dalo.app.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.dalo.app.helper.ApiConfig;
import com.dalo.app.helper.AudienceProgress;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import com.dalo.app.Constant;
import com.dalo.app.R;
import com.dalo.app.helper.AndroidMultiPartEntity;
import com.dalo.app.helper.AppController;
import com.dalo.app.helper.CircleImageView;
import com.dalo.app.helper.Session;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.dalo.app.helper.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends AppCompatActivity {

    public CircleImageView imgProfile;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public int reqReadPermission = 1;
    public int reqWritePermission = 2;
    public static int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static int SELECT_FILE = 110;
    Uri fileUri;
    public ProgressBar progressBar;
    private String filePath = null;
    File sourceFile;
    private BottomSheetDialog bottomSheetDialog;
    public FloatingActionButton fabProfile;
    public TextView tvEmailId, tvUpdate;
    public AudienceProgress progress;
    public LinearLayout edtNameLayout, edtMobileLayout;
    public EditText edtName, edtMobile;
    public RelativeLayout mainLayout, editprofile;
    Toolbar toolbar;
    ImageView tvLogout;
    ImageView adsimage3;

    String currentPhotoPath;
    public static int REQUEST_IMAGE_CAPTURE = 100;
    public static int REQUEST_CROP_IMAGE = 120;
    File output = null;

    Uri imageUri;


    long totalSize = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newprofile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainLayout = findViewById(R.id.mainLayout);
/*        edtNameLayout = findViewById(R.id.edtNameLayout);
        edtMobileLayout = findViewById(R.id.edtMobileLayout);*/

        edtName = findViewById(R.id.edtUserName);
        edtMobile = findViewById(R.id.edtMobile);
        tvEmailId = findViewById(R.id.tvEmailId);
        // tvUpdate = findViewById(R.id.tvUpdate);
        tvLogout = findViewById(R.id.tvLogout);
        editprofile = findViewById(R.id.editprofile);

        adsimage3 = findViewById(R.id.adsimage3);
        Glide.with(this).load("https://www.dalo.games/assets/ads/3.gif").into(adsimage3);
        adsimage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Getintent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.dalo.games/assets/ads/3.php"));
                startActivity(Getintent);
            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileEdit();
            }
        });
        fabProfile = findViewById(R.id.fabProfile);
        progressBar = findViewById(R.id.progressBar);
        progress = findViewById(R.id.progress);
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setDefaultImageResId(R.drawable.ic_account);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, ProfileActivity.this), imageLoader);
        fabProfile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                SelectProfileImage();
            }
        });

        edtName.setText(Session.getUserData(Session.NAME, ProfileActivity.this));
        edtMobile.setText(Session.getUserData(Session.MOBILE, ProfileActivity.this));
        tvEmailId.setText(Session.getUserData(Session.EMAIL, ProfileActivity.this));

        Utils.LoadNativeAd(ProfileActivity.this);
    }


    public void ProfileEdit() {

        bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetTheme);

        View sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_profileupdate, null);

        sheetView.findViewById(R.id.imgClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        final TextInputEditText edtName, edtEmail, edtPassword, edtRefer, editTextPhone, editTextCountryCode;
        final TextInputLayout inputName, inputEmail, inputPass;
        final LinearLayout mobilelyt;

        final EditText editText = sheetView.findViewById(R.id.edtEmail);
        edtName = sheetView.findViewById(R.id.edtName);
        editTextPhone = sheetView.findViewById(R.id.editTextPhone);
        edtEmail = sheetView.findViewById(R.id.edtEmail);
        inputEmail = sheetView.findViewById(R.id.inputEmail);
        mobilelyt = sheetView.findViewById(R.id.mobilelyt);
        editTextCountryCode = sheetView.findViewById(R.id.editTextCountryCode);

        if ((Session.getUserData(Session.TYPE, ProfileActivity.this)).equals("mobile"))
        {
            mobilelyt.setVisibility(View.GONE);
            inputEmail.setVisibility(View.VISIBLE);
        } else {
            if ((Session.getUserData(Session.EMAIL, ProfileActivity.this)).equals("null")) {
                inputEmail.setVisibility(View.VISIBLE);
            } else {
                inputEmail.setVisibility(View.GONE);
            }
            mobilelyt.setVisibility(View.VISIBLE);
        }


        sheetView.findViewById(R.id.emailsubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = edtName.getText().toString().trim();
                final String number = editTextPhone.getText().toString().trim();
                final String code = editTextCountryCode.getText().toString().trim();

                final String PhoneNumber = code + number;
                final String Email = edtEmail.getText().toString().trim();
                if ((Session.getUserData(Session.TYPE, ProfileActivity.this)).equals("mobile")) {

                    inputEmail.setVisibility(View.VISIBLE);
                    if (edtName.getText().toString().isEmpty())
                        edtName.setError(getString(R.string.empty_alert_msg));
                    else
                        UpdateProfileEmail(name, Email, Session.getUserData(Session.MOBILE, ProfileActivity.this));
                } else {

                    mobilelyt.setVisibility(View.VISIBLE);
                    if (edtName.getText().toString().isEmpty())
                        edtName.setError(getString(R.string.empty_alert_msg));
                    else
                        UpdateProfile(name, PhoneNumber, Session.getUserData(Session.EMAIL, ProfileActivity.this));

                }


            }
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();

    }


    public void dailogs() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ProfileActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.permission_dialog, null);
        dialog.setView(dialogView);
        TextView ok = (TextView) dialogView.findViewById(R.id.ok);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }


    public void UpdateProfileEmail(final String name, final String email, final String moible) {
        if (Utils.isNetworkAvailable(this)) {
            Map<String, String> params = new HashMap<>();

            params.put(Constant.updateProfile, "1");
            params.put(Constant.userId, Session.getUserData(Session.USER_ID, ProfileActivity.this));
            params.put(Constant.email, email);
            params.put(Constant.mobile, moible);
            params.put(Constant.name, name);
            ApiConfig.RequestToVolley(new ApiConfig.VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {

                    if (result) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");
                            if (!error) {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();

                                Session.setUserData(Session.NAME, name, ProfileActivity.this);
                                Session.setUserData(Session.EMAIL, email, ProfileActivity.this);
                                edtName.setText(name);
                                tvEmailId.setText(email);
                                //edtemail.setText(mobile);
                                MainActivity.tvName.setText(getString(R.string.hello) + name);
                                bottomSheetDialog.dismiss();
                            } else {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, params);

        } else {
            setUpadte();
        }
    }


    public void UpdateProfile(final String name, final String mobile, final String Email) {
        if (Utils.isNetworkAvailable(this)) {
            Map<String, String> params = new HashMap<>();

            params.put(Constant.updateProfile, "1");
            params.put(Constant.userId, Session.getUserData(Session.USER_ID, ProfileActivity.this));
            params.put(Constant.name, name);
            params.put(Constant.email, Session.getUserData(Session.EMAIL, ProfileActivity.this));
            params.put(Constant.mobile, mobile);
            ApiConfig.RequestToVolley(new ApiConfig.VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {

                    if (result) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");
                            if (!error) {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                                Session.setUserData(Session.MOBILE, mobile, ProfileActivity.this);
                                Session.setUserData(Session.NAME, name, ProfileActivity.this);
                                edtName.setText(name);
                                edtMobile.setText(mobile);
                                MainActivity.tvName.setText(getString(R.string.hello) + name);
                                bottomSheetDialog.dismiss();
                            } else {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, params);

        } else {
            setUpadte();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == reqWritePermission) {
            // for each permission check if the user granted/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        dailogs();
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission) && (Manifest.permission.CAMERA.equals(permission))) {
                        showRationale(permission, R.string.app_name);
                        // user did NOT check "never ask again"
                        // this is a good place to explain the user
                        // why you need the permission and ask if he wants
                        // to accept it (the rationale)
                    }
                }
            }
        }
    }

    private void showRationale(String permission, int app_name) {

    }

    public void SelectProfileImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqWritePermission);
            } else if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, reqWritePermission);
            } else {
                selectDialog();
            }
        } else {
            selectDialog();
        }
    }

    public void selectDialog() {
        final CharSequence[] items = {getString(R.string.from_library), getString(R.string.from_camera), getString(R.string.cancel)};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.from_library))) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals(getString(R.string.from_camera))) {
                    dispatchTakePictureIntent();
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TUKUTUKU_" + timeStamp + "_";
        File storageDir = ProfileActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(ProfileActivity.this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(ProfileActivity.this, ProfileActivity.this.getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputCompressQuality(90)
                        .setRequestedSize(300, 300)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setAspectRatio(1, 1)
                        .start(ProfileActivity.this);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputCompressQuality(90)
                        .setRequestedSize(300, 300)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setAspectRatio(1, 1)
                        .start(ProfileActivity.this);
            } else if (requestCode == REQUEST_CROP_IMAGE) {
                CropImage.activity(FileProvider.getUriForFile(ProfileActivity.this, ProfileActivity.this.getPackageName() + ".provider", output)).start(ProfileActivity.this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                fileUri = result.getUri();
                new UploadFileToServer().execute();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        String uploadFile() {
            String responseString;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constant.QUIZ_URL);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                //publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                filePath = fileUri.getPath();
                sourceFile = new File(filePath);
//                // Adding file data to http body
                entity.addPart(Constant.image, new FileBody(sourceFile));
                entity.addPart(Constant.accessKey, new StringBody(Constant.accessKeyValue));
                entity.addPart(Constant.userId, new StringBody(Session.getUserData(Session.USER_ID, ProfileActivity.this)));
                entity.addPart(Constant.upload_profile_image, new StringBody("1"));
                totalSize = entity.getContentLength();
                httppost.addHeader(Constant.AUTHORIZATION, "Bearer " + AppController.createJWT("quiz", "quiz Authentication"));
                httppost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            try {
                JSONObject jsonObject = new JSONObject(result);
                boolean error = jsonObject.getBoolean("error");
                if (!error) {

                    Session.setUserData(Session.PROFILE, jsonObject.getString("file_path"), ProfileActivity.this);
                    imgProfile.setImageUrl(jsonObject.getString("file_path"), imageLoader);
                    DrawerActivity.imgProfile.setImageUrl(jsonObject.getString("file_path"), imageLoader);


                    Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }
    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectProfileImage();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    public void setUpadte() {
        bottomSheetDialog.dismiss();
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProfileEdit();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    public void Logout(View view) {
        final android.app.AlertDialog.Builder dialog1 = new android.app.AlertDialog.Builder(ProfileActivity.this);
        LayoutInflater inflater = (LayoutInflater) ProfileActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_logout, null);
        dialog1.setView(dialogView);
        dialog1.setCancelable(true);

        final android.app.AlertDialog alertDialog = dialog1.create();
        TextView tvMessage = dialogView.findViewById(R.id.tv_message);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);

        TextView btnok = dialogView.findViewById(R.id.btn_ok);
        TextView btnNo = dialogView.findViewById(R.id.btnNo);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.clearUserSession(ProfileActivity.this);
                LoginManager.getInstance().logOut();
                LoginTabActivity.mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                Intent intentLogin = new Intent(ProfileActivity.this, LoginTabActivity.class);
                intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ProfileActivity.this.startActivity(intentLogin);
                ProfileActivity.this.finish();

            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void UserStatistics(View view) {
        Intent intent = new Intent(getApplicationContext(), UserStatistics.class);
        startActivity(intent);
    }

    public void LeaderBoard(View view) {
        Intent intent = new Intent(getApplicationContext(), LeaderboardTabActivity.class);
        startActivity(intent);
    }

    public void Bookmarks(View view) {
        Intent intent = new Intent(getApplicationContext(), BookmarkList.class);
        startActivity(intent);
    }

    public void InviteFriend(View view) {
        Intent intent = new Intent(getApplicationContext(), InviteFriendActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
