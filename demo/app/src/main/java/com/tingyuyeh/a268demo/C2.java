package com.tingyuyeh.a268demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.Problem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class C2 extends AppCompatActivity {
    private static final String DEBUG_TAG = "C2_DEBUG";
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;
    private static boolean INITIAL_LAUNCH = true;
    private static String currentPhotoPath;
    private ImageView imageView;
    private Button reportButton;
    private EditText titleEditText, descriptionEditText;
    private String callingActivity = "";

    // GPS tracker class
    private GPS_Tracker gps;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c2);

        try {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        gps = new GPS_Tracker(this);

        reportButton = findViewById(R.id.reportButton);
        imageView = findViewById(R.id.imageView);
        titleEditText = findViewById(R.id.problemTitleEditText);
        descriptionEditText = findViewById(R.id.problemDescriptionEditText);

        // Get the transferred data from source activity.
        Intent intent = getIntent();
        callingActivity = intent.getStringExtra("message");

        // check if the calling activity is C0 or C3
        // activity C4 just needs to use the camera feature
        if(callingActivity.equals("C3")) {
            dispatchTakePictureIntent();
        }
        else {
            // set the on click listener for the report problem button
            reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportProblem();
                }
            });

            // set the action for if the user clicks the image again
            // allow them to take another picture
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent();
                }
            });

            if (INITIAL_LAUNCH) {
                // automatically start the camera intent when the user enters activity C2
                dispatchTakePictureIntent();
                INITIAL_LAUNCH = false;
            } else {
                setImage();
            }
        }
    }

    void reportProblem() {
        Log.d(DEBUG_TAG, "Starting report problem function");
        // get the problem title and description
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        if (0 == title.length()) {
            toastMessage("Please enter a title for the problem");
            return;
        } else if (0 == description.length()) {
            toastMessage("Please enter a description for the problem");
            return;
        }
        // set default coordinates to Santa Clara
        Double latitude = 37.3496;
        Double longitude = 121.9390;
        // check for GPS coordinates
        if (gps.canGetLocation()) {
            if(0 != gps.getLatitude() && 0 != gps.getLongitude()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            }
            toastMessage("Lat: " + latitude.toString() + "\nLong: " + longitude.toString());
        }
        else {
            gps.showSettingsAlert();
            return;
        }
        Double[] coordinates = {latitude, longitude};

        // call FirebaseHelper singleton and pass it the required information
        FirebaseHelper.getInstance().createProblem(photoURI, coordinates, title, description, C2.this);

        // reset the initial launch
        INITIAL_LAUNCH = true;

        this.finish();
    }

    // this function sets the image view of C2
    private void setImage() {

        // check if the calling activity is C3
        if(callingActivity.equals("C3")) {
            Log.d(DEBUG_TAG, "Calling Fire base Helper for user upload photo");
            // only need to call the FirebaseHelper function
            FirebaseHelper.getInstance().uploadUserPhoto(photoURI, this);
            // send results to C3 letting it know that picture has been taken
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            this.finish();
        }

        // otherwise let the function operate normally

        // resize the picture for the image view using the full image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);

        // need this to handle high definition

        int nh = (int) ( bitmap.getHeight() * (800.0 / bitmap.getWidth()) );
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 600, nh, true);

        imageView.setImageBitmap(scaledBitmap);
    }

    // create intent to take the picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent


        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(DEBUG_TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if(null != photoFile) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.tingyuyeh.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   // prefix
                ".jpg",   // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // function to display toast messages
    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_TAKE_PHOTO == requestCode && resultCode == RESULT_OK) {
            setImage();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        INITIAL_LAUNCH = true;
    }
}
