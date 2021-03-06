package com.tingyuyeh.a268demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tingyuyeh.a268demo.models.Callback;
import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.NavigationHelper;
import com.tingyuyeh.a268demo.models.User;
// user profile

public class C3 extends AppCompatActivity {

    TextView textView_email;
    TextView textView_totalTime;
    TextView textView_description;
    ImageView profile_img;
    Button button_logout;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private String DEBUG = "C3";


    ImageView header_profile_image;
    TextView header_email_text;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c3);


        textView_email = findViewById(R.id.textView_email);
        textView_description = findViewById(R.id.textView_description);
        textView_totalTime = findViewById(R.id.textView_totalTime);
        profile_img = findViewById(R.id.profile_image);
        button_logout = findViewById(R.id.button_logout);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent pictureIntent = new Intent(getApplicationContext(), C2.class);
                pictureIntent.putExtra("message", "C3");
                startActivityForResult(pictureIntent, REQUEST_TAKE_PHOTO);

                FirebaseHelper.getInstance().setUserPhotoCb(new Callback(){
                    @Override
                    public void onComplete(boolean success) {
                        super.onComplete(success);
                        Log.d("C3_onActivityResult", "I hear ya");
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        getSupportActionBar().setDisplayShowTitleEnabled(false);
                        NavigationHelper.buildNavigation(drawer,
                                C3.this,
                                navigationView,
                                header_profile_image,
                                header_email_text,
                                getApplicationContext()
                        );
                        User user = FirebaseHelper.getInstance().getUser();

                        if (user._thumbnail != null && !user._thumbnail.equals("")) {
//                            profile_img.setImageBitmap(FirebaseHelper.decodeImage(user._thumbnail));
                            FirebaseHelper.MyAsyncTask task = new FirebaseHelper.MyAsyncTask(new FirebaseHelper.MyAsyncTask.TaskListener() {
                                @Override
                                public void onFinished(Bitmap result) {
                                    profile_img.setImageBitmap(result);
                                }
                            });
                            task.execute(FirebaseHelper.getInstance().getUser()._imageUri);
                        }
                    }
                });


            }
        });



        // navbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);

        header_profile_image = headerLayout.findViewById(R.id.header_profile_image);
        header_email_text = headerLayout.findViewById(R.id.header_email_text);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationHelper.buildNavigation(drawer,
                C3.this,
                navigationView,
                header_profile_image,
                header_email_text,
                getApplicationContext()
        );


    }

    @Override
    protected void onStart() {
        super.onStart();

        User user = FirebaseHelper.getInstance().getUser();
        textView_email.setText(FirebaseHelper.getInstance().getEmail());
        textView_description.setText(user._selfIntroduction);

        int totalTime = user._totalWorkMinutes;
        int hour = totalTime / 60;
        int min = totalTime % 60;
        StringBuilder sb = new StringBuilder();
        if (hour == 1) {
            sb.append(hour + " hour ");
        } else if (hour > 1) {
            sb.append(hour + " hours ");
        }
        sb.append(min);
        sb.append((min > 1) ? " minutes" : " minute");
        textView_totalTime.setText(sb.toString());
        if (user._thumbnail != null && !user._thumbnail.equals("")) {
//            profile_img.setImageBitmap(FirebaseHelper.decodeImage(user._thumbnail));
            FirebaseHelper.MyAsyncTask task = new FirebaseHelper.MyAsyncTask(new FirebaseHelper.MyAsyncTask.TaskListener() {
                @Override
                public void onFinished(Bitmap result) {
                    profile_img.setImageBitmap(result);
                }
            });
            task.execute(FirebaseHelper.getInstance().getUser()._imageUri);
        }

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper.getInstance().logout();
                Intent intent = new Intent(C3.this, A1.class);
                overridePendingTransition(R.anim.fade_in, R.anim.nothing);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (user._thumbnail != null && !user._thumbnail.equals("")) {
//            profile_img.setImageBitmap(FirebaseHelper.decodeImage(user._thumbnail));
//            FirebaseHelper.MyAsyncTask task = new FirebaseHelper.MyAsyncTask(new FirebaseHelper.MyAsyncTask.TaskListener() {
//                @Override
//                public void onFinished(Bitmap result) {
//                    profile_img.setImageBitmap(result);
//                }
//            });
//            task.execute(FirebaseHelper.getInstance().getUser()._imageUri);
//        }
//        header_profile_image.setImageBitmap(FirebaseHelper.decodeImage(FirebaseHelper.getInstance().getUser()._thumbnail));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("C3_onActivityResult", "In C3's onActivityResult");

        if(REQUEST_TAKE_PHOTO == requestCode && resultCode == RESULT_OK) {

            Log.d("C3_onActivityResult", "Inside (REQUEST_TAKE_PHOTO == requestCode && resultCode == RESULT_OK)");








        }
    }

}
