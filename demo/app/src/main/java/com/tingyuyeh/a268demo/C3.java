package com.tingyuyeh.a268demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tingyuyeh.a268demo.models.FirebaseHelper;
import com.tingyuyeh.a268demo.models.User;
// user profile

public class C3 extends AppCompatActivity {

    TextView textView_email;
    TextView textView_totalTime;
    TextView textView_description;
    ImageView profile_img;
    Button button_logout;
    private String DEBUG = "C3";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c3);


        textView_email = findViewById(R.id.textView_email);
        textView_description = findViewById(R.id.textView_description);
        textView_totalTime = findViewById(R.id.textView_totalTime);
        profile_img = findViewById(R.id.profile_image);
        button_logout = findViewById(R.id.button_logout);
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
            profile_img.setImageBitmap(FirebaseHelper.decodeImage(user._thumbnail));
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
}
