package com.tingyuyeh.a268demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
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
    private static final String DEBUG_TAG = "C2";

    private static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView imageView;
    private static String currentPhotoPath;
    private Button reportButton;
    private EditText titleEditText, descriptionEditText;
    private static final int REQUEST_CODE_PERMISSION = 2;
    private static boolean INITIAL_LAUNCH = true;
    private String pictureAction = "";

    // GPS tracker class
    GPS_Tracker gps;


    Uri photoURI;
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
        pictureAction = intent.getStringExtra("message");
        descriptionEditText.setText(pictureAction);


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

        if(INITIAL_LAUNCH) {

            // automatically start the camera intent when the user enters activity C2
            dispatchTakePictureIntent();
            INITIAL_LAUNCH = false;

        }
        else {
            setImage();
        }
    }



    void reportProblem() {
        Log.d(DEBUG_TAG, "report");
        try {
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


            Location l = gps.getLocation();
            Double latitude = 0.0;
            Double longitude = 0.0;
            // check for GPS coordinates
            if (gps.canGetLocation()) {
                latitude = l.getLatitude();
                longitude = l.getLongitude();

                toastMessage("Lat: " + latitude.toString() + "\nLong: " + longitude.toString());
            } else {
                gps.showSettingsAlert();
                return;
            }
            Double[] coordinates = {latitude, longitude};

            // call FirebaseHelper singleton and pass it the required information
            FirebaseHelper.getInstance().createProblem(photoURI, coordinates, title, description, C2.this);
        }
        catch (Exception e) {
            Log.v("REPORT_PROBLEM", e.getMessage());
        }
        // reset the initial launch
        INITIAL_LAUNCH = true;

        this.finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_TAKE_PHOTO == requestCode && resultCode == RESULT_OK) {

            if(pictureAction != "") {
                setImage();
            }
            else {
                Intent intent = new Intent();
                intent.putExtra("message_return", currentPhotoPath);
                setResult(RESULT_OK, intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        INITIAL_LAUNCH = true;
    }

    private void setImage() {
        // resize the picture for the image view using the full image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);

        imageView.setImageBitmap(bitmap);
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
                Log.v(DEBUG_TAG, ex.getMessage());
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


}
