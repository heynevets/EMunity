package com.tingyuyeh.a268demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.widget.Button;

public class A1 extends AppCompatActivity {

    Button signUpButton;
    Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a1);

        signUpButton = findViewById(R.id.signUpButton);
        logInButton = findViewById(R.id.logInButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), B1.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.nothing);
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), B0.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.nothing);
            }
        });


    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.nothing, R.anim.fade_out);
    }
}
