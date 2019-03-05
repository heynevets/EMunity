package com.tingyuyeh.a268demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//added by Sneha on 03/04
//added by Sneha

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

//     mDatabase= FirebaseDatabase.getInstance().getReference().child("GPS");

//        Intent i=new Intent(MainActivity.this,MapsActivity.class);
//        startActivity(i);

        Thread timer= new Thread()
        {
            public void run()
            {
                try
                {
                    //Display for 3 seconds
                    sleep(1500);
                }
                catch (InterruptedException e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                finally
                {
                    //Goes to Activity  StartingPoint.java(STARTINGPOINT)
                    Intent intent = new Intent(getApplicationContext(), A1.class);
//                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.nothing);

                }
            }
        };
        timer.start();


    }
    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        finish();

    }

}
